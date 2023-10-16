/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.HttpRequest;
import com.ea.nimble.IOperationalTelemetryDispatch;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import java.net.URL;
import java.util.HashMap;

public interface INetwork {
    public void forceRedetectNetworkStatus();

    public Network.Status getStatus();

    public boolean isNetworkWifi();

    public NetworkConnectionHandle sendDeleteRequest(URL var1, HashMap<String, String> var2, NetworkConnectionCallback var3);

    public NetworkConnectionHandle sendGetRequest(URL var1, HashMap<String, String> var2, NetworkConnectionCallback var3);

    public NetworkConnectionHandle sendPostRequest(URL var1, HashMap<String, String> var2, byte[] var3, NetworkConnectionCallback var4);

    public NetworkConnectionHandle sendRequest(HttpRequest var1, NetworkConnectionCallback var2);

    public NetworkConnectionHandle sendRequest(HttpRequest var1, NetworkConnectionCallback var2, IOperationalTelemetryDispatch var3);
}

