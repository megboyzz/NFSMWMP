/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

public interface ILog {
    String getLogFilePath();

    int getThresholdLevel();

    void setThresholdLevel(int var1);

    void writeWithSource(int var1, Object var2, String var3, Object ... var4);

    void writeWithTitle(int var1, String var2, String var3, Object ... var4);
}

