package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class PurchasedInAppContents implements Parcelable {
    public static final Parcelable.Creator<PurchasedInAppContents> CREATOR = new Parcelable.Creator<PurchasedInAppContents>() {
        /* class com.verizon.vcast.apps.PurchasedInAppContents.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public PurchasedInAppContents createFromParcel(Parcel parcel) {
            return new PurchasedInAppContents(parcel, null);
        }

        @Override // android.os.Parcelable.Creator
        public PurchasedInAppContents[] newArray(int i) {
            return new PurchasedInAppContents[i];
        }
    };
    public Purchase[] purchases;
    public Integer result;
    public Integer totalSize;

    public PurchasedInAppContents() {
    }

    private PurchasedInAppContents(Parcel parcel) {
        readFromParcel(parcel);
    }

    /* synthetic */ PurchasedInAppContents(Parcel parcel, PurchasedInAppContents purchasedInAppContents) {
        this(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        this.result = Integer.valueOf(parcel.readInt());
        this.totalSize = Integer.valueOf(parcel.readInt());
        Object[] readArray = parcel.readArray(Purchase.class.getClassLoader());
        this.purchases = new Purchase[readArray.length];
        for (int i = 0; i < readArray.length; i++) {
            this.purchases[i] = (Purchase) readArray[i];
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.result.intValue());
        parcel.writeInt(this.totalSize.intValue());
        parcel.writeArray(this.purchases);
    }
}
