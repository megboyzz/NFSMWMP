package com.eamobile.download;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/* access modifiers changed from: package-private */
/* compiled from: ZipExtractor */
public class ReadThread extends Thread {
    private byte[] buffer = new byte[8192];
    private float compRatio = BitmapDescriptorFactory.HUE_RED;
    private InputStream in;
    public boolean killMe = false;
    private OutputStream out;
    public volatile boolean pause = false;
    public boolean reading = true;
    public int sizeDownloaded = 0;
    public long timestamp = System.currentTimeMillis();
    private IZipExtractorEvent zipExtractorEvent;

    public ReadThread(InputStream inputStream, OutputStream outputStream, float f, IZipExtractorEvent iZipExtractorEvent) {
        this.in = inputStream;
        this.out = outputStream;
        this.compRatio = f;
        this.zipExtractorEvent = iZipExtractorEvent;
    }

    public void run() {
        int i = 0;
        while (i != -1) {
            if (i != 0) {
                this.timestamp = System.currentTimeMillis();
            }
            if (this.killMe) {
                break;
            } else if (!this.pause) {
                try {
                    i = this.in.read(this.buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (i == 0) {
                    Logging.DEBUG_OUT("0 BYTES READ>>>");
                } else if (i > 0) {
                    try {
                        this.out.write(this.buffer, 0, i);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    this.sizeDownloaded += i;
                    this.zipExtractorEvent.onReportDownload(Math.round(((float) i) * this.compRatio));
                }
            }
        }
        this.reading = false;
    }
}
