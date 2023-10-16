package com.eamobile.download;

public class ZipExtractor {
    private static final int ERROR_ZIP_CHECKSUM_MATCH_FAILED = -4;
    private static final int ERROR_ZIP_CHECKSUM_NOT_FOUND = -3;
    private static final int ERROR_ZIP_EXCEPTION = -1;
    private static final int ERROR_ZIP_NO_ENTRIES = -2;
    private static final int NO_ZIP_ERRORS = 1;
    private static boolean pause = false;
    private static boolean pauseDirty = false;
    private IZipExtractorEvent zipExtractorEvent;

    public static void setPause(boolean z) {
        pause = z;
        pauseDirty = true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:104:?, code lost:
        return -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0020, code lost:
        if (r18 <= 0) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0026, code lost:
        if (r36.isEmpty() == false) goto L_0x0054;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0028, code lost:
        com.eamobile.download.Logging.DEBUG_OUT("Read all the files from Zip Stream");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0054, code lost:
        com.eamobile.download.Logging.DEBUG_OUT("[ERROR] Bad Zip, contained " + r18 + " Files");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0331, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x0332, code lost:
        com.eamobile.download.Logging.DEBUG_OUT("Exception: downloadAndValidateZipFile():" + r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
        return 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:?, code lost:
        return -2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int extractFiles(java.io.InputStream r35, java.util.Hashtable<java.lang.String, java.lang.Long> r36, java.lang.String r37, int r38, com.eamobile.download.IZipExtractorEvent r39) {
        /*
        // Method dump skipped, instructions count: 853
        */
        throw new UnsupportedOperationException("Method not decompiled: com.eamobile.download.ZipExtractor.extractFiles(java.io.InputStream, java.util.Hashtable, java.lang.String, int, com.eamobile.download.IZipExtractorEvent):int");
    }
}
