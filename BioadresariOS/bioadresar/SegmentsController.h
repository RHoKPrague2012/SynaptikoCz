//
//  SegmentsController.h
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>

@interface SegmentsController : NSObject <CLLocationManagerDelegate> {
    NSArray *_viewControllers;
    UINavigationController *_navigationController;
    
    CLLocationManager *locationManager;
    CLLocation *bestEffortAtLocation;
}

@property (nonatomic, retain, readonly) NSArray *viewControllers;
@property (nonatomic, retain, readonly) UINavigationController * navigationController;

@property (nonatomic, retain) CLLocationManager *locationManager;
@property (nonatomic, retain) CLLocation *bestEffortAtLocation;

- (id)initWithNavigationController:(UINavigationController *)aNavigationController
                   viewControllers:(NSArray *)viewControllers;

- (void)indexDidChangeForSegmentedControl:(UISegmentedControl *)aSegmentedControl;

@end
