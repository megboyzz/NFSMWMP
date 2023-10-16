/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Binder
 *  android.os.IBinder
 *  android.os.IInterface
 *  android.os.Parcel
 *  android.os.RemoteException
 */
package com.bda.controller;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IControllerListener
extends IInterface {
    void onKeyEvent(KeyEvent var1) throws RemoteException;

    void onMotionEvent(MotionEvent var1) throws RemoteException;

    void onStateEvent(StateEvent var1) throws RemoteException;

    abstract class Stub
    extends Binder
    implements IControllerListener {
        private static final String DESCRIPTOR = "com.bda.controller.IControllerListener";
        static final int TRANSACTION_onKeyEvent = 1;
        static final int TRANSACTION_onMotionEvent = 2;
        static final int TRANSACTION_onStateEvent = 3;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IControllerListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterface == null) return new Proxy(iBinder);
            if (!(iInterface instanceof IControllerListener)) return new Proxy(iBinder);
            return (IControllerListener)iInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int n2, Parcel object, Parcel parcel, int n3) throws RemoteException {
            switch (n2) {
                default: {
                    return super.onTransact(n2, object, parcel, n3);
                }
                case 1598968902: {
                    parcel.writeString(DESCRIPTOR);
                    return true;
                }
                case 1:
                case 2: {
                    object.enforceInterface(DESCRIPTOR);
                    KeyEvent keyEvent = object.readInt() != 0 ? KeyEvent.CREATOR.createFromParcel(object) : null;
                    this.onKeyEvent(keyEvent);
                    parcel.writeNoException();
                    return true;
                }
                case 3: 
            }
            object.enforceInterface(DESCRIPTOR);
            StateEvent fromParcel = new StateEvent(object);
            if (object.readInt() != 0)
            fromParcel = StateEvent.CREATOR.createFromParcel(object);
            this.onStateEvent(fromParcel);
            parcel.writeNoException();
            return true;
        }

        private static class Proxy
        implements IControllerListener {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override
            public void onKeyEvent(KeyEvent keyEvent) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (keyEvent != null) {
                        parcel.writeInt(1);
                        keyEvent.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(1, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void onMotionEvent(MotionEvent motionEvent) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (motionEvent != null) {
                        parcel.writeInt(1);
                        motionEvent.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(2, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void onStateEvent(StateEvent stateEvent) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (stateEvent != null) {
                        parcel.writeInt(1);
                        stateEvent.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(3, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }
        }
    }
}

