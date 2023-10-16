package com.ea.easp;

import android.view.KeyEvent;

public abstract class KeyboardAndroid {
    KeyboardAndroid() {
    }

    public static final boolean IsSystemKey(int i) {
        switch (i) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 24:
            case 25:
            case 26:
            case 91:
                return true;
            default:
                return false;
        }
    }

    public static final boolean IsVirtualKeyboardEvent(KeyEvent keyEvent) {
        return (keyEvent.getFlags() & 2) != 0;
    }

    private native void NativeOnKeyDown(int i, int i2);

    private native void NativeOnKeyUp(int i, int i2);

    /* access modifiers changed from: protected */
    public native void NativeOnCharacter(int i);

    /* access modifiers changed from: protected */
    public native void NativeOnCursorMove(int i, int i2);

    /* access modifiers changed from: protected */
    public void NativeOnKeyDown(int i, boolean z) {
        NativeOnKeyDown(i, z ? 1 : 0);
    }

    /* access modifiers changed from: protected */
    public void NativeOnKeyUp(int i, boolean z) {
        NativeOnKeyUp(i, z ? 1 : 0);
    }

    /* access modifiers changed from: protected */
    public native void NativeOnKeyboardHideHerself();

    /* access modifiers changed from: protected */
    public native void NativeOnVisibilityChanged(boolean z);
}
