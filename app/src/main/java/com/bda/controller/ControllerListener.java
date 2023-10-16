/*
 * Decompiled with CFR 0.152.
 */
package com.bda.controller;

import com.bda.controller.KeyEvent;
import com.bda.controller.MotionEvent;
import com.bda.controller.StateEvent;

public interface ControllerListener {
    public void onKeyEvent(KeyEvent var1);

    public void onMotionEvent(MotionEvent var1);

    public void onStateEvent(StateEvent var1);
}

