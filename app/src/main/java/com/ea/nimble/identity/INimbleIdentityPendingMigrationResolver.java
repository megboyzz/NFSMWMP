package com.ea.nimble.identity;

import com.ea.nimble.identity.INimbleIdentityAuthenticator;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityPendingMigrationResolver.class */
public interface INimbleIdentityPendingMigrationResolver {
    String getMigrationSourceAuthenticatorId();

    String getMigrationTargetAuthenticatorId();

    void resume(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);
}
