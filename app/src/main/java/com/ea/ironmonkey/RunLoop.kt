package com.ea.ironmonkey

import android.opengl.GLSurfaceView
import android.util.Log

class RunLoop(private val glSurfaceView: GLSurfaceView) {
    private var state = 0

    init { updateRenderMode() }

    private external fun nativeOnRunLoopTick()
    private fun setState(state: Int) {
        this.state = state
        updateRenderMode()
    }

    private fun updateRenderMode() {
        Log.v("RunLoop", "RunLoop.state = $state")
        if (state == STATE_STOPPED) glSurfaceView.renderMode = STATE_STOPPED
        if (state == STATE_RUNNING) glSurfaceView.renderMode = STATE_RUNNING
    }

    fun join() = setState(STATE_STOPPED)

    fun onRunLoopTick() {
        if (state == STATE_RUNNING) nativeOnRunLoopTick()
    }

    fun start() = setState(STATE_RUNNING)

    fun stop() = setState(STATE_STOPPED)

    companion object {
        const val STATE_RUNNING = 1
        const val STATE_STOPPED = 0
    }
}
