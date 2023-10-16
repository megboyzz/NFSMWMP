package com.ea.nimble.identity;

import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityMigrationLogoutResolver.class */
public interface INimbleIdentityMigrationLogoutResolver {
    String getLoggingOutAuthenticatorId();

    List<String> getStillLoggedInAuthenticatorIds();

    void resolve(String str);
}
