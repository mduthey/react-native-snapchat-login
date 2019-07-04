package com.reactlibrary;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import android.support.annotation.Nullable;

import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;

public class SnapchatLoginModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final LoginStateController.OnLoginStateChangedListener mLoginStateChangedListener =
            new LoginStateController.OnLoginStateChangedListener() {
                @Override
                public void onLoginSucceeded() {
                    sendEvent("LoginSucceeded", null);
                }

                @Override
                public void onLoginFailed() {
                    sendEvent("LoginFailed", null);
                }

                @Override
                public void onLogout() {
                    sendEvent("LogOut", null);
                }
            };

    public SnapchatLoginModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    public void sendEvent(String eventName, @Nullable WritableMap params) {
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public String getName() {
        return "SnapchatLogin";
    }

    @ReactMethod
    public void login(final Promise promise) {
        try {
            SnapLogin.getLoginStateController(getReactApplicationContext()).addOnLoginStateChangedListener(this.mLoginStateChangedListener);
            SnapLogin.getAuthTokenManager(getReactApplicationContext()).startTokenGrant();
            promise.resolve("{\"result\": true}");
        } catch (Exception e) {
            promise.resolve("{\"result\": false, \"error\": "+ e.toString() +"}");
        }
    }

    @ReactMethod
    public void logout(final Promise promise) {
        try {
            SnapLogin.getAuthTokenManager(getReactApplicationContext()).revokeToken();
            promise.resolve("{\"result\": true}");
        } catch (Exception e) {
            promise.resolve("{\"result\": false, \"error\": "+ e.toString() +"}");
        }
    }

    @ReactMethod
    public void isUserLoggedIn(Promise promise) {
        boolean isTrue = SnapLogin.isUserLoggedIn(getReactApplicationContext());
        promise.resolve("{\"result\": " + isTrue + "}");
    }

    @ReactMethod
    public void fetchUserData(final Promise promise) {
        String query = "{me{bitmoji{avatar},displayName,externalId}}";
        if(SnapLogin.isUserLoggedIn(getReactApplicationContext())){
            SnapLogin.fetchUserData(getReactApplicationContext(), query, null, new FetchUserDataCallback() {
                @Override
                public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                    if (userDataResponse == null || userDataResponse.getData() == null) {
                        promise.resolve(null);
                        return;
                    }

                    MeData meData = userDataResponse.getData().getMe();
                    if (meData == null) {
                        promise.resolve(null);
                        return;
                    }
                    String output = "{"
                            + "\"displayName\": \"" + meData.getDisplayName() + "\""
                            + ", \"externalId\": \"" + meData.getExternalId() + "\""
                            + ", \"avatar\": \"" + meData.getBitmojiData().getAvatar() + "\""
                            + ", \"accessToken\": \""+ SnapLogin.getAuthTokenManager(getReactApplicationContext()).getAccessToken() + "\""
                            + "}";
                    promise.resolve(output);
                }

                @Override
                public void onFailure(boolean b, int i) {
                    String I = Integer.toString(i);

                    promise.reject(I);
                }
            });
        } else {
            promise.resolve(null);
        }
    }

    @ReactMethod
    public void getAccessToken(final Promise promise) {
        try {
            String accessToken = SnapLogin.getAuthTokenManager(getReactApplicationContext()).getAccessToken();
            promise.resolve("{\"accessToken\": \"" + accessToken + "\"}");
        } catch (Exception e) {
            promise.resolve("{\"accessToken\": \"null\", \"error\": \"" + e.toString() + "\"}");
        }
    }

}
