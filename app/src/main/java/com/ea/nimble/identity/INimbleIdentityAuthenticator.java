package com.ea.nimble.identity;

import com.ea.nimble.Error;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityAuthenticator.class */
public interface INimbleIdentityAuthenticator {
    public static final String AUTHENTICATOR_COMPONENT_PREFIX = "com.ea.nimble.identity.authenticator.";

    /* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityAuthenticator$NimbleAuthenticatorAccessTokenCallback.class */
    public interface NimbleAuthenticatorAccessTokenCallback {
        void AccessTokenCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str, String str2, Error error);
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityAuthenticator$NimbleIdentityAuthenticationState.class */
    public enum NimbleIdentityAuthenticationState {
        NIMBLE_IDENTITY_AUTHENTICATION_UNAVAILABLE,
        NIMBLE_IDENTITY_AUTHENTICATION_NONE,
        NIMBLE_IDENTITY_AUTHENTICATION_GOING,
        NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS,
        NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityAuthenticator$NimbleIdentityAuthenticatorCallback.class */
    public interface NimbleIdentityAuthenticatorCallback {
        void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error);
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityAuthenticator$NimbleIdentityFriendsIdentityInfoCallback.class */
    public interface NimbleIdentityFriendsIdentityInfoCallback {
        void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, JSONObject jSONObject, Error error);
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/INimbleIdentityAuthenticator$NimbleIdentityServerAuthCodeCallback.class */
    public interface NimbleIdentityServerAuthCodeCallback {
        void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str, String str2, String str3, Error error);
    }

    String getAuthenticatorId();

    NimbleIdentityPersona getPersonaByNamespace(String str, long j);

    NimbleIdentityPersona getPersonaByNamespace(String str, String str2);

    List<NimbleIdentityPersona> getPersonas();

    NimbleIdentityPidInfo getPidInfo();

    NimbleIdentityAuthenticationState getState();

    NimbleIdentityUserInfo getUserInfo();

    void login(NimbleIdentityLoginParams nimbleIdentityLoginParams, NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);

    void logout(NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);

    void refreshPersonas(NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);

    void refreshPidInfo(NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);

    void refreshUserInfo(NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback);

    void requestAccessToken(NimbleAuthenticatorAccessTokenCallback nimbleAuthenticatorAccessTokenCallback);

    void requestAuthCode(String str, String str2, NimbleIdentityServerAuthCodeCallback nimbleIdentityServerAuthCodeCallback);

    void requestIdentityForFriends(ArrayList<String> arrayList, NimbleIdentityFriendsIdentityInfoCallback nimbleIdentityFriendsIdentityInfoCallback) throws Exception;
}
