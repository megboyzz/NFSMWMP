/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.os.Bundle
 */
package com.ea.nimble.bridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.SynergyNetworkConnectionCallback;
import com.ea.nimble.SynergyNetworkConnectionHandle;
import com.ea.nimble.SynergyRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseNativeCallback
extends BroadcastReceiver
implements NetworkConnectionCallback,
SynergyNetworkConnectionCallback,
SynergyRequest.SynergyRequestPreparingCallback {
    private int m_id;

    public BaseNativeCallback(int n2) {
        this.m_id = n2;
    }

    public static native void nativeCallback(int var0, Object ... var1);

    public static native void nativeFinalize(int var0);

    @Override
    public void callback(NetworkConnectionHandle networkConnectionHandle) {
        BaseNativeCallback.nativeCallback(this.m_id, networkConnectionHandle);
    }

    @Override
    public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
        BaseNativeCallback.nativeCallback(this.m_id, synergyNetworkConnectionHandle);
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }

    public void onReceive(Context object, Intent intent) {
        Map<String, Object> hashMap = new HashMap<>();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String string2 : bundle.keySet()) {
                hashMap.put(string2, bundle.get(string2));
            }
        }
        BaseNativeCallback.nativeCallback(this.m_id, intent.getAction(), hashMap);
    }

    @Override
    public void prepareRequest(SynergyRequest synergyRequest) {
        BaseNativeCallback.nativeCallback(this.m_id, synergyRequest);
    }
}

