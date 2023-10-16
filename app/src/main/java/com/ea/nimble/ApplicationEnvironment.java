/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 */
package com.ea.nimble;

import android.app.Activity;
import com.ea.nimble.ApplicationEnvironmentImpl;
import com.ea.nimble.BaseCore;
import com.ea.nimble.IApplicationEnvironment;

public class ApplicationEnvironment {
    public static final String COMPONENT_ID = "com.ea.nimble.applicationEnvironment";
    public static final String NOTIFICATION_AGE_COMPLIANCE_REFRESHED = "nimble.notification.age_compliance_refreshed";

    public static IApplicationEnvironment getComponent() {
        return BaseCore.getInstance().getApplicationEnvironment();
    }

    public static Activity getCurrentActivity() {
        return ApplicationEnvironmentImpl.getCurrentActivity();
    }

    public static boolean isMainApplicationRunning() {
        return ApplicationEnvironmentImpl.isMainApplicationRunning();
    }

    public static void setCurrentActivity(Activity activity) {
        ApplicationEnvironmentImpl.setCurrentActivity(activity);
    }
}

