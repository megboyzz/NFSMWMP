package com.eamobile.download;

public class LocalZipExtractorEvent implements IZipExtractorEvent {
    @Override // com.eamobile.download.IZipExtractorEvent
    public void onExtractEntryFinish() {
        Logging.DEBUG_OUT("LocalZipExtractorEvent.onExtractEntryFinish");
    }

    @Override // com.eamobile.download.IZipExtractorEvent
    public void onExtractEntryStart(String str, long j) {
        Logging.DEBUG_OUT("LocalZipExtractorEvent.onExtractEntryStart");
    }

    @Override // com.eamobile.download.IZipExtractorEvent
    public void onReportDownload(int i) {
    }

    @Override // com.eamobile.download.IZipExtractorEvent
    public void onReportProgress(int i) {
    }
}
