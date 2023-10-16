/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Error;

class HttpError
extends Error {
    public static final String ERROR_DOMAIN = "HttpError";
    private static final long serialVersionUID = 1L;

    public HttpError(int n2, String string2) {
        super(ERROR_DOMAIN, n2, string2, null);
    }

    public HttpError(int n2, String string2, Throwable throwable) {
        super(ERROR_DOMAIN, n2, string2, throwable);
    }
}

