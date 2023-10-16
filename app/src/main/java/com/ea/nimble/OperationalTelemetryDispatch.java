/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Base;
import com.ea.nimble.IOperationalTelemetryDispatch;

public class OperationalTelemetryDispatch {
    public static final String COMPONENT_ID = "com.ea.nimble.operationaltelemetrydispatch";
    public static final String LOG_TAG = "OTDispatch";

    public static IOperationalTelemetryDispatch getComponent() {
        return (IOperationalTelemetryDispatch)((Object)Base.getComponent(COMPONENT_ID));
    }
}

