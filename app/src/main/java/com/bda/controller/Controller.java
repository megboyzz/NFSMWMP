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
import com.bda.controller.IControllerService;
import com.bda.controller.KeyEvent;
import com.bda.controller.MotionEvent;
import com.bda.controller.StateEvent;

public final class Controller
extends BaseController<IControllerService> {
    static final int CONTROLLER_ID = 1;

    Controller(Context context) {
        super(context);
    }

    public static final Controller getInstance(Context context) {
        return new Controller(context);
    }

    public final float getAxisValue(int n2) {
        if (this.mService == null) return 0.0f;
        try {
            return ((IControllerService)this.mService).getAxisValue(1, n2);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 0.0f;
    }

    @Override
    IControllerListener.Stub getControllerListenerStub() {
        return new IControllerListenerStub();
    }

    @Override
    IControllerMonitor.Stub getControllerMonitorStub() {
        return new BaseController.IControllerMonitorStub();
    }

    public final int getInfo(int n2) {
        if (this.mService == null) return 0;
        try {
            return ((IControllerService)this.mService).getInfo(n2);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 0;
    }

    public final int getKeyCode(int n2) {
        if (this.mService == null) return 1;
        try {
            return ((IControllerService)this.mService).getKeyCode(1, n2);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 1;
    }

    @Override
    String getServiceIntentName() {
        return IControllerService.class.getName();
    }

    @Override
    IControllerService getServiceInterface(IBinder iBinder) {
        return IControllerService.Stub.asInterface(iBinder);
    }

    public final int getState(int n2) {
        if (this.mService == null) return 0;
        try {
            return ((IControllerService)this.mService).getState(1, n2);
        }
        catch (RemoteException remoteException) {
            // empty catch block
        }
        return 0;
    }

    @Override
    void registerListener() {
        if (this.mListener == null) return;
        if (this.mService == null) return;
        try {
            ((IControllerService)this.mService).registerListener(this.mListenerStub, this.mActivityEvent);
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
            ((IControllerService)this.mService).registerMonitor(this.mMonitorStub, this.mActivityEvent);
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
            ((IControllerService)this.mService).sendMessage(n2, n3);
            return;
        }
        catch (RemoteException remoteException) {
            return;
        }
    }

    @Override
    void unregisterListener() {
        if (this.mService == null) return;
        try {
            ((IControllerService)this.mService).unregisterListener(this.mListenerStub, this.mActivityEvent);
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
            ((IControllerService)this.mService).unregisterMonitor(this.mMonitorStub, this.mActivityEvent);
            return;
        }
        catch (RemoteException remoteException) {
            return;
        }
    }

    class IControllerListenerStub
    extends BaseController.IControllerListenerStub {
        IControllerListenerStub() {
        }

        @Override
        public void onKeyEvent(KeyEvent keyEvent) throws RemoteException {
            if (keyEvent.getControllerId() != 1) return;
            super.onKeyEvent(keyEvent);
        }

        @Override
        public void onMotionEvent(MotionEvent motionEvent) throws RemoteException {
            if (motionEvent.getControllerId() != 1) return;
            super.onMotionEvent(motionEvent);
        }

        @Override
        public void onStateEvent(StateEvent stateEvent) throws RemoteException {
            if (stateEvent.getControllerId() != 1) return;
            super.onStateEvent(stateEvent);
        }
    }
}

