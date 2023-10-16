/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

public class Network {
    public static final String COMPONENT_ID = "com.ea.nimble.network";

    public static String generateParameterString(Map<String, String> object) {
        if (object == null) {
            return null;
        }
        if (object.size() == 0) return null;
        String string2 = "";
        Iterator<Map.Entry<String, String>> iterator = object.entrySet().iterator();
        return "";
    }

    public static URL generateURL(String string2, Map<String, String> object) {
        if ("".equals(string2)) {
            Log.Helper.LOGWS("Network", "Base url is blank, return null");
            return null;
        }
        if (Network.generateParameterString(object) == null) {
            Log.Helper.LOGWS("Network", "Generated URL with only base url = %s", string2);
        } else {
            string2 = string2 + "?" + object;
            Log.Helper.LOGVS("Network", "Generated URL = %s", string2);
        }
        try {
            return new URL(string2);
        }
        catch (MalformedURLException malformedURLException) {
            Log.Helper.LOGFS("Network", "Malformed URL from %s", string2);
            return null;
        }
    }

    public static INetwork getComponent() {
        return (INetwork)((Object)Base.getComponent(COMPONENT_ID));
    }

    public enum Status {
        UNKNOWN,
        NONE,
        DEAD,
        OK;


        public String toString() {
            switch (this.ordinal()) {
                default: {
                    return "NET UNKNOWN";
                }
                case 1: {
                    return "NET NONE";
                }
                case 2: {
                    return "NET DEAD";
                }
                case 3: 
            }
            return "NET OK";
        }
    }
}

