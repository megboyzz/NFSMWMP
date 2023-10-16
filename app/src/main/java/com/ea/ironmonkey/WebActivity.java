package com.ea.ironmonkey;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.ea.games.nfs13_na.R;
import java.util.Locale;

public class WebActivity extends Activity {
    public static String m_Language;
    private static volatile Runnable m_RunnableBuildAndShowHTML = null;
    public static String m_URL;
    public static boolean m_bWorkingState = false;
    private Bitmap backButton;
    private Bitmap backButtonPressed;
    private ImageView backImage;
    private String m_PageURL = null;
    private ProgressBar m_progressBar = null;
    final Handler progressHandler = new Handler() {
        /* class com.ea.ironmonkey.WebActivity.AnonymousClass1 */

        public void handleMessage(Message message) {
            WebActivity.this.setProgress(message.arg1);
            WebActivity.this.m_progressBar.setProgress(message.arg1);
        }
    };
    private WebView webview;

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void BuildAndShowHTML() {
        if (this.m_PageURL != null) {
            this.webview.loadUrl(this.m_PageURL);
        }
    }

    public void onCreate(Bundle bundle) {
        m_bWorkingState = true;
        super.onCreate(bundle);
        requestWindowFeature(2);
        getWindow().setFlags(1024, 1024);
        setRequestedOrientation(0);
        requestWindowFeature(1);
        setProgressBarVisibility(true);
        setContentView(R.layout.webview);
        this.webview = (WebView) findViewById(R.id.mainWebView);
        this.webview.getSettings().setJavaScriptEnabled(true);
        this.m_progressBar = (ProgressBar) findViewById(R.id.progressBarWeb);
        this.backButton = BitmapFactory.decodeResource(getResources(), R.drawable.btn_back);
        this.backButtonPressed = BitmapFactory.decodeResource(getResources(), R.drawable.btn_back_pressed);
        this.backImage = (ImageView) findViewById(R.id.backButton);
        this.backImage.setOnTouchListener(new View.OnTouchListener() {
            /* class com.ea.ironmonkey.WebActivity.AnonymousClass2 */

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    WebActivity.this.backImage.setImageBitmap(WebActivity.this.backButtonPressed);
                    return false;
                } else if (motionEvent.getAction() != 1) {
                    return false;
                } else {
                    WebActivity.this.backImage.setImageBitmap(WebActivity.this.backButton);
                    return false;
                }
            }
        });
        this.backImage.setOnClickListener(new View.OnClickListener() {
            /* class com.ea.ironmonkey.WebActivity.AnonymousClass3 */

            public void onClick(View view) {
                WebActivity.m_bWorkingState = false;
                WebActivity.this.finish();
            }
        });
        Bundle extras = getIntent().getExtras();
        String string = extras.getString("URL");
        this.m_PageURL = string;
        m_URL = string;
        String string2 = extras.getString("Language");
        m_Language = string2;
        if (string2 != null) {
            try {
                Configuration configuration = new Configuration(getResources().getConfiguration());
                configuration.locale = new Locale(string2);
                ((TextView) findViewById(R.id.appName)).setText(new Resources(getResources().getAssets(), getResources().getDisplayMetrics(), configuration).getString(R.string.app_full_name));
            } catch (Throwable th) {
            }
        }
        this.webview.setWebChromeClient(new WebChromeClient() {
            /* class com.ea.ironmonkey.WebActivity.AnonymousClass4 */

            public void onProgressChanged(WebView webView, int i) {
                Message obtainMessage = WebActivity.this.progressHandler.obtainMessage();
                obtainMessage.arg1 = i;
                WebActivity.this.progressHandler.sendMessage(obtainMessage);
            }
        });
        this.webview.setWebViewClient(new WebViewClient() {
            /* class com.ea.ironmonkey.WebActivity.AnonymousClass5 */

            public void onPageFinished(WebView webView, String str) {
                WebActivity.this.m_progressBar.setVisibility(8);
            }

            public void onReceivedError(WebView webView, int i, String str, String str2) {
                Toast.makeText(WebActivity.this, "Error ! " + str, 0).show();
            }
        });
        m_RunnableBuildAndShowHTML = new Runnable() {
            /* class com.ea.ironmonkey.WebActivity.AnonymousClass6 */

            public void run() {
                WebActivity.this.BuildAndShowHTML();
            }
        };
        runOnUiThread(m_RunnableBuildAndShowHTML);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        this.backImage.setImageBitmap(this.backButtonPressed);
        return true;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyUp(i, keyEvent);
        }
        this.backImage.setImageBitmap(this.backButton);
        m_bWorkingState = false;
        finish();
        return true;
    }
}
