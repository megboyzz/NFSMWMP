package com.ea.InputMan;

import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.MotionEventCompat;
import android.view.MotionEvent;

public class InputMan {
    private boolean gbMotionEvent_GetSource = false;

    public InputMan() {
        try {
            MotionEvent.class.getMethod("getSource");
            this.gbMotionEvent_GetSource = true;
        } catch (Exception e) {
        }
    }

    private static int GetEventType(int i) {
        switch (i) {
            case 0:
            case 4:
            case 5:
            default:
                return 0;
            case 1:
            case 6:
                return 2;
            case 2:
                return 1;
            case 3:
                return 3;
        }
    }

    private int GetSource(MotionEvent motionEvent) {
        if (!this.gbMotionEvent_GetSource) {
            return 0;
        }
        int source = motionEvent.getSource();
        if ((source & 2) == 0) {
            return -1;
        }
        switch (source) {
            case 1048584:
                return 10;
            case FragmentTransaction.TRANSIT_FRAGMENT_CLOSE:
                break;
        }
        return 0;
    }

    private static native boolean InputMan_OnMotionEvent(int i, int i2, float f, float f2, int i3, float f3);

    public boolean onTouchEvent(int i, MotionEvent motionEvent) {
        boolean z = false;
        int historySize = motionEvent.getHistorySize();
        int pointerCount = motionEvent.getPointerCount();
        int GetSource = GetSource(motionEvent);
        int GetEventType = GetEventType(motionEvent.getAction() & MotionEventCompat.ACTION_MASK);
        if (GetSource >= 0) {
            for (int i2 = 0; i2 < historySize; i2++) {
                for (int i3 = 0; i3 < pointerCount; i3++) {
                    InputMan_OnMotionEvent(i, motionEvent.getPointerId(i3), motionEvent.getHistoricalX(i3, i2), motionEvent.getHistoricalY(i3, i2), GetEventType, motionEvent.getHistoricalPressure(i3, i2));
                }
            }
            for (int i4 = 0; i4 < pointerCount; i4++) {
                z = InputMan_OnMotionEvent(i, motionEvent.getPointerId(i4), motionEvent.getX(i4), motionEvent.getY(i4), GetEventType, motionEvent.getPressure(i4));
            }
        }
        return z;
    }
}
