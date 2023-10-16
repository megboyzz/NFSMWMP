/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class OperationalTelemetryDispatchImpl
extends Component
implements IOperationalTelemetryDispatch,
LogSource {
    private Map<String, Integer> m_maxEventQueueSizeDict;
    private List<OperationalTelemetryEvent> m_networkMetricsArray = new ArrayList<OperationalTelemetryEvent>();
    private List<OperationalTelemetryEvent> m_networkPayloadsArray = new ArrayList<OperationalTelemetryEvent>();

    public OperationalTelemetryDispatchImpl() {
        this.m_maxEventQueueSizeDict = new HashMap<String, Integer>();
        this.m_maxEventQueueSizeDict.put("com.ea.nimble.network", 100);
        this.m_maxEventQueueSizeDict.put("com.ea.nimble.trackingimpl.synergy", 100);
    }

    private boolean canLogEvent(String list) {
        return true;
    }

    /*
     * Enabled unnecessary exception pruning
     */
    private void purgeOldestEvent(List<OperationalTelemetryEvent> list) {
        synchronized (this) {
            if (list.size() == 0) {
                Log.Helper.LOGD(this, "purgeOldestEvent called with empty event array.");
                return;
            }
            OperationalTelemetryEvent operationalTelemetryEvent = null;
            Iterator<OperationalTelemetryEvent> iterator = list.iterator();
            while (true) {
                if (!iterator.hasNext()) {
                    if (operationalTelemetryEvent == null) return;
                    list.remove(operationalTelemetryEvent);
                    return;
                }
                OperationalTelemetryEvent operationalTelemetryEvent2 = iterator.next();
                if (operationalTelemetryEvent != null && !operationalTelemetryEvent2.getLoggedTime().before(operationalTelemetryEvent.getLoggedTime())) continue;
                operationalTelemetryEvent = operationalTelemetryEvent2;
            }
        }
    }

    /*
     * Enabled unnecessary exception pruning
     */
    private void trimEventQueue(String list) {
    }

    private void updateEventThresholdListeners() {
        HashMap<String, String> hashMap;
        int n2 = this.getMaxEventCount("com.ea.nimble.network");
        if (n2 > 0) {
            n2 = (int)((double)n2 * 0.75);
            if (this.m_networkMetricsArray.size() >= n2) {
                hashMap = new HashMap<String, String>();
                hashMap.put("eventType", "com.ea.nimble.network");
                Utility.sendBroadcast("nimble.notification.ot.eventthresholdwarning", hashMap);
                Log.Helper.LOGV(this, "updateEventThresholdListeners, notifying listeners event queue is approaching threshold.");
            }
        }
        if ((n2 = this.getMaxEventCount("com.ea.nimble.trackingimpl.synergy")) <= 0) return;
        n2 = (int)((double)n2 * 0.75);
        if (this.m_networkPayloadsArray.size() < n2) return;
        hashMap = new HashMap();
        hashMap.put("eventType", "com.ea.nimble.trackingimpl.synergy");
        Utility.sendBroadcast("nimble.notification.ot.eventthresholdwarning", hashMap);
        Log.Helper.LOGV(this, "updateEventThresholdListeners, notifying listeners event queue is approaching threshold.");
    }

    @Override
    protected void cleanup() {
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.operationaltelemetrydispatch";
    }

    /*
     * Enabled unnecessary exception pruning
     */
    @Override
    public List<OperationalTelemetryEvent> getEvents(String string2) {
        if (!Utility.validString(string2)) {
            Log.Helper.LOGE(this, "getEvents called with null or empty eventType.");
            return null;
        }
        List<OperationalTelemetryEvent> list = null;
        synchronized (this) {
            if (string2.equals("com.ea.nimble.network")) {
                list = this.m_networkMetricsArray;
                this.m_networkMetricsArray = new ArrayList<OperationalTelemetryEvent>();
            } else if (string2.equals("com.ea.nimble.trackingimpl.synergy")) {
                list = this.m_networkPayloadsArray;
                this.m_networkPayloadsArray = new ArrayList<OperationalTelemetryEvent>();
            }
        }
        if (list != null) return Collections.unmodifiableList(list);
        Log.Helper.LOGE(this, "getEvents, unsupported OT eventType, " + string2 + ".");
        return Collections.unmodifiableList(list);
    }

    @Override
    public String getLogSourceTitle() {
        return "OTDispatch";
    }

    @Override
    public int getMaxEventCount(String object) {
        return 0;
    }

    /*
     * Enabled unnecessary exception pruning
     * Converted monitor instructions to comments
     */
    @Override
    public void logEvent(String string2, Map<String, String> object) {}

    @Override
    protected void restore() {
    }

    @Override
    protected void resume() {
    }

    @Override
    public void setMaxEventCount(String string2, int n2) {
        if (!Utility.validString(string2)) {
            Log.Helper.LOGE(this, "setMaxEventCount called with null or empty eventType.");
            return;
        }
        this.m_maxEventQueueSizeDict.put(string2, n2);
        this.trimEventQueue(string2);
    }

    @Override
    protected void suspend() {
    }
}

