//
//  FarmerDetailViewController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "FarmerDetailViewController.h"
#import "Product.h"

#define SECTION_PRODUCTION 0
#define SECTION_ADDRESS 1
#define SECTION_CONTACT 2
#define SECTION_DESCRIPTION 3

#define ADDRESS_STREET 0
#define ADDRESS_CITY 1

#define CONTACT_PHONE 0
#define CONTACT_WEB 1
#define CONTACT_EMAIL 2

@interface FarmerDetailViewController ()

@end

@implementation FarmerDetailViewController

@synthesize farmer = _farmer;

#pragma mark - farmer

- (void)setFarmer:(Farmer *)farmer
{
    if (farmer != _farmer) {
        [_farmer release];
        _farmer = [farmer retain];
        
        [self setNavigationTitle:farmer.name];
        [self.tableView reloadData];
    }
}

#pragma mark - navigation

- (void)setNavigationTitle:(NSString *)title
{
    // title
    self.title = title;
    
    // navigation title
    UILabel *titleLabel = [[[UILabel alloc] initWithFrame:CGRectZero] autorelease];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont boldSystemFontOfSize:18];
    titleLabel.textAlignment = UITextAlignmentCenter;
    titleLabel.textColor = [UIColor whiteColor];
    
    self.navigationItem.titleView = titleLabel;
    titleLabel.text = title;
    [titleLabel sizeToFit];
}

- (void)setUpNavigationBarWithTitle:(NSString *)title
{
    [self setNavigationTitle:title];
    
    // right bar item
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(shareButtonTapped:)] autorelease];
}

#pragma mark - init

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
        //self.view.backgroundColor = [[UIColor alloc] initWithPatternImage:[UIImage imageNamed:@"Default"]];
;
    }
    return self;
}

#pragma mark - view

- (void)viewDidLoad
{
    [super viewDidLoad];

    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
 
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 4;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section) {
        case SECTION_ADDRESS:
            return 2;
        case SECTION_CONTACT:
            return 3;
        case SECTION_DESCRIPTION:
            return 1;
        case SECTION_PRODUCTION:
            return 1;
        default:
            return 0;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"DetailCell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (!cell) {
		
        cell = [[[UITableViewCell alloc] initWithStyle : UITableViewCellStyleValue2
                                     reuseIdentifier: CellIdentifier] autorelease];
    }
    
    switch (indexPath.section) {
        case SECTION_ADDRESS:
            switch (indexPath.row) {
                case ADDRESS_CITY:
                    cell.textLabel.text = @"Město";
                    cell.detailTextLabel.text = self.farmer.city;
                    break;
                case ADDRESS_STREET:
                    cell.textLabel.text = @"Ulice";
                    cell.detailTextLabel.text = self.farmer.street;
                    break;
            }
            break;
        case SECTION_CONTACT:
            switch (indexPath.row) {
                case CONTACT_EMAIL:
                    cell.textLabel.text = @"E-mail";
                    cell.detailTextLabel.text = self.farmer.email;
                    break;
                case CONTACT_PHONE:
                    cell.textLabel.text = @"Telefon";
                    cell.detailTextLabel.text = self.farmer.phone;
                    break;
                case CONTACT_WEB:
                    cell.textLabel.text = @"Web";
                    cell.detailTextLabel.text = self.farmer.web;
                    break;
            }
            break;
        case SECTION_DESCRIPTION:
            cell.textLabel.text = @"Popis";
            cell.detailTextLabel.text = self.farmer.desc;
            cell.detailTextLabel.numberOfLines = 14;
            break;
        case SECTION_PRODUCTION:
            cell.textLabel.text = @"Produkce";
            NSString *production = @"";
            NSArray *products = [self.farmer.productFarmer allObjects];
            int i = 0;
            for (Product *product in products) {
                production = [production stringByAppendingString:product.name];
                if (i < products.count) {
                    production = [production stringByAppendingString:@", "];
                }
                i++;
            }
            cell.detailTextLabel.text = production;
            cell.detailTextLabel.numberOfLines = 3;
            break;
        default:
            break;
    }
    
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
        case SECTION_ADDRESS:
            return @"Adresa";
        case SECTION_CONTACT:
            return @"Kontakt";
        case SECTION_DESCRIPTION:
            return @"";
        case SECTION_PRODUCTION:
            return @"Produkce";
        default:
            return @"";
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.section) {
        case SECTION_ADDRESS:
            return 44;
        case SECTION_CONTACT:
            return 44;
        case SECTION_DESCRIPTION:
            return 300;
        case SECTION_PRODUCTION:
            return 70;
        default:
            return 44;
    }
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.section) {
        case SECTION_ADDRESS:
            break;
        case SECTION_CONTACT:
            switch (indexPath.row) {
                case CONTACT_EMAIL:
                    [self displayComposerSheet];
                    break;
                case CONTACT_PHONE:
                    [self callWithNumber:self.farmer.phone];
                    break;
                case CONTACT_WEB:
                    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:self.farmer.web]];
                    break;
            }
            break;
        case SECTION_DESCRIPTION:
            break;
        case SECTION_PRODUCTION:
            break;
        default:
            break;
    }
}

- (void)callWithNumber:(NSString *)number
{
    UIDevice *device = [UIDevice currentDevice];
    if ([[device model] isEqualToString:@"iPhone"] ) {
        [[UIApplication sharedApplication] openURL:[NSURL URLWithString:[NSString stringWithFormat:@"tel:%@", number]]];
    } else {
        UIAlertView *Notpermitted=[[UIAlertView alloc] initWithTitle:@"Alert" message:@"Your device doesn't support this feature." delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [Notpermitted show];
        [Notpermitted release];
    }
}

#pragma mark - mail compose

// Dismisses the email composition interface when users tap Cancel or Send. Proceeds to update the message field with the result of the operation.
- (void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError*)error 
{	
	// Notifies users about errors associated with the interface
	switch (result)
	{
		case MFMailComposeResultCancelled:
			//message.text = @"Result: canceled";
			break;
		case MFMailComposeResultSaved:
			//message.text = @"Result: saved";
			break;
		case MFMailComposeResultSent:
			//message.text = @"Result: sent";
			break;
		case MFMailComposeResultFailed:
			//message.text = @"Result: failed";
			break;
		default:
			//message.text = @"Result: not sent";
			break;
	}
	[self dismissModalViewControllerAnimated:YES];
}

-(void)displayComposerSheet 
{
	MFMailComposeViewController *picker = [[MFMailComposeViewController alloc] init];
	picker.mailComposeDelegate = self;
	
	[picker setSubject:@"BioAdresar - Dotaz"];

    // Fill out the email body text
    
	NSString *emailBody = @"";
	[picker setMessageBody:emailBody isHTML:YES];
	
	[self presentModalViewController:picker animated:YES];
    [picker release];
}

#pragma mark - dealloc

- (void)dealloc
{
    self.farmer = nil;
    
    [_farmer release];
    
    [super dealloc];
}

@end
