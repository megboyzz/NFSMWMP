/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Intent
 *  android.os.Bundle
 */
package com.ea.nimble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.BaseCore;
import com.ea.nimble.IApplicationLifecycle;

public class ApplicationLifecycle {
    public static final String COMPONENT_ID = "com.ea.nimble.applicationlifecycle";

    public static IApplicationLifecycle getComponent() {
        return BaseCore.getInstance().getApplicationLifecycle();
    }

    public static void onActivityCreate(Bundle bundle, Activity activity) {
        ApplicationEnvironment.setCurrentActivity(activity);
        ApplicationLifecycle.getComponent().notifyActivityCreate(bundle, activity);
    }

    public static void onActivityDestroy(Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityDestroy(activity);
    }

    public static void onActivityPause(Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityPause(activity);
    }

    public static void onActivityRestart(Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityRestart(activity);
    }

    public static void onActivityRestoreInstanceState(Bundle bundle, Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityRestoreInstanceState(bundle, activity);
    }

    public static void onActivityResult(int n2, int n3, Intent intent, Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityResult(n2, n3, intent, activity);
    }

    public static void onActivityResume(Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityResume(activity);
    }

    public static void onActivityRetainNonConfigurationInstance() {
        ApplicationLifecycle.getComponent().notifyActivityRetainNonConfigurationInstance();
    }

    public static void onActivitySaveInstanceState(Bundle bundle, Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivitySaveInstanceState(bundle, activity);
    }

    public static void onActivityStart(Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityStart(activity);
    }

    public static void onActivityStop(Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityStop(activity);
    }

    public static void onActivityWindowFocusChanged(boolean bl2, Activity activity) {
        ApplicationLifecycle.getComponent().notifyActivityWindowFocusChanged(bl2, activity);
    }

    public static boolean onBackPressed() {
        return ApplicationLifecycle.getComponent().handleBackPressed();
    }
}

