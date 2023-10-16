package com.ea.games.nfs13_na;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.ea.ironmonkey.C2DMConstants;
import com.google.android.c2dm.C2DMBaseReceiver;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class C2DMReceiver extends C2DMBaseReceiver {
    private static final String TAG = "C2DMReceiver";
    public static List<Bundle> unhandledMessages = new ArrayList();

    public C2DMReceiver() {
        super(C2DMConstants.SENDER_EMAIL);
        Log.i(TAG, "C2DMReceiver Constuctor()");
    }

    private String extractPayload(Bundle bundle) {
        for (String str : bundle.keySet()) {
            if (str.startsWith("eamobile-message")) {
                try {
                    return URLDecoder.decode(bundle.getString(str), "UTF-8");
                } catch (Exception e) {
                    return "";
                }
            }
        }
        return "";
    }

    public void generateNotification(Context context, String str, String str2) {
        int i = Build.VERSION.SDK_INT;
        Log.i("Vj", "Vj:: osVersion - " + i);
        Notification notification = new Notification(i < 10 ? R.drawable.iconb : R.drawable.iconw, str, System.currentTimeMillis());
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClassName(C2DMConstants.SYSTEM_MSG_PACKAGE_RECIPIENT, C2DMConstants.SYSTEM_MSG_CLASS_RECIPIENT);
        //notification.setLatestEventInfo(context, str, str2, PendingIntent.getActivity(context, 0, intent, 0));
        notification.flags |= 16;
        ((NotificationManager) context.getSystemService("notification")).notify(0, notification);
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    public void onError(Context context, String str) {
        Log.e(TAG, "C2DMReceiver onError() " + str);
        Intent intent = new Intent(C2DMConstants.ACTION_ERROR);
        intent.putExtra(C2DMConstants.EXTRA_ERROR_ID, str);
        context.sendBroadcast(intent, null);
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    public void onMessage(Context context, Intent intent) {
        Log.i(TAG, "C2DMReceiver onMessage()");
        Bundle extras = intent.getExtras();
        unhandledMessages.add(extras);
        Intent intent2 = new Intent(C2DMConstants.ACTION_MESSAGE);
        intent2.putExtras(extras);
        context.sendBroadcast(intent2, null);
        generateNotification(context, C2DMConstants.SYSTEM_MSG_TITLE, extractPayload(intent.getExtras()));
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    public void onRegistered(Context context, String str) {
        Log.i(TAG, " onRegistered()");
        Intent intent = new Intent(C2DMConstants.ACTION_REGISTER);
        intent.putExtra(C2DMConstants.EXTRA_REGISTRATION_ID, str);
        context.sendBroadcast(intent, null);
    }

    @Override // com.google.android.c2dm.C2DMBaseReceiver
    public void onUnregistered(Context context) {
        Log.i(TAG, "C2DMReceiver onUnregistered()");
        context.sendBroadcast(new Intent(C2DMConstants.ACTION_UNREGISTER), null);
    }
}
