//
//  AppDelegate.m
//  bioadresar
//
//  Created by Tomas Hanacek on 02.06.12.
//  Copyright (c) 2012 Tomas Hanacek. All rights reserved.
//

#import "AppDelegate.h"
#import "FarmersListViewController.h"
#import "FarmersParser.h"
#import "AroundMeViewController.h"
#import "InfoViewController.h"

@implementation AppDelegate

@synthesize window = _window;
@synthesize tabBarController = _tabBarController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // load data TODO
    //FarmersParser *farmersParser = [[FarmersParser alloc] init];
    //[farmersParser parse];
    
    self.window = [[[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]] autorelease];
    
    // generic around me controller
    AroundMeViewController *aroundMeViewController = [[AroundMeViewController alloc] init];
    UINavigationController *aroundMeNavigationController = [[UINavigationController alloc] initWithRootViewController:aroundMeViewController];
    [aroundMeViewController release];
        
    // farmers list
    FarmersListViewController *farmersListViewController = [[FarmersListViewController alloc] initWithNibName:@"FarmersListViewController" bundle:[NSBundle mainBundle]];
    UINavigationController *farmersListViewNavigationController = [[UINavigationController alloc] initWithRootViewController:farmersListViewController];
    [farmersListViewController release];
    
    // info
    InfoViewController *infoViewController = [[InfoViewController alloc] init];
    UINavigationController *infoViewNavigationController = [[UINavigationController alloc] initWithRootViewController:infoViewController];
    [infoViewController release];
    
    // tab bar
    self.tabBarController = [[[UITabBarController alloc] init] autorelease];
    self.tabBarController.viewControllers = [NSArray arrayWithObjects:aroundMeNavigationController, farmersListViewNavigationController, infoViewNavigationController, nil];
    
    [farmersListViewNavigationController release];
    [aroundMeNavigationController release];
    [infoViewNavigationController release];
    
    self.window.rootViewController = self.tabBarController;
    
    [self.window makeKeyAndVisible];
    return YES;
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
