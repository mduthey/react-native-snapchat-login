# react-native-snapchat-login

## Getting started

`$ npm install react-native-snapchat-login --save`

### Mostly automatic installation

**On iOS use CocoaPods**

`$ react-native link react-native-snapchat-login`

## Manual steps

#### iOS
Add to `Info.plist`

```
<key>SCSDKClientId</key>
<string>YOUR CLIENT ID</string>

<key>SCSDKRedirectUrl</key>
<string>YOUR REDIRECT URL</string>

<key>SCSDKScopes</key>
<array>
     <string>https://auth.snapchat.com/oauth2/api/user.display_name</string>
    <string>https://auth.snapchat.com/oauth2/api/user.bitmoji.avatar</string>
</array>

<key>LSApplicationQueriesSchemes</key>
<array>
    <string>snapchat</string>
    <string>bitmoji-sdk</string>
    <string>itms-apps</string>
</array>
```

**REMEMBER** Add the app url to your URL Types on Xcode config.

Update the `AppDelegate.m`

```objc
#import <SCSDKLoginKit/SCSDKLoginKit.h>

- (BOOL)application:(UIApplication *)application
            openURL:(NSURL *)url
            options:(NSDictionary<UIApplicationOpenURLOptionsKey, id> *)options {
  if ([SCSDKLoginClient application:application openURL:url options:options]) {
    return YES;
  }
  
  return NO;
}
```

#### Android
Update `android/build.gradle` with the min SDK Version
```
minSdkVersion = 19
```
and add 
```
maven {
    url "https://storage.googleapis.com/snap-kit-build/maven"
}
```
to your repositories list.

Update `AndroidManifest.xml`

Add the INTERNET permission
```
<uses-permission android:name="android.permission.INTERNET" />
```

Add this to your application
```
<meta-data android:name="com.snapchat.kit.sdk.clientId" android:value="YOUR CLIENT ID" />
<meta-data android:name="com.snapchat.kit.sdk.redirectUrl" android:value="YOUR REDIRECT URL" />
<meta-data android:name="com.snapchat.kit.sdk.scopes" android:resource="@array/snap_connect_scopes" />

<activity android:name="com.snapchat.kit.sdk.SnapKitActivity" android:launchMode="singleTask">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <!--
            Enter the parts of your redirect url below
            e.g., if your redirect url is myapp://snap-kit/oauth2
                android:scheme="myapp"
                android:host="snap-kit"
                android:path="/oauth2"
        !-->
        <data
            android:scheme="the scheme of your redirect url"
            android:host="the host of your redirect url"
            android:path="the path of your redirect url"
        />
    </intent-filter>
</activity>
```

Create a new file `values/arrays.xml`
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string-array name="snap_connect_scopes">
        <item>https://auth.snapchat.com/oauth2/api/user.display_name</item>
        <item>https://auth.snapchat.com/oauth2/api/user.bitmoji.avatar</item>
    </string-array>
</resources>
```

## Usage
```javascript
import SnapchatLogin from 'react-native-snapchat-login';

// TODO: What to do with the module?
SnapchatLogin;
```