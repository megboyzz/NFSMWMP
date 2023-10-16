package com.ea.nimble.identity;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityAuthenticationConductorHandler.class */
interface NimbleIdentityAuthenticationConductorHandler {
    void handleLogin(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, boolean z);

    void handleLogout(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator);
}
