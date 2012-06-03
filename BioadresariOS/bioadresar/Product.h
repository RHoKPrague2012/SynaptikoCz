//
//  Product.h
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface Product : NSManagedObject

@property (nonatomic, retain) NSDate * lastProcessedTime;
@property (nonatomic, retain) NSString * name;
@property (nonatomic, retain) NSNumber * productId;
@property (nonatomic, retain) NSNumber * categoryId;
@property (nonatomic, retain) NSSet *farmerProduct;
@end

@interface Product (CoreDataGeneratedAccessors)

- (void)addFarmerProductObject:(NSManagedObject *)value;
- (void)removeFarmerProductObject:(NSManagedObject *)value;
- (void)addFarmerProduct:(NSSet *)values;
- (void)removeFarmerProduct:(NSSet *)values;

@end
