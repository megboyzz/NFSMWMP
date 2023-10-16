package com.ea.ironmonkey;

import android.opengl.GLSurfaceView;

public class RunLoop {
    public static final int STATE_RUNNING = 1;
    public static final int STATE_STOPPED = 0;
    private GLSurfaceView glSurfaceView;
    private int state;

    public RunLoop(GLSurfaceView gLSurfaceView) {
        this.glSurfaceView = gLSurfaceView;
        updateRenderMode();
    }

    private native void nativeOnRunLoopTick();

    private void setState(int i) {
        this.state = i;
        updateRenderMode();
    }

    private void updateRenderMode() {
        Log.v("RunLoop", "RunLoop.state = " + this.state);
        if(state == STATE_STOPPED) glSurfaceView.setRenderMode(STATE_STOPPED);
        if(state == STATE_RUNNING) glSurfaceView.setRenderMode(STATE_RUNNING);
    }

    public int getState() {
        return this.state;
    }

    public void join() {
        setState(STATE_STOPPED);
    }

    public void onRunLoopTick() {
        if (state == STATE_RUNNING) nativeOnRunLoopTick();
    }

    public void start() {
        setState(STATE_RUNNING);
    }

    public void stop() {
        setState(STATE_STOPPED);
    }
}
