package com.ea.ironmonkey;

import com.bda.controller.ControllerListener;
import com.bda.controller.KeyEvent;
import com.bda.controller.MotionEvent;
import com.bda.controller.StateEvent;

public class MogaController implements ControllerListener {
    /* access modifiers changed from: package-private */
    public native void nativeOnKeyEvent(KeyEvent keyEvent);

    /* access modifiers changed from: package-private */
    public native void nativeOnMotionEvent(MotionEvent motionEvent);

    /* access modifiers changed from: package-private */
    public native void nativeOnStateEvent(StateEvent stateEvent);

    @Override // com.bda.controller.ControllerListener
    public void onKeyEvent(KeyEvent keyEvent) {
        nativeOnKeyEvent(keyEvent);
    }

    @Override // com.bda.controller.ControllerListener
    public void onMotionEvent(MotionEvent motionEvent) {
        nativeOnMotionEvent(motionEvent);
    }

    @Override // com.bda.controller.ControllerListener
    public void onStateEvent(StateEvent stateEvent) {
        nativeOnStateEvent(stateEvent);
    }
}
