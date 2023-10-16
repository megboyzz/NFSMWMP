package com.ea.nimble.identity;

import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityMigrationLoginResolver.class */
public interface INimbleIdentityMigrationLoginResolver {
    List<String> getLoggedInAuthenticatorIds();

    String getLoggingInAuthenticatorId();

    void ignore();

    void migrate(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);

    void switchAuthenticators(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);
}
