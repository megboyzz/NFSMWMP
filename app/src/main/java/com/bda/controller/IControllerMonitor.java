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

public interface IControllerMonitor
extends IInterface {
    public void onLog(int var1, int var2, String var3) throws RemoteException;

    public static abstract class Stub
    extends Binder
    implements IControllerMonitor {
        private static final String DESCRIPTOR = "com.bda.controller.IControllerMonitor";
        static final int TRANSACTION_onLog = 1;

        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        public static IControllerMonitor asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterface == null) return new Proxy(iBinder);
            if (!(iInterface instanceof IControllerMonitor)) return new Proxy(iBinder);
            return (IControllerMonitor)iInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int n2, Parcel parcel, Parcel parcel2, int n3) throws RemoteException {
            switch (n2) {
                default: {
                    return super.onTransact(n2, parcel, parcel2, n3);
                }
                case 1598968902: {
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                }
                case 1: 
            }
            parcel.enforceInterface(DESCRIPTOR);
            this.onLog(parcel.readInt(), parcel.readInt(), parcel.readString());
            parcel2.writeNoException();
            return true;
        }

        private static class Proxy
        implements IControllerMonitor {
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
            public void onLog(int n2, int n3, String string2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcel.writeInt(n2);
                    parcel.writeInt(n3);
                    parcel.writeString(string2);
                    this.mRemote.transact(1, parcel, parcel2, 0);
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

