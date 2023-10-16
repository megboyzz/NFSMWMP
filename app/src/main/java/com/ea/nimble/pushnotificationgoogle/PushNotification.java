/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.util.Log
 */
package com.ea.nimble.pushnotificationgoogle;

import static com.ea.easp.Debug.Log.d;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.Base;
import com.ea.nimble.Log.Helper;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.SynergyIdManager;
import com.ea.nimble.SynergyNetwork;
import com.ea.nimble.SynergyNetworkConnectionCallback;
import com.ea.nimble.SynergyNetworkConnectionHandle;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentity;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PushNotification {
    public static final String COMPONENT_ID = "com.ea.nimble.pushnotificationgoogle";
    static String s_registerId = null;

    static void callSynergyRevokePushTokenByPid(String string2) {
        Helper.LOGIS("PN", "GCM- SYNERGY ID2.0 registering device (regId = " + string2 + ")");
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("pids", new String[]{string2});
        hashMap.put("sellId", Utility.safeString(SynergyEnvironment.getComponent().getSellId()));
        hashMap.put("clientApiVersion", "1.0.1");
        hashMap.put("hwId", SynergyEnvironment.getComponent().getEAHardwareId());
        SynergyNetwork.getComponent().sendPostRequest(SynergyEnvironment.getComponent().getServerUrlWithKey("synergy.m2u"), "/m2u/api/android/revokePids", null, hashMap, new SynergyNetworkConnectionCallback() {

            @Override
            public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                if (synergyNetworkConnectionHandle.getResponse().getError() == null) {
                    Helper.LOGD(this, "Push Token revoke sent to synergy. Status code: " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
                    return;
                }
                Helper.LOGD(this, "Error: Push Token revoke unable to be sent. " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
            }
        });
    }

    static void callSynergyStorePushTokenByPid(String string2) {
        if (!Utility.validString(string2)) {
            return;
        }
        PushNotification.callSynergyStorePushTokenByPidArray(new String[]{string2});
    }

    private static void callSynergyStorePushTokenByPidArray(String[] stringArray) {
        Helper.LOGIS("PN", "GCM- SYNERGY registering device with pids. PIDS WERE FOUND.");
        if (!Utility.validString(s_registerId)) {
            Helper.LOGW("PN", "No valid push token was found. GCM registration has failed. Please check the log above for a reason.\n Aborting storing token to synergy");
            return;
        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("pids", stringArray);
        hashMap.put("registrationId", s_registerId);
        hashMap.put("language", Utility.safeString(ApplicationEnvironment.getComponent().getShortApplicationLanguageCode()));
        hashMap.put("localization", Utility.safeString(ApplicationEnvironment.getComponent().getApplicationLanguageCode()));
        hashMap.put("deviceLanguage", Utility.safeString(Locale.getDefault().getLanguage()));
        hashMap.put("deviceLocale", Utility.safeString(Locale.getDefault().toString()));
        hashMap.put("sellId", Utility.safeString(SynergyEnvironment.getComponent().getSellId()));
        hashMap.put("network", "1");
        hashMap.put("clientApiVersion", "1.0.1");
        hashMap.put("hwId", SynergyEnvironment.getComponent().getEAHardwareId());
        SynergyNetwork.getComponent().sendPostRequest(SynergyEnvironment.getComponent().getServerUrlWithKey("synergy.m2u"), "/m2u/api/android/storePids", null, hashMap, new SynergyNetworkConnectionCallback() {

            @Override
            public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                if (synergyNetworkConnectionHandle.getResponse().getError() == null) {
                    Helper.LOGD(this, "Push Token sent to synergy. Status code: " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
                    return;
                }
                Helper.LOGD(this, "Error: Push Token unable to be sent. " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
            }
        });
    }

    public static IPushNotification getComponent() {
        return (IPushNotification) ((Object) Base.getComponent(COMPONENT_ID));
    }

    private static void initialize() {
        Helper.LOGDS("PN", "initialize");
        Base.registerComponent(new PushNotificationImpl(), COMPONENT_ID);
    }

    static boolean register(Context object, final String string2) {
        boolean bl2;
        boolean bl3 = bl2 = false;
        if (!ApplicationEnvironment.isMainApplicationRunning()) return bl3;
        bl3 = bl2;
        if (ApplicationEnvironment.getCurrentActivity() == null) return bl3;
        Helper.LOGDS("PN", "Push token returned from OS. Attempting to register token with snyergy backend.");
        if (SynergyEnvironment.getComponent().getServerUrlWithKey("synergy.m2u") != null) {
            PushNotification.registerTokenWithSynergy(object, string2);
            return true;
        }
        Helper.LOGDS("PN", "Synergy backend URL is missing. /GetDirection is probably not finished or callback has not fired. Waiting... ");
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                Helper.LOGDS("PN", "Synergy PN URL found/startup is finished. Try PNs now.");
                Bundle extras = intent.getExtras();
                if (extras == null) return;
                if (!extras.getString("result").equals("1")) return;
                PushNotification.registerTokenWithSynergy(context, string2);
            }
        };
        Utility.registerReceiver("nimble.environment.notification.startup_requests_finished", broadcastReceiver);
        Utility.registerReceiver("nimble.environment.notification.restored_from_persistent", broadcastReceiver);
        return false;
    }

    static void registerTokenWithSynergy(Context context, String string2) {
        boolean bl2;
        boolean bl3 = bl2 = false;
        if (Base.getComponent("com.ea.nimble.identity") != null) {
            List<INimbleIdentityAuthenticator> list = ((INimbleIdentity) ((Object) Base.getComponent("com.ea.nimble.identity"))).getAuthenticators();
            bl3 = bl2;
            if (list != null) {
                bl3 = bl2;
                if (!list.isEmpty()) {
                    PushNotification.registerTokenWithSynergyID20Internal(context, string2);
                    bl3 = true;
                }
            }
        }
        if (bl3) return;
        PushNotification.registerTokenWithSynergyInternal(context, string2);
    }

    private static void registerTokenWithSynergyID20Internal(Context object, String list) {
        s_registerId = list;
        if (Base.getComponent("com.ea.nimble.identity") == null) return;
        INimbleIdentity component = (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add(SynergyIdManager.getComponent().getSynergyId());
        if (arrayList.isEmpty()) return;
        PushNotification.callSynergyStorePushTokenByPidArray(arrayList.toArray(new String[0]));
    }

    private static void registerTokenWithSynergyInternal(final Context context, String string2) {
        Helper.LOGIS("PN", "GCM- SYNERGY registering device (regId = " + string2 + ") using synergyId. PIDS WERE NOT FOUND.");
        s_registerId = string2;
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("uid", Utility.safeString(SynergyIdManager.getComponent().getSynergyId()));
        hashMap.put("registrationId", string2);
        hashMap.put("language", Utility.safeString(ApplicationEnvironment.getComponent().getShortApplicationLanguageCode()));
        hashMap.put("localization", Utility.safeString(ApplicationEnvironment.getComponent().getApplicationLanguageCode()));
        hashMap.put("deviceLanguage", Utility.safeString(Locale.getDefault().getLanguage()));
        hashMap.put("deviceLocale", Utility.safeString(Locale.getDefault().toString()));
        hashMap.put("sellId", Utility.safeString(SynergyEnvironment.getComponent().getSellId()));
        hashMap.put("network", "1");
        SynergyNetwork.getComponent().sendGetRequest(SynergyEnvironment.getComponent().getServerUrlWithKey("synergy.m2u"), "/m2u/api/android/storePushRegistrationId", hashMap, new SynergyNetworkConnectionCallback() {

            @Override
            public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                if (synergyNetworkConnectionHandle.getResponse().getError() != null) return;
                Helper.LOGD(this, "GCM Push Token registered with synergy. Status code: " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
            }
        });
    }

    static void unregister(Context object, String object2) {
        ArrayList object3 = new ArrayList();
        Log.d((String) "PN", (String) ("GCM - unregistering device (regId = " + (String) object2 + ")"));
        ArrayList<String> arrayList = new ArrayList<String>();
        if (Base.getComponent("com.ea.nimble.identity") != null && ((INimbleIdentity) ((Object) Base.getComponent("com.ea.nimble.identity"))).getAuthenticators() != null) {
            for (int i2 = 0; i2 < object3.size(); ++i2) {
                if (((INimbleIdentityAuthenticator) object3.get(i2)).getPidInfo() == null || !Utility.validString(((INimbleIdentityAuthenticator) object3.get(i2)).getPidInfo().getPid()))
                    continue;
                arrayList.add(((INimbleIdentityAuthenticator) object3.get(i2)).getPidInfo().getPid());
            }
        }


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pids", arrayList);
        hashMap.put("registrationId", object2);
        hashMap.put("language", Utility.safeString(ApplicationEnvironment.getComponent().getShortApplicationLanguageCode()));
        hashMap.put("localization", Utility.safeString(ApplicationEnvironment.getComponent().getApplicationLanguageCode()));
        hashMap.put("deviceLanguage", Utility.safeString(Locale.getDefault().getLanguage()));
        hashMap.put("deviceLocale", Utility.safeString(Locale.getDefault().toString()));
        hashMap.put("network", "1");
        hashMap.put("clientApiVersion", "1.0.0");
        SynergyNetwork.getComponent().sendPostRequest((String) object2, "/m2u/api/android/revokePids", null, null, new SynergyNetworkConnectionCallback() {

            @Override
            public void callback(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                if (synergyNetworkConnectionHandle.getResponse().getError() == null) {
                    Helper.LOGD(this, "Push Token removed to synergy. Status code: " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
                    return;
                }
                Helper.LOGD(this, "Error: Push Token removal unable to be sent. " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
            }
        });


        HashMap<String, String> object5 = new HashMap<>();

        object5.put("uid", Utility.safeString(SynergyIdManager.getComponent().getSynergyId()));
        object5.put("sellId", Utility.safeString(SynergyEnvironment.getComponent().getSellId()));
        final Context finalContext = object;
        SynergyNetwork.getComponent().sendGetRequest(SynergyEnvironment.getComponent().getServerUrlWithKey("synergy.m2u"), "/m2u/api/android/revokePushRegistrationId", object5, synergyNetworkConnectionHandle -> {
            if (synergyNetworkConnectionHandle.getResponse().getError() != null) return;
            d("GCM", "GCM Push Token unregistered with synergy. Status code: " + synergyNetworkConnectionHandle.getResponse().getHttpResponse().getStatusCode());
        });
    }
}

