//
//  FarmersListViewController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "FarmersListViewController.h"
#import "CoreDataStack.h"
#import "Product.h"

@interface FarmersListViewController () {
    NSFetchedResultsController *fetchedResultsController;
}

@property (nonatomic, readonly) NSFetchedResultsController *fetchedResultsController;

@end

@implementation FarmersListViewController

#pragma mark - Fetched results controller

- (NSFetchRequest*)fetchRequest {
	
	NSFetchRequest *request = [[NSFetchRequest alloc] init];
	
	request.entity = [NSEntityDescription entityForName: @"Product"
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
                                    cacheName: @"ProductCache"];
	}
    
	return fetchedResultsController;
}

#pragma mark - init

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // tab bar
        self.tabBarItem = [[[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"Search", "") image:[UIImage imageNamed:@"icon-search"] tag:0] autorelease];
    }
    return self;
}

#pragma mark - navigation

- (void)setUpNavigationBar
{
    // title
    NSString *title = NSLocalizedString(@"Search", "");
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
    
    self.navigationItem.titleView = titleLabel;
    
    // search
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemSearch target:self action:@selector(searchTapped:)];
}

#pragma mark - search

- (void)searchTapped:(id)sender
{
    NSLog(@"search tapped");
}

#pragma mark - view

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // navigation
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
    static NSString *CellIdentifier = @"ProductCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (!cell) {
		
        cell = [[[UITableViewCell alloc] initWithStyle : UITableViewCellStyleDefault
                                     reuseIdentifier: CellIdentifier] autorelease];
    }
    
    Product *product = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    cell.textLabel.text = product.name;
    
    return cell;
}


#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    
    if (cell.accessoryType == UITableViewCellAccessoryNone) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
}

#pragma mark - Dealloc

- (void)dealloc
{
    [fetchedResultsController release];

    [super dealloc];
}

@end
