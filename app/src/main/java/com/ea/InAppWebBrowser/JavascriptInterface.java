package com.ea.InAppWebBrowser;

public class JavascriptInterface {
    private InAppWebBrowserWebViewClient mWebViewClient;

    public JavascriptInterface(InAppWebBrowserWebViewClient inAppWebBrowserWebViewClient) {
        this.mWebViewClient = inAppWebBrowserWebViewClient;
    }

    public void ReceiveResult(String str, String str2) throws Exception {
        this.mWebViewClient.onJavascriptResult(str, str2);
    }
}
