package com.eamobile.download;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class AssetManager {
    static final String ADC_ASSET_INFO_FILE = "AssetInfo.indicate";
    static final String DOWNLOAD_CONTROL_FILE1 = "Downloaded.indicate";
    static final String DOWNLOAD_CONTROL_FILE2 = "Downloaded2.indicate";
    static final String INDICATE_FINISHED = "COMPLETE";
    static final String INDICATE_PROGRESS = "PROGRESS";
    private String mAlternativeAssetPath;
    private String mAssetPath;
    private boolean mUseAlternativeAssetPath = false;

    private boolean belongsToADC(File file, ArrayList<File> arrayList) {
        return arrayList.contains(file);
    }

    private boolean checkFiles(ArrayList<String> arrayList) {
        Logging.DEBUG_OUT("[checkFiles]");
        for (int i = 0; i < arrayList.size(); i++) {
            String filePath = getFilePath(arrayList.get(i));
            if (!new File(filePath).exists()) {
                Logging.DEBUG_OUT("\tChecking file: " + filePath + " (NOT FOUND)");
                new File(getFilePath(DOWNLOAD_CONTROL_FILE1)).delete();
                return false;
            }
            Logging.DEBUG_OUT("\tChecking file: " + filePath + " (OK)");
        }
        Logging.DEBUG_OUT(" ");
        return true;
    }

    private void deleteEmptyDirs(String str, ArrayList<File> arrayList) {
        try {
            File file = new File(str);
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                Logging.DEBUG_OUT("Error listing files for directory: " + file.getAbsolutePath());
                return;
            }
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    deleteEmptyDirs(listFiles[i].getAbsolutePath(), arrayList);
                }
            }
            File[] listFiles2 = file.listFiles();
            String absolutePath = file.getAbsolutePath();
            if (listFiles2.length != 0) {
                Logging.DEBUG_OUT(absolutePath + " (NOT EMPTY)");
            } else if (belongsToADC(file, arrayList)) {
                if (file.delete()) {
                    Logging.DEBUG_OUT(absolutePath + " (DELETED)");
                } else {
                    Logging.DEBUG_OUT(absolutePath + " (UNKNOWN ERROR WHILE DELETING)");
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Logging.DEBUG_OUT_STACK(e);
                }
            } else {
                Logging.DEBUG_OUT(absolutePath + " (DOES NOT BELONG TO ADC)");
            }
        } catch (SecurityException e2) {
            Logging.DEBUG_OUT_STACK(e2);
        }
    }

    private boolean exists(String str, String str2) {
        String str3 = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(str2)), 8192);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                str3 = str3 + readLine + "\r\n";
            }
            Logging.DEBUG_OUT("Existing Text:" + str3 + ", new Value:" + str);
            bufferedReader.close();
        } catch (Exception e) {
        }
        return str3.contains(str);
    }

    private ArrayList<File> getDirList() {
        try {
            ArrayList<File> arrayList = new ArrayList<>();
            File file = new File(getFilePath(DOWNLOAD_CONTROL_FILE2));
            if (!file.exists()) {
                return arrayList;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file), 8192);
            TreeSet treeSet = new TreeSet();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                String[] split = readLine.split("/");
                String str = "";
                for (int i = 0; i < split.length - 1; i++) {
                    str = str + split[i] + "/";
                    treeSet.add(str);
                }
            }
            Iterator it = treeSet.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                if (new File(getFilePath(str2)).isDirectory()) {
                    arrayList.add(new File(getFilePath(str2)));
                }
            }
            Logging.DEBUG_OUT("ADC Directories read from Downloaded2.indicate: ");
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                Logging.DEBUG_OUT(arrayList.get(i2).getAbsolutePath());
            }
            bufferedReader.close();
            return arrayList;
        } catch (Exception e) {
            return null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x008a A[SYNTHETIC, Splitter:B:28:0x008a] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x009d A[SYNTHETIC, Splitter:B:34:0x009d] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getFileList(com.eamobile.download.DownloadFileData r14) {
        /*
        // Method dump skipped, instructions count: 187
        */
        throw new UnsupportedOperationException("Method not decompiled: com.eamobile.download.AssetManager.getFileList(com.eamobile.download.DownloadFileData):java.lang.String");
    }

    private String normalisedVersion(String str) {
        return normalisedVersion(str, ".", 4);
    }

    private String normalisedVersion(String str, String str2, int i) {
        String[] split = Pattern.compile(str2, 16).split(str);
        StringBuilder sb = new StringBuilder();
        for (String str3 : split) {
            sb.append(String.format("%" + i + 's', str3));
        }
        return sb.toString();
    }

    public boolean assetsFoundLocally() {
        File file = new File(getAssetPath());
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File file2 = new File(getFilePath(DOWNLOAD_CONTROL_FILE1));
            if (!file2.exists()) {
                return false;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file2), 8192);
            String str = "";
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                str = str + readLine + "\n";
            }
            bufferedReader.close();
            if (!str.contains(INDICATE_FINISHED)) {
                return false;
            }
            File file3 = new File(getFilePath(DOWNLOAD_CONTROL_FILE2));
            if (!file3.exists()) {
                return true;
            }
            BufferedReader bufferedReader2 = new BufferedReader(new FileReader(file3), 8192);
            ArrayList<String> arrayList = new ArrayList<>();
            while (true) {
                String readLine2 = bufferedReader2.readLine();
                if (readLine2 != null) {
                    arrayList.add(readLine2);
                } else {
                    bufferedReader2.close();
                    return checkFiles(arrayList);
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean checkForUpdates(DownloadFileData[] downloadFileDataArr) {
        Logging.DEBUG_OUT("Calling: AssetManager checkForUpdates()");
        boolean z = false;
        if (downloadFileDataArr != null && downloadFileDataArr.length > 0) {
            try {
                if (isVersionLower(getLocalAssetVersion(), downloadFileDataArr[0].getVersion())) {
                    z = true;
                }
            } catch (Exception e) {
                Logging.DEBUG_OUT("[ERROR] An exception occurred while checking for updates: " + e);
                Logging.DEBUG_OUT_STACK(e);
                return false;
            }
        }
        return z;
    }

    public void clearDownloadDir() {
        String[] list;
        File file = new File(getAssetPath());
        if (file.isDirectory()) {
            for (String str : file.list()) {
                new File(file, str).delete();
            }
        }
    }

    public void deleteAssets() {
        try {
            File file = new File(getFilePath(DOWNLOAD_CONTROL_FILE2));
            if (file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file), 8192);
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        String filePath = getFilePath(readLine);
                        File file2 = new File(filePath);
                        if (!file2.exists()) {
                            Logging.DEBUG_OUT("\tDeleting file: " + filePath + " (NOT FOUND)");
                        } else if (file2.delete()) {
                            Logging.DEBUG_OUT("\tDeleting file: " + filePath + " (OK)");
                        } else {
                            Logging.DEBUG_OUT("\tDeleting file: " + filePath + " (FAILED)");
                        }
                    } else {
                        bufferedReader.close();
                        deleteEmptyDirs(getAssetPath(), getDirList());
                        return;
                    }
                }
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("Exception while deleting assets:");
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    public void deleteEntireDownloadFolder() {
        deleteEntireDownloadFolderAux(new File(getAssetPath()));
    }

    public void deleteEntireDownloadFolderAux(File file) {
        try {
            File[] listFiles = file.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    deleteEntireDownloadFolderAux(listFiles[i]);
                }
                if (!listFiles[i].exists()) {
                    Logging.DEBUG_OUT("\tDeleting file: " + listFiles[i].getAbsolutePath() + " (NOT FOUND)");
                } else if (listFiles[i].delete()) {
                    Logging.DEBUG_OUT("\tDeleting file: " + listFiles[i].getAbsolutePath() + " (OK)");
                } else {
                    Logging.DEBUG_OUT("\tDeleting file: " + listFiles[i].getAbsolutePath() + " (FAILED)");
                }
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("Exception while deleting download folder:");
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    public String getAssetPath() {
        return !this.mUseAlternativeAssetPath ? this.mAssetPath : this.mAlternativeAssetPath;
    }

    public String getDownloadList(DownloadFileData[] downloadFileDataArr) {
        String str = "";
        for (int i = 0; i < downloadFileDataArr.length; i++) {
            if (downloadFileDataArr[i].getType() == 2) {
                str = str + getFileList(downloadFileDataArr[i]);
            }
        }
        return str;
    }

    public String getFilePath(String str) {
        return !this.mUseAlternativeAssetPath ? this.mAssetPath + "/" + str : this.mAlternativeAssetPath + "/" + str;
    }

    public long getFileSize(String str) {
        File file = new File(getFilePath(str));
        if (file.exists()) {
            Logging.DEBUG_OUT("File: " + getFilePath(str) + "   Size: " + file.length());
            return file.length();
        }
        Logging.DEBUG_OUT("File: " + getFilePath(str) + "   Size: 0");
        return 0;
    }

    public String getLocalAssetVersion() {
        Logging.DEBUG_OUT("Calling: AssetManager getLocalAssetVersion()");
        try {
            File file = new File(getFilePath(DOWNLOAD_CONTROL_FILE1));
            if (!file.exists()) {
                return null;
            }
            Hashtable hashtable = new Hashtable();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file), 8192);
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                } else if (!readLine.contains(INDICATE_FINISHED) && !readLine.trim().equals("")) {
                    String[] split = readLine.split("\t");
                    hashtable.put(split[0], split[1]);
                }
            }
            bufferedReader.close();
            Enumeration keys = hashtable.keys();
            String str = "";
            while (keys.hasMoreElements()) {
                str = (String) hashtable.get((String) keys.nextElement());
            }
            return str;
        } catch (Exception e) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred while getting local asset version: " + e);
            Logging.DEBUG_OUT_STACK(e);
            return null;
        }
    }

    public long getTotalSize(String str) {
        String[] split;
        long j = 0;
        for (String str2 : str.split("\n")) {
            j += getFileSize(str2);
        }
        return j;
    }

    public boolean isAssetVersionCompatible(String str) {
        String localAssetVersion = getLocalAssetVersion();
        Logging.DEBUG_OUT("Checking asset version:");
        Logging.DEBUG_OUT("\tLocal asset version: " + localAssetVersion);
        Logging.DEBUG_OUT("\tMinimum asset version: " + str);
        if (isVersionLower(localAssetVersion, str)) {
            Logging.DEBUG_OUT("FAILED");
            return false;
        }
        Logging.DEBUG_OUT("OK");
        return true;
    }

    public boolean isFileDownloaded(String str) {
        File file = new File(getAssetPath());
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File file2 = new File(getFilePath(DOWNLOAD_CONTROL_FILE1));
            if (!file2.exists()) {
                return false;
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file2), 8192);
            String str2 = "";
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                str2 = str2 + readLine + "\n";
            }
            bufferedReader.close();
            return str2.contains(str);
        } catch (Exception e) {
            Logging.DEBUG_OUT(str + " is not downloaded.");
            return false;
        }
    }

    public boolean isVersionLower(String str, String str2) {
        int compareTo = normalisedVersion(str2).compareTo(normalisedVersion(str));
        return compareTo >= 0 && compareTo > 0;
    }

    public Properties loadAssetInfo() {
        try {
            File file = new File(getFilePath(ADC_ASSET_INFO_FILE));
            if (!file.exists()) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInputStream);
            return properties;
        } catch (Exception e) {
            Logging.DEBUG_OUT("\tException while loading properties from: AssetInfo.indicate");
            Logging.DEBUG_OUT_STACK(e);
            return null;
        }
    }

    public void prepareSDCard() {
        try {
            File file = new File(getFilePath(DOWNLOAD_CONTROL_FILE1));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("Exception while cleaning SDCard:");
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    public void saveAssetInfo(Properties properties) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getFilePath(ADC_ASSET_INFO_FILE));
            properties.store(fileOutputStream, "");
            fileOutputStream.close();
        } catch (Exception e) {
            Logging.DEBUG_OUT("\tException while saving properties to: AssetInfo.indicate");
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    public void saveDownloadListFile(DownloadFileData[] downloadFileDataArr) {
        String downloadList = getDownloadList(downloadFileDataArr);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(getFilePath(DOWNLOAD_CONTROL_FILE2));
            fileOutputStream.write(downloadList.getBytes());
            fileOutputStream.close();
        } catch (Exception e) {
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    public void saveState(String str, String str2) {
        try {
            String filePath = getFilePath(DOWNLOAD_CONTROL_FILE1);
            File file = new File(filePath);
            if (file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file), 8192);
                String str3 = "";
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    str3 = str3 + readLine + "\n";
                }
                bufferedReader.close();
                String replaceAll = str2 != null ? str3.replaceAll(str, str2) : !exists(str, filePath) ? str3 + str : str;
                if (!exists(replaceAll, filePath)) {
                    File file2 = new File(filePath);
                    if (file2.exists()) {
                        file2.delete();
                    }
                    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true), 8192);
                    bufferedWriter.write(replaceAll + "\n");
                    bufferedWriter.close();
                    return;
                }
                return;
            }
            try {
                new File(getAssetPath()).mkdirs();
            } catch (SecurityException e) {
                Logging.DEBUG_OUT("saveState Security Exception:" + e);
            }
            FileWriter fileWriter = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter, 8192);
            bufferedWriter2.write(str + "\n");
            bufferedWriter2.close();
            fileWriter.close();
        } catch (Exception e2) {
            Logging.DEBUG_OUT("Exception while saving state to SDCard: " + e2);
        }
    }

    public void saveStateDownloadFinished() {
        saveState(INDICATE_PROGRESS, INDICATE_FINISHED);
    }

    public void saveStateDownloadStarted() {
        saveState(INDICATE_PROGRESS, null);
    }

    public void setAlternativeAssetPath(String str) {
        this.mAlternativeAssetPath = str;
    }

    public void setAssetPath(String str) {
        this.mAssetPath = str;
    }

    public void useAlternativeAssetPath(boolean z) {
        if (z) {
            Logging.DEBUG_OUT("AssetManager: using alternative location");
        } else {
            Logging.DEBUG_OUT("AssetManager: using main location");
        }
        this.mUseAlternativeAssetPath = z;
    }
}
