/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.SynergyNetworkConnectionCallback;
import com.ea.nimble.SynergyNetworkConnectionHandle;
import com.ea.nimble.SynergyRequest;
import java.util.Map;

public interface ISynergyNetwork {
    public SynergyNetworkConnectionHandle sendGetRequest(String var1, String var2, Map<String, String> var3, SynergyNetworkConnectionCallback var4);

    public SynergyNetworkConnectionHandle sendPostRequest(String var1, String var2, Map<String, String> var3, Map<String, Object> var4, SynergyNetworkConnectionCallback var5);

    public SynergyNetworkConnectionHandle sendPostRequest(String var1, String var2, Map<String, String> var3, Map<String, Object> var4, SynergyNetworkConnectionCallback var5, Map<String, String> var6);

    public void sendRequest(SynergyRequest var1, SynergyNetworkConnectionCallback var2);
}

