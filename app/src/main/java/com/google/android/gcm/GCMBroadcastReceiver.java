/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.util.Log
 */
package com.google.android.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMBroadcastReceiver
extends BroadcastReceiver {
    private static final String TAG = "GCMBroadcastReceiver";
    private static boolean mReceiverSet = false;

    static final String getDefaultIntentServiceClassName(Context context) {
        return context.getPackageName() + ".GCMIntentService";
    }

    protected String getGCMIntentServiceClassName(Context context) {
        return GCMBroadcastReceiver.getDefaultIntentServiceClassName(context);
    }

    public final void onReceive(Context context, Intent intent) {
        String string;
        Log.v((String)TAG, (String)("onReceive: " + intent.getAction()));
        if (!mReceiverSet) {
            mReceiverSet = true;
            string = ((Object)((Object)this)).getClass().getName();
            if (!string.equals(GCMBroadcastReceiver.class.getName())) {
                GCMRegistrar.setRetryReceiverClassName(string);
            }
        }
        string = this.getGCMIntentServiceClassName(context);
        Log.v((String)TAG, (String)("GCM IntentService class: " + string));
        GCMBaseIntentService.runIntentInService(context, intent, string);
        this.setResult(-1, null, null);
    }
}

