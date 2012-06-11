//
//  AroundMeViewController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 08.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "AroundMeViewController.h"
#import "FarmerDetailViewController.h"
#import "AroundMeCell.h"
#import "CoreDataStack.h"
#import "Product.h"
#import <CoreLocation/CoreLocation.h>

#define TITLE_AROUND_ME @"Around me"
#define IMAGE_AROUND_ME @"icon-aroundme"
#define SEGMENTED_TITLE_MAP @"Mapa"
#define SEGMENTED_TITLE_LIST @"Seznam"

@interface AroundMeViewController () <CLLocationManagerDelegate> {
    FarmerDetailViewController *_farmerDetailViewController;
    NSArray *_aroundMeData;
    NSMutableDictionary *farmerIndexPath;
    // core location
    CLLocationManager *_locationManager;
    CLLocation *_bestEffortAtLocation;
}

@property (nonatomic, retain) FarmerDetailViewController *farmerDetailViewController;
@property (nonatomic, retain) NSArray *aroundMeData;
// core location
@property (nonatomic, retain) CLLocationManager *locationManager;
@property (nonatomic, retain) CLLocation *bestEffortAtLocation;

@end

@implementation AroundMeViewController

@synthesize farmerDetailViewController = _farmerDetailViewController;
@synthesize aroundMeData = _aroundMeData;
@synthesize locationManager = _locationManager;
@synthesize bestEffortAtLocation = _bestEffortAtLocation;
@synthesize mapView = _mapView;
@synthesize tableView = _tableView;

#pragma mark - map view getter

- (MKMapView *)mapView
{
    if (!_mapView) _mapView = [[MKMapView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame];
    
    return _mapView;
}

#pragma mark - around me data getter

- (NSArray *)aroundMeData
{
    if (!_aroundMeData) {
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        
        request.entity = [NSEntityDescription entityForName:@"Farmer"
                                     inManagedObjectContext:[CoreDataStack sharedStack].managedObjectContext];
        
        NSError *error;
        self.aroundMeData = [[CoreDataStack sharedStack].managedObjectContext executeFetchRequest:request error:&error];
        
        if (self.aroundMeData == nil) {
            NSLog(@"an error occurred");
        }
        
        [request release];
    }
    return _aroundMeData;
}


- (void)dataWithDistance
{
    for (Farmer *farmer in self.aroundMeData) {
        
        CLLocationCoordinate2D location;
        location.latitude = [farmer.latitude doubleValue];
        location.longitude = [farmer.longtitude doubleValue];
        
        CLLocation *locB = [[CLLocation alloc] initWithLatitude:location.latitude 
                                                      longitude:location.longitude];
        
        farmer.distance = [self.bestEffortAtLocation distanceFromLocation:locB];
        
        [locB release];
    }
    
    NSSortDescriptor *sortDescriptor;
    sortDescriptor = [[[NSSortDescriptor alloc] initWithKey:@"distance" ascending:YES] autorelease];
    NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    self.aroundMeData = [self.aroundMeData sortedArrayUsingDescriptors:sortDescriptors];
    
    [self.tableView reloadData];
}

#pragma mark - init

- (id)init
{
    self = [super init];
    if (self) {
        // tab bar
        self.tabBarItem = [[[UITabBarItem alloc] initWithTitle:NSLocalizedString(TITLE_AROUND_ME, "") image:[UIImage imageNamed:IMAGE_AROUND_ME] tag:0] autorelease];
        
        // segmented control
        [self setUpSegmentedControl];
        
        // location
        self.locationManager = [[[CLLocationManager alloc] init] autorelease];
        self.locationManager.delegate = self;
        // This is the most important property to set for the manager. It ultimately determines how the manager will
        // attempt to acquire location and thus, the amount of power that will be consumed.
        self.locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters;
        // Once configured, the location manager must be "started".
        [self.locationManager startUpdatingLocation];
    }
    return self;
}

#pragma mark - filter

- (void)filterWithProducts:(NSArray *)products
{
    // prepare query
    int i = 0;
    NSString *predicateString = @"";
    for (Product *product in products) {
        predicateString = [predicateString stringByAppendingFormat:@"(ANY productFarmer.productId = %@)", product.productId];
        if (i < products.count - 1) {
            predicateString = [predicateString stringByAppendingFormat:@" OR "];
        }
        i++;
    }
    
    // fetch request
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    
    request.entity = [NSEntityDescription entityForName:@"Farmer"
                                 inManagedObjectContext:[CoreDataStack sharedStack].managedObjectContext];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:predicateString]; 
    [request setPredicate:predicate];
    
    NSError *error;
    self.aroundMeData = [[CoreDataStack sharedStack].managedObjectContext executeFetchRequest:request error:&error];
    
    if (self.aroundMeData == nil) {
        NSLog(@"an error occurred");
    }
    
    [request release];
    
    // reload data
    [self.tableView reloadData];
    
    [self setAnnotations];
}

