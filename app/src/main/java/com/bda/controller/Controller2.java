/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.IBinder
 *  android.os.RemoteException
 */
package com.bda.controller;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.bda.controller.BaseController;
import com.bda.controller.IControllerListener;
import com.bda.controller.IControllerMonitor;
import com.bda.controller.IControllerService2;

final class Controller2
extends BaseController<IControllerService2> {
    Controller2(Context context) {
        super(context);
    }

    public static final Controller2 getInstance(Context context) {
        return new Controller2(context);
    }

    public final float getAxisValue(int n2, int n3) {
        if (this.mService == null) return 0.0f;
        try {
            return ((IControllerService2)this.mService).getAxisValue(n2, n3);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 0.0f;
    }

    @Override
    IControllerListener.Stub getControllerListenerStub() {
        return new BaseController.IControllerListenerStub();
    }

    @Override
    IControllerMonitor.Stub getControllerMonitorStub() {
        return new BaseController.IControllerMonitorStub();
    }

    public final int getInfo(int n2, int n3) {
        if (this.mService == null) return 0;
        try {
            return ((IControllerService2)this.mService).getInfo(n2, n3);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 0;
    }

    public final int getKeyCode(int n2, int n3) {
        if (this.mService == null) return 1;
        try {
            return ((IControllerService2)this.mService).getKeyCode(n2, n3);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 1;
    }

    @Override
    String getServiceIntentName() {
        return IControllerService2.class.getName();
    }

    @Override
    IControllerService2 getServiceInterface(IBinder iBinder) {
        return IControllerService2.Stub.asInterface(iBinder);
    }

    public final int getState(int n2, int n3) {
        if (this.mService == null) return 0;
        try {
            return ((IControllerService2)this.mService).getState(n2, n3);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 0;
    }

    @Override
    void registerListener() {
        if (this.mMonitor == null) return;
        if (this.mService == null) return;
        try {
            ((IControllerService2)this.mService).registerListener(this.mListenerStub, this.mActivityEvent);
            return;
        }
        catch (RemoteException remoteException) {
            return;
        }
    }

    @Override
    void registerMonitor() {
        if (this.mMonitor == null) return;
        if (this.mService == null) return;
        try {
            ((IControllerService2)this.mService).registerMonitor(this.mMonitorStub, this.mActivityEvent);
            return;
        }
        catch (RemoteException remoteException) {
            return;
        }
    }

    @Override
    void sendMessage(int n2, int n3) {
        if (this.mService == null) return;
        try {
            ((IControllerService2)this.mService).sendMessage(n2, n3);
            return;
        }
        catch (RemoteException remoteException) {
            return;
        }
    }

    @Override
    void unregisterListener() {
        if (this.mListener == null) return;
        if (this.mService == null) return;
        try {
            ((IControllerService2)this.mService).unregisterListener(this.mListenerStub, this.mActivityEvent);
            return;
        }
        catch (RemoteException remoteException) {
            return;
        }
    }

    @Override
    void unregisterMonitor() {
        if (this.mService == null) return;
        try {
            ((IControllerService2)this.mService).unregisterMonitor(this.mMonitorStub, this.mActivityEvent);
            return;
        }
        catch (RemoteException remoteException) {
            return;
        }
    }
}

