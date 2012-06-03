//
//  FarmerDetailViewController.h
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>
#import <MessageUI/MFMailComposeViewController.h>
#import "Farmer.h"

@interface FarmerDetailViewController : UITableViewController <MFMailComposeViewControllerDelegate> {
    Farmer *_farmer;
}

@property (nonatomic, retain) Farmer *farmer;

@end
