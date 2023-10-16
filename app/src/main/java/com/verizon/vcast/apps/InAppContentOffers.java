package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class InAppContentOffers implements Parcelable {
    public static final Parcelable.Creator<InAppContentOffers> CREATOR = new Parcelable.Creator<InAppContentOffers>() {
        /* class com.verizon.vcast.apps.InAppContentOffers.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public InAppContentOffers createFromParcel(Parcel parcel) {
            return new InAppContentOffers(parcel, null);
        }

        @Override // android.os.Parcelable.Creator
        public InAppContentOffers[] newArray(int i) {
            return new InAppContentOffers[i];
        }
    };
    Offer[] offers;
    int result;
    int totalSize;

    public InAppContentOffers() {
    }

    private InAppContentOffers(Parcel parcel) {
        readFromParcel(parcel);
    }

    /* synthetic */ InAppContentOffers(Parcel parcel, InAppContentOffers inAppContentOffers) {
        this(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        Object[] readArray = parcel.readArray(Offer.class.getClassLoader());
        this.offers = new Offer[readArray.length];
        for (int i = 0; i < readArray.length; i++) {
            this.offers[i] = (Offer) readArray[i];
        }
        this.result = parcel.readInt();
        this.totalSize = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeArray(this.offers);
        parcel.writeInt(this.result);
        parcel.writeInt(this.totalSize);
    }
}
