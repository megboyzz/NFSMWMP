package com.ea.nimble.identity;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.Log;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;
import com.ea.nimble.Timer;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import com.ea.nimble.identity.NimbleIdentityError;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/identity/AuthenticatorAnonymous.class */
public class AuthenticatorAnonymous extends AuthenticatorBase {
    private static final String ANONYMOUS_USER_NAME = "Guest";
    private static final double INITIAL_RETRY_TIME = 1.0d;
    private static final double MAX_RETRY_TIME = 300.0d;
    private static final String PID_TYPE = "mobile_upid";
    private static final String URL_TEMPLATE_ANONYMOUS_LOGIN = "%s/connect/auth?mobile_login_type=mobile_game_UPID&mobile_UPIDToken=%s&client_id=%s&response_type=code&redirect_uri=nucleus:rest";
    private static final String URL_TEMPLATE_ANONYMOUS_UPIDTOKEN = "%s/connect/upidtoken?client_id=%s";
    private String m_upidToken = "";
    private double m_currentRetryTime = INITIAL_RETRY_TIME;
    private Timer m_retryTimer = new Timer(new Runnable() { // from class: com.ea.nimble.identity.AuthenticatorAnonymous.1
        @Override // java.lang.Runnable
        public void run() {
            AuthenticatorAnonymous.this.onRetryTimerExpired();
        }
    });

    private AuthenticatorAnonymous() {
        this.TAG = "AuthenticatorAnonymous";
    }

