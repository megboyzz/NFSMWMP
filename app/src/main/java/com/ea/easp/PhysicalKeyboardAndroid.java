package com.ea.easp;

import android.content.res.Configuration;
import android.view.KeyEvent;

public class PhysicalKeyboardAndroid extends KeyboardAndroid {
    private static final boolean DEBUG_LOG_ENABLED = true;
    private static final String DEBUG_LOG_TAG = "EASP PhysicalKeyboardAndroid";
    private int mPhysicalKeyboardVisibility = 0;
    private TaskLauncher mTaskLauncher;

    PhysicalKeyboardAndroid(TaskLauncher taskLauncher) {
        this.mTaskLauncher = taskLauncher;
    }

    public boolean OnKeyDown(final int mCode, KeyEvent keyEvent) {
        Debug.Log.d(DEBUG_LOG_TAG, "physical keyboard OnKeyDown: " + mCode);
        if (IsSystemKey(mCode)) {
            return false;
        }
        if (keyEvent.getRepeatCount() == 0) {
            final boolean mAlt = keyEvent.isAltPressed();
            this.mTaskLauncher.runInGLThread(new Runnable() {
                @Override
                public void run() {
                    PhysicalKeyboardAndroid.this.NativeOnKeyDown(mCode, mAlt);
                }
            });
        }
        return true;
    }

    public boolean OnKeyUp(final int mCode, KeyEvent keyEvent) {
        Debug.Log.d(DEBUG_LOG_TAG, "physical keyboard OnKeyUp: " + mCode);
        if (IsSystemKey(mCode)) {
            return false;
        }
        final boolean mAlt = keyEvent.isAltPressed();
        this.mTaskLauncher.runInGLThread(new Runnable() {
            @Override
            public void run() {
                PhysicalKeyboardAndroid.this.NativeOnKeyUp(mCode, mAlt);
            }
        });
        final int unicodeChar = keyEvent.getUnicodeChar();
        if (unicodeChar != 0) {
            this.mTaskLauncher.runInGLThread(new Runnable() {
                public void run() {
                    PhysicalKeyboardAndroid.this.NativeOnCharacter(unicodeChar);
                }
            });
        }
        return true;
    }

    public void onConfigurationChanged(Configuration configuration) {
        boolean mKeyboardVisible = true;
        if (this.mPhysicalKeyboardVisibility != configuration.hardKeyboardHidden) {
            switch (configuration.hardKeyboardHidden) {
                case 1:
                case 2:
                    if (configuration.hardKeyboardHidden != 1) {
                        mKeyboardVisible = false;
                    }
                    final boolean finalMKeyboardVisible = mKeyboardVisible;
                    this.mTaskLauncher.runInGLThread(new Runnable() {
                        @Override
                        public void run() {
                            PhysicalKeyboardAndroid.this.NativeOnVisibilityChanged(finalMKeyboardVisible);
                        }
                    });
                    break;
            }
            this.mPhysicalKeyboardVisibility = configuration.hardKeyboardHidden;
        }
    }
}
