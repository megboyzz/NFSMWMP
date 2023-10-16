package com.ea.nimble.identity;

import com.ea.nimble.identity.INimbleIdentityAuthenticator;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPendingMigrationResolver.class */
public class NimbleIdentityPendingMigrationResolver implements INimbleIdentityPendingMigrationResolver {
    private String m_GUID;
    private String m_currentAuthenticatorId;
    private String m_currentAuthenticatorPid;
    private String m_newAuthenticatorId;

    /* JADX INFO: Access modifiers changed from: package-private */
    public NimbleIdentityPendingMigrationResolver(String str, String str2, String str3, String str4, String str5) {
        this.m_GUID = str;
        this.m_newAuthenticatorId = str2;
        this.m_currentAuthenticatorId = str4;
        this.m_currentAuthenticatorPid = str5;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityPendingMigrationResolver
    public String getMigrationSourceAuthenticatorId() {
        return this.m_currentAuthenticatorId;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityPendingMigrationResolver
    public String getMigrationTargetAuthenticatorId() {
        return this.m_newAuthenticatorId;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityPendingMigrationResolver
    public void resume(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        NimbleIdentityImpl component = NimbleIdentityImpl.getComponent();
        INimbleIdentityAuthenticator authenticatorById = component.getAuthenticatorById(this.m_currentAuthenticatorId);
        INimbleIdentityAuthenticator authenticatorById2 = component.getAuthenticatorById(this.m_newAuthenticatorId);
        if (!authenticatorById.getPidInfo().getPid().equals(this.m_currentAuthenticatorPid)) {
            component.completeMigration(authenticatorById, authenticatorById2, nimbleIdentityAuthenticatorCallback);
        } else {
            component.handlePendingMigration(nimbleIdentityAuthenticatorCallback, this.m_GUID, authenticatorById2, authenticatorById);
        }
    }
}
