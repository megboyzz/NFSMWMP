package com.ea.nimble.identity;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityMigrationAuthenticationConductor.class */
public interface INimbleIdentityMigrationAuthenticationConductor extends INimbleIdentityAuthenticationConductor {
    void handleLogin(INimbleIdentityMigrationLoginResolver iNimbleIdentityMigrationLoginResolver);

    void handleLogout();

    void handlePendingMigration(INimbleIdentityPendingMigrationResolver iNimbleIdentityPendingMigrationResolver);
}
