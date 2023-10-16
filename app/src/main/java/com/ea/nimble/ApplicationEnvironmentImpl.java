package com.ea.nimble;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;

import com.ea.nimble.Log.Helper;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationEnvironmentImpl
extends Component
implements IApplicationEnvironment,
LogSource {
    private static final int MILLIS_IN_AN_HOUR = 3600000;
    private static final String NIMBLE_APPLICATIONENVIRONMENT_PERSISTENCE_GAME_SPECIFIED_ID = "nimble_applicationenvironment_game_specified_id";
    private static final String PERSISTENCE_AGE_REQUIREMENTS = "ageRequirement";
    private static final String PERSISTENCE_LANGUAGE = "language";
    private static final String PERSISTENCE_TIME_RETRIEVED = "timeRetrieved";
    private static final String SYNERGY_API_GET_AGE_REQUIREMENTS = "/rest/agerequirements/ip";
    private static boolean isMainApplicationRunning = false;
    private static Activity s_currentActivity = null;
    private Context m_context;
    private BaseCore m_core;
    private String m_gameSpecifiedPlayerId;
    private boolean m_googleAdvertiserInfoLoaded;
    private String m_googleAdvertisingId;
    private boolean m_googleLimitAdTrackingEnabled;
    private String m_language;
    private String m_packageId;
    private String m_version;

    ApplicationEnvironmentImpl(BaseCore fileArray) {
        int n2 = 0;
        this.m_googleAdvertisingId = "";
        this.m_googleLimitAdTrackingEnabled = true;
        this.m_googleAdvertiserInfoLoaded = false;
        if (s_currentActivity == null) {
            throw new AssertionError("Cannot create a ApplicationEnvironment without a valid current activity");
        }
        this.m_core = fileArray;
        this.m_context = s_currentActivity.getApplicationContext();
        this.m_language = null;
        File documentPath = new File(this.getDocumentPath());
        File tempPath = new File(this.getTempPath());
        if (!documentPath.exists()) {
            if (!documentPath.mkdirs()) throw new AssertionError("APP_ENV: Cannot create necessary folder");
        }
        Log.i("lol", tempPath.getAbsolutePath());
        if (!tempPath.exists() && !tempPath.mkdirs()) {
            throw new AssertionError("APP_ENV: Cannot create necessary folder");
        }
        File[] files = tempPath.listFiles();
        int n3 = files.length;
        while (n2 < n3) {
            tempPath = files[n2];
            tempPath.delete();
            Log.d("Nimble", "APP_ENV: Delete temp file " + tempPath.getName());
            ++n2;
        }
    }

    static /* synthetic */ String access$002(ApplicationEnvironmentImpl applicationEnvironmentImpl, String string2) {
        applicationEnvironmentImpl.m_googleAdvertisingId = string2;
        return string2;
    }

    static /* synthetic */ boolean access$102(ApplicationEnvironmentImpl applicationEnvironmentImpl, boolean bl2) {
        applicationEnvironmentImpl.m_googleLimitAdTrackingEnabled = bl2;
        return bl2;
    }

    static /* synthetic */ boolean access$202(ApplicationEnvironmentImpl applicationEnvironmentImpl, boolean bl2) {
        applicationEnvironmentImpl.m_googleAdvertiserInfoLoaded = bl2;
        return bl2;
    }

    private static boolean commandExists(String string2) {
        String path = System.getenv("PATH");
        if (path == null) {
            return false;
        }
        String[] split = path.split(Pattern.quote(File.pathSeparator));
        int n2 = split.length;
        int n3 = 0;
        while (n3 < n2) {
            if (new File(split[n3], string2).exists()) {
                return true;
            }
            ++n3;
        }
        return false;
    }

    public static Activity getCurrentActivity() {
        return s_currentActivity;
    }

    private String getDeviceLanguage() {
        return Locale.getDefault().toString();
    }

    public static boolean isMainApplicationRunning() {
        return isMainApplicationRunning;
    }

    /*
     * Enabled unnecessary exception pruning
     */
    private void retrieveGoogleAdvertiserId() {

    }

    private void setApplicationLanguageCode(String object, boolean bl2) {
        if ((object = this.validatedLanguageCode((String)object, bl2)) == null) {
            return;
        }
        if (this.m_language != null && this.m_language.equals(object)) {
            if (bl2) {
                Helper.LOGD(this, "Setting the same language %s, skipping assignment", this.m_language);
                return;
            }
        } else {
            this.m_language = object;
            Helper.LOGI(this, "Successfully set language to %s.", this.m_language);
            Utility.sendBroadcast("nimble.notification.LanguageChanged", null);
        }
        if (bl2) return;
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.applicationEnvironment", Persistence.Storage.DOCUMENT);
        if (persistenceForNimbleComponent != null) {
            Helper.LOGD(this, "Saving language data to persistence.");
            persistenceForNimbleComponent.setValue(PERSISTENCE_LANGUAGE, (Serializable)((Object)this.m_language));
            return;
        }
        Helper.LOGE(this, "Could not get application environment persistence object to save to.");
    }

    public static void setCurrentActivity(Activity activity) {
        isMainApplicationRunning = true;
        s_currentActivity = activity;
    }

    private String validatedLanguageCode(String string2, boolean bl2) {
        if (!Utility.validString(string2)) {
            Helper.LOGI(this, "AppEnv: Language parameter is null or empty; keeping language at previous value.");
            return null;
        }
        string2 = string2.replace('_', '-');
        Object object = Pattern.compile("^([a-z]{2,3})?(-([A-Z][a-z]{3}))?(-([A-Z]{2}))?(-.*)*$").matcher(string2);
        if (!((Matcher)object).find()) {
            Helper.LOGE(this, "Malformed language code " + string2 + " cannot be validated; backend system will likely treat it as en-US.");
            return string2;
        }
        String string3 = ((Matcher)object).group(1);
        if (Utility.validString(string3) && !Arrays.asList(Locale.getISOLanguages()).contains(string3)) {
            Helper.LOGE(this, "Unknown language code " + string3 + " in language code " + string2 + "; backend system will likely treat it as en-US.");
        }
        if (!Utility.validString((String)(object = ((Matcher)object).group(5)))) return string2;
        if (Arrays.asList(Locale.getISOCountries()).contains(object)) return string2;
        Helper.LOGE(this, "Unknown region code " + (String)object + " in language code " + string2 + "; backend system will likely treat it as en-US.");
        return string2;
    }

    @Override
    public int getAgeCompliance() {
        Persistence persistence = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.applicationEnvironment", Persistence.Storage.CACHE);
        Serializable serializable = persistence.getValue(PERSISTENCE_TIME_RETRIEVED);
        if (serializable != null) {
            if ((int)(new Date().getTime() - (Long)serializable) / 3600000 <= 24) return (Integer)persistence.getValue(PERSISTENCE_AGE_REQUIREMENTS);
            Helper.LOGI(this, "getAgeCompliance- Stored value is older than 24 hours. Call refreshAgeCompliance to retrieve minAgeCompliance", new Object[]{null});
            return -1;
        }
        Helper.LOGI(this, "getAgeCompliance- No stored value in persistance. Call refreshAgeCompliance to retrieve minAgeCompliance.", new Object[]{null});
        return -1;
    }

    @Override
    public String getApplicationBundleId() {
        if (this.m_packageId != null) return this.m_packageId;
        Context context = this.getApplicationContext();
        if (context == null) return this.m_packageId;
        this.m_packageId = context.getPackageName();
        return this.m_packageId;
    }

    @Override
    public Context getApplicationContext() {
        return this.m_context;
    }

    @Override
    public String getApplicationLanguageCode() {
        return this.m_language;
    }

    @Override
    public String getApplicationName() {
        Context context = this.getApplicationContext();
        if (context != null) return context.getPackageManager().getApplicationLabel(context.getApplicationInfo()).toString();
        return null;
    }

    @Override
    public String getApplicationVersion() {
        if (this.m_version != null) return this.m_version;
        Context context = this.getApplicationContext();
        if (context == null) {
            return null;
        }
        try {
            this.m_version = context.getPackageManager().getPackageInfo((String)context.getPackageName(), (int)0).versionName;
            return this.m_version;
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            Helper.LOGE(this, "Package name %s not found", context.getPackageName());
            return null;
        }
    }

    @Override
    public String getCachePath() {
        return "/data/data/" + m_context.getPackageName() + File.separator + "cache" + File.separator + "Nimble" + File.separator + this.m_core.getConfiguration().toString();
    }

    @Override
    public String getCarrier() {
        Context context = this.getApplicationContext();
        if (context != null) return ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperator();
        return null;
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.applicationEnvironment";
    }

    @Override
    public String getDeviceBrand() {
        return Build.BRAND;
    }

    @Override
    public String getDeviceCodename() {
        return Build.DEVICE;
    }

    @Override
    public String getDeviceFingerprint() {
        return Build.FINGERPRINT;
    }

    @Override
    public String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    @Override
    public String getDeviceModel() {
        return Build.MODEL;
    }

    @Override
    public String getDeviceString() {
        return Build.MANUFACTURER + Build.MODEL;
    }

    @Override
    public String getDocumentPath() {
        Context object = this.getApplicationContext();
        if (object == null) {
            String s = System.getProperty("user.dir") + File.separator + "doc";
            return s + File.separator + "Nimble" + File.separator + this.m_core.getConfiguration().toString();
        }
        String path = object.getFilesDir().getPath();
        return path + File.separator + "Nimble" + File.separator + this.m_core.getConfiguration().toString();
    }

    @Override
    public String getGameSpecifiedPlayerId() {
        return this.m_gameSpecifiedPlayerId;
    }

    @Override
    public String getGoogleAdvertisingId() {
        long l2 = System.currentTimeMillis();
        while (!this.m_googleAdvertiserInfoLoaded) {
            if (System.currentTimeMillis() - l2 > 5000L) {
                this.m_googleAdvertiserInfoLoaded = true;
                continue;
            }
            try {
                Thread.sleep(1L);
            }
            catch (InterruptedException interruptedException) {
                Helper.LOGI(this, "Blocking call to getGoogleAdvertisingId was interrupted prematurely.");
            }
        }
        return this.m_googleAdvertisingId;
    }

    @Override
    public String getGoogleEmail() {
        int n2 = 0;
        AccountManager accountArray = AccountManager.get(this.getApplicationContext());
        Account[] accountsByType = accountArray.getAccountsByType("com.google");
        if (accountsByType.length > 0) {
            return accountsByType[0].name;
        }
        Pattern emailAddress = Patterns.EMAIL_ADDRESS;
        Account[] accounts = accountArray.getAccounts();
        int n3 = accounts.length;
        while (n2 < n3) {
            Account account = accounts[n2];
            if (emailAddress.matcher(account.name).matches()) {
                return account.name;
            }
            ++n2;
        }
        return null;
    }

    @Override
    public boolean getIadAttribution() {
        return false;
    }

    @Override
    public String getLogSourceTitle() {
        return "AppEnv";
    }

    @Override
    public String getMACAddress() {
        Context context = this.getApplicationContext();
        if (context == null) {
            return null;
        }
        WifiInfo wifi = ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        if ( null != wifi ) return wifi.getMacAddress();
        return null;
    }

    @Override
    public String getOsVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    @Override
    public String getShortApplicationLanguageCode() {
        if (this.m_language == null) return this.m_language;
        int n2 = this.m_language.indexOf(45);
        if (n2 == -1) return this.m_language;
        return this.m_language.substring(0, n2);
    }

    @Override
    public String getTempPath() {
        return this.getCachePath() + File.separator + "temp";
    }

    @Override
    public boolean isAppCracked() {
        Helper.LOGDS("FraudDetection", "Returning false for isAppCracked() since it hasn't been implemented yet");
        return false;
    }

    @Override
    public boolean isDeviceRooted() {
        String string2 = Build.TAGS;
        if (string2 != null && string2.contains("test-keys")) {
            return true;
        }
        if (new File("/system/app/Superuser.apk").exists()) return true;
        if (ApplicationEnvironmentImpl.commandExists("su")) return true;
        return false;
    }

    @Override
    public boolean isLimitAdTrackingEnabled() {
        return this.m_googleLimitAdTrackingEnabled;
    }

    @Override
    public void refreshAgeCompliance() {
        if (Network.getComponent().getStatus() != Network.Status.OK) {
            Error error = new Error(Error.Code.NETWORK_NO_CONNECTION, "No network connection, Min Age cannot update.");
            HashMap<String, Serializable> hashMap = new HashMap<String, Serializable>();
            hashMap.put("result", (Serializable)((Object)"0"));
            hashMap.put("error", error);
            Utility.sendBroadcastSerializable("nimble.notification.age_compliance_refreshed", hashMap);
            return;
        }
        Object object = (SynergyRequest.SynergyRequestPreparingCallback) synergyRequest -> {
            synergyRequest.baseUrl = SynergyEnvironment.getComponent().getServerUrlWithKey("geoip.url");
            synergyRequest.send();
        };
        SynergyNetworkConnectionCallback synergyNetworkConnectionCallback = new SynergyNetworkConnectionCallback(){

            @Override
            public void callback(SynergyNetworkConnectionHandle object) {
                HashMap<String, Serializable> hashMap = new HashMap<>();
                if (object.getResponse().getError() == null) {
                    Integer n2 = (Integer)object.getResponse().getJsonData().get("code");

                } else {
                    Helper.LOGD(this, "LOG_CALLBACK_ERROR : %s", object.getResponse().getError().getMessage());
                    hashMap.put("result", (Serializable)((Object)"0"));
                    hashMap.put("error", object.getResponse().getError());
                }
                Utility.sendBroadcastSerializable("nimble.notification.age_compliance_refreshed", hashMap);
            }
        };
        object = new SynergyRequest(SYNERGY_API_GET_AGE_REQUIREMENTS, IHttpRequest.Method.GET, (SynergyRequest.SynergyRequestPreparingCallback)object);
        SynergyNetwork.getComponent().sendRequest((SynergyRequest)object, synergyNetworkConnectionCallback);
    }

    @Override
    protected void restore() {
        Object object = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.applicationEnvironment", Persistence.Storage.DOCUMENT);
        if (object == null) {
            this.setApplicationLanguageCode(this.getDeviceLanguage(), true);
            Helper.LOGWS("ApplicationEnvironment", "Persistence is null - Couldn't read Game Specified Player ID or Language from Persistence");
            return;
        }
        if (!Utility.validString(this.m_gameSpecifiedPlayerId)) {
            Helper.LOGDS("ApplicationEnvironment", "Current game specified player ID is empty, reload from persistence");
            this.m_gameSpecifiedPlayerId = ((Persistence)object).getStringValue(NIMBLE_APPLICATIONENVIRONMENT_PERSISTENCE_GAME_SPECIFIED_ID);
        }
        if (Utility.validString((String)(object = ((Persistence)object).getStringValue(PERSISTENCE_LANGUAGE)))) {
            Helper.LOGD(this, "Restored language %s from persistence.", this.m_language);
            return;
        }
        Helper.LOGD(this, "Unable to restore language from persistence. Setting language to device language.");
        this.setApplicationLanguageCode(this.getDeviceLanguage(), true);
    }

    @Override
    protected void resume() {
        try {
            this.retrieveGoogleAdvertiserId();
            return;
        }
        catch (VerifyError verifyError) {}
        finally {
            Helper.LOGW(this, "APP_ENV: Cannot get Google Advertising ID because this device is not supported");
            return;
        }
    }

    @Override
    public void setApplicationBundleId(String string2) {
        this.m_packageId = string2;
    }

    @Override
    public void setApplicationLanguageCode(String string2) {
        this.setApplicationLanguageCode(string2, false);
    }

    @Override
    public void setGameSpecifiedPlayerId(String object) {
        this.m_gameSpecifiedPlayerId = object;
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.applicationEnvironment", Persistence.Storage.DOCUMENT);
        if (object == null) {
            Helper.LOGWS("ApplicationEnvironment", "Persistence is null - Couldn't save Game Specified Player ID to Persistence");
            return;
        }
        persistenceForNimbleComponent.setValue(NIMBLE_APPLICATIONENVIRONMENT_PERSISTENCE_GAME_SPECIFIED_ID, (Serializable)((Object)this.m_gameSpecifiedPlayerId));
    }

    @Override
    protected void setup() {
        this.m_context = s_currentActivity.getApplicationContext();
        try {
            this.retrieveGoogleAdvertiserId();
        }
        catch (VerifyError verifyError) {}
        finally {
            Helper.LOGW(this, "APP_ENV: Cannot get Google Advertising ID because this device is not supported");
        }
    }

    @Override
    protected void teardown() {
        this.m_context = null;
    }
}

