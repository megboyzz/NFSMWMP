package com.ea.nimble.tracking;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.ea.ironmonkey.devmenu.util.Observer;
import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.Global;
import com.ea.nimble.IApplicationEnvironment;
import com.ea.nimble.IHttpRequest;
import com.ea.nimble.ISynergyEnvironment;
import com.ea.nimble.ISynergyIdManager;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.SynergyIdManager;
import com.ea.nimble.SynergyRequest;
import com.ea.nimble.Utility;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/* loaded from: stdlib.jar:com/ea/nimble/tracking/NimbleTrackingS2SImpl.class */
public class NimbleTrackingS2SImpl extends NimbleTrackingImplBase implements LogSource {
    public static final int EVENT_APPRESUMED = 103;
    public static final int EVENT_APPSTARTED = 102;
    public static final int EVENT_APPSTARTED_AFTERINSTALL = 101;
    public static final int EVENT_LEVEL_UP = 108;
    public static final int EVENT_MTXVIEW_ITEM_PURCHASED = 105;
    private static final String EVENT_PREFIX = "SYNERGYS2S::";
    public static final int EVENT_REFERRERID_RECEIVED = 106;
    public static final int EVENT_TUTORIAL_COMPLETE = 107;
    public static final int EVENT_USER_REGISTERED = 104;
    private static final double MARS_DEFAULT_POST_INTERVAL = 60.0d;
    private static final double MARS_MAX_POST_RETRY_DELAY = 86400.0d;
    private static final String SYNERGY_API_POST_EVENTS = "/s2s/api/core/postEvents";

