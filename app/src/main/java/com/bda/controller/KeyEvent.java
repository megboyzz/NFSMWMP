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

public final class KeyEvent
extends BaseEvent
implements Parcelable {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final Parcelable.Creator<KeyEvent> CREATOR = new ParcelableCreator();
    public static final int KEYCODE_BUTTON_A = 96;
    public static final int KEYCODE_BUTTON_B = 97;
    public static final int KEYCODE_BUTTON_L1 = 102;
    public static final int KEYCODE_BUTTON_L2 = 104;
    public static final int KEYCODE_BUTTON_R1 = 103;
    public static final int KEYCODE_BUTTON_R2 = 105;
    public static final int KEYCODE_BUTTON_SELECT = 109;
    public static final int KEYCODE_BUTTON_START = 108;
    public static final int KEYCODE_BUTTON_THUMBL = 106;
    public static final int KEYCODE_BUTTON_THUMBR = 107;
    public static final int KEYCODE_BUTTON_X = 98;
    public static final int KEYCODE_BUTTON_Y = 99;
    public static final int KEYCODE_DPAD_DOWN = 20;
    public static final int KEYCODE_DPAD_LEFT = 21;
    public static final int KEYCODE_DPAD_RIGHT = 22;
    public static final int KEYCODE_DPAD_UP = 19;
    public static final int KEYCODE_UNKNOWN = 0;
    final int mAction;
    final int mKeyCode;

    public KeyEvent(long l2, int n2, int n3, int n4) {
        super(l2, n2);
        this.mKeyCode = n3;
        this.mAction = n4;
    }

    KeyEvent(Parcel parcel) {
        super(parcel);
        this.mKeyCode = parcel.readInt();
        this.mAction = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final int getAction() {
        return this.mAction;
    }

    public final int getKeyCode() {
        return this.mKeyCode;
    }

    @Override
    public void writeToParcel(Parcel parcel, int n2) {
        super.writeToParcel(parcel, n2);
        parcel.writeInt(this.mKeyCode);
        parcel.writeInt(this.mAction);
    }

    static class ParcelableCreator
    implements Parcelable.Creator<KeyEvent> {
        ParcelableCreator() {
        }

        public KeyEvent createFromParcel(Parcel parcel) {
            return new KeyEvent(parcel);
        }

        public KeyEvent[] newArray(int n2) {
            return new KeyEvent[n2];
        }
    }
}

