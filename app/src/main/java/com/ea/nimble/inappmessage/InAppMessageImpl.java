/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.AlertDialog$Builder
 *  android.content.BroadcastReceiver
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.DialogInterface
 *  android.content.DialogInterface$OnClickListener
 *  android.content.Intent
 *  android.net.Uri
 *  android.provider.Settings$Secure
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ea.nimble.inappmessage;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.Component;
import com.ea.nimble.IApplicationEnvironment;
import com.ea.nimble.ISynergyEnvironment;
import com.ea.nimble.ISynergyIdManager;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.SynergyIdManager;
import com.ea.nimble.SynergyNetwork;
import com.ea.nimble.SynergyNetworkConnectionCallback;
import com.ea.nimble.SynergyNetworkConnectionHandle;
import com.ea.nimble.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;

public class InAppMessageImpl
extends Component
implements LogSource,
IInAppMessage {
    private BroadcastReceiver m_receiver;
    private SynergyNetworkConnectionHandle m_synergyNetworkConnectionHandle;

    static /* synthetic */ SynergyNetworkConnectionHandle access$102(InAppMessageImpl inAppMessageImpl, SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
        inAppMessageImpl.m_synergyNetworkConnectionHandle = synergyNetworkConnectionHandle;
        return synergyNetworkConnectionHandle;
    }

    private static void addMessageToCache(Message message) {
        int n2;
        if (message == null) {
            return;
        }
        Persistence persistence = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.inappmessage", Persistence.Storage.CACHE);
        Serializable serializable = persistence.getValue("messageExcludeID");
        int n3 = n2 = -1;
        if (serializable != null) {
            n3 = n2;
            if (serializable.getClass() == Integer.class) {
                try {
                    n3 = (Integer)serializable;
                }
                catch (ClassCastException classCastException) {
                    Log.Helper.LOGES("IAM", "Invalid persistence value for excludeID, expected Integer");
                    n3 = n2;
                }
            }
        }
        if (message.m_messageID <= n3) return;
        persistence.setValue("currentInAppMessage", message);
        persistence.synchronize();
        Utility.sendBroadcast("nimble.inappmessage.notification.message_refresh", null);
    }

    private Message getMessageFromCache() {
        Message message = null;
        Message message2 = (Message)PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.inappmessage", Persistence.Storage.CACHE).getValue("currentInAppMessage");
        if (message2 == null) return message;
        return message2;
    }

    private void refreshInAppMessage() {
        Log.Helper.LOGD(this, "refresh in app message cache");
        IApplicationEnvironment iApplicationEnvironment = ApplicationEnvironment.getComponent();
        ISynergyEnvironment iSynergyEnvironment = SynergyEnvironment.getComponent();
        ISynergyIdManager iSynergyIdManager = SynergyIdManager.getComponent();
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("language", iApplicationEnvironment.getShortApplicationLanguageCode());
        hashMap.put("localization", iApplicationEnvironment.getApplicationLanguageCode());
        hashMap.put("deviceLanguage", Locale.getDefault().getLanguage());
        hashMap.put("deviceLocale", Locale.getDefault().toString());
        hashMap.put("apiVer", "1.0.1");
        hashMap.put("appVer", iApplicationEnvironment.getApplicationVersion());
        hashMap.put("hwId", iSynergyEnvironment.getEAHardwareId());
        hashMap.put("sellId", SynergyEnvironment.getComponent().getSellId());
        hashMap.put("uid", iSynergyIdManager.getSynergyId());
        hashMap.put("type", "4");
        hashMap.put("excludeIds", "");
        this.m_synergyNetworkConnectionHandle = SynergyNetwork.getComponent().sendGetRequest(SynergyEnvironment.getComponent().getServerUrlWithKey("synergy.m2u"), "/m2u/api/core/getMessage", hashMap, new SynergyNetworkConnectionCallback(){

            /*
             * Enabled unnecessary exception pruning
             */
            @Override
            public void callback(SynergyNetworkConnectionHandle object) {
                Log.Helper.LOGD(this, "IAM callback done is status code: " + object.getResponse().getHttpResponse().getStatusCode());
                InAppMessageImpl.access$102(InAppMessageImpl.this, null);
                if (object.getResponse().getError() != null) return;
                try {
                    JSONObject jsonObject = new JSONObject(object.getResponse().getJsonData());
                    String string2 = jsonObject.getString("resultCode");
                    Log.Helper.LOGD(this, "getMessage result code " + string2 + "~");
                    if (string2.compareTo("-50005") == 0) {
                        return;
                    }
                    if (string2.compareTo("1") != 0) return;
                    Log.Helper.LOGD(this, "getMessage BODY: " + object);
                    String string3 = jsonObject.getString("message");
                    String string4 = jsonObject.getString("title");
                    Object object2 = jsonObject.getString("url");
                    int n2 = jsonObject.getInt("messageId");
                    string2 = "";
                    String shortApplicationLanguageCode = ApplicationEnvironment.getComponent().getShortApplicationLanguageCode();

                    try {
                        object2 = (HttpURLConnection)new URL((String)object2).openConnection();
                        ((URLConnection)object2).setConnectTimeout(15000);
                        ((URLConnection)object2).setReadTimeout(15000);
                        ((HttpURLConnection)object2).setInstanceFollowRedirects(false);
                        ((URLConnection)object2).connect();
                        object2 = ((URLConnection)object2).getHeaderField("Location");
                        String string5 = Settings.Secure.getString((ContentResolver)ApplicationEnvironment.getComponent().getApplicationContext().getContentResolver(), (String)"android_id");
                        String string6 = ApplicationEnvironment.getComponent().getGoogleAdvertisingId();
                        string5 = (String)object2 + "&android_id=" + string5;
                        object2 = string5;
                        if (string6 != null) {
                            object2 = string5;
                            if (string6.length() == 0) {
                                object2 = string5 + "&google_aid=" + string6;
                            }
                        }
                        InAppMessageImpl.addMessageToCache(new Message(n2, string4, string3, (String)object2, string2, null, null));
                        return;
                    }
                    catch (Exception exception) {
                        System.out.println("error happened: " + exception.toString());
                    }
                    return;
                }
                catch (JSONException jSONException) {
                    jSONException.printStackTrace();
                    return;
                }
            }
        });
    }

    private boolean refreshInAppMessageCache() {
        if (SynergyEnvironment.getComponent().isDataAvailable()) {
            this.refreshInAppMessage();
            return true;
        }
        if (this.m_receiver != null) return false;
        this.m_receiver = new BroadcastReceiver(){

            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                if (extras == null) return;
                if (!extras.getString("result").equals("1")) return;
                InAppMessageImpl.this.refreshInAppMessage();
            }
        };
        Utility.registerReceiver("nimble.environment.notification.startup_requests_finished", this.m_receiver);
        Utility.registerReceiver("nimble.environment.notification.restored_from_persistent", this.m_receiver);
        return false;
    }

    private void removeMessageFromCache(Message message) {
        if (message == null) {
            Log.Helper.LOGD(this, "Removing msg from cache but no message to remove");
            return;
        }
        Persistence persistence = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.inappmessage", Persistence.Storage.CACHE);
        Message message2 = (Message)persistence.getValue("currentInAppMessage");
        if (message2 == null) {
            Log.Helper.LOGD(this, "Removing message from cache but nothing in the cache");
            return;
        }
        if (message2.m_messageID != message.m_messageID) return;
        Log.Helper.LOGD(this, "Removing message from cache. Removed successfully");
        persistence.setValue("currentInAppMessage", null);
        persistence.setValue("messageExcludeID", Integer.valueOf(message.m_messageID));
        persistence.synchronize();
    }

    @Override
    public void cleanup() {
        Log.Helper.LOGD(this, "cleanup");
        SynergyNetworkConnectionHandle synergyNetworkConnectionHandle = this.m_synergyNetworkConnectionHandle;
        if (synergyNetworkConnectionHandle != null) {
            Log.Helper.LOGD(this, "Canceling network connection.");
            synergyNetworkConnectionHandle.cancel();
            this.m_synergyNetworkConnectionHandle = null;
        }
        if (this.m_receiver == null) return;
        Utility.unregisterReceiver(this.m_receiver);
        this.m_receiver = null;
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.inappmessage";
    }

    @Override
    public String getLogSourceTitle() {
        return "IAM";
    }

    @Override
    public Message popMessageFromCache() {
        Message message = this.getMessageFromCache();
        if (message != null) {
            this.removeMessageFromCache(message);
            Log.Helper.LOGV(this, "----- BEGIN POPPED IAM INFO -----");
            Log.Helper.LOGV(this, "messageId = " + message.getMessageId());
            Log.Helper.LOGV(this, "title = " + message.getTitle());
            Log.Helper.LOGV(this, "message = " + message.getMessage());
            Log.Helper.LOGV(this, "url = " + message.getUrl());
            Log.Helper.LOGV(this, "buttonLabel1 = " + message.buttonLabel1Title());
            Log.Helper.LOGV(this, "buttonLabel2 = " + message.buttonLabel2Title());
            Log.Helper.LOGV(this, "buttonLabel3 = " + message.buttonLabel3Title());
            Log.Helper.LOGV(this, "----- END POPPED IAM INFO -----");
            return message;
        }
        Log.Helper.LOGD(this, "No message in cache to display info for.");
        return null;
    }

    @Override
    public void restore() {
        Log.Helper.LOGD(this, "restore");
        this.refreshInAppMessageCache();
    }

    @Override
    public void resume() {
        Log.Helper.LOGD(this, "resume");
        this.refreshInAppMessageCache();
    }

    @Override
    public void setup() {
        Log.Helper.LOGD(this, "setup");
    }

    @Override
    public void showInAppMessage() {
        final Message message = (Message)PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.inappmessage", Persistence.Storage.CACHE).getValue("currentInAppMessage");
        if (message == null) {
            return;
        }
        this.removeMessageFromCache(message);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationEnvironment.getCurrentActivity());
        builder.setTitle(message.m_title);
        builder.setMessage(message.m_message);
        if (message.m_buttonLabel1Title != null && message.m_url != null && !message.m_url.equals("")) {
            builder.setPositiveButton(message.m_buttonLabel1Title, (dialogInterface, n2) -> {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(message.m_url));
                ApplicationEnvironment.getCurrentActivity().startActivity(intent);
            });
        }
        if (message.m_buttonLabel2Title != null) {
            builder.setNegativeButton((CharSequence)message.m_buttonLabel2Title, (dialogInterface, n2) -> dialogInterface.cancel());
        }
        ApplicationEnvironment.getCurrentActivity().runOnUiThread(builder::show);
    }

    @Override
    public void suspend() {
        if (this.m_synergyNetworkConnectionHandle == null) return;
        Log.Helper.LOGD(this, "Canceling network connection.");
        this.m_synergyNetworkConnectionHandle.cancel();
        this.m_synergyNetworkConnectionHandle = null;
    }
}

