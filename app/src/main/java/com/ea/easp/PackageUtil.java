package com.ea.easp;

import android.content.Intent;
import com.ea.easp.Debug;

public class PackageUtil {
    private static final String TAG = "PackageUtil";

    public static void init() {
        initJNI();
    }

    public static native void initJNI();

    public static void launchApplication(String str, String[] strArr, String[] strArr2) {
        Debug.Log.d(TAG, "launchApplication()...");
        Intent launchIntentForPackage = EASPHandler.mActivity.getPackageManager().getLaunchIntentForPackage(str);
        for (int i = 0; i < strArr.length; i++) {
            launchIntentForPackage.putExtra(strArr[i], strArr2[i]);
        }
        EASPHandler.mActivity.startActivity(launchIntentForPackage);
        Debug.Log.d(TAG, "...launchApplication()");
    }

    public static boolean packageIsInstalled(String str) {
        Debug.Log.d(TAG, "packageIsInstalled()...");
        Intent launchIntentForPackage = EASPHandler.mActivity.getPackageManager().getLaunchIntentForPackage(str);
        Debug.Log.d(TAG, "...packageIsInstalled()");
        return launchIntentForPackage != null;
    }

    public static void shutdown() {
        shutdownJNI();
    }

    public static native void shutdownJNI();
}
