package com.bda.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

abstract class BaseServiceConnection<TService> implements ServiceConnection {
    final Context mContext;
    boolean mIsBound = false;
    TService mService = null;

    BaseServiceConnection(Context context) {
        this.mContext = context;
    }

    public void exit() {
        if (!this.mIsBound) return;
        this.mContext.unbindService((ServiceConnection)this);
        this.mIsBound = false;
    }

    abstract String getServiceIntentName();

    abstract TService getServiceInterface(IBinder var1);

    public boolean init() {
        if (this.mIsBound) return true;
        Intent intent = new Intent(mContext, IControllerService.class);
        this.mContext.startService(intent);
        this.mIsBound = this.mContext.bindService(intent, (ServiceConnection)this, 1);
        return this.mIsBound;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        this.mService = this.getServiceInterface(iBinder);
    }

    public void onServiceDisconnected(ComponentName componentName) {
        this.mService = null;
    }
}

