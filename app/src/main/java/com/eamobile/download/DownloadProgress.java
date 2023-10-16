package com.eamobile.download;

import java.util.HashMap;

public class DownloadProgress {
    private FileProgress currentFileProgress = null;
    private boolean isLastReportDownload = true;
    private HashMap<String, FileProgress> progressMap = new HashMap<>();
    private long realDownloaded = 0;
    private long sizeDownloaded = 0;

    /* access modifiers changed from: package-private */
    public class FileProgress {
        public long downloaded;
        public long total;

        FileProgress(long j, long j2) {
            this.downloaded = j;
            this.total = j2;
        }
    }

    public DownloadProgress() {
        Logging.DEBUG_OUT("DownloadProgress: constructor");
    }

    public void fillCurrentFileDownload(boolean z) {
        if (this.currentFileProgress != null) {
            long j = this.currentFileProgress.total - this.currentFileProgress.downloaded;
            if (j < 0) {
                j = 0;
            }
            this.currentFileProgress.downloaded += j;
            if (z) {
                this.sizeDownloaded += j;
            }
        }
    }

    public boolean getFlagLastReportDownload() {
        return this.isLastReportDownload;
    }

    public long getRealDownloaded() {
        return this.realDownloaded;
    }

    public long getSizeDownloaded() {
        return this.sizeDownloaded;
    }

    public void reportProgress(long j, boolean z) {
        if (this.currentFileProgress != null) {
            if (j > this.currentFileProgress.total) {
                j = this.currentFileProgress.total;
            }
            long j2 = j - this.currentFileProgress.downloaded;
            if (j2 > 0) {
                if (z) {
                    this.sizeDownloaded += j2;
                }
                this.currentFileProgress.downloaded = j;
            }
        }
    }

    public void reportTotalDownloaded(long j) {
        this.realDownloaded += j;
    }

    public void setCurrentFile(String str, long j) {
        Logging.DEBUG_OUT("DownloadProgress: controlling progress for: " + str);
        Logging.DEBUG_OUT("DownloadProgress: size: " + j);
        FileProgress fileProgress = this.progressMap.get(str);
        if (fileProgress == null) {
            this.currentFileProgress = new FileProgress(0, j);
            this.progressMap.put(str, this.currentFileProgress);
            return;
        }
        this.currentFileProgress = fileProgress;
    }

    public void setFlagLastReportDownload(boolean z) {
        this.isLastReportDownload = z;
    }
}
