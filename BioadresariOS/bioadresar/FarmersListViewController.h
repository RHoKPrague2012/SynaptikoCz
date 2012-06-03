//
//  FarmersListViewController.h
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <UIKit/UIKit.h>

@class OverlayViewController;

@interface FarmersListViewController : UIViewController <UITableViewDataSource, UITableViewDelegate> {
    IBOutlet UISearchBar *searchBar;
    IBOutlet UIToolbar *toolbar;
    NSMutableArray *listOfItems;
	NSMutableArray *copyListOfItems;
    BOOL searching;
	BOOL letUserSelectRow;
    
    OverlayViewController *ovController;
}

@property (nonatomic, retain) IBOutlet UISearchBar *searchBar;
@property (retain, nonatomic) IBOutlet UIToolbar *toolbar;
@property (retain, nonatomic) IBOutlet UITableView *tableView;

- (void) searchTableView;
- (void) doneSearching_Clicked:(id)sender;

@end
