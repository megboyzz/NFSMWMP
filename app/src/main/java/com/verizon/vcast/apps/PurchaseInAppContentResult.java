package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class PurchaseInAppContentResult implements Parcelable {
    public static final Parcelable.Creator<PurchaseInAppContentResult> CREATOR = new Parcelable.Creator<PurchaseInAppContentResult>() {
        /* class com.verizon.vcast.apps.PurchaseInAppContentResult.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public PurchaseInAppContentResult createFromParcel(Parcel parcel) {
            return new PurchaseInAppContentResult(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public PurchaseInAppContentResult[] newArray(int i) {
            return new PurchaseInAppContentResult[i];
        }
    };
    public String license;
    public String purchaseID;
    public Integer result;

    public PurchaseInAppContentResult() {
    }

    public PurchaseInAppContentResult(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        this.license = parcel.readString();
        this.purchaseID = parcel.readString();
        this.result = Integer.valueOf(parcel.readInt());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.license);
        parcel.writeString(this.purchaseID);
        parcel.writeInt(this.result.intValue());
    }
}
