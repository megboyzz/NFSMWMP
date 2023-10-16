/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.ea.nimble.pushnotificationgoogle;

import android.content.Context;
import com.ea.nimble.pushnotificationgoogle.GCMIntentService;
import com.google.android.gcm.GCMBroadcastReceiver;

public class NimbleBroadcastReceiver
extends GCMBroadcastReceiver {
    @Override
    protected String getGCMIntentServiceClassName(Context context) {
        return GCMIntentService.class.getName();
    }
}

