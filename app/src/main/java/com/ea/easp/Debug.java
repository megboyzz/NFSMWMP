package com.ea.easp;

public class Debug {
    public static boolean LogEnabled = true;

    public static class Log {
        public static void d(String str, String str2) {
            if (Debug.LogEnabled) {
                android.util.Log.d(str, str2);
            }
        }

        public static void e(String str, String str2) {
            if (Debug.LogEnabled) {
                android.util.Log.e(str, str2);
            }
        }

        public static void e(String str, String str2, Throwable th) {
            if (Debug.LogEnabled) {
                android.util.Log.e(str, str2, th);
            }
        }

        public static void i(String str, String str2) {
            if (Debug.LogEnabled) {
                android.util.Log.i(str, str2);
            }
        }

        public static void w(String str, String str2) {
            if (Debug.LogEnabled) {
                android.util.Log.w(str, str2);
            }
        }
    }
}
