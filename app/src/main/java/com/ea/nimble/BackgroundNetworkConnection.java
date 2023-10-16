/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

public class BackgroundNetworkConnection
extends NetworkConnection {
    public BackgroundNetworkConnection(NetworkImpl networkImpl, HttpRequest httpRequest, IOperationalTelemetryDispatch iOperationalTelemetryDispatch) {
        super(networkImpl, httpRequest, iOperationalTelemetryDispatch);
    }

    @Override
    public void cancelForAppSuspend() {
    }
}

