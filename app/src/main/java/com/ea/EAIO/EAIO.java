package com.ea.EAIO;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Environment;

public class EAIO {
    public static native void Shutdown();

    public static void Startup(Activity activity) {
        StartupNativeImpl(activity.getAssets(), Environment.getDataDirectory().getAbsolutePath(), activity.getFilesDir().getAbsolutePath(), Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    private static native void StartupNativeImpl(AssetManager assetManager, String dataDirectory, String filesDir, String externalStorage);
}
