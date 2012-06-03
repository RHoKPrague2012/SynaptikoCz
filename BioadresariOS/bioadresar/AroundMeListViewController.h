//
//  AroundMeListViewController.h
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <CoreLocation/CoreLocation.h>

@interface AroundMeListViewController : UITableViewController {
    CLLocation *bestEffortAtLocation;
}

@property (nonatomic, retain) CLLocation *bestEffortAtLocation;

@end
