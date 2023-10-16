/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.ILog;
import com.ea.nimble.LogImpl;

public class Log {
    public static final String COMPONENT_ID = "com.ea.nimble.NimbleLog";
    public static final int LEVEL_DEBUG = 200;
    public static final int LEVEL_ERROR = 500;
    public static final int LEVEL_FATAL = 600;
    public static final int LEVEL_INFO = 300;
    public static final int LEVEL_SILENT = 700;
    public static final int LEVEL_VERBOSE = 100;
    public static final int LEVEL_WARN = 400;
    private static ILog s_instance;

    public static ILog getComponent() {
        synchronized (Log.class) {
            if (s_instance == null) {
                s_instance = new LogImpl();
            }
            ILog iLog = s_instance;
            return iLog;
        }
    }

    public static class Helper {
        public static void LOG(int n2, String string2, Object ... objectArray) {
            Log.getComponent().writeWithTitle(n2, null, string2, objectArray);
        }

        public static void LOGD(Object object, String string2, Object ... objectArray) {
            Log.getComponent().writeWithSource(200, object, string2, objectArray);
        }

        public static void LOGDS(String string2, String string3, Object ... objectArray) {
            Log.getComponent().writeWithTitle(200, string2, string3, objectArray);
        }

        public static void LOGE(Object object, String string2, Object ... objectArray) {
            Log.getComponent().writeWithSource(500, object, string2, objectArray);
        }

        public static void LOGES(String string2, String string3, Object ... objectArray) {
            Log.getComponent().writeWithTitle(500, string2, string3, objectArray);
        }

        public static void LOGF(Object object, String string2, Object ... objectArray) {
            Log.getComponent().writeWithSource(600, object, string2, objectArray);
        }

        public static void LOGFS(String string2, String string3, Object ... objectArray) {
            Log.getComponent().writeWithTitle(600, string2, string3, objectArray);
        }

        public static void LOGI(Object object, String string2, Object ... objectArray) {
            Log.getComponent().writeWithSource(300, object, string2, objectArray);
        }

        public static void LOGIS(String string2, String string3, Object ... objectArray) {
            Log.getComponent().writeWithTitle(300, string2, string3, objectArray);
        }

        public static void LOGV(Object object, String string2, Object ... objectArray) {
            Log.getComponent().writeWithSource(100, object, string2, objectArray);
        }

        public static void LOGVS(String string2, String string3, Object ... objectArray) {
            Log.getComponent().writeWithTitle(100, string2, string3, objectArray);
        }

        public static void LOGW(Object object, String string2, Object ... objectArray) {
            Log.getComponent().writeWithSource(400, object, string2, objectArray);
        }

        public static void LOGWS(String string2, String string3, Object ... objectArray) {
            Log.getComponent().writeWithTitle(400, string2, string3, objectArray);
        }
    }
}

