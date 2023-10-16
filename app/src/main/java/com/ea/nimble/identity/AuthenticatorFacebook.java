package com.ea.nimble.identity;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.Facebook;
import com.ea.nimble.Global;
import com.ea.nimble.HttpRequest;
import com.ea.nimble.IFacebook;
import com.ea.nimble.Log;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import com.ea.nimble.identity.NimbleIdentityError;
import com.ea.nimble.identity.NimbleIdentityLoginParams;
import com.facebook.FacebookOperationCanceledException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/identity/AuthenticatorFacebook.class */
class AuthenticatorFacebook extends AuthenticatorBase {
    private static final String PID_TYPE = "mobile_facebook";
    private static final String URL_TEMPLATE_FACEBOOK_IMAGE_URL = "https://graph.facebook.com/%s/picture?type=normal";
    private static final String URL_TEMPLATE_FACEBOOK_LOGIN = "%s/connect/auth?mobile_login_type=mobile_game_facebook&client_id=%s&response_type=code&display=mobilegame/login&fb_token=%s&redirect_uri=nucleus:rest";
    private String m_overrideBirthday;

    private AuthenticatorFacebook() {
        this.TAG = "AuthenticatorFacebook";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void exchangeFacebookAccessTokenForAuthCode(String str) {
        synchronized (this) {
            URL url = null;
            try {
                NimbleIdentityConfig configuration = getConfiguration();
                url = new URL(String.format(URL_TEMPLATE_FACEBOOK_LOGIN, configuration.getConnectServerUrl(), configuration.getClientId(), str));
            } catch (MalformedURLException e) {
            }
            HttpRequest httpRequest = new HttpRequest(url);
            httpRequest.runInBackground = true;
            this.m_authenticateRequest = Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorFacebook.2
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    if (AuthenticatorFacebook.this.m_state != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE) {
                        try {
                            Map<String, Object> parseBodyJSONData = NimbleIdentityUtility.parseBodyJSONData(networkConnectionHandle);
                            String str2 = (String) parseBodyJSONData.get("code");
                            if (!Utility.validString(str2)) {
                                AuthenticatorFacebook.this.closeAuthentication(new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Cannot read login OAuth code data from server response data " + parseBodyJSONData));
                            } else {
                                AuthenticatorFacebook.this.exchangeAuthCodeToToken(str2);
                            }
                        } catch (Error e2) {
                            AuthenticatorFacebook.this.closeAuthentication(e2);
                        }
                    }
                }
            });
        }
    }

    private static void initialize() {
        Log.Helper.LOGVS("AuthenticatorFacebook", "Initializing...", new Object[0]);
        AuthenticatorFacebook authenticatorFacebook = new AuthenticatorFacebook();
        Base.registerComponent(authenticatorFacebook, authenticatorFacebook.getComponentId());
    }

    private void loginFacebook(IFacebook iFacebook, List<String> list, INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        synchronized (this) {
            setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING);
            if (nimbleIdentityAuthenticatorCallback != null) {
                this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
            }
        }
        iFacebook.login(list, new IFacebook.FacebookCallback() { // from class: com.ea.nimble.identity.AuthenticatorFacebook.1
            @Override // com.ea.nimble.IFacebook.FacebookCallback
            public void callback(IFacebook iFacebook2, boolean z, Exception exc) {
                if (!z) {
                    AuthenticatorFacebook.this.closeAuthentication((exc == null || (exc instanceof FacebookOperationCanceledException)) ? new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_USER_CANCELLED, "Facebook login is cancelled by user") : exc instanceof Error ? (Error) exc : new Error(Error.Code.UNKNOWN, "Unknown error type from Facebook", exc));
                } else if (!Utility.validString(iFacebook2.getAccessToken())) {
                    AuthenticatorFacebook.this.closeAuthentication(new Error(Error.Code.SYSTEM_UNEXPECTED, "Facebook SDK gives login success without a valid Facebook token"));
                } else {
                    synchronized (this) {
                        AuthenticatorFacebook.this.exchangeFacebookAccessTokenForAuthCode(iFacebook2.getAccessToken());
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void autoLogin() {
        Log.Helper.LOGIS(this.TAG, "Facebook Authenticator AutoLogin", new Object[0]);
        this.m_autoLoginAttempt = true;
        IFacebook iFacebook = (IFacebook) Base.getComponent(Facebook.COMPONENT_ID);
        if (iFacebook == null) {
            closeAuthentication(new Error(Error.Code.SYSTEM_UNEXPECTED, "Cannot auto login with FacebookAuthenticator since it is disabled for no NimbleFacebook component"));
        } else if (Utility.validString(iFacebook.getAccessToken())) {
            synchronized (this) {
                this.m_state = INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING;
                exchangeFacebookAccessTokenForAuthCode(iFacebook.getAccessToken());
            }
        } else {
            closeAuthentication(new Error(Error.Code.SYSTEM_UNEXPECTED, "Cannot auto login with FacebookAuthenticator since Facebook SDK doesn't have any session existing"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void cancelAuthentication() {
        if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING && this.m_authenticateRequest == null) {
            super.cancelAuthentication();
            setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING);
            return;
        }
        super.cancelAuthentication();
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public String getAuthenticatorId() {
        return Global.NIMBLE_AUTHENTICATOR_FACEBOOK;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void login(NimbleIdentityLoginParams nimbleIdentityLoginParams, INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGIS(this.TAG, "Facebook Authenticator Login", new Object[0]);
        IFacebook iFacebook = (IFacebook) Base.getComponent(Facebook.COMPONENT_ID);
        if (iFacebook == null) {
            Log.Helper.LOGIS(this.TAG, "Cannot login with FacebookAuthenticator since it is disabled for no NimbleFacebook component", new Object[0]);
            if (nimbleIdentityAuthenticatorCallback != null) {
                nimbleIdentityAuthenticatorCallback.onCallback(this, new Error(Error.Code.NOT_AVAILABLE, "Cannot login with FacebookAuthenticator since it is disabled for no NimbleFacebook component"));
            }
        } else if (nimbleIdentityLoginParams == null) {
            loginFacebook(iFacebook, null, nimbleIdentityAuthenticatorCallback);
        } else if (nimbleIdentityLoginParams instanceof NimbleIdentityLoginParams.FacebookClientLoginParams) {
            loginFacebook(iFacebook, ((NimbleIdentityLoginParams.FacebookClientLoginParams) nimbleIdentityLoginParams).getFacebookPermissions(), nimbleIdentityAuthenticatorCallback);
        } else if (nimbleIdentityLoginParams instanceof NimbleIdentityLoginParams.FacebookAccessTokenLoginParams) {
            NimbleIdentityLoginParams.FacebookAccessTokenLoginParams facebookAccessTokenLoginParams = (NimbleIdentityLoginParams.FacebookAccessTokenLoginParams) nimbleIdentityLoginParams;
            if (Utility.validString(facebookAccessTokenLoginParams.getFacebookAccessToken())) {
                synchronized (this) {
                    setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING);
                    if (nimbleIdentityAuthenticatorCallback != null) {
                        this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
                    }
                    iFacebook.refreshSession(facebookAccessTokenLoginParams.getFacebookAccessToken(), facebookAccessTokenLoginParams.getExpiryDate());
                    exchangeFacebookAccessTokenForAuthCode(facebookAccessTokenLoginParams.getFacebookAccessToken());
                }
            } else if (nimbleIdentityAuthenticatorCallback != null) {
                nimbleIdentityAuthenticatorCallback.onCallback(this, new Error(Error.Code.INVALID_ARGUMENT, "Invalid Facebook access token for Facebook token"));
            }
        } else if (nimbleIdentityAuthenticatorCallback != null) {
            nimbleIdentityAuthenticatorCallback.onCallback(this, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_INVALID_LOGINPARAMS, "Unrecognized login parameters"));
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void logout(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGIS(this.TAG, "Facebook Authenticator is logging out", new Object[0]);
        IFacebook iFacebook = (IFacebook) Base.getComponent(Facebook.COMPONENT_ID);
        if (iFacebook == null) {
            Log.Helper.LOGIS(this.TAG, "Cannot logout with FacebookAuthenticator since it is disabled for no NimbleFacebook component", new Object[0]);
            NimbleIdentityError nimbleIdentityError = new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_INVALID_REQUEST, "Cannot logout with FacebookAuthenticator since it is disabled for no NimbleFacebook component");
            if (nimbleIdentityAuthenticatorCallback != null) {
                nimbleIdentityAuthenticatorCallback.onCallback(this, nimbleIdentityError);
                return;
            }
            return;
        }
        cancelAuthentication();
        iFacebook.logout();
        cleanAtLogout(nimbleIdentityAuthenticatorCallback);
    }

    @Override // com.ea.nimble.identity.AuthenticatorBase, com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void requestIdentityForFriends(ArrayList<String> arrayList, INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback nimbleIdentityFriendsIdentityInfoCallback) throws Exception {
        requestIdentityForFriends(PID_TYPE, arrayList, nimbleIdentityFriendsIdentityInfoCallback);
    }

    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void restoreAuthenticator(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        if (((IFacebook) Base.getComponent(Facebook.COMPONENT_ID)) == null) {
            Log.Helper.LOGIS(this.TAG, "FacebookAuthenticator is disabled since there is no NimbleFacebook component", new Object[0]);
            Log.Helper.LOGIS(this.TAG, "To enable FacebookAuthenticator, please ensure the NimbleFacebook jar is correctly linked", new Object[0]);
            setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_UNAVAILABLE);
        }
        super.restoreAuthenticator(nimbleIdentityAuthenticatorCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.Component
    public void setup() {
        this.m_overrideBirthday = null;
    }

    @Override // com.ea.nimble.identity.AuthenticatorBase
    protected void updateUserProfile() {
        IFacebook iFacebook = (IFacebook) Base.getComponent(Facebook.COMPONENT_ID);
        if (iFacebook == null) {
            closeUserInfoUpdate(new Error(Error.Code.NOT_AVAILABLE, "Cannot auto login with FacebookAuthenticator since it is disabled for no NimbleFacebook component"), true);
            return;
        }
        Map<String, Object> graphUser = iFacebook.getGraphUser();
        NimbleIdentityUserInfo nimbleIdentityUserInfo = this.m_userInfo == null ? new NimbleIdentityUserInfo() : this.m_userInfo.clone();
        if (graphUser != null) {
            nimbleIdentityUserInfo.setUserId((String) graphUser.get("id"));
            nimbleIdentityUserInfo.setDisplayName((String) graphUser.get("name"));
            nimbleIdentityUserInfo.setUserName((String) graphUser.get("username"));
            nimbleIdentityUserInfo.setAvatarUri(String.format(URL_TEMPLATE_FACEBOOK_IMAGE_URL, nimbleIdentityUserInfo.getUserId()));
            String str = (String) graphUser.get("birthday");
            if (Utility.validString(str) && (!Utility.validString(nimbleIdentityUserInfo.getDateOfBirth()) || nimbleIdentityUserInfo.getDateOfBirth().equals(this.m_overrideBirthday))) {
                this.m_overrideBirthday = str;
                nimbleIdentityUserInfo.setDateOfBirth(str);
            }
            nimbleIdentityUserInfo.setEmail((String) graphUser.get("email"));
            nimbleIdentityUserInfo.setExpiryTime(new Date(System.currentTimeMillis() + ((long) (getConfiguration().getExpiryInterval() * 1000.0d))));
            synchronized (this) {
                this.m_userInfo = nimbleIdentityUserInfo;
            }
            closeUserInfoUpdate(null, true);
            HashMap hashMap = new HashMap();
            hashMap.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, getAuthenticatorId());
            Utility.sendBroadcast(Global.NIMBLE_NOTIFICATION_IDENTITY_USER_INFO_UPDATE, hashMap);
        }
    }
}
