package com.ea.nimble.identity;

import com.ea.nimble.Global;
import com.ea.nimble.Log;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import java.util.ArrayList;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityGenericLoginResolver.class */
class NimbleIdentityGenericLoginResolver implements INimbleIdentityGenericLoginResolver {
    private INimbleIdentityAuthenticator m_authenticator;
    private String m_authenticatorId;
    private ArrayList<String> m_loggedInAuthenticatorIds;

    public NimbleIdentityGenericLoginResolver(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        if (iNimbleIdentityAuthenticator != null) {
            this.m_authenticator = iNimbleIdentityAuthenticator;
            this.m_authenticatorId = iNimbleIdentityAuthenticator.getAuthenticatorId();
            this.m_loggedInAuthenticatorIds = new ArrayList<>();
            for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2 : NimbleIdentity.getComponent().getLoggedInAuthenticators()) {
                this.m_loggedInAuthenticatorIds.add(iNimbleIdentityAuthenticator2.getAuthenticatorId());
            }
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLoginResolver
    public List<String> getLoggedInAuthenticatorIds() {
        return this.m_loggedInAuthenticatorIds;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLoginResolver
    public String getLoggingInAuthenticatorId() {
        return this.m_authenticatorId;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLoginResolver
    public void highlight() {
        NimbleIdentityImpl.getComponent().highlight(this.m_authenticator);
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLoginResolver
    public void ignore() {
        Log.Helper.LOGI(this, "Game decided to resolve login of %s by ignoring", this.m_authenticatorId);
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLoginResolver
    public void switchAuthenticators(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        NimbleIdentityImpl component = NimbleIdentityImpl.getComponent();
        component.switchAuthenticators(nimbleIdentityAuthenticatorCallback, this.m_authenticator, component.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ANONYMOUS));
    }
}
