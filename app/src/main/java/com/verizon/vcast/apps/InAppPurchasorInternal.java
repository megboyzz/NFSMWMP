package com.verizon.vcast.apps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class InAppPurchasorInternal {
    protected static final String ODP_INTENT_CLASSNAME = "com.verizon.vcast.apps.VCastInAppService";
    protected static final String ODP_PURCHASE_CLASSNAME = "com.vzw.inapp.VZWInAppPurchase";
    protected static final String TAG = "InAppPurchasor";
    protected static final String VCAST_GM_ODP_IDENTIFIER = "com.gravitymobile.app.hornbill";
    protected static final String VCAST_UIE_ODP_IDENTIFIER = "com.verizon.vcast.apps";
    protected APIUtils apiUtils;
    protected Context c;
    protected boolean isVcastGMInstalled;
    protected boolean isVcastUIEInstalled;
    public String itemIDBeingPurchased;
    protected RemoteServiceManager serviceManager;

    /* access modifiers changed from: protected */
    public boolean isOdpInstalled(String str) {
        boolean z = false;
        try {
            this.c.getPackageManager().getApplicationInfo(str, 8192);
            z = true;
            Log.i(TAG, String.valueOf(str) + " is installed");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, String.valueOf(str) + " is not installed");
            return z;
        }
    }
}
