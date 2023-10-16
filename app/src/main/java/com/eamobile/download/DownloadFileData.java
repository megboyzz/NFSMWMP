package com.eamobile.download;

public class DownloadFileData {
    public static final int FILE_TYPE_CHECKSUMS = 2;
    public static final int FILE_TYPE_OTHER = 3;
    public static final int FILE_TYPE_ZIP = 1;
    private String fileName;
    private String fileURL;
    private String language;
    private int size;
    private int type;
    private String version;

    public DownloadFileData(String str, int i, String str2, String str3, String str4, int i2) {
        this.fileName = str;
        this.size = i;
        this.version = str2;
        this.language = str3;
        this.fileURL = str4;
        this.type = i2;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFileURL() {
        return this.fileURL;
    }

    public String getLanguage() {
        return this.language;
    }

    public int getSize() {
        return this.size;
    }

    public int getType() {
        return this.type;
    }

    public String getVersion() {
        return this.version;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void setFileURL(String str) {
        this.fileURL = str;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public void setSize(int i) {
        this.size = i;
    }

    public void setType(int i) {
        this.type = i;
    }

    public void setVersion(String str) {
        this.version = str;
    }

    public String toString() {
        return this.fileName + " " + this.size + " " + this.version + " " + this.language + " " + this.fileURL + " " + this.type;
    }
}
