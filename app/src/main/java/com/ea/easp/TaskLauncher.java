package com.ea.easp;

import android.opengl.GLSurfaceView;
import android.os.Handler;

public class TaskLauncher {
    private static final String TAG = "TaskLauncher";
    private GLSurfaceView mGLSurfaceView;
    private Handler mHandler;

    public TaskLauncher(Handler handler, GLSurfaceView gLSurfaceView) {
        this.mHandler = handler;
        this.mGLSurfaceView = gLSurfaceView;
    }

    public void runInGLThread(Runnable runnable) {
        this.mGLSurfaceView.queueEvent(runnable);
    }

    public void runInUIThread(Runnable runnable) {
        this.mHandler.post(runnable);
    }
}
