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
#import "OverlayViewController.h"

@interface FarmersListViewController () <UISearchBarDelegate> {
    NSFetchedResultsController *fetchedResultsController;
    NSArray *_sections;
    int rowsSelected;
}

@property (nonatomic, readonly) NSFetchedResultsController *fetchedResultsController;
@property (nonatomic, retain) NSArray *sections;

@end

@implementation FarmersListViewController

@synthesize sections = _sections;
@synthesize searchBar = searchBar;
@synthesize toolbar;
@synthesize tableView;

#pragma mark - section

- (NSArray *)sections
{
    if (!_sections) {
        NSArray *products = self.fetchedResultsController.fetchedObjects;
        NSMutableOrderedSet *tempSet = [[NSMutableOrderedSet alloc] init];
        
        for (Product *product in products) {
            // get first letter
            NSString *firstLetter = [product.name substringToIndex:1];
            
            // add letter to set
            [tempSet addObject:firstLetter];
        }
        
        self.sections = [tempSet array];
    }
    
    return _sections;
}

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

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    
    if (self) {
        // tab bar
        self.tabBarItem = [[[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"Search", "") image:[UIImage imageNamed:@"icon-search"] tag:0] autorelease];
        
        // set default values
        rowsSelected = 0;
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
    
    // search bar    
    //self.searchBar.delegate = self;

	self.tableView.tableHeaderView = self.searchBar;
	self.searchBar.autocorrectionType = UITextAutocorrectionTypeNo;
	
	searching = NO;
	letUserSelectRow = YES;
    
    // toolbar
    //TODOself.toolbar.frame = CGRectMake(self.toolbar.frame.origin.x, self.toolbar.frame.origin.y, 0, 0);
    
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
    id <NSFetchedResultsSectionInfo> sectionInfo = [self.fetchedResultsController.sections objectAtIndex: 0];
	
    return sectionInfo.numberOfObjects;
}

- (UITableViewCell *)tableView:(UITableView *)theTableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"ProductCell";
    UITableViewCell *cell = [theTableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (!cell) {
		
        cell = [[[UITableViewCell alloc] initWithStyle : UITableViewCellStyleDefault
                                     reuseIdentifier: CellIdentifier] autorelease];
    }
    
    NSIndexPath *repairIndexPath = [NSIndexPath indexPathForRow:indexPath.row inSection:0];
    Product *product = [self.fetchedResultsController objectAtIndexPath:repairIndexPath];
    
    cell.textLabel.text = product.name;
    
    return cell;
}


#pragma mark - Table view delegate

- (void)tableView:(UITableView *)theTableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [theTableView cellForRowAtIndexPath:indexPath];
    
    if (cell.accessoryType == UITableViewCellAccessoryNone) {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
        
        rowsSelected++;
    } else {
        cell.accessoryType = UITableViewCellAccessoryNone;
        rowsSelected--;
    }
    
    if (rowsSelected == 0) {
        self.toolbar.hidden = TRUE;
    } else {
        self.toolbar.hidden = FALSE;
    }
}

#pragma mark - devel

- (NSArray *)sectionIndexTitlesForTableView:(UITableView *)tableView
{
	return self.sections;
}

- (NSInteger)tableView:(UITableView *)tableView sectionForSectionIndexTitle:(NSString *)title atIndex:(NSInteger)index {
	
	return index % 2;
}

#pragma mark - Search Bar 

- (void) searchBarTextDidBeginEditing:(UISearchBar *)theSearchBar {
	
	//This method is called again when the user clicks back from the detail view.
	//So the overlay is displayed on the results, which is something we do not want to happen.
	if(searching)
		return;
	
	//Add the overlay view.
	if(ovController == nil)
		ovController = [[OverlayViewController alloc] initWithNibName:@"OverlayViewController" bundle:[NSBundle mainBundle]];
	
	CGFloat yaxis = self.navigationController.navigationBar.frame.size.height;
	CGFloat width = self.view.frame.size.width;
	CGFloat height = self.view.frame.size.height;
	
	//Parameters x = origion on x-axis, y = origon on y-axis.
	CGRect frame = CGRectMake(0, yaxis, width, height);
	ovController.view.frame = frame;	
	ovController.view.backgroundColor = [UIColor grayColor];
	ovController.view.alpha = 0.5;
	
	ovController.fvController = self;
	
	[self.tableView insertSubview:ovController.view aboveSubview:self.parentViewController.view];
	
	searching = YES;
	letUserSelectRow = NO;
	self.tableView.scrollEnabled = NO;
	
	//Add the done button.
	self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] 
											   initWithBarButtonSystemItem:UIBarButtonSystemItemDone 
											   target:self action:@selector(doneSearching_Clicked:)] autorelease];
	
}

- (void)searchBar:(UISearchBar *)theSearchBar textDidChange:(NSString *)searchText {
    
	//Remove all objects first.
	[copyListOfItems removeAllObjects];
	
	if([searchText length] > 0) {
		
		[ovController.view removeFromSuperview];
		searching = YES;
		letUserSelectRow = YES;
		self.tableView.scrollEnabled = YES;
		[self searchTableView];
	}
	else {
		
		[self.tableView insertSubview:ovController.view aboveSubview:self.parentViewController.view];
		
		searching = NO;
		letUserSelectRow = NO;
		self.tableView.scrollEnabled = NO;
	}
	
	[self.tableView reloadData];
}

- (void) searchBarSearchButtonClicked:(UISearchBar *)theSearchBar {
	
	[self searchTableView];
}

- (void) searchTableView {
	
	NSString *searchText = self.searchBar.text;
	NSMutableArray *searchArray = [[NSMutableArray alloc] init];
	
	for (NSDictionary *dictionary in listOfItems)
	{
		NSArray *array = [dictionary objectForKey:@"Countries"];
		[searchArray addObjectsFromArray:array];
	}
	
	for (NSString *sTemp in searchArray)
	{
		NSRange titleResultsRange = [sTemp rangeOfString:searchText options:NSCaseInsensitiveSearch];
		
		if (titleResultsRange.length > 0)
			[copyListOfItems addObject:sTemp];
	}
	
	[searchArray release];
	searchArray = nil;
}

- (void) doneSearching_Clicked:(id)sender {
	
	self.searchBar.text = @"";
	[self.searchBar resignFirstResponder];
	
	letUserSelectRow = YES;
	searching = NO;
	self.navigationItem.rightBarButtonItem = nil;
	self.tableView.scrollEnabled = YES;
	
	[ovController.view removeFromSuperview];
	[ovController release];
	ovController = nil;
	
	[self.tableView reloadData];
}


#pragma mark - Dealloc

- (void)dealloc
{
    [fetchedResultsController release];
    [searchBar release];

    [toolbar release];
    [tableView release];
    [super dealloc];
}

- (void)viewDidUnload {
    [self setToolbar:nil];
    [self setTableView:nil];
    [super viewDidUnload];
}
@end
