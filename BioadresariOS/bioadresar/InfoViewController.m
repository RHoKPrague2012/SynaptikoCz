//
//  InfoViewController.m
//  bioadresar
//
//  Created by Tomas Hanacek on 08.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "InfoViewController.h"

#define TITLE_INFO @"Hnutí duha"
#define IMAGE_INFO @"icon-hnuti-duha"

@interface InfoViewController ()

@end

@implementation InfoViewController

#pragma mark - init

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // tab bar
        self.tabBarItem = [[[UITabBarItem alloc] initWithTitle:NSLocalizedString(TITLE_INFO, "") image:[UIImage imageNamed:IMAGE_INFO] tag:0] autorelease];
    }
    return self;
}

#pragma mark - view

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self setUpNavigationBar];
}

#pragma mark - navigation

- (void)setUpNavigationBar
{
    // title
    NSString *title = NSLocalizedString(TITLE_INFO, "");
    self.title = title;
    
    // navigation
    self.navigationController.navigationBar.tintColor = [UIColor colorWithRed:0.204 green:0.553 blue:0.118 alpha:1.000];
    
    // navigation title
    UILabel *titleLabel = [[[UILabel alloc] initWithFrame:CGRectZero] autorelease];
    titleLabel.backgroundColor = [UIColor clearColor];
    titleLabel.font = [UIFont boldSystemFontOfSize:18];
    titleLabel.textAlignment = UITextAlignmentCenter;
    titleLabel.textColor = [UIColor whiteColor];
    
    titleLabel.text = title;
    [titleLabel sizeToFit];
}

#pragma mark - dealloc

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}

@end
