package com.ea.nimble.identity;

import java.io.Serializable;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityMigrationObject.class */
class NimbleIdentityMigrationObject implements Serializable {
    private static final long serialVersionUID = 1;
    String m_currentAuthenticatorId;
    String m_currentAuthenticatorPid;
    String m_migrationGUID;
    String m_newAuthenticatorId;
    String m_newAuthenticatorPid;

    NimbleIdentityMigrationObject() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NimbleIdentityMigrationObject(String str, String str2, String str3, String str4, String str5) {
        this.m_migrationGUID = str;
        this.m_newAuthenticatorId = str2;
        this.m_newAuthenticatorPid = str3;
        this.m_currentAuthenticatorId = str4;
        this.m_currentAuthenticatorPid = str5;
    }
}
