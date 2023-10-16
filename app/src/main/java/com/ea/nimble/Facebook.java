/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Base;
import com.ea.nimble.FacebookImpl;
import com.ea.nimble.IFacebook;

public class Facebook {
    public static final String COMPONENT_ID = "com.ea.nimble.facebook";

    public static IFacebook getComponent() {
        return (IFacebook)((Object)Base.getComponent(COMPONENT_ID));
    }

    private static void initialize() {
        Base.registerComponent(new FacebookImpl(), COMPONENT_ID);
    }
}

