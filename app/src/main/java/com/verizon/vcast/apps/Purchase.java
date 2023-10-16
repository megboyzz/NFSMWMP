package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Date;

public class Purchase implements Parcelable {
    public static final Parcelable.Creator<Purchase> CREATOR = new Parcelable.Creator<Purchase>() {
        /* class com.verizon.vcast.apps.Purchase.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public Purchase createFromParcel(Parcel parcel) {
            return new Purchase(parcel, null);
        }

        @Override // android.os.Parcelable.Creator
        public Purchase[] newArray(int i) {
            return new Purchase[i];
        }
    };
    public String inAppName;
    public Item item;
    public float price;
    public String priceLine;
    public String priceType;
    public String pricingTerms;
    public Date purchaseDate;
    public String purchaseID;
    public String sku;

    public Purchase() {
    }

    private Purchase(Parcel parcel) {
        readFromParcel(parcel);
    }

    /* synthetic */ Purchase(Parcel parcel, Purchase purchase) {
        this(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        this.inAppName = parcel.readString();
        this.item = (Item) parcel.readValue(Item.class.getClassLoader());
        this.priceLine = parcel.readString();
        this.priceType = parcel.readString();
        this.pricingTerms = parcel.readString();
        this.purchaseID = parcel.readString();
        this.sku = parcel.readString();
        this.price = parcel.readFloat();
        this.purchaseDate = new Date();
        this.purchaseDate.setTime(parcel.readLong());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.inAppName);
        parcel.writeValue(this.item);
        parcel.writeString(this.priceLine);
        parcel.writeString(this.priceType);
        parcel.writeString(this.pricingTerms);
        parcel.writeString(this.purchaseID);
        parcel.writeString(this.sku);
        parcel.writeFloat(this.price);
        parcel.writeLong(this.purchaseDate.getTime());
    }
}
