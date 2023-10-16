/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Map;

public interface IHttpResponse {
    public InputStream getDataStream();

    public long getDownloadedContentLength();

    public Exception getError();

    public long getExpectedContentLength();

    public Map<String, String> getHeaders();

    public Date getLastModified();

    public int getStatusCode();

    public URL getUrl();

    public boolean isCompleted();
}

