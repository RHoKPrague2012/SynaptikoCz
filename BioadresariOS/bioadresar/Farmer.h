//
//  Farmer.h
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import <MapKit/MapKit.h>

@class Product;

@interface Farmer : NSManagedObject <MKAnnotation>

@property (nonatomic, retain) NSString * city;
@property (nonatomic, retain) NSString * desc;
@property (nonatomic, retain) NSString * email;
@property (nonatomic, retain) NSNumber * farmerId;
@property (nonatomic, retain) NSDate * lastProcessedTime;
@property (nonatomic, retain) NSNumber * latitude;
@property (nonatomic, retain) NSNumber * longtitude;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSString * phone;
@property (nonatomic, retain) NSString * street;
@property (nonatomic, retain) NSString * type;
@property (nonatomic, retain) NSString * web;
@property (nonatomic, retain) NSSet *productFarmer;

@property (nonatomic, readonly) CLLocationCoordinate2D coordinate;
@property (nonatomic, readonly, copy) NSString *title;
@property (nonatomic) CLLocationDistance distance;

@end

@interface Farmer (CoreDataGeneratedAccessors)

- (void)addProductFarmerObject:(Product *)value;
- (void)removeProductFarmerObject:(Product *)value;
- (void)addProductFarmer:(NSSet *)values;
- (void)removeProductFarmer:(NSSet *)values;

@end
