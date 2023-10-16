/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 */
package com.ea.nimble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class SynergyNetworkImpl
extends Component
implements ISynergyNetwork {
    private ArrayList<SynergyNetworkConnection> m_pendingRequests = null;
    private String m_sessionId;
    private BroadcastReceiver m_synergyEnvironmentNotifyReceiver = null;

    SynergyNetworkImpl() {
    }

    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase(Locale.US);
    }

    public static ISynergyNetwork getComponent() {
        return (ISynergyNetwork)(Base.getComponent("com.ea.nimble.synergynetwork"));
    }

    private SynergyNetworkConnectionHandle sendSynergyRequest(SynergyRequest object, SynergyNetworkConnectionCallback synergyNetworkConnectionCallback) {
        SynergyNetworkConnection synergyNetworkConnection = new SynergyNetworkConnection(object, synergyNetworkConnectionCallback);
        synergyNetworkConnection.start();
        return synergyNetworkConnection;
    }

    @Override
    public void cleanup() {
        if (this.m_synergyEnvironmentNotifyReceiver != null) {
            Utility.unregisterReceiver(this.m_synergyEnvironmentNotifyReceiver);
            this.m_synergyEnvironmentNotifyReceiver = null;
        }
        this.m_pendingRequests.clear();
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.synergynetwork";
    }

    String getSessionId() {
        return this.m_sessionId;
    }

    @Override
    public void restore() {
        if (SynergyEnvironment.getComponent().isDataAvailable()) return;
        this.m_synergyEnvironmentNotifyReceiver = new BroadcastReceiver(){

            public void onReceive(Context object, Intent intent) {

                for (SynergyNetworkConnection m_pendingRequest : SynergyNetworkImpl.this.m_pendingRequests)
                    if (intent.getStringExtra("result").equals("0"))
                        m_pendingRequest.errorPriorToSend(new Error(Error.Code.SYNERGY_ENVIRONMENT_UPDATE_FAILURE, "Failed to retrieve Environment data from Synergy"));
                    else
                        m_pendingRequest.start();

                SynergyNetworkImpl.this.m_pendingRequests.clear();
            }
        };
        Utility.registerReceiver("nimble.environment.notification.startup_requests_finished", this.m_synergyEnvironmentNotifyReceiver);
    }

    @Override
    protected void resume() {
        this.m_sessionId = this.generateSessionId();
    }

    @Override
    public SynergyNetworkConnectionHandle sendGetRequest(String string2, String object, Map<String, String> map, SynergyNetworkConnectionCallback synergyNetworkConnectionCallback) {
        SynergyRequest synergyRequest = new SynergyRequest(object, IHttpRequest.Method.GET, null);
        synergyRequest.baseUrl = string2;
        synergyRequest.urlParameters = map;
        return this.sendSynergyRequest(synergyRequest, synergyNetworkConnectionCallback);
    }

    @Override
    public SynergyNetworkConnectionHandle sendPostRequest(String string2, String object, Map<String, String> map, Map<String, Object> map2, SynergyNetworkConnectionCallback synergyNetworkConnectionCallback) {
        SynergyRequest synergyRequest = new SynergyRequest(object, IHttpRequest.Method.POST, null);
        synergyRequest.baseUrl = string2;
        synergyRequest.urlParameters = map;
        synergyRequest.jsonData = map2;
        return this.sendSynergyRequest(synergyRequest, synergyNetworkConnectionCallback);
    }

    @Override
    public SynergyNetworkConnectionHandle sendPostRequest(String string2, String object, Map<String, String> map, Map<String, Object> map2, SynergyNetworkConnectionCallback synergyNetworkConnectionCallback, Map<String, String> map3) {
        SynergyRequest synergyRequest = new SynergyRequest((String) object, IHttpRequest.Method.POST, null);
        synergyRequest.baseUrl = string2;
        synergyRequest.urlParameters = map;
        synergyRequest.jsonData = map2;
        if (map3 == null) return this.sendSynergyRequest(synergyRequest, synergyNetworkConnectionCallback);
        synergyRequest.httpRequest.headers.putAll(map3);
        return this.sendSynergyRequest(synergyRequest, synergyNetworkConnectionCallback);
    }

    @Override
    public void sendRequest(SynergyRequest object, SynergyNetworkConnectionCallback object2) {
        ISynergyEnvironment iSynergyEnvironment = SynergyEnvironment.getComponent();
        if (iSynergyEnvironment.isDataAvailable()) {
            this.sendSynergyRequest(object, object2);
            return;
        }
        SynergyNetworkConnection synergyNetworkConnection = new SynergyNetworkConnection(object, object2);
        if (iSynergyEnvironment.isUpdateInProgress()) {
            this.m_pendingRequests.add(synergyNetworkConnection);
            return;
        }
        Error error = iSynergyEnvironment.checkAndInitiateSynergyEnvironmentUpdate();
        if (error == null) {
            this.m_pendingRequests.add(synergyNetworkConnection);
            return;
        }
        synergyNetworkConnection.errorPriorToSend((Exception)object2);
    }

    @Override
    public void setup() {
        this.m_pendingRequests = new ArrayList();
        this.m_sessionId = this.generateSessionId();
    }
}

