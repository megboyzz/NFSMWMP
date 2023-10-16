/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Error;

public class SynergyIdManagerError
extends Error {
    public static final String ERROR_DOMAIN = "NimbleSynergyIdManager";
    private static final long serialVersionUID = 1L;

    public SynergyIdManagerError(int n2, String string2) {
        super(ERROR_DOMAIN, n2, string2, null);
    }

    public SynergyIdManagerError(int n2, String string2, Throwable throwable) {
        super(ERROR_DOMAIN, n2, string2, throwable);
    }

    public static enum Code {
        AUTHENTICATOR_CONFLICT(5000),
        UNEXPECTED_LOGIN_STATE(5001),
        INVALID_ID(5002),
        MISSING_AUTHENTICATOR(5003);

        private int m_value;

        private Code(int n3) {
            this.m_value = n3;
        }

        public int intValue() {
            return this.m_value;
        }
    }
}

