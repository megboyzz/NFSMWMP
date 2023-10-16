/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.util.Date;
import java.util.Map;

public interface OperationalTelemetryEvent {
    public Map<String, String> getEventDictionary();

    public String getEventType();

    public Date getLoggedTime();
}

