//
//  AroundMeViewController.h
//  bioadresar
//
//  Created by Tomas Hanacek on 08.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MapKit/MapKit.h>

@interface AroundMeViewController : UITableViewController <MKMapViewDelegate> {
    IBOutlet MKMapView *_mapView;
    IBOutlet UITableView *_tableView;
}

@property (nonatomic, retain) IBOutlet MKMapView *mapView;

- (void)filterWithProducts:(NSArray *)products;

@end
