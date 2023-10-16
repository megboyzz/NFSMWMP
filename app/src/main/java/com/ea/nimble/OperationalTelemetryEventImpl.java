/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.OperationalTelemetryEvent;
import java.util.Date;
import java.util.Map;

class OperationalTelemetryEventImpl
implements OperationalTelemetryEvent {
    private Map<String, String> m_eventDictionary;
    private String m_eventType;
    private Date m_loggedTime;

    public OperationalTelemetryEventImpl(String string2, Map<String, String> map, Date date) {
        this.m_eventType = string2;
        this.m_eventDictionary = map;
        this.m_loggedTime = date;
    }

    @Override
    public Map<String, String> getEventDictionary() {
        return this.m_eventDictionary;
    }

    @Override
    public String getEventType() {
        return this.m_eventType;
    }

    @Override
    public Date getLoggedTime() {
        return this.m_loggedTime;
    }

    public String toString() {
        return String.format("OperationalTelemetryEvent(%s)-(%s) > %s", this.getEventType(), this.getLoggedTime(), this.getEventDictionary());
    }
}

