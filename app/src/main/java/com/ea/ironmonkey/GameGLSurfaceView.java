package com.ea.ironmonkey;

import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class GameGLSurfaceView extends GLSurfaceView {

    private static final String TAG = "GameGLSurfaceView";
    private boolean enableHistoricalEvents = false;
    private boolean kMotionEvent_GetSource = true;
    private GameActivity mActivity = null;

    // TODO Разобраться с рендерингом игры
    public GameGLSurfaceView(GameActivity gameActivity) {

        super(gameActivity);

        //MotionEvent motionEvent;

        this.mActivity = gameActivity;
        try {
            MotionEvent.class.getMethod("getSource");

            this.kMotionEvent_GetSource = true;
        } catch (Exception e) {
        }
        setGLESVersion2();
        setFocusable(true);
        setFocusableInTouchMode(true);
        if (Build.VERSION.SDK_INT >= 11) {
            try {
                Log.i(TAG, "setPreserveEGLContextOnPause");
                setPreserveEGLContextOnPause(false);
                Log.e(TAG, "setPreserveEGLContextOnPause(false) success");
            } catch (Exception e2) {
                Log.e(TAG, "setPreserveEGLContextOnPause failed");
            }
        }
    }

    /* access modifiers changed from: private */
    public static class ConfigChooser implements GLSurfaceView.EGLConfigChooser {
        private static final int EGL_DEPTH_ENCODING_NONLINEAR_NV = 12515;
        private static final int EGL_DEPTH_ENCODING_NV = 12514;
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue = new int[1];

        public ConfigChooser(int redSize, int greenSize, int blueSize, int alphaSize, int depthSize, int stencilSize) {
            this.mRedSize = redSize;
            this.mGreenSize = greenSize;
            this.mBlueSize = blueSize;
            this.mAlphaSize = alphaSize;
            this.mDepthSize = depthSize;
            this.mStencilSize = stencilSize;
        }

        private int findConfigAttrib(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig, int i, int i2) {
            return egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue) ? this.mValue[0] : i2;
        }

        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay) {
            int[] iArr = {12352, 4, 12324, 4, 12323, 4, 12322, 4, 12344};
            int[] iArr2 = new int[1];
            egl10.eglChooseConfig(eGLDisplay, iArr, null, 0, iArr2);
            int i = iArr2[0];
            if (i <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            EGLConfig[] eGLConfigArr = new EGLConfig[i];
            egl10.eglChooseConfig(eGLDisplay, iArr, eGLConfigArr, i, iArr2);
            return chooseConfig(egl10, eGLDisplay, eGLConfigArr);
        }

        public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig[] eGLConfigArr) {

            EGLConfig eGLConfig = null;

            while (eGLConfig == null) {
                for (EGLConfig eGLConfig2 : eGLConfigArr) {
                    int findConfigAttrib = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12325, 0);
                    int findConfigAttrib2 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12326, 0);
                    if (findConfigAttrib >= this.mDepthSize && findConfigAttrib2 >= this.mStencilSize) {
                        int findConfigAttrib3 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12324, 0);
                        int findConfigAttrib4 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12323, 0);
                        int findConfigAttrib5 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12322, 0);
                        int findConfigAttrib6 = findConfigAttrib(egl10, eGLDisplay, eGLConfig2, 12321, 0);
                        if (findConfigAttrib3 == this.mRedSize && findConfigAttrib4 == this.mGreenSize && findConfigAttrib5 == this.mBlueSize && findConfigAttrib6 == this.mAlphaSize) {
                            eGLConfig = eGLConfig2;
                            if (findConfigAttrib(egl10, eGLDisplay, eGLConfig2, EGL_DEPTH_ENCODING_NV, 0) == 0) {
                                break;
                            }
                        }
                    }
                }
                if (eGLConfig == null) {
                    if (this.mDepthSize <= 0) {
                        return null;
                    }
                    this.mDepthSize -= 8;
                }
            }
            Log.i(GameGLSurfaceView.TAG, "depth=" + findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12325, 0) + " stencil=" + findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12326, 0) + " red=" + findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12324, 0) + " green=" + findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12323, 0) + " blue=" + findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12322, 0) + " alpha=" + findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12321, 0) + " nonLinear=" + (findConfigAttrib(egl10, eGLDisplay, eGLConfig, EGL_DEPTH_ENCODING_NV, 0) == EGL_DEPTH_ENCODING_NONLINEAR_NV));
            return eGLConfig;
        }
    }


    private native void nativeTouchPadEvent(int i, int i2, float f, float f2);

    private native void nativeTouchScreenEvent(int i, int i2, float f, float f2);

    private void setGLESVersion2() {
        setEGLContextFactory(new GLSurfaceView.EGLContextFactory() {

            private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;

            public EGLContext createContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLConfig eGLConfig) {
                return egl10.eglCreateContext(eGLDisplay, eGLConfig, EGL10.EGL_NO_CONTEXT, new int[]{EGL_CONTEXT_CLIENT_VERSION, 2, 12344});
            }

            public void destroyContext(EGL10 egl10, EGLDisplay eGLDisplay, EGLContext eGLContext) {
                egl10.eglDestroyContext(eGLDisplay, eGLContext);
            }
        });
        setEGLConfigChooser(new ConfigChooser(5, 6, 5, 0, 24, 0));
    }

    @Override
    public boolean performClick() {
        Log.i(TAG, "performClick()...");
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int state = GameActivity.state;
        Log.i(TAG, "TouchEvent, GameActivity.state = " + state);
        motionEvent.getHistorySize();
        final int pointerCount = motionEvent.getPointerCount();
        final MotionEvent obtain = MotionEvent.obtain(motionEvent);
        queueEvent(() -> {
            Log.i(TAG, "queueEvent...");

            if (GameGLSurfaceView.this.kMotionEvent_GetSource) {

                if (obtain.getSource() == 4098 || obtain.getSource() == 1048584) {
                    if (obtain.getAction() == MotionEvent.ACTION_MOVE) {
                        for (int i = 0; i < pointerCount; i++) {
                            Log.i(TAG, "TouchEvent 1");
                            GameGLSurfaceView.this.nativeTouchScreenEvent(obtain.getAction(), obtain.getPointerId(i), obtain.getX(i), obtain.getY(i));
                        }
                        return;
                    }
                    int actionIndex = obtain.getActionIndex();
                    Log.i(TAG, "TouchEvent 2");
                    GameGLSurfaceView.this.nativeTouchScreenEvent(obtain.getAction(), obtain.getPointerId(actionIndex), obtain.getX(actionIndex), obtain.getY(actionIndex));
                }
            } else if (obtain.getAction() == MotionEvent.ACTION_MOVE) {
                for (int i = 0; i < pointerCount; i++) {
                    Log.i(TAG, "TouchEvent 5");
                    GameGLSurfaceView.this.nativeTouchScreenEvent(obtain.getAction(), obtain.getPointerId(i), obtain.getX(i), obtain.getY(i));
                }
            } else {
                int actionIndex3 = obtain.getActionIndex();
                Log.i(TAG, "TouchEvent 6");
                GameGLSurfaceView.this.nativeTouchScreenEvent(obtain.getAction(), obtain.getPointerId(actionIndex3), obtain.getX(actionIndex3), obtain.getY(actionIndex3));
            }
        });
        return true;
    }

    public void setEnableHistoricalEvents(boolean z) {
        this.enableHistoricalEvents = z;
    }
}
