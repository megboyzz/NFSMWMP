/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.tracking;

import com.ea.nimble.Base;
import com.ea.nimble.tracking.NimbleTrackingSynergyImpl;
import com.ea.nimble.tracking.NimbleTrackingThreadProxy;

class NimbleTrackingSynergyComponent
extends NimbleTrackingThreadProxy {
    static final String COMPONENT_ID = "com.ea.nimble.trackingimpl.synergy";

    private NimbleTrackingSynergyComponent() {
        super(new NimbleTrackingSynergyImpl());
    }

    static void initialize() {
        Base.registerComponent(new NimbleTrackingSynergyComponent(), COMPONENT_ID);
    }
}

