package com.eamobile.download;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class ChecksumValidator {
    public static boolean validate(String str, String str2, long j) {
        String str3 = str;
        if (str3.contains(str2)) {
            str3 = str3.substring(str3.lastIndexOf(str2) + (str2.charAt(str2.length() + -1) == '/' ? str2.length() : str2.length() + 1));
        }
        Logging.DEBUG_OUT("Validating checksum for " + str3);
        try {
            CheckedInputStream checkedInputStream = new CheckedInputStream(new FileInputStream(str), new CRC32());
            try {
                do {
                } while (checkedInputStream.read(new byte[8192]) != -1);
                long value = checkedInputStream.getChecksum().getValue();
                checkedInputStream.close();
                boolean z = value == j;
                if (z) {
                    Logging.DEBUG_OUT("Checksums match: " + j);
                    Logging.DEBUG_OUT("File " + str3 + " downloaded successfully");
                    return z;
                }
                Logging.DEBUG_OUT("[ERROR] Checksums do not match FileChecksum:" + value + ", Server Checksums:" + j);
                Logging.DEBUG_OUT("File " + str3 + " failed to download");
                return z;
            } catch (IOException e) {
                return false;
            }
        } catch (IOException e2) {
            return false;
        }
    }
}
