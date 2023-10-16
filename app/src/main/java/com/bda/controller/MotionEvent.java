/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Parcel
 *  android.os.Parcelable
 *  android.os.Parcelable$Creator
 *  android.util.SparseArray
 */
package com.bda.controller;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;

public final class MotionEvent
extends BaseEvent
implements Parcelable {
    public static final int AXIS_LTRIGGER = 17;
    public static final int AXIS_RTRIGGER = 18;
    public static final int AXIS_RZ = 14;
    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 11;
    public static final Parcelable.Creator<MotionEvent> CREATOR = new ParcelableCreator();
    final SparseArray<Float> mAxis;
    final SparseArray<Float> mPrecision;

    public MotionEvent(long l2, int n2, float f2, float f3, float f4, float f5, float f6, float f7) {
        super(l2, n2);
        this.mAxis = new SparseArray(4);
        this.mAxis.put(0, f2);
        this.mAxis.put(1, f3);
        this.mAxis.put(11, f4);
        this.mAxis.put(14, f5);
        this.mPrecision = new SparseArray(2);
        this.mPrecision.put(0, f6);
        this.mPrecision.put(1, f7);
    }

    public MotionEvent(long l2, int n2, int[] nArray, float[] fArray, int[] nArray2, float[] fArray2) {
        super(l2, n2);
        int n3 = nArray.length;
        this.mAxis = new SparseArray(n3);
        n2 = 0;
        while (true) {
            if (n2 >= n3) break;
            this.mAxis.put(nArray[n2], fArray[n2]);
            ++n2;
        }
        n3 = nArray2.length;
        this.mPrecision = new SparseArray(n3);
        n2 = 0;
        while (n2 < n3) {
            this.mPrecision.put(nArray2[n2], fArray2[n2]);
            ++n2;
        }
        return;
    }

    MotionEvent(Parcel parcel) {
        super(parcel);
        float f2;
        int n2;
        int n3 = parcel.readInt();
        this.mAxis = new SparseArray(n3);
        int n4 = 0;
        while (true) {
            if (n4 >= n3) break;
            n2 = parcel.readInt();
            f2 = parcel.readFloat();
            this.mAxis.put(n2, f2);
            ++n4;
        }
        this.mPrecision = new SparseArray(parcel.readInt());
        n4 = 0;
        while (n4 < n3) {
            n2 = parcel.readInt();
            f2 = parcel.readFloat();
            this.mPrecision.put(n2, f2);
            ++n4;
        }
        return;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final int findPointerIndex(int n2) {
        return -1;
    }

    public final float getAxisValue(int n2) {
        return this.getAxisValue(n2, 0);
    }

    public final float getAxisValue(int n2, int n3) {
        float f2 = 0.0f;
        if (n3 != 0) return f2;
        return this.mAxis.get(n2, 0.001f);
    }

    public final int getPointerCount() {
        return 1;
    }

    public final int getPointerId(int n2) {
        return 0;
    }

    public final float getRawX() {
        return this.getX();
    }

    public final float getRawY() {
        return this.getY();
    }

    public final float getX() {
        return this.getAxisValue(0, 0);
    }

    public final float getX(int n2) {
        return this.getAxisValue(0, n2);
    }

    public final float getXPrecision() {
        return this.mPrecision.get(0, 0.0f);
    }

    public final float getY() {
        return this.getAxisValue(1, 0);
    }

    public final float getY(int n2) {
        return this.getAxisValue(1, n2);
    }

    public final float getYPrecision() {
        return this.mPrecision.get(1, 0.0f);
    }

    @Override
    public void writeToParcel(Parcel parcel, int n2) {
        super.writeToParcel(parcel, n2);
        int n3 = this.mAxis.size();
        parcel.writeInt(n3);
        n2 = 0;
        while (true) {
            if (n2 >= n3) break;
            parcel.writeInt(this.mAxis.keyAt(n2));
            parcel.writeFloat(((Float)this.mAxis.valueAt(n2)).floatValue());
            ++n2;
        }
        n3 = this.mPrecision.size();
        parcel.writeInt(n3);
        n2 = 0;
        while (n2 < n3) {
            parcel.writeInt(this.mPrecision.keyAt(n2));
            parcel.writeFloat(((Float)this.mPrecision.valueAt(n2)).floatValue());
            ++n2;
        }
        return;
    }

    static class ParcelableCreator
    implements Parcelable.Creator<MotionEvent> {
        ParcelableCreator() {
        }

        public MotionEvent createFromParcel(Parcel parcel) {
            return new MotionEvent(parcel);
        }

        public MotionEvent[] newArray(int n2) {
            return new MotionEvent[n2];
        }
    }
}

