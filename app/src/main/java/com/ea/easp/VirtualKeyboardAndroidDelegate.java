package com.ea.easp;

import android.app.Activity;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.ea.nimble.Log;
import com.google.android.gms.drive.DriveFile;

public class VirtualKeyboardAndroidDelegate extends KeyboardAndroid {
    private static final boolean DEBUG_LOG_ENABLED = true;
    private static final String DEBUG_LOG_TAG = "EASP VirtualKeyboardAndroidDelegate";
    private static final boolean DEBUG_SHOW_TEXT_FIELD = false;
    public static final int IME_FLAG_NO_FULLSCREEN = 33554432;
    private static final int kEnterKeyLabelDefault = 0;
    private static final int kEnterKeyLabelDone = 5;
    private static final int kEnterKeyLabelGo = 1;
    private static final int kEnterKeyLabelNext = 2;
    private static final int kEnterKeyLabelSearch = 3;
    private static final int kEnterKeyLabelSend = 4;
    private static final int kLayoutDefault = 0;
    private static final int kLayoutDigits = 1;
    private static final int kLayoutEmail = 2;
    private static final int kLayoutPass = 5;
    private static final int kLayoutPhone = 3;
    private static final int kLayoutUrl = 4;
    public int mAnchor;
    public String mCurrenText;
    public int mCursorPos;
    private volatile int mEnterkeyLabel = 6;
    private final InputMethodManager mInputMethodManager;
    private volatile int mKeyboardLayout = 1;
    private Activity mMainActivity;
    public ViewGroup mMainViewGroup;
    public int mMaxTextLength = Log.LEVEL_INFO;
    public String mNewText;
    private boolean mPhysicalKeyboardVisible = false;
    public RelativeLayout mRelativeLayout;
    RelativeLayout.LayoutParams mRelativeLayoutParam;
    private volatile int mShiftFlag = 0;
    private TextField mTextField;
    private boolean mVisibleRequested = false;

    /* access modifiers changed from: protected */
    public class TextField extends EditText {
        boolean isExtChange = false;
        private final CharSequence mDefaultText = "";
        private final int mDefaultTextLength = this.mDefaultText.length();

        TextField() {
            super(VirtualKeyboardAndroidDelegate.this.mMainActivity);
        }

        private int GetInputType() {
            return VirtualKeyboardAndroidDelegate.this.mKeyboardLayout | VirtualKeyboardAndroidDelegate.this.mShiftFlag | AccessibilityEventCompat.TYPE_GESTURE_DETECTION_END;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void Hide() {
            VirtualKeyboardAndroidDelegate.Log("Hide() from UiThread");
            try {
                setFilters(new InputFilter[]{new InputFilter.LengthFilter(1000)});
                clearFocus();
                Thread.sleep(10);
                if (!VirtualKeyboardAndroidDelegate.this.mVisibleRequested) {
                    VirtualKeyboardAndroidDelegate.this.mMainActivity.getWindow().setSoftInputMode(3);
                    VirtualKeyboardAndroidDelegate.this.mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
                    VirtualKeyboardAndroidDelegate.this.mMainViewGroup.removeView(VirtualKeyboardAndroidDelegate.this.mRelativeLayout);
                }
            } catch (Exception e) {
            }
        }

        private boolean IsDefaultText(CharSequence charSequence) {
            if (charSequence.length() != this.mDefaultTextLength) {
                return false;
            }
            for (int i = 0; i < this.mDefaultTextLength; i++) {
                if (charSequence.charAt(i) != this.mDefaultText.charAt(i)) {
                    return false;
                }
            }
            return true;
        }

        /* access modifiers changed from: private */
        /* access modifiers changed from: public */
        private void Show() {
            VirtualKeyboardAndroidDelegate.Log("Show() from UiThread");
            setInputType(GetInputType());
            setImeOptions(VirtualKeyboardAndroidDelegate.this.mEnterkeyLabel | VirtualKeyboardAndroidDelegate.IME_FLAG_NO_FULLSCREEN | DriveFile.MODE_READ_ONLY);
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(VirtualKeyboardAndroidDelegate.this.mMaxTextLength)});
            try {
                VirtualKeyboardAndroidDelegate.this.mMainViewGroup.addView(VirtualKeyboardAndroidDelegate.this.mRelativeLayout);
            } catch (Exception e) {
                VirtualKeyboardAndroidDelegate.Log("addView exception " + e);
            }
            requestFocus();
            VirtualKeyboardAndroidDelegate.this.mMainActivity.getWindow().setSoftInputMode(5);
            VirtualKeyboardAndroidDelegate.this.mInputMethodManager.showSoftInput(this, 2);
        }

