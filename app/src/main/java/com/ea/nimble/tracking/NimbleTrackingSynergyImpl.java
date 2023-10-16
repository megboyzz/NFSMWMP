/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.Intent
 *  android.os.Build$VERSION
 *  android.provider.Settings$Secure
 */
package com.ea.nimble.tracking;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.ea.ironmonkey.devmenu.util.Observer;
import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.IApplicationEnvironment;
import com.ea.nimble.IHttpRequest;
import com.ea.nimble.INetwork;
import com.ea.nimble.ISynergyEnvironment;
import com.ea.nimble.ISynergyIdManager;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Network;
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
import java.util.Random;
import java.util.Set;

class NimbleTrackingSynergyImpl
        extends NimbleTrackingImplBase
        implements LogSource {
    private static final String EVENT_PREFIX = "SYNERGYTRACKING::";
    private static final int MAX_CUSTOM_EVENT_PARAMETERS = 20;
    private int m_eventNumber;
    private Map<String, String> m_mainAuthenticator;
    private final BroadcastReceiver m_mainAuthenticatorUpdateReceiver;
    private List<Map<String, String>> m_pendingEvents;
    private final BroadcastReceiver m_pidInfoUpdateReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, final Intent intent) {
            NimbleTrackingSynergyImpl.this.m_threadManager.runInWorkerThread(new Runnable() {

                @Override
                public void run() {
                    NimbleTrackingSynergyImpl.this.onPidInfoUpdate(intent);
                }
            });
        }
    };
    private Map<String, String> m_pidMap;
    private String m_sessionId;
    private SynergyIdChangedReceiver m_synergyIdChangedReceiver;

    NimbleTrackingSynergyImpl() {
        this.m_mainAuthenticatorUpdateReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, final Intent intent) {
                NimbleTrackingSynergyImpl.this.m_threadManager.runInWorkerThread(() -> NimbleTrackingSynergyImpl.this.onMainAuthenticatorUpdate(intent));
            }
        };
        this.m_synergyIdChangedReceiver = new SynergyIdChangedReceiver();
        this.m_pendingEvents = new ArrayList<Map<String, String>>();
    }

    private void addPushTNGTrackingParams(Tracking.Event event, Map<String, String> map) {
        map.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MESSAGEID.value));
        map.put("eventValue01", event.parameters.get("NIMBLESTANDARD::KEY_PN_MESSAGE_ID"));
        map.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
        map.put("eventValue02", event.parameters.get("NIMBLESTANDARD::KEY_PN_MESSAGE_TYPE"));
        map.put("eventKeyType03", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
        map.put("eventValue03", event.parameters.get("NIMBLESTANDARD::KEY_PN_DEVICE_ID"));
    }

    /*
     * Unable to fully structure code
     */
    private Map<String, Object> generateSessionInfoDictionary(String var1_1) {
        ISynergyEnvironment var12_2 = SynergyEnvironment.getComponent();
        ISynergyIdManager var10_3 = SynergyIdManager.getComponent();
        IApplicationEnvironment var9_4 = ApplicationEnvironment.getComponent();
        HashMap<String, Object> var8_5 = new HashMap();
        String var6_6 = "";
        Boolean var4_7 = true;
        try {
            String var7_8 = ApplicationEnvironment.getComponent().getGoogleAdvertisingId();
            var4_7 = ApplicationEnvironment.getComponent().isLimitAdTrackingEnabled();
            var6_6 = var7_8;
            var8_5.put("advertiserID", var6_6);
        } catch (Exception var7_9) {
            Log.Helper.LOGW(this, "Exception when getting advertising ID for Android");
        }
        var8_5.put("limitAdTracking", var4_7);
        String var7_8 = var12_2.getSellId();
        String var11_11 = var12_2.getEAHardwareId();
        String eaDeviceId = var12_2.getEADeviceId(); // var12_2
        String var13_12 = Build.VERSION.RELEASE;
        var6_6 = ApplicationEnvironment.getComponent().getCarrier();
        String var14_13 = ApplicationEnvironment.getComponent().getApplicationVersion();
        String var15_14 = String.format(Locale.US, "%tZ", Calendar.getInstance());
        var8_5.put("carrier", var6_6);
        var8_5.put("timezone", var15_14);
        var6_6 = var9_4.isAppCracked() ? "1" : "0";
        var8_5.put("pflag", var6_6);
        var6_6 = var9_4.isDeviceRooted() ? "1" : "0";
        var8_5.put("jflag", var6_6);
        var8_5.put("firmwareVer", var13_12);
        var8_5.put("sellId", Utility.safeString(var7_8));
        var8_5.put("buildId", Utility.safeString(var14_13));
        var8_5.put("sdkVer", "1.23.14.1217");
        var8_5.put("sdkCfg", "DL");
        var8_5.put("deviceId", Utility.safeString(eaDeviceId));
        var8_5.put("hwId", Utility.safeString(var11_11));
        var8_5.put("schemaVer", "2");
        var8_5.put("platform", "android");
        var6_6 = "N";
        INetwork component = Network.getComponent();//var7_8
        if (component.getStatus() == Network.Status.OK) {
            var6_6 = component.isNetworkWifi() ? "W" : "G";
        }
        var8_5.put("networkAccess", var6_6);
        var6_6 = this.m_loggedInToOrigin ? "Y" : "N";
        var8_5.put("originUser", var6_6);
        if (Utility.validString(var1_1)) {
            var8_5.put("uid", Utility.safeString(var1_1));
            var8_5.put("androidId", Utility.safeString(Settings.Secure.getString((ContentResolver) var9_4.getApplicationContext().getContentResolver(), (String) "android_id")));
            var8_5.put("macHash", Utility.safeString(Utility.SHA256HashString(var9_4.getMACAddress())));
            var8_5.put("aut", Utility.safeString(""));
            if (this.m_pidMap != null && this.m_pidMap.size() > 0) {
                var8_5.put("pidMap", this.m_pidMap);
            }
            if ((var1_1 = var9_4.getGameSpecifiedPlayerId()) != null && var1_1.length() > 0) {
                var8_5.put("gamePlayerId", var1_1);
            }
            if (this.m_customSessionData.size() <= 0) return var8_5;

            for (SessionData base : this.m_customSessionData) {
                var8_5.put(base.key, base.value);
            }
        }
        return var8_5;
    }

    private String generateSynergySessionId() {
        Object object = Utility.getUTCDateStringFormat(new Date()).replace("_", "");
        StringBuilder stringBuilder = new StringBuilder(24);
        stringBuilder.append((String) object);
        int n2 = stringBuilder.length();
        object = new Random();
        int n3 = 0;
        while (n3 < 24 - n2) {
            stringBuilder.append(((Random) object).nextInt(10));
            ++n3;
        }
        return stringBuilder.toString();
    }

    private static boolean isSynergyEvent(String string2) {
        if (string2 != null) return string2.startsWith(EVENT_PREFIX);
        return false;
    }

    private void onMainAuthenticatorUpdate(Intent object) {
        Observer.onCallingMethod(Observer.Method.SUSPICIOUS_METHOD);
    }

    private void onPidInfoUpdate(Intent object) {
        if ((object.getSerializableExtra("pidMapId")) == null) return;
        this.m_currentSessionObject.sessionData.put("pidMap", this.m_pidMap);
    }

    private void onSynergyIdChanged(Intent object) {
        String string2 = object.getStringExtra("previousSynergyId");
        String currentSynergyId = object.getStringExtra("currentSynergyId");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("eventType", String.valueOf(SynergyConstants.EVT_SESSION_END_SYNERGYID_CHANGE.value));
        hashMap.put("keyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_SYNERGYID.value));
        hashMap.put("keyValue01", Utility.safeString(string2));
        hashMap.put("keyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_SYNERGYID.value));
        hashMap.put("keyValue02", Utility.safeString(currentSynergyId));
        this.logEvent("SYNERGYTRACKING::CUSTOM", hashMap);
        this.m_currentSessionObject.sessionData = new HashMap<>(this.generateSessionInfoDictionary(string2));
        this.queueCurrentEventsForPost();
        hashMap.put("eventType", String.valueOf(SynergyConstants.EVT_NEW_SESSION_START_SYNERGYID_CHANGE.value));
        this.logEvent("SYNERGYTRACKING::CUSTOM", hashMap);
    }

    /*
     * Exception decompiling
     */
    private void parseCustomParameters(Map<String, String> var1_1, Map<String, String> var2_2) {
        Observer.onCallingMethod(Observer.Method.IMPOSSIBLE_TO_DECOMPILE, Observer.Method.SUSPICIOUS_METHOD);
    }

    private void resetSession() {
        this.m_sessionId = this.generateSynergySessionId();
        this.m_eventNumber = 1;
    }

    private void sleep() {
        Utility.unregisterReceiver(this.m_synergyIdChangedReceiver);
    }

    private void wakeup() {
        Utility.registerReceiver("nimble.synergyidmanager.notification.synergy_id_changed", this.m_synergyIdChangedReceiver);
    }

    @Override
    protected boolean canDropSession(List<TrackingBaseSessionObject> iterator) {
        String string2;
        TrackingBaseSessionObject trackingBaseSessionObject = iterator.get(0);
        if (trackingBaseSessionObject.events.size() == 0) {
            Log.Helper.LOGE(this, "Trying to drop session with no events");
            return true;
        }
        Iterator<Map<String, String>> iterator1 = trackingBaseSessionObject.events.iterator();
        do {
            if (!iterator1.hasNext()) return true;
        } while ((string2 = iterator1.next().get("eventType")) == null || !string2.equals(String.valueOf(SynergyConstants.EVT_APPSTART_AFTERINSTALL.value)));
        return false;
    }

    @Override
    protected void cleanup() {
        this.sleep();
        super.cleanup();
    }

    /*
     * Loose catch block
     * Enabled unnecessary exception pruning
     */
    @Override
    protected List<Map<String, String>> convertEvent(Tracking.Event arrayList) {
        Object object;
        Object object2222222;
        HashMap<String, String> hashMap;
        Object object3;
        Object object4;

        object4 = SynergyConstants.EVT_UNDEFINED;
        object3 = -1;
        hashMap = new HashMap<String, String>();
        if (!Tracking.isNimbleStandardEvent(((Tracking.Event) ((Object) arrayList)).type) && !NimbleTrackingSynergyImpl.isSynergyEvent(((Tracking.Event) ((Object) arrayList)).type)) {
            return null;
        }

        switch (arrayList.type) {
            case "NIMBLESTANDARD::APPSTART_NORMAL": {
                object4 = SynergyConstants.EVT_APPSTART_NORMALLY;
            }
            case "NIMBLESTANDARD::APPSTART_AFTERINSTALL": {
                object4 = SynergyConstants.EVT_APPSTART_AFTERINSTALL;
            }
            case "NIMBLESTANDARD::APPSTART_AFTERUPGRADE": {
                object4 = SynergyConstants.EVT_APPSTART_AFTERUPGRADE;
            }
            case "NIMBLESTANDARD::APPSTART_FROMURL": {
                object4 = SynergyConstants.EVT_APPSTART_FROM_URL;
            }
            case "NIMBLESTANDARD::APPSTART_FROMPUSH": {
                object4 = SynergyConstants.EVT_APPSTART_FROMPUSH;
                try {
                    object2222222 = ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).getString("messageId", null);
                    if (object2222222 == null) {
                        this.addPushTNGTrackingParams((Tracking.Event) ((Object) arrayList), (Map<String, String>) hashMap);
                        object2222222 = object3;
                    }
                    hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MESSAGEID.value));
                    hashMap.put("eventValue01", (String) object2222222);
                    ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).edit().remove("messageId").commit();
                    ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).edit().remove("PushNotification").commit();
                    object2222222 = object3;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    object2222222 = object3;
                }
            }
            case "NIMBLESTANDARD::APPRESUME_FROMURL": {
                object4 = SynergyConstants.EVT_APP_ENTER_FOREGROUND_FROM_URL;
            }
            case "NIMBLESTANDARD::APPRESUME_FROMEBISU": {
                object4 = SynergyConstants.EVT_APP_ENTER_FOREGROUND_FROM_EBISU;
            }
            case "NIMBLESTANDARD::APPRESUME_FROMPUSH": {
                object4 = SynergyConstants.EVT_APP_RESUME_FROM_PUSH;
                try {
                    object2222222 = ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).getString("messageId", null);
                    if (object2222222 == null) {
                        this.addPushTNGTrackingParams((Tracking.Event) ((Object) arrayList), (Map<String, String>) hashMap);
                        object2222222 = object3;
                    }
                    hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MESSAGEID.value));
                    hashMap.put("eventValue01", (String) object2222222);
                    ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).edit().remove("messageId").commit();
                    ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).edit().remove("PushNotification").commit();
                    object2222222 = object3;
                } catch (Exception exception) {
                    exception.printStackTrace();
                    object2222222 = object3;
                }
            }
            case "NIMBLESTANDARD::SESSION_START": {
                object4 = SynergyConstants.EVT_APP_SESSION_START;
            }
            case "NIMBLESTANDARD::SESSION_END": {
                object4 = SynergyConstants.EVT_APP_SESSION_END;
            }
            case "NIMBLESTANDARD::SESSION_TIME": {
                object4 = SynergyConstants.EVT_APP_SESSION_TIME;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_DURATION.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_DURATION"));
            }
            case "NIMBLESTANDARD::MTX_ITEM_BEGIN_PURCHASE": {
                object4 = SynergyConstants.EVT_MTXVIEW_ITEMPURCHASE;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MTX_SELLID.value));
                hashMap.put("eventValue01", ((Tracking.Event) ((Object) arrayList)).parameters.get("NIMBLESTANDARD::KEY_MTX_SELLID"));
            }
            case "NIMBLESTANDARD::MTX_ITEM_PURCHASED": {
                object4 = SynergyConstants.EVT_MTXVIEW_ITEM_PURCHASED;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MTX_SELLID.value));
                hashMap.put("eventValue01", ((Tracking.Event) ((Object) arrayList)).parameters.get("NIMBLESTANDARD::KEY_MTX_SELLID"));
            }
            case "NIMBLESTANDARD::MTX_FREEITEM_DOWNLOADED": {
                object4 = SynergyConstants.EVT_MTXVIEW_FREEITEM_DOWNLOADED;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MTX_SELLID.value));
                hashMap.put("eventValue01", ((Tracking.Event) ((Object) arrayList)).parameters.get("NIMBLESTANDARD::KEY_MTX_SELLID"));
            }
            case "NIMBLESTANDARD::USER_TRACKING_OPTOUT": {
                object4 = SynergyConstants.EVT_USER_TRACKING_OPTOUT;
            }
            case "NIMBLESTANDARD::PN_DISPLAY_OPT_IN": {
                object4 = SynergyConstants.EVT_USER_SHOWN_PN_OPTIN_PROMPT;
            }
            case "NIMBLESTANDARD::PN_USER_OPT_IN": {
                object4 = SynergyConstants.EVT_USER_SHOWN_PN_OPTIN_PROMPT;
                hashMap.put("eventKeyType02", ((Tracking.Event) ((Object) arrayList)).parameters.get(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue02", "Yes");
            }
            case "NIMBLESTANDARD::PN_SHOWN_TO_USER": {
                object = SynergyConstants.EVT_PN_SHOWN_TO_USER;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MESSAGEID.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_PN_MESSAGE_ID"));
                if (arrayList.parameters.containsKey("NIMBLESTANDARD::KEY_PN_MESSAGE_ID")) {
                    hashMap.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                    hashMap.put("eventValue02", arrayList.parameters.get("NIMBLESTANDARD::KEY_PN_MESSAGE_TYPE"));
                }
                object4 = object;
                if (arrayList.parameters.containsKey("NIMBLESTANDARD::KEY_PN_DEVICE_ID")) {
                    hashMap.put("eventKeyType03", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                    hashMap.put("eventValue03", arrayList.parameters.get("NIMBLESTANDARD::KEY_PN_DEVICE_ID"));
                }
            }
            case "NIMBLESTANDARD::PN_RECEIVED": {
                object4 = SynergyConstants.EVT_PN_RECEIVED;
                this.addPushTNGTrackingParams(arrayList, hashMap);
            }
            case "NIMBLESTANDARD::PN_DEVICE_REGISTERED": {
                object4 = SynergyConstants.EVT_PN_DEVICE_REGISTERED;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue01", ((Tracking.Event) ((Object) arrayList)).parameters.get("NIMBLESTANDARD::KEY_PN_DATE_OF_BIRTH"));
                hashMap.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue02", ((Tracking.Event) ((Object) arrayList)).parameters.get("NIMBLESTANDARD::KEY_PN_DISABLED_FLAG"));
                hashMap.put("eventKeyType03", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue03", ((Tracking.Event) ((Object) arrayList)).parameters.get("NIMBLESTANDARD::KEY_PN_DEVICE_ID"));
            }
            case "NIMBLESTANDARD::PN_USER_CLICKED_OK": {
                object4 = SynergyConstants.EVT_PN_SHOWN_TO_USER;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_MESSAGEID.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_PN_MESSAGE_ID"));
                hashMap.put("eventKeyType02", arrayList.parameters.get(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue02", "Ok");
            }
            case "NIMBLESTANDARD::IDENTITY_MIGRATION": {
                object4 = SynergyConstants.EVT_IDENTITY_MIGRATION;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_MIGRATION_GAME_TRIGGERED"));
                hashMap.put("eventValue02", arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_SOURCE"));
                hashMap.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_JSON_MAP.value));
            }
            case "NIMBLESTANDARD::IDENTITY_LOGIN": {
                object4 = SynergyConstants.EVT_IDENTITY_LOGIN;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_JSON_MAP.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_PIDMAP_LOGIN"));
                hashMap.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_JSON_MAP.value));
                hashMap.put("eventValue02", arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_TARGET"));

                HashMap<String, String> hashMap1 = new HashMap<>();

                Set<Map.Entry<String, Object>> entries =
                        Utility
                                .convertJSONObjectStringToMap(
                                        arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_PIDMAP_LOGIN"
                                        )).entrySet();

                for (Map.Entry<String, Object> object5 : entries)
                    hashMap1.put(object5.getKey(), object5.getValue().toString());

                Set<Map.Entry<String, Object>> entries1 = Utility
                        .convertJSONObjectStringToMap(
                                arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_TARGET"
                                )).entrySet();

                for (Map.Entry<String, Object> object5 : entries1)
                    hashMap1.put(object5.getKey(), object5.getValue().toString());
                this.m_pidMap = hashMap1;
                this.m_currentSessionObject.sessionData.put("pidMap", this.m_pidMap);
            }
            case "NIMBLESTANDARD::IDENTITY_LOGOUT": {
                object4 = SynergyConstants.EVT_IDENTITY_LOGOUT;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_JSON_MAP.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_SOURCE"));
                hashMap.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_JSON_MAP.value));
                hashMap.put("eventValue02", arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_PIDMAP_LOGOUT"));

                HashMap<String, String> hashMap1 = new HashMap<>();

                Set<Map.Entry<String, Object>> entries =
                        Utility
                                .convertJSONObjectStringToMap(
                                        arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_PIDMAP_LOGOUT"
                                        )).entrySet();

                for (Map.Entry<String, Object> object5 : entries)
                    hashMap1.put(object5.getKey(), object5.getValue().toString());

                this.m_currentSessionObject.sessionData.put("pidMap", this.m_pidMap);
                this.m_pidMap = hashMap1;
            }
            case "NIMBLESTANDARD::IDENTITY_MIGRATION_STARTED": {
                object4 = SynergyConstants.EVT_IDENTITY_MIGRATION_STARTED;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_MIGRATION_GAME_TRIGGERED"));
                hashMap.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_JSON_MAP.value));
                hashMap.put("eventValue02", arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_SOURCE"));
                hashMap.put("eventKeyType03", String.valueOf(SynergyConstants.EVT_KEYTYPE_JSON_MAP.value));
                hashMap.put("eventValue03", arrayList.parameters.get("NIMBLESTANDARD::KEY_IDENTITY_TARGET"));
            }
            case "NIMBLESTANDARD::TUTORIAL_COMPLETE": {
                object4 = SynergyConstants.EVT_GAMEPLAY_PROGRESSION_TUTORIAL_COMPLETE;
            }
            case "NIMBLESTANDARD::LEVEL_UP": {
                object4 = SynergyConstants.EVT_GP_LEVEL_PROMOTION;
                hashMap.put("eventKeyType01", String.valueOf(SynergyConstants.EVT_KEYTYPE_DURATION.value));
                hashMap.put("eventValue01", arrayList.parameters.get("NIMBLESTANDARD::KEY_DURATION"));
                hashMap.put("eventKeyType02", String.valueOf(SynergyConstants.EVT_KEYTYPE_DURATION.value));
                hashMap.put("eventValue02", arrayList.parameters.get("NIMBLESTANDARD::KEY_GAMEPLAY_DURATION"));
                hashMap.put("eventKeyType03", String.valueOf(SynergyConstants.EVT_KEYTYPE_ENUMERATION.value));
                hashMap.put("eventValue03", arrayList.parameters.get("NIMBLESTANDARD::KEY_USER_LEVEL"));
            }
            case "SYNERGYTRACKING::CUSTOM":
                return null;
        }

        object4 = arrayList.parameters.get("eventType");
        int n2 = Integer.parseInt((String) object4);
        object4 = SynergyConstants.fromInt(n2);
        this.parseCustomParameters(arrayList.parameters, hashMap);


        Log.Helper.LOGE(this, "Error: Invalid format for eventType parameter. Expected integer value, got " + (String) object4);
        Observer.onCallingMethod(Observer.Method.VERY_SUSPICIOUS_METHOD);
        return null;
