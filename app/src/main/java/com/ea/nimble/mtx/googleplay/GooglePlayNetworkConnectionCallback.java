/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.googleplay;

import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.mtx.googleplay.GooglePlay;

public abstract class GooglePlayNetworkConnectionCallback
implements NetworkConnectionCallback {
    String mParameter;
    GooglePlay mParentGooglePlay;
    String mTransactionId;

    public GooglePlayNetworkConnectionCallback(GooglePlay googlePlay, String string2, String string3) {
        this.mParentGooglePlay = googlePlay;
        this.mTransactionId = string2;
        this.mParameter = string3;
    }

    @Override
    public abstract void callback(NetworkConnectionHandle var1);
}