#pragma mark - segmented control

- (void)setUpSegmentedControl
{
    UISegmentedControl *segmentedControl = [[[UISegmentedControl alloc] initWithItems:[NSArray arrayWithObjects:SEGMENTED_TITLE_LIST, SEGMENTED_TITLE_MAP, nil]] autorelease];
    segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
    
    [segmentedControl addTarget:self
                         action:@selector(indexDidChangeForSegmentedControl:)
               forControlEvents:UIControlEventValueChanged];
    
    self.navigationItem.titleView = segmentedControl;
    
    segmentedControl.selectedSegmentIndex = 0;
}

- (void)indexDidChangeForSegmentedControl:(UISegmentedControl *)aSegmentedControl
{
    if (self.mapView.isHidden) {
        self.tableView.hidden = YES;
        self.mapView.hidden = NO;
    } else {
        self.tableView.hidden = NO;
        self.mapView.hidden = YES;
    }
    
    //TODO DELETE self.navigationItem.titleView = aSegmentedControl;
}

#pragma mark - view

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // set views
    if (!_tableView && [self.view isKindOfClass:[UITableView class]]) {
        _tableView = (UITableView *)self.view;
    }
    
    self.view = [[[UIView alloc] initWithFrame:[UIScreen mainScreen].applicationFrame] autorelease];
    
    self.tableView.frame = self.view.bounds;
    [self.view addSubview:self.tableView];
    
    self.mapView.frame = self.view.bounds;
    [self.view addSubview:self.mapView];
    
    self.mapView.hidden = YES;
    self.mapView.delegate = self;

    // navigation bar
    [self setUpNavigationBar];
    
    // set annotations
    [self setAnnotations];
}

#pragma mark - navigation

- (void)setUpNavigationBar
{
    // title
    NSString *title = NSLocalizedString(TITLE_AROUND_ME, "");
    self.title = title;
    
    // navigation
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:0.204 green:0.553 blue:0.118 alpha:1.000];
    
    // navigation title
    UILabel *titleLabel = [[[UILabel alloc] initWithFrame:CGRectZero] autorelease];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont boldSystemFontOfSize:18];
    titleLabel.textAlignment = UITextAlignmentCenter;
    titleLabel.textColor = [UIColor whiteColor];
    
    titleLabel.text = title;
    [titleLabel sizeToFit];
    
    // refresh
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refreshTapped:)] autorelease];
}

#pragma mark - refresh

- (void)refreshTapped:(id)sender
{
    NSLog(@"refresh tapped");
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return self.aroundMeData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"AroundMeCell";
    
    //TODO AroundMeCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // TODO if cell
    
    AroundMeCell *cell = [[[AroundMeCell alloc] initWithStyle:UITableViewCellStyleDefault
                                reuseIdentifier:CellIdentifier] autorelease];
    
    Farmer *farmer = [self.aroundMeData objectAtIndex:indexPath.row];
    
    [cell setFarmer:farmer];
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 70;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Navigation logic may go here. Create and push another view controller.
    if (!self.farmerDetailViewController) {
        self.farmerDetailViewController = [[[FarmerDetailViewController alloc] initWithStyle:UITableViewStyleGrouped] autorelease];
    }
    
    self.farmerDetailViewController.farmer = [self.aroundMeData objectAtIndex:indexPath.row];
    
    [self.navigationController pushViewController:self.farmerDetailViewController animated:YES];
}

#pragma mark - map view

- (void)setMapCenter
{
    MKCoordinateRegion region;
    MKCoordinateSpan span;
    
    span.latitudeDelta = 1.0;
    span.longitudeDelta = 1.0;
    
    region.span = span;
    region.center = self.bestEffortAtLocation.coordinate;
    
    [self.mapView setRegion:region animated:TRUE];
    [self.mapView regionThatFits:region];
}

