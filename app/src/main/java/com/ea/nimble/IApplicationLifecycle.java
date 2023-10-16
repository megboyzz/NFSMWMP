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

public interface IApplicationLifecycle {
    boolean handleBackPressed();

    void notifyActivityCreate(Bundle var1, Activity var2);

    void notifyActivityDestroy(Activity var1);

    void notifyActivityPause(Activity var1);

    void notifyActivityRestart(Activity var1);

    void notifyActivityRestoreInstanceState(Bundle var1, Activity var2);

    void notifyActivityResult(int var1, int var2, Intent var3, Activity var4);

    void notifyActivityResume(Activity var1);

    void notifyActivityRetainNonConfigurationInstance();

    void notifyActivitySaveInstanceState(Bundle var1, Activity var2);

    void notifyActivityStart(Activity var1);

    void notifyActivityStop(Activity var1);

    void notifyActivityWindowFocusChanged(boolean var1, Activity var2);

    void registerActivityEventCallbacks(ActivityEventCallbacks var1);

    void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks var1);

    void registerApplicationLifecycleCallbacks(ApplicationLifecycleCallbacks var1);

    void unregisterActivityEventCallbacks(ActivityEventCallbacks var1);

    void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks var1);

    void unregisterApplicationLifecycleCallbacks(ApplicationLifecycleCallbacks var1);

    interface ActivityEventCallbacks {
        void onActivityResult(Activity var1, int var2, int var3, Intent var4);

        boolean onBackPressed();

        void onWindowFocusChanged(boolean var1);
    }

    interface ActivityLifecycleCallbacks {
        void onActivityCreated(Activity var1, Bundle var2);

        void onActivityDestroyed(Activity var1);

        void onActivityPaused(Activity var1);

        void onActivityResumed(Activity var1);

        void onActivitySaveInstanceState(Activity var1, Bundle var2);

        void onActivityStarted(Activity var1);

        void onActivityStopped(Activity var1);
    }

    interface ApplicationLifecycleCallbacks {
        void onApplicationLaunch(Intent var1);

        void onApplicationQuit();

        void onApplicationResume();

        void onApplicationSuspend();
    }
}

