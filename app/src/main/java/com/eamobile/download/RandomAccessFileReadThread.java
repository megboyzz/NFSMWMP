package com.eamobile.download;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileReadThread extends Thread {
    private byte[] buffer = new byte[8192];
    public int currentFileSize = 0;
    private DownloadProgress downloadProgress;
    private RandomAccessFile file;
    private InputStream in;
    public boolean killMe = false;
    public boolean reading = true;
    public int sizeDownloaded = 0;
    public long timestamp = System.currentTimeMillis();

    public RandomAccessFileReadThread(InputStream inputStream, RandomAccessFile randomAccessFile, DownloadProgress downloadProgress2) {
        this.in = inputStream;
        this.file = randomAccessFile;
        this.downloadProgress = downloadProgress2;
    }

    public void run() {
        int i = 0;
        while (i != -1) {
            if (i != 0) {
                this.timestamp = System.currentTimeMillis();
            }
            try {
                i = this.in.read(this.buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (this.killMe) {
                break;
            } else if (i == 0) {
                Logging.DEBUG_OUT("0 BYTES READ>>>");
            } else if (i > 0) {
                try {
                    this.file.write(this.buffer, 0, i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.sizeDownloaded += i;
                this.downloadProgress.reportTotalDownloaded((long) i);
                try {
                    this.currentFileSize = ((int) this.file.getFilePointer()) - 1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (this.currentFileSize < 0) {
                    this.currentFileSize = 0;
                }
            }
        }
        this.reading = false;
    }
}
