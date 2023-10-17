package com.ea.ironmonkey

import com.bda.controller.ControllerListener
import com.bda.controller.KeyEvent
import com.bda.controller.MotionEvent
import com.bda.controller.StateEvent
object MogaController : ControllerListener {
    external fun nativeOnKeyEvent(keyEvent: KeyEvent?)
    external fun nativeOnMotionEvent(motionEvent: MotionEvent?)
    external fun nativeOnStateEvent(stateEvent: StateEvent?)
    override fun onKeyEvent(keyEvent: KeyEvent) {
        nativeOnKeyEvent(keyEvent)
    }

    override fun onMotionEvent(motionEvent: MotionEvent) {
        nativeOnMotionEvent(motionEvent)
    }

    override fun onStateEvent(stateEvent: StateEvent) {
        nativeOnStateEvent(stateEvent)
    }
}
