/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.ComponentName
 *  android.content.Context
 *  android.os.Handler
 *  android.os.IBinder
 *  android.os.RemoteException
 */
package com.bda.controller;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

abstract class BaseController<TService>
extends BaseServiceConnection<TService> {
    public static final int ACTION_CONNECTED = 1;
    public static final int ACTION_CONNECTING = 2;
    public static final int ACTION_DISCONNECTED = 0;
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_FALSE = 0;
    public static final int ACTION_TRUE = 1;
    public static final int ACTION_UP = 1;
    public static final int AXIS_LTRIGGER = 17;
    public static final int AXIS_RTRIGGER = 18;
    public static final int AXIS_RZ = 14;
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 11;
    public static final int INFO_ACTIVE_DEVICE_COUNT = 2;
    public static final int INFO_KNOWN_DEVICE_COUNT = 1;
    public static final int INFO_UNKNOWN = 0;
    public static final int KEYCODE_BUTTON_A = 96;
    public static final int KEYCODE_BUTTON_B = 97;
    public static final int KEYCODE_BUTTON_L1 = 102;
    public static final int KEYCODE_BUTTON_L2 = 104;
    public static final int KEYCODE_BUTTON_R1 = 103;
    public static final int KEYCODE_BUTTON_R2 = 105;
    public static final int KEYCODE_BUTTON_SELECT = 109;
    public static final int KEYCODE_BUTTON_START = 108;
    public static final int KEYCODE_BUTTON_THUMBL = 106;
    public static final int KEYCODE_BUTTON_THUMBR = 107;
    public static final int KEYCODE_BUTTON_X = 98;
    public static final int KEYCODE_BUTTON_Y = 99;
    public static final int KEYCODE_DPAD_DOWN = 20;
    public static final int KEYCODE_DPAD_LEFT = 21;
    public static final int KEYCODE_DPAD_RIGHT = 22;
    public static final int KEYCODE_DPAD_UP = 19;
    public static final int KEYCODE_UNKNOWN = 0;
    public static final int STATE_CONNECTION = 1;
    public static final int STATE_POWER_LOW = 2;
    public static final int STATE_SELECTED_VERSION = 4;
    public static final int STATE_SUPPORTED_VERSION = 3;
    public static final int STATE_UNKNOWN = 0;
    int mActivityEvent = 6;
    Handler mHandler = null;
    ControllerListener mListener = null;
    final IControllerListener.Stub mListenerStub = this.getControllerListenerStub();
    ControllerMonitor mMonitor = null;
    final IControllerMonitor.Stub mMonitorStub = this.getControllerMonitorStub();

    BaseController(Context context) {
        super(context);
    }

    @Override
    public final void exit() {
        this.setListener(null, null);
        this.setMonitor(null);
        super.exit();
    }

    abstract IControllerListener.Stub getControllerListenerStub();

    abstract IControllerMonitor.Stub getControllerMonitorStub();

    public final void onPause() {
        this.mActivityEvent = 6;
        this.sendMessage(1, this.mActivityEvent);
        this.registerListener();
    }

    public final void onResume() {
        this.mActivityEvent = 5;
        this.sendMessage(1, this.mActivityEvent);
        this.registerListener();
    }

    @Override
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        super.onServiceConnected(componentName, iBinder);
        this.registerListener();
        this.registerMonitor();
        if (this.mActivityEvent != 5) return;
        this.sendMessage(1, this.mActivityEvent);
        this.sendMessage(1, 7);
    }

    abstract void registerListener();

    abstract void registerMonitor();

    abstract void sendMessage(int var1, int var2);

    public final void setListener(ControllerListener controllerListener, Handler handler) {
        this.unregisterListener();
        this.mListener = controllerListener;
        this.mHandler = handler;
        this.registerListener();
    }

    public final void setMonitor(ControllerMonitor controllerMonitor) {
        this.unregisterMonitor();
        this.mMonitor = controllerMonitor;
        this.registerMonitor();
    }

    abstract void unregisterListener();

    abstract void unregisterMonitor();

    class IControllerListenerStub
    extends IControllerListener.Stub {
        IControllerListenerStub() {
        }

        @Override
        public void onKeyEvent(KeyEvent object) throws RemoteException {
            if (BaseController.this.mListener == null) return;
            KeyRunnable keyRunnable = new KeyRunnable(object);
            if (BaseController.this.mHandler != null) {
                BaseController.this.mHandler.post(keyRunnable);
                return;
            }
            keyRunnable.run();
        }

        @Override
        public void onMotionEvent(MotionEvent object) throws RemoteException {
            if (BaseController.this.mListener == null) return;
            MotionRunnable motionRunnable = new MotionRunnable(object);
            if (BaseController.this.mHandler != null) {
                BaseController.this.mHandler.post(motionRunnable);
                return;
            }
            motionRunnable.run();
        }

        @Override
        public void onStateEvent(StateEvent object) throws RemoteException {
            if (BaseController.this.mListener == null) return;
            StateRunnable stateRunnable = new StateRunnable(object);
            if (BaseController.this.mHandler != null) {
                BaseController.this.mHandler.post(stateRunnable);
                return;
            }
            stateRunnable.run();
        }
    }

    class IControllerMonitorStub
    extends IControllerMonitor.Stub {
        IControllerMonitorStub() {
        }

        @Override
        public void onLog(int n2, int n3, String string2) throws RemoteException {
            if (BaseController.this.mMonitor == null) return;
            BaseController.this.mMonitor.onLog(n2, n3, string2);
        }
    }

    class KeyRunnable
    implements Runnable {
        final KeyEvent mEvent;

        public KeyRunnable(KeyEvent keyEvent) {
            this.mEvent = keyEvent;
        }

        @Override
        public void run() {
            if (BaseController.this.mListener == null) return;
            BaseController.this.mListener.onKeyEvent(this.mEvent);
        }
    }

    class MotionRunnable
    implements Runnable {
        final MotionEvent mEvent;

        public MotionRunnable(MotionEvent motionEvent) {
            this.mEvent = motionEvent;
        }

        @Override
        public void run() {
            if (BaseController.this.mListener == null) return;
            BaseController.this.mListener.onMotionEvent(this.mEvent);
        }
    }

    class StateRunnable
    implements Runnable {
        final StateEvent mEvent;

        public StateRunnable(StateEvent stateEvent) {
            this.mEvent = stateEvent;
        }

        @Override
        public void run() {
            if (BaseController.this.mListener == null) return;
            BaseController.this.mListener.onStateEvent(this.mEvent);
        }
    }
}

