package com.eamobile.download;

public class Device {
    private int height = 0;
    private String name = "";
    private int width = 0;

    public Device(String str, int i, int i2) {
        this.name = str;
        this.width = i;
        this.height = i2;
    }

    public int getHeight() {
        return this.height;
    }

    public String getName() {
        return this.name;
    }

    public String getResolutionString() {
        return this.width + "x" + this.height;
    }

    public int getWidth() {
        return this.width;
    }
}
