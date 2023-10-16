/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONObject
 */
package com.ea.nimble.bridge;

import com.ea.nimble.Error;
import com.ea.nimble.bridge.BaseNativeCallback;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import org.json.JSONObject;

public class IdentityNativeCallback
implements INimbleIdentityAuthenticator.NimbleIdentityAuthenticatorCallback,
INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback,
INimbleIdentityAuthenticator.NimbleIdentityServerAuthCodeCallback {
    private int m_id;

    public IdentityNativeCallback(int n2) {
        this.m_id = n2;
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }

    @Override
    public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, Error error) {
        BaseNativeCallback.nativeCallback(this.m_id, iNimbleIdentityAuthenticator, error);
    }

    @Override
    public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String string2, String string3, String string4, Error error) {
        BaseNativeCallback.nativeCallback(this.m_id, iNimbleIdentityAuthenticator, string2, string3, string4, error);
    }

    @Override
    public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, JSONObject jSONObject, Error error) {
        BaseNativeCallback.nativeCallback(this.m_id, iNimbleIdentityAuthenticator, jSONObject, error);
    }
}

