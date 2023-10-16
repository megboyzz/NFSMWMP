package com.eamobile;

import androidx.core.view.MotionEventCompat;

import com.eamobile.download.Logging;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class Language {
    public static final int ASCII = 1;
    public static final int BTN_3G = 23;
    public static final int BTN_ALWAYS = 48;
    public static final int BTN_CANCEL = 49;
    public static final int BTN_DOWNLOAD = 5;
    public static final int BTN_EXIT = 6;
    public static final int BTN_NO = 18;
    public static final int BTN_OK = 2;
    public static final int BTN_RETRY = 14;
    public static final int BTN_RETRY_TITLE = 13;
    public static final int BTN_THIS_TIME_ONLY = 47;
    public static final int BTN_WIFI = 16;
    public static final int BTN_YES = 17;
    public static final int CHECK_UPDATES_MSG = 22;
    public static final int CHECK_UPDATES_TITLE = 21;
    public static final int CONTACTING_SERVER_MSG = 38;
    public static final int CONTACTING_SERVER_TITLE = 37;
    public static final int DEBUG_DOWNLOAD_TITLE = 52;
    public static final int DELETING_OLD_CONTENT = 45;
    public static final int DOWNLOADING_MSG = 20;
    public static final int DOWNLOADING_TITLE = 19;
    public static final int DOWNLOAD_EXIT_CONFIRMATION = 41;
    public static final int DOWNLOAD_MSG = 1;
    public static final int DOWNLOAD_MSG_NEW = 50;
    public static final int DOWNLOAD_PROGRESS = 15;
    public static final int DOWNLOAD_SPEED_TXT = 36;
    public static final int DOWNLOAD_TITLE = 0;
    public static final char END_OF_LINE = '\n';
    public static final int FAILED_MSG = 12;
    public static final int FAILED_TITLE = 11;
    public static final int INVALID_CONTENT_TITLE = 42;
    public static final int INVALID_CONTENT_TXT = 43;
    public static final int MANDATORY_UPDATE_WARNING = 44;
    private static final int NB_STRINGS = 53;
    public static final int NETWORK_3G_CONNECT_TITLE = 39;
    public static final int NETWORK_WARNING_TXT = 24;
    public static final int NETWORK_WIFI_DISABLED = 40;
    public static final int NW_UNAVAIL = 9;
    public static final int NW_UNAVAIL_MSG = 10;
    public static final int PRESS_BACK_FOR_3G = 30;
    public static final int PRESS_BACK_FOR_WIFI = 29;
    public static final int PRESS_BTN_3G_FOR_3G = 31;
    public static final int SERVER_ERROR_TEXT = 34;
    public static final int SERVER_ERROR_TITLE = 33;
    public static final int SIGNAL_STRENGTH_TXT = 35;
    public static final int SPACE_UNAVAIL_MSG = 4;
    public static final int SPACE_UNAVAIL_MSG_NEW = 51;
    public static final int SPACE_UNAVAIL_TITLE = 3;
    public static final int UNICODE = 2;
    public static final int UNSUPPORTED_DEVICE_TITLE = 27;
    public static final int UNSUPPORTED_DEVICE_TXT = 28;
    public static final int UPDATES_FOUND_TITLE = 25;
    public static final int UPDATES_FOUND_TXT = 26;
    public static final int WIFI_LOST = 46;
    public static final int WIFI_MSG = 8;
    public static final int WIFI_MSG_ENABLE_MANUALLY = 32;
    public static final int WIFI_TITLE = 7;
    private static int fileType;
    public static final String[] strings = new String[NB_STRINGS];
    public final int BUFFER_SIZE = 8096;
    private String curLanguage;

    public static int determineFileType(String str) {
        int i = 1;
        DataInputStream dataInputStream = null;
        try {
            DataInputStream dataInputStream2 = new DataInputStream(DownloadActivityInternal.getInstance().getAssets().open(str));
            try {
                if (dataInputStream2.readUnsignedShort() == 65534) {
                    i = 2;
                }
                try {
                    dataInputStream2.close();
                } catch (Exception ignored) {
                }
            } catch (IOException e2) {
                dataInputStream = dataInputStream2;
                try {
                    dataInputStream.close();
                } catch (Exception ignored) {
                }
                return i;
            } catch (Throwable th2) {
                dataInputStream = dataInputStream2;
                try {
                    dataInputStream.close();
                } catch (Exception e4) {
                }
            }
        } catch (IOException e5) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return i;
        }
        return i;
    }

    public static String getString(int i) {
        return getString(i, null);
    }

    public static String getString(int i, String[] strArr) {
        int i2 = 0;
        String str = strings[i];
        if (strArr != null) {
            try {
                if (strArr.length > 0) {
                    while (str.indexOf("%%") != -1 && i2 < strArr.length) {
                        int indexOf = str.indexOf("%%");
                        str = str.substring(0, indexOf) + strArr[i2] + str.substring(indexOf + 2);
                        i2++;
                    }
                }
            } catch (Exception e) {
                Logging.DEBUG_OUT("Exception e:" + e);
            }
        }
        return str;
    }

    public static char readChar(DataInput dataInput) throws IOException {
        if (fileType != 2) {
            return (char) dataInput.readUnsignedByte();
        }
        int readUnsignedShort = dataInput.readUnsignedShort();
        return (char) ((readUnsignedShort >> 8) | ((readUnsignedShort & MotionEventCompat.ACTION_MASK) << 8));
    }

    public static String readTo(DataInput dataInput, char c, boolean z) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        char readChar = readChar(dataInput);
        while (readChar != c) {
            stringBuffer.append(readChar);
            readChar = readChar(dataInput);
        }
        String stringBuffer2 = stringBuffer.toString();
        if (stringBuffer2 != null) {
            return stringBuffer2.trim();
        }
        return null;
    }

    public String getCurrentLanguage() {
        return this.curLanguage;
    }


    public boolean loadStrings(String param) {

        return false;
/*
        String path;
        String pSVar2;
        Activity ref;
        AssetManager ref_00;
        InputStream fileStream = null;
        int iVar4;
        DataInput ref_01;
        Vector ref_02;
        StringBuilder pSVar5;

        Logging.DEBUG_OUT("\tloadString(" + param + ")");

        if (param != null) {

            path = DownloadActivityInternal.getResourcesPath() + param + ".txt";
            Logging.DEBUG_OUT("\tOpening file: " + path);

            ref = DownloadActivityInternal.getInstance();
            ref_00 = ref.getAssets();

            try {
                fileStream = ref_00.open(path);
            } catch (IOException e) {
                Log.e("Lang", e.toString());
            }

            if (fileStream != null) {
                ref_02 = new Vector();

                ref_01 = new DataInput(fileStream);
                iVar4 = Language.determineFileType(path);
                Language.fileType = iVar4;
                if (Language.fileType == 2) {
                    ref_01.skipBytes(2);
                }
                do {
                    path = Language.readTo(ref_01,'\n',true);
                    ref_02.addElement(path);
                } while( true );
            }
            Logging.DEBUG_OUT("\tCouldn't find file: " + path);
        }
        return false;
*/
    }
}
