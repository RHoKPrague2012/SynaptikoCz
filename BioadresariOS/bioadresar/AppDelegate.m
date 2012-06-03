//
//  AppDelegate.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "AppDelegate.h"
#import "AroundMeListViewController.h"
#import "AroundMeMapViewController.h"
#import "FarmersListViewController.h"
#import "SegmentsController.h"
#import "FarmersParser.h"

@interface AppDelegate () {
    SegmentsController *_segmentsController;
    UISegmentedControl *_segmentedControl;
}

@property (nonatomic, retain) SegmentsController *segmentsController;
@property (nonatomic, retain) UISegmentedControl *segmentedControl;

@end

@implementation AppDelegate

@synthesize window = _window;
@synthesize tabBarController = _tabBarController;
@synthesize segmentsController = _segmentsController;
@synthesize segmentedControl = _segmentedControl;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // load data TODO
    FarmersParser *farmersParser = [[FarmersParser alloc] init];
    [farmersParser parse];
    
    self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
    
    // segmented control
    NSArray * viewControllers = [self segmentViewControllers];
    UINavigationController * navigationController = [[[UINavigationController alloc] init] autorelease];
    self.segmentsController = [[SegmentsController alloc] initWithNavigationController:navigationController viewControllers:viewControllers];
    
    self.segmentedControl = [[UISegmentedControl alloc] initWithItems:[NSArray arrayWithObjects:@"Seznam", @"Mapa" , nil]];
    self.segmentedControl.segmentedControlStyle = UISegmentedControlStyleBar;
    
    [self.segmentedControl addTarget:self.segmentsController
                              action:@selector(indexDidChangeForSegmentedControl:)
                    forControlEvents:UIControlEventValueChanged];
    
    [self firstUserExperience];
    
    // farmers list
    FarmersListViewController *farmersListViewController = [[FarmersListViewController alloc] init];
    UINavigationController *farmersListViewNavigationController = [[UINavigationController alloc] initWithRootViewController:farmersListViewController];
    
    // tab bar
    self.tabBarController = [[UITabBarController alloc] init];
    self.tabBarController.viewControllers = [NSArray arrayWithObjects:navigationController, farmersListViewNavigationController, nil];
    
    self.window.rootViewController = self.tabBarController;
    
    [self.window makeKeyAndVisible];
    return YES;
}

- (NSArray *)segmentViewControllers {
    AroundMeListViewController *aroundMeListViewController = [[AroundMeListViewController alloc] init];
    AroundMeMapViewController *aroundMeMapViewController = [[AroundMeMapViewController alloc] init];
    
    NSArray * viewControllers = [NSArray arrayWithObjects:aroundMeListViewController, aroundMeMapViewController, nil];
    [aroundMeListViewController release];
    [aroundMeMapViewController release];
    
    return viewControllers;
}

- (void)firstUserExperience {
    self.segmentedControl.selectedSegmentIndex = 0;
    [self.segmentsController indexDidChangeForSegmentedControl:self.segmentedControl];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

#pragma mark - dealloc

- (void)dealloc
{
    [_window release];
    [_tabBarController release];
    [super dealloc];
}

@end
