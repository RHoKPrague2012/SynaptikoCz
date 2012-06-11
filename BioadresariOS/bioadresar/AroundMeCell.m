//
//  AroundMeCell.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "AroundMeCell.h"
#import "Product.h"

@implementation AroundMeCell {
    UILabel *lblName;
	UILabel *lblDistance;
    UIView *imgFrame;
}

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        
        // image frame
        imgFrame = [[UIView alloc] initWithFrame:CGRectMake(10, 30, 75, 75)];
        
        // name
        lblName = [[UILabel alloc] init];
        
        lblName.frame = CGRectMake(10, 5, 200, 20);
        lblName.font = [UIFont boldSystemFontOfSize:16];
        lblName.textColor = [UIColor colorWithRed:0.204 green:0.553 blue:0.118 alpha:1.000];
        
        // distance
        UIImageView *mapMarkerView = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"icon-map-marker"]] autorelease];
        mapMarkerView.frame = CGRectMake(235, 5, 11.2, 18.2);
        
        lblDistance = [[UILabel alloc] init];
        lblDistance.frame = CGRectMake(250, 5, 100, 14);
        lblDistance.font = [UIFont systemFontOfSize:13];
        
        [self.contentView addSubview:imgFrame];
        [self.contentView addSubview:lblName];
        [self.contentView addSubview:mapMarkerView];
        [self.contentView addSubview:lblDistance];
    }
    return self;
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

- (void)setFarmer:(Farmer *)farmer
{
    // categories
    
    NSMutableSet *categoriesSet = [[NSMutableSet alloc] init];
    NSArray *products = [farmer.productFarmer allObjects];
    for (Product *product in products) {
        [categoriesSet addObject:product.categoryId];
    }
    
    int position = 0;
    for (NSNumber *categoryId in [categoriesSet allObjects]) {
        UIImageView *imgView = [[UIImageView alloc] initWithImage:
                                [UIImage imageNamed:[categoryId stringValue]]];
        imgView.frame = CGRectMake(position*50, 0, 29, 29);
        [imgFrame addSubview:imgView];
        [imgView release];
        position++;
    }
    
    [categoriesSet release];
    
    // name
    lblName.text = farmer.name;
    
    // distance
    double distance = farmer.distance / 1000;

    lblDistance.text = [NSString stringWithFormat:@"%.2f km", distance];
}

- (void)dealloc {
    [lblName release];
    [lblDistance release];
    [imgFrame release];
    [super dealloc];
}

@end
