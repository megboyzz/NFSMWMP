package com.eamobile.download;

public class RemoteZipExtractorEvent implements IZipExtractorEvent {
    private DownloadProgress downloadProgress;
    private DownloadFileData zipFileData;

    public RemoteZipExtractorEvent(DownloadProgress downloadProgress2, DownloadFileData downloadFileData) {
        this.downloadProgress = downloadProgress2;
        this.zipFileData = downloadFileData;
    }

    @Override // com.eamobile.download.IZipExtractorEvent
    public void onExtractEntryFinish() {
        Logging.DEBUG_OUT("RemoteZipExtractorEvent.onExtractEntryFinish");
        this.downloadProgress.fillCurrentFileDownload(true);
    }

    @Override // com.eamobile.download.IZipExtractorEvent
    public void onExtractEntryStart(String str, long j) {
        Logging.DEBUG_OUT("RemoteZipExtractorEvent.onExtractEntryStart");
        this.downloadProgress.setCurrentFile("z_" + this.zipFileData.getFileName() + "_" + str, j);
    }

    @Override // com.eamobile.download.IZipExtractorEvent
    public void onReportDownload(int i) {
        this.downloadProgress.reportTotalDownloaded((long) i);
    }

    @Override // com.eamobile.download.IZipExtractorEvent
    public void onReportProgress(int i) {
        this.downloadProgress.reportProgress((long) i, true);
    }
}
