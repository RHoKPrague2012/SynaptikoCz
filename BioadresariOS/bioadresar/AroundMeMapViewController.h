//
//  AroundMeViewController.h
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>
#import <CoreLocation/CoreLocation.h>

@interface AroundMeMapViewController : UIViewController <MKMapViewDelegate> {
    CLLocation *bestEffortAtLocation;
}

@property (retain, nonatomic) IBOutlet MKMapView *mapView;
@property (nonatomic, retain) CLLocation *bestEffortAtLocation;

@end
