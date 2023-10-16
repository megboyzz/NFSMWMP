/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.googleplay.util;

import com.ea.nimble.mtx.googleplay.util.IabHelper;

public class IabResult {
    String mMessage;
    int mResponse;

    public IabResult(int n2, String string2) {
        this.mResponse = n2;
        if (string2 != null && string2.trim().length() != 0) {
            this.mMessage = string2 + " (response: " + IabHelper.getResponseDesc(n2) + ")";
            return;
        }
        this.mMessage = IabHelper.getResponseDesc(n2);
    }

    public String getMessage() {
        return this.mMessage;
    }

    public int getResponse() {
        return this.mResponse;
    }

    public boolean isFailure() {
        if (this.isSuccess()) return false;
        return true;
    }

    public boolean isSuccess() {
        if (this.mResponse != 0) return false;
        return true;
    }

    public String toString() {
        return "IabResult: " + this.getMessage();
    }
}

