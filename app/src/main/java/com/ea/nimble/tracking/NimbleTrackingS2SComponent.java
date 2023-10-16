/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.tracking;

import com.ea.nimble.Base;
import com.ea.nimble.tracking.NimbleTrackingS2SImpl;
import com.ea.nimble.tracking.NimbleTrackingThreadProxy;

class NimbleTrackingS2SComponent
extends NimbleTrackingThreadProxy {
    static final String COMPONENT_ID = "com.ea.nimble.trackingimpl.s2s";

    private NimbleTrackingS2SComponent() {
        super(new NimbleTrackingS2SImpl());
    }

    static void initialize() {
        Base.registerComponent(new NimbleTrackingS2SComponent(), COMPONENT_ID);
    }
}

