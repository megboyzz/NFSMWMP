/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.PendingIntent
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  android.content.pm.ActivityInfo
 *  android.content.pm.PackageManager
 *  android.content.pm.PackageManager$NameNotFoundException
 *  android.content.pm.ResolveInfo
 *  android.os.Build$VERSION
 *  android.os.Parcelable
 *  android.util.Log
 */
package com.google.android.gcm;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class GCMRegistrar {
    private static final String BACKOFF_MS = "backoff_ms";
    private static final int DEFAULT_BACKOFF_MS = 3000;
    public static final long DEFAULT_ON_SERVER_LIFESPAN_MS = 604800000L;
    private static final String GSF_PACKAGE = "com.google.android.gsf";
    private static final String PREFERENCES = "com.google.android.gcm";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_ON_SERVER = "onServer";
    private static final String PROPERTY_ON_SERVER_EXPIRATION_TIME = "onServerExpirationTime";
    private static final String PROPERTY_ON_SERVER_LIFESPAN = "onServerLifeSpan";
    private static final String PROPERTY_REG_ID = "regId";
    private static final String TAG = "GCMRegistrar";
    private static GCMBroadcastReceiver sRetryReceiver;
    private static String sRetryReceiverClassName;

    private GCMRegistrar() {
        throw new UnsupportedOperationException();
    }

    public static void checkDevice(Context context) {
        int n2 = Build.VERSION.SDK_INT;
        if (n2 < 8) {
            throw new UnsupportedOperationException("Device must be at least API Level 8 (instead of " + n2 + ")");
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            packageManager.getPackageInfo(GSF_PACKAGE, 0);
            return;
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            throw new UnsupportedOperationException("Device does not have package com.google.android.gsf");
        }
    }

    public static void checkManifest(Context context) {
        PackageManager packageManager2 = context.getPackageManager();
        String packageName = context.getPackageName();//object
        ActivityInfo[] activityInfoArray;
        String permissions = packageName + ".permission.C2D_MESSAGE";

        try {
            packageManager2.getPermissionInfo(packageName, PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            throw new IllegalStateException("Application does not define permission " + permissions);
        }
        PackageInfo packageInfo = new PackageInfo();
        try {
            packageInfo = packageManager2.getPackageInfo(packageName, PackageManager.GET_RECEIVERS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        activityInfoArray = packageInfo.receivers;
        if (activityInfoArray == null) throw new IllegalStateException("No receiver for package " + packageName);
        if (activityInfoArray.length == 0) {
            throw new IllegalStateException("No receiver for package " + packageName);
        }
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "number of receivers for " + packageName + ": " + activityInfoArray.length);
        }
        HashSet<String> hashSet = new HashSet<>();
        for (ActivityInfo activityInfo : activityInfoArray) {
            if (!"com.google.android.c2dm.permission.SEND".equals(activityInfo.permission)) continue;
            hashSet.add(activityInfo.name);
        }
        if (hashSet.isEmpty()) {
            throw new IllegalStateException("No receiver allowed to receive com.google.android.c2dm.permission.SEND");
        }
        GCMRegistrar.checkReceiver(context, hashSet, "com.google.android.c2dm.intent.REGISTRATION");
        GCMRegistrar.checkReceiver(context, hashSet, "com.google.android.c2dm.intent.RECEIVE");
    }

    private static void checkReceiver(Context object, Set<String> set, String string) {
        PackageManager packageManager = object.getPackageManager();
        String packageName = object.getPackageName();
        Intent intent = new Intent(string);
        intent.setPackage(packageName);
        @SuppressLint("WrongConstant") List<ResolveInfo> resolveInfos = packageManager.queryBroadcastReceivers(intent, PackageManager.GET_INTENT_FILTERS);
        if (resolveInfos.isEmpty()) {
            throw new IllegalStateException("No receivers for action " + string);
        }
        if (Log.isLoggable(TAG, (int)2)) {
            Log.v(TAG, "Found " + resolveInfos.size() + " receivers for action " + string);
        }
        Iterator<ResolveInfo> iterator = resolveInfos.iterator();
        do {
            if (!iterator.hasNext()) return;
        } while (set.contains(string = iterator.next().activityInfo.name));
        throw new IllegalStateException("Receiver " + string + " is not set with permission " + "com.google.android.c2dm.permission.SEND");
    }

    static String clearRegistrationId(Context context) {
        return GCMRegistrar.setRegistrationId(context, "");
    }

    private static int getAppVersion(Context context) {
        try {
            return context.getPackageManager().getPackageInfo((String)context.getPackageName(), (int)0).versionCode;
        }
        catch (PackageManager.NameNotFoundException nameNotFoundException) {
            throw new RuntimeException("Coult not get package name: " + (Object)((Object)nameNotFoundException));
        }
    }

    static int getBackoff(Context context) {
        return GCMRegistrar.getGCMPreferences(context).getInt(BACKOFF_MS, 3000);
    }

    static String getFlatSenderIds(String ... stringArray) {
        if (stringArray == null) throw new IllegalArgumentException("No senderIds");
        if (stringArray.length == 0) {
            throw new IllegalArgumentException("No senderIds");
        }
        StringBuilder stringBuilder = new StringBuilder(stringArray[0]);
        int n2 = 1;
        while (n2 < stringArray.length) {
            stringBuilder.append(',').append(stringArray[n2]);
            ++n2;
        }
        return stringBuilder.toString();
    }

    private static SharedPreferences getGCMPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES, 0);
    }

    public static long getRegisterOnServerLifespan(Context context) {
        return GCMRegistrar.getGCMPreferences(context).getLong(PROPERTY_ON_SERVER_LIFESPAN, 604800000L);
    }

    public static String getRegistrationId(Context context) {
        SharedPreferences gcmPreferences = GCMRegistrar.getGCMPreferences(context);
        String string = gcmPreferences.getString(PROPERTY_REG_ID, "");
        int n2 = gcmPreferences.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int n3 = GCMRegistrar.getAppVersion(context);
        if (n2 == Integer.MIN_VALUE) return string;
        if (n2 == n3) return string;
        Log.v(TAG, "App version changed from " + n2 + " to " + n3 + "; resetting registration id");
        GCMRegistrar.clearRegistrationId(context);
        return "";
    }

    static void internalRegister(Context context, String ... object) {
        String flatSenderIds = GCMRegistrar.getFlatSenderIds(object);
        Log.v(TAG, "Registering app " + context.getPackageName() + " of senders " + flatSenderIds);
        Intent intent = new Intent("com.google.android.c2dm.intent.REGISTER");
        intent.setPackage(GSF_PACKAGE);
        intent.putExtra("app", PendingIntent.getBroadcast(context, 0, new Intent(), 0));
        intent.putExtra("sender", flatSenderIds);
        context.startService(intent);
    }

    static void internalUnregister(Context context) {
        Log.v((String)TAG, (String)("Unregistering app " + context.getPackageName()));
        Intent intent = new Intent("com.google.android.c2dm.intent.UNREGISTER");
        intent.setPackage(GSF_PACKAGE);
        intent.putExtra("app", (Parcelable)PendingIntent.getBroadcast((Context)context, (int)0, (Intent)new Intent(), (int)0));
        context.startService(intent);
    }

    public static boolean isRegistered(Context context) {
        if (GCMRegistrar.getRegistrationId(context).length() <= 0) return false;
        return true;
    }

    public static boolean isRegisteredOnServer(Context context) {
        SharedPreferences gcmPreferences = GCMRegistrar.getGCMPreferences(context);
        boolean bl2 = gcmPreferences.getBoolean(PROPERTY_ON_SERVER, false);
        Log.v(TAG, "Is registered on server: " + bl2);
        if (!bl2) return false;
        long l2 = gcmPreferences.getLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, -1L);
        if (System.currentTimeMillis() <= l2) return true;
        Log.v(TAG, "flag expired on: " + new Timestamp(l2));
        return false;
    }

    public static void onDestroy(Context context) {
        synchronized (GCMRegistrar.class) {
            if (sRetryReceiver == null) return;
            Log.v(TAG, "Unregistering receiver");
            context.unregisterReceiver((BroadcastReceiver)sRetryReceiver);
            sRetryReceiver = null;
        }
    }

    public static void register(Context context, String ... stringArray) {
        GCMRegistrar.resetBackoff(context);
        GCMRegistrar.internalRegister(context, stringArray);
    }

    static void resetBackoff(Context context) {
        Log.d((String)TAG, (String)("resetting backoff for " + context.getPackageName()));
        GCMRegistrar.setBackoff(context, 3000);
    }

    static void setBackoff(Context context, int n2) {
        SharedPreferences.Editor edit = GCMRegistrar.getGCMPreferences(context).edit();
        edit.putInt(BACKOFF_MS, n2);
        edit.apply();
    }

    public static void setRegisterOnServerLifespan(Context context, long l2) {
        SharedPreferences.Editor edit = GCMRegistrar.getGCMPreferences(context).edit();
        edit.putLong(PROPERTY_ON_SERVER_LIFESPAN, l2);
        edit.apply();
    }

    public static void setRegisteredOnServer(Context context, boolean bl2) {
        SharedPreferences.Editor editor = GCMRegistrar.getGCMPreferences(context).edit();
        editor.putBoolean(PROPERTY_ON_SERVER, bl2);
        long l2 = GCMRegistrar.getRegisterOnServerLifespan(context);
        l2 = System.currentTimeMillis() + l2;
        Log.v((String)TAG, (String)("Setting registeredOnServer status as " + bl2 + " until " + new Timestamp(l2)));
        editor.putLong(PROPERTY_ON_SERVER_EXPIRATION_TIME, l2);
        editor.apply();
    }

    static String setRegistrationId(Context context, String string) {
        SharedPreferences sharedPreferences = GCMRegistrar.getGCMPreferences(context);
        String string2 = sharedPreferences.getString(PROPERTY_REG_ID, "");
        int n2 = GCMRegistrar.getAppVersion(context);
        Log.v(TAG, "Saving regId on app version " + n2);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(PROPERTY_REG_ID, string);
        edit.putInt(PROPERTY_APP_VERSION, n2);
        edit.commit();
        return string2;
    }

    static void setRetryBroadcastReceiver(Context context) {
        synchronized (GCMRegistrar.class) {
            if (sRetryReceiver != null) return;
            if (sRetryReceiverClassName == null) {
                Log.e(TAG, "internal error: retry receiver class not set yet");
                sRetryReceiver = new GCMBroadcastReceiver();
            } else {
                try {
                    sRetryReceiver = (GCMBroadcastReceiver) Class.forName(sRetryReceiverClassName).newInstance();
                }
                catch (Exception exception) {
                    Log.e(TAG, "Could not create instance of " + sRetryReceiverClassName + ". Using " + GCMBroadcastReceiver.class.getName() + " directly.");
                    sRetryReceiver = new GCMBroadcastReceiver();
                }
            }
            String string = context.getPackageName();
            IntentFilter intentFilter = new IntentFilter("com.google.android.gcm.intent.RETRY");
            intentFilter.addCategory(string);
            string = string + ".permission.C2D_MESSAGE";
            Log.v(TAG, "Registering receiver");
            context.registerReceiver((BroadcastReceiver)sRetryReceiver, intentFilter, string, null);
            return;
        }
    }

    static void setRetryReceiverClassName(String string) {
        Log.v(TAG, "Setting the name of retry receiver class to " + string);
        sRetryReceiverClassName = string;
    }

    public static void unregister(Context context) {
        GCMRegistrar.resetBackoff(context);
        GCMRegistrar.internalUnregister(context);
    }
}

