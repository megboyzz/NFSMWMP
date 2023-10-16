package com.eamobile.views;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import com.eamobile.DownloadActivityInternal;
import com.eamobile.download.LockManager;
import com.eamobile.download.Logging;
import com.eamobile.download.SpeedCalculator;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.Timer;
import java.util.TimerTask;

public class DownloadProgressView extends CustomView {
    protected static final int MSG_DOWNLOAD_FAILURE = 2;
    protected static final int MSG_DOWNLOAD_RETRY = 1;
    public static boolean downloaded = false;
    private static Timer t;
    private LockManager lockManager;
    private IProgressDialog progressDialog;
    public Handler progressHandler = new Handler() {
        /* class com.eamobile.views.DownloadProgressView.AnonymousClass2 */

        public void handleMessage(Message message) {
            try {
                if (DownloadActivityInternal.getMainActivity() != null && DownloadActivityInternal.isInitialized()) {
                    int totalDownloadSizeMB = DownloadActivityInternal.getTotalDownloadSizeMB();
                    int sizeDownloaded = (int) ((DownloadActivityInternal.getSizeDownloaded() / 1024) / 1024);
                    DownloadActivityInternal.setFlagLastReportDownload(true);
                    if (totalDownloadSizeMB > 0) {
                        if (DownloadProgressView.this.speedCalculator == null) {
                            Logging.DEBUG_OUT("DownloadProgressView: creating a new SpeedCalculator");
                            DownloadProgressView.this.speedCalculator = new SpeedCalculator(0.05f);
                            DownloadProgressView.this.timeSeconds = ((float) SystemClock.uptimeMillis()) / 1000.0f;
                        } else {
                            DownloadProgressView.this.speedCalculator.reportAmount(((float) DownloadActivityInternal.getRealDownloaded()) / 1024.0f, (((float) SystemClock.uptimeMillis()) / 1000.0f) - DownloadProgressView.this.timeSeconds);
                        }
                        DownloadProgressView.this.progressDialog.setDownloadMax(totalDownloadSizeMB);
                        DownloadProgressView.this.progressDialog.setDownloadProgress(sizeDownloaded);
                        DownloadProgressView.this.progressDialog.setDownloadSpeed(DownloadProgressView.this.speedCalculator.getCurrentSpeed());
                        DownloadProgressView.this.progressDialog.updateDialog();
                    }
                    if (DownloadActivityInternal.getMainActivity().getPercentDownloaded() >= 100 && DownloadProgressView.downloaded) {
                        Logging.DEBUG_OUT("Going to State SUCCESS");
                        DownloadProgressView.this.progressDialog.dismissDialog();
                        DownloadActivityInternal.getMainActivity().setState(11);
                    } else if (message.what == 1 && !DownloadProgressView.downloaded) {
                        Logging.DEBUG_OUT("Going to State RETRY");
                        Timer unused = DownloadProgressView.t = new Timer();
                        DownloadProgressView.this.timerTask = new RetryTimerTask();
                        DownloadProgressView.t.schedule(DownloadProgressView.this.timerTask, 0, 5000);
                    } else if (message.what == 2) {
                        Logging.DEBUG_OUT("Going to State FAILURE");
                        DownloadProgressView.this.progressDialog.dismissDialog();
                        DownloadActivityInternal.getMainActivity().setState(5);
                    }
                }
            } catch (Exception e) {
                Logging.DEBUG_OUT("Exception here:" + e + ",DownloadActivityInternal.getMainActivity():" + DownloadActivityInternal.getMainActivity());
                Logging.DEBUG_OUT_STACK(e);
            }
        }
    };
    private SpeedCalculator speedCalculator = null;
    private float timeSeconds = BitmapDescriptorFactory.HUE_RED;
    private RetryTimerTask timerTask;

    /* access modifiers changed from: package-private */
    public class RetryTimerTask extends TimerTask {
        static final int QTY_RETRY = 10;
        int numTries = 10;

        RetryTimerTask() {
        }

        public void run() {
            Logging.DEBUG_OUT("Attempting to download assets (attempt " + (10 - this.numTries) + "/" + 10 + ") in RetryTimerTask");
            if (this.numTries > 0) {
                DownloadProgressView.this.progressHandler.sendEmptyMessage(0);
                if (DownloadActivityInternal.getMainActivity().startDownload()) {
                    Logging.DEBUG_OUT("Download: Successful (in RetryTimerTask, after " + (10 - this.numTries) + " attempt(s)).");
                    DownloadProgressView.t.cancel();
                    DownloadProgressView.downloaded = true;
                    this.numTries = 0;
                } else if (DownloadActivityInternal.getMainActivity() != null && DownloadProgressView.this.encounteredFatalDownloadError()) {
                    DownloadProgressView.t.cancel();
                    DownloadProgressView.this.progressHandler.sendEmptyMessage(2);
                    this.numTries = 0;
                }
                this.numTries--;
                return;
            }
            Logging.DEBUG_OUT("Download: Failed (in RetryTimerTask).");
            DownloadProgressView.t.cancel();
            DownloadProgressView.this.progressHandler.sendEmptyMessage(2);
            this.numTries = 0;
        }
    }

