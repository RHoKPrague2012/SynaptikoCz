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
#import "CSVParser.h"
#import "NSManagedObjectContext+SaveAssert.h"

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
    [self loadFarmerProduct];
    [[CoreDataStack sharedStack].managedObjectContext save];
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
    //SELECT * FROM produkt INTO OUTFILE '/tmp/products.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
    
    //select pr.id, pr.nazev, d.typ, d.poznamka, k.latitude, k.longtitude, k.mesto, k.ulice, k.mobil, k.web, k.email from producent pr inner join divize d on d.producent_id = pr.id inner join kontakt k on k.divize_id = d.id INTO OUTFILE '/tmp/farmers.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
    
    //select p.produkt_id, pr.id as farmer_id from produkuje p inner join divize d on p.divize_id = d.id inner join producent pr on pr.id = d.producent_id INTO OUTFILE '/tmp/farmerproduct.csv' FIELDS TERMINATED BY ',' ENCLOSED BY '"' LINES TERMINATED BY '\n';
    
    // parse csv
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"products" ofType:@"csv"];
    NSString *dataFile = [NSString stringWithContentsOfFile:filePath encoding:NSUTF8StringEncoding error:nil ];
    
    CSVParser *parser = [[[CSVParser alloc] initWithString:dataFile separator:@";" hasHeader:NO fieldNames:[NSArray arrayWithObjects: @"productId", @"name", @"categoryId", nil]] autorelease];
    
    NSArray *rows = [parser arrayOfParsedRows];
    
    for (NSDictionary *row in rows) {
        [self createProductWithName:[row valueForKey:@"name"]
                              andId:[NSNumber numberWithInt:
                                     [[row valueForKey:@"productId"] intValue]] 
                      andCategoryid:[NSNumber numberWithInt:
                                     [[row valueForKey:@"categoryId"] intValue]]];
    }
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
    // parse csv
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"farmers" ofType:@"csv"];
    NSString *dataFile = [NSString stringWithContentsOfFile:filePath encoding:NSUTF8StringEncoding error:nil ];
    
    CSVParser *parser = [[[CSVParser alloc] initWithString:dataFile separator:@";" hasHeader:NO fieldNames:[NSArray arrayWithObjects: @"farmerId", @"name", @"type", @"latitude", @"longtitude", @"city", @"street", @"phone", @"web", @"email", nil]] autorelease];
    
    NSArray *rows = [parser arrayOfParsedRows];
    
    for (NSDictionary *row in rows) {
        [self createFarmerWithName:[row valueForKey:@"name"]
                       andFarmerId:[NSNumber numberWithInt:
                                    [[row valueForKey:@"farmerId"] intValue]]
                            andLat:[NSNumber numberWithDouble:
                                    [[row valueForKey:@"latitude"] doubleValue]] 
                            andLon:[NSNumber numberWithDouble:
                                    [[row valueForKey:@"longtitude"] doubleValue]] 
                           andCity:[row valueForKey:@"city"] 
                         andStreet:[row valueForKey:@"street"] 
                          andEmail:[row valueForKey:@"email"] 
                          andPhone:[row valueForKey:@"phone"] 
                            andWeb:[row valueForKey:@"web"]];
    }
    
    
    /*
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
     */
}

- (void)loadFarmerProduct
{
    // parse csv
    NSString *filePath = [[NSBundle mainBundle] pathForResource:@"farmerproduct" ofType:@"csv"];
    NSString *dataFile = [NSString stringWithContentsOfFile:filePath encoding:NSUTF8StringEncoding error:nil ];
    
    CSVParser *parser = [[[CSVParser alloc] initWithString:dataFile separator:@";" hasHeader:NO fieldNames:[NSArray arrayWithObjects: @"productId", @"farmerId", nil]] autorelease];
    
    NSArray *rows = [parser arrayOfParsedRows];
    
    for (NSDictionary *row in rows) {
        
        Product *product = [dbProducts objectForKey:[row valueForKey:@"productId"]];
        Farmer *farmer = [self getFarmerFromId:[NSNumber numberWithInt:
                                                [[row valueForKey:@"farmerId"] intValue]]];
        
        if (farmer) {
            [product addFarmerProductObject:farmer];
        }
    }
}

- (Farmer *)getFarmerFromId:(NSNumber *)farmerId
{
    // check if flower exists
    Farmer *dbFarmer = nil;
    
    NSFetchRequest *request = [[NSFetchRequest alloc] init];
    request.entity = [NSEntityDescription entityForName:@"Farmer" inManagedObjectContext:[CoreDataStack sharedStack].managedObjectContext];
    request.predicate = [NSPredicate predicateWithFormat:@"farmerId = %@", farmerId];
    
    NSError *error = nil;
    dbFarmer = [[[CoreDataStack sharedStack].managedObjectContext executeFetchRequest:request error:&error] lastObject];
    
    [request release];
    
    return dbFarmer;
}

- (void)createFarmerWithName:(NSString *)name andFarmerId:(NSNumber *)farmerId andLat:(NSNumber *)lat andLon:(NSNumber *)lon andCity:(NSString *)city andStreet:(NSString *)street andEmail:(NSString *)email andPhone:(NSString *)phone andWeb:(NSString *)web
{
    Farmer *farmer = (Farmer *)[NSEntityDescription insertNewObjectForEntityForName:@"Farmer" inManagedObjectContext:[CoreDataStack sharedStack].managedObjectContext];
    farmer.farmerId = farmerId;
    farmer.name = name;
    farmer.latitude = lat;
    farmer.longtitude = lon;
    farmer.desc = @"Náš systém „Složte si svou Bio Bedýnku” jsme začali provozovat v únoru 2010. Funguje na principu objednávkového stažitelného formuláře z našich webových stránek, ve kterém si samy určíte množství nebízeného sortimentu (nejen ovoce a zelenina) z aktualizovaného ceníku. Na sezónu 2010 jsme navázali spolupráci z ekofarmáři z kraje Vysočina a budeme nabízet veškerou možnou tuzemskou produkci do našich Bio Bedýnek. Kromě toho využíváme i celoevropskou distribuční síť (ekofarma Deblín, Gastro Fresh). V našem systému si můžete objednávat i jednorázově. Objednávky vyřizujeme jednou týdně.";
    farmer.city = city;
    farmer.street = street;
    farmer.email = email;
    farmer.phone = phone;
    farmer.web = web;
}
     
- (void)dealloc
{
    [dbProducts release];
    
    [super dealloc];
}

@end
