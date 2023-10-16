/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.BaseCore;
import com.ea.nimble.HttpResponse;
import com.ea.nimble.IOperationalTelemetryDispatch;
import com.ea.nimble.ISynergyRequest;
import com.ea.nimble.ISynergyResponse;
import com.ea.nimble.Log;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.OperationalTelemetryDispatch;
import com.ea.nimble.OperationalTelemetryEvent;
import com.ea.nimble.SynergyNetworkConnectionCallback;
import com.ea.nimble.SynergyNetworkConnectionHandle;
import com.ea.nimble.SynergyRequest;
import com.ea.nimble.SynergyResponse;
import java.util.List;
import java.util.Map;

class SynergyNetworkConnection
implements SynergyNetworkConnectionHandle {
    private SynergyNetworkConnectionCallback m_completionCallback;
    private SynergyNetworkConnectionCallback m_headerCallback;
    private NetworkConnectionHandle m_networkHandle = null;
    private SynergyOperationalTelemetryDispatch m_otDispatch;
    private SynergyNetworkConnectionCallback m_progressCallback;
    private SynergyRequest m_request;
    private SynergyResponse m_response;

    public SynergyNetworkConnection(SynergyRequest synergyRequest, SynergyNetworkConnectionCallback synergyNetworkConnectionCallback) {
        this.m_request = synergyRequest;
        this.m_response = new SynergyResponse();
        this.m_otDispatch = new SynergyOperationalTelemetryDispatch();
        this.m_headerCallback = null;
        this.m_progressCallback = null;
        this.m_completionCallback = synergyNetworkConnectionCallback;
    }

    static /* synthetic */ NetworkConnectionHandle access$102(SynergyNetworkConnection synergyNetworkConnection, NetworkConnectionHandle networkConnectionHandle) {
        synergyNetworkConnection.m_networkHandle = networkConnectionHandle;
        return networkConnectionHandle;
    }

    private void parseDataFromNetworkHandle() {
        if (this.m_networkHandle == null) return;
        this.m_response.httpResponse = this.m_networkHandle.getResponse();
        this.m_response.parseData();
    }

    private void updateNetworkHeaderHandler() {
        if (this.m_headerCallback == null) {
            this.m_networkHandle.setHeaderCallback(null);
            return;
        }
        this.m_networkHandle.setHeaderCallback(new NetworkConnectionCallback(){

            @Override
            public void callback(NetworkConnectionHandle networkConnectionHandle) {
                SynergyNetworkConnection.this.m_headerCallback.callback(SynergyNetworkConnection.this);
            }
        });
    }

    private void updateNetworkProgressHandler() {
        if (this.m_progressCallback == null) {
            this.m_networkHandle.setProgressCallback(null);
            return;
        }
        this.m_networkHandle.setProgressCallback(new NetworkConnectionCallback(){

            @Override
            public void callback(NetworkConnectionHandle networkConnectionHandle) {
                SynergyNetworkConnection.this.m_progressCallback.callback(SynergyNetworkConnection.this);
            }
        });
    }

    @Override
    public void cancel() {
        if (this.m_networkHandle == null) return;
        this.m_networkHandle.cancel();
    }

    public void errorPriorToSend(Exception exception) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.error = exception;
        httpResponse.isCompleted = true;
        this.m_response.httpResponse = httpResponse;
        if (this.m_completionCallback == null) return;
        this.m_completionCallback.callback(this);
    }

    @Override
    public SynergyNetworkConnectionCallback getCompletionCallback() {
        return this.m_completionCallback;
    }

    @Override
    public SynergyNetworkConnectionCallback getHeaderCallback() {
        return this.m_headerCallback;
    }

    public NetworkConnectionHandle getNetworkConnectionHandle() {
        return this.m_networkHandle;
    }

    @Override
    public SynergyNetworkConnectionCallback getProgressCallback() {
        return this.m_progressCallback;
    }

    @Override
    public ISynergyRequest getRequest() {
        return this.m_request;
    }

    @Override
    public ISynergyResponse getResponse() {
        return this.m_response;
    }

    void send() {
        try {
            this.m_request.build();
            this.m_networkHandle = Network.getComponent().sendRequest(this.m_request.httpRequest, new NetworkConnectionCallback(){

                @Override
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    if (SynergyNetworkConnection.this.m_networkHandle == null) {
                        SynergyNetworkConnection.access$102(SynergyNetworkConnection.this, networkConnectionHandle);
                    }
                    SynergyNetworkConnection.this.parseDataFromNetworkHandle();
                    networkConnectionHandle.setHeaderCallback(null);
                    networkConnectionHandle.setProgressCallback(null);
                    networkConnectionHandle.setCompletionCallback(null);
                    if (SynergyNetworkConnection.this.m_completionCallback == null) return;
                    SynergyNetworkConnection.this.m_completionCallback.callback(SynergyNetworkConnection.this);
                }
            }, this.m_otDispatch);
            this.m_response.httpResponse = this.m_networkHandle.getResponse();
            this.updateNetworkHeaderHandler();
            this.updateNetworkProgressHandler();
            return;
        }
        catch (Exception exception) {
            this.errorPriorToSend(exception);
            return;
        }
    }

    @Override
    public void setCompletionCallback(SynergyNetworkConnectionCallback synergyNetworkConnectionCallback) {
        this.m_completionCallback = synergyNetworkConnectionCallback;
    }

    @Override
    public void setHeaderCallback(SynergyNetworkConnectionCallback synergyNetworkConnectionCallback) {
        this.m_headerCallback = synergyNetworkConnectionCallback;
        if (this.m_networkHandle == null) return;
        this.updateNetworkHeaderHandler();
    }

    public void setNetworkConnectionHandle(NetworkConnectionHandle networkConnectionHandle) {
        this.m_networkHandle = networkConnectionHandle;
    }

    @Override
    public void setProgressCallback(SynergyNetworkConnectionCallback synergyNetworkConnectionCallback) {
        this.m_progressCallback = synergyNetworkConnectionCallback;
        if (this.m_networkHandle == null) return;
        this.updateNetworkProgressHandler();
    }

    public void start() {
        this.m_request.prepare(this);
    }

    @Override
    public void waitOn() {
        if (this.m_networkHandle == null) return;
        this.m_networkHandle.waitOn();
    }

    private class SynergyOperationalTelemetryDispatch
    implements IOperationalTelemetryDispatch {
        private SynergyOperationalTelemetryDispatch() {
        }

        @Override
        public List<OperationalTelemetryEvent> getEvents(String string2) {
            if (!BaseCore.getInstance().isActive()) {
                Log.Helper.LOGV(this, "BaseCore not active for operational telemetry logging.", new Object[0]);
                return null;
            }
            IOperationalTelemetryDispatch iOperationalTelemetryDispatch = OperationalTelemetryDispatch.getComponent();
            if (iOperationalTelemetryDispatch == null) return null;
            return iOperationalTelemetryDispatch.getEvents(string2);
        }

        @Override
        public int getMaxEventCount(String string2) {
            if (!BaseCore.getInstance().isActive()) {
                Log.Helper.LOGV(this, "BaseCore not active for operational telemetry logging.", new Object[0]);
                return -1;
            }
            IOperationalTelemetryDispatch iOperationalTelemetryDispatch = OperationalTelemetryDispatch.getComponent();
            if (iOperationalTelemetryDispatch == null) return -1;
            return iOperationalTelemetryDispatch.getMaxEventCount(string2);
        }

        @Override
        public void logEvent(String string2, Map<String, String> map) {
            if (!BaseCore.getInstance().isActive()) {
                Log.Helper.LOGV(this, "BaseCore not active for operational telemetry logging.", new Object[0]);
                return;
            }
            IOperationalTelemetryDispatch iOperationalTelemetryDispatch = OperationalTelemetryDispatch.getComponent();
            if (iOperationalTelemetryDispatch == null) return;
            SynergyNetworkConnection.this.parseDataFromNetworkHandle();
            Map<String, Object> map2 = SynergyNetworkConnection.this.m_response.getJsonData();
            if (map2 != null && map2.containsKey("resultCode")) {
                map.put("SYNERGY_RESULT_CODE", ((Integer)map2.get("resultCode")).toString());
            }
            iOperationalTelemetryDispatch.logEvent(string2, map);
        }

        @Override
        public void setMaxEventCount(String string2, int n2) {
            if (!BaseCore.getInstance().isActive()) {
                Log.Helper.LOGV(this, "BaseCore not active for operational telemetry logging.", new Object[0]);
                return;
            }
            IOperationalTelemetryDispatch iOperationalTelemetryDispatch = OperationalTelemetryDispatch.getComponent();
            if (iOperationalTelemetryDispatch == null) return;
            iOperationalTelemetryDispatch.setMaxEventCount(string2, n2);
        }
    }
}

