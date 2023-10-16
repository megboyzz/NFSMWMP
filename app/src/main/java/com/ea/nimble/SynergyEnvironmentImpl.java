package com.ea.nimble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import java.io.Serializable;
import java.util.HashMap;

/* loaded from: stdlib.jar:com/ea/nimble/SynergyEnvironmentImpl.class */
public class SynergyEnvironmentImpl extends Component implements ISynergyEnvironment, LogSource {
    private static final String PERSISTENCE_DATA_ID = "environmentData";
    public static final int SYNERGY_APP_VERSION_OK = 0;
    public static final int SYNERGY_APP_VERSION_UPDATE_RECOMMENDED = 1;
    public static final int SYNERGY_APP_VERSION_UPDATE_REQUIRED = 2;
    private static final String SYNERGY_INT_SERVER_URL = "https://director-int.sn.eamobile.com";
    private static final String SYNERGY_LIVE_SERVER_URL = "https://syn-dir.sn.eamobile.com";
    private static final String SYNERGY_STAGE_SERVER_URL = "https://director-stage.sn.eamobile.com";
    public static final double SYNERGY_UPDATE_RATE_LIMIT_PERIOD_IN_SECONDS = 60.0d;
    public static final double SYNERGY_UPDATE_REFRESH_PERIOD_IN_SECONDS = 300.0d;
    private BaseCore m_core;
    private EnvironmentDataContainer m_environmentDataContainer;
    private EnvironmentDataContainer m_previousValidEnvironmentDataContainer;
    private Long m_synergyEnvironmentUpdateRateLimitTriggerTimestamp;
    private SynergyEnvironmentUpdater m_synergyStartupObject;
    private BroadcastReceiver m_networkStatusChangeReceiver = null;
    private boolean m_dataLoadedOnComponentSetup = false;

    /* renamed from: com.ea.nimble.SynergyEnvironmentImpl$3  reason: invalid class name */
    /* loaded from: stdlib.jar:com/ea/nimble/SynergyEnvironmentImpl$3.class */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$ea$nimble$NimbleConfiguration = new int[NimbleConfiguration.values().length];

