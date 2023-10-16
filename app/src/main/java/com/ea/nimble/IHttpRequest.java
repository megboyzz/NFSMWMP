/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.net.URL;
import java.util.EnumSet;
import java.util.Map;

public interface IHttpRequest {
    byte[] getData();

    Map<String, String> getHeaders();

    Method getMethod();

    EnumSet<OverwritePolicy> getOverwritePolicy();

    boolean getRunInBackground();

    String getTargetFilePath();

    double getTimeout();

    URL getUrl();

    static enum Method {
        GET("GET"),
        HEAD("HEAD"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        UNRECOGNIZED("UNRECOGNIZED");
        
        private String title;

        Method(String title) {
            this.title = title;
        }

        public String toString() {
            return title;
        }
    }

    enum OverwritePolicy {
        RESUME_DOWNLOAD,
        DATE_CHECK,
        LENGTH_CHECK;

        static final EnumSet<OverwritePolicy> OVERWRITE;
        static final EnumSet<OverwritePolicy> SMART;

        static {
            OVERWRITE = EnumSet.noneOf(OverwritePolicy.class);
            SMART = EnumSet.allOf(OverwritePolicy.class);
        }
    }
}

