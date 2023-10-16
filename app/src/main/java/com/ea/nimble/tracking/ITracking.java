/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.tracking;

import java.util.Map;

public interface ITracking {
    public void addCustomSessionData(String var1, String var2);

    public void clearCustomSessionData();

    public boolean getEnable();

    public void logEvent(String var1, Map<String, String> var2);

    public void setEnable(boolean var1);

    public void setTrackingAttribute(String var1, String var2);
}

