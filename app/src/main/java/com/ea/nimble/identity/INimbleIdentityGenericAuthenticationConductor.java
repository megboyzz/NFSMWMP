package com.ea.nimble.identity;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityGenericAuthenticationConductor.class */
public interface INimbleIdentityGenericAuthenticationConductor extends INimbleIdentityAuthenticationConductor {
    void handleLogin(INimbleIdentityGenericLoginResolver iNimbleIdentityGenericLoginResolver);

    void handleLogout(INimbleIdentityGenericLogoutResolver iNimbleIdentityGenericLogoutResolver);
}
