/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.IHttpRequest;
import java.util.Map;

public interface ISynergyRequest {
    public String getApi();

    public String getBaseUrl();

    public IHttpRequest getHttpRequest();

    public Map<String, Object> getJsonData();

    public Map<String, String> getUrlParameters();
}

