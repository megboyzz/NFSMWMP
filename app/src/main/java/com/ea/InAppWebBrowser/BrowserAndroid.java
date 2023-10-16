package com.ea.InAppWebBrowser;

import android.app.Activity;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import com.ea.easp.VirtualKeyboardAndroidDelegate;
import java.util.UUID;

public class BrowserAndroid {
    public static int gInstanceCount = 0;
    public static Activity mActivity;
    public static ViewGroup mViewGroup;
    public final int JAVASCRIPT_DISABLE = 0;
    public final int JAVASCRIPT_ENABLE = 1;
    public final int SCROLLBARSTYLE_DEFAULT = 0;
    public final int SCROLLBARSTYLE_INSIDE_INSET = 2;
    public final int SCROLLBARSTYLE_INSIDE_OVERLAY = 1;
    public final int SCROLLBARSTYLE_OUTSIDE_INSET = 4;
    public final int SCROLLBARSTYLE_OUTSIDE_OVERLAY = 3;
    public final int SCROLLBAR_INVISIBLE = 1;
    public final int SCROLLBAR_VISIBLE = 0;
    public int mInstanceID = 0;
    public RelativeLayout mLayout;
    public WebView mWebView;
    public InAppWebBrowserWebViewClient mWebViewClient;

    public static void Shutdown() {
        ShutdownNativeImpl();
    }

    private static native void ShutdownNativeImpl();

    public static void Startup(Activity activity, ViewGroup viewGroup) {
        mActivity = activity;
        mViewGroup = viewGroup;
        StartupNativeImpl();
    }

    private static native void StartupNativeImpl();

    public void EvaluateJavaScript(final String str) {
        mActivity.runOnUiThread(new Runnable() {
            /* class com.ea.InAppWebBrowser.BrowserAndroid.AnonymousClass5 */

            public void run() {
                UUID randomUUID;
                do {
                    randomUUID = UUID.randomUUID();
                } while (!BrowserAndroid.this.mWebViewClient.addUUID(randomUUID));
                BrowserAndroid.this.mWebView.loadUrl("javascript: JavascriptCallback.ReceiveResult(eval(" + str + "), '" + randomUUID.toString() + "');");
            }
        });
    }

    public void LoadHTML(final String str) {
        mActivity.runOnUiThread(new Runnable() {
            /* class com.ea.InAppWebBrowser.BrowserAndroid.AnonymousClass3 */

            public void run() {
                BrowserAndroid.this.mWebView.loadData(str, "text/html", null);
            }
        });
    }

    public void OpenUrl(final String str) {
        mActivity.runOnUiThread(new Runnable() {
            /* class com.ea.InAppWebBrowser.BrowserAndroid.AnonymousClass2 */

            public void run() {
                BrowserAndroid.this.mWebView.loadUrl(str);
            }
        });
    }

    public void SetViewFrame(final int i, final int i2, final int i3, final int i4) {
        if (this.mWebView != null) {
            mActivity.runOnUiThread(new Runnable() {
                /* class com.ea.InAppWebBrowser.BrowserAndroid.AnonymousClass4 */

                public void run() {
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(i3, i4);
                    layoutParams.leftMargin = i;
                    layoutParams.topMargin = i2;
                    BrowserAndroid.this.mLayout.updateViewLayout(BrowserAndroid.this.mWebView, layoutParams);
                }
            });
        }
    }

    public void destroy() {
        mActivity.runOnUiThread(new Runnable() {
            /* class com.ea.InAppWebBrowser.BrowserAndroid.AnonymousClass6 */

            public void run() {
                BrowserAndroid.this.mWebView.stopLoading();
                BrowserAndroid.this.mWebView.setWebViewClient(null);
                BrowserAndroid.this.mWebViewClient = null;
                BrowserAndroid.this.mLayout.removeView(BrowserAndroid.this.mWebView);
                BrowserAndroid.mViewGroup.removeView(BrowserAndroid.this.mLayout);
                BrowserAndroid.this.mWebView = null;
                BrowserAndroid.this.mLayout = null;
            }
        });
    }

