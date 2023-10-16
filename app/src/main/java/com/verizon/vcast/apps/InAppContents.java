package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class InAppContents implements Parcelable {
    public static final Parcelable.Creator<InAppContents> CREATOR = new Parcelable.Creator<InAppContents>() {
        /* class com.verizon.vcast.apps.InAppContents.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public InAppContents createFromParcel(Parcel parcel) {
            return new InAppContents(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public InAppContents[] newArray(int i) {
            return new InAppContents[i];
        }
    };
    public Item[] items;
    public Integer result;
    public Integer totalSize;

    public InAppContents() {
    }

    public InAppContents(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        Object[] readArray = parcel.readArray(Item.class.getClassLoader());
        this.items = new Item[readArray.length];
        for (int i = 0; i < readArray.length; i++) {
            this.items[i] = (Item) readArray[i];
        }
        this.result = Integer.valueOf(parcel.readInt());
        this.totalSize = Integer.valueOf(parcel.readInt());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeArray(this.items);
        parcel.writeInt(this.result.intValue());
        parcel.writeInt(this.totalSize.intValue());
    }
}
