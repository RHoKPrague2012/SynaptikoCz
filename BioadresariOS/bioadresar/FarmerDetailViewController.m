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
#define SECTION_CONTACT 1
#define SECTION_DESCRIPTION 2

#define CONTACT_ADDRESS 0
#define CONTACT_PHONE 1
#define CONTACT_WEB 3
#define CONTACT_EMAIL 2

#define DEFAULT_TEXT1 @"N/A"

@interface FarmerDetailViewController () {
    NSString *production;
}

@end

@implementation FarmerDetailViewController

@synthesize farmer = _farmer;

#pragma mark - farmer

- (void)setFarmer:(Farmer *)farmer
{
    if (farmer != _farmer) {
        [_farmer release];
        _farmer = [farmer retain];
        
        // set navigation title
        [self setNavigationTitle:farmer.name];
        
        // set production
        production = @"";
        NSArray *products = [self.farmer.productFarmer allObjects];
        int i = 0;
        for (Product *product in products) {
            production = [production stringByAppendingString:product.name];
            if (i < products.count - 1) {
                production = [production stringByAppendingString:@", "];
            }
            i++;
        }
        
        if ([production isEqualToString:@""]) {
            production = DEFAULT_TEXT1;
        }
        
        [production retain];

        // reload data
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

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    // Return the number of sections.
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section) {
        case SECTION_CONTACT:
            return 4;
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
		
        cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleValue2
                                       reuseIdentifier:CellIdentifier] autorelease];
    }
    
    switch (indexPath.section) {
        case SECTION_CONTACT:
            switch (indexPath.row) {
                case CONTACT_ADDRESS:
                    cell.textLabel.text = @"Adresa";
                    NSString *address;
                    if ([self.farmer.street isEqualToString:@""] && [self.farmer.city isEqualToString:@""]) {
                        address = DEFAULT_TEXT1;
                    } else if ([self.farmer.street isEqualToString:@""]) {
                        address = self.farmer.city;
                    } else if ([self.farmer.city isEqualToString:@""]) {
                        address = self.farmer.street;
                    } else {
                        address = [NSString stringWithFormat:@"%@, %@", self.farmer.street, self.farmer.city];
                    }
                    cell.detailTextLabel.text = address;
                    break;
                case CONTACT_EMAIL:
                    cell.textLabel.text = @"E-mail";
                    NSString *email;
                    if ([self.farmer.email isEqualToString:@""]) {
                        email = DEFAULT_TEXT1;
                    } else {
                        email = self.farmer.email;
                    }
                    cell.detailTextLabel.text = email;
                    break;
                case CONTACT_PHONE:
                    cell.textLabel.text = @"Telefon";
                    NSString *phone;
                    if ([self.farmer.phone isEqualToString:@""]) {
                        phone = DEFAULT_TEXT1;
                    } else {
                        phone = self.farmer.phone;
                    }
                    cell.detailTextLabel.text = phone;
                    break;
                case CONTACT_WEB:
                    cell.textLabel.text = @"Web";
                    NSString *web;
                    if ([self.farmer.email isEqualToString:@""]) {
                        web = DEFAULT_TEXT1;
                    } else {
                        web = self.farmer.email;
                    }
                    cell.detailTextLabel.text = web;
                    break;
            }
            break;
        case SECTION_DESCRIPTION:
            cell.textLabel.text = @"Popis";
            cell.detailTextLabel.text = self.farmer.desc;
            cell.detailTextLabel.lineBreakMode = UILineBreakModeWordWrap;
            cell.detailTextLabel.numberOfLines = 0;
            break;
        case SECTION_PRODUCTION:
            cell.textLabel.text = @"Produkce";
            cell.detailTextLabel.text = production;
            cell.detailTextLabel.lineBreakMode = UILineBreakModeWordWrap;
            cell.detailTextLabel.numberOfLines = 0;
            break;
        default:
            break;
    }
    
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
    switch (section) {
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
        case SECTION_DESCRIPTION:
        {
            UIFont *cellFont = [UIFont systemFontOfSize:[UIFont systemFontSize]];
            CGSize constraintSize = CGSizeMake(207.0f, MAXFLOAT);
            CGSize labelSize = [self.farmer.desc sizeWithFont:cellFont constrainedToSize:constraintSize lineBreakMode:UILineBreakModeWordWrap];
            
            return labelSize.height + 100;
        }
        case SECTION_PRODUCTION:
        {
            UIFont *cellFont = [UIFont systemFontOfSize:[UIFont systemFontSize]];
            CGSize constraintSize = CGSizeMake(207.0f, MAXFLOAT);
            CGSize labelSize = [production sizeWithFont:cellFont constrainedToSize:constraintSize lineBreakMode:UILineBreakModeWordWrap];
            
            double height = labelSize.height + 40;
            
            return height > 44 ? height : 44;
        }
        default:
            return 44;
    }
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    switch (indexPath.section) {
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
    [production release];
    
    [super dealloc];
}

@end
