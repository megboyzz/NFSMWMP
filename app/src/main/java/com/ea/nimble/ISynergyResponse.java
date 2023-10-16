/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.IHttpResponse;
import java.util.Map;

public interface ISynergyResponse {
    public Exception getError();

    public IHttpResponse getHttpResponse();

    public Map<String, Object> getJsonData();

    public boolean isCompleted();
}

