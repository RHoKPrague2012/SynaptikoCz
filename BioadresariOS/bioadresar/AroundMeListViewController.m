//
//  AroundMeListViewController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "AroundMeListViewController.h"
#import "AroundMeCell.h"
#import "FarmerDetailViewController.h"
#import "CoreDataStack.h"
#import "Farmer.h"
#import <CoreLocation/CoreLocation.h>

@interface AroundMeListViewController () {
    FarmerDetailViewController *_farmerDetailViewController;
    NSArray *_aroundMeData;
}

@property (nonatomic, retain) FarmerDetailViewController *farmerDetailViewController;
@property (nonatomic, retain) NSArray *aroundMeData;

@end

@implementation AroundMeListViewController

@synthesize farmerDetailViewController = _farmerDetailViewController;
@synthesize bestEffortAtLocation;
@synthesize aroundMeData = _aroundMeData;

#pragma mark - Fetched results controller

- (NSArray *)aroundMeData
{
    if (!_aroundMeData) {
        NSFetchRequest *request = [[NSFetchRequest alloc] init];
        
        request.entity = [NSEntityDescription entityForName: @"Farmer"
                                     inManagedObjectContext: [CoreDataStack sharedStack].managedObjectContext];
        
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
    NSLog(@"dataWithDistance");
    
    for (Farmer *farmer in self.aroundMeData) {
        
        CLLocationCoordinate2D location;
        location.latitude = [farmer.latitude doubleValue];
        location.longitude = [farmer.longtitude doubleValue];
        
        CLLocation *locB = [[CLLocation alloc] initWithLatitude:location.latitude 
                                                      longitude:location.longitude];
        
        farmer.distance = [bestEffortAtLocation distanceFromLocation:locB];
        
        [locB release];
    }
    
    NSSortDescriptor *sortDescriptor;
    sortDescriptor = [[[NSSortDescriptor alloc] initWithKey:@"distance" ascending:YES] autorelease];
    NSArray *sortDescriptors = [NSArray arrayWithObject:sortDescriptor];
    self.aroundMeData = [self.aroundMeData sortedArrayUsingDescriptors:sortDescriptors];
    
    [self.tableView reloadData];
}

#pragma mark - init

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
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

#pragma mark - view

- (void)viewDidLoad
{
    [super viewDidLoad];

    [self setUpNavigationBar];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.aroundMeData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"AroundMeCell";
    
    AroundMeCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    // TODO if cell
    
    cell = [[[AroundMeCell alloc] initWithStyle : UITableViewCellStyleDefault
                                   reuseIdentifier: CellIdentifier] autorelease];
    
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

#pragma mark - dealloc

- (void)dealloc
{
    [_farmerDetailViewController release];
    [_aroundMeData release];
    [bestEffortAtLocation release];
    
    [super dealloc];
}


@end
