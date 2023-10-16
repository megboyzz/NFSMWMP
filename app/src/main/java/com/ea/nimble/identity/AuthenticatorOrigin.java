package com.ea.nimble.identity;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.Log;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import com.ea.nimble.identity.NimbleIdentityError;
import com.ea.nimble.identity.NimbleIdentityLoginParams;
import java.util.ArrayList;

/* loaded from: stdlib.jar:com/ea/nimble/identity/AuthenticatorOrigin.class */
class AuthenticatorOrigin extends AuthenticatorBase {
    private static final String DATA_TEMPLATE_LOGIN_WITH_ORIGIN_PASSWORD = "grant_type=password&client_id=%s&client_secret=%s&username=%s&password=%s&redirect_uri=nucleus:rest";
    private static final String PID_TYPE = "mobile_origin";

    private AuthenticatorOrigin() {
        this.TAG = "AuthenticatorOrigin";
    }

    private static void initialize() {
        Log.Helper.LOGIS("AuthenticatorOrigin", "Initializing...", new Object[0]);
        AuthenticatorOrigin authenticatorOrigin = new AuthenticatorOrigin();
        Base.registerComponent(authenticatorOrigin, authenticatorOrigin.getComponentId());
    }

    private void loginOrigin(String str, String str2, INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        NimbleIdentityConfig configuration = getConfiguration();
        exchangeDataForToken(String.format(DATA_TEMPLATE_LOGIN_WITH_ORIGIN_PASSWORD, configuration.getClientId(), configuration.getClientSecret(), str, str2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void autoLogin() {
        closeAuthentication(new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_SESSION_EXPIRED, "Cannot auto login for origin after session expired"));
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public String getAuthenticatorId() {
        return Global.NIMBLE_AUTHENTICATOR_ORIGIN;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void login(NimbleIdentityLoginParams nimbleIdentityLoginParams, INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        if (nimbleIdentityLoginParams instanceof NimbleIdentityLoginParams.OriginCredentialsLoginParams) {
            NimbleIdentityLoginParams.OriginCredentialsLoginParams originCredentialsLoginParams = (NimbleIdentityLoginParams.OriginCredentialsLoginParams) nimbleIdentityLoginParams;
            if (Utility.validString(originCredentialsLoginParams.getUsername()) && Utility.validString(originCredentialsLoginParams.getPassword())) {
                synchronized (this) {
                    setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING);
                    if (nimbleIdentityAuthenticatorCallback != null) {
                        this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
                    }
                    loginOrigin(originCredentialsLoginParams.getUsername(), originCredentialsLoginParams.getPassword(), nimbleIdentityAuthenticatorCallback);
                }
            } else if (nimbleIdentityAuthenticatorCallback != null) {
                nimbleIdentityAuthenticatorCallback.onCallback(this, new Error(Error.Code.INVALID_ARGUMENT, "Invalid username/password for Origin login"));
            }
        } else if (nimbleIdentityLoginParams instanceof NimbleIdentityLoginParams.OriginOAuthCodeLoginParams) {
            NimbleIdentityLoginParams.OriginOAuthCodeLoginParams originOAuthCodeLoginParams = (NimbleIdentityLoginParams.OriginOAuthCodeLoginParams) nimbleIdentityLoginParams;
            if (Utility.validString(originOAuthCodeLoginParams.getOauthCode())) {
                synchronized (this) {
                    setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING);
                    if (nimbleIdentityAuthenticatorCallback != null) {
                        this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
                    }
                    exchangeAuthCodeToToken(originOAuthCodeLoginParams.getOauthCode());
                }
            } else if (nimbleIdentityAuthenticatorCallback != null) {
                nimbleIdentityAuthenticatorCallback.onCallback(this, new Error(Error.Code.INVALID_ARGUMENT, "Invalid auth code for Origin login"));
            }
        } else if (nimbleIdentityAuthenticatorCallback != null) {
            nimbleIdentityAuthenticatorCallback.onCallback(this, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_INVALID_LOGINPARAMS, "Unrecognized login parameters"));
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void logout(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        cancelAuthentication();
        cleanAtLogout(nimbleIdentityAuthenticatorCallback);
    }

    @Override // com.ea.nimble.identity.AuthenticatorBase, com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void requestIdentityForFriends(ArrayList<String> arrayList, INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback nimbleIdentityFriendsIdentityInfoCallback) {
        requestIdentityForFriends(PID_TYPE, arrayList, nimbleIdentityFriendsIdentityInfoCallback);
    }
}
