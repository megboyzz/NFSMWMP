/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.IHttpRequest;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.EnumSet;
import java.util.HashMap;

public class HttpRequest
implements IHttpRequest {
    private static int DEFAULT_NETWORK_TIMEOUT = 30;
    public ByteArrayOutputStream data;
    public HashMap<String, String> headers;
    public IHttpRequest.Method method = IHttpRequest.Method.GET;
    public EnumSet<IHttpRequest.OverwritePolicy> overwritePolicy;
    public boolean runInBackground;
    public String targetFilePath;
    public double timeout;
    public URL url = null;

    public HttpRequest() {
        this.data = new ByteArrayOutputStream();
        this.headers = new HashMap();
        this.overwritePolicy = IHttpRequest.OverwritePolicy.SMART;
        this.timeout = DEFAULT_NETWORK_TIMEOUT;
    }

    public HttpRequest(URL uRL) {
        this();
        this.url = uRL;
    }

    @Override
    public byte[] getData() {
        return this.data.toByteArray();
    }

    public HashMap<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public IHttpRequest.Method getMethod() {
        return this.method;
    }

    @Override
    public EnumSet<IHttpRequest.OverwritePolicy> getOverwritePolicy() {
        return this.overwritePolicy;
    }

    @Override
    public boolean getRunInBackground() {
        return this.runInBackground;
    }

    @Override
    public String getTargetFilePath() {
        return this.targetFilePath;
    }

    @Override
    public double getTimeout() {
        return this.timeout;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }
}

