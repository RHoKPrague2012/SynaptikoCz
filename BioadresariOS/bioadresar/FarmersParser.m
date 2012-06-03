//
//  FarmersParser.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "FarmersParser.h"
#import "CoreDataStack.h"
#import "Product.h"
#import "Farmer.h"

@interface FarmersParser () {
    NSMutableDictionary *dbProducts;
}

@end

@implementation FarmersParser

- (void)parse
{
    dbProducts = [[NSMutableDictionary alloc] init];
    
    [self loadProducts];
    [self fetchProductsFromDatabase];
    [self loadFarmers];
}

/*
 maso = 44
 mleko = 43
 ovoce = 15
 zelenina = 5
 */

#pragma mark - products

- (void)loadProducts
{
    [self createProductWithName:@"kravské mléko" 
                          andId:[NSNumber numberWithInt:1] 
                  andCategoryid:[NSNumber numberWithInt:43]];
    [self createProductWithName:@"mrkev" 
                          andId:[NSNumber numberWithInt:2] 
                  andCategoryid:[NSNumber numberWithInt:5]];
    
    [self createProductWithName:@"jehňata" 
                          andId:[NSNumber numberWithInt:3] 
                  andCategoryid:[NSNumber numberWithInt:44]];
    
    [self createProductWithName:@"vlna" 
                          andId:[NSNumber numberWithInt:4] 
                  andCategoryid:[NSNumber numberWithInt:0]];
    
    [self createProductWithName:@"zelenina" 
                          andId:[NSNumber numberWithInt:5] 
                  andCategoryid:[NSNumber numberWithInt:5]];
    
    [self createProductWithName:@"cibule" 
                          andId:[NSNumber numberWithInt:6] 
                  andCategoryid:[NSNumber numberWithInt:5]];
    
    [self createProductWithName:@"dýně Hokaido" 
                          andId:[NSNumber numberWithInt:7] 
                  andCategoryid:[NSNumber numberWithInt:5]];
    
    [self createProductWithName:@"pastinák" 
                          andId:[NSNumber numberWithInt:8] 
                  andCategoryid:[NSNumber numberWithInt:5]];
    
    [self createProductWithName:@"česnek" 
                          andId:[NSNumber numberWithInt:9] 
                  andCategoryid:[NSNumber numberWithInt:5]];
    
    [self createProductWithName:@"brambory" 
                          andId:[NSNumber numberWithInt:10] 
                  andCategoryid:[NSNumber numberWithInt:5]];
    
    [self createProductWithName:@"obiloviny" 
                          andId:[NSNumber numberWithInt:11] 
                  andCategoryid:[NSNumber numberWithInt:0]];
}

- (void)createProductWithName:(NSString *)name andId:(NSNumber *)productId andCategoryid:(NSNumber *)categoryId
{
    Product *product = (Product *)[NSEntityDescription insertNewObjectForEntityForName:@"Product" inManagedObjectContext:[CoreDataStack sharedStack].managedObjectContext];
    product.name = name;
    product.productId = productId;
    product.categoryId = categoryId;
}

- (void)fetchProductsFromDatabase
{
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    
    request.entity = [NSEntityDescription entityForName: @"Product"
                                 inManagedObjectContext: [CoreDataStack sharedStack].managedObjectContext];
    
    NSError *error;
    NSArray *fetchResults = [[CoreDataStack sharedStack].managedObjectContext executeFetchRequest:request error:&error];
    
    if (fetchResults == nil) {
        NSLog(@"an error occurred");
    }
    NSEnumerator *enumerator = [fetchResults objectEnumerator];
    id setObject;
    while ((setObject = [enumerator nextObject]) != nil) {
        Product *product = (Product *)setObject;
        [dbProducts setValue:product forKey:[NSString stringWithFormat:@"%@", product.productId]];
    }
    
    [request release];
}

#pragma mark - farmers

- (void)loadFarmers
{
    [self createFarmerWithName:@"SADY Svobodná Ves s.r.o." 
                   andFarmerId:[NSNumber numberWithInt:505] 
                        andLat:[NSNumber numberWithDouble:49.9969] 
                        andLon:[NSNumber numberWithDouble:15.42382]];
    [self createFarmerWithName:@"Čerstvé bedýnky" 
                   andFarmerId:[NSNumber numberWithInt:506] 
                        andLat:[NSNumber numberWithDouble:50.00301] 
                        andLon:[NSNumber numberWithDouble:14.2979]];
    [self createFarmerWithName:@"Čertovy brambory " 
                   andFarmerId:[NSNumber numberWithInt:507] 
                        andLat:[NSNumber numberWithDouble:50.07496] 
                        andLon:[NSNumber numberWithDouble:14.43648]];
    [self createFarmerWithName:@"Biodream" 
                   andFarmerId:[NSNumber numberWithInt:508] 
                        andLat:[NSNumber numberWithDouble:50.07524] 
                        andLon:[NSNumber numberWithDouble:14.45341]];
    [self createFarmerWithName:@"ŠŤOVÍK - TEPLICKÝ BIOKLUB" 
                   andFarmerId:[NSNumber numberWithInt:509] 
                        andLat:[NSNumber numberWithDouble:50.64484] 
                        andLon:[NSNumber numberWithDouble:13.85202]];
}

- (void)createFarmerWithName:(NSString *)name andFarmerId:(NSNumber *)farmerId andLat:(NSNumber *)lat andLon:(NSNumber *)lon
{
    Farmer *farmer = (Farmer *)[NSEntityDescription insertNewObjectForEntityForName:@"Farmer" inManagedObjectContext:[CoreDataStack sharedStack].managedObjectContext];
    farmer.farmerId = farmerId;
    farmer.name = name;
    farmer.latitude = lat;
    farmer.longtitude = lon;
    farmer.desc = @"Náš systém „Složte si svou Bio Bedýnku” jsme začali provozovat v únoru 2010. Funguje na principu objednávkového stažitelného formuláře z našich webových stránek, ve kterém si samy určíte množství nebízeného sortimentu (nejen ovoce a zelenina) z aktualizovaného ceníku. Na sezónu 2010 jsme navázali spolupráci z ekofarmáři z kraje Vysočina a budeme nabízet veškerou možnou tuzemskou produkci do našich Bio Bedýnek. Kromě toho využíváme i celoevropskou distribuční síť (ekofarma Deblín, Gastro Fresh). V našem systému si můžete objednávat i jednorázově. Objednávky vyřizujeme jednou týdně.";
    farmer.city = @"Praha";
    farmer.street = @"Korunní 90";
    farmer.email = @"biobedynky@biodream.cz";
    farmer.phone = @"732 408 982";
    farmer.web = @"http://www.biodream.cz";
    
    NSArray *keys = [dbProducts allKeys];
    
    for (NSString *key in keys) {
        Product *product = [dbProducts objectForKey:key];
        [product addFarmerProductObject:farmer];
    }
}
     
- (void)dealloc
{
    [dbProducts release];
    
    [super dealloc];
}

@end
