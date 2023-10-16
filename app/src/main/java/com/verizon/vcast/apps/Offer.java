package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class Offer implements Parcelable {
    public static final Parcelable.Creator<Offer> CREATOR = new Parcelable.Creator<Offer>() {
        /* class com.verizon.vcast.apps.Offer.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public Offer createFromParcel(Parcel parcel) {
            return new Offer(parcel, null);
        }

        @Override // android.os.Parcelable.Creator
        public Offer[] newArray(int i) {
            return new Offer[i];
        }
    };
    public float maxPrice;
    public float minPrice;
    public String offerID;
    public String priceLine;
    public String priceType;
    public String pricingTerms;

    public Offer() {
    }

    private Offer(Parcel parcel) {
        readFromParcel(parcel);
    }

    /* synthetic */ Offer(Parcel parcel, Offer offer) {
        this(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        this.maxPrice = parcel.readFloat();
        this.minPrice = parcel.readFloat();
        this.offerID = parcel.readString();
        this.priceLine = parcel.readString();
        this.priceType = parcel.readString();
        this.pricingTerms = parcel.readString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.maxPrice);
        parcel.writeFloat(this.minPrice);
        parcel.writeString(this.offerID);
        parcel.writeString(this.priceLine);
        parcel.writeString(this.priceType);
        parcel.writeString(this.pricingTerms);
    }
}