/*
        while (object.hasNext()) {
            String object5;
            object5 = (String) object.next();
            object3 = hashMap.get(object5);
            if (!Utility.validString((String) object3) || !((String) object3).startsWith("${") || !((String) object3).endsWith("}"))
                continue;
            if ((object3 = (String) this.m_trackingAttributes.get(((String) object3).substring(2, ((String) object3).length() - 1))) == null) {
                object3 = "";
            }
            hashMap.put((String) object5, (String) object3);
        }

        object3 = Utility.getUTCDateStringFormat(arrayList.timestamp);
        arrayList = new ArrayList<Map<String, String>>();
        if ((Integer) object2222222 != -1) {
            hashMap.put("eventType", String.valueOf(object2222222));
        } else {
            hashMap.put("eventType", String.valueOf(object4.value));
        }
        hashMap.put("timestamp", (String) object3);
        if (object4.isSessionStartEventType()) {
            if (this.m_currentSessionObject.sessionData.size() > 0) {
                this.queueCurrentEventsForPost();
            }
            this.resetSession();
            for (Object object2222222 : this.m_pendingEvents) {
                object2222222.put("session", this.m_sessionId);
                object2222222.put("step", String.valueOf(this.m_eventNumber));
                ++this.m_eventNumber;
                arrayList.add((Map<String, String>) object2222222);
            }
            this.m_pendingEvents.clear();
        } else if (this.m_sessionId == null) {
            this.m_pendingEvents.add(hashMap);
            return null;
        }
        hashMap.put("session", this.m_sessionId);
        hashMap.put("step", String.valueOf(this.m_eventNumber));
        ++this.m_eventNumber;
        if (object4.isSessionStartEventType()) {
            Log.Helper.LOGD(this, "Logging session start event, %s. Posting event queue now.", object4);
            this.resetPostTimer(0.0);
        }
        if (object4 == SynergyConstants.EVT_APP_SESSION_END) {
            this.m_sessionId = null;
        }
        arrayList.add(hashMap);
        return arrayList;
        */

    }

    @Override
    protected SynergyRequest createPostRequest(TrackingBaseSessionObject object) {
        Observer.onCallingMethod(Observer.Method.HARD_TO_RECOVER_LOGIC);
        return new SynergyRequest("lol", IHttpRequest.Method.POST, var1 -> Observer.onCallingMethod(Observer.Method.SUSPICIOUS_METHOD));
        /*
        Object object2 = SynergyEnvironment.getComponent().getServerUrlWithKey("synergy.tracking");
        if (object2 == null) {
            Log.Helper.LOGI(this, "Tracking server URL from NimbleEnvironment is nil. Adding observer for environment update finish.");
            this.addObserverForSynergyEnvironmentUpdateFinished();
            return null;
        }
        Object object3 = object.sessionData;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.putAll((Map<String, Object>) object3);
        hashMap.put("now_timestamp", Utility.getUTCDateStringFormat(new Date()));
        object3 = new ArrayList<>(object.events);
        for (int i2 = 0; i2 < object3.size(); ++i2) {
            ((Map) object3.get(i2)).put("repostCount", String.valueOf(object.repostCount));
        }
        hashMap.put("events", object3);
        if (hashMap.get("uid") == null) {
            object = SynergyIdManager.getComponent().getSynergyId();
            if (Utility.validString((String) object)) {
                Log.Helper.LOGV(this, "Creating post request. No uid in session info dictionary, inserting uid value %s now.", object);
                hashMap.put("uid", object);
            } else {
                Log.Helper.LOGV(this, "Creating post request. No uid in session info dictionary, still no uid available now.");
            }
        }
        object = SynergyEnvironment.getComponent();
        object3 = hashMap.get("sellId").toString();
        if (object3 == null || ((String) object3).equals("")) {
            object3 = Utility.safeString(object.getSellId());
            if (object3 == null || ((String) object3).equals("")) {
                Log.Helper.LOGE(this, "Creating POST request. Missing sell id.");
            } else {
                hashMap.put("sellId", object3);
            }
        }
        if ((object3 = hashMap.get("hwId").toString()) == null || ((String) object3).equals("")) {
            object3 = Utility.safeString(object.getEAHardwareId());
            if (object3 == null || ((String) object3).equals("")) {
                Log.Helper.LOGE(this, "Creating POST request. Missing hw id.");
            } else {
                hashMap.put("hwId", object3);
            }
        }
        if ((object3 = hashMap.get("deviceId").toString()) == null || ((String) object3).equals("")) {
            if ((object = Utility.safeString(object.getEADeviceId())) == null || ((String) object).equals("")) {
                Log.Helper.LOGE(this, "Creating POST request. Missing device id.");
            } else {
                hashMap.put("deviceId", object);
            }
        }
        object = new SynergyRequest("/tracking/api/core/logEvent", IHttpRequest.Method.POST, null);
        ((SynergyRequest) object).baseUrl = object2;
        ((SynergyRequest) object).jsonData = hashMap;
        object2 = OperationalTelemetryDispatch.getComponent();
        if (object2 != null) {
            object3 = new HashMap<String, String>();
            ((HashMap) object3).put("BASEURL", ((SynergyRequest) object).baseUrl);
            ((HashMap) object3).put("API", ((SynergyRequest) object).api);
            ((HashMap) object3).put("POSTDATA", Utility.convertObjectToJSONString(hashMap));
            object2.logEvent("com.ea.nimble.trackingimpl.synergy", (Map<String, String>) object3);
        }
        Utility.sendBroadcast("nimble.notification.trackingimpl.synergy.postingToServer", null);
        return object;

         */
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.trackingimpl.synergy";
    }

    @Override
    public String getLogSourceTitle() {
        return "TrackingSynergy";
    }

    @Override
    protected String getPersistenceIdentifier() {
        return "Synergy";
    }

    @Override
    protected boolean isSameSession(TrackingBaseSessionObject object, TrackingBaseSessionObject object2) {
        if (((TrackingBaseSessionObject) object).events.size() == 0 || ((TrackingBaseSessionObject) object2).events.size() == 0) {
            Log.Helper.LOGE(this, "Trying to compare session with no events");
            return true;
        }
        String session = object.events.get(0).get("session");
        String session2 = object2.events.get(0).get("session");
        if (session != null) {
            if (session2 != null) return session.equals(session2);
        }
        Log.Helper.LOGE(this, "Trying to compare event with no session");
        return true;
    }

    @Override
    protected void packageCurrentSession() {
        if (this.m_currentSessionObject.countOfEvents() <= 0) return;
        Log.Helper.LOGV(this, "Preparing for post, generating session info dictionary.");
        this.m_currentSessionObject.sessionData = new HashMap<String, Object>(this.generateSessionInfoDictionary(null));
        this.queueCurrentEventsForPost();
    }

    @Override
    protected void restore() {
        super.restore();
        Utility.registerReceiver("nimble.notification.identity.authenticator.pid.info.update", this.m_pidInfoUpdateReceiver);
        Utility.registerReceiver("nimble.notification.identity.main.authenticator.change", this.m_mainAuthenticatorUpdateReceiver);
        this.wakeup();
    }

    @Override
    public void setEnable(boolean bl2) {
        super.setEnable(bl2);
        if (!bl2) {
            this.m_sessionId = null;
        }
        if (this.m_sessionId != null) return;
        if (!bl2) return;
        this.resetSession();
        this.logEvent("NIMBLESTANDARD::SESSION_START", null);
    }

    private class SynergyIdChangedReceiver
            extends BroadcastReceiver {
        private SynergyIdChangedReceiver() {
        }

        public void onReceive(Context context, final Intent intent) {
            NimbleTrackingSynergyImpl.this.m_threadManager.runInWorkerThread(new Runnable() {

                @Override
                public void run() {
                    NimbleTrackingSynergyImpl.this.onSynergyIdChanged(intent);
                }
            });
        }
    }
}

