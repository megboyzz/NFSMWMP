package com.verizon.vcast.apps;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IVCastAppsLicenseService extends IInterface {

    public static abstract class Stub extends Binder implements IVCastAppsLicenseService {
        private static final String DESCRIPTOR = "com.verizon.vcast.apps.IVCastAppsLicenseService";
        static final int TRANSACTION_getLicense = 1;
        static final int TRANSACTION_getRemoteLicense = 3;
        static final int TRANSACTION_getTime = 2;

        private static class Proxy implements IVCastAppsLicenseService {
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

            @Override // com.verizon.vcast.apps.IVCastAppsLicenseService
            public byte[] getLicense(String str) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createByteArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.verizon.vcast.apps.IVCastAppsLicenseService
            public byte[] getRemoteLicense(String str, boolean z) throws RemoteException {
                int i = 0;
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (z) {
                        i = 1;
                    }
                    obtain.writeInt(i);
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.createByteArray();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.verizon.vcast.apps.IVCastAppsLicenseService
            public long getTime() throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readLong();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVCastAppsLicenseService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IVCastAppsLicenseService)) ? new Proxy(iBinder) : (IVCastAppsLicenseService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] license = getLicense(parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeByteArray(license);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    long time = getTime();
                    parcel2.writeNoException();
                    parcel2.writeLong(time);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    byte[] remoteLicense = getRemoteLicense(parcel.readString(), parcel.readInt() != 0);
                    parcel2.writeNoException();
                    parcel2.writeByteArray(remoteLicense);
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    byte[] getLicense(String str) throws RemoteException;

    byte[] getRemoteLicense(String str, boolean z) throws RemoteException;

    long getTime() throws RemoteException;
}