    public void init(final int i, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final boolean z, final boolean z2) {
        int i8 = gInstanceCount;
        gInstanceCount = i8 + 1;
        this.mInstanceID = i8;
        mActivity.runOnUiThread(new Runnable() {
            /* class com.ea.InAppWebBrowser.BrowserAndroid.AnonymousClass1 */

            public void run() {
                boolean z = true;
                BrowserAndroid.this.mLayout = new RelativeLayout(BrowserAndroid.mActivity);
                BrowserAndroid.this.mWebView = new WebView(BrowserAndroid.mActivity);
                BrowserAndroid.this.mWebViewClient = new InAppWebBrowserWebViewClient();
                BrowserAndroid.this.mWebViewClient.mInstanceID = BrowserAndroid.this.mInstanceID;
                BrowserAndroid.this.mWebView.setWebViewClient(BrowserAndroid.this.mWebViewClient);
                if (z2) {
                    BrowserAndroid.this.mWebView.setBackgroundColor(0);
                    Class<?> cls = BrowserAndroid.this.mWebView.getClass();
                    try {
                        cls.getMethod("setLayerType", Integer.TYPE, Paint.class).invoke(BrowserAndroid.this.mWebView, Integer.valueOf(((Integer) cls.getField("LAYER_TYPE_SOFTWARE").get(BrowserAndroid.this.mWebView)).intValue()), null);
                    } catch (Exception e) {
                    }
                }
                if (!z) {
                    Class<?> cls2 = BrowserAndroid.this.mWebView.getClass();
                    try {
                        cls2.getMethod("setOverScrollMode", Integer.TYPE).invoke(BrowserAndroid.this.mWebView, Integer.valueOf(((Integer) cls2.getField("OVER_SCROLL_NEVER").get(BrowserAndroid.this.mWebView)).intValue()));
                    } catch (Exception e2) {
                    }
                }
                //BrowserAndroid.this.mWebView.requestFocus(TransportMediator.KEYCODE_MEDIA_RECORD);
                BrowserAndroid.this.mWebView.setOnTouchListener(new View.OnTouchListener() {
                    /* class com.ea.InAppWebBrowser.BrowserAndroid.AnonymousClass1.AnonymousClass1 */

                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case 0:
                            case 1:
                                if (view.hasFocus()) {
                                    return false;
                                }
                                view.requestFocus();
                                return false;
                            default:
                                return false;
                        }
                    }
                });
                if (i5 == 1) {
                    BrowserAndroid.this.mWebView.getSettings().setJavaScriptEnabled(true);
                }
                BrowserAndroid.this.mWebView.setVerticalScrollBarEnabled(i6 == 0);
                WebView webView = BrowserAndroid.this.mWebView;
                if (i6 != 0) {
                    z = false;
                }
                webView.setHorizontalScrollBarEnabled(z);
                BrowserAndroid.this.mWebView.addJavascriptInterface(new JavascriptInterface(BrowserAndroid.this.mWebViewClient), "JavascriptCallback");
                switch (i7) {
                    case 1:
                        BrowserAndroid.this.mWebView.setScrollBarStyle(0);
                        break;
                    case 2:
                        BrowserAndroid.this.mWebView.setScrollBarStyle(16777216);
                        break;
                    case 3:
                        BrowserAndroid.this.mWebView.setScrollBarStyle(VirtualKeyboardAndroidDelegate.IME_FLAG_NO_FULLSCREEN);
                        break;
                    case 4:
                        BrowserAndroid.this.mWebView.setScrollBarStyle(50331648);
                        break;
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(i3, i4);
                layoutParams.leftMargin = i;
                layoutParams.topMargin = i2;
                BrowserAndroid.this.mLayout.addView(BrowserAndroid.this.mWebView, layoutParams);
                BrowserAndroid.mViewGroup.addView(BrowserAndroid.this.mLayout);
            }
        });
    }
}
