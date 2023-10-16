/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.ByteBufferIOStream;
import com.ea.nimble.IHttpResponse;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse
implements IHttpResponse {
    public ByteBufferIOStream data;
    public long downloadedContentLength = 0L;
    public Exception error;
    public long expectedContentLength = 0L;
    public HashMap<String, String> headers = new HashMap();
    public boolean isCompleted = false;
    public long lastModified = -1L;
    public int statusCode = 0;
    public URL url = null;

    public HttpResponse() {
        this.data = new ByteBufferIOStream();
    }

    @Override
    public InputStream getDataStream() {
        return this.data.getInputStream();
    }

    @Override
    public long getDownloadedContentLength() {
        return this.downloadedContentLength;
    }

    @Override
    public Exception getError() {
        return this.error;
    }

    @Override
    public long getExpectedContentLength() {
        return this.expectedContentLength;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Override
    public Date getLastModified() {
        if (this.lastModified == 0L) {
            return new Date();
        }
        if (this.lastModified <= 0L) return null;
        return new Date(this.lastModified);
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public boolean isCompleted() {
        return this.isCompleted;
    }
}

