/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Error;

public class NimbleFacebookError
extends Error {
    public static final String NIMBLE_FACEBOOK_ERROR_DOMAIN = "NimbleFacebookError";
    private static final long serialVersionUID = 1L;

    public NimbleFacebookError(int n2, String string2) {
        super(NIMBLE_FACEBOOK_ERROR_DOMAIN, n2, string2, null);
    }

    public NimbleFacebookError(int n2, String string2, Throwable throwable) {
        super(NIMBLE_FACEBOOK_ERROR_DOMAIN, n2, string2, throwable);
    }

    public NimbleFacebookError(Code code, String string2) {
        super(NIMBLE_FACEBOOK_ERROR_DOMAIN, code.intValue(), string2, null);
    }

    public boolean isError(int n2) {
        if (this.getCode() != n2) return false;
        return true;
    }

    public static enum Code {
        FBSERVER_ERROR(90000),
        RESPONSE_PARSE_ERROR(90001);

        private int m_value;

        private Code(int n3) {
            this.m_value = n3;
        }

        public int intValue() {
            return this.m_value;
        }
    }
}

