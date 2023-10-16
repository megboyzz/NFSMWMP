package com.ea.easp;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import java.util.Calendar;
import java.util.Locale;

public class DeviceInfoUtil {
    private static final String TAG = "DeviceInfoUtil";
    private static ConnectivityManager connectivityManager = null;
    private static TelephonyManager telephonyManager = null;
    private static WifiManager wifiManager = null;

    public static String GetApplicationName() {
        ApplicationInfo applicationInfo;
        Debug.Log.d(TAG, "GetApplicationName()...");
        PackageManager packageManager = EASPHandler.mActivity.getPackageManager();
        try {
            applicationInfo = EASPHandler.mActivity.getApplicationInfo();
        } catch (Exception e) {
            Debug.Log.e(TAG, "[GetApplicationName] getApplicationInfo Exception : " + e);
            applicationInfo = null;
        }
        String str = (String) (applicationInfo != null ? packageManager.getApplicationLabel(applicationInfo) : "");
        Debug.Log.d(TAG, "...GetApplicationName()");
        return str;
    }

    public static String GetCurrentTimeZoneAbbreviation() {
        Debug.Log.d(TAG, "GetCurrentTimeZoneAbbreviation()...");
        String format = String.format(Locale.US, "%tZ", Calendar.getInstance());
        Debug.Log.d(TAG, "...GetCurrentTimeZoneAbbreviation()");
        return format;
    }

    public static String GetDeviceCountry() {
        Debug.Log.d(TAG, "GetDeviceCountry()...");
        Locale locale = Locale.getDefault();
        Debug.Log.d(TAG, "...GetDeviceCountry()");
        return locale.getCountry();
    }

    public static String getAndroidID() {
        Debug.Log.d(TAG, "getAndroidID()...");
        String string = Settings.Secure.getString(EASPHandler.mActivity.getContentResolver(), "android_id");
        Debug.Log.d(TAG, "...getAndroidID()");
        return string;
    }

    public static String getBuildVersionSDK_INT() {
        return "" + Build.VERSION.SDK_INT;
    }

    public static String getMacAddress() {
        Debug.Log.d(TAG, "getMacAddress()...");
        String str = null;
        if (wifiManager != null) {
            WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null) {
                str = connectionInfo.getMacAddress();
                Debug.Log.d(TAG, "MAC address: " + str);
            } else {
                Debug.Log.e(TAG, "WifiInfo is not available");
            }
        } else {
            Debug.Log.e(TAG, "WifiManager is not available");
        }
        Debug.Log.d(TAG, "...getMacAddress()");
        return str;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL;
    }

    public static String getNetworkOperator() {
        Debug.Log.d(TAG, "getNetworkOperator()...");
        String str = null;
        if (telephonyManager != null) {
            str = telephonyManager.getNetworkOperator();
        } else {
            Debug.Log.e(TAG, "TelephonyManager is not available");
        }
        Debug.Log.d(TAG, "...getNetworkOperator()");
        return str;
    }

    public static String getNetworkType() {
        NetworkInfo activeNetworkInfo;
        if (connectivityManager == null || (activeNetworkInfo = connectivityManager.getActiveNetworkInfo()) == null || !activeNetworkInfo.isConnectedOrConnecting()) {
            return null;
        }
        switch (activeNetworkInfo.getType()) {
            case 0:
                return activeNetworkInfo.getSubtypeName();
            case 1:
                return "WIFI";
            default:
                return (Build.VERSION.SDK_INT < 8 || activeNetworkInfo.getType() != 6) ? "UNKNOWN" : "WIMAX";
        }
    }

    public static String getPlatformVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getTelephonyDeviceID() {
        Debug.Log.d(TAG, "getTelephonyDeviceID()...");
        String str = null;
        if (telephonyManager != null) {
            str = telephonyManager.getDeviceId();
        } else {
            Debug.Log.e(TAG, "TelephonyManager is not available");
        }
        Debug.Log.d(TAG, "...getTelephonyDeviceID()");
        return str;
    }

    public static void init() {
        Debug.Log.d(TAG, "init()...");
        Activity activity = EASPHandler.mActivity;
        telephonyManager = (TelephonyManager) activity.getSystemService("phone");
        wifiManager = (WifiManager) activity.getSystemService("wifi");
        connectivityManager = (ConnectivityManager) activity.getSystemService("connectivity");
        initJNI();
        Debug.Log.d(TAG, "...init()");
    }

    public static native void initJNI();

    public static void shutdown() {
        Debug.Log.d(TAG, "shutdown()...");
        shutdownJNI();
        telephonyManager = null;
        wifiManager = null;
        connectivityManager = null;
        Debug.Log.d(TAG, "...shutdown()");
    }

    public static native void shutdownJNI();
}
