//
//  Farmer.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "Farmer.h"
#import "Product.h"


@implementation Farmer

@dynamic city;
@dynamic desc;
@dynamic email;
@dynamic farmerId;
@dynamic lastProcessedTime;
@dynamic latitude;
@dynamic longtitude;
@dynamic name;
@dynamic phone;
@dynamic street;
@dynamic type;
@dynamic web;
@dynamic productFarmer;

@synthesize distance;

- (CLLocationCoordinate2D)coordinate
{
    CLLocationCoordinate2D location;
    location.latitude = [self.latitude doubleValue];
    location.longitude = [self.longtitude doubleValue];
    
    return location;
}

- (NSString*)title
{
    return self.name;
}

- (NSComparisonResult)compare:(Farmer *)otherObject {
    return [[NSNumber numberWithDouble:self.distance] compare:[NSNumber numberWithDouble:otherObject.distance]];
}

@end
