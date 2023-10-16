package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class Item implements Parcelable {
    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {
        /* class com.verizon.vcast.apps.Item.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel, null);
        }

        @Override // android.os.Parcelable.Creator
        public Item[] newArray(int i) {
            return new Item[i];
        }
    };
    public String ageRating;
    public String itemDescription;
    public String itemID;
    public String itemName;

    public Item() {
    }

    private Item(Parcel parcel) {
        readFromParcel(parcel);
    }

    /* synthetic */ Item(Parcel parcel, Item item) {
        this(parcel);
    }

    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel parcel) {
        this.ageRating = parcel.readString();
        this.itemDescription = parcel.readString();
        this.itemID = parcel.readString();
        this.itemName = parcel.readString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.ageRating);
        parcel.writeString(this.itemDescription);
        parcel.writeString(this.itemID);
        parcel.writeString(this.itemName);
    }
}
