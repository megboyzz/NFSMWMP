package com.ea.EAMIO;

import android.app.Activity;
import android.os.Environment;

public class StorageDirectory {
    public static Activity sActivity;

    public static String GetDedicatedDirectory() {
        return "Android/data/" + sActivity.getClass().getPackage().getName() + "/files/";
    }

    public static String GetInternalStorageDirectory() {
        return sActivity.getFilesDir().getAbsolutePath();
    }

    public static String GetPrimaryExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String GetPrimaryExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    public static void Shutdown() {
        ShutdownNativeImpl();
    }

    private static native void ShutdownNativeImpl();

    public static void Startup(Activity activity) {
        sActivity = activity;
        StartupNativeImpl();
    }

    private static native void StartupNativeImpl();
}
