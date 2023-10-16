/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx;

import com.ea.nimble.Base;
import com.ea.nimble.Component;
import com.ea.nimble.Log;
import com.ea.nimble.mtx.INimbleMTX;

public class NimbleMTX {
    public static final String COMPONENT_ID = "com.ea.nimble.mtx";

    public static INimbleMTX getComponent() {
        Component[] componentArray = Base.getComponentList(COMPONENT_ID);
        if (componentArray == null) return null;
        if (componentArray.length <= 0) return null;
        if (componentArray.length == 1) return (INimbleMTX)((Object)componentArray[0]);
        Log.Helper.LOGFS("MTX", "More than one MTX component registered!", new Object[0]);
        return (INimbleMTX)((Object)componentArray[0]);
    }
}

