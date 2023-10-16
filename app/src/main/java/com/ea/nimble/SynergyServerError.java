/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Error;

public class SynergyServerError
extends Error {
    public static final String ERROR_DOMAIN = "SynergyServerError";
    private static final long serialVersionUID = 1L;

    public SynergyServerError() {
    }

    public SynergyServerError(int n2, String string2) {
        super(ERROR_DOMAIN, n2, string2, null);
    }

    public SynergyServerError(int n2, String string2, Throwable throwable) {
        super(ERROR_DOMAIN, n2, string2, throwable);
    }

    public boolean isError(int n2) {
        if (this.getCode() != n2) return false;
        return true;
    }

    public static enum Code {
        ERROR_NONCE_VERIFICATION(-30013),
        ERROR_SIGNATURE_VERIFICATION(-30014),
        ERROR_NOT_SUPPORTED_RECEIPT_TYPE(-30015),
        AMAZON_SERVER_CONNECTION_ERROR(-30016),
        APPLE_SERVER_CONNECTION_ERROR(10001);

        private int m_value;

        private Code(int n3) {
            this.m_value = n3;
        }

        public int intValue() {
            return this.m_value;
        }
    }
}