        public void onEditorAction(int i) {
            VirtualKeyboardAndroidDelegate.Log("onEditorAction actionCode: " + i);
            VirtualKeyboardAndroidDelegate.this.NativeOnKeyDown(66, false);
            VirtualKeyboardAndroidDelegate.this.NativeOnKeyUp(66, false);
            VirtualKeyboardAndroidDelegate.this.mVisibleRequested = false;
            VirtualKeyboardAndroidDelegate.this.NativeOnKeyboardHideHerself();
            Hide();
        }

        /* access modifiers changed from: protected */
        public void onExtTextChanged(CharSequence charSequence) {
            if (!VirtualKeyboardAndroidDelegate.this.mCurrenText.equals(charSequence)) {
                VirtualKeyboardAndroidDelegate.Log("onExtTextChanged NewText: \"" + ((Object) charSequence) + "\" Old Text:\"" + VirtualKeyboardAndroidDelegate.this.mCurrenText + "\"");
                this.isExtChange = true;
                setText(charSequence);
            }
        }

        public boolean onKeyDown(int i, KeyEvent keyEvent) {
            VirtualKeyboardAndroidDelegate.Log("onKeyDown: " + i);
            super.onKeyDown(i, keyEvent);
            return !KeyboardAndroid.IsSystemKey(i);
        }

