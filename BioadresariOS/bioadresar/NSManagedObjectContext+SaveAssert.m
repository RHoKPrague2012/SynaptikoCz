//
//  NSManagedObjectContext+SaveAssert.m
//  bioadresar
//
//  Created by Tomas Hanacek on 03.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "NSManagedObjectContext+SaveAssert.h"
#import "CoreDataStack.h"

@implementation NSManagedObjectContext (SaveAssert)

- (void) save
{
	NSError *error = nil;
	NSAssert2([[CoreDataStack sharedStack].managedObjectContext save: &error],
			  @"Unresolved error %@, %@", error, [error userInfo]);
}

@end
