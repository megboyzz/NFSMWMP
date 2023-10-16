/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

public enum NimbleConfiguration {
    UNKNOWN,
    INTEGRATION,
    STAGE,
    LIVE,
    CUSTOMIZED;


    public static NimbleConfiguration fromName(String string2) {
        if (string2.equals("int")) {
            return INTEGRATION;
        }
        if (string2.equals("stage")) {
            return STAGE;
        }
        if (string2.equals("live")) {
            return LIVE;
        }
        if (!string2.equals("custom")) return UNKNOWN;
        return CUSTOMIZED;
    }

    public String toString() {
        switch (this.ordinal()) {
            default: {
                return "unknown";
            }
            case 1: {
                return "int";
            }
            case 2: {
                return "stage";
            }
            case 3: {
                return "live";
            }
            case 4: 
        }
        return "custom";
    }
}

