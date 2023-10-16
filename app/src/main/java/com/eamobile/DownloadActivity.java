package com.eamobile;

import android.app.Activity;
import android.content.Context;

public class DownloadActivity {
    private DownloadActivityInternal mDownloadActivityInternal;

    public DownloadActivity(Context context) {
        this.mDownloadActivityInternal = new DownloadActivityInternal(context);
    }

    public DownloadActivity(Context context, String str) {
        this.mDownloadActivityInternal = new DownloadActivityInternal(context, str);
    }

    public void destroyDownloadActvity() {
        this.mDownloadActivityInternal.destroyDownloadActvity();
    }

    public void init(Activity activity, IDownloadActivity iDownloadActivity, Context context, Object obj) {
        this.mDownloadActivityInternal.init(activity, iDownloadActivity, context, obj);
    }

    public void onDestroy() {
        this.mDownloadActivityInternal.onDestroy();
    }

    public void onPause() {
        this.mDownloadActivityInternal.onPause();
    }

    public void onResume() {
        this.mDownloadActivityInternal.onResume();
    }

    public void onWindowFocusChanged(boolean z) {
        this.mDownloadActivityInternal.onWindowFocusChanged(z);
    }

    public void setAssetPath(String str) {
        this.mDownloadActivityInternal.setAssetPath(str, true);
    }

    public void setAssetPath(String str, boolean z) {
        this.mDownloadActivityInternal.setAssetPath(str, z);
    }
}
