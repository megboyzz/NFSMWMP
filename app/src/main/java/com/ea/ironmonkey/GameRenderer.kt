package com.ea.ironmonkey

import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

//TODO Это тоже рендеринг
class GameRenderer(private val activity: GameActivity) : GLSurfaceView.Renderer {
    var height = 0
        private set
    var width = 0
        private set
    private var drawFrameListener: DrawFrameListener? = null
    override fun onDrawFrame(gl10: GL10) {
        if (drawFrameListener != null) {
            drawFrameListener!!.onDrawFrame(gl10)
        } else {
            activity.runLoop.onRunLoopTick()
        }
    }

    override fun onSurfaceChanged(gl10: GL10, width: Int, height: Int) {
        if (gl10 !== gl) {
            activity.nativeSurfaceChanged(gl10, width, height)
            gl = gl10
        }
        this.width = width
        this.height = height
    }

    override fun onSurfaceCreated(gl10: GL10, eGLConfig: EGLConfig) {
        activity.nativeSurfaceCreated(gl10, eGLConfig)
    }

    fun setDrawFrameListener(drawFrameListener2: DrawFrameListener?) {
        drawFrameListener = drawFrameListener2
    }

    companion object {
        var gl: GL10? = null
    }
}
