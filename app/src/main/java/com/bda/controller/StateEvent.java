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
import com.bda.controller.BaseEvent;

public class StateEvent
extends BaseEvent
implements Parcelable {
    public static final int ACTION_CONNECTED = 1;
    public static final int ACTION_CONNECTING = 2;
    public static final int ACTION_DISCONNECTED = 0;
    public static final int ACTION_FALSE = 0;
    public static final int ACTION_TRUE = 1;
    public static final Parcelable.Creator<StateEvent> CREATOR = new ParcelableCreator();
    public static final int STATE_CONNECTION = 1;
    public static final int STATE_POWER_LOW = 2;
    public static final int STATE_SELECTED_VERSION = 4;
    public static final int STATE_SUPPORTED_VERSION = 3;
    public static final int STATE_UNKNOWN = 0;
    final int mAction;
    final int mState;

    public StateEvent(long l2, int n2, int n3, int n4) {
        super(l2, n2);
        this.mState = n3;
        this.mAction = n4;
    }

    StateEvent(Parcel parcel) {
        super(parcel);
        this.mState = parcel.readInt();
        this.mAction = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final int getAction() {
        return this.mAction;
    }

    public final int getState() {
        return this.mState;
    }

    @Override
    public void writeToParcel(Parcel parcel, int n2) {
        super.writeToParcel(parcel, n2);
        parcel.writeInt(this.mState);
        parcel.writeInt(this.mAction);
    }

    static class ParcelableCreator
    implements Parcelable.Creator<StateEvent> {
        ParcelableCreator() {
        }

        public StateEvent createFromParcel(Parcel parcel) {
            return new StateEvent(parcel);
        }

        public StateEvent[] newArray(int n2) {
            return new StateEvent[n2];
        }
    }
}

