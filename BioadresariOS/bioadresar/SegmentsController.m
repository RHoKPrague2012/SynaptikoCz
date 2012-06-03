//
//  SegmentsController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "SegmentsController.h"
#import "AroundMeMapViewController.h"
#import "AroundMeListViewController.h"

@interface SegmentsController ()

@property (nonatomic, retain, readwrite) NSArray * viewControllers;
@property (nonatomic, retain, readwrite) UINavigationController * navigationController;

@end

@implementation SegmentsController

@synthesize viewControllers = _viewControllers;
@synthesize navigationController = _navigationController;
@synthesize locationManager;
@synthesize bestEffortAtLocation;

#pragma mark - init

- (id)initWithNavigationController:(UINavigationController *)aNavigationController
                   viewControllers:(NSArray *)theViewControllers
{
    if (self = [super init]) {
        self.navigationController = aNavigationController;
        self.viewControllers = theViewControllers;
        
        
        // location
        self.locationManager = [[[CLLocationManager alloc] init] autorelease];
        locationManager.delegate = self;
        // This is the most important property to set for the manager. It ultimately determines how the manager will
        // attempt to acquire location and thus, the amount of power that will be consumed.
        locationManager.desiredAccuracy = kCLLocationAccuracyHundredMeters;
        // Once configured, the location manager must be "started".
        [locationManager startUpdatingLocation];
    }
    return self;
}

#pragma mark - segmented control

- (void)indexDidChangeForSegmentedControl:(UISegmentedControl *)aSegmentedControl
{
    NSUInteger index = aSegmentedControl.selectedSegmentIndex;
    UIViewController * incomingViewController = [self.viewControllers objectAtIndex:index];
    
    if ([incomingViewController isKindOfClass:[AroundMeListViewController class]]) {
        AroundMeListViewController *aroundMeListViewController = (AroundMeListViewController *)incomingViewController;
        aroundMeListViewController.bestEffortAtLocation = self.bestEffortAtLocation;
    } else if ([incomingViewController isKindOfClass:[AroundMeMapViewController class]]) {
        AroundMeMapViewController *aroundMeMapViewController = (AroundMeMapViewController *)incomingViewController;
        aroundMeMapViewController.bestEffortAtLocation = self.bestEffortAtLocation;
    }
    
    
    NSArray * theViewControllers = [NSArray arrayWithObject:incomingViewController];
    [self.navigationController setViewControllers:theViewControllers animated:NO];
    
    incomingViewController.navigationItem.titleView = aSegmentedControl;
}

#pragma mark - location

/*
 * We want to get and store a location measurement that meets the desired accuracy. For this example, we are
 *      going to use horizontal accuracy as the deciding factor. In other cases, you may wish to use vertical
 *      accuracy, or both together.
 */
- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation {
    // store all of the measurements, just so we can see what kind of data we might receive
    //[locationMeasurements addObject:newLocation];

    // test the age of the location measurement to determine if the measurement is cached
    // in most cases you will not want to rely on cached measurements
    NSTimeInterval locationAge = -[newLocation.timestamp timeIntervalSinceNow];
    if (locationAge > 5.0) return;
    // test that the horizontal accuracy does not indicate an invalid measurement
    if (newLocation.horizontalAccuracy < 0) return;
    // test the measurement to see if it is more accurate than the previous measurement
    if (bestEffortAtLocation == nil || bestEffortAtLocation.horizontalAccuracy > newLocation.horizontalAccuracy) {
        // store the location as the "best effort"
        self.bestEffortAtLocation = newLocation;
        // test the measurement to see if it meets the desired accuracy
        //
        // IMPORTANT!!! kCLLocationAccuracyBest should not be used for comparison with location coordinate or altitidue 
        // accuracy because it is a negative value. Instead, compare against some predetermined "real" measure of 
        // acceptable accuracy, or depend on the timeout to stop updating. This sample depends on the timeout.
        //
        if (newLocation.horizontalAccuracy <= locationManager.desiredAccuracy) {
            // we have a measurement that meets our requirements, so we can stop updating the location
            // 
            // IMPORTANT!!! Minimize power usage by stopping the location manager as soon as possible.
            //
            [self stopUpdatingLocation:NSLocalizedString(@"Acquired Location", @"Acquired Location")];
            // we can also cancel our previous performSelector:withObject:afterDelay: - it's no longer necessary
            [NSObject cancelPreviousPerformRequestsWithTarget:self selector:@selector(stopUpdatingLocation:) object:nil];
        }
    }
    // update the display with the new location data
    
    NSLog(@"%@", self.bestEffortAtLocation);
}

- (void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error {
    // The location "unknown" error simply means the manager is currently unable to get the location.
    // We can ignore this error for the scenario of getting a single location fix, because we already have a 
    // timeout that will stop the location manager to save power.
    if ([error code] != kCLErrorLocationUnknown) {
        [self stopUpdatingLocation:NSLocalizedString(@"Error", @"Error")];
    }
}

- (void)stopUpdatingLocation:(NSString *)state {
    //self.stateString = state;
    //[self.tableView reloadData];
    [locationManager stopUpdatingLocation];
    locationManager.delegate = nil;
    //[self.navigationItem setLeftBarButtonItem:resetItem animated:YES];;
}

#pragma mark - dealloc

- (void)dealloc {
    self.viewControllers = nil;
    self.navigationController = nil;
    
    [_viewControllers release];
    [_navigationController release];
    
    [locationManager release];
    [bestEffortAtLocation release];
    
    [super dealloc];
}

@end
