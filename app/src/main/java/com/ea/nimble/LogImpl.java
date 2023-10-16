/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Environment
 *  android.util.Log
 */
package com.ea.nimble;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class LogImpl
extends Component
implements ILog {
    private static final int DEFAULT_CHECK_INTERVAL = 3600;
    private static final int DEFAULT_CONSOLE_OUTPUT_LIMIT = 4000;
    private static final int DEFAULT_MESSAGE_LENGTH_LIMIT = 1000;
    private static final int DEFAULT_SIZE_LIMIT = 1024;
    private ArrayList<LogRecord> m_cache = new ArrayList();
    private BaseCore m_core;
    private File m_filePath = null;
    private DateFormat m_format = null;
    private Timer m_guardTimer = null;
    private int m_interval = 0;
    private int m_level = 0;
    private FileOutputStream m_logFileStream = null;
    private int m_messageLengthLimit;
    private int m_sizeLimit;

    LogImpl() {
    }

    private void clearLog() {
        try {
            this.m_logFileStream.close();
            this.m_logFileStream = new FileOutputStream(this.m_filePath, false);
            return;
        }
        catch (IOException iOException) {
            Log.e((String)"Nimble", (String)"LOG: Can't clear log file");
            return;
        }
    }

    private void configure() {
        boolean z = this.m_level == 0;
        Map<String, String> settings = this.m_core.getSettings(BaseCore.NIMBLE_LOG_SETTING);
        if (settings == null) {
            int parseLevel = parseLevel(null);
            if (parseLevel != this.m_level) {
                this.m_level = parseLevel;
                Log.i(Global.NIMBLE_ID, String.format("LOG: Default Log level(%d) without log configuration file", Integer.valueOf(this.m_level)));
                return;
            }
            return;
        }
        int parseLevel2 = parseLevel(settings.get("Level"));
        if (parseLevel2 != this.m_level) {
            this.m_level = parseLevel2;
            Log.i(Global.NIMBLE_ID, String.format("LOG: Log level(%d)", Integer.valueOf(this.m_level)));
        }
        if (this.m_level <= 100) {
            this.m_messageLengthLimit = 0;
        } else {
            String str = settings.get("MessageLengthLimit");
            if (str == null) {
                this.m_messageLengthLimit = 1000;
            } else {
                try {
                    this.m_messageLengthLimit = Integer.parseInt(str);
                    if (this.m_messageLengthLimit < 0) {
                        this.m_messageLengthLimit = 1000;
                    }
                } catch (NumberFormatException e) {
                    this.m_messageLengthLimit = 1000;
                }
            }
        }
        String str2 = settings.get("File");
        if (Utility.validString(str2)) {
            String str3 = settings.get("Location");
            String str4 = ApplicationEnvironment.getComponent().getCachePath() + File.separator + str2;
            String str5 = str4;
            if (Utility.validString(str3)) {
                str5 = str4;
                if (str3.equalsIgnoreCase("external")) {
                    str5 = str4;
                    if (Environment.getExternalStorageState().equals("mounted")) {
                        String name = ApplicationEnvironment.getCurrentActivity().getClass().getPackage().getName();
                        try {
                            name = ApplicationEnvironment.getCurrentActivity().getPackageManager().getPackageInfo(ApplicationEnvironment.getCurrentActivity().getPackageName(), 0).packageName;
                        } catch (Exception e2) {
                        }
                        File file = new File(Environment.getExternalStorageDirectory(), name);
                        boolean exists = file.exists();
                        boolean z2 = exists;
                        if (!exists) {
                            z2 = file.mkdir();
                        }
                        str5 = str4;
                        if (z2) {
                            str5 = file + File.separator + str2;
                        }
                    }
                }
            }
            File file2 = new File(str5);
            if (file2 != this.m_filePath) {
                this.m_filePath = file2;
                try {
                    this.m_logFileStream = new FileOutputStream(this.m_filePath, true);
                    Log.d(Global.NIMBLE_ID, "LOG: File path: " + this.m_filePath.toString());
                } catch (FileNotFoundException e3) {
                    Log.e(Global.NIMBLE_ID, "LOG: Can't create log file at " + str5);
                    this.m_filePath = null;
                    return;
                }
            }
            String str6 = settings.get("DateFormat");
            if (str6 == null || str6.length() <= 0) {
                this.m_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            } else {
                this.m_format = new SimpleDateFormat(str6, Locale.getDefault());
            }
            try {
                this.m_interval = Integer.parseInt(settings.get("FileCheckInterval"));
                if (this.m_interval <= 0) {
                    this.m_interval = DEFAULT_CHECK_INTERVAL;
                }
            } catch (NumberFormatException e4) {
                this.m_interval = DEFAULT_CHECK_INTERVAL;
            }
            try {
                this.m_sizeLimit = Integer.parseInt(settings.get("MaxFileSize"));
                if (this.m_sizeLimit <= 0) {
                    this.m_sizeLimit = DEFAULT_SIZE_LIMIT;
                }
            } catch (NumberFormatException e5) {
                this.m_sizeLimit = DEFAULT_SIZE_LIMIT;
            }
            GuardTask guardTask = new GuardTask();
            guardTask.run();
            this.m_guardTimer = new Timer(guardTask);
            this.m_guardTimer.schedule((double) this.m_interval, true);
        } else if (z || this.m_filePath != null) {
            this.m_filePath = null;
            this.m_logFileStream = null;
            this.m_interval = 0;
            Log.i(Global.NIMBLE_ID, "LOG: Disable log to file since no filename provided");
        }
    }

    private void flushCache() {
        Iterator<LogRecord> iterator = this.m_cache.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.m_cache = null;
                return;
            }
            LogRecord logRecord = iterator.next();
            this.writeLine(logRecord.level, logRecord.message);
        }
    }

    private String[] formatLine(String string2, String stringArray) {
        int n2;
        stringArray = string2 + ">" + (String)stringArray;
        int n3 = n2 = stringArray.length();
        string2 = stringArray;
        if (n2 > this.m_messageLengthLimit) {
            n3 = n2;
            string2 = stringArray;
            if (this.m_messageLengthLimit != 0) {
                n3 = n2 - this.m_messageLengthLimit;
                string2 = stringArray.substring(0, this.m_messageLengthLimit) + String.format("... and %d chars more", n3);
            }
        }
        String[] strings = new String[(int) Math.ceil((double) n3 / 4000.0)];
        n2 = 0;
        while (n2 < n3) {
            strings[n2 / 4000] = n2 + 4000 < n3 ? string2.substring(n2, n2 + 4000) : string2.substring(n2);
            n2 += 4000;
        }
        return strings;
    }

    private void outputMessageToFile(String string2) {
        String string3 = System.getProperty("line.separator");
        if (this.m_logFileStream == null) return;
        try {
            string2 = this.m_format.format(new Date()) + " " + string2 + string3;
            this.m_logFileStream.write(string2.getBytes());
            this.m_logFileStream.flush();
            return;
        }
        catch (IOException iOException) {
            Log.e((String)"Nimble", (String)("Error writing to log file: " + iOException.toString()));
            return;
        }
    }

    private int parseLevel(String string2) {
        block9: {
            try {
                int n2;
                if (Utility.validString(string2) && (n2 = Integer.parseInt(string2)) != 0) {
                    return n2;
                }
            }
            catch (NumberFormatException numberFormatException) {
                if (string2.equalsIgnoreCase("verbose")) {
                    return 100;
                }
                if (string2.equalsIgnoreCase("debug")) {
                    return 200;
                }
                if (string2.equalsIgnoreCase("info")) {
                    return 300;
                }
                if (string2.equalsIgnoreCase("warn")) {
                    return 400;
                }
                if (string2.equalsIgnoreCase("error")) {
                    return 500;
                }
                if (string2.equalsIgnoreCase("fatal")) {
                    return 600;
                }
                if (!string2.equalsIgnoreCase("silent")) break block9;
                return 700;
            }
        }
        if (this.m_core.getConfiguration() == NimbleConfiguration.INTEGRATION) return 100;
        if (this.m_core.getConfiguration() != NimbleConfiguration.STAGE) return 500;
        return 100;
    }

    private void write(int n2, String string2, String object) {
        string2 = Utility.validString(string2) ? string2 + "> " + object : " " + object;
        if (this.m_cache != null) {
            LogRecord logRecord = new LogRecord();//object
            logRecord.level = n2;
            logRecord.message = string2;
            this.m_cache.add(logRecord);
            return;
        }
        this.writeLine(n2, string2);
    }

    private void writeLine(int n2, String string2) {
        block15: {
            String string3;
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            switch (n2) {
                default: {
                    int n7;
                    String[] stringArray = this.formatLine(String.format("NIM(%d)", n2), string2);
                    string3 = stringArray[0];
                    for (n7 = 0; n7 < stringArray.length - 1; ++n7) {
                        Log.e((String)"Nimble", (String)string3);
                        this.outputMessageToFile(string3);
                        string3 = stringArray[n7 + 1];
                    }
                    break;
                }
                case 100: {
                    for (String string4 : this.formatLine("NIM_VERBOSE", string2)) {
                        Log.v((String)"Nimble", (String)string4);
                        this.outputMessageToFile(string4);
                    }
                    break block15;
                }
                case 200: {
                    int n7;
                    String[] stringArray = this.formatLine("NIM_DEBUG", string2);
                    n4 = stringArray.length;
                    for (n7 = n3; n7 < n4; ++n7) {
                        String string5 = stringArray[n7];
                        Log.d((String)"Nimble", (String)string5);
                        this.outputMessageToFile(string5);
                    }
                    break block15;
                }
                case 300: {
                    int n7;
                    String[] stringArray = this.formatLine("NIM_INFO", string2);
                    n3 = stringArray.length;
                    for (n7 = n4; n7 < n3; ++n7) {
                        String string6 = stringArray[n7];
                        Log.i((String)"Nimble", (String)string6);
                        this.outputMessageToFile(string6);
                    }
                    break block15;
                }
                case 400: {
                    int n7;
                    String[] stringArray = this.formatLine("NIM_WARN", string2);
                    n3 = stringArray.length;
                    for (n7 = n5; n7 < n3; ++n7) {
                        String string7 = stringArray[n7];
                        Log.w((String)"Nimble", (String)string7);
                        this.outputMessageToFile(string7);
                    }
                    break block15;
                }
                case 500: {
                    int n7;
                    String[] stringArray = this.formatLine("NIM_ERROR", string2);
                    n3 = stringArray.length;
                    for (n7 = n6; n7 < n3; ++n7) {
                        String string8 = stringArray[n7];
                        Log.e((String)"Nimble", (String)string8);
                        this.outputMessageToFile(string8);
                    }
                    break block15;
                }
                case 600: {
                    int n7;
                    String[] stringArray = this.formatLine("NIM_FATAL", string2);
                    String string9 = stringArray[0];
                    for (n7 = 0; n7 < stringArray.length - 1; ++n7) {
                        Log.e((String)"Nimble", (String)string9);
                        this.outputMessageToFile(string9);
                        string9 = stringArray[n7 + 1];
                    }
                    Log.wtf((String)"Nimble", (String)string9);
                    this.outputMessageToFile(string9);
                    break block15;
                }
            }
            Log.wtf((String)"Nimble", (String)string3);
            this.outputMessageToFile(string3);
        }
        if (n2 < 600) return;
        if (this.m_core.getConfiguration() == NimbleConfiguration.INTEGRATION) throw new AssertionError((Object)string2);
        if (this.m_core.getConfiguration() != NimbleConfiguration.STAGE) return;
        throw new AssertionError((Object)string2);
    }

    protected void connectToCore(BaseCore baseCore) {
        this.m_core = baseCore;
        this.configure();
        this.flushCache();
    }

    protected void disconnectFromCore() {
        this.m_core = null;
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.NimbleLog";
    }

    @Override
    public String getLogFilePath() {
        return this.m_filePath.toString();
    }

    @Override
    public int getThresholdLevel() {
        return this.m_level;
    }

    @Override
    public void resume() {
        if (this.m_guardTimer == null) return;
        this.m_guardTimer.fire();
        this.m_guardTimer.resume();
    }

    @Override
    public void setThresholdLevel(int n2) {
        this.m_level = n2;
    }

    @Override
    public void setup() {
        this.configure();
    }

    @Override
    public void suspend() {
        if (this.m_guardTimer == null) return;
        this.m_guardTimer.pause();
    }

    @Override // com.ea.nimble.Component
    public void teardown() {
        if (this.m_guardTimer != null) {
            this.m_guardTimer.cancel();
            this.m_guardTimer = null;
        }
        if (this.m_logFileStream != null) {
            try {
                this.m_logFileStream.close();
            } catch (IOException e) {
                Log.e(Global.NIMBLE_ID, "LOG: Can't close log file");
            }
            this.m_logFileStream = null;
        }
    }

    @Override
    public void writeWithSource(int n2, Object object, String string2, Object ... objectArray) {
        if (n2 < this.m_level) return;
        if (!Utility.validString(string2)) {
            return;
        }
        if (objectArray.length > 0) {
            string2 = String.format(string2, objectArray);
        }
        if (object instanceof LogSource) {
            this.write(n2, ((LogSource)object).getLogSourceTitle(), string2);
            return;
        }
        if (this.m_level <= 100 && object != null) {
            this.write(n2, object.getClass().getName(), string2);
            return;
        }
        this.write(n2, null, string2);
    }

    @Override
    public void writeWithTitle(int n2, String string2, String string3, Object ... objectArray) {
        if (n2 < this.m_level) return;
        if (!Utility.validString(string3)) {
            return;
        }
        if (objectArray.length > 0) {
            string3 = String.format(string3, objectArray);
        }
        this.write(n2, string2, string3);
    }

    private class GuardTask
    implements Runnable {
        private GuardTask() {
        }

        @Override
        public void run() {
            if (LogImpl.this.m_filePath == null) return;
            if (LogImpl.this.m_filePath.length() <= (long)(LogImpl.this.m_sizeLimit * 1024)) return;
            LogImpl.this.clearLog();
        }
    }

    private class LogRecord {
        public int level;
        public String message;

        private LogRecord() {
        }
    }
}

