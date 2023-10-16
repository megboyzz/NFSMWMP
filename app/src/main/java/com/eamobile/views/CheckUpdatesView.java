package com.eamobile.views;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import com.eamobile.DownloadActivityInternal;
import com.eamobile.Language;
import com.eamobile.download.Logging;

public class CheckUpdatesView extends CustomView {
    protected static final int MSG_NO_UPDATES = 0;
    protected static final int MSG_UPDATE_FOUND = 1;
    Dialog dialog;
    public Handler handler = new Handler() {
        /* class com.eamobile.views.CheckUpdatesView.AnonymousClass3 */

        public void handleMessage(Message message) {
            if (message.what == 1) {
                Logging.DEBUG_OUT("An update has been found");
                CheckUpdatesView.this.dialog.dismiss();
                if (DownloadActivityInternal.getMainActivity() != null) {
                    DownloadActivityInternal.getMainActivity().setState(9);
                }
            } else if (message.what == 0) {
                Logging.DEBUG_OUT("No updates found");
                CheckUpdatesView.this.dialog.dismiss();
                if (DownloadActivityInternal.getMainActivity() != null) {
                    DownloadActivityInternal.getMainActivity().setState(11);
                }
            }
        }
    };
    boolean updateFound = false;

    public CheckUpdatesView(Context context) {
        super(context);
        this.context = context;
    }

    private void showContent(View view) {
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void clean() {
        super.clean();
        try {
            if (this.dialog != null) {
                this.dialog.dismiss();
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("CheckUpdates View clean:" + e);
        }
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void init() {
        super.init();
        this.dialog = ProgressDialog.show(this.context, Language.getString(21), Language.getString(22), true);
        this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class com.eamobile.views.CheckUpdatesView.AnonymousClass1 */

            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 82 || i == 84;
            }
        });
        new Thread() {
            /* class com.eamobile.views.CheckUpdatesView.AnonymousClass2 */

            public void run() {
                CheckUpdatesView.this.updateFound = DownloadActivityInternal.getMainActivity().checkForUpdates();
                if (CheckUpdatesView.this.updateFound) {
                    CheckUpdatesView.this.handler.sendEmptyMessage(1);
                } else if (DownloadActivityInternal.getMainActivity() == null || !DownloadActivityInternal.getMainActivity().checkScreenSizeChange()) {
                    CheckUpdatesView.this.handler.sendEmptyMessage(0);
                } else {
                    CheckUpdatesView.this.handler.sendEmptyMessage(1);
                }
            }
        }.start();
        showContent(this);
    }

    @Override // com.eamobile.views.CustomView
    public void resume() {
        if (this.dialog != null) {
            showContent(this);
        }
    }
}
