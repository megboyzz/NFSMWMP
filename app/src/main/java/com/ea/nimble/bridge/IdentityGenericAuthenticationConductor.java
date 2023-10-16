/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.bridge;

import com.ea.nimble.bridge.BaseNativeCallback;
import com.ea.nimble.identity.INimbleIdentityGenericAuthenticationConductor;
import com.ea.nimble.identity.INimbleIdentityGenericLoginResolver;
import com.ea.nimble.identity.INimbleIdentityGenericLogoutResolver;

public class IdentityGenericAuthenticationConductor
implements INimbleIdentityGenericAuthenticationConductor {
    private int m_id;

    public IdentityGenericAuthenticationConductor(int n2) {
        this.m_id = n2;
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }

    @Override
    public void handleLogin(INimbleIdentityGenericLoginResolver iNimbleIdentityGenericLoginResolver) {
        BaseNativeCallback.nativeCallback(this.m_id, iNimbleIdentityGenericLoginResolver);
    }

    @Override
    public void handleLogout(INimbleIdentityGenericLogoutResolver iNimbleIdentityGenericLogoutResolver) {
        BaseNativeCallback.nativeCallback(this.m_id, iNimbleIdentityGenericLogoutResolver, null);
    }
}

