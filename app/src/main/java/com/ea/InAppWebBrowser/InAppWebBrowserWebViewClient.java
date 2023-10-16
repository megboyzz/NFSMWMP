package com.ea.InAppWebBrowser;

import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

public class InAppWebBrowserWebViewClient extends WebViewClient {
    public int mInstanceID = 0;
    private SortedSet<UUID> mJavascriptIds = Collections.synchronizedSortedSet(new TreeSet());

    public native void OnJavascriptResult(String str, int i);

    public native void OnLoadError(String str, int i);

    public native void OnLoadFinished(String str, int i);

    public native void OnLoadStarted(String str, int i);

    public native boolean ShouldLoadURL(String str, int i);

    public boolean addUUID(UUID uuid) {
        if (this.mJavascriptIds.contains(uuid)) {
            return false;
        }
        this.mJavascriptIds.add(uuid);
        return true;
    }

    public void onJavascriptResult(String str, String str2) throws Exception {
        UUID fromString = UUID.fromString(str2);
        if (fromString == null || !this.mJavascriptIds.contains(fromString)) {
            throw new Exception("Unable to verify validity of script result.");
        }
        this.mJavascriptIds.remove(fromString);
        OnJavascriptResult(str, this.mInstanceID);
    }

    public void onLoadResource(WebView webView, String str) {
        Log.e("InAppWebBrowserWebViewClient", "onLoadResource: " + str);
        OnLoadStarted(str, this.mInstanceID);
    }

    public void onPageFinished(WebView webView, String str) {
        Log.e("InAppWebBrowserWebViewClient", "onPageFinished: " + str);
        OnLoadFinished(str, this.mInstanceID);
    }

    public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
        Log.e("InAppWebBrowserWebViewClient", "onPageStarted: " + str);
        OnLoadStarted(str, this.mInstanceID);
    }

    public void onReceivedError(WebView webView, int i, String str, String str2) {
        Log.e("InAppWebBrowserWebViewClient", "onReceivedError");
        OnLoadError("Loading Error. URL: " + str2 + " ErrorCode: " + i + " Description: " + str, this.mInstanceID);
    }

    @Override // android.webkit.WebViewClient
    public boolean shouldOverrideUrlLoading(WebView webView, String str) {
        Log.e("InAppWebBrowserWebViewClient", "shouldOverrideUrlLoading: " + str);
        return !ShouldLoadURL(str, this.mInstanceID);
    }
}
