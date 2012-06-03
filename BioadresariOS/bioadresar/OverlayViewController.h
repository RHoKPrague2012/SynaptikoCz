//
//  OverlayViewController.h
//  bioadresar
//
//  Created by Tomas Hanacek on 03.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <UIKit/UIKit.h>

@class FarmersListViewController;

@interface OverlayViewController : UIViewController {
    FarmersListViewController *fvController;
}

@property (nonatomic, retain) FarmersListViewController *fvController;

@end