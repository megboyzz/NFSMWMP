package com.eamobile.download;

public interface IZipExtractorEvent {
    void onExtractEntryFinish();

    void onExtractEntryStart(String str, long j);

    void onReportDownload(int i);

    void onReportProgress(int i);
}
