package com.ea.nimble.identity;

import com.ea.nimble.Global;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import java.util.ArrayList;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityMigrationLoginResolver.class */
class NimbleIdentityMigrationLoginResolver implements INimbleIdentityMigrationLoginResolver {
    private INimbleIdentityAuthenticator m_authenticator;
    private String m_authenticatorId;
    private ArrayList<String> m_loggedInAuthenticatorIds = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public NimbleIdentityMigrationLoginResolver(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        this.m_authenticator = iNimbleIdentityAuthenticator;
        this.m_authenticatorId = iNimbleIdentityAuthenticator.getAuthenticatorId();
        for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2 : NimbleIdentity.getComponent().getLoggedInAuthenticators()) {
            this.m_loggedInAuthenticatorIds.add(iNimbleIdentityAuthenticator2.getAuthenticatorId());
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityMigrationLoginResolver
    public List<String> getLoggedInAuthenticatorIds() {
        return this.m_loggedInAuthenticatorIds;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityMigrationLoginResolver
    public String getLoggingInAuthenticatorId() {
        return this.m_authenticatorId;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityMigrationLoginResolver
    public void ignore() {
    }

    @Override // com.ea.nimble.identity.INimbleIdentityMigrationLoginResolver
    public void migrate(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        NimbleIdentityImpl component = NimbleIdentityImpl.getComponent();
        component.migrate(nimbleIdentityAuthenticatorCallback, this.m_authenticator, component.getMainAuthenticator());
    }

    @Override // com.ea.nimble.identity.INimbleIdentityMigrationLoginResolver
    public void switchAuthenticators(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        NimbleIdentityImpl component = NimbleIdentityImpl.getComponent();
        component.switchAuthenticators(nimbleIdentityAuthenticatorCallback, this.m_authenticator, component.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ANONYMOUS));
    }
}
