/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.IHttpRequest;
import com.ea.nimble.IHttpResponse;
import com.ea.nimble.NetworkConnectionCallback;

public interface NetworkConnectionHandle {
    public void cancel();

    public NetworkConnectionCallback getCompletionCallback();

    public NetworkConnectionCallback getHeaderCallback();

    public NetworkConnectionCallback getProgressCallback();

    public IHttpRequest getRequest();

    public IHttpResponse getResponse();

    public void setCompletionCallback(NetworkConnectionCallback var1);

    public void setHeaderCallback(NetworkConnectionCallback var1);

    public void setProgressCallback(NetworkConnectionCallback var1);

    public void waitOn();
}