    public DownloadProgressView(Context context) {
        super(context);
        Logging.DEBUG_OUT("DownloadProgressView constructor");
        this.context = context;
        this.lockManager = new LockManager(context);
    }

    private void showContent(View view) {
        Logging.DEBUG_OUT("DownloadProgressView showContent");
        this.progressDialog.showDialogContent();
        if (this.lockManager != null) {
            this.lockManager.acquireWifiLock();
        }
        if (DownloadActivityInternal.getForceWakeDuringDownload() && this.lockManager != null) {
            this.lockManager.acquireWakeLock();
        }
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void clean() {
        super.clean();
        Logging.DEBUG_OUT("DownloadProgressView clean");
        try {
            if (this.progressDialog != null && this.progressDialog.isDialogValid()) {
                this.progressDialog.dismissDialog();
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("DownloadProgressView Clean Exception:" + e);
        }
        if (this.lockManager != null) {
            this.lockManager.releaseWifiLock();
        }
        if (DownloadActivityInternal.getForceWakeDuringDownload() && this.lockManager != null) {
            this.lockManager.releaseWakeLock();
        }
    }

    public boolean encounteredFatalDownloadError() {
        int state = DownloadActivityInternal.getMainActivity().getState();
        return state == 12 || state == 13;
    }

    @Override // com.eamobile.views.CustomView, com.eamobile.views.IDownloadView
    public void init() {
        super.init();
        Logging.DEBUG_OUT("DownloadProgressView init");
        if (!DownloadActivityInternal.getMainActivity().useCustomProgressBar() || DownloadActivityInternal.getMainActivity().useOldProgressBar()) {
            this.progressDialog = new DefaultProgressDialog(this.context);
        } else {
            this.progressDialog = new CustomProgressDialog(this.context);
        }
        this.progressDialog.initDialog();
        new Thread(new Runnable() {
            /* class com.eamobile.views.DownloadProgressView.AnonymousClass1 */

            public void run() {
                try {
                    DownloadProgressView.downloaded = false;
                    new Thread() {
                        /* class com.eamobile.views.DownloadProgressView.AnonymousClass1.AnonymousClass1 */

                        public void run() {
                            Logging.DEBUG_OUT("STARTING A NEW DOWNLOAD >>>>>>>");
                            DownloadProgressView.downloaded = DownloadActivityInternal.getMainActivity().startDownload();
                            Logging.DEBUG_OUT("DOWNLOADED in PROGRESS VIEW:" + DownloadProgressView.downloaded);
                            if (!DownloadProgressView.downloaded && DownloadActivityInternal.getMainActivity() != null) {
                                Logging.DEBUG_OUT("Failed to download assets.  Internal state: " + DownloadActivityInternal.getMainActivity().getStateName());
                                if (!DownloadProgressView.this.encounteredFatalDownloadError()) {
                                    DownloadProgressView.this.progressHandler.sendEmptyMessage(1);
                                }
                            }
                        }
                    }.start();
                    Logging.DEBUG_OUT("DownloadProgressView before background loop downloaded=" + DownloadProgressView.downloaded);
                    while (!DownloadProgressView.downloaded && DownloadActivityInternal.isInitialized()) {
                        Thread.sleep(100);
                        DownloadProgressView.this.progressHandler.sendMessage(DownloadProgressView.this.progressHandler.obtainMessage());
                    }
                } catch (Exception e) {
                    Logging.DEBUG_OUT("DownloadProgressView init Exception:" + e);
                }
            }
        }).start();
        showContent(this);
    }

    @Override // com.eamobile.views.CustomView
    public void pause() {
        Logging.DEBUG_OUT("DownloadProgressView pause");
        if (DownloadActivityInternal.getForceWakeDuringDownload() && this.lockManager != null) {
            this.lockManager.releaseWakeLock();
        }
    }

    @Override // com.eamobile.views.CustomView
    public void resume() {
        Logging.DEBUG_OUT("DownloadProgressView resume");
        if (this.progressDialog != null && this.progressDialog.isDialogValid()) {
            showContent(this);
        }
    }
}
