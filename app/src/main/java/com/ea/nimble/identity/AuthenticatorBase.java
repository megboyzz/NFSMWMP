package com.ea.nimble.identity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ea.nimble.Base;
import com.ea.nimble.Component;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.HttpRequest;
import com.ea.nimble.IHttpRequest;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;
import com.ea.nimble.Timer;
import com.ea.nimble.Utility;
import com.ea.nimble.tracking.ITracking;
import com.ea.nimble.tracking.Tracking;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/identity/AuthenticatorBase.class */
public abstract class AuthenticatorBase extends Component implements LogSource, INimbleIdentityAuthenticator {
    private static final String AUTHORIZATION_KEY = "Authorization";
    private static final String DATA_CONTENT_TYPE_KEY = "Content-type";
    private static final String DATA_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String DATA_TEMPLATE_LOGIN_WITH_OAUTH_CODE = "grant_type=authorization_code&code=%s&client_id=%s&client_secret=%s&redirect_uri=nucleus:rest";
    private static final String DATA_TEMPLATE_LOGIN_WITH_REFRESH_TOKEN = "grant_type=refresh_token&refresh_token=%s&client_id=%s&client_secret=%s&redirect_uri=nucleus:rest";
    private static final double DEFAULT_RETRY_INTERVAL = 60.0d;
    private static final double EXPIRY_MARGIN_FOR_TIMER = 5.0d;
    public static final String NIMBLE_IDENTITY_PERSISTENCE_TOKEN_SUFFIX = "nimble_identity_access_token";
    private static final String REQUEST_HEADER_PERSONA_INFO_EXTRA_FLAG = "X-Expand-Results";
    private static final String REQUEST_HEADER_PID_INFO_EXTRA_FLAG = "X-Include-Underage";
    private static final String REQUEST_HEADER_REFRESH_TOKEN_EXTRA_FLAG = "X-Include-RT-Time";
    private static final String REQUEST_HEADER_TRUE_VALUE = "true";
    private static final String URL_TEMPALTE_GET_IDENTITY_INFO_FOR_FRIENDS = "/proxy/identity/pids/personaextref/bulk";
    private static final String URL_TEMPLATE_GET_PERSONA_INFO = "%s/proxy/identity/pids/me/personas";
    private static final String URL_TEMPLATE_GET_PID_INFO = "%s/proxy/identity/pids/me";
    private static final String URL_TEMPLATE_LOGIN = "%s/connect/token";
    private static final String URL_TEMPLATE_LOGOUT = "%s/connect/clearsid?client_id=%s&access_token=%s";
    private static final String URL_TEMPLATE_REQUEST_SERVER_AUTHENTICATION_OAUTH_CODE = "%s/connect/auth?client_id=%s&response_type=code&access_token=%s&redirect_uri=nucleus:rest";
    protected NetworkConnectionHandle m_authenticateRequest;
    protected boolean m_autoLoginAttempt;
    private NetworkConnectionHandle m_personaRequest;
    private ArrayList<NimbleIdentityPersona> m_personas;
    private NimbleIdentityPidInfo m_pidInfo;
    private NetworkConnectionHandle m_pidInfoRequest;
    private NimbleIdentityToken m_token;
    protected INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState m_state = INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE;
    protected NimbleIdentityUserInfo m_userInfo = new NimbleIdentityUserInfo();
    protected ArrayList<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> m_authenticateCallbacks = new ArrayList<>();
    private ArrayList<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> m_userInfoCallbacks = new ArrayList<>();
    private ArrayList<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> m_pidInfoCallbacks = new ArrayList<>();
    private ArrayList<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> m_personaCallbacks = new ArrayList<>();
    private Timer m_tokenRefreshTimer = new Timer(new Runnable() { // from class: com.ea.nimble.identity.AuthenticatorBase.1
        @Override // java.lang.Runnable
        public void run() {
            AuthenticatorBase.this.onTokenRefreshTimer();
        }
    });
    private Timer m_userProfileRefreshTimer = new Timer(new Runnable() { // from class: com.ea.nimble.identity.AuthenticatorBase.2
        @Override // java.lang.Runnable
        public void run() {
            AuthenticatorBase.this.onUserProfileRefreshTimer();
        }
    });
    private Timer m_pidInfoRefreshTimer = new Timer(new Runnable() { // from class: com.ea.nimble.identity.AuthenticatorBase.3
        @Override // java.lang.Runnable
        public void run() {
            AuthenticatorBase.this.onPidInfoRefreshTimer();
        }
    });
    private Timer m_personaRefreshTimer = new Timer(new Runnable() { // from class: com.ea.nimble.identity.AuthenticatorBase.4
        @Override // java.lang.Runnable
        public void run() {
            AuthenticatorBase.this.onPersonaRefreshTimer();
        }
    });
    protected String TAG = "AuthenticatorBase";
    private BroadcastReceiver m_networkChangeReceiver = new BroadcastReceiver() { // from class: com.ea.nimble.identity.AuthenticatorBase.5
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AuthenticatorBase.this.onNetworkChange();
        }
    };
    protected BroadcastReceiver m_identityConfigChangeReceiver = new BroadcastReceiver() { // from class: com.ea.nimble.identity.AuthenticatorBase.6
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            AuthenticatorBase.this.onConfigurationChange();
        }
    };

    /* loaded from: stdlib.jar:com/ea/nimble/identity/AuthenticatorBase$INimbleIdentityInternalServiceRequestCallback.class */
    interface INimbleIdentityInternalServiceRequestCallback {
        void onServiceComplete(Error error);
    }

    /* JADX WARN: Type inference failed for: r0v42, types: [double] */
    /* JADX WARN: Type inference failed for: r9v1 */
    /* JADX WARN: Type inference failed for: r9v2 */
    /* JADX WARN: Type inference failed for: r9v3 */
    /* JADX WARN: Type inference failed for: r9v5 */
    /* JADX WARN: Unknown variable types count: 2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void closePersonaUpdate(com.ea.nimble.Error r8) {
        /*
            Method dump skipped, instructions count: 213
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.identity.AuthenticatorBase.closePersonaUpdate(com.ea.nimble.Error):void");
    }

    public void closePidInfoUpdate(Error error) {
        ArrayList<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> arrayList;
        if (error == null) {
            Log.Helper.LOGVS(this.TAG, "Updating pid information succeed!", new Object[0]);
        } else {
            Log.Helper.LOGES(this.TAG, "Fail to get pid information from Identity server for error %s", error);
        }
        synchronized (this) {
            this.m_pidInfoRequest = null;
            if (getConfiguration().getAutoRefresh() && this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                if (error == null) {
                    this.m_pidInfoRefreshTimer.schedule((((double) (this.m_pidInfo.getExpiryTime().getTime() - System.currentTimeMillis())) / 1000.0d) - EXPIRY_MARGIN_FOR_TIMER, false);
                } else {
                    this.m_pidInfoRefreshTimer.schedule(60.0d, false);
                }
            }
            arrayList = this.m_pidInfoCallbacks;
            this.m_pidInfoCallbacks = new ArrayList<>();
        }
        Iterator<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onCallback(this, error);
        }
    }

    private Error environmentCheck() {
        Error error;
        synchronized (this) {
            if (Network.getComponent().getStatus() != Network.Status.OK) {
                if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                    setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE);
                }
                error = new Error(Error.Code.NETWORK_NO_CONNECTION, "Idenitity cannot work without network.");
            } else {
                NimbleIdentityConfig configuration = getConfiguration();
                error = (configuration == null || !configuration.isReady()) ? new Error(Error.Code.NOT_READY, "Identity is still in initialization and not ready for operation.") : NimbleIdentityImpl.getComponent().getAuthenticationConductor() == null ? new Error(Error.Code.NOT_READY, "No authentication conductor has been set yet.") : null;
            }
        }
        return error;
    }

    private void loadPidInfo() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null) {
            this.m_pidInfo = (NimbleIdentityPidInfo) persistenceForNimbleComponent.getValue("pidInfo");
            if (this.m_pidInfo != null) {
                Log.Helper.LOGDS(this.TAG, "Restored existing pidInfo (pid = %s) for %s authenticator from persistence.", this.m_pidInfo.getPid(), getAuthenticatorId());
                HashMap hashMap = new HashMap();
                hashMap.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, getAuthenticatorId());
                Utility.sendBroadcast(Global.NIMBLE_NOTIFICATION_IDENTITY_PID_INFO_UPDATE, hashMap);
                return;
            }
            Log.Helper.LOGDS(this.TAG, "The previous Exception in Persistence while getting NimbleIdentityPidInfo can be safely ignored. A cached value for pidInfo does not exist or was stored in an older format. This value will instead be retrieved from the server.", new Object[0]);
        }
    }

    private void loadState() {
        Boolean bool;
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent == null || (bool = (Boolean) persistenceForNimbleComponent.getValue("loggedIn")) == null || !bool.booleanValue()) {
            this.m_state = INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE;
            Log.Helper.LOGVS(this.TAG, "Loaded state: NONE", new Object[0]);
            return;
        }
        this.m_state = INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE;
        Log.Helper.LOGVS(this.TAG, "Loaded state: OFFLINE", new Object[0]);
    }

    private void loadToken() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent == null) {
            this.m_token = null;
            return;
        }
        this.m_token = (NimbleIdentityToken) persistenceForNimbleComponent.getValue("token");
        Log.Helper.LOGVS(this.TAG, "Loading token: " + this.m_token, new Object[0]);
    }

    public void onConfigurationChange() {
        Log.Helper.LOGVS(this.TAG, "Identity resumes after configuration changed.", new Object[0]);
        resume();
    }

    public void onNetworkChange() {
        if (Network.getComponent().getStatus() == Network.Status.OK) {
            Log.Helper.LOGDS(this.TAG, "Identity resumes after network recovered.", new Object[0]);
            resume();
            return;
        }
        if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING) {
            cancelAuthentication();
        }
        if (this.m_state != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE) {
            setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE);
        }
    }

    public void onPersonaRefreshTimer() {
        refreshPersonas(null);
    }

    public void onPidInfoRefreshTimer() {
        refreshPidInfo(null);
    }

    public void onTokenRefreshTimer() {
        this.m_tokenRefreshTimer.cancel();
        refreshToken();
    }

    public void onTokenResponse(NetworkConnectionHandle networkConnectionHandle) {
        if (this.m_state != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE) {
            try {
                Map<String, Object> parseBodyJSONData = NimbleIdentityUtility.parseBodyJSONData(networkConnectionHandle);
                NimbleIdentityToken nimbleIdentityToken = new NimbleIdentityToken(parseBodyJSONData);
                if (!Utility.validString(nimbleIdentityToken.getAccessToken()) || !Utility.validString(nimbleIdentityToken.getRefreshToken())) {
                    closeAuthentication(new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Fail to parse token from server response data " + parseBodyJSONData));
                    return;
                }
                this.m_pidInfoCallbacks.add(new INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.18
                    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback
                    public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
                        AuthenticatorBase.this.closeAuthentication(error);
                    }
                });
                synchronized (this) {
                    this.m_token = nimbleIdentityToken;
                    saveToken();
                    updateUserProfile();
                    updatePidInfo();
                    updatePersonas();
                }
            } catch (Error e) {
                closeAuthentication(e);
            }
        }
    }

    public void onUserProfileRefreshTimer() {
        refreshUserProfile(null);
    }

    private void prepare(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        synchronized (this) {
            Error environmentCheck = environmentCheck();
            if (environmentCheck != null) {
                if (nimbleIdentityAuthenticatorCallback != null) {
                    nimbleIdentityAuthenticatorCallback.onCallback(this, environmentCheck);
                }
            } else if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING || this.m_token == null || this.m_token.getRefreshTokenExpiryTime().before(new Date())) {
                if (nimbleIdentityAuthenticatorCallback != null) {
                    this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
                }
                if (this.m_state != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING) {
                    autoLogin();
                }
            } else if (!this.m_token.getAccessTokenExpiryTime().after(new Date())) {
                if (nimbleIdentityAuthenticatorCallback != null) {
                    this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
                }
                refreshToken();
            } else if (nimbleIdentityAuthenticatorCallback != null) {
                nimbleIdentityAuthenticatorCallback.onCallback(this, null);
            }
        }
    }

    private void refreshToken() {
        ByteArrayOutputStream byteArrayOutputStream;
        synchronized (this) {
            Log.Helper.LOGVS(this.TAG, "Refreshing token to get access token.", new Object[0]);
            if (this.m_tokenRefreshTimer.isRunning()) {
                this.m_tokenRefreshTimer.cancel();
            }
            setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING);
            NimbleIdentityConfig configuration = getConfiguration();
            URL url = null;
            String format = String.format(DATA_TEMPLATE_LOGIN_WITH_REFRESH_TOKEN, this.m_token.getRefreshToken(), configuration.getClientId(), configuration.getClientSecret());
            try {
                url = new URL(String.format(URL_TEMPLATE_LOGIN, configuration.getConnectServerUrl()));
                try {
                    byte[] bytes = format.getBytes("UTF-8");
                    byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
                    try {
                        byteArrayOutputStream.write(bytes);
                    } catch (IOException e) {
                    }
                } catch (IOException e2) {
                    byteArrayOutputStream = null;
                }
            } catch (IOException e3) {
                byteArrayOutputStream = null;
            }
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put(DATA_CONTENT_TYPE_KEY, DATA_CONTENT_TYPE_VALUE);
            hashMap.put(REQUEST_HEADER_REFRESH_TOKEN_EXTRA_FLAG, REQUEST_HEADER_TRUE_VALUE);
            HttpRequest httpRequest = new HttpRequest(url);
            httpRequest.method = IHttpRequest.Method.POST;
            httpRequest.headers = hashMap;
            httpRequest.data = byteArrayOutputStream;
            httpRequest.runInBackground = true;
            this.m_authenticateRequest = Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.13
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    if (networkConnectionHandle.getResponse().getError() != null) {
                        AuthenticatorBase.this.autoLogin();
                    } else {
                        AuthenticatorBase.this.onTokenResponse(networkConnectionHandle);
                    }
                }
            });
        }
    }

    public void refreshUserProfile(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        synchronized (this) {
            Error environmentCheck = environmentCheck();
            if (environmentCheck == null) {
                Log.Helper.LOGVS(this.TAG, "Ready to refresh user profile", new Object[0]);
                if (this.m_userProfileRefreshTimer.isRunning()) {
                    this.m_userProfileRefreshTimer.cancel();
                }
                if (nimbleIdentityAuthenticatorCallback != null) {
                    this.m_pidInfoCallbacks.add(nimbleIdentityAuthenticatorCallback);
                }
                updateUserProfile();
            } else if (nimbleIdentityAuthenticatorCallback != null) {
                nimbleIdentityAuthenticatorCallback.onCallback(this, environmentCheck);
            }
        }
    }

    private void resume(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        synchronized (this) {
            if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE) {
                Log.Helper.LOGIS(this.TAG, "Skipping autoLogin for state NONE", new Object[0]);
            } else {
                if (nimbleIdentityAuthenticatorCallback != null) {
                    this.m_authenticateCallbacks.add(nimbleIdentityAuthenticatorCallback);
                }
                if (environmentCheck() != null) {
                    Log.Helper.LOGIS(this.TAG, "Authenticator %s resume failing - environment not ready.", getAuthenticatorId());
                } else if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING) {
                    Log.Helper.LOGIS(this.TAG, "Skipping autoLogin for state GOING", new Object[0]);
                } else {
                    if (this.m_token != null) {
                        if (this.m_token.getAccessTokenExpiryTime().after(new Date())) {
                            closeAuthentication(null);
                            getPidInfo();
                            getPersonas();
                            getUserInfo();
                        } else if (this.m_token.getRefreshTokenExpiryTime().after(new Date())) {
                            refreshToken();
                        }
                    }
                    autoLogin();
                }
            }
        }
    }

    private void savePidInfo() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null && this.m_pidInfo != null) {
            persistenceForNimbleComponent.setValue("pidInfo", this.m_pidInfo);
            persistenceForNimbleComponent.synchronize();
            Log.Helper.LOGDS(this.TAG, "Saved current pidInfo (pid = %s) for %s authenticator to persistence.", this.m_pidInfo.getPid(), getAuthenticatorId());
        }
    }

    private void saveState() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null) {
            boolean z = this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING || this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE || this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS;
            persistenceForNimbleComponent.setValue("loggedIn", Boolean.valueOf(z));
            persistenceForNimbleComponent.synchronize();
            Log.Helper.LOGDS(this.TAG, "Saved loggedIn value to persistence as %s for state %s", Boolean.valueOf(z), this.m_state);
        }
    }

    private void saveToken() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null) {
            Log.Helper.LOGVS(this.TAG, "Saving token: " + this.m_token, new Object[0]);
            persistenceForNimbleComponent.setValue("token", this.m_token);
        }
    }

    public void updatePersonas() {
        if (this.m_personaRequest != null) {
            Log.Helper.LOGVS(this.TAG, "Persona update in progress, skipping", new Object[0]);
            return;
        }
        Log.Helper.LOGVS(this.TAG, "Updating persona info", new Object[0]);
        URL url = null;
        try {
            url = new URL(String.format(URL_TEMPLATE_GET_PERSONA_INFO, getConfiguration().getProxyServerUrl()));
        } catch (MalformedURLException e) {
        }
        String format = String.format("%s %s", this.m_token.getType(), this.m_token.getAccessToken());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AUTHORIZATION_KEY, format);
        hashMap.put(REQUEST_HEADER_PERSONA_INFO_EXTRA_FLAG, REQUEST_HEADER_TRUE_VALUE);
        HttpRequest httpRequest = new HttpRequest(url);
        httpRequest.method = IHttpRequest.Method.GET;
        httpRequest.headers = hashMap;
        this.m_personaRequest = Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.16
            @Override // com.ea.nimble.NetworkConnectionCallback
            public void callback(NetworkConnectionHandle networkConnectionHandle) {
                try {
                    Map<String, Object> parseBodyJSONData = NimbleIdentityUtility.parseBodyJSONData(networkConnectionHandle);
                    Map map = (Map) parseBodyJSONData.get("personas");
                    if (map == null) {
                        AuthenticatorBase.this.closePersonaUpdate(new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Fail to parse persona information from server response data " + parseBodyJSONData));
                    }
                    List<Map> list = (List) map.get("persona");
                    if (list == null) {
                        AuthenticatorBase.this.closePersonaUpdate(new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Fail to parse persona information from server response data " + parseBodyJSONData));
                    }
                    ArrayList arrayList = new ArrayList();
                    Date date = new Date(System.currentTimeMillis() + ((long) (AuthenticatorBase.this.getConfiguration().getExpiryInterval() * 1000.0d)));
                    for (Map map2 : list) {
                        arrayList.add(new NimbleIdentityPersona(map2, date));
                    }
                    Log.Helper.LOGVS(AuthenticatorBase.this.TAG, "Update personas successfully with new data", new Object[0]);
                    synchronized (this) {
                        AuthenticatorBase.this.m_personas = arrayList;
                    }
                    AuthenticatorBase.this.closePersonaUpdate(null);
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, AuthenticatorBase.this.getAuthenticatorId());
                    Utility.sendBroadcast(Global.NIMBLE_NOTIFICATION_IDENTITY_PERSONA_INFO_UPDATE, hashMap2);
                } catch (Error e2) {
                    AuthenticatorBase.this.closePersonaUpdate(e2);
                }
            }
        });
    }

    public void updatePidInfo() {
        if (this.m_pidInfoRequest != null) {
            Log.Helper.LOGVS(this.TAG, "Pid info update in progress, skipping", new Object[0]);
            return;
        }
        Log.Helper.LOGVS(this.TAG, "Updating pid info", new Object[0]);
        URL url = null;
        try {
            url = new URL(String.format(URL_TEMPLATE_GET_PID_INFO, getConfiguration().getProxyServerUrl()));
        } catch (MalformedURLException e) {
        }
        String format = String.format("%s %s", this.m_token.getType(), this.m_token.getAccessToken());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(AUTHORIZATION_KEY, format);
        hashMap.put(REQUEST_HEADER_PID_INFO_EXTRA_FLAG, REQUEST_HEADER_TRUE_VALUE);
        HttpRequest httpRequest = new HttpRequest(url);
        httpRequest.method = IHttpRequest.Method.GET;
        httpRequest.headers = hashMap;
        this.m_pidInfoRequest = Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.15
            @Override // com.ea.nimble.NetworkConnectionCallback
            public void callback(NetworkConnectionHandle networkConnectionHandle) {
                try {
                    Map<String, Object> parseBodyJSONData = NimbleIdentityUtility.parseBodyJSONData(networkConnectionHandle);
                    Date date = new Date(System.currentTimeMillis() + ((long) (AuthenticatorBase.this.getConfiguration().getExpiryInterval() * 1000.0d)));
                    NimbleIdentityPidInfo nimbleIdentityPidInfo = new NimbleIdentityPidInfo(parseBodyJSONData, date);
                    if (!Utility.validString(nimbleIdentityPidInfo.getPid())) {
                        AuthenticatorBase.this.closePidInfoUpdate(new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Fail to parse valid pid information from server response data " + parseBodyJSONData));
                    } else if (nimbleIdentityPidInfo.equals(AuthenticatorBase.this.m_pidInfo)) {
                        Log.Helper.LOGVS(AuthenticatorBase.this.TAG, "Update pid info succesfully, but nothing change.", new Object[0]);
                        AuthenticatorBase.this.m_pidInfo.setExpiryTime(date);
                        AuthenticatorBase.this.closePidInfoUpdate(null);
                    } else {
                        Log.Helper.LOGVS(AuthenticatorBase.this.TAG, "Update pid info successfully with new data.", new Object[0]);
                        NimbleIdentityUserInfo nimbleIdentityUserInfo = AuthenticatorBase.this.m_userInfo == null ? new NimbleIdentityUserInfo() : AuthenticatorBase.this.m_userInfo.clone();
                        nimbleIdentityUserInfo.setPid(nimbleIdentityPidInfo.getPid());
                        if (Utility.validString(nimbleIdentityPidInfo.getDob())) {
                            nimbleIdentityUserInfo.setDateOfBirth(nimbleIdentityPidInfo.getDob());
                        }
                        synchronized (AuthenticatorBase.this) {
                            AuthenticatorBase.this.m_pidInfo = nimbleIdentityPidInfo;
                            AuthenticatorBase.this.m_userInfo = nimbleIdentityUserInfo;
                        }
                        AuthenticatorBase.this.closePidInfoUpdate(null);
                        HashMap hashMap2 = new HashMap();
                        hashMap2.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, AuthenticatorBase.this.getAuthenticatorId());
                        hashMap2.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_PIDMAP_ID, NimbleIdentityImpl.getComponent().getPidMapInternal());
                        Utility.sendBroadcastSerializable(Global.NIMBLE_NOTIFICATION_IDENTITY_USER_INFO_UPDATE, hashMap2);
                        Utility.sendBroadcastSerializable(Global.NIMBLE_NOTIFICATION_IDENTITY_PID_INFO_UPDATE, hashMap2);
                    }
                } catch (Error e2) {
                    AuthenticatorBase.this.closePidInfoUpdate(e2);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void autoLogin();

    public void cancelAuthentication() {
        if (this.m_pidInfoRequest != null) {
            this.m_pidInfoRequest.cancel();
            this.m_pidInfoRequest = null;
        }
        if (this.m_personaRequest != null) {
            this.m_personaRequest.cancel();
            this.m_personaRequest = null;
        }
        if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING) {
            if (this.m_authenticateRequest != null) {
                this.m_authenticateRequest.cancel();
                this.m_authenticateRequest = null;
            }
            setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE);
        }
    }

    protected void cleanAtLogout(final INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        synchronized (this) {
            if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                URL url = null;
                try {
                    NimbleIdentityConfig configuration = getConfiguration();
                    url = new URL(String.format(URL_TEMPLATE_LOGOUT, configuration.getConnectServerUrl(), configuration.getClientId(), this.m_token.getAccessToken()));
                } catch (MalformedURLException e) {
                }
                HttpRequest httpRequest = new HttpRequest(url);
                httpRequest.method = IHttpRequest.Method.GET;
                Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.17
                    @Override // com.ea.nimble.NetworkConnectionCallback
                    public void callback(NetworkConnectionHandle networkConnectionHandle) {
                        if (networkConnectionHandle.getResponse().getError() != null) {
                        }
                        if (nimbleIdentityAuthenticatorCallback != null) {
                            nimbleIdentityAuthenticatorCallback.onCallback(AuthenticatorBase.this, null);
                        }
                    }
                });
                if (this.m_tokenRefreshTimer.isRunning()) {
                    this.m_tokenRefreshTimer.cancel();
                }
                if (this.m_pidInfoRefreshTimer.isRunning()) {
                    this.m_pidInfoRefreshTimer.cancel();
                }
                if (this.m_personaRefreshTimer.isRunning()) {
                    this.m_personaRefreshTimer.cancel();
                }
                this.m_token = null;
                saveToken();
                String pid = this.m_pidInfo != null ? this.m_pidInfo.getPid() : null;
                boolean z = this.m_pidInfo != null;
                this.m_pidInfo = null;
                boolean z2 = this.m_personas != null;
                this.m_personas = null;
                setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE);
                Log.Helper.LOGDS(this.TAG, "Logout of authenticator " + getAuthenticatorId(), new Object[0]);
                if (z) {
                    HashMap hashMap = new HashMap();
                    hashMap.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, getAuthenticatorId());
                    Utility.sendBroadcast(Global.NIMBLE_NOTIFICATION_IDENTITY_PID_INFO_UPDATE, hashMap);
                }
                if (z2) {
                    HashMap hashMap2 = new HashMap();
                    hashMap2.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, getAuthenticatorId());
                    Utility.sendBroadcast(Global.NIMBLE_NOTIFICATION_IDENTITY_PERSONA_INFO_UPDATE, hashMap2);
                }
                Component component = Base.getComponent(Tracking.COMPONENT_ID);
                if (!(component == null || pid == null)) {
                    ITracking iTracking = (ITracking) component;
                    HashMap hashMap3 = new HashMap();
                    hashMap3.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_PIDMAP_LOGOUT, NimbleIdentityImpl.getComponent().getPidMapInternal().toString());
                    HashMap hashMap4 = new HashMap();
                    hashMap4.put(getAuthenticatorId(), pid);
                    hashMap3.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_MAP_SOURCE, Utility.convertObjectToJSONString(hashMap4));
                    iTracking.logEvent(Tracking.NIMBLE_TRACKING_EVENT_IDENTITY_LOGOUT, hashMap3);
                }
                NimbleIdentityAuthenticationConductorHandler authenticationConductor = NimbleIdentityImpl.getComponent().getAuthenticationConductor();
                if (authenticationConductor != null) {
                    authenticationConductor.handleLogout(this);
                }
            } else if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING) {
                setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE);
                Log.Helper.LOGWS(this.TAG, "Logout of authenticator %s while state was going. Premature interruption. Check for failure.", getAuthenticatorId());
                if (nimbleIdentityAuthenticatorCallback != null) {
                    nimbleIdentityAuthenticatorCallback.onCallback(this, null);
                }
                NimbleIdentityAuthenticationConductorHandler authenticationConductor2 = NimbleIdentityImpl.getComponent().getAuthenticationConductor();
                if (authenticationConductor2 != null) {
                    authenticationConductor2.handleLogout(this);
                }
            } else {
                setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE);
            }
        }
    }

    @Override // com.ea.nimble.Component
    public void cleanup() {
        Utility.unregisterReceiver(this.m_networkChangeReceiver);
        Utility.unregisterReceiver(this.m_identityConfigChangeReceiver);
    }

    public void closeAuthentication(Error error) {
        ArrayList<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> arrayList;
        synchronized (this) {
            this.m_authenticateRequest = null;
            if (error == null) {
                Log.Helper.LOGVS(this.TAG, "Authentication succeed!", new Object[0]);
                Log.Helper.LOGWS(this.TAG, "\n\n\nAuthentication succeeded for %s\n\n\n", getAuthenticatorId());
                Component component = Base.getComponent(Tracking.COMPONENT_ID);
                if (component != null) {
                    ITracking iTracking = (ITracking) component;
                    HashMap hashMap = new HashMap();
                    HashMap hashMap2 = new HashMap();
                    HashMap<String, String> pidMapInternal = NimbleIdentityImpl.getComponent().getPidMapInternal();
                    for (String str : pidMapInternal.keySet()) {
                        if (str != null && !str.equals(getAuthenticatorId())) {
                            hashMap2.put(str, pidMapInternal.get(str));
                        }
                    }
                    hashMap.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_PIDMAP_LOGIN, Utility.convertObjectToJSONString(hashMap2));
                    HashMap hashMap3 = new HashMap();
                    NimbleIdentityPidInfo pidInfo = getPidInfo();
                    if (pidInfo != null) {
                        hashMap3.put(getAuthenticatorId(), Utility.safeString(pidInfo.getPid()));
                    } else {
                        hashMap3.put(getAuthenticatorId(), "");
                    }
                    hashMap.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_MAP_TARGET, Utility.convertObjectToJSONString(hashMap3));
                    iTracking.logEvent(Tracking.NIMBLE_TRACKING_EVENT_IDENTITY_LOGIN, hashMap);
                }
                setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS);
                if (getConfiguration().getAutoRefresh()) {
                    this.m_tokenRefreshTimer.schedule((((double) (this.m_token.getAccessTokenExpiryTime().getTime() - System.currentTimeMillis())) / 1000.0d) - EXPIRY_MARGIN_FOR_TIMER, false);
                }
                NimbleIdentityImpl component2 = NimbleIdentityImpl.getComponent();
                if (component2 != null) {
                    NimbleIdentityAuthenticationConductorHandler authenticationConductor = component2.getAuthenticationConductor();
                    if (authenticationConductor != null) {
                        authenticationConductor.handleLogin(this, this.m_autoLoginAttempt);
                    } else {
                        Log.Helper.LOGWS(this.TAG, "\n\n\n@@@@@@@@@@@@@@@@Attempting to login with no conductor registered!~!!!!!@@@@@@@@@@@@@\n\n\n", new Object[0]);
                    }
                }
            } else {
                Log.Helper.LOGES(this.TAG, "Authentication failed with error %s.", error);
                cleanAtLogout(null);
            }
            arrayList = this.m_authenticateCallbacks;
            this.m_authenticateCallbacks = new ArrayList<>();
        }
        this.m_autoLoginAttempt = false;
        Iterator<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onCallback(this, error);
        }
    }

    protected void closeUserInfoUpdate(Error error, boolean z) {
        ArrayList<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> arrayList;
        if (error == null) {
            Log.Helper.LOGVS(this.TAG, "Updating user profile succeed!", new Object[0]);
        } else {
            Log.Helper.LOGES(this.TAG, "Fail to get user profile for error %s", error);
        }
        synchronized (this) {
            if (z) {
                if (getConfiguration().getAutoRefresh() && this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                    if (error != null || this.m_userInfo.getExpiryTime() == null) {
                        this.m_userProfileRefreshTimer.schedule(60.0d, false);
                    } else {
                        this.m_userProfileRefreshTimer.schedule((((double) (this.m_userInfo.getExpiryTime().getTime() - System.currentTimeMillis())) / 1000.0d) - EXPIRY_MARGIN_FOR_TIMER, false);
                    }
                }
            }
            arrayList = this.m_userInfoCallbacks;
            this.m_userInfoCallbacks = new ArrayList<>();
        }
        Iterator<INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onCallback(this, error);
        }
    }

    public void completeMigration() {
        Log.Helper.LOGDS(this.TAG, "AuthenticatorBase complete migration called - has no implementation", new Object[0]);
    }

    public void enableAutoRefresh(boolean z) {
        synchronized (this) {
            if (!z) {
                if (this.m_tokenRefreshTimer.isRunning()) {
                    this.m_tokenRefreshTimer.cancel();
                }
                if (this.m_pidInfoRefreshTimer.isRunning()) {
                    this.m_pidInfoRefreshTimer.cancel();
                }
                if (this.m_personaRefreshTimer.isRunning()) {
                    this.m_personaRefreshTimer.cancel();
                }
            } else if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                if (!this.m_tokenRefreshTimer.isRunning() && this.m_authenticateRequest == null && this.m_token != null) {
                    this.m_tokenRefreshTimer.schedule((((double) (this.m_token.getAccessTokenExpiryTime().getTime() - System.currentTimeMillis())) / 1000.0d) - EXPIRY_MARGIN_FOR_TIMER, false);
                }
                if (!(this.m_userProfileRefreshTimer.isRunning() || this.m_userInfo == null || this.m_userInfo.getExpiryTime() == null)) {
                    this.m_userProfileRefreshTimer.schedule((((double) (this.m_userInfo.getExpiryTime().getTime() - System.currentTimeMillis())) / 1000.0d) - EXPIRY_MARGIN_FOR_TIMER, false);
                }
                if (!this.m_pidInfoRefreshTimer.isRunning() && this.m_pidInfoRequest == null && this.m_pidInfo != null) {
                    this.m_pidInfoRefreshTimer.schedule((((double) (this.m_pidInfo.getExpiryTime().getTime() - System.currentTimeMillis())) / 1000.0d) - EXPIRY_MARGIN_FOR_TIMER, false);
                }
                if (!this.m_personaRefreshTimer.isRunning() && this.m_personaRequest == null && this.m_personas != null && this.m_personas.size() > 0) {
                    this.m_personaRefreshTimer.schedule((((double) (this.m_personas.get(0).getExpiryTime().getTime() - System.currentTimeMillis())) / 1000.0d) - EXPIRY_MARGIN_FOR_TIMER, false);
                }
            }
        }
    }

    protected void exchangeAuthCodeToToken(String str) {
        Log.Helper.LOGVS(this.TAG, "Use oauth code to get token.", new Object[0]);
        NimbleIdentityConfig configuration = getConfiguration();
        exchangeDataForToken(String.format(DATA_TEMPLATE_LOGIN_WITH_OAUTH_CODE, str, configuration.getClientId(), configuration.getClientSecret()));
    }

    protected void exchangeDataForToken(String str) {
        ByteArrayOutputStream byteArrayOutputStream;
        URL url = null;
        try {
            URL url2 = new URL(String.format(URL_TEMPLATE_LOGIN, getConfiguration().getConnectServerUrl()));
            try {
                byte[] bytes = str.getBytes("UTF-8");
                ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream(bytes.length);
                try {
                    byteArrayOutputStream2.write(bytes);
                    url = url2;
                    byteArrayOutputStream = byteArrayOutputStream2;
                } catch (IOException e) {
                    byteArrayOutputStream = byteArrayOutputStream2;
                    url = url2;
                }
            } catch (IOException e2) {
                url = url2;
                byteArrayOutputStream = null;
            }
        } catch (IOException e3) {
            byteArrayOutputStream = null;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(DATA_CONTENT_TYPE_KEY, DATA_CONTENT_TYPE_VALUE);
        hashMap.put(REQUEST_HEADER_REFRESH_TOKEN_EXTRA_FLAG, REQUEST_HEADER_TRUE_VALUE);
        HttpRequest httpRequest = new HttpRequest(url);
        httpRequest.method = IHttpRequest.Method.POST;
        httpRequest.headers = hashMap;
        httpRequest.data = byteArrayOutputStream;
        httpRequest.runInBackground = true;
        synchronized (this) {
            this.m_authenticateRequest = Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.12
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    AuthenticatorBase.this.onTokenResponse(networkConnectionHandle);
                }
            });
        }
    }

    public NimbleIdentityToken getAccessToken() {
        return this.m_token;
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return INimbleIdentityAuthenticator.AUTHENTICATOR_COMPONENT_PREFIX + getAuthenticatorId();
    }

    protected NimbleIdentityConfig getConfiguration() {
        return NimbleIdentityImpl.getComponent().getConfiguration();
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return this.TAG;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public NimbleIdentityPersona getPersonaByNamespace(String str, long j) {
        List<NimbleIdentityPersona> personas = getPersonas();
        if (personas == null) {
            return null;
        }
        for (NimbleIdentityPersona nimbleIdentityPersona : personas) {
            if (nimbleIdentityPersona.getPersonaId() == j) {
                if (nimbleIdentityPersona.getNamespaceName() != null && nimbleIdentityPersona.getNamespaceName().equals(str)) {
                    return nimbleIdentityPersona;
                }
                Log.Helper.LOGWS(this.TAG, "Try to get persona with persona id %d, but namespace %s doesn't match the asked for %s", Long.valueOf(nimbleIdentityPersona.getPersonaId()), nimbleIdentityPersona.getNamespaceName(), str);
                return null;
            }
        }
        return null;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public NimbleIdentityPersona getPersonaByNamespace(String str, String str2) {
        List<NimbleIdentityPersona> personas = getPersonas();
        if (personas == null) {
            return null;
        }
        for (NimbleIdentityPersona nimbleIdentityPersona : personas) {
            if (nimbleIdentityPersona.getNamespaceName() != null && nimbleIdentityPersona.getNamespaceName().equals(str) && nimbleIdentityPersona.getName() != null && nimbleIdentityPersona.getName().equals(str2)) {
                return nimbleIdentityPersona;
            }
        }
        return null;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public List<NimbleIdentityPersona> getPersonas() {
        ArrayList<NimbleIdentityPersona> arrayList;
        synchronized (this) {
            arrayList = this.m_personas;
            if (this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS && (this.m_personas == null || (this.m_personas.size() > 0 && this.m_personas.get(0).getExpiryTime().before(new Date())))) {
                updatePersonas();
            }
        }
        return arrayList;
    }

    /* JADX WARN: Code restructure failed: missing block: B:7:0x002b, code lost:
        if (r4.m_pidInfo.getExpiryTime().before(new java.util.Date()) != false) goto L_0x002e;
     */
    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public com.ea.nimble.identity.NimbleIdentityPidInfo getPidInfo() {
        /*
            r4 = this;
            r0 = r4
            com.ea.nimble.identity.NimbleIdentityPidInfo r0 = r0.m_pidInfo
            r6 = r0
            r0 = r6
            r5 = r0
            r0 = r4
            com.ea.nimble.identity.INimbleIdentityAuthenticator$NimbleIdentityAuthenticationState r0 = r0.m_state
            com.ea.nimble.identity.INimbleIdentityAuthenticator$NimbleIdentityAuthenticationState r1 = com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS
            if (r0 != r1) goto L_0x0061
            r0 = r4
            com.ea.nimble.identity.NimbleIdentityPidInfo r0 = r0.m_pidInfo
            if (r0 == 0) goto L_0x002e
            r0 = r6
            r5 = r0
            r0 = r4
            com.ea.nimble.identity.NimbleIdentityPidInfo r0 = r0.m_pidInfo
            java.util.Date r0 = r0.getExpiryTime()
            java.util.Date r1 = new java.util.Date
            r2 = r1
            r2.<init>()
            boolean r0 = r0.before(r1)
            if (r0 == 0) goto L_0x0061
        L_0x002e:
            r0 = r4
            monitor-enter(r0)
            r0 = r4
            com.ea.nimble.identity.NimbleIdentityPidInfo r0 = r0.m_pidInfo     // Catch: all -> 0x0067
            r5 = r0
            r0 = r4
            com.ea.nimble.identity.INimbleIdentityAuthenticator$NimbleIdentityAuthenticationState r0 = r0.m_state     // Catch: all -> 0x0067
            com.ea.nimble.identity.INimbleIdentityAuthenticator$NimbleIdentityAuthenticationState r1 = com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS     // Catch: all -> 0x0067
            if (r0 != r1) goto L_0x005f
            r0 = r4
            com.ea.nimble.identity.NimbleIdentityPidInfo r0 = r0.m_pidInfo     // Catch: all -> 0x0067
            if (r0 == 0) goto L_0x005a
            r0 = r4
            com.ea.nimble.identity.NimbleIdentityPidInfo r0 = r0.m_pidInfo     // Catch: all -> 0x0067
            java.util.Date r0 = r0.getExpiryTime()     // Catch: all -> 0x0067
            java.util.Date r1 = new java.util.Date     // Catch: all -> 0x0067
            r2 = r1
            r2.<init>()     // Catch: all -> 0x0067
            boolean r0 = r0.before(r1)     // Catch: all -> 0x0067
            if (r0 == 0) goto L_0x005f
        L_0x005a:
            r0 = r4
            r1 = 0
            r0.refreshPidInfo(r1)     // Catch: all -> 0x0067
        L_0x005f:
            r0 = r4
            monitor-exit(r0)     // Catch: all -> 0x0067
        L_0x0061:
            r0 = r4
            r0.savePidInfo()
            r0 = r5
            return r0
        L_0x0067:
            r5 = move-exception
            r0 = r4
            monitor-exit(r0)     // Catch: all -> 0x0067
            r0 = r5
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.identity.AuthenticatorBase.getPidInfo():com.ea.nimble.identity.NimbleIdentityPidInfo");
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState getState() {
        return this.m_state;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public NimbleIdentityUserInfo getUserInfo() {
        NimbleIdentityUserInfo nimbleIdentityUserInfo;
        synchronized (this) {
            nimbleIdentityUserInfo = this.m_userInfo;
            if (this.m_userInfo == null || (this.m_userInfo.getExpiryTime() != null && this.m_userInfo.getExpiryTime().before(new Date()))) {
                refreshUserProfile(null);
            }
            if (this.m_pidInfo == null || this.m_pidInfo.getExpiryTime().before(new Date())) {
                refreshPidInfo(null);
            }
        }
        return nimbleIdentityUserInfo;
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void refreshPersonas(final INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGDS(this.TAG, "Refreshing persona info", new Object[0]);
        prepare(new INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.9
            @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback
            public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
                if (error == null) {
                    Log.Helper.LOGVS(AuthenticatorBase.this.TAG, "Ready to refresh persona info", new Object[0]);
                    synchronized (this) {
                        if (AuthenticatorBase.this.m_personaRefreshTimer.isRunning()) {
                            AuthenticatorBase.this.m_personaRefreshTimer.cancel();
                        }
                        if (nimbleIdentityAuthenticatorCallback != null) {
                            AuthenticatorBase.this.m_personaCallbacks.add(nimbleIdentityAuthenticatorCallback);
                        }
                        AuthenticatorBase.this.updatePersonas();
                    }
                    return;
                }
                if (AuthenticatorBase.this.getConfiguration().getAutoRefresh() && AuthenticatorBase.this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                    AuthenticatorBase.this.m_personaRefreshTimer.schedule(60.0d, false);
                }
                if (nimbleIdentityAuthenticatorCallback != null) {
                    Log.Helper.LOGWS(AuthenticatorBase.this.TAG, "Persona refresh failed early because of error %s", error);
                    nimbleIdentityAuthenticatorCallback.onCallback(AuthenticatorBase.this, error);
                }
            }
        });
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void refreshPidInfo(final INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGDS(this.TAG, "Refreshing pid Info", new Object[0]);
        prepare(new INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.8
            @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback
            public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
                if (error == null) {
                    Log.Helper.LOGVS(AuthenticatorBase.this.TAG, "Ready to refresh pid info", new Object[0]);
                    synchronized (this) {
                        if (AuthenticatorBase.this.m_pidInfoRefreshTimer.isRunning()) {
                            AuthenticatorBase.this.m_pidInfoRefreshTimer.cancel();
                        }
                        if (nimbleIdentityAuthenticatorCallback != null) {
                            AuthenticatorBase.this.m_pidInfoCallbacks.add(nimbleIdentityAuthenticatorCallback);
                        }
                        AuthenticatorBase.this.updatePidInfo();
                    }
                    return;
                }
                if (AuthenticatorBase.this.getConfiguration().getAutoRefresh() && AuthenticatorBase.this.m_state == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                    AuthenticatorBase.this.m_pidInfoRefreshTimer.schedule(60.0d, false);
                }
                if (nimbleIdentityAuthenticatorCallback != null) {
                    Log.Helper.LOGWS(AuthenticatorBase.this.TAG, "Persona refresh failed early because of error %s", error);
                    nimbleIdentityAuthenticatorCallback.onCallback(AuthenticatorBase.this, error);
                }
            }
        });
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void refreshUserInfo(final INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        refreshPidInfo(new INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.7
            @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback
            public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
                if (nimbleIdentityAuthenticatorCallback == null) {
                    Log.Helper.LOGDS(AuthenticatorBase.this.TAG, "Received null callback in refreshUserInfo", new Object[0]);
                } else if (error == null) {
                    AuthenticatorBase.this.refreshUserProfile(nimbleIdentityAuthenticatorCallback);
                } else {
                    nimbleIdentityAuthenticatorCallback.onCallback(iNimbleIdentityAuthenticator, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_REFRESH_USER_INFO_FROM_PID_INFO.intValue(), "Fail to refresh user info from pid info", error));
                }
            }
        });
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void requestAccessToken(final INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback nimbleAuthenticatorAccessTokenCallback) {
        if (nimbleAuthenticatorAccessTokenCallback == null) {
            Log.Helper.LOGW(this, "requestAccessToken API called without a callback. Aborting token refresh", new Object[0]);
            return;
        }
        Log.Helper.LOGD(this, "requestAccessToken API called, proceeding with token refresh", new Object[0]);
        if (this.m_token == null) {
            nimbleAuthenticatorAccessTokenCallback.AccessTokenCallback(this, "", "", new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_UNAUTHENTICATED, "No tokens found for your authenticator"));
        } else if (!Utility.validString(this.m_token.getAccessToken())) {
            nimbleAuthenticatorAccessTokenCallback.AccessTokenCallback(this, "", "", new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_UNAUTHENTICATED, "No tokens found for your authenticator"));
        } else if (this.m_token.getAccessTokenExpiryTime().after(new Date())) {
            nimbleAuthenticatorAccessTokenCallback.AccessTokenCallback(this, this.m_token.getAccessToken(), this.m_token.getType(), null);
        } else {
            prepare(new INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.11
                @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback
                public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
                    if (error == null) {
                        nimbleAuthenticatorAccessTokenCallback.AccessTokenCallback(AuthenticatorBase.this, AuthenticatorBase.this.m_token.getAccessToken(), AuthenticatorBase.this.m_token.getType(), null);
                    } else {
                        nimbleAuthenticatorAccessTokenCallback.AccessTokenCallback(AuthenticatorBase.this, "", "", error);
                    }
                }
            });
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void requestAuthCode(final String str, final String str2, final INimbleIdentityAuthenticator.NimbleIdentityServerAuthCodeCallback nimbleIdentityServerAuthCodeCallback) {
        if (nimbleIdentityServerAuthCodeCallback == null) {
            Log.Helper.LOGWS(this.TAG, "Request server authentication oAuth code without callback, no way to get result", new Object[0]);
            return;
        }
        Log.Helper.LOGDS(this.TAG, "Request server authentication oauth code for serverId %s and scope %s", str, str2);
        prepare(new INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.10
            @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback
            public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
                if (error == null) {
                    Log.Helper.LOGVS(AuthenticatorBase.this.TAG, "Ready to request server auth code for serverId %s and scope %s", str, str2);
                    URL url = null;
                    try {
                        String format = String.format(AuthenticatorBase.URL_TEMPLATE_REQUEST_SERVER_AUTHENTICATION_OAUTH_CODE, AuthenticatorBase.this.getConfiguration().getConnectServerUrl(), str, AuthenticatorBase.this.m_token.getAccessToken());
                        String str3 = format;
                        if (Utility.validString(str2)) {
                            str3 = String.format("%s&scope=%s", format, str2);
                        }
                        url = new URL(str3);
                    } catch (MalformedURLException e) {
                    }
                    Network.getComponent().sendGetRequest(url, new HashMap<>(), new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.10.1
                        @Override // com.ea.nimble.NetworkConnectionCallback
                        public void callback(NetworkConnectionHandle networkConnectionHandle) {
                            try {
                                Map<String, Object> parseBodyJSONData = NimbleIdentityUtility.parseBodyJSONData(networkConnectionHandle);
                                String str4 = (String) parseBodyJSONData.get("code");
                                if (!Utility.validString(str4)) {
                                    Error error2 = new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, String.format("Fail to parse oAuth code from server response data %s", parseBodyJSONData));
                                    Log.Helper.LOGDS(AuthenticatorBase.this.TAG, "Request for server auth code failed for error %s", error2);
                                    nimbleIdentityServerAuthCodeCallback.onCallback(AuthenticatorBase.this, null, str, str2, error2);
                                    return;
                                }
                                Log.Helper.LOGDS(AuthenticatorBase.this.TAG, "Request for server auth code succeed with auth code: %s", str4);
                                nimbleIdentityServerAuthCodeCallback.onCallback(AuthenticatorBase.this, str4, str, str2, null);
                            } catch (Error e2) {
                                Log.Helper.LOGDS(AuthenticatorBase.this.TAG, "Request for server auth code failed for error %s", e2);
                                nimbleIdentityServerAuthCodeCallback.onCallback(AuthenticatorBase.this, null, str, str2, e2);
                            }
                        }
                    });
                    return;
                }
                Log.Helper.LOGDS(AuthenticatorBase.this.TAG, "Fail to get server authentication oauth code because of error %s", error);
                nimbleIdentityServerAuthCodeCallback.onCallback(AuthenticatorBase.this, null, str, str2, error);
            }
        });
    }

    protected void requestIdentityForFriends(String str, ArrayList<String> arrayList, final INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback nimbleIdentityFriendsIdentityInfoCallback) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        if (nimbleIdentityFriendsIdentityInfoCallback == null) {
            Log.Helper.LOGES(this.TAG, "requestIdentityForFriends called with no way to notify caller", new Object[0]);
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("pidType", str);
        hashMap.put("clientId", getConfiguration().getClientId());
        hashMap.put("values", arrayList);
        String convertObjectToJSONString = Utility.convertObjectToJSONString(hashMap);
        synchronized (this) {
            try {
                URL url = null;
                try {
                    url = new URL(String.format("%s%s", getConfiguration().getProxyServerUrl(), URL_TEMPALTE_GET_IDENTITY_INFO_FOR_FRIENDS));
                    try {
                        byte[] bytes = convertObjectToJSONString.getBytes("UTF-8");
                        byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
                        try {
                            byteArrayOutputStream.write(bytes);
                        } catch (IOException e) {
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    } catch (IOException e2) {
                        byteArrayOutputStream = null;
                    } catch (Throwable th2) {
                    }
                } catch (IOException e3) {
                    byteArrayOutputStream = null;
                }
                String format = String.format("%s %s", this.m_token.getType(), this.m_token.getAccessToken());
                HashMap<String, String> hashMap2 = new HashMap<>();
                hashMap2.put(AUTHORIZATION_KEY, format);
                hashMap2.put("Content-Type", "text/plain;charset=UTF-8");
                HttpRequest httpRequest = new HttpRequest(url);
                httpRequest.method = IHttpRequest.Method.POST;
                httpRequest.headers = hashMap2;
                httpRequest.data = byteArrayOutputStream;
                Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.AuthenticatorBase.14
                    @Override // com.ea.nimble.NetworkConnectionCallback
                    public void callback(NetworkConnectionHandle networkConnectionHandle) {
                        try {
                            nimbleIdentityFriendsIdentityInfoCallback.onCallback(AuthenticatorBase.this, NimbleIdentityUtility.parseJsonResponse(networkConnectionHandle), null);
                        } catch (Error e4) {
                            nimbleIdentityFriendsIdentityInfoCallback.onCallback(AuthenticatorBase.this, null, e4);
                        }
                    }
                });
            } catch (Throwable th3) {
            }
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator
    public void requestIdentityForFriends(ArrayList<String> arrayList, INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback nimbleIdentityFriendsIdentityInfoCallback) throws Exception {
        throw new Exception("Authenticator " + getAuthenticatorId() + " doesn't support identity information for friends");
    }

    public void restoreAuthenticator(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        if (this.m_state != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_UNAVAILABLE) {
            loadState();
            if (this.m_state != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_NONE) {
                loadToken();
                loadPidInfo();
            }
            Utility.registerReceiver(Global.NOTIFICATION_NETWORK_STATUS_CHANGE, this.m_networkChangeReceiver);
            Utility.registerReceiver("nimble.notification.identity.configuration.change", this.m_identityConfigChangeReceiver);
            resume(nimbleIdentityAuthenticatorCallback);
        }
    }

    @Override // com.ea.nimble.Component
    public void resume() {
        resume(null);
    }

    public void setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState nimbleIdentityAuthenticationState) {
        if (nimbleIdentityAuthenticationState != this.m_state) {
            this.m_state = nimbleIdentityAuthenticationState;
            saveState();
            HashMap hashMap = new HashMap();
            hashMap.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, getAuthenticatorId());
            Utility.sendBroadcastSerializable(Global.NIMBLE_NOTIFICATION_IDENTITY_AUTHENTICATION_UPDATE, hashMap);
        }
    }

    @Override // com.ea.nimble.Component
    public void suspend() {
        cancelAuthentication();
    }

    protected void updateUserProfile() {
        this.m_userInfo.setExpiryTime(null);
        closeUserInfoUpdate(null, false);
    }
}
