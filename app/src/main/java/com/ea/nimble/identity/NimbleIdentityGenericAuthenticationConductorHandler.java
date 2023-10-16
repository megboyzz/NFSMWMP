package com.ea.nimble.identity;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityGenericAuthenticationConductorHandler.class */
class NimbleIdentityGenericAuthenticationConductorHandler implements NimbleIdentityAuthenticationConductorHandler {
    private NimbleIdentityGenericLoginResolver loginResolver;
    private NimbleIdentityGenericLogoutResolver logoutResolver;
    private INimbleIdentityGenericAuthenticationConductor m_conductor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NimbleIdentityGenericAuthenticationConductorHandler(INimbleIdentityAuthenticationConductor iNimbleIdentityAuthenticationConductor) {
        this.m_conductor = (INimbleIdentityGenericAuthenticationConductor) iNimbleIdentityAuthenticationConductor;
    }

    @Override // com.ea.nimble.identity.NimbleIdentityAuthenticationConductorHandler
    public void handleLogin(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, boolean z) {
        this.loginResolver = new NimbleIdentityGenericLoginResolver(iNimbleIdentityAuthenticator);
        this.m_conductor.handleLogin(this.loginResolver);
    }

    @Override // com.ea.nimble.identity.NimbleIdentityAuthenticationConductorHandler
    public void handleLogout(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        this.logoutResolver = new NimbleIdentityGenericLogoutResolver(iNimbleIdentityAuthenticator);
        this.m_conductor.handleLogout(this.logoutResolver);
    }
}
