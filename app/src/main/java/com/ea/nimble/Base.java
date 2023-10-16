/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.BaseCore;
import com.ea.nimble.Component;
import com.ea.nimble.NimbleConfiguration;

public class Base {
    public static Component getComponent(String string2) {
        return BaseCore.getInstance().activeValidate().getComponentManager().getComponent(string2);
    }

    public static Component[] getComponentList(String string2) {
        return BaseCore.getInstance().activeValidate().getComponentManager().getComponentList(string2);
    }

    public static NimbleConfiguration getConfiguration() {
        return BaseCore.getInstance().getConfiguration();
    }

    public static void registerComponent(Component component, String string2) {
        BaseCore.getInstance().getComponentManager().registerComponent(component, string2);
    }

    public static void restartWithConfiguration(NimbleConfiguration nimbleConfiguration) {
        BaseCore.getInstance().activeValidate().restartWithConfiguration(nimbleConfiguration);
    }

    public static void setupNimble() {
        BaseCore.getInstance().setup();
    }

    public static void teardownNimble() {
        BaseCore.getInstance().teardown();
    }
}

