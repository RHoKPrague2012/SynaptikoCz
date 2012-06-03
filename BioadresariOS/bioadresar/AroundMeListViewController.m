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

@interface AroundMeListViewController () {
    FarmerDetailViewController *_farmerDetailViewController;
    NSFetchedResultsController *fetchedResultsController;
}

@property (nonatomic, retain) FarmerDetailViewController *farmerDetailViewController;
@property (nonatomic, readonly) NSFetchedResultsController *fetchedResultsController;

@end

@implementation AroundMeListViewController

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
    
    // fetch data
    NSError *error;
    NSAssert1([self.fetchedResultsController performFetch: &error],
			  @"Unhandled error while fetching: %@", [error localizedDescription]);
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
    // Return the number of rows in the section.
    id <NSFetchedResultsSectionInfo> sectionInfo = [self.fetchedResultsController.sections objectAtIndex: section];
	
    return sectionInfo.numberOfObjects;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"AroundMeCell";
    
    AroundMeCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (!cell) {
		
        cell = [[[AroundMeCell alloc] initWithStyle : UITableViewCellStyleDefault
                                   reuseIdentifier: CellIdentifier] autorelease];
    }
    
    Farmer *farmer = [self.fetchedResultsController objectAtIndexPath: indexPath];
    
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
    
    self.farmerDetailViewController.farmer = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    [self.navigationController pushViewController:self.farmerDetailViewController animated:YES];
}

#pragma mark - dealloc

- (void)dealloc
{
    [_farmerDetailViewController release];
    [fetchedResultsController release];
    [bestEffortAtLocation release];
    
    [super dealloc];
}


@end