    private Map<String, Object> createEventRequestPostMap() {
        String str;
        String gameSpecifiedPlayerId;
        IApplicationEnvironment component = ApplicationEnvironment.getComponent();
        ISynergyEnvironment component2 = SynergyEnvironment.getComponent();
        ISynergyIdManager component3 = SynergyIdManager.getComponent();
        Date date = new Date();
        HashMap<String, Object> hashMap = new HashMap<>();
        String str2 = "";
        boolean z = true;
        try {
            String googleAdvertisingId = ApplicationEnvironment.getComponent().getGoogleAdvertisingId();
            str2 = googleAdvertisingId;
            z = ApplicationEnvironment.getComponent().isLimitAdTrackingEnabled();
            str2 = googleAdvertisingId;
        } catch (Exception e) {
            Log.Helper.LOGW(this, "Exception when getting advertising ID for Android");
        }
        hashMap.put("advertiserID", str2);
        hashMap.put("limitAdTracking", z+"");
        hashMap.put("bundleId", Utility.safeString(component.getApplicationBundleId()));
        hashMap.put("sellId", Utility.safeString(component2.getSellId()));
        hashMap.put("appName", Utility.safeString(component.getApplicationName()));
        hashMap.put("appVersion", Utility.safeString(component.getApplicationVersion()));
        hashMap.put("deviceId", Utility.safeString(component2.getEADeviceId()));
        hashMap.put("deviceNativeId", Utility.safeString(Settings.Secure.getString(component.getApplicationContext().getContentResolver(), "android_id")));
        hashMap.put("systemName", "Android");
        hashMap.put("systemVersion", Build.VERSION.RELEASE);
        hashMap.put("deviceType", Build.MODEL);
        hashMap.put("deviceBrand", Build.BRAND);
        PackageManager packageManager = component.getApplicationContext().getPackageManager();
        TelephonyManager telephonyManager = (TelephonyManager) component.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        hashMap.put("carrierName", Utility.safeString(telephonyManager.getNetworkOperatorName()));
        if (packageManager.checkPermission("android.permission.READ_PHONE_STATE", component.getApplicationContext().getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            hashMap.put("imei", Utility.safeString(telephonyManager.getDeviceId()));
        }
        hashMap.put("androidId", Utility.safeString(Settings.Secure.getString(component.getApplicationContext().getContentResolver(), "android_id")));
        hashMap.put("countryCode", Utility.safeString(Locale.getDefault().getCountry()));
        hashMap.put("appLanguage", Utility.safeString(component.getShortApplicationLanguageCode()));
        hashMap.put("localization", Utility.safeString(component.getApplicationLanguageCode()));
        hashMap.put("deviceLanguage", Utility.safeString(Locale.getDefault().getLanguage()));
        hashMap.put("deviceLocale", Utility.safeString(Locale.getDefault().toString()));
        hashMap.put("timezone", String.format(Locale.US, "%tZ", Calendar.getInstance()));
        hashMap.put("gmtOffset", String.valueOf(TimeZone.getDefault().getOffset(date.getTime()) / 1000));
        hashMap.put("synergyId", Utility.safeString(component3.getSynergyId()));
        hashMap.put("macAddress", Utility.safeString(component.getMACAddress()));
        hashMap.put("jflag", component.isDeviceRooted() ? Global.NOTIFICATION_DICTIONARY_RESULT_SUCCESS : Global.NOTIFICATION_DICTIONARY_RESULT_FAIL);
        if (!((gameSpecifiedPlayerId = component.getGameSpecifiedPlayerId()) == null || gameSpecifiedPlayerId.length() <= 0)) {
            hashMap.put("gamePlayerId", gameSpecifiedPlayerId);
        }
        try {
            Context applicationContext = ApplicationEnvironment.getComponent().getApplicationContext();
            ApplicationInfo applicationInfo = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), PackageManager.GET_META_DATA);
            str = null;
            if (applicationInfo.metaData != null) {
                str = applicationInfo.metaData.getString("com.facebook.sdk.ApplicationId");
            }
        } catch (PackageManager.NameNotFoundException e2) {
            str = null;
        }
        if (Utility.validString(str)) {
            hashMap.put("fbAppId", str);
        }
        try {
            Cursor query = ApplicationEnvironment.getComponent().getApplicationContext().getContentResolver().query(Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider"), null, null, null, null);
            if (query != null) {
                query.moveToFirst();
                hashMap.put("fbAttrId", query.getString(0));
            }
        } catch (IllegalStateException e3) {
            e3.printStackTrace();
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        hashMap.put("originUser", this.m_loggedInToOrigin ? "Y" : "N");
        int size = this.m_customSessionData.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                hashMap.put(((NimbleTrackingImplBase.SessionData) this.m_customSessionData.get(i)).key, ((NimbleTrackingImplBase.SessionData) this.m_customSessionData.get(i)).value);
            }
        }
        if (!(this.m_currentSessionObject == null || this.m_currentSessionObject.events == null)) {
            ArrayList arrayList = new ArrayList(this.m_currentSessionObject.events);
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Map map = (Map) it.next();
                if (map.containsKey("referrer")) {
                    map.remove("referrer");
                    hashMap.put("referrer", (String) map.get("referrer"));
                }
            }
            hashMap.put("adEvents", arrayList.toString());
        }
        return hashMap;
    }

    private static boolean isS2SEvent(String str) {
        if (str == null) {
            return false;
        }
        return str.startsWith(EVENT_PREFIX);
    }

    @Override // com.ea.nimble.tracking.NimbleTrackingImplBase
    protected boolean canDropSession(List<TrackingBaseSessionObject> list) {
        TrackingBaseSessionObject trackingBaseSessionObject = list.get(0);
        if (trackingBaseSessionObject.events.size() == 0) {
            Log.Helper.LOGE(this, "Trying to drop session with no events");
            return true;
        }
        for (Map<String, String> map : trackingBaseSessionObject.events) {
            String str = map.get("eventType");
            if (str != null && str.equals(String.valueOf((int) EVENT_APPSTARTED_AFTERINSTALL))) {
                return false;
            }
        }
        return true;
    }

    @Override // com.ea.nimble.tracking.NimbleTrackingImplBase
    protected List<Map<String, String>> convertEvent(Tracking.Event event) {
        int i;
        String str;
        String str2;
        String str3;
        String str4;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        if (!Tracking.isNimbleStandardEvent(event.type) && !isS2SEvent(event.type)) {
            return null;
        }
        HashMap hashMap = new HashMap(7);
        if (event.type.equals(Tracking.EVENT_APPSTART_AFTERINSTALL)) {
            i = EVENT_APPSTARTED_AFTERINSTALL;
            str5 = "Launch";
            str4 = null;
            str3 = null;
            str2 = null;
            str = null;
        } else if (event.type.equals(Tracking.EVENT_APPSTART_NORMAL) || event.type.equals(Tracking.EVENT_APPSTART_AFTERUPGRADE) || event.type.equals(Tracking.EVENT_APPSTART_FROMURL)) {
            i = 102;
            str5 = "Launch";
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
        } else if (event.type.equals(Tracking.EVENT_APPSTART_FROMPUSH)) {
            i = 102;
            str5 = "NotificationLaunch";
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
        } else if (event.type.equals(Tracking.EVENT_APPRESUME_NORMAL) || event.type.equals(Tracking.EVENT_SESSION_START) || event.type.equals(Tracking.EVENT_APPRESUME_FROMURL) || event.type.equals(Tracking.EVENT_APPRESUME_FROMEBISU)) {
            i = 103;
            str5 = "Resume";
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
        } else if (event.type.equals(Tracking.EVENT_APPRESUME_FROMPUSH)) {
            i = 103;
            str5 = "NotificationResume";
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
        } else if (event.type.equals(Tracking.EVENT_USER_REGISTERED)) {
            String str8 = event.parameters.get(Tracking.KEY_USERNAME);
            str6 = "username";
            str = null;
            str2 = null;
            str7 = str8;
            str3 = null;
            str4 = null;
            str5 = "Registration";
            i = 104;
            if (str8 == null) {
                Log.Helper.LOGE(this, "Error: missing event parameter \"%s\"", Tracking.KEY_USERNAME);
                str6 = "username";
                str = null;
                str2 = null;
                str7 = str8;
                str3 = null;
                str4 = null;
                str5 = "Registration";
                i = 104;
            }
        } else if (event.type.equals(Tracking.EVENT_MTX_ITEM_PURCHASED)) {
            String str9 = event.parameters.get(Tracking.KEY_MTX_CURRENCY);
            String str10 = event.parameters.get(Tracking.KEY_MTX_PRICE);
            if (str9 == null) {
                Log.Helper.LOGE(this, "Error: missing event parameter \"%s\"", Tracking.KEY_MTX_CURRENCY);
            }
            str6 = "tvalue";
            str = "fvalue";
            str2 = null;
            str7 = str9;
            str3 = str10;
            str4 = null;
            str5 = "Purchase";
            i = 105;
            if (str10 == null) {
                Log.Helper.LOGE(this, "Error: missing event parameter \"%s\"", Tracking.KEY_MTX_PRICE);
                str6 = "tvalue";
                str = "fvalue";
                str2 = null;
                str7 = str9;
                str3 = str10;
                str4 = null;
                str5 = "Purchase";
                i = 105;
            }
        } else if (event.type.equals(Tracking.EVENT_REFERRERID_RECEIVED)) {
            str7 = event.parameters.get(Tracking.KEY_REFERRER_ID);
            if (str7 == null) {
                Log.Helper.LOGE(this, "Error: invalid (null) referrer id.");
                return null;
            }
            i = 106;
            str5 = "Referrer";
            str6 = "referrerId";
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
        } else if (event.type.equals(Tracking.EVENT_TUTORIAL_COMPLETE)) {
            i = 107;
            str5 = "TutorialComplete";
            str = null;
            str2 = null;
            str3 = null;
            str4 = null;
        } else if (event.type.equals(Tracking.EVENT_LEVEL_UP)) {
            i = 108;
            str5 = "LevelUp";
            str6 = "duration";
            str7 = event.parameters.get(Tracking.KEY_DURATION);
            str = "gameplayDuration";
            str3 = event.parameters.get(Tracking.KEY_GAMEPLAY_DURATION);
            str2 = "userLevel";
            str4 = event.parameters.get(Tracking.KEY_USER_LEVEL);
        } else if (!event.type.equals(TrackingS2S.EVENT_CUSTOM)) {
            return null;
        } else {
            i = Integer.parseInt(event.parameters.get("eventType"));
            str6 = event.parameters.get("keyType01");
            str7 = event.parameters.get("keyValue01");
            str = event.parameters.get("keyType02");
            str3 = event.parameters.get("keyValue02");
            str2 = event.parameters.get("keyType03");
            str4 = event.parameters.get("keyValue03");
        }
        hashMap.put("eventType", String.valueOf(i));
        hashMap.put("eventName", str5);
        hashMap.put("timestamp", Utility.getUTCDateStringFormat(event.timestamp));
        hashMap.put("eventKeyType01", str6 == null ? Global.NOTIFICATION_DICTIONARY_RESULT_FAIL : str6);
        hashMap.put("eventValue01", str7 == null ? "" : str7);
        hashMap.put("eventKeyType02", str == null ? Global.NOTIFICATION_DICTIONARY_RESULT_FAIL : str);
        hashMap.put("eventValue02", str3 == null ? "" : str3);
        hashMap.put("eventKeyType03", str2 == null ? Global.NOTIFICATION_DICTIONARY_RESULT_FAIL : str2);
        hashMap.put("eventValue03", str4 == null ? "" : str4);
        hashMap.put("transactionId", UUID.randomUUID().toString());
        if (i == 101 || i == 102 || i == 103) {
            if (this.m_currentSessionObject.sessionData.size() > 0) {
                queueCurrentEventsForPost();
            }
            Log.Helper.LOGD(this, "Logging session start event. Posting event queue now.");
            resetPostTimer(0.0d);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(hashMap);
        return arrayList;
    }

    @Override // com.ea.nimble.tracking.NimbleTrackingImplBase
    protected SynergyRequest createPostRequest(TrackingBaseSessionObject trackingBaseSessionObject) {
        HashMap hashMap = new HashMap();
        hashMap.put("apiVer", "1.0.0");
        String serverUrlWithKey = SynergyEnvironment.getComponent().getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_SYNERGY_S2S);
        if (serverUrlWithKey == null) {
            Log.Helper.LOGI(this, "Tracking server URL from NimbleEnvironment is nil. Adding observer for environment update finish.");
            super.addObserverForSynergyEnvironmentUpdateFinished();
            return null;
        }
        HashMap hashMap2 = new HashMap(trackingBaseSessionObject.sessionData);
        hashMap2.put("now_timestamp", Utility.getUTCDateStringFormat(new Date()));
        if (hashMap2.get("synergyId") == null || hashMap2.get("synergyId").toString().length() == 0) {
            String synergyId = SynergyIdManager.getComponent().getSynergyId();
            if (Utility.validString(synergyId)) {
                Log.Helper.LOGV(this, "Creating post request. No synergyId in session info dictionary, inserting synergyId value %s now.", synergyId);
                hashMap2.put("synergyId", synergyId);
            } else {
                Log.Helper.LOGV(this, "Creating post request. No synergyId in session info dictionary, still no synergyId available now.");
            }
        }
        SynergyRequest synergyRequest = new SynergyRequest(SYNERGY_API_POST_EVENTS, IHttpRequest.Method.POST, null);
        synergyRequest.baseUrl = serverUrlWithKey;
        synergyRequest.urlParameters = hashMap;
        synergyRequest.jsonData = hashMap2;
        return synergyRequest;
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return "com.ea.nimble.trackingimpl.s2s";
    }

    @Override // com.ea.nimble.tracking.NimbleTrackingImplBase, com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "TrackingS2S";
    }

    @Override // com.ea.nimble.tracking.NimbleTrackingImplBase
    protected String getPersistenceIdentifier() {
        return "S2S";
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v30, types: [double] */
    /* JADX WARN: Type inference failed for: r0v9, types: [double] */
    /* JADX WARN: Type inference failed for: r10v1 */
    /* JADX WARN: Type inference failed for: r10v2 */
    /* JADX WARN: Type inference failed for: r10v3, types: [double] */
    /* JADX WARN: Type inference failed for: r10v4 */
    /* JADX WARN: Type inference failed for: r10v5 */
    /* JADX WARN: Type inference failed for: r8v0, types: [double] */
    /* JADX WARN: Type inference failed for: r8v12 */
    /* JADX WARN: Type inference failed for: r8v5 */
    /* JADX WARN: Unknown variable types count: 4 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public double getRetryTime(com.ea.nimble.SynergyNetworkConnectionHandle r7) {
        /*
            Method dump skipped, instructions count: 349
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.tracking.NimbleTrackingS2SImpl.getRetryTime(com.ea.nimble.SynergyNetworkConnectionHandle):double");
    }

    @Override // com.ea.nimble.tracking.NimbleTrackingImplBase
    protected void packageCurrentSession() {
        if (this.m_currentSessionObject.countOfEvents() > 0) {
            this.m_currentSessionObject.sessionData = new HashMap(createEventRequestPostMap());
            if (!((ArrayList) this.m_currentSessionObject.sessionData.get("adEvents")).isEmpty()) {
                queueCurrentEventsForPost();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x007d, code lost:
        if (r6 > -22000) goto L_0x0080;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean shouldAttemptReTrans(com.ea.nimble.SynergyNetworkConnectionHandle r5) {
        Observer.onCallingMethod(Observer.Method.IMPOSSIBLE_TO_DECOMPILE);
        return true;
    }
}
