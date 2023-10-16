/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.bridge;

import com.ea.nimble.IFacebook;
import com.ea.nimble.bridge.BaseNativeCallback;

public class FacebookNativeCallback
implements IFacebook.FacebookCallback {
    private int m_id;

    public FacebookNativeCallback(int n2) {
        this.m_id = n2;
    }

    @Override
    public void callback(IFacebook iFacebook, boolean bl2, Exception exception) {
        BaseNativeCallback.nativeCallback(this.m_id, iFacebook, bl2, exception);
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }
}