- (void)setAnnotations
{
    [self.mapView removeAnnotations:self.mapView.annotations];
    
    farmerIndexPath = [[NSMutableDictionary alloc] init];
    
	// Add the annotation to our map view
    int farmerIndex = 0;
    for (Farmer *farmer in self.aroundMeData) {
        [self.mapView addAnnotation:farmer];
        [farmerIndexPath setValue:[NSIndexPath indexPathForRow:farmerIndex inSection:0] forKey:[farmer.farmerId stringValue]];
        farmerIndex++;
    }
    
    [self.mapView setCenterCoordinate:self.bestEffortAtLocation.coordinate animated:YES];
    [self.mapView setShowsUserLocation:YES];
}

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id <MKAnnotation>)annotation
{	
	// if it's the user location, just return nil.
    if ([annotation isKindOfClass:[MKUserLocation class]]) {
        return nil;
    }
    
	// try to dequeue an existing pin view first
	static NSString* AnnotationIdentifier = @"AnnotationIdentifier";
	MKPinAnnotationView* pinView = [[[MKPinAnnotationView alloc]
									 initWithAnnotation:annotation reuseIdentifier:AnnotationIdentifier] autorelease];
	pinView.animatesDrop = YES;
	pinView.canShowCallout = YES;
    pinView.pinColor = MKPinAnnotationColorGreen;
    
    Farmer *farmer = (Farmer *)annotation;
	
	UIButton* rightButton = [UIButton buttonWithType:UIButtonTypeDetailDisclosure];
	[rightButton setTitle:[farmer.farmerId stringValue] forState:UIControlStateNormal];
	[rightButton addTarget:self
					action:@selector(showDetails:)
		  forControlEvents:UIControlEventTouchUpInside];
	pinView.rightCalloutAccessoryView = rightButton;
	
	return pinView;
}

- (void)mapView:(MKMapView *)mapView annotationView:(MKAnnotationView *)view calloutAccessoryControlTapped:(UIControl *)control
{
    NSLog(@"tapped");
}

- (IBAction)showDetails:(id)sender
{    
    // TODO smazat
    
    NSNumber *farmerId = [NSNumber numberWithInt:[((UIButton*)sender).currentTitle intValue]];
    
    if (!self.farmerDetailViewController) {
        self.farmerDetailViewController = [[[FarmerDetailViewController alloc] initWithStyle:UITableViewStyleGrouped] autorelease];
    }
    
    NSIndexPath *indexPath = [farmerIndexPath valueForKey:[farmerId stringValue]];
    self.farmerDetailViewController.farmer = [self.aroundMeData objectAtIndex:indexPath.row];
    
    [self.navigationController pushViewController:self.farmerDetailViewController animated:YES];
}

#pragma mark - location

/*
 * We want to get and store a location measurement that meets the desired accuracy. For this example, we are
 *      going to use horizontal accuracy as the deciding factor. In other cases, you may wish to use vertical
 *      accuracy, or both together.
 */
- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation {
    // store all of the measurements, just so we can see what kind of data we might receive
    //[locationMeasurements addObject:newLocation];
    
    // test the age of the location measurement to determine if the measurement is cached
    // in most cases you will not want to rely on cached measurements
    NSTimeInterval locationAge = -[newLocation.timestamp timeIntervalSinceNow];
    if (locationAge > 5.0) return;
    // test that the horizontal accuracy does not indicate an invalid measurement
    if (newLocation.horizontalAccuracy < 0) return;
    // test the measurement to see if it is more accurate than the previous measurement
    if (_bestEffortAtLocation == nil || _bestEffortAtLocation.horizontalAccuracy > newLocation.horizontalAccuracy) {
        // store the location as the "best effort"
        self.bestEffortAtLocation = newLocation;
        // test the measurement to see if it meets the desired accuracy
        //
        // IMPORTANT!!! kCLLocationAccuracyBest should not be used for comparison with location coordinate or altitidue 
        // accuracy because it is a negative value. Instead, compare against some predetermined "real" measure of 
        // acceptable accuracy, or depend on the timeout to stop updating. This sample depends on the timeout.
        //
        if (newLocation.horizontalAccuracy <= _locationManager.desiredAccuracy) {
            // we have a measurement that meets our requirements, so we can stop updating the location
            // 
            // IMPORTANT!!! Minimize power usage by stopping the location manager as soon as possible.
            //
            [self stopUpdatingLocation:NSLocalizedString(@"Acquired Location", @"Acquired Location")];
            // we can also cancel our previous performSelector:withObject:afterDelay: - it's no longer necessary
            [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(stopUpdatingLocation:) object:nil];
        }
    }
    // update the display with the new location data
    
    [self dataWithDistance];
    
    // set map center
    [self setMapCenter];
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    // The location "unknown" error simply means the manager is currently unable to get the location.
    // We can ignore this error for the scenario of getting a single location fix, because we already have a 
    // TODOtimeout that will stop the location manager to save power.
    if ([error code] != kCLErrorLocationUnknown) {
        [self stopUpdatingLocation:NSLocalizedString(@"Error", @"Error")];
    }
}

- (void)stopUpdatingLocation:(NSString *)state {
    //TODO[self.tableView reloadData];
    [self.locationManager stopUpdatingLocation];
    self.locationManager.delegate = nil;
}

#pragma mark - dealloc

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

- (void)dealloc
{
    [_farmerDetailViewController release];
    [_aroundMeData release];
    [farmerIndexPath release];
    
    [_locationManager release];
    [_bestEffortAtLocation release];
    
    [_tableView release];
    [_mapView release];
    
    [super dealloc];
}

@end
