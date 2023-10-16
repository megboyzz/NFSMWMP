package com.eamobile.views;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import com.eamobile.DownloadActivityInternal;
import com.eamobile.download.Logging;

public class ShowBGView extends CustomView {
    public ShowBGView(Context context) {
        super(context);
        this.context = context;
    }

    private void showContent(View view) {
        new CountDownTimer(1000, 100) {
            /* class com.eamobile.views.ShowBGView.AnonymousClass1 */

            public void onFinish() {
                if (DownloadActivityInternal.getMainActivity() != null) {
                    DownloadActivityInternal.getMainActivity().setState(3);
                }
            }

            public void onTick(long j) {
            }
        }.start();
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void clean() {
        super.clean();
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void init() {
        super.init();
        Logging.DEBUG_OUT("ShowBGView.init: before calling showContent");
        showContent(this);
        Logging.DEBUG_OUT("ShowBGView.init: after calling showContent");
    }

    @Override // com.eamobile.views.CustomView
    public void resume() {
        Logging.DEBUG_OUT("ShowBGView.resume");
    }
}
