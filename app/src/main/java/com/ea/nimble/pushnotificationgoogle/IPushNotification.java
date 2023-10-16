/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.pushnotificationgoogle;

import java.util.Map;

public interface IPushNotification {
    public void cleanup();

    public void register();

    public void sendPushNotificationTemplate(String var1, String var2, Map<String, String> var3, Map<String, String> var4);
}

