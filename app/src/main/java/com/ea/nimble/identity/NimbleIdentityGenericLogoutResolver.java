package com.ea.nimble.identity;

import com.ea.nimble.Utility;
import java.util.ArrayList;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityGenericLogoutResolver.class */
public class NimbleIdentityGenericLogoutResolver implements INimbleIdentityGenericLogoutResolver {
    private String m_authenticatorId;
    private ArrayList<String> m_stillLoggedInAuthenticatorIds = new ArrayList<>();

    public NimbleIdentityGenericLogoutResolver(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        this.m_authenticatorId = iNimbleIdentityAuthenticator.getAuthenticatorId();
        for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2 : NimbleIdentity.getComponent().getLoggedInAuthenticators()) {
            this.m_stillLoggedInAuthenticatorIds.add(iNimbleIdentityAuthenticator2.getAuthenticatorId());
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLogoutResolver
    public String getLoggingOutAuthenticatorId() {
        return this.m_authenticatorId;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLogoutResolver
    public List<String> getStillLoggedInAuthenticatorIds() {
        return this.m_stillLoggedInAuthenticatorIds;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityGenericLogoutResolver
    public void resolve(String str) {
        INimbleIdentityAuthenticator authenticatorById;
        if (Utility.validString(str) && (authenticatorById = NimbleIdentityImpl.getComponent().getAuthenticatorById(str)) != null) {
            NimbleIdentityImpl.getComponent().resolveLogout(authenticatorById);
        }
    }
}