    public void exchangeUpidTokenForAuthCode() {
        synchronized (this) {
            URL url = null;
            try {
                NimbleIdentityConfig configuration = getConfiguration();
                url = new URL(String.format(URL_TEMPLATE_ANONYMOUS_LOGIN, configuration.getConnectServerUrl(), this.m_upidToken, configuration.getClientId()));
            } catch (MalformedURLException e) {
            }
            this.m_authenticateRequest = Network.getComponent().sendGetRequest(url, new HashMap<>(), new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorAnonymous.3
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    try {
                        Map<String, Object> parseBodyJSONData = NimbleIdentityUtility.parseBodyJSONData(networkConnectionHandle);
                        String str = (String) parseBodyJSONData.get("code");
                        if (!Utility.validString(str)) {
                            AuthenticatorAnonymous.this.closeAuthentication(new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Cannot read login OAuth code data from server response data " + parseBodyJSONData));
                            return;
                        }
                        synchronized (this) {
                            AuthenticatorAnonymous.this.exchangeAuthCodeToToken(str);
                        }
                    } catch (Error e2) {
                        AuthenticatorAnonymous.this.closeAuthentication(e2);
                    }
                }
            });
        }
    }

    private void getUpidToken() {
        synchronized (this) {
            URL url = null;
            try {
                NimbleIdentityConfig configuration = getConfiguration();
                url = new URL(String.format(URL_TEMPLATE_ANONYMOUS_UPIDTOKEN, configuration.getConnectServerUrl(), configuration.getClientId()));
            } catch (MalformedURLException e) {
            }
            this.m_authenticateRequest = Network.getComponent().sendGetRequest(url, new HashMap<>(), new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorAnonymous.2
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    Exception error = networkConnectionHandle.getResponse().getError();
                    if (error != null) {
                        AuthenticatorAnonymous.this.closeAuthentication(error instanceof Error ? (Error) error : new Error(Error.Code.NETWORK_CONNECTION_ERROR, "Connection error", error));
                    }
                    String str = null;
                    try {
                        str = Utility.readStringFromStream(networkConnectionHandle.getResponse().getDataStream());
                    } catch (IOException e2) {
                    }
                    if (!Utility.validString(str)) {
                        AuthenticatorAnonymous.this.closeAuthentication(new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Cannot read UPID token from server response"));
                        return;
                    }
                    synchronized (this) {
                        AuthenticatorAnonymous.this.m_upidToken = str;
                        AuthenticatorAnonymous.this.saveUpidToken();
                        AuthenticatorAnonymous.this.exchangeUpidTokenForAuthCode();
                    }
                }
            });
        }
    }

    private static void initialize() {
        Log.Helper.LOGVS("AuthenticatorAnonymous", "Initializing...", new Object[0]);
        AuthenticatorAnonymous authenticatorAnonymous = new AuthenticatorAnonymous();
        Base.registerComponent(authenticatorAnonymous, authenticatorAnonymous.getComponentId());
    }

    private void loadUpidToken() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent == null) {
            this.m_upidToken = null;
            return;
        }
        this.m_upidToken = persistenceForNimbleComponent.getStringValue("UpidToken");
        if (this.m_upidToken == null) {
            this.m_upidToken = persistenceForNimbleComponent.getStringValue("upid_token");
        }
    }

    private void login(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        synchronized (this) {
            this.m_state = INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING;
            if (nimbleIdentityAuthenticatorCallback != null) {
                this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
            }
            Log.Helper.LOGDS(this.TAG, "Anonymous Login", new Object[0]);
            if (Utility.validString(this.m_upidToken)) {
                Log.Helper.LOGVS(this.TAG, "Found cached UPID token %s, use it for login", this.m_upidToken);
                exchangeUpidTokenForAuthCode();
            } else {
                Log.Helper.LOGVS(this.TAG, "Request a new User PID token", new Object[0]);
                getUpidToken();
            }
        }
    }

    public void onRetryTimerExpired() {
        Log.Helper.LOGDS(this.TAG, "Retry timer expired. Attempting login now", new Object[0]);
        login(null);
    }

    public void saveUpidToken() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null) {
            if (!persistenceForNimbleComponent.getBackUp()) {
                persistenceForNimbleComponent.setBackUp(true);
            }
            persistenceForNimbleComponent.setValue("UpidToken", this.m_upidToken);
            persistenceForNimbleComponent.synchronize();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void autoLogin() {
        Log.Helper.LOGIS(this.TAG, "Anonymous authenticator Autologin.", new Object[0]);
        this.m_autoLoginAttempt = true;
        login(null);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void closeAuthentication(Error error) {
        if (error == null || !this.m_autoLoginAttempt) {
            this.m_currentRetryTime = INITIAL_RETRY_TIME;
            this.m_retryTimer.cancel();
            super.closeAuthentication(error);
            return;
        }
        double d = this.m_currentRetryTime;
        this.m_currentRetryTime = Math.min(this.m_currentRetryTime * 2.0d, 300.0d);
        Log.Helper.LOGDS(this.TAG, "Auto login failed. Retrying in %.1f seconds", Double.valueOf(d));
        this.m_retryTimer.schedule(d, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void completeMigration() {
        Log.Helper.LOGDS(this.TAG, "Complete migration called for Anonymous authenticator", new Object[0]);
        cleanAtLogout(null);
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public String getAuthenticatorId() {
        return Global.NIMBLE_AUTHENTICATOR_ANONYMOUS;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void login(NimbleIdentityLoginParams nimbleIdentityLoginParams, INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGWS(this.TAG, "Anonymous authenticator is self-maintained, explicitly calling login on the anonymous authenticator does nothing.", new Object[0]);
        if (nimbleIdentityAuthenticatorCallback != null) {
            nimbleIdentityAuthenticatorCallback.onCallback(this, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_UNSUPPORTED_ACTION, "Anonymous authenticator is self-maintained, explicitly calling login on the anonymous authenticator does nothing."));
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void logout(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGWS(this.TAG, "Anonymous authenticator is self-maintained, explicitly calling logout on the anonymous authenticator does nothing.", new Object[0]);
        NimbleIdentityError nimbleIdentityError = new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_UNSUPPORTED_ACTION, "Anonymous authenticator is self-maintained, explicitly calling logout on the anonymous authenticator does nothing.");
        if (nimbleIdentityAuthenticatorCallback != null) {
            nimbleIdentityAuthenticatorCallback.onCallback(null, nimbleIdentityError);
        }
    }

    public void logoutInternal(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGIS(this.TAG, "Anonymous authenticator logout internal.", new Object[0]);
        if (NimbleIdentityImpl.getComponent().getLoggedInAuthenticators().size() == 1) {
            Log.Helper.LOGWS(this.TAG, "Attempting to logout the anonymous authenticator but it is the only one logged in. Aborting", new Object[0]);
            return;
        }
        cancelAuthentication();
        cleanAtLogout(nimbleIdentityAuthenticatorCallback);
    }

    @Override // com.ea.nimble.identity.AuthenticatorBase, com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void requestIdentityForFriends(ArrayList<String> arrayList, INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback nimbleIdentityFriendsIdentityInfoCallback) {
        requestIdentityForFriends(PID_TYPE, arrayList, nimbleIdentityFriendsIdentityInfoCallback);
    }

    @Override // com.ea.nimble.identity.AuthenticatorBase
    public void restoreAuthenticator(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        this.m_userInfo.setUserName(ANONYMOUS_USER_NAME);
        this.m_userInfo.setDisplayName(ANONYMOUS_USER_NAME);
        loadUpidToken();
        super.restoreAuthenticator(nimbleIdentityAuthenticatorCallback);
    }

    @Override // com.ea.nimble.identity.AuthenticatorBase, com.ea.nimble.Component
    public void suspend() {
        super.suspend();
        this.m_retryTimer.cancel();
        this.m_currentRetryTime = INITIAL_RETRY_TIME;
    }
}
