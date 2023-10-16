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

public class ContactingServerView extends CustomView {
    protected static final int MSG_DONE = 1;
    Dialog dialog;
    public Handler handler = new Handler() {
        /* class com.eamobile.views.ContactingServerView.AnonymousClass3 */

        public void handleMessage(Message message) {
            ContactingServerView.this.dialog.dismiss();
        }
    };

    public ContactingServerView(Context context) {
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
        this.dialog = ProgressDialog.show(this.context, Language.getString(37), Language.getString(38), true);
        this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            /* class com.eamobile.views.ContactingServerView.AnonymousClass1 */

            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return i == 82 || i == 84;
            }
        });
        new Thread() {
            /* class com.eamobile.views.ContactingServerView.AnonymousClass2 */

            public void run() {
                DownloadActivityInternal.getMainActivity().checkServerContent(true);
                ContactingServerView.this.handler.sendEmptyMessage(1);
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
