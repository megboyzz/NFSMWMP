package com.ea.ironmonkey;

public class Log {
    private static boolean enable = false;

    public static void d(String str, String str2) {
        if (enable) {
            android.util.Log.d(str, str2);
        }
    }

    public static void e(String str, String str2) {
        if (enable) {
            android.util.Log.e(str, str2);
        }
    }

    public static void e(String str, String str2, Exception exc) {
        if (enable) {
            android.util.Log.e(str, str2, exc);
        }
    }

    public static void i(String str, String str2) {
        if (enable) {
            android.util.Log.i(str, str2);
        }
    }

    public static void setEnable(boolean z) {
        enable = z;
    }

    public static void v(String str, String str2) {
        if (enable) {
            android.util.Log.v(str, str2);
        }
    }

    public static void w(String str, String str2) {
        if (enable) {
            android.util.Log.w(str, str2);
        }
    }
}