        public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
            VirtualKeyboardAndroidDelegate.Log("onKeyPreIme: " + i);
            if (i != 4) {
                return super.onKeyPreIme(i, keyEvent);
            }
            VirtualKeyboardAndroidDelegate.this.mVisibleRequested = false;
            VirtualKeyboardAndroidDelegate.this.NativeOnKeyboardHideHerself();
            Hide();
            return true;
        }

        public boolean onKeyUp(int i, KeyEvent keyEvent) {
            VirtualKeyboardAndroidDelegate.Log("onKeyUp: " + i);
            super.onKeyUp(i, keyEvent);
            if (i == 66) {
                VirtualKeyboardAndroidDelegate.this.mVisibleRequested = false;
                VirtualKeyboardAndroidDelegate.this.NativeOnKeyboardHideHerself();
                Hide();
            }
            return !KeyboardAndroid.IsSystemKey(i);
        }

        /* access modifiers changed from: protected */
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            VirtualKeyboardAndroidDelegate.Log("onTextChanged Start: " + i + " Before:" + i2 + " After: " + i3 + "  FullText: \"" + ((Object) charSequence) + "\"");
            super.onTextChanged(charSequence, i, i2, i3);
            if (!this.isExtChange) {
                VirtualKeyboardAndroidDelegate.this.NativeOnCursorMove(i + i2, i + i2);
                for (int i4 = 0; i4 < i2; i4++) {
                    VirtualKeyboardAndroidDelegate.this.NativeOnKeyDown(67, false);
                    VirtualKeyboardAndroidDelegate.this.NativeOnKeyUp(67, false);
                }
                for (int i5 = i; i5 < i + i3; i5++) {
                    VirtualKeyboardAndroidDelegate.this.NativeOnCharacter(charSequence.charAt(i5));
                }
                VirtualKeyboardAndroidDelegate.this.mCursorPos = i + i3;
                VirtualKeyboardAndroidDelegate.this.mAnchor = i + i3;
            }
            VirtualKeyboardAndroidDelegate.this.mCurrenText = "" + ((Object) charSequence);
            this.isExtChange = false;
        }

        public void setToastText(CharSequence charSequence) {
        }
    }

    VirtualKeyboardAndroidDelegate() {
        Log("VirtualKeyboardAndroidDelegate");
        this.mMainActivity = EASPHandler.mActivity;
        this.mMainViewGroup = EASPHandler.mViewGroup;
        this.mInputMethodManager = (InputMethodManager) this.mMainActivity.getSystemService("input_method");
        this.mPhysicalKeyboardVisible = this.mMainActivity.getResources().getConfiguration().hardKeyboardHidden == 1;
        this.mRelativeLayoutParam = new RelativeLayout.LayoutParams(0, 60);
        this.mCurrenText = "";
        this.mMainActivity.runOnUiThread(() -> {
            VirtualKeyboardAndroidDelegate.this.mTextField = new TextField();
            VirtualKeyboardAndroidDelegate.this.mRelativeLayout = new RelativeLayout(VirtualKeyboardAndroidDelegate.this.mMainActivity);
            VirtualKeyboardAndroidDelegate.this.mRelativeLayout.setGravity(53);
            VirtualKeyboardAndroidDelegate.this.mRelativeLayout.addView(VirtualKeyboardAndroidDelegate.this.mTextField, VirtualKeyboardAndroidDelegate.this.mRelativeLayoutParam);
        });
    }

    public static native int CppGetkEnterKeyLabelDefault();

    public static native int CppGetkEnterKeyLabelDone();

    public static native int CppGetkEnterKeyLabelGo();

    public static native int CppGetkEnterKeyLabelNext();

    public static native int CppGetkEnterKeyLabelSearch();

    public static native int CppGetkEnterKeyLabelSend();

    public static native int CppGetkLayoutDefault();

    public static native int CppGetkLayoutDigits();

    public static native int CppGetkLayoutEmail();

    public static native int CppGetkLayoutPhone();

    public static native int CppGetkLayoutUrl();

    private void Hide() {
        Log("Hide() from game thread");
        this.mMainActivity.runOnUiThread(new Runnable() {
            /* class com.ea.easp.VirtualKeyboardAndroidDelegate.AnonymousClass2 */

            public void run() {
                if (VirtualKeyboardAndroidDelegate.this.mTextField != null) {
                    VirtualKeyboardAndroidDelegate.this.mTextField.Hide();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public static void Log(String str) {
        Debug.Log.d(DEBUG_LOG_TAG, str);
    }

    private void Show() {
        Log("Show() from game thread");
        this.mMainActivity.runOnUiThread(new Runnable() {
            /* class com.ea.easp.VirtualKeyboardAndroidDelegate.AnonymousClass3 */

            public void run() {
                if (VirtualKeyboardAndroidDelegate.this.mTextField != null) {
                    VirtualKeyboardAndroidDelegate.this.mTextField.Show();
                }
            }
        });
    }

    public static final int StdToRawEnterKeyLabel(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 1) {
            return 2;
        }
        if (i == 2) {
            return 5;
        }
        if (i == 3) {
            return 3;
        }
        if (i == 4) {
            return 4;
        }
        return i == 5 ? 6 : 1;
    }

    public static final int StdToRawLayout(int i) {
        if (i == 0) {
            return 1;
        }
        if (i == 2) {
            return 33;
        }
        if (i == 5) {
            return 129;
        }
        if (i == 4) {
            return 17;
        }
        if (i == 1) {
            return 2;
        }
        return i == 3 ? 3 : 1;
    }

    public boolean IsVisible() {
        return this.mVisibleRequested && this.mTextField != null && this.mTextField.hasFocus();
    }

    public void OnPhysicalKeyboardVisibilityChanged(boolean z) {
        this.mPhysicalKeyboardVisible = z;
        if (this.mPhysicalKeyboardVisible) {
            Log("Hide from visibility changed");
            Hide();
        } else if (this.mVisibleRequested) {
            Log("Show from visibility changed");
            Show();
        }
    }

    public void OnUpdate() {
    }

    public void SetCursor(int i, int i2) {
        Log("SetCursor from GL - pos: " + i + "; anchor: " + i2 + ";");
        if (!(this.mCursorPos == i && this.mAnchor == i2) && !this.mPhysicalKeyboardVisible) {
            this.mCursorPos = i;
            this.mAnchor = i2;
            this.mMainActivity.runOnUiThread(new Runnable() {
                /* class com.ea.easp.VirtualKeyboardAndroidDelegate.AnonymousClass4 */

                public void run() {
                    if (VirtualKeyboardAndroidDelegate.this.mTextField != null) {
                        VirtualKeyboardAndroidDelegate.Log("SetCursor from UI - pos: " + VirtualKeyboardAndroidDelegate.this.mCursorPos + "; anchor: " + VirtualKeyboardAndroidDelegate.this.mAnchor + ";");
                        VirtualKeyboardAndroidDelegate.this.mTextField.setSelection(VirtualKeyboardAndroidDelegate.this.mAnchor, VirtualKeyboardAndroidDelegate.this.mCursorPos);
                        VirtualKeyboardAndroidDelegate.this.mInputMethodManager.updateSelection(VirtualKeyboardAndroidDelegate.this.mTextField, VirtualKeyboardAndroidDelegate.this.mAnchor, VirtualKeyboardAndroidDelegate.this.mCursorPos, VirtualKeyboardAndroidDelegate.this.mAnchor, VirtualKeyboardAndroidDelegate.this.mCursorPos);
                    }
                }
            });
        }
    }

    public void SetEnterKeyLabel(int i) {
        if (this.mEnterkeyLabel != StdToRawEnterKeyLabel(i)) {
            this.mEnterkeyLabel = StdToRawEnterKeyLabel(i);
            UpdateDisplay();
        }
    }

    public void SetLayout(int i) {
        if (this.mKeyboardLayout != StdToRawLayout(i)) {
            this.mKeyboardLayout = StdToRawLayout(i);
            UpdateDisplay();
        }
    }

    public void SetMaxTextLength(int i) {
        this.mMaxTextLength = i;
    }

    public void SetShiftEnabled(boolean z) {
        int i = 4096;
        if (this.mShiftFlag != (z ? 4096 : 0)) {
            if (!z) {
                i = 0;
            }
            this.mShiftFlag = i;
            UpdateDisplay();
        }
    }

    public void SetText(String str) {
        Log("SetText() from game thread ( " + str + " )");
        this.mNewText = str;
        if (this.mNewText == null) {
            this.mNewText = "";
        }
        this.mMainActivity.runOnUiThread(new Runnable() {
            /* class com.ea.easp.VirtualKeyboardAndroidDelegate.AnonymousClass5 */

            public void run() {
                if (VirtualKeyboardAndroidDelegate.this.mTextField != null) {
                    VirtualKeyboardAndroidDelegate.this.mTextField.onExtTextChanged(VirtualKeyboardAndroidDelegate.this.mNewText);
                }
            }
        });
    }

    public void Shutdown() {
        UserSetVisible(false);
        this.mMainViewGroup = null;
        this.mRelativeLayout = null;
        this.mTextField = null;
    }

    public void UpdateDisplay() {
        if (IsVisible()) {
            Log("UpdateDisplay()");
            Hide();
            Show();
        }
    }

    public void UserSetVisible(boolean z) {
        this.mVisibleRequested = z;
        if (!this.mVisibleRequested) {
            Log("Hide from user set visible");
            Hide();
        } else if (!this.mPhysicalKeyboardVisible) {
            Log("Show from user set visible");
            Show();
        }
    }
}
