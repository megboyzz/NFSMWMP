package com.eamobile.download;

import android.os.Environment;
import android.os.StatFs;

public final class MemoryStatus {
    static final int ERROR = -1;

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static String formatSize(long j) {
        String str = null;
        if (j >= 1024) {
            str = "KiB";
            j /= 1024;
            if (j >= 1024) {
                str = "MiB";
                j /= 1024;
            }
        }
        StringBuilder sb = new StringBuilder(Long.toString(j));
        for (int length = sb.length() - 3; length > 0; length -= 3) {
            sb.insert(length, ',');
        }
        if (str != null) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static long getAvailableExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return -1;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public static long getAvailableInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getAvailableBlocks()) * ((long) statFs.getBlockSize());
    }

    public static long getTotalExternalMemorySize() {
        if (!externalMemoryAvailable()) {
            return -1;
        }
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize());
    }

    public static long getTotalInternalMemorySize() {
        StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        return ((long) statFs.getBlockCount()) * ((long) statFs.getBlockSize());
    }
}
