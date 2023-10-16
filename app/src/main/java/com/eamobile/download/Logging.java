package com.eamobile.download;

import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public class Logging {
    public static boolean DEBUG_ON = false;
    static OutputStream out = null;

    public static void DEBUG_CLOSE() {
        if (DEBUG_ON) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void DEBUG_INIT() {
        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/debug.enable").exists()) {
            DEBUG_ON = true;
            DEBUG_OUT("*** WARNING: debug.enable found in SD card root, enabling debug output.   ***");
            DEBUG_OUT("*** ADC-only debug output is saved in SD card root, under debug.txt file. ***");
        }
        if (DEBUG_ON) {
            try {
                out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/debug.txt", true);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    public static void DEBUG_OUT(String str) {
        if (DEBUG_ON) {
            Log.w("DownloadActivity", str);
            try {
                if (out != null) {
                    out.write((str + "\n").getBytes());
                }
            } catch (IOException e) {
            }
        }
    }

    public static void DEBUG_OUT_STACK(Exception exc) {
        StringWriter stringWriter = new StringWriter();
        exc.printStackTrace(new PrintWriter(stringWriter));
        DEBUG_OUT(stringWriter.toString());
    }
}
