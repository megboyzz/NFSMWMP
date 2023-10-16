package com.verizon.vcast.apps;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IVCastInAppService extends IInterface {

    public static abstract class Stub extends Binder implements IVCastInAppService {
        private static final String DESCRIPTOR = "com.verizon.vcast.apps.IVCastInAppService";
        static final int TRANSACTION_cancelInAppContentSubscription = 1;
        static final int TRANSACTION_getInAppContentOffer = 2;
        static final int TRANSACTION_getInAppContents = 3;
        static final int TRANSACTION_getPurchasedInAppContents = 4;
        static final int TRANSACTION_purchaseInAppContent = 5;

        /* access modifiers changed from: private */
        public static class Proxy implements IVCastInAppService {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.verizon.vcast.apps.IVCastInAppService
            public int cancelInAppContentSubscription(String str, String str2, String str3) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    obtain.writeString(str3);
                    this.mRemote.transact(1, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt();
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.verizon.vcast.apps.IVCastInAppService
            public InAppContentOffers getInAppContentOffer(String str, String str2) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    this.mRemote.transact(2, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? InAppContentOffers.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.verizon.vcast.apps.IVCastInAppService
            public InAppContents getInAppContents(String str, DiscoveryParameters discoveryParameters) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (discoveryParameters != null) {
                        obtain.writeInt(1);
                        discoveryParameters.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(3, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? InAppContents.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.verizon.vcast.apps.IVCastInAppService
            public PurchasedInAppContents getPurchasedInAppContents(String str, DiscoveryParameters discoveryParameters) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    if (discoveryParameters != null) {
                        obtain.writeInt(1);
                        discoveryParameters.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(4, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? PurchasedInAppContents.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }

            @Override // com.verizon.vcast.apps.IVCastInAppService
            public PurchaseInAppContentResult purchaseInAppContent(String str, String str2, PurchaseParameters purchaseParameters) throws RemoteException {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    obtain.writeString(str);
                    obtain.writeString(str2);
                    if (purchaseParameters != null) {
                        obtain.writeInt(1);
                        purchaseParameters.writeToParcel(obtain, 0);
                    } else {
                        obtain.writeInt(0);
                    }
                    this.mRemote.transact(5, obtain, obtain2, 0);
                    obtain2.readException();
                    return obtain2.readInt() != 0 ? PurchaseInAppContentResult.CREATOR.createFromParcel(obtain2) : null;
                } finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVCastInAppService asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            return (queryLocalInterface == null || !(queryLocalInterface instanceof IVCastInAppService)) ? new Proxy(iBinder) : (IVCastInAppService) queryLocalInterface;
        }

        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            switch (i) {
                case 1:
                    parcel.enforceInterface(DESCRIPTOR);
                    int cancelInAppContentSubscription = cancelInAppContentSubscription(parcel.readString(), parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    parcel2.writeInt(cancelInAppContentSubscription);
                    return true;
                case 2:
                    parcel.enforceInterface(DESCRIPTOR);
                    InAppContentOffers inAppContentOffer = getInAppContentOffer(parcel.readString(), parcel.readString());
                    parcel2.writeNoException();
                    if (inAppContentOffer != null) {
                        parcel2.writeInt(1);
                        inAppContentOffer.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case 3:
                    parcel.enforceInterface(DESCRIPTOR);
                    InAppContents inAppContents = getInAppContents(parcel.readString(), parcel.readInt() != 0 ? DiscoveryParameters.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    if (inAppContents != null) {
                        parcel2.writeInt(1);
                        inAppContents.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case 4:
                    parcel.enforceInterface(DESCRIPTOR);
                    PurchasedInAppContents purchasedInAppContents = getPurchasedInAppContents(parcel.readString(), parcel.readInt() != 0 ? DiscoveryParameters.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    if (purchasedInAppContents != null) {
                        parcel2.writeInt(1);
                        purchasedInAppContents.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case 5:
                    parcel.enforceInterface(DESCRIPTOR);
                    PurchaseInAppContentResult purchaseInAppContent = purchaseInAppContent(parcel.readString(), parcel.readString(), parcel.readInt() != 0 ? PurchaseParameters.CREATOR.createFromParcel(parcel) : null);
                    parcel2.writeNoException();
                    if (purchaseInAppContent != null) {
                        parcel2.writeInt(1);
                        purchaseInAppContent.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                case 1598968902:
                    parcel2.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }
    }

    int cancelInAppContentSubscription(String str, String str2, String str3) throws RemoteException;

    InAppContentOffers getInAppContentOffer(String str, String str2) throws RemoteException;

    InAppContents getInAppContents(String str, DiscoveryParameters discoveryParameters) throws RemoteException;

    PurchasedInAppContents getPurchasedInAppContents(String str, DiscoveryParameters discoveryParameters) throws RemoteException;

    PurchaseInAppContentResult purchaseInAppContent(String str, String str2, PurchaseParameters purchaseParameters) throws RemoteException;
}
