//
//  AroundMeViewController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "AroundMeMapViewController.h"
#import "FarmerDetailViewController.h"
#import "CoreDataStack.h"
#import "Farmer.h"

@interface AroundMeMapViewController () {
    FarmerDetailViewController *_farmerDetailViewController;
    NSFetchedResultsController *fetchedResultsController;
    NSMutableDictionary *farmerIndexPath;
}

@property (nonatomic, retain) FarmerDetailViewController *farmerDetailViewController;
@property (nonatomic, readonly) NSFetchedResultsController *fetchedResultsController;

@end

@implementation AroundMeMapViewController

@synthesize farmerDetailViewController = _farmerDetailViewController;
@synthesize bestEffortAtLocation;

#pragma mark - Fetched results controller

- (NSFetchRequest*)fetchRequest {
	
	NSFetchRequest *request = [[NSFetchRequest alloc] init];
	
	request.entity = [NSEntityDescription entityForName: @"Farmer"
								 inManagedObjectContext: [CoreDataStack sharedStack].managedObjectContext];
	
	NSSortDescriptor *descriptor = [[NSSortDescriptor alloc] initWithKey: @"name" ascending: YES];
	
	request.sortDescriptors = [NSArray arrayWithObject: descriptor];
	[descriptor release];
	
	return [request autorelease];
}

- (NSFetchedResultsController*)fetchedResultsController {
    
	if (!fetchedResultsController) {
		
		fetchedResultsController = [[NSFetchedResultsController alloc] 
                                    initWithFetchRequest: [self fetchRequest]
                                    managedObjectContext: [CoreDataStack sharedStack].managedObjectContext
                                    sectionNameKeyPath: nil
                                    cacheName: @"FarmerCache"];
	}
    
	return fetchedResultsController;
}

#pragma mark - init
@synthesize mapView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // tab bar
        self.tabBarItem = [[[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"Around me", "") image:[UIImage imageNamed:@"icon-aroundme"] tag:0] autorelease];
    }
    return self;
}

#pragma mark - navigation

- (void)setUpNavigationBar
{
    // title
    NSString *title = NSLocalizedString(@"Around me", "");
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
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refreshTapped:)];
}

#pragma mark - refresh

- (void)refreshTapped:(id)sender
{
    NSLog(@"refresh tapped");
}

#pragma mark - map view

- (void)setMapCenter
{
    MKCoordinateRegion region;
    MKCoordinateSpan span;
    
    span.latitudeDelta=1;
    span.longitudeDelta=1;
    
    region.span = span;
    region.center = self.bestEffortAtLocation.coordinate;
    
    [self.mapView setRegion:region animated:TRUE];
    [self.mapView regionThatFits:region];
}

- (void)setAnnotations
{
    farmerIndexPath = [[NSMutableDictionary alloc] init];
    
	// Add the annotation to our map view
    int farmerIndex = 0;
    for (Farmer *farmer in [self.fetchedResultsController fetchedObjects]) {
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

- (IBAction)showDetails:(id)sender
{    
    NSNumber *farmerId = [NSNumber numberWithInt:[((UIButton*)sender).currentTitle intValue]];
    
    if (!self.farmerDetailViewController) {
        self.farmerDetailViewController = [[[FarmerDetailViewController alloc] initWithStyle:UITableViewStyleGrouped] autorelease];
    }
    
    self.farmerDetailViewController.farmer = [self.fetchedResultsController objectAtIndexPath:[farmerIndexPath valueForKey:[farmerId stringValue]]];
    
    [self.navigationController pushViewController:self.farmerDetailViewController animated:YES];
}

#pragma mark - view

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.mapView.delegate = self;
    
    // navigation
    [self setUpNavigationBar];
    
    [self setMapCenter];
    
    // fetch data
    NSError *error;
    NSAssert1([self.fetchedResultsController performFetch: &error],
			  @"Unhandled error while fetching: %@", [error localizedDescription]);
    
    // set annotations
    
    [self setAnnotations];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

- (void)dealloc {
    [_farmerDetailViewController release];
    [mapView release];
    [fetchedResultsController release];
    [farmerIndexPath release];
    [bestEffortAtLocation release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setMapView:nil];
    [super viewDidUnload];
}
@end
