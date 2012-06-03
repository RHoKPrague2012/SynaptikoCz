//
//  OverlayViewController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 03.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "OverlayViewController.h"
#import "FarmersListViewController.h"

@interface OverlayViewController ()

@end

@implementation OverlayViewController

@synthesize fvController;

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
	
	[fvController doneSearching_Clicked:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}


- (void)dealloc {
	[fvController release];
    [super dealloc];
}

@end
