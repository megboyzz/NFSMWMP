package com.verizon.vcast.apps;

import android.os.Parcel;
import android.os.Parcelable;

public class DiscoveryParameters implements Parcelable {
    public static final Parcelable.Creator<DiscoveryParameters> CREATOR = new Parcelable.Creator<DiscoveryParameters>() {
        /* class com.verizon.vcast.apps.DiscoveryParameters.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public DiscoveryParameters createFromParcel(Parcel parcel) {
            return new DiscoveryParameters(parcel, null);
        }

        @Override // android.os.Parcelable.Creator
        public DiscoveryParameters[] newArray(int i) {
            return new DiscoveryParameters[i];
        }
    };
    public boolean ascendingOrder;
    public int maxResults;
    public String sortBy;
    public int startIndex;

    public DiscoveryParameters() {
    }

    private DiscoveryParameters(Parcel parcel) {
        readFromParcel(parcel);
    }

    /* synthetic */ DiscoveryParameters(Parcel parcel, DiscoveryParameters discoveryParameters) {
        this(parcel);
    }

    private void readFromParcel(Parcel parcel) {
        parcel.readBooleanArray(new boolean[1]);
        this.ascendingOrder = false;
        this.maxResults = parcel.readInt();
        this.sortBy = parcel.readString();
        this.startIndex = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBooleanArray(new boolean[]{this.ascendingOrder});
        parcel.writeInt(this.maxResults);
        parcel.writeString(this.sortBy);
        parcel.writeInt(this.startIndex);
    }
}
