package com.ea.nimble.identity;

import com.ea.nimble.Global;
import com.ea.nimble.Log;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPlainAuthenticationConductorHandler.class */
public class NimbleIdentityPlainAuthenticationConductorHandler implements NimbleIdentityAuthenticationConductorHandler {
    private INimbleIdentityPlainAuthenticationConductor m_conductor;

    public NimbleIdentityPlainAuthenticationConductorHandler(INimbleIdentityAuthenticationConductor iNimbleIdentityAuthenticationConductor) {
        this.m_conductor = (INimbleIdentityPlainAuthenticationConductor) iNimbleIdentityAuthenticationConductor;
    }

    @Override // com.ea.nimble.identity.NimbleIdentityAuthenticationConductorHandler
    public void handleLogin(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, boolean z) {
        if (iNimbleIdentityAuthenticator.getAuthenticatorId() == Global.NIMBLE_AUTHENTICATOR_ANONYMOUS && NimbleIdentityImpl.getComponent().getMainAuthenticator() == null) {
            NimbleIdentityImpl.getComponent().setMainAuthenticator(iNimbleIdentityAuthenticator);
        }
        Log.Helper.LOGD(this, "New authenticator %s being LogIn handled by Plain authenticator.", iNimbleIdentityAuthenticator.getAuthenticatorId());
    }

    @Override // com.ea.nimble.identity.NimbleIdentityAuthenticationConductorHandler
    public void handleLogout(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        Log.Helper.LOGD(this, "New authenticator %s being LogOut handled by Plain authenticator.", iNimbleIdentityAuthenticator.getAuthenticatorId());
    }
}
