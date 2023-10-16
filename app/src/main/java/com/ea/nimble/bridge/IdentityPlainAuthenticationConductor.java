/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.bridge;

import com.ea.nimble.bridge.BaseNativeCallback;
import com.ea.nimble.identity.INimbleIdentityPlainAuthenticationConductor;

public class IdentityPlainAuthenticationConductor
implements INimbleIdentityPlainAuthenticationConductor {
    private int m_id;

    public IdentityPlainAuthenticationConductor(int n2) {
        this.m_id = n2;
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }

    @Override
    public void handleLogin() {
        BaseNativeCallback.nativeCallback(this.m_id, new Object[0]);
    }

    @Override
    public void handleLogout() {
        BaseNativeCallback.nativeCallback(this.m_id, new Object[]{null});
    }
}

