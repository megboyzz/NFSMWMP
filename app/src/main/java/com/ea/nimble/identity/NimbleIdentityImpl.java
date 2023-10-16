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
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentity;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import com.ea.nimble.identity.NimbleIdentityError;
import com.ea.nimble.tracking.ITracking;
import com.ea.nimble.tracking.Tracking;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityImpl.class */
public class NimbleIdentityImpl extends Component implements LogSource, INimbleIdentity {
    private static final String DATA_TEMPLATE_MIGRATE = "?client_id=%s&tuid=%s";
    private static final String REQUEST_HEADER_CONTENT_TYPE_KEY = "Content-type";
    private static final String REQUEST_HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String REQUEST_HEADER_SOURCE_AT_KEY = "X-SOURCE-ACCESS-TOKEN";
    private static final String REQUEST_HEADER_TARGET_AT_KEY = "X-TARGET-ACCESS-TOKEN";
    private static final String URL_TEMPLATE_MIGRATE = "%s/connect/migrate";
    private static final String URL_TEMPLATE_REQUEST_SERVER_AUTHENTICATION_OAUTH_CODE = "%s/connect/auth?client_id=%s&response_type=code&access_token=%s&redirect_uri=nucleus:rest";
    private INimbleIdentityAuthenticationConductor m_authenticationConductor;
    private NimbleIdentityAuthenticationConductorHandler m_authenticationConductorHandler;
    private INimbleIdentityAuthenticator m_mainAuthenticator;
    private INimbleIdentity.NimbleIdentityState m_state = INimbleIdentity.NimbleIdentityState.NIMBLE_IDENTITY_UNAVAILABLE;
    private HashMap<String, INimbleIdentityAuthenticator> m_authenticators = new HashMap<>();
    private NimbleIdentityConfig m_configuration = new NimbleIdentityConfig();
    private HashMap<String, String> m_pidMap = new HashMap<>();
    private final BroadcastReceiver m_authenticationUpdateReceiver = new BroadcastReceiver() { // from class: com.ea.nimble.identity.NimbleIdentityImpl.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NimbleIdentityImpl.this.onAuthenticatorStateChange();
        }
    };
    private final BroadcastReceiver m_pidInfoUpdateReceiver = new BroadcastReceiver() { // from class: com.ea.nimble.identity.NimbleIdentityImpl.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NimbleIdentityImpl.this.onPidInfoUpdate();
        }
    };

    private NimbleIdentityImpl() {
    }

    private String getAndValidateTokenForMigration(String str) {
        String accessToken = getAuthenticatorBaseById(str).getAccessToken().getAccessToken();
        String str2 = accessToken;
        if (!Utility.validString(accessToken)) {
            str2 = null;
        }
        return str2;
    }

    public static NimbleIdentityImpl getComponent() {
        return (NimbleIdentityImpl) Base.getComponent("com.ea.nimble.identity");
    }

    private static void initialize() {
        Base.registerComponent(new NimbleIdentityImpl(), "com.ea.nimble.identity");
    }

    private String loadMainAuthenticator() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null) {
            return persistenceForNimbleComponent.getStringValue("mainAuthenticatorId");
        }
        return null;
    }

    public void onAuthenticatorStateChange() {
        synchronized (this) {
            Iterator<INimbleIdentityAuthenticator> it = this.m_authenticators.values().iterator();
            while (true) {
                if (it.hasNext()) {
                    if (it.next().getState() == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING) {
                        setState(INimbleIdentity.NimbleIdentityState.NIMBLE_IDENTITY_AUTHENTICATING);
                        break;
                    }
                } else {
                    setState(INimbleIdentity.NimbleIdentityState.NIMBLE_IDENTITY_READY);
                    break;
                }
            }
        }
    }

    public void onPidInfoUpdate() {
        HashMap<String, String> hashMap = new HashMap<>();
        for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator : this.m_authenticators.values()) {
            NimbleIdentityPidInfo pidInfo = iNimbleIdentityAuthenticator.getPidInfo();
            if (pidInfo != null) {
                hashMap.put(iNimbleIdentityAuthenticator.getAuthenticatorId(), pidInfo.getPid());
            }
        }
        synchronized (this) {
            this.m_pidMap = hashMap;
        }
    }

    private void saveMainAuthenticator(String str) {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(getComponentId(), Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null) {
            persistenceForNimbleComponent.setValue("mainAuthenticatorId", str);
        }
    }

    private Error validateMigrationIds(String str, String str2) {
        if (str == null || str.length() <= 0) {
            Log.Helper.LOGE(this, "Source authenticator ID is either null or empty", new Object[0]);
            return new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_SOURCE_INVALID, "Source authenticator ID is either null or empty");
        } else if (str2 == null || str2.length() <= 0) {
            Log.Helper.LOGE(this, "Target authenticator ID is either null or empty", new Object[0]);
            return new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_TARGET_INVALID, "Target authenticator ID is either null or empty");
        } else if (!str.equals(Global.NIMBLE_AUTHENTICATOR_ANONYMOUS)) {
            Log.Helper.LOGE(this, "Source authenticator must be anonymous", new Object[0]);
            return new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_SOURCE_INVALID, "Source authenticator must be anonymous");
        } else if (str2.equals(Global.NIMBLE_AUTHENTICATOR_ANONYMOUS)) {
            Log.Helper.LOGE(this, "Target authenticator cannot be anonymous", new Object[0]);
            return new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_TARGET_INVALID, "Target authenticator cannot be anonymous");
        } else {
            INimbleIdentityAuthenticator authenticatorById = getAuthenticatorById(str);
            if (authenticatorById == null || authenticatorById.getState() != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                Log.Helper.LOGE(this, "Source authenticator is not logged in", new Object[0]);
                return new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_NOT_AUTHENTICATED, "Source authenticator is not logged in");
            }
            INimbleIdentityAuthenticator authenticatorById2 = getAuthenticatorById(str2);
            if (authenticatorById2 != null && authenticatorById2.getState() == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                return null;
            }
            Log.Helper.LOGE(this, "Target authenticator is not logged in", new Object[0]);
            return new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_NOT_AUTHENTICATED, "Target authenticator is not logged in");
        }
    }

    @Override // com.ea.nimble.Component
    public void cleanup() {
        Utility.unregisterReceiver(this.m_authenticationUpdateReceiver);
        Utility.unregisterReceiver(this.m_pidInfoUpdateReceiver);
    }

    public void completeMigration(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2, INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback) {
        Log.Helper.LOGD(this, "Account migration successful", new Object[0]);
        Component component = Base.getComponent(Tracking.COMPONENT_ID);
        if (component != null) {
            ITracking iTracking = (ITracking) component;
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            hashMap2.put(iNimbleIdentityAuthenticator.getAuthenticatorId(), iNimbleIdentityAuthenticator.getPidInfo().getPid());
            hashMap.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_MAP_SOURCE, Utility.convertObjectToJSONString(hashMap2));
            HashMap hashMap3 = new HashMap();
            hashMap3.put(iNimbleIdentityAuthenticator2.getAuthenticatorId(), iNimbleIdentityAuthenticator2.getPidInfo().getPid());
            hashMap.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_MAP_TARGET, Utility.convertObjectToJSONString(hashMap3));
            hashMap.put(Tracking.NIMBLE_TRACKING_KEY_MIGRATION_GAME_TRIGGERED, Global.NOTIFICATION_DICTIONARY_RESULT_FAIL);
            iTracking.logEvent(Tracking.NIMBLE_TRACKING_EVENT_IDENTITY_MIGRATION, hashMap);
        }
        setMainAuthenticator(iNimbleIdentityAuthenticator2);
        getAuthenticatorBaseById(iNimbleIdentityAuthenticator.getAuthenticatorId()).completeMigration();
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.identity", Persistence.Storage.DOCUMENT);
        persistenceForNimbleComponent.setValue(INimbleIdentity.MIGRATION_PERSISTENCE_ID, null);
        persistenceForNimbleComponent.synchronize();
        if (nimbleIdentityAuthenticatorCallback != null) {
            nimbleIdentityAuthenticatorCallback.onCallback(iNimbleIdentityAuthenticator2, null);
        }
    }

    public NimbleIdentityAuthenticationConductorHandler getAuthenticationConductor() {
        return this.m_authenticationConductorHandler;
    }

    public AuthenticatorBase getAuthenticatorBaseById(String str) {
        INimbleIdentityAuthenticator iNimbleIdentityAuthenticator = this.m_authenticators.get(str);
        if (iNimbleIdentityAuthenticator == null || !(iNimbleIdentityAuthenticator instanceof AuthenticatorBase)) {
            return null;
        }
        return (AuthenticatorBase) iNimbleIdentityAuthenticator;
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public INimbleIdentityAuthenticator getAuthenticatorById(String str) {
        INimbleIdentityAuthenticator iNimbleIdentityAuthenticator = null;
        if (this.m_authenticationConductor != null) {
            iNimbleIdentityAuthenticator = this.m_authenticators.get(str);
        }
        return iNimbleIdentityAuthenticator;
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public List<INimbleIdentityAuthenticator> getAuthenticators() {
        if (this.m_authenticationConductor == null) {
            return null;
        }
        return new LinkedList(this.m_authenticators.values());
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public boolean getAutoRefreshFlag() {
        return this.m_configuration.getAutoRefresh();
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return "com.ea.nimble.identity";
    }

    public NimbleIdentityConfig getConfiguration() {
        return this.m_configuration;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "Identity";
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public List<INimbleIdentityAuthenticator> getLoggedInAuthenticators() {
        ArrayList arrayList = new ArrayList();
        if (this.m_authenticationConductor != null) {
            synchronized (this) {
                if (this.m_authenticators != null && this.m_authenticators.size() > 0) {
                    for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator : this.m_authenticators.values()) {
                        if (iNimbleIdentityAuthenticator.getState() == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                            arrayList.add(iNimbleIdentityAuthenticator);
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public INimbleIdentityAuthenticator getMainAuthenticator() {
        return this.m_mainAuthenticator;
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public Map<String, String> getPidMap() {
        if (this.m_authenticationConductor == null) {
            return null;
        }
        return getPidMapInternal();
    }

    public HashMap<String, String> getPidMapInternal() {
        return this.m_pidMap;
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public INimbleIdentity.NimbleIdentityState getState() {
        return this.m_state;
    }

    public void handlePendingMigration(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback, String str, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2) {
        makeMigrationNetworkCall(nimbleIdentityAuthenticatorCallback, str, iNimbleIdentityAuthenticator, getAndValidateTokenForMigration(iNimbleIdentityAuthenticator.getAuthenticatorId()), iNimbleIdentityAuthenticator2, getAndValidateTokenForMigration(iNimbleIdentityAuthenticator2.getAuthenticatorId()));
    }

    public void highlight(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        Log.Helper.LOGD(this, "Game wants to high light the just logged in authenticator", new Object[0]);
        setMainAuthenticator(iNimbleIdentityAuthenticator);
    }

    void makeMigrationNetworkCall(final INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback, String str, final INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str2, final INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2, String str3) {
        ByteArrayOutputStream byteArrayOutputStream;
        Log.Helper.LOGD(this, "Starting migration network call", new Object[0]);
        String format = String.format(DATA_TEMPLATE_MIGRATE, this.m_configuration.getClientId(), str);
        URL url = null;
        try {
            String serverUrlWithKey = SynergyEnvironment.getComponent().getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_IDENTITY_CONNECT);
            String str4 = serverUrlWithKey;
            if (serverUrlWithKey.substring(serverUrlWithKey.length() - 1).equals("/")) {
                str4 = serverUrlWithKey.substring(0, serverUrlWithKey.length() - 1);
            }
            url = new URL(String.format(URL_TEMPLATE_MIGRATE, str4) + format);
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
        hashMap.put(REQUEST_HEADER_CONTENT_TYPE_KEY, REQUEST_HEADER_CONTENT_TYPE_VALUE);
        hashMap.put(REQUEST_HEADER_SOURCE_AT_KEY, str3);
        hashMap.put(REQUEST_HEADER_TARGET_AT_KEY, str2);
        HttpRequest httpRequest = new HttpRequest(url);
        httpRequest.method = IHttpRequest.Method.POST;
        httpRequest.headers = hashMap;
        httpRequest.data = byteArrayOutputStream;
        Network.getComponent().sendRequest(httpRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.NimbleIdentityImpl.5
            @Override // com.ea.nimble.NetworkConnectionCallback
            public void callback(NetworkConnectionHandle networkConnectionHandle) {
                Exception error = networkConnectionHandle.getResponse().getError();
                if (error != null) {
                    Log.Helper.LOGE(this, "Request for account migration failed for error " + error, new Object[0]);
                    NimbleIdentityImpl.this.getAuthenticatorBaseById(iNimbleIdentityAuthenticator.getAuthenticatorId()).logout(null);
                    Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(NimbleIdentityImpl.this.getComponentId(), Persistence.Storage.DOCUMENT);
                    persistenceForNimbleComponent.setValue(INimbleIdentity.MIGRATION_PERSISTENCE_ID, null);
                    persistenceForNimbleComponent.synchronize();
                    nimbleIdentityAuthenticatorCallback.onCallback(null, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_FAILED.intValue(), error.getMessage(), error));
                    return;
                }
                NimbleIdentityImpl.this.completeMigration(iNimbleIdentityAuthenticator2, iNimbleIdentityAuthenticator, nimbleIdentityAuthenticatorCallback);
            }
        });
    }

    public void migrate(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2) {
        if (nimbleIdentityAuthenticatorCallback == null) {
            Log.Helper.LOGW(this, "Request account migration without callback, no way to notify caller", new Object[0]);
            return;
        }
        Log.Helper.LOGI(this, "Request account migration", new Object[0]);
        Component component = Base.getComponent(Tracking.COMPONENT_ID);
        if (component != null) {
            ITracking iTracking = (ITracking) component;
            HashMap hashMap = new HashMap();
            HashMap hashMap2 = new HashMap();
            hashMap2.put(iNimbleIdentityAuthenticator2.getAuthenticatorId(), iNimbleIdentityAuthenticator2.getPidInfo().getPid());
            hashMap.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_MAP_SOURCE, Utility.convertObjectToJSONString(hashMap2));
            HashMap hashMap3 = new HashMap();
            hashMap3.put(iNimbleIdentityAuthenticator.getAuthenticatorId(), iNimbleIdentityAuthenticator.getPidInfo().getPid());
            hashMap.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_MAP_TARGET, Utility.convertObjectToJSONString(hashMap3));
            hashMap.put(Tracking.NIMBLE_TRACKING_KEY_MIGRATION_GAME_TRIGGERED, Global.NOTIFICATION_DICTIONARY_RESULT_FAIL);
            iTracking.logEvent(Tracking.NIMBLE_TRACKING_EVENT_IDENTITY_MIGRATION_STARTED, hashMap);
        }
        String authenticatorId = iNimbleIdentityAuthenticator2.getAuthenticatorId();
        String authenticatorId2 = iNimbleIdentityAuthenticator.getAuthenticatorId();
        Error validateMigrationIds = validateMigrationIds(authenticatorId, authenticatorId2);
        if (validateMigrationIds != null) {
            nimbleIdentityAuthenticatorCallback.onCallback(null, validateMigrationIds);
        }
        String andValidateTokenForMigration = getAndValidateTokenForMigration(authenticatorId);
        String andValidateTokenForMigration2 = getAndValidateTokenForMigration(authenticatorId2);
        if (andValidateTokenForMigration == null || andValidateTokenForMigration2 == null) {
            NimbleIdentityError nimbleIdentityError = new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_NO_ACCESS_TOKENS, "Access token is empty or invalid");
            Log.Helper.LOGE(this, "Request for account migration failed for error " + nimbleIdentityError, new Object[0]);
            nimbleIdentityAuthenticatorCallback.onCallback(null, nimbleIdentityError);
        }
        if (!Utility.validString(this.m_configuration.getClientId())) {
            NimbleIdentityError nimbleIdentityError2 = new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_BAD_CLIENT_ID, "ClientId is empty or invalid");
            Log.Helper.LOGE(this, "Request for account migration failed for error " + nimbleIdentityError2, new Object[0]);
            nimbleIdentityAuthenticatorCallback.onCallback(null, nimbleIdentityError2);
        }
        String uuid = UUID.randomUUID().toString();
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.identity", Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent == null) {
            Log.Helper.LOGW(this, "Attempted to save pending migration data but persistence was not available. Not saving.", new Object[0]);
        } else {
            persistenceForNimbleComponent.setValue(INimbleIdentity.MIGRATION_PERSISTENCE_ID, new NimbleIdentityMigrationObject(uuid, authenticatorId2, iNimbleIdentityAuthenticator.getPidInfo().getPid(), authenticatorId, iNimbleIdentityAuthenticator2.getPidInfo().getPid()));
            persistenceForNimbleComponent.synchronize();
            Log.Helper.LOGV(this, "Migration object saved to persistence", new Object[0]);
        }
        makeMigrationNetworkCall(nimbleIdentityAuthenticatorCallback, uuid, iNimbleIdentityAuthenticator, andValidateTokenForMigration2, iNimbleIdentityAuthenticator2, andValidateTokenForMigration);
    }

    void migrationFailed(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
        Log.Helper.LOGD(this, "Migration failed for %s.", iNimbleIdentityAuthenticator);
        iNimbleIdentityAuthenticator.logout(nimbleIdentityAuthenticatorCallback);
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public void requestServerAuthCodeForLegacyOriginToken(String str, final String str2, final String str3, final INimbleIdentityAuthenticator.NimbleIdentityServerAuthCodeCallback nimbleIdentityServerAuthCodeCallback) {
        if (nimbleIdentityServerAuthCodeCallback == null) {
            Log.Helper.LOGW(this, "Request server authentication oAuth code without callback, no way to get result", new Object[0]);
            return;
        }
        Log.Helper.LOGV(this, "Request server authentication oauth code for legacy origin ocs token", new Object[0]);
        URL url = null;
        try {
            String format = String.format(URL_TEMPLATE_REQUEST_SERVER_AUTHENTICATION_OAUTH_CODE, this.m_configuration.getConnectServerUrl(), str2, str);
            String str4 = format;
            if (Utility.validString(str3)) {
                str4 = format + String.format("&scope=%s", str3);
            }
            url = new URL(str4);
        } catch (MalformedURLException e) {
        }
        Network.getComponent().sendGetRequest(url, new HashMap<>(), new NetworkConnectionCallback() { // from class: com.ea.nimble.identity.NimbleIdentityImpl.4
            @Override // com.ea.nimble.NetworkConnectionCallback
            public void callback(NetworkConnectionHandle networkConnectionHandle) {
                try {
                    Map<String, Object> parseBodyJSONData = NimbleIdentityUtility.parseBodyJSONData(networkConnectionHandle);
                    String str5 = (String) parseBodyJSONData.get("code");
                    if (!Utility.validString(str5)) {
                        Error error = new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Fail to parse oAuth code from server response data " + parseBodyJSONData);
                        Log.Helper.LOGD(NimbleIdentityImpl.this, "Request for legacy server auth code failed for error " + error, new Object[0]);
                        nimbleIdentityServerAuthCodeCallback.onCallback(null, null, str2, str3, error);
                        return;
                    }
                    Log.Helper.LOGD(this, "Request for server auth code succeed with auth code: " + str5, new Object[0]);
                    nimbleIdentityServerAuthCodeCallback.onCallback(null, str5, str2, str3, null);
                } catch (Error e2) {
                    Log.Helper.LOGD(NimbleIdentityImpl.this, "Request for legacy server auth code failed for error " + e2, new Object[0]);
                    nimbleIdentityServerAuthCodeCallback.onCallback(null, null, str2, str3, e2);
                }
            }
        });
    }

    public void resolveLogout(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        if (iNimbleIdentityAuthenticator != null) {
            setMainAuthenticator(iNimbleIdentityAuthenticator);
        }
    }

    @Override // com.ea.nimble.Component
    public void restore() {
        this.m_configuration.initialize();
        this.m_authenticators.clear();
        Component[] componentList = Base.getComponentList(INimbleIdentityAuthenticator.AUTHENTICATOR_COMPONENT_PREFIX);
        Utility.registerReceiver(Global.NIMBLE_NOTIFICATION_IDENTITY_AUTHENTICATION_UPDATE, this.m_authenticationUpdateReceiver);
        Utility.registerReceiver(Global.NIMBLE_NOTIFICATION_IDENTITY_PID_INFO_UPDATE, this.m_pidInfoUpdateReceiver);
        for (Component component : componentList) {
            if (!(component instanceof INimbleIdentityAuthenticator)) {
                Log.Helper.LOGW(this, "Invalid authenticator %s", component.getComponentId());
            } else {
                INimbleIdentityAuthenticator iNimbleIdentityAuthenticator = (INimbleIdentityAuthenticator) component;
                this.m_authenticators.put(iNimbleIdentityAuthenticator.getAuthenticatorId(), iNimbleIdentityAuthenticator);
            }
        }
        String loadMainAuthenticator = loadMainAuthenticator();
        String str = loadMainAuthenticator;
        if (loadMainAuthenticator == null) {
            Log.Helper.LOGD(this, "No existing main authenticator. Will log in anonymous authenticator", new Object[0]);
            str = Global.NIMBLE_AUTHENTICATOR_ANONYMOUS;
        }
        Log.Helper.LOGD(this, "Main authenticator is " + str, new Object[0]);
        AuthenticatorBase authenticatorBaseById = getAuthenticatorBaseById(str);
        authenticatorBaseById.setState(INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_OFFLINE);
        authenticatorBaseById.restoreAuthenticator(new INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback() { // from class: com.ea.nimble.identity.NimbleIdentityImpl.3
            @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback
            public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2, Error error) {
                for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator3 : NimbleIdentityImpl.this.m_authenticators.values()) {
                    AuthenticatorBase authenticatorBase = (AuthenticatorBase) iNimbleIdentityAuthenticator3;
                    if (authenticatorBase != iNimbleIdentityAuthenticator2) {
                        authenticatorBase.restoreAuthenticator(null);
                    }
                }
            }
        });
        for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2 : this.m_authenticators.values()) {
            if (iNimbleIdentityAuthenticator2.getState() == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_GOING) {
                this.m_state = INimbleIdentity.NimbleIdentityState.NIMBLE_IDENTITY_AUTHENTICATING;
            }
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public void setAuthenticationConductor(INimbleIdentityAuthenticationConductor iNimbleIdentityAuthenticationConductor) {
        if (this.m_authenticationConductor != null) {
            Log.Helper.LOGW(this, "Attempting setAuthenticationConductor when we already have one. Unsupported. Ignoring.", new Object[0]);
        } else if (iNimbleIdentityAuthenticationConductor == null) {
            Log.Helper.LOGW(this, "Attempting to setAuthenticationConductor with a null conductor.", new Object[0]);
        } else if (this.m_authenticationConductor != iNimbleIdentityAuthenticationConductor) {
            this.m_authenticationConductor = iNimbleIdentityAuthenticationConductor;
            if (iNimbleIdentityAuthenticationConductor instanceof INimbleIdentityGenericAuthenticationConductor) {
                Log.Helper.LOGD(this, "Setting the generic auth conductor as our conductor.", new Object[0]);
                this.m_authenticationConductorHandler = new NimbleIdentityGenericAuthenticationConductorHandler(iNimbleIdentityAuthenticationConductor);
            } else if (iNimbleIdentityAuthenticationConductor instanceof INimbleIdentityMigrationAuthenticationConductor) {
                Log.Helper.LOGD(this, "Setting the migration auth conductor as our conductor.", new Object[0]);
                this.m_authenticationConductorHandler = new NimbleIdentityMigrationAuthenticationConductorHandler(iNimbleIdentityAuthenticationConductor);
            }
            for (INimbleIdentityAuthenticator iNimbleIdentityAuthenticator : getAuthenticators()) {
                if (iNimbleIdentityAuthenticator != null) {
                    ((AuthenticatorBase) iNimbleIdentityAuthenticator).resume();
                }
            }
        }
    }

    @Override // com.ea.nimble.identity.INimbleIdentity
    public void setAutoRefreshFlag(boolean z) {
        this.m_configuration.setAutoRefresh(z);
        Iterator<INimbleIdentityAuthenticator> it = this.m_authenticators.values().iterator();
        while (it.hasNext()) {
            ((AuthenticatorBase) it.next()).enableAutoRefresh(z);
        }
    }

    public void setMainAuthenticator(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator) {
        if (iNimbleIdentityAuthenticator == null) {
            Log.Helper.LOGW(this, "Failed to set mainAuthenticator with new authenticator because given authenticator is null.", new Object[0]);
        } else if (iNimbleIdentityAuthenticator == this.m_mainAuthenticator) {
            Log.Helper.LOGV(this, "Skipping setMainAuthenticator because the given authenticator is the same as the previous one.", new Object[0]);
        } else {
            Log.Helper.LOGD(this, "Setting mainAuthenticator to: " + iNimbleIdentityAuthenticator.getAuthenticatorId(), new Object[0]);
            this.m_mainAuthenticator = iNimbleIdentityAuthenticator;
            HashMap hashMap = new HashMap();
            String authenticatorId = this.m_mainAuthenticator.getAuthenticatorId();
            saveMainAuthenticator(authenticatorId);
            NimbleIdentityPidInfo pidInfo = this.m_mainAuthenticator.getPidInfo();
            if (pidInfo != null) {
                hashMap.put(authenticatorId, pidInfo.getPid());
            } else {
                hashMap.put(authenticatorId, "");
            }
            HashMap hashMap2 = new HashMap();
            hashMap2.put(Tracking.NIMBLE_TRACKING_KEY_IDENTITY_MAP_SOURCE, hashMap);
            Utility.sendBroadcastSerializable(Global.NIMBLE_NOTIFICATION_IDENTITY_MAIN_AUTHENTICATOR_CHANGE, hashMap2);
        }
    }

    public void setState(INimbleIdentity.NimbleIdentityState nimbleIdentityState) {
        if (this.m_state != nimbleIdentityState) {
            this.m_state = nimbleIdentityState;
            Utility.sendBroadcast(INimbleIdentity.NIMBLE_NOTIFICATION_IDENTITY_UPDATE, null);
        }
    }

    public void switchAuthenticators(INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback nimbleIdentityAuthenticatorCallback, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, INimbleIdentityAuthenticator iNimbleIdentityAuthenticator2) {
        if (nimbleIdentityAuthenticatorCallback == null) {
            Log.Helper.LOGW(this, "Requested account switching without callback. No way to notify caller. Aborting...", new Object[0]);
        } else if (iNimbleIdentityAuthenticator2 == null) {
            nimbleIdentityAuthenticatorCallback.onCallback(null, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_SOURCE_INVALID, "Attempted to switchAuthenticators but source authenticator was null"));
        } else if (iNimbleIdentityAuthenticator == null) {
            nimbleIdentityAuthenticatorCallback.onCallback(null, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_TARGET_INVALID, "Attempted to switchAuthenticators but target authenticator was null"));
        } else if (iNimbleIdentityAuthenticator2.getAuthenticatorId().equals(iNimbleIdentityAuthenticator.getAuthenticatorId())) {
            nimbleIdentityAuthenticatorCallback.onCallback(null, new NimbleIdentityError(NimbleIdentityError.NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_MIGRATION_FAILED, "Attempted to switch from one authenticator to itself. Your from and your to authenticators must be different."));
        } else {
            setMainAuthenticator(iNimbleIdentityAuthenticator);
            if (iNimbleIdentityAuthenticator2.getAuthenticatorId() == Global.NIMBLE_AUTHENTICATOR_ANONYMOUS) {
                ((AuthenticatorAnonymous) iNimbleIdentityAuthenticator2).logoutInternal(nimbleIdentityAuthenticatorCallback);
            } else {
                nimbleIdentityAuthenticatorCallback.onCallback(iNimbleIdentityAuthenticator, null);
            }
        }
    }
}
