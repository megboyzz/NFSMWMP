package com.eamobile.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import com.eamobile.DownloadActivityInternal;
import com.eamobile.Language;
import com.eamobile.download.Logging;
import java.lang.reflect.Method;

public class DefaultProgressDialog extends CustomView implements IProgressDialog {
    private static Method mSetProgressNumberFormat;
    private ProgressDialog dialog = null;
    private int max = 0;
    private int progress = 0;
    private float speed;

    public DefaultProgressDialog(Context context) {
        super(context);
        this.context = context;
        mSetProgressNumberFormat = null;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void dismissDialog() {
        this.dialog.dismiss();
    }

    @Override // com.eamobile.views.IProgressDialog
    public void initDialog() {
        try {
            mSetProgressNumberFormat = ProgressDialog.class.getMethod("setProgressNumberFormat", String.class);
        } catch (NoSuchMethodException e) {
        }
        this.dialog = new ProgressDialog(this.context);
        this.dialog.setCancelable(true);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setMessage(Language.getString(20));
        this.dialog.setProgressStyle(1);
        this.dialog.setProgress(0);
    }

    @Override // com.eamobile.views.IProgressDialog
    public boolean isDialogValid() {
        return this.dialog != null;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void setDownloadMax(int i) {
        this.max = i;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void setDownloadProgress(int i) {
        this.progress = i;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void setDownloadSpeed(float f) {
        this.speed = f;
    }

    @Override // com.eamobile.views.IProgressDialog
    public void showDialogContent() {
        if (this.dialog != null) {
            this.dialog.show();
            this.dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                /* class com.eamobile.views.DefaultProgressDialog.AnonymousClass1 */

                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    return i == 82 || i == 84;
                }
            });
            this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                /* class com.eamobile.views.DefaultProgressDialog.AnonymousClass2 */

                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    DownloadActivityInternal.getMainActivity().startGameActivity(0);
                }
            });
        }
    }

    @Override // com.eamobile.views.IProgressDialog
    public void updateDialog() {
        try {
            if (mSetProgressNumberFormat != null) {
                this.dialog.setMax(this.max);
                try {
                    String format = String.format("%.0f", Float.valueOf(this.speed));
                    mSetProgressNumberFormat.invoke(this.dialog, "%d MB of %d MB    " + format + " Kb/s");
                    this.dialog.setProgress(this.progress);
                } catch (Exception e) {
                    this.dialog.setProgress(DownloadActivityInternal.getMainActivity().getPercentDownloaded());
                }
            }
        } catch (Exception e2) {
            Logging.DEBUG_OUT("Exception here:" + e2);
            this.dialog.setProgress(DownloadActivityInternal.getMainActivity().getPercentDownloaded());
        }
    }
}
