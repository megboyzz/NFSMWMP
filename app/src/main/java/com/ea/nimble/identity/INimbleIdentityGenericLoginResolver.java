package com.ea.nimble.identity;

import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityGenericLoginResolver.class */
public interface INimbleIdentityGenericLoginResolver {
    List<String> getLoggedInAuthenticatorIds();

    String getLoggingInAuthenticatorId();

    void highlight();

    void ignore();

    void switchAuthenticators(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);
}
