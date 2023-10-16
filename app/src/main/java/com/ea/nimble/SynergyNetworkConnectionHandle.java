/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.ISynergyRequest;
import com.ea.nimble.ISynergyResponse;
import com.ea.nimble.SynergyNetworkConnectionCallback;

public interface SynergyNetworkConnectionHandle {
    public void cancel();

    public SynergyNetworkConnectionCallback getCompletionCallback();

    public SynergyNetworkConnectionCallback getHeaderCallback();

    public SynergyNetworkConnectionCallback getProgressCallback();

    public ISynergyRequest getRequest();

    public ISynergyResponse getResponse();

    public void setCompletionCallback(SynergyNetworkConnectionCallback var1);

    public void setHeaderCallback(SynergyNetworkConnectionCallback var1);

    public void setProgressCallback(SynergyNetworkConnectionCallback var1);

    public void waitOn();
}

