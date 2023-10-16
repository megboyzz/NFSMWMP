/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SynergyRequest
implements ISynergyRequest {
    public String api;
    public String baseUrl;
    public HttpRequest httpRequest;
    public Map<String, Object> jsonData;
    private SynergyNetworkConnection m_connection;
    public SynergyRequestPreparingCallback prepareRequestCallback;
    public Map<String, String> urlParameters;

    public SynergyRequest(String string2, IHttpRequest.Method method, SynergyRequestPreparingCallback synergyRequestPreparingCallback) {
        this.api = string2;
        this.httpRequest = new HttpRequest();
        this.prepareRequestCallback = synergyRequestPreparingCallback;
        this.urlParameters = null;
        this.jsonData = null;
        this.httpRequest.method = method;
        this.httpRequest.headers.put("Content-Type", "application/json");
        this.httpRequest.headers.put("SDK-VERSION", "1.23.14.1217");
        this.httpRequest.headers.put("SDK-TYPE", "Nimble");
        this.httpRequest.headers.put("EAM-USER-ID", SynergyIdManager.getComponent().getSynergyId());
        this.httpRequest.headers.put("EA-SELL-ID", SynergyEnvironment.getComponent().getSellId());
        this.httpRequest.headers.put("EAM-SESSION", ((SynergyNetworkImpl)SynergyNetwork.getComponent()).getSessionId());
    }

    void build() throws Error {
        if (!Utility.validString(this.baseUrl) || !Utility.validString(this.api)) {
            throw new Error(Error.Code.INVALID_ARGUMENT, String.format("Invalid synergy request parameter (%s, %s) to build http request url", this.baseUrl, this.api));
        }
        IApplicationEnvironment object = ApplicationEnvironment.getComponent();
        HashMap<String, String> object2 = new HashMap<String, String>();
        object2.put("appVer", object.getApplicationVersion());
        object2.put("appLang", object.getShortApplicationLanguageCode());
        object2.put("localization", object.getApplicationLanguageCode());
        object2.put("deviceLanguage", Locale.getDefault().getLanguage());
        object2.put("deviceLocale", Locale.getDefault().toString());
        String eaHardwareId = SynergyEnvironment.getComponent().getEAHardwareId();
        if (Utility.validString(eaHardwareId)) {
            object2.put("hwId", eaHardwareId);
        }
        if (this.urlParameters != null) {
            object2.putAll(this.urlParameters);
        }
        this.httpRequest.url = Network.generateURL(this.baseUrl + this.api, object2);
        if (this.httpRequest.method != IHttpRequest.Method.POST) {
            if (this.httpRequest.method != IHttpRequest.Method.PUT) return;
        }
        if (this.jsonData == null) return;
        if (this.jsonData.size() <= 0) return;
        String s = Utility.convertObjectToJSONString(this.jsonData);
        this.httpRequest.data = new ByteArrayOutputStream();
        try {
            this.httpRequest.data.write(s.getBytes());
        }
        catch (IOException iOException) {
            throw new Error(Error.Code.INVALID_ARGUMENT, "Error converting jsonData in SynergyRequest to a data stream", iOException);
        }
    }

    @Override
    public String getApi() {
        return this.api;
    }

    @Override
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public HttpRequest getHttpRequest() {
        return this.httpRequest;
    }

    @Override
    public Map<String, Object> getJsonData() {
        return this.jsonData;
    }

    public IHttpRequest.Method getMethod() {
        return this.httpRequest.getMethod();
    }

    @Override
    public Map<String, String> getUrlParameters() {
        return this.urlParameters;
    }

    void prepare(SynergyNetworkConnection synergyNetworkConnection) {
        this.m_connection = synergyNetworkConnection;
        if (this.prepareRequestCallback != null) {
            this.prepareRequestCallback.prepareRequest(this);
            return;
        }
        this.send();
    }

    public void send() {
        this.m_connection.send();
    }

    public void setMethod(IHttpRequest.Method method) {
        this.httpRequest.method = method;
    }

    public static interface SynergyRequestPreparingCallback {
        public void prepareRequest(SynergyRequest var1);
    }
}

