package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class PurchaseParameters implements Parcelable {
    public static final Parcelable.Creator<PurchaseParameters> CREATOR = new Parcelable.Creator<PurchaseParameters>() {
        /* class com.verizon.vcast.apps.PurchaseParameters.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public PurchaseParameters createFromParcel(Parcel parcel) {
            return new PurchaseParameters(parcel, null);
        }

        @Override // android.os.Parcelable.Creator
        public PurchaseParameters[] newArray(int i) {
            return new PurchaseParameters[i];
        }
    };
    public Integer contentSize;
    public String inAppName;
    public String offerID;
    public float price;
    public String priceLine;
    public String priceType;
    public String pricingTerms;
    public String sku;

    public PurchaseParameters() {
        this.contentSize = 0;
    }

    private PurchaseParameters(Parcel parcel) {
        this.contentSize = 0;
        readFromParcel(parcel);
    }

    /* synthetic */ PurchaseParameters(Parcel parcel, PurchaseParameters purchaseParameters) {
        this(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        this.contentSize = Integer.valueOf(parcel.readInt());
        this.inAppName = parcel.readString();
        this.offerID = parcel.readString();
        this.price = parcel.readFloat();
        this.sku = parcel.readString();
        this.priceType = parcel.readString();
        this.priceLine = parcel.readString();
        this.pricingTerms = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.contentSize.intValue());
        parcel.writeString(this.inAppName);
        parcel.writeString(this.offerID);
        parcel.writeFloat(this.price);
        parcel.writeString(this.sku);
        parcel.writeString(this.priceType);
        parcel.writeString(this.priceLine);
        parcel.writeString(this.pricingTerms);
    }
}
