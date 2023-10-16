/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.googleplay.util;

import com.ea.nimble.mtx.googleplay.util.IabResult;

public class IabException
extends Exception {
    private static final long serialVersionUID = 5626567164828265535L;
    IabResult mResult;

    public IabException(int n2, String string2) {
        this(new IabResult(n2, string2));
    }

    public IabException(int n2, String string2, Exception exception) {
        this(new IabResult(n2, string2), exception);
    }

    public IabException(IabResult iabResult) {
        this(iabResult, null);
    }

    public IabException(IabResult iabResult, Exception exception) {
        super(iabResult.getMessage(), exception);
        this.mResult = iabResult;
    }

    public IabResult getResult() {
        return this.mResult;
    }
}

