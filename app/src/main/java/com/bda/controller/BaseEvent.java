/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Parcel
 *  android.os.Parcelable
 *  android.os.Parcelable$Creator
 */
package com.bda.controller;

import android.os.Parcel;
import android.os.Parcelable;

class BaseEvent
implements Parcelable {
    public static final Parcelable.Creator<BaseEvent> CREATOR = new ParcelableCreator();
    final int mControllerId;
    final long mEventTime;

    public BaseEvent(long l2, int n2) {
        this.mEventTime = l2;
        this.mControllerId = n2;
    }

    BaseEvent(Parcel parcel) {
        this.mEventTime = parcel.readLong();
        this.mControllerId = parcel.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public final int getControllerId() {
        return this.mControllerId;
    }

    public final long getEventTime() {
        return this.mEventTime;
    }

    public void writeToParcel(Parcel parcel, int n2) {
        parcel.writeLong(this.mEventTime);
        parcel.writeInt(this.mControllerId);
    }

    static class ParcelableCreator
    implements Parcelable.Creator<BaseEvent> {
        ParcelableCreator() {
        }

        public BaseEvent createFromParcel(Parcel parcel) {
            return new BaseEvent(parcel);
        }

        public BaseEvent[] newArray(int n2) {
            return new BaseEvent[n2];
        }
    }
}

