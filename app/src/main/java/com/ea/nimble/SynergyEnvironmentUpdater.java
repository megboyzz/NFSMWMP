package com.ea.nimble;

import android.content.Context;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: stdlib.jar:com/ea/nimble/SynergyEnvironmentUpdater.class */
public class SynergyEnvironmentUpdater implements LogSource {
    private static final int GET_ANONUID_MAX_RETRY_ATTEMPTS = 3;
    private static final int GET_DIRECTION_MAX_RETRY_ATTEMPTS = 3;
    private static final int GET_EADEVICEID_MAX_RETRY_ATTEMPTS = 3;
    private static final int SYNERGY_DIRECTOR_RESPONSE_ERROR_CODE_SERVERS_FULL = -70002;
    private static final int SYNERGY_USER_VALIDATE_EADEVICEID_RESPONSE_ERROR_CODE_CLEAR_CLIENT_CACHED_EADEVICEID = -20094;
    private static final int SYNERGY_USER_VALIDATE_EADEVICEID_RESPONSE_ERROR_CODE_VALIDATION_FAILED = -20093;
    private static final int VALIDATE_EADEVICEID_MAX_RETRY_ATTEMPTS = 3;
    private BaseCore m_core;
    private long m_getAnonUIDRetryCount;
    private long m_getDirectionRetryCount;
    private EnvironmentDataContainer m_environmentForSynergyStartUp = new EnvironmentDataContainer();
    private CompletionCallback m_completionCallback = null;
    private EnvironmentDataContainer m_previousValidEnvironmentData = null;
    private SynergyNetworkConnectionHandle m_synergyNetworkConnectionHandle = null;
    private long m_validateEADeviceIDRetryCount = 0;
    private long m_getEADeviceIDRetryCount = 0;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.ea.nimble.SynergyEnvironmentUpdater$5  reason: invalid class name */
    /* loaded from: stdlib.jar:com/ea/nimble/SynergyEnvironmentUpdater$5.class */
    public static /* synthetic */ class AnonymousClass5 {
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
    /* loaded from: stdlib.jar:com/ea/nimble/SynergyEnvironmentUpdater$CompletionCallback.class */
    public interface CompletionCallback {
        void callback(Exception exc);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SynergyEnvironmentUpdater(BaseCore baseCore) {
        this.m_core = baseCore;
    }

    static /* synthetic */ long access$1008(SynergyEnvironmentUpdater synergyEnvironmentUpdater) {
        long j = synergyEnvironmentUpdater.m_getEADeviceIDRetryCount;
        synergyEnvironmentUpdater.m_getEADeviceIDRetryCount = 1 + j;
        return j;
    }

    static /* synthetic */ long access$1108(SynergyEnvironmentUpdater synergyEnvironmentUpdater) {
        long j = synergyEnvironmentUpdater.m_validateEADeviceIDRetryCount;
        synergyEnvironmentUpdater.m_validateEADeviceIDRetryCount = 1 + j;
        return j;
    }

    static /* synthetic */ long access$1208(SynergyEnvironmentUpdater synergyEnvironmentUpdater) {
        long j = synergyEnvironmentUpdater.m_getAnonUIDRetryCount;
        synergyEnvironmentUpdater.m_getAnonUIDRetryCount = 1 + j;
        return j;
    }

    static /* synthetic */ long access$708(SynergyEnvironmentUpdater synergyEnvironmentUpdater) {
        long j = synergyEnvironmentUpdater.m_getDirectionRetryCount;
        synergyEnvironmentUpdater.m_getDirectionRetryCount = 1 + j;
        return j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callSynergyGetAnonUid() {
        String anonymousSynergyId = SynergyIdManager.getComponent().getAnonymousSynergyId();
        if (anonymousSynergyId != null) {
            Log.Helper.LOGD(this, "Not getting anonymous ID from Synergy since it was loaded from persistence");
            this.m_environmentForSynergyStartUp.setSynergyAnonymousId(anonymousSynergyId);
            onStartUpSequenceFinished(null);
            return;
        }
        HashMap hashMap = new HashMap();
        hashMap.put("apiVer", "1.0.0");
        hashMap.put("updatePriority", "false");
        hashMap.put("hwId", this.m_environmentForSynergyStartUp.getEAHardwareId());
        if (Utility.validString(this.m_environmentForSynergyStartUp.getEADeviceId())) {
            hashMap.put("eadeviceid", this.m_environmentForSynergyStartUp.getEADeviceId());
            this.m_synergyNetworkConnectionHandle = SynergyNetwork.getComponent().sendGetRequest(this.m_environmentForSynergyStartUp.getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_SYNERGY_USER), "/user/api/android/getAnonUid", hashMap, new SynergyNetworkConnectionCallback() { // from class: com.ea.nimble.SynergyEnvironmentUpdater.4
                @Override // com.ea.nimble.SynergyNetworkConnectionCallback
                public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                    SynergyEnvironmentUpdater.this.m_synergyNetworkConnectionHandle = null;
                    Exception error = synergyNetworkConnectionHandle.getResponse().getError();
                    if (error == null) {
                        Log.Helper.LOGD(this, "GETANON Success");
                        SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.setSynergyAnonymousId(synergyNetworkConnectionHandle.getResponse().getJsonData().get("uid").toString());
                        SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(null);
                    } else if (SynergyEnvironmentUpdater.this.isTimeoutError(error) || SynergyEnvironmentUpdater.this.m_getAnonUIDRetryCount >= 3) {
                        SynergyEnvironmentUpdater.this.m_getAnonUIDRetryCount = 0;
                        Log.Helper.LOGD(this, "GETANON Error, (%s)", synergyNetworkConnectionHandle.getResponse().getError().toString());
                        SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(new Error(Error.Code.SYNERGY_GET_ANONYMOUS_ID_FAILURE, "Synergy \"get anonymous id\" call failed.", error));
                    } else {
                        SynergyEnvironmentUpdater.access$1208(SynergyEnvironmentUpdater.this);
                        Log.Helper.LOGD(this, "GetAnonUid, call failed. Making retry attempt number %d.", Long.valueOf(SynergyEnvironmentUpdater.this.m_getAnonUIDRetryCount));
                        SynergyEnvironmentUpdater.this.callSynergyGetAnonUid();
                    }
                }
            });
            return;
        }
        Log.Helper.LOGE(this, "getAnonUid got an invalid EA Device ID.");
        onStartUpSequenceFinished(new Error(Error.Code.INVALID_ARGUMENT, "EA Device ID is invalid"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callSynergyGetDirection() {
        String applicationBundleId = ApplicationEnvironment.getComponent().getApplicationBundleId();
        String deviceString = ApplicationEnvironment.getComponent().getDeviceString();
        String deviceCodename = ApplicationEnvironment.getComponent().getDeviceCodename();
        String deviceManufacturer = ApplicationEnvironment.getComponent().getDeviceManufacturer();
        String deviceModel = ApplicationEnvironment.getComponent().getDeviceModel();
        String deviceBrand = ApplicationEnvironment.getComponent().getDeviceBrand();
        String deviceFingerprint = ApplicationEnvironment.getComponent().getDeviceFingerprint();
        if (!Utility.validString(applicationBundleId)) {
            Log.Helper.LOGE(this, "GETDIRECTION bundleId is invalid");
            onStartUpSequenceFinished(new Error(Error.Code.INVALID_ARGUMENT, "bundleId is invalid"));
        } else if (!Utility.validString(deviceString)) {
            Log.Helper.LOGE(this, "GETDIRECTION deviceString is invalid");
            onStartUpSequenceFinished(new Error(Error.Code.INVALID_ARGUMENT, "deviceString is invalid"));
        } else {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("packageId", applicationBundleId);
            hashMap.put("deviceString", deviceString);
            hashMap.put("deviceCodename", deviceCodename);
            hashMap.put("manufacturer", deviceManufacturer);
            hashMap.put("model", deviceModel);
            hashMap.put("brand", deviceBrand);
            hashMap.put("fingerprint", deviceFingerprint);
            hashMap.put("serverEnvironment", getSynergyServerEnvironmentName());
            hashMap.put("sdkVersion", "1.23.14.1217");
            hashMap.put("apiVer", "1.0.0");
            this.m_synergyNetworkConnectionHandle = SynergyNetwork.getComponent().sendGetRequest(this.m_environmentForSynergyStartUp.getSynergyDirectorServerUrl(Base.getConfiguration()), "/director/api/android/getDirectionByPackage", hashMap, new SynergyNetworkConnectionCallback() { // from class: com.ea.nimble.SynergyEnvironmentUpdater.1
                /* JADX WARN: Type inference failed for: r1v24, types: [java.lang.Object] */
                /* JADX WARN: Type inference failed for: r2v10, types: [java.lang.Object] */
                @Override // com.ea.nimble.SynergyNetworkConnectionCallback
                public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                    Log.Helper.LOGD(this, "GETDIRECTION FINISHED");
                    SynergyEnvironmentUpdater.this.m_synergyNetworkConnectionHandle = null;
                    Exception error = synergyNetworkConnectionHandle.getResponse().getError();
                    if (error == null) {
                        SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.setMostRecentDirectorResponseTimestamp(System.currentTimeMillis());
                        SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.setGetDirectionResponseDictionary(synergyNetworkConnectionHandle.getResponse().getJsonData());
                        List<Map> list = (List) SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.getGetDirectionResponseDictionary().get("serverData");
                        SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.setServerUrls(new HashMap());
                        if (list != null) {
                            for (Map map : list) {
                                SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.getServerUrls().put((String) map.get("key"), (String)map.get("value"));
                            }
                        }
                        if (SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.getServerUrls().size() == 0) {
                            SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(new Error(Error.Code.NOT_AVAILABLE, "No Synergy server URLs available."));
                        } else if (SynergyEnvironmentUpdater.this.m_previousValidEnvironmentData == null || !Utility.validString(SynergyEnvironmentUpdater.this.m_previousValidEnvironmentData.getEADeviceId())) {
                            String loadEADeviceId = EASPDataLoader.loadEADeviceId();
                            if (loadEADeviceId != null) {
                                SynergyEnvironmentUpdater.this.callSynergyValidateEADeviceId(loadEADeviceId);
                            } else {
                                SynergyEnvironmentUpdater.this.callSynergyGetEADeviceId();
                            }
                        } else {
                            SynergyEnvironmentUpdater.this.callSynergyValidateEADeviceId(SynergyEnvironmentUpdater.this.m_previousValidEnvironmentData.getEADeviceId());
                        }
                    } else if (!(error instanceof SynergyServerError)) {
                        boolean isTimeoutError = SynergyEnvironmentUpdater.this.isTimeoutError(error);
                        if (isTimeoutError || SynergyEnvironmentUpdater.this.m_getDirectionRetryCount >= 3) {
                            SynergyEnvironmentUpdater.this.m_getDirectionRetryCount = 0;
                            if (isTimeoutError) {
                                SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(new Error(Error.Code.SYNERGY_GET_DIRECTION_TIMEOUT, "Synergy /getDirectionByPackage request timed out.", error));
                            } else {
                                SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(error);
                            }
                        } else {
                            SynergyEnvironmentUpdater.access$708(SynergyEnvironmentUpdater.this);
                            Log.Helper.LOGD(this, "GetDirection, call failed. Making retry attempt number %d.", Long.valueOf(SynergyEnvironmentUpdater.this.m_getDirectionRetryCount));
                            SynergyEnvironmentUpdater.this.callSynergyGetDirection();
                        }
                    } else if (((SynergyServerError) error).isError(SynergyEnvironmentUpdater.SYNERGY_DIRECTOR_RESPONSE_ERROR_CODE_SERVERS_FULL)) {
                        SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(new Error(Error.Code.SYNERGY_SERVER_FULL, "Synergy ServerUnavailable signal received.", error));
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callSynergyGetEADeviceId() {
        int phoneType;
        String string;
        String SHA256HashString;
        EnvironmentDataContainer environmentDataContainer = this.m_environmentForSynergyStartUp;
        HashMap hashMap = new HashMap();
        hashMap.put("apiVer", "1.0.0");
        hashMap.put("hwId", environmentDataContainer.getEAHardwareId());
        String mACAddress = ApplicationEnvironment.getComponent().getMACAddress();
        if (Utility.validString(mACAddress) && (SHA256HashString = Utility.SHA256HashString(mACAddress)) != null) {
            hashMap.put("macHash", SHA256HashString);
        }
        IApplicationEnvironment component = ApplicationEnvironment.getComponent();
        Context context = null;
        if (component != null) {
            context = component.getApplicationContext();
        }
        if (!(context == null || (string = Settings.Secure.getString(component.getApplicationContext().getContentResolver(), "android_id")) == null)) {
            hashMap.put("androidId", string);
        }
        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            PackageManager packageManager = context.getPackageManager();
            if (!(telephonyManager == null || packageManager.checkPermission("android.permission.READ_PHONE_STATE", context.getPackageName()) != 0 || (phoneType = telephonyManager.getPhoneType()) == 0)) {
                String deviceId = telephonyManager.getDeviceId();
                if (Utility.validString(deviceId)) {
                    String str = "imei";
                    if (phoneType == 2) {
                        str = "meid";
                    }
                    hashMap.put(str, deviceId);
                }
            }
        }
        this.m_synergyNetworkConnectionHandle = SynergyNetwork.getComponent().sendGetRequest(this.m_environmentForSynergyStartUp.getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_SYNERGY_USER), "/user/api/android/getDeviceID", hashMap, new SynergyNetworkConnectionCallback() { // from class: com.ea.nimble.SynergyEnvironmentUpdater.2
            @Override // com.ea.nimble.SynergyNetworkConnectionCallback
            public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                SynergyEnvironmentUpdater.this.m_synergyNetworkConnectionHandle = null;
                Exception error = synergyNetworkConnectionHandle.getResponse().getError();
                if (error == null) {
                    Log.Helper.LOGD(this, "GetEADeviceID Success");
                    SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.setEADeviceId((String) synergyNetworkConnectionHandle.getResponse().getJsonData().get("deviceId"));
                    SynergyEnvironmentUpdater.this.callSynergyGetAnonUid();
                } else if (SynergyEnvironmentUpdater.this.isTimeoutError(error) || SynergyEnvironmentUpdater.this.m_getEADeviceIDRetryCount >= 3) {
                    SynergyEnvironmentUpdater.this.m_getEADeviceIDRetryCount = 0;
                    Log.Helper.LOGD(this, "GetEADeviceID Error (%s)", synergyNetworkConnectionHandle.getResponse().getError());
                    SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(new Error(Error.Code.SYNERGY_GET_EA_DEVICE_ID_FAILURE, "GetEADevideId call failed", synergyNetworkConnectionHandle.getResponse().getError()));
                } else {
                    SynergyEnvironmentUpdater.access$1008(SynergyEnvironmentUpdater.this);
                    Log.Helper.LOGD(this, "GetEADeviceID, call failed. Making retry attempt number %d.", Long.valueOf(SynergyEnvironmentUpdater.this.m_getEADeviceIDRetryCount));
                    SynergyEnvironmentUpdater.this.callSynergyGetEADeviceId();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void callSynergyValidateEADeviceId(final String str) {
        int phoneType;
        String string;
        String SHA256HashString;
        EnvironmentDataContainer environmentDataContainer = this.m_environmentForSynergyStartUp;
        HashMap hashMap = new HashMap();
        hashMap.put("apiVer", "1.0.0");
        hashMap.put("hwId", environmentDataContainer.getEAHardwareId());
        hashMap.put("eadeviceid", str);
        String mACAddress = ApplicationEnvironment.getComponent().getMACAddress();
        if (Utility.validString(mACAddress) && (SHA256HashString = Utility.SHA256HashString(mACAddress)) != null) {
            hashMap.put("macHash", SHA256HashString);
        }
        IApplicationEnvironment component = ApplicationEnvironment.getComponent();
        Context context = null;
        if (component != null) {
            context = component.getApplicationContext();
        }
        if (!(context == null || (string = Settings.Secure.getString(component.getApplicationContext().getContentResolver(), "android_id")) == null)) {
            hashMap.put("androidId", string);
        }
        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
            PackageManager packageManager = context.getPackageManager();
            if (!(telephonyManager == null || packageManager.checkPermission("android.permission.READ_PHONE_STATE", context.getPackageName()) != 0 || (phoneType = telephonyManager.getPhoneType()) == 0)) {
                String deviceId = telephonyManager.getDeviceId();
                if (Utility.validString(deviceId)) {
                    String str2 = "imei";
                    if (phoneType == 2) {
                        str2 = "meid";
                    }
                    hashMap.put(str2, deviceId);
                }
            }
        }
        this.m_synergyNetworkConnectionHandle = SynergyNetwork.getComponent().sendGetRequest(this.m_environmentForSynergyStartUp.getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_SYNERGY_USER), "/user/api/android/validateDeviceID", hashMap, new SynergyNetworkConnectionCallback() { // from class: com.ea.nimble.SynergyEnvironmentUpdater.3
            @Override // com.ea.nimble.SynergyNetworkConnectionCallback
            public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                SynergyEnvironmentUpdater.this.m_synergyNetworkConnectionHandle = null;
                Exception error = synergyNetworkConnectionHandle.getResponse().getError();
                if (error == null) {
                    Log.Helper.LOGD(this, "ValidateEADeviceID Success");
                    SynergyEnvironmentUpdater.this.m_environmentForSynergyStartUp.setEADeviceId((String) synergyNetworkConnectionHandle.getResponse().getJsonData().get("deviceId"));
                    SynergyEnvironmentUpdater.this.callSynergyGetAnonUid();
                    return;
                }
                Log.Helper.LOGD(this, "ValidateEADeviceID Error (%s)", error);
                if (error instanceof SynergyServerError) {
                    SynergyServerError synergyServerError = (SynergyServerError) error;
                    if (synergyServerError.isError(SynergyEnvironmentUpdater.SYNERGY_USER_VALIDATE_EADEVICEID_RESPONSE_ERROR_CODE_CLEAR_CLIENT_CACHED_EADEVICEID)) {
                        if (SynergyEnvironmentUpdater.this.m_previousValidEnvironmentData != null) {
                            SynergyEnvironmentUpdater.this.m_previousValidEnvironmentData.setEADeviceId(null);
                        }
                        Log.Helper.LOGD(this, "ValidateEADeviceID, Server signal received to delete cached EA Device ID. Making request to get a new EA Device ID.");
                        SynergyEnvironmentUpdater.this.callSynergyGetEADeviceId();
                        return;
                    } else if (synergyServerError.isError(SynergyEnvironmentUpdater.SYNERGY_USER_VALIDATE_EADEVICEID_RESPONSE_ERROR_CODE_VALIDATION_FAILED)) {
                        Log.Helper.LOGD(this, "ValidateEADeviceID, EADeviceID validation failed. Making request to get a new EA Device ID.");
                        SynergyEnvironmentUpdater.this.callSynergyGetEADeviceId();
                        return;
                    }
                }
                if (SynergyEnvironmentUpdater.this.isTimeoutError(error) || SynergyEnvironmentUpdater.this.m_validateEADeviceIDRetryCount >= 3) {
                    SynergyEnvironmentUpdater.this.m_validateEADeviceIDRetryCount = 0;
                    SynergyEnvironmentUpdater.this.onStartUpSequenceFinished(new Error(Error.Code.SYNERGY_GET_EA_DEVICE_ID_FAILURE, "ValidateEADeviceId call failed", error));
                    return;
                }
                SynergyEnvironmentUpdater.access$1108(SynergyEnvironmentUpdater.this);
                Log.Helper.LOGD(this, "ValidateEADeviceID, call failed. Making retry attempt number %d.", Long.valueOf(SynergyEnvironmentUpdater.this.m_validateEADeviceIDRetryCount));
                SynergyEnvironmentUpdater.this.callSynergyValidateEADeviceId(str);
            }
        });
    }

    private String getSynergyServerEnvironmentName() {
        switch (AnonymousClass5.$SwitchMap$com$ea$nimble$NimbleConfiguration[this.m_core.getConfiguration().ordinal()]) {
            case 1:
            case 2:
            case 3:
                return this.m_core.getConfiguration().toString();
            case 4:
                return PreferenceManager.getDefaultSharedPreferences(ApplicationEnvironment.getComponent().getApplicationContext()).getString("NimbleCustomizedSynergyServerEnvironmentName", "live");
            default:
                Log.Helper.LOGF(this, "Request for Synergy server environment name with unknown NimbleConfiguration %s", this.m_core.getConfiguration().toString());
                return "live";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isTimeoutError(Exception exc) {
        return (exc instanceof Error) && ((Error) exc).isError(Error.Code.NETWORK_TIMEOUT);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStartUpSequenceFinished(Exception exc) {
        if (this.m_completionCallback != null) {
            this.m_completionCallback.callback(exc);
        } else {
            Log.Helper.LOGW(this, "Startup sequence finished, but no completion callback set.");
        }
    }

    public void cancel() {
        SynergyNetworkConnectionHandle synergyNetworkConnectionHandle = this.m_synergyNetworkConnectionHandle;
        if (synergyNetworkConnectionHandle != null) {
            Log.Helper.LOGD(this, "Canceling network connection.");
            synergyNetworkConnectionHandle.cancel();
            this.m_synergyNetworkConnectionHandle = null;
        }
        onStartUpSequenceFinished(new Error(Error.Code.NETWORK_OPERATION_CANCELLED, "Synergy startup sequence canceled."));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public EnvironmentDataContainer getEnvironmentDataContainer() {
        return this.m_environmentForSynergyStartUp;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "SynergyEnv";
    }

    public void startSynergyStartupSequence(EnvironmentDataContainer environmentDataContainer, CompletionCallback completionCallback) {
        this.m_completionCallback = completionCallback;
        this.m_previousValidEnvironmentData = environmentDataContainer;
        if (Network.getComponent().getStatus() != Network.Status.OK) {
            onStartUpSequenceFinished(new Error(Error.Code.NETWORK_NO_CONNECTION, "Device is not connected to Wifi or wireless."));
        } else {
            callSynergyGetDirection();
        }
    }
}
