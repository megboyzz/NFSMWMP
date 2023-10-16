package com.ea.nimble.identity;

import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import java.util.List;
import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentity.class */
public interface INimbleIdentity {
    public static final String EXTRA_NIMBLE_IDENTITY_AUTOREFRESH_VALUE = "nimble.identity.extra.autorefresh.value";
    public static final String MIGRATION_PERSISTENCE_ID = "nimble.notification.identity.migraiton";
    public static final String NIMBLE_COMPONENT_ID_IDENTITY = "com.ea.nimble.identity";
    public static final String NIMBLE_NOTIFICATION_IDENTITY_UPDATE = "nimble.notification.identity.update";
    public static final String NOTIFICATION_NIMBLE_IDENTITY_AUTOREFRESH_CHANGE = "nimble.identity.notification.autorefresh_changed";

    /* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentity$NimbleIdentityState.class */
    public enum NimbleIdentityState {
        NIMBLE_IDENTITY_READY,
        NIMBLE_IDENTITY_AUTHENTICATING,
        NIMBLE_IDENTITY_UNAVAILABLE
    }

    INimbleIdentityAuthenticator getAuthenticatorById(String str);

    List<INimbleIdentityAuthenticator> getAuthenticators();

    boolean getAutoRefreshFlag();

    List<INimbleIdentityAuthenticator> getLoggedInAuthenticators();

    Map<String, String> getPidMap();

    NimbleIdentityState getState();

    void requestServerAuthCodeForLegacyOriginToken(String str, String str2, String str3, INimbleIdentityAuthenticator.NimbleIdentityServerAuthCodeCallback nimbleIdentityServerAuthCodeCallback);

    void setAuthenticationConductor(INimbleIdentityAuthenticationConductor iNimbleIdentityAuthenticationConductor);

    void setAutoRefreshFlag(boolean z);
}
