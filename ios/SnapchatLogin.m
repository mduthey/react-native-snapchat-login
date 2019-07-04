#import "SnapchatLogin.h"
#import <SCSDKLoginKit/SCSDKLoginKit.h>

@implementation SnapchatLogin

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"AccessToken"];
}

RCT_REMAP_METHOD(login,
                 loginResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    UIViewController *rootViewController = [UIApplication sharedApplication].delegate.window.rootViewController;
    
    [SCSDKLoginClient loginFromViewController:rootViewController
                                   completion:^(BOOL success, NSError * _Nullable error) {
                                       if (error) {
                                           resolve([NSString stringWithFormat:@"{\"result\": false, \"error\": \"%@\"}", error.localizedDescription]);
                                       } else {
                                           resolve(@"{\"result\": true}");
                                       }
                                   }];
}

RCT_REMAP_METHOD(logout,
                 logoutResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    [SCSDKLoginClient unlinkAllSessionsWithCompletion:^(BOOL success) {
        resolve([NSString stringWithFormat:@"{\"result\": %s}", success ? "true" : "false"]);
    }];
}

RCT_REMAP_METHOD(isUserLoggedIn,
                 isUserLoggedInResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve([NSString stringWithFormat:@"{\"result\": %s}", [SCSDKLoginClient isUserLoggedIn] ? "true" : "false"]);
}

RCT_REMAP_METHOD(fetchUserData,
                 fetchUserDataResolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    if ([SCSDKLoginClient isUserLoggedIn]) {
        NSString *graphQLQuery = @"{me{displayName, externalId, bitmoji{avatar}}}";
        
        NSDictionary *variables = @{@"page": @"bitmoji"};
        
        [SCSDKLoginClient fetchUserDataWithQuery:graphQLQuery
                                       variables:variables
                                         success:^(NSDictionary *resources) {
                                             NSDictionary *data = resources[@"data"];
                                             NSDictionary *me = data[@"me"];
                                             NSString *displayName = me[@"displayName"];
                                             NSDictionary *bitmoji = me[@"bitmoji"];
                                             NSString *bitmojiAvatarUrl = bitmoji[@"avatar"];
                                             NSString *externalId = me[@"externalId"];
                                             resolve([NSString stringWithFormat:@"{\"displayName\": \"%@\", \"externalId\": \"%@\", \"avatar\": \"%@\"}", displayName, externalId, bitmojiAvatarUrl]);
                                         } failure:^(NSError * error, BOOL isUserLoggedOut) {
                                             reject(@"error", @"error", error);
                                         }];
    } else {
        resolve(@"null");
    }
}

RCT_EXPORT_METHOD(getAccessToken)
{
    [SCSDKLoginClient getAccessTokenWithCompletion:^(NSString * _Nullable accessToken, NSError *_Nullable error) {
        if (accessToken) {
            [self sendEventWithName:@"AccessToken" body:@{@"accessToken": accessToken}];
        } else {
            [self sendEventWithName:@"AccessToken" body:@{@"accessToken": @"null", @"error": error}];
        }
    }];
}

@end
