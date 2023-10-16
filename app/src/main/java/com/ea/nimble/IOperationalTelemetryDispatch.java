/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.OperationalTelemetryEvent;
import java.util.List;
import java.util.Map;

public interface IOperationalTelemetryDispatch {
    public static final String EVENTTYPE_NETWORK_METRICS = "com.ea.nimble.network";
    public static final String EVENTTYPE_TRACKING_SYNERGY_PAYLOADS = "com.ea.nimble.trackingimpl.synergy";
    public static final int NIMBLE_DEFAULT_MAX_OT_EVENT_COUNT = 100;
    public static final String NOTIFICATION_OT_EVENT_THRESHOLD_WARNING = "nimble.notification.ot.eventthresholdwarning";

    public List<OperationalTelemetryEvent> getEvents(String var1);

    public int getMaxEventCount(String var1);

    public void logEvent(String var1, Map<String, String> var2);

    public void setMaxEventCount(String var1, int var2);
}

