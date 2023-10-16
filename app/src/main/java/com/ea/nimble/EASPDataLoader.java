package com.ea.nimble;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: stdlib.jar:com/ea/nimble/EASPDataLoader.class */
public class EASPDataLoader {

    /* loaded from: stdlib.jar:com/ea/nimble/EASPDataLoader$EASPDataBuffer.class */
    public static class EASPDataBuffer {
        public ByteBuffer m_decryptedByteBuffer;
        public String m_version;

        public EASPDataBuffer(String str, ByteBuffer byteBuffer) {
            this.m_version = str;
            this.m_decryptedByteBuffer = byteBuffer;
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/EASPDataLoader$LogEvent.class */
    public static class LogEvent {
        public int m_EAUID;
        public long m_dateTimeInNanoseconds;
        public int m_indexInsideSession;
        public int m_keyType01;
        public int m_keyType02;
        public int m_keyType03;
        public String m_randomPart;
        public long m_timestamp;
        public int m_type;
        public int m_userLevel;
        public String m_value01;
        public String m_value02;
        public String m_value03;
    }

    public static boolean deleteDatFile(String str) {
        File file = new File(str);
        if (!file.exists()) {
            return true;
        }
        return file.delete();
    }

    public static String getTrackingDatFilePath() {
        Context applicationContext = ApplicationEnvironment.getComponent().getApplicationContext();
        return (applicationContext == null ? System.getProperty("user.dir") + File.separator + "doc" : applicationContext.getFilesDir().getPath()) + File.separator + "EASP" + File.separator + "Tracking" + File.separator + "tracking.dat";
    }

    public static EASPDataBuffer loadDatFile(String str) throws Exception {
        Throwable th;
        Exception e;
        if (str == null || str.length() == 0) {
            Log.Helper.LOGDS("Legacy", "Empty path passed to loadLegacyEASPDatFile");
            throw new NullPointerException();
        }
        File file = new File(str);
        if (!file.exists()) {
            Log.Helper.LOGDS("Legacy", "Couldn't find EASP data file, " + str);
            throw new FileNotFoundException("Non-existent or empty file, " + str + ".");
        }
        Log.Helper.LOGDS("Legacy", "Attempt to read EASP data file, %s, size %d.", str, file.length());
        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            bufferedInputStream = new BufferedInputStream(fileInputStream);
            byte[] bArr = new byte[(int) file.length()];
            int read = bufferedInputStream.read(bArr);
            Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
            instance.init(2, new SecretKeySpec(new byte[]{-25, -17, -122, 91, 109, -87, 10, 61, 57, 50, 14, -5, -108, 24, -28, -25, -58, 20, 24, Byte.MAX_VALUE, 59, -107, -123, -38, 101, 43, -82, 117, 27, -62, -102, 55}, "AES"), new IvParameterSpec(bArr, 8, 16));
            ByteBuffer wrap = ByteBuffer.wrap(instance.doFinal(bArr, 24, read - 24));
            wrap.order(ByteOrder.LITTLE_ENDIAN);
            EASPDataBuffer eASPDataBuffer = new EASPDataBuffer(readString(wrap), wrap.slice().order(ByteOrder.LITTLE_ENDIAN));
            try {
                fileInputStream.close();
                bufferedInputStream.close();
            } catch (IOException e2) {
                Log.Helper.LOGES("Legacy", "Exception closing file stream, for file, %s.", str);
            }
            return eASPDataBuffer;

        } catch (Exception e4) {
            Log.Helper.LOGES("Legacy", "Exception reading EASP data file, %s: %s", str, e4.toString());
            e4.printStackTrace();
        }

        return null;

    }

    public static String loadEADeviceId() {
        try {
            EASPDataBuffer loadDatFile = loadDatFile(ApplicationEnvironment.getComponent().getApplicationContext().getFilesDir().getPath() + "/EASP/commoninfo.dat");
            if (!loadDatFile.m_version.equals("1.00.02")) {
                return null;
            }
            ByteBuffer byteBuffer = loadDatFile.m_decryptedByteBuffer;
            readString(byteBuffer);
            readBooleanByte(byteBuffer);
            return readString(byteBuffer);
        } catch (Exception e) {
            Log.Helper.LOGES("Legacy", "Exception when trying to load EASP data: %s", e);
            return null;
        }
    }

    public static boolean readBooleanByte(ByteBuffer byteBuffer) {
        return byteBuffer.get() != 0;
    }

    public static LogEvent readLogEvent(ByteBuffer byteBuffer) throws IOException {
        LogEvent logEvent = new LogEvent();
        try {
            if (!readBooleanByte(byteBuffer)) {
                return null;
            }
            logEvent.m_type = byteBuffer.getInt();
            logEvent.m_indexInsideSession = byteBuffer.getInt();
            logEvent.m_dateTimeInNanoseconds = byteBuffer.getLong();
            logEvent.m_EAUID = byteBuffer.getInt();
            logEvent.m_randomPart = readString(byteBuffer);
            logEvent.m_keyType01 = byteBuffer.getInt();
            logEvent.m_value01 = readString(byteBuffer);
            logEvent.m_keyType02 = byteBuffer.getInt();
            logEvent.m_value02 = readString(byteBuffer);
            logEvent.m_timestamp = byteBuffer.getLong();
            logEvent.m_keyType03 = byteBuffer.getInt();
            logEvent.m_value03 = readString(byteBuffer);
            logEvent.m_userLevel = byteBuffer.getInt();
            return logEvent;
        } catch (IOException e) {
            Log.Helper.LOGES("Legacy", "Exception reading LogEvent: " + e, new Object[0]);
            throw e;
        }
    }

    public static String readString(ByteBuffer byteBuffer) throws IOException {
        Exception e;
        int i = byteBuffer.getInt();
        if (i <= 0) {
            return null;
        }
        if (i > byteBuffer.remaining()) {
            Log.Helper.LOGES("Legacy", "String length greater than buffer remaining bytes.", new Object[0]);
            throw new IOException("String length uint32 corrupt, longer than remaining bytes.");
        }
        byte[] bArr = new byte[i];
        byteBuffer.get(bArr, 0, i);
        String str = null;
        try {
            String str2 = new String(bArr, "UTF-8");
            try {
                Log.Helper.LOGDS("Legacy", "Read string (%s)", str2);
                return str2;
            } catch (Exception e2) {
                e = e2;
                str = str2;
                Log.Helper.LOGES("Legacy", "Read string exception: " + e);
                return str;
            }
        } catch (Exception e3) {
            e = e3;
        }
        return "";
    }
}
