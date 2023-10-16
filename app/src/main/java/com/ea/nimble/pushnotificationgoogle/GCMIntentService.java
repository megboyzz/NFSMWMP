package com.ea.nimble.pushnotificationgoogle;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import com.ea.nimble.Log.Helper;
import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.Base;
import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;
import com.ea.nimble.tracking.ITracking;
import com.ea.nimble.tracking.Tracking;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/* loaded from: stdlib.jar:com/ea/nimble/pushnotificationgoogle/GCMIntentService.class */
public class GCMIntentService extends GCMBaseIntentService {
    public static final String GCMPersistentMessageID = "GCMMessageId";
    public static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super("927779459434");
    }

    protected static void generateNotification(Context context, String str, String str2, String str3, Bundle bundle) {
        long currentTimeMillis = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String str4 = (String) context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        int identifier = context.getResources().getIdentifier("icon_pushnotification_custom", "drawable", context.getPackageName());
        int i = identifier;
        if (identifier == 0) {
            i = context.getApplicationContext().getApplicationInfo().icon;
        }
        Context applicationContext = context.getApplicationContext();
        Intent launchIntentForPackage = applicationContext.getPackageManager().getLaunchIntentForPackage(applicationContext.getPackageName());
        if (bundle != null) {
            launchIntentForPackage.putExtras(bundle);
        }
        launchIntentForPackage.putExtra("PushNotification", "true");
        if (str2 != null && str2.length() > 0) {
            launchIntentForPackage.putExtra("messageId", str2);
        }
        launchIntentForPackage.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent activity = PendingIntent.getActivity(context, 0, launchIntentForPackage, PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new Notification(i, str, currentTimeMillis);
        if (str3 != null && str3.length() > 0) {
            if (applicationContext.getResources().getIdentifier(str3, "raw", applicationContext.getPackageName()) != 0) {
                notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/raw/" + str3);
            } else {
                Log.e(TAG, "Attempt to play sound file " + str3 + " but the resource was not found");
            }
        }
        //notification.setLatestEventInfo(context, str4, str, activity);
        notification.flags |= 16;
        notificationManager.notify(0, notification);
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onDeletedMessages(Context context, int i) {
        Log.i(TAG, "Received deleted messages notification");
        generateNotification(context, "Message deleted", null, null, null);
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onError(Context context, String str) {
        Log.i(TAG, "Received error: " + str);
        if (str.equals("ACCOUNT_MISSING") && Base.getComponent(PushNotification.COMPONENT_ID) != null) {
            ((IPushNotification) Base.getComponent(PushNotification.COMPONENT_ID)).cleanup();
        }
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onMessage(Context context, Intent intent) {
        Log.v(TAG, "Real - onMessage start");
        String str = "";
        String str2 = "";
        String str3 = "";
        Bundle extras = intent.getExtras();
        Log.v(TAG, "Real - message stuff: " + extras.toString());
        String str4 = "eamobile-message_" + (ApplicationEnvironment.isMainApplicationRunning() ? ApplicationEnvironment.getComponent().getShortApplicationLanguageCode() : Locale.getDefault().getLanguage());
        for (String str5 : extras.keySet()) {
            String str6 = str;
            if (str5.startsWith(str4)) {
                try {
                    str6 = URLDecoder.decode(extras.getString(str5), "UTF-8");
                } catch (Exception e) {
                    str6 = str;
                }
            }
            String str7 = str2;
            if (str5.startsWith("messageId")) {
                Log.v(TAG, "message id is " + extras.getString("messageId"));
                str7 = extras.getString("messageId");
            }
            str = str6;
            str2 = str7;
            if (str5.startsWith("eamobile-song")) {
                str3 = extras.getString("eamobile-song");
                Log.v(TAG, "sound file to play is " + str3);
                str = str6;
                str2 = str7;
            }
        }
        if (str.length() == 0) {
            Log.v(TAG, "*****Recieved PN but message payload did not match app selected language. Suppressing PN*****");
            return;
        }
        try {
            if (ApplicationEnvironment.isMainApplicationRunning()) {
                Log.v(TAG, "Attempting to save PN details to persistent cache. Assumption is that the application is running");
                if (str2 != null && str2.length() > 0) {
                    Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(TAG, Persistence.Storage.CACHE);
                    ArrayList arrayList = (ArrayList) persistenceForNimbleComponent.getValue(GCMPersistentMessageID);
                    ArrayList arrayList2 = arrayList;
                    if (arrayList == null) {
                        arrayList2 = new ArrayList();
                    }
                    arrayList2.add(str2);
                    persistenceForNimbleComponent.setValue(GCMPersistentMessageID, arrayList2);
                    persistenceForNimbleComponent.synchronize();
                }
            }
        } catch (Exception e2) {
            Helper.LOGD(this, "GCM failed to save campaign ID to persistent. App was probably killed.", new Object[0]);
            e2.printStackTrace();
        }
        processIncomingMessage(context, str, str2, str3, extras);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.gcm.GCMBaseIntentService
    public boolean onRecoverableError(Context context, String str) {
        android.util.Log.i(TAG, "Received recoverable error: " + str);
        return super.onRecoverableError(context, str);
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onRegistered(Context context, String str) {
        android.util.Log.i(TAG, "Real - Device registered: regId = " + str);
        PushNotification.register(context, str);
    }

    @Override // com.google.android.gcm.GCMBaseIntentService
    protected void onUnregistered(Context context, String str) {
        android.util.Log.i(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            PushNotification.unregister(context, str);
        } else {
            android.util.Log.i(TAG, "Ignoring unregister callback");
        }
    }

    protected void processIncomingMessage(Context context, String str, String str2, String str3, Bundle bundle) {
        if (!ApplicationEnvironment.isMainApplicationRunning() || ApplicationEnvironment.getCurrentActivity() == null) {
            generateNotification(context, str, str2, str3, bundle);
        } else {
            showMessage(str, str2);
        }
    }

    protected void showMessage(String str, final String str2) {
        if (str != null && ApplicationEnvironment.isMainApplicationRunning()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(ApplicationEnvironment.getCurrentActivity());
            builder.setTitle("");
            builder.setMessage(str);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() { // from class: com.ea.nimble.pushnotificationgoogle.GCMIntentService.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    ITracking iTracking;
                    if (!(Base.getComponent(Tracking.COMPONENT_ID) == null || (iTracking = (ITracking) Base.getComponent(Tracking.COMPONENT_ID)) == null)) {
                        HashMap hashMap = new HashMap();
                        if (str2 != null) {
                            hashMap.put("NIMBLESTANDARD::KEY_PN_MESSAGE_ID", str2);
                        }
                        iTracking.logEvent(Tracking.EVENT_PN_USER_CLICKED_OK, hashMap);
                    }
                    dialogInterface.cancel();
                }
            });
            ApplicationEnvironment.getCurrentActivity().runOnUiThread(new Runnable() { // from class: com.ea.nimble.pushnotificationgoogle.GCMIntentService.2
                @Override // java.lang.Runnable
                public void run() {
                    builder.show();
                }
            });
        }
    }
}
