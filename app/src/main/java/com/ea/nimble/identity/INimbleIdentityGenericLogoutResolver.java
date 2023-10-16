package com.ea.nimble.identity;

import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityGenericLogoutResolver.class */
public interface INimbleIdentityGenericLogoutResolver {
    String getLoggingOutAuthenticatorId();

    List<String> getStillLoggedInAuthenticatorIds();

    void resolve(String str);
}
