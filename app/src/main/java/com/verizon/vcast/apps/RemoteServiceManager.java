package com.verizon.vcast.apps;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class RemoteServiceManager {
    public static final int ERROR_CONTENT_HANDLER = 100;
    public static final int ERROR_SECURITY = 102;
    public static final String TAG = "RemoteServiceManager";
    protected Context callingContext;
    private String className;
    private volatile Object initLock = new Object();
    private String packageName;
    protected volatile IBinder remoteServiceBinder = null;
    protected volatile RemoteServiceConnection serviceConnection;
    private volatile boolean serviceStarted = false;

    /* access modifiers changed from: package-private */
    public class RemoteServiceConnection implements ServiceConnection {
        RemoteServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RemoteServiceManager.this.remoteServiceBinder = iBinder;
            synchronized (RemoteServiceManager.this.initLock) {
                RemoteServiceManager.this.initLock.notifyAll();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            RemoteServiceManager.this.remoteServiceBinder = null;
            RemoteServiceManager.this.serviceConnection = null;
            synchronized (RemoteServiceManager.this.initLock) {
                RemoteServiceManager.this.initLock.notifyAll();
            }
        }
    }

    public RemoteServiceManager(Context context, String str, String str2) {
        this.callingContext = context;
        this.packageName = str;
        this.className = str2;
    }

    public IBinder getServiceBinder() {
        return this.remoteServiceBinder;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0077, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0078, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0082, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0083, code lost:
        android.util.Log.e(com.verizon.vcast.apps.RemoteServiceManager.TAG, "initializing remote service failed", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0077 A[ExcHandler: SecurityException (r3v0 'e' java.lang.SecurityException A[CUSTOM_DECLARE]), Splitter:B:2:0x0005] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initRemoteService() throws java.lang.SecurityException {
        /*
        // Method dump skipped, instructions count: 142
        */
        throw new UnsupportedOperationException("Method not decompiled: com.verizon.vcast.apps.RemoteServiceManager.initRemoteService():void");
    }

    public void shutDownRemoteService(Context context) {
        Log.i("LicenseAuthenticator", "shutDownRemoteService()");
        try {
            if (this.serviceConnection != null) {
                context.unbindService(this.serviceConnection);
                this.serviceConnection = null;
            }
        } catch (Exception e) {
        }
    }

    public int validateService() {
        if (this.remoteServiceBinder == null || this.serviceConnection == null) {
            try {
                initRemoteService();
            } catch (SecurityException e) {
                Log.e(TAG, "Security error connecting to remote service", e);
                Log.e(TAG, "Likely cause: missing service permission. Required permissions:");
                Log.e(TAG, "  android.permission.READ_PHONE_STATE");
                Log.e(TAG, "  android.permission.START_BACKGROUND_SERVICE");
                Log.e(TAG, "  com.verizon.vcast.apps.VCAST_IN_APP_SERVICE");
                Log.e(TAG, "  com.verizon.vcast.apps.ACCESS_NETWORK_STATE");
                Log.e(TAG, "  android.permission.INTERNET");
                Log.e(TAG, "  com.vzw.appstore.android.permission");
                return 102;
            }
        }
        if (this.remoteServiceBinder != null && this.serviceConnection != null) {
            return 0;
        }
        Log.e(TAG, "Failure connecting to remote service");
        return 100;
    }
}
