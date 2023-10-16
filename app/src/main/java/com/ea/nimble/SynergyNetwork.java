/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.ISynergyNetwork;
import com.ea.nimble.SynergyNetworkImpl;

public class SynergyNetwork {
    public static final String COMPONENT_ID = "com.ea.nimble.synergynetwork";

    public static ISynergyNetwork getComponent() {
        return SynergyNetworkImpl.getComponent();
    }
}

