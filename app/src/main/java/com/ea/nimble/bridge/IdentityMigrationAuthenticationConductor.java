/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.bridge;

import com.ea.nimble.bridge.BaseNativeCallback;
import com.ea.nimble.identity.INimbleIdentityMigrationAuthenticationConductor;
import com.ea.nimble.identity.INimbleIdentityMigrationLoginResolver;
import com.ea.nimble.identity.INimbleIdentityPendingMigrationResolver;

public class IdentityMigrationAuthenticationConductor
implements INimbleIdentityMigrationAuthenticationConductor {
    private int m_id;

    public IdentityMigrationAuthenticationConductor(int n2) {
        this.m_id = n2;
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }

    @Override
    public void handleLogin(INimbleIdentityMigrationLoginResolver iNimbleIdentityMigrationLoginResolver) {
        BaseNativeCallback.nativeCallback(this.m_id, iNimbleIdentityMigrationLoginResolver);
    }

    @Override
    public void handleLogout() {
        BaseNativeCallback.nativeCallback(this.m_id, new Object[0]);
    }

    @Override
    public void handlePendingMigration(INimbleIdentityPendingMigrationResolver iNimbleIdentityPendingMigrationResolver) {
        BaseNativeCallback.nativeCallback(this.m_id, iNimbleIdentityPendingMigrationResolver, null);
    }
}

