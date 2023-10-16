/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.SynergyIdManagerError;

public interface ISynergyIdManager {
    public String getAnonymousSynergyId();

    public String getSynergyId();

    public SynergyIdManagerError login(String var1, String var2);

    public SynergyIdManagerError logout(String var1);
}