        static {
            try {
                $SwitchMap$com$ea$nimble$NimbleConfiguration[NimbleConfiguration.INTEGRATION.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$ea$nimble$NimbleConfiguration[NimbleConfiguration.STAGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$ea$nimble$NimbleConfiguration[NimbleConfiguration.LIVE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$ea$nimble$NimbleConfiguration[NimbleConfiguration.CUSTOMIZED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SynergyEnvironmentImpl(BaseCore baseCore) {
        this.m_core = baseCore;
    }

    private void clearSynergyEnvironmentUpdateRateLimiting() {
        this.m_synergyEnvironmentUpdateRateLimitTriggerTimestamp = null;
    }

    private boolean isInSynergyEnvironmentUpdateRateLimitingPeriod() {
        return this.m_synergyEnvironmentUpdateRateLimitTriggerTimestamp != null && ((double) (System.currentTimeMillis() - this.m_synergyEnvironmentUpdateRateLimitTriggerTimestamp.longValue())) <= 60000.0d;
    }

    private boolean restoreEnvironmentDataFromPersistent(boolean z) {
        boolean z2 = true;
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(SynergyEnvironment.COMPONENT_ID, Persistence.Storage.CACHE);
        if (persistenceForNimbleComponent != null) {
            Serializable value = persistenceForNimbleComponent.getValue(PERSISTENCE_DATA_ID);
            if (value == null) {
                Log.Helper.LOGD(this, "Environment persistence data value not found in persistence object. Probably first install.", new Object[0]);
            } else {
                try {
                    this.m_environmentDataContainer = (EnvironmentDataContainer) value;
                    Log.Helper.LOGD(this, "Restored environment data from persistent. Restored data timestamp, %s", this.m_environmentDataContainer.getMostRecentDirectorResponseTimestamp());
                    if (this.m_environmentDataContainer.getEADeviceId() == null) {
                        this.m_environmentDataContainer.setEADeviceId(EASPDataLoader.loadEADeviceId());
                    }
                    if (!z) {
                        Utility.sendBroadcast(SynergyEnvironment.NOTIFICATION_RESTORED_FROM_PERSISTENT, null);
                        return true;
                    }
                } catch (ClassCastException e) {
                    Log.Helper.LOGE(this, "Environment persistence data value is not the expected type.", new Object[0]);
                }
                return z2;
            }
        } else {
            Log.Helper.LOGE(this, "Could not get environment persistence object to restore from", new Object[0]);
        }
        this.m_environmentDataContainer = null;
        z2 = false;
        return z2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveEnvironmentDataToPersistent() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(SynergyEnvironment.COMPONENT_ID, Persistence.Storage.CACHE);
        if (persistenceForNimbleComponent != null) {
            Log.Helper.LOGD(this, "Saving environment data to persistent.", new Object[0]);
            persistenceForNimbleComponent.setValue(PERSISTENCE_DATA_ID, this.m_environmentDataContainer);
            persistenceForNimbleComponent.synchronize();
            return;
        }
        Log.Helper.LOGE(this, "Could not get environment persistence object to save to.", new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSynergyEnvironmentUpdate() {
        if (isUpdateInProgress()) {
            Log.Helper.LOGD(this, "Attempt made to start Synergy environment update while a previous one is active. Exiting.", new Object[0]);
        } else if (Network.getComponent().getStatus() == Network.Status.OK) {
            this.m_synergyStartupObject = new SynergyEnvironmentUpdater(this.m_core);
            this.m_previousValidEnvironmentDataContainer = this.m_environmentDataContainer;
            HashMap hashMap = new HashMap();
            hashMap.put(Global.NOTIFICATION_DICTIONARY_KEY_RESULT, Global.NOTIFICATION_DICTIONARY_RESULT_SUCCESS);
            Utility.sendBroadcast(SynergyEnvironment.NOTIFICATION_STARTUP_REQUESTS_STARTED, hashMap);
            this.m_synergyStartupObject.startSynergyStartupSequence(this.m_previousValidEnvironmentDataContainer, new SynergyEnvironmentUpdater.CompletionCallback() { // from class: com.ea.nimble.SynergyEnvironmentImpl.2
                @Override // com.ea.nimble.SynergyEnvironmentUpdater.CompletionCallback
                public void callback(Exception exc) {
                    if (exc != null) {
                        Log.Helper.LOGE(this, "StartupError(%s)", exc);
                        if (exc instanceof Error) {
                            Error error = (Error) exc;
                            if (error.isError(Error.Code.SYNERGY_GET_DIRECTION_TIMEOUT) || error.isError(Error.Code.SYNERGY_SERVER_FULL)) {
                                Log.Helper.LOGD(this, "GetDirection request timed out or ServerUnavailable signal received. Start rate limiting of /getDirection call.", new Object[0]);
                                SynergyEnvironmentImpl.this.startSynergyEnvironmentUpdateRateLimiting();
                            }
                        } else if (SynergyEnvironmentImpl.this.m_synergyStartupObject == null || SynergyEnvironmentImpl.this.m_synergyStartupObject.getEnvironmentDataContainer() == null) {
                            Log.Helper.LOGD(this, "Synergy Environment Update object or dataContainer null at callback. More than one update was being peroformed", new Object[0]);
                        }
                        HashMap hashMap2 = new HashMap();
                        hashMap2.put(Global.NOTIFICATION_DICTIONARY_KEY_RESULT, Global.NOTIFICATION_DICTIONARY_RESULT_FAIL);
                        hashMap2.put("error", exc.toString());
                        if (!ApplicationEnvironment.isMainApplicationRunning() || ApplicationEnvironment.getCurrentActivity() == null) {
                            Log.Helper.LOGI(this, "App is not running in forground, discard the NOTIFICATION_STARTUP_REQUESTS_FINISHED notification", new Object[0]);
                        } else {
                            Log.Helper.LOGD(this, "App is running in forground, send the NOTIFICATION_STARTUP_REQUESTS_FINISHED notification", new Object[0]);
                            Utility.sendBroadcast(SynergyEnvironment.NOTIFICATION_STARTUP_REQUESTS_FINISHED, hashMap2);
                        }
                    } else if (SynergyEnvironmentImpl.this.m_synergyStartupObject == null || SynergyEnvironmentImpl.this.m_synergyStartupObject.getEnvironmentDataContainer() == null) {
                        Log.Helper.LOGD(this, "Synergy Environment Update object or dataContainer null at callback. More than one update was being peroformed", new Object[0]);
                    } else {
                        SynergyEnvironmentImpl.this.m_environmentDataContainer = SynergyEnvironmentImpl.this.m_synergyStartupObject.getEnvironmentDataContainer();
                        SynergyEnvironmentImpl.this.saveEnvironmentDataToPersistent();
                        if (SynergyEnvironmentImpl.this.m_environmentDataContainer.getKeysOfDifferences(SynergyEnvironmentImpl.this.m_previousValidEnvironmentDataContainer) != null) {
                            Utility.sendBroadcast(SynergyEnvironment.NOTIFICATION_STARTUP_ENVIRONMENT_DATA_CHANGED, null);
                        }
                        HashMap hashMap3 = new HashMap();
                        hashMap3.put(Global.NOTIFICATION_DICTIONARY_KEY_RESULT, Global.NOTIFICATION_DICTIONARY_RESULT_SUCCESS);
                        if (!ApplicationEnvironment.isMainApplicationRunning() || ApplicationEnvironment.getCurrentActivity() == null) {
                            Log.Helper.LOGI(this, "App is not running in forground, discard the NOTIFICATION_STARTUP_REQUESTS_FINISHED notification", new Object[0]);
                        } else {
                            Log.Helper.LOGD(this, "App is running in forground, send the NOTIFICATION_STARTUP_REQUESTS_FINISHED notification", new Object[0]);
                            Utility.sendBroadcast(SynergyEnvironment.NOTIFICATION_STARTUP_REQUESTS_FINISHED, hashMap3);
                        }
                    }
                    SynergyEnvironmentImpl.this.m_synergyStartupObject = null;
                }
            });
        } else if (this.m_networkStatusChangeReceiver == null) {
            this.m_networkStatusChangeReceiver = new BroadcastReceiver() { // from class: com.ea.nimble.SynergyEnvironmentImpl.1
                @Override // android.content.BroadcastReceiver
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(Global.NOTIFICATION_NETWORK_STATUS_CHANGE) && Network.getComponent().getStatus() == Network.Status.OK) {
                        Log.Helper.LOGD(this, "Network restored. Starting Synergy environment update.", new Object[0]);
                        Utility.unregisterReceiver(SynergyEnvironmentImpl.this.m_networkStatusChangeReceiver);
                        SynergyEnvironmentImpl.this.m_networkStatusChangeReceiver = null;
                        SynergyEnvironmentImpl.this.startSynergyEnvironmentUpdate();
                    }
                }
            };
            Log.Helper.LOGD(this, "Network not available to perform environment update. Setting receiver to listen for network status change.", new Object[0]);
            Utility.registerReceiver(Global.NOTIFICATION_NETWORK_STATUS_CHANGE, this.m_networkStatusChangeReceiver);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSynergyEnvironmentUpdateRateLimiting() {
        this.m_synergyEnvironmentUpdateRateLimitTriggerTimestamp = Long.valueOf(System.currentTimeMillis());
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public Error checkAndInitiateSynergyEnvironmentUpdate() {
        if (isUpdateInProgress()) {
            return new Error(Error.Code.SYNERGY_ENVIRONMENT_UPDATE_FAILURE, "Update in progress.");
        }
        if (this.m_environmentDataContainer != null && this.m_environmentDataContainer.getMostRecentDirectorResponseTimestamp() != null) {
            return new Error(Error.Code.SYNERGY_ENVIRONMENT_UPDATE_FAILURE, "Environment data already cached.");
        }
        if (isInSynergyEnvironmentUpdateRateLimitingPeriod()) {
            Log.Helper.LOGD(this, "Attempt to re-initiate Synergy environment update blocked by rate limiting. %.2f seconds of rate limiting left", Double.valueOf(60.0d - (((double) (System.currentTimeMillis() - this.m_synergyEnvironmentUpdateRateLimitTriggerTimestamp.longValue())) / 1000.0d)));
            return new Error(Error.Code.SYNERGY_ENVIRONMENT_UPDATE_FAILURE, "Synergy environment update rate limit in effect.");
        }
        startSynergyEnvironmentUpdate();
        return null;
    }

    @Override // com.ea.nimble.Component
    public void cleanup() {
        Log.Helper.LOGD(this, "cleanup", new Object[0]);
        if (this.m_synergyStartupObject != null) {
            this.m_synergyStartupObject.cancel();
            this.m_synergyStartupObject = null;
        }
        if (this.m_networkStatusChangeReceiver != null) {
            Utility.unregisterReceiver(this.m_networkStatusChangeReceiver);
            this.m_networkStatusChangeReceiver = null;
        }
        saveEnvironmentDataToPersistent();
        this.m_environmentDataContainer = null;
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return SynergyEnvironment.COMPONENT_ID;
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getEADeviceId() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getEADeviceId();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getEAHardwareId() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getEAHardwareId();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getGosMdmAppKey() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getGosMdmAppKey();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public int getLatestAppVersionCheckResult() {
        if (this.m_environmentDataContainer == null) {
            return 0;
        }
        return this.m_environmentDataContainer.getLatestAppVersionCheckResult();
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "SynergyEnv";
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getNexusClientId() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getNexusClientId();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getNexusClientSecret() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getNexusClientSecret();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getProductId() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getProductId();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getSellId() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getSellId();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getServerUrlWithKey(String str) {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getServerUrlWithKey(str);
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getSynergyDirectorServerUrl(NimbleConfiguration nimbleConfiguration) {
        switch (AnonymousClass3.$SwitchMap$com$ea$nimble$NimbleConfiguration[nimbleConfiguration.ordinal()]) {
            case 1:
                return SYNERGY_INT_SERVER_URL;
            case 2:
                return SYNERGY_STAGE_SERVER_URL;
            case 3:
                return SYNERGY_LIVE_SERVER_URL;
            case 4:
                return PreferenceManager.getDefaultSharedPreferences(ApplicationEnvironment.getComponent().getApplicationContext()).getString("NimbleCustomizedSynergyServerEndpointUrl", SYNERGY_LIVE_SERVER_URL);
            default:
                Log.Helper.LOGF(this, "Request for Synergy Director server URL with unknown NimbleConfiguration, %d.", nimbleConfiguration);
                return SYNERGY_LIVE_SERVER_URL;
        }
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public String getSynergyId() {
        checkAndInitiateSynergyEnvironmentUpdate();
        if (this.m_environmentDataContainer == null) {
            return null;
        }
        return this.m_environmentDataContainer.getSynergyId();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public int getTrackingPostInterval() {
        if (this.m_environmentDataContainer == null) {
            return -1;
        }
        return this.m_environmentDataContainer.getTrackingPostInterval();
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public boolean isDataAvailable() {
        return this.m_environmentDataContainer != null;
    }

    @Override // com.ea.nimble.ISynergyEnvironment
    public boolean isUpdateInProgress() {
        return this.m_synergyStartupObject != null;
    }

    @Override // com.ea.nimble.Component
    public void restore() {
        Log.Helper.LOGD(this, "restore", new Object[0]);
        if (this.m_dataLoadedOnComponentSetup) {
            this.m_dataLoadedOnComponentSetup = false;
            Utility.sendBroadcast(SynergyEnvironment.NOTIFICATION_RESTORED_FROM_PERSISTENT, null);
        } else {
            restoreEnvironmentDataFromPersistent(false);
        }
        if (this.m_environmentDataContainer == null || this.m_environmentDataContainer.getMostRecentDirectorResponseTimestamp() == null || ((double) (System.currentTimeMillis() - this.m_environmentDataContainer.getMostRecentDirectorResponseTimestamp().longValue())) / 1000.0d > 300.0d) {
            startSynergyEnvironmentUpdate();
        } else {
            checkAndInitiateSynergyEnvironmentUpdate();
        }
    }

    @Override // com.ea.nimble.Component
    public void resume() {
        Log.Helper.LOGD(this, "resume", new Object[0]);
        clearSynergyEnvironmentUpdateRateLimiting();
        if (this.m_environmentDataContainer == null || this.m_environmentDataContainer.getMostRecentDirectorResponseTimestamp() == null || ((double) (System.currentTimeMillis() - this.m_environmentDataContainer.getMostRecentDirectorResponseTimestamp().longValue())) / 1000.0d > 300.0d) {
            startSynergyEnvironmentUpdate();
        }
    }

    @Override // com.ea.nimble.Component
    public void setup() {
        Log.Helper.LOGD(this, "setup", new Object[0]);
        this.m_dataLoadedOnComponentSetup = restoreEnvironmentDataFromPersistent(true);
    }

    @Override // com.ea.nimble.Component
    public void suspend() {
        Log.Helper.LOGD(this, "suspend", new Object[0]);
        if (this.m_synergyStartupObject != null) {
            this.m_synergyStartupObject.cancel();
            this.m_synergyStartupObject = null;
        }
        if (this.m_networkStatusChangeReceiver != null) {
            Utility.unregisterReceiver(this.m_networkStatusChangeReceiver);
            this.m_networkStatusChangeReceiver = null;
        }
        saveEnvironmentDataToPersistent();
    }

    @Override // com.ea.nimble.Component
    public void teardown() {
        this.m_environmentDataContainer = null;
    }
}
