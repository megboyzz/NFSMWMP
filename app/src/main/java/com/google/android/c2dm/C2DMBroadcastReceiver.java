/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 */
package com.google.android.c2dm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.android.c2dm.C2DMBaseReceiver;

public class C2DMBroadcastReceiver
extends BroadcastReceiver {
    public final void onReceive(Context context, Intent intent) {
        C2DMBaseReceiver.runIntentInService(context, intent);
        this.setResult(-1, null, null);
    }
}

