package com.ea.nimble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

class FacebookImpl extends Component implements IApplicationLifecycle.ActivityEventCallbacks, IApplicationLifecycle.ActivityLifecycleCallbacks, IFacebook, LogSource {
    static boolean bHasSeenOpening;
    private static String TAG = "Facebook";
    private static List<IFacebook.FacebookCallback> m_callbackQueue = new ArrayList();
    private Map<String, Object> userInfo = null;
    private AtomicBoolean isUserGraphReady = new AtomicBoolean(true);

    private String getFacebookSDKVersion() {
        try {
            return String.valueOf(getClass().getClassLoader().loadClass("com.facebook.FacebookSdkVersion").getField("BUILD").get(null));
        } catch (ClassNotFoundException e) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        } catch (IllegalAccessException e2) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        } catch (IllegalArgumentException e3) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        } catch (NoSuchFieldException e4) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.Component
    public void cleanup() {
        ApplicationLifecycle.getComponent().unregisterActivityLifecycleCallbacks(this);
        ApplicationLifecycle.getComponent().unregisterActivityEventCallbacks(this);
    }

    @Override // com.ea.nimble.IFacebook
    public String getAccessToken() {
        return null;
    }

    @Override // com.ea.nimble.IFacebook
    public String getApplicationId() {
        return null;
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return Facebook.COMPONENT_ID;
    }

    @Override // com.ea.nimble.IFacebook
    public Date getExpirationDate() {
        return null;
    }

    @Override // com.ea.nimble.IFacebook
    public Map<String, Object> getGraphUser() {
        if (this.isUserGraphReady.get()) {
            return this.userInfo;
        }
        return null;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "Facebook";
    }

    @Override // com.ea.nimble.IFacebook
    public boolean hasOpenSession() {
        return false;
    }

    @Override // com.ea.nimble.IFacebook
    public void login(List<String> list, IFacebook.FacebookCallback facebookCallback) {

    }

    @Override // com.ea.nimble.IFacebook
    public void logout() {

    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {

    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {

    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {

    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public boolean onBackPressed() {
        return true;
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public void onWindowFocusChanged(boolean z) {
    }

    @Override // com.ea.nimble.IFacebook
    public void refreshSession(final String str, Date date) {

    }

    @Override
    public void restore() {
        ApplicationLifecycle.getComponent().registerActivityEventCallbacks(this);
        ApplicationLifecycle.getComponent().registerActivityLifecycleCallbacks(this);
    }

    public void retrieveCurrentUserProfile(IFacebook.FacebookFriendsCallback facebookFriendsCallback) {
    }

    @Override // com.ea.nimble.IFacebook
    public void retrieveFriends(final int i, final int i2, final IFacebook.FacebookFriendsCallback facebookFriendsCallback) {

    }

    @Override // com.ea.nimble.IFacebook
    public void sendAppRequest(final String str, final String str2, final String str3, final IFacebook.FacebookCallback facebookCallback) {

    }


    @Override // com.ea.nimble.Component
    public void setup() {

    }
}
