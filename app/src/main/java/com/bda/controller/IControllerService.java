package com.bda.controller;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

interface IControllerService extends IInterface {
    float getAxisValue(int var1, int var2) throws RemoteException;

    int getInfo(int var1) throws RemoteException;

    int getKeyCode(int var1, int var2) throws RemoteException;

    int getState(int var1, int var2) throws RemoteException;

    void registerListener(IControllerListener var1, int var2) throws RemoteException;

    void registerMonitor(IControllerMonitor var1, int var2) throws RemoteException;

    void sendMessage(int var1, int var2) throws RemoteException;

    void unregisterListener(IControllerListener var1, int var2) throws RemoteException;

    void unregisterMonitor(IControllerMonitor var1, int var2) throws RemoteException;

    abstract class Stub
    extends Binder
    implements IControllerService {
        private static final String DESCRIPTOR = "com.bda.controller.IControllerService";
        static final int TRANSACTION_getAxisValue = 7;
        static final int TRANSACTION_getInfo = 5;
        static final int TRANSACTION_getKeyCode = 6;
        static final int TRANSACTION_getState = 8;
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_registerMonitor = 3;
        static final int TRANSACTION_sendMessage = 9;
        static final int TRANSACTION_unregisterListener = 2;
        static final int TRANSACTION_unregisterMonitor = 4;

        Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        static IControllerService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (iInterface == null) return new Proxy(iBinder);
            if (!(iInterface instanceof IControllerService)) return new Proxy(iBinder);
            return (IControllerService)iInterface;
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
                case 1: {
                    parcel.enforceInterface(DESCRIPTOR);
                    this.registerListener(IControllerListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 2: {
                    parcel.enforceInterface(DESCRIPTOR);
                    this.unregisterListener(IControllerListener.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 3: {
                    parcel.enforceInterface(DESCRIPTOR);
                    this.registerMonitor(IControllerMonitor.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 4: {
                    parcel.enforceInterface(DESCRIPTOR);
                    this.unregisterMonitor(IControllerMonitor.Stub.asInterface(parcel.readStrongBinder()), parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 5: {
                    parcel.enforceInterface(DESCRIPTOR);
                    n2 = this.getInfo(parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n2);
                    return true;
                }
                case 6: {
                    parcel.enforceInterface(DESCRIPTOR);
                    n2 = this.getKeyCode(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n2);
                    return true;
                }
                case 7: {
                    parcel.enforceInterface(DESCRIPTOR);
                    float f2 = this.getAxisValue(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeFloat(f2);
                    return true;
                }
                case 8: {
                    parcel.enforceInterface(DESCRIPTOR);
                    n2 = this.getState(parcel.readInt(), parcel.readInt());
                    parcel2.writeNoException();
                    parcel2.writeInt(n2);
                    return true;
                }
                case 9: 
            }
            parcel.enforceInterface(DESCRIPTOR);
            this.sendMessage(parcel.readInt(), parcel.readInt());
            parcel2.writeNoException();
            return true;
        }

        private static class Proxy
        implements IControllerService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override
            public float getAxisValue(int n2, int n3) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcel.writeInt(n2);
                    parcel.writeInt(n3);
                    this.mRemote.transact(7, parcel, parcel2, 0);
                    parcel2.readException();
                    float f2 = parcel2.readFloat();
                    return f2;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public int getInfo(int n2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcel.writeInt(n2);
                    this.mRemote.transact(5, parcel, parcel2, 0);
                    parcel2.readException();
                    n2 = parcel2.readInt();
                    return n2;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override
            public int getKeyCode(int n2, int n3) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcel.writeInt(n2);
                    parcel.writeInt(n3);
                    this.mRemote.transact(6, parcel, parcel2, 0);
                    parcel2.readException();
                    n2 = parcel2.readInt();
                    return n2;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public int getState(int n2, int n3) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcel.writeInt(n2);
                    parcel.writeInt(n3);
                    this.mRemote.transact(8, parcel, parcel2, 0);
                    parcel2.readException();
                    n2 = parcel2.readInt();
                    return n2;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void registerListener(IControllerListener iControllerListener, int n2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    iControllerListener = iControllerListener != null ? (IControllerListener) iControllerListener.asBinder() : null;
                    parcel.writeStrongBinder((IBinder)iControllerListener);
                    parcel.writeInt(n2);
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
            public void registerMonitor(IControllerMonitor iControllerMonitor, int n2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    iControllerMonitor = iControllerMonitor != null ? (IControllerMonitor) iControllerMonitor.asBinder() : null;
                    parcel.writeStrongBinder((IBinder)iControllerMonitor);
                    parcel.writeInt(n2);
                    this.mRemote.transact(3, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void sendMessage(int n2, int n3) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcel.writeInt(n2);
                    parcel.writeInt(n3);
                    this.mRemote.transact(9, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void unregisterListener(IControllerListener iControllerListener, int n2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    iControllerListener = iControllerListener != null ? (IControllerListener) iControllerListener.asBinder() : null;
                    parcel.writeStrongBinder((IBinder)iControllerListener);
                    parcel.writeInt(n2);
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
            public void unregisterMonitor(IControllerMonitor iControllerMonitor, int n2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken(Stub.DESCRIPTOR);
                    iControllerMonitor = iControllerMonitor != null ? (IControllerMonitor) iControllerMonitor.asBinder() : null;
                    parcel.writeStrongBinder((IBinder)iControllerMonitor);
                    parcel.writeInt(n2);
                    this.mRemote.transact(4, parcel, parcel2, 0);
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

