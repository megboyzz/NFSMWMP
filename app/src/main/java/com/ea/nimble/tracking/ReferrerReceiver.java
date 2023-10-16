/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.util.Log
 */
package com.ea.nimble.tracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ea.ironmonkey.devmenu.util.Observer;

public class ReferrerReceiver
extends BroadcastReceiver {
    public static final String TAG = "ReferrerReceiver";

    public static void clearReferrerId(Context context) {
        context.getSharedPreferences("referrer", 0).edit().putString("referrer", null).commit();
    }

    public static String getReferrerId(Context context) {
        return context.getSharedPreferences("referrer", 0).getString("referrer", null);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled unnecessary exception pruning
     */
    public void onReceive(Context var1_1, Intent var2_3) {
        Observer.onCallingMethod(Observer.Method.HARD_TO_RECOVER_LOGIC, Observer.Method.SUSPICIOUS_METHOD);
        /*
        if (var2_3 == null) return;
        if (!Objects.equals(var2_3.getAction(), "com.android.vending.INSTALL_REFERRER")) return;
        Log.i((String)"ReferrerReceiver", (String)"Received install referrer notification from the OS.");
        var2_3   = var2_3  .getStringExtra("referrer");
        if (var2_3   == null) return;
        var2_3   = URLDecoder.decode((String)var2_3  , "UTF-8");
        Log.i((String)"ReferrerReceiver", (String)("Referrer Id from the notification: " + (String)var2_3  ));
        {
            catch (Exception var1_2) {
                Log.w((String)"ReferrerReceiver", (String)"Unable to log a Referrer - 106 event indicating that Nimble has received a referrer id from the OS.");
                return;
            }
            try {
                if (ApplicationEnvironment.isMainApplicationRunning() && ApplicationEnvironment.getCurrentActivity() != null && Base.getComponent("com.ea.nimble.tracking") != null) {
                    var3_4 = (ITracking)Base.getComponent("com.ea.nimble.tracking");
                    var4_6 = new HashMap<String, String>();
                    var4_6.put("NIMBLESTANDARD::KEY_REFERRER_ID", (String)var2_3  );
                    var3_4.logEvent("NIMBLESTANDARD::REFERRER_ID_RECEIVED", var4_6);
                    return;
                }
                Log.w((String)"ReferrerReceiver", (String)("Unable to log Referrer - 106 event because the Tracking component isn't ready. Persisting referrerId (" + (String)var2_3   + ") to try again later."));
            }
            catch (Exception var3_5) {}
lbl-1000:
            // 2 sources

            {
                while (true) {
                    var1_1.getSharedPreferences("referrer", 0).edit().putString("referrer", (String)var2_3  ).commit();
                    return;
                }
            }
            {
                Log.w((String)"ReferrerReceiver", (String)("Unable to log Referrer - 106 event because the Tracking component isn't ready. Persisting referrerId (" + (String)var2_3   + ") to try again later."));
                ** continue;
            }
        }
        */
    }
    
}

