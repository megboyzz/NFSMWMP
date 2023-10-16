/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx;

import com.ea.nimble.Error;

public class NimbleMTXError
extends Error {
    public static final String ERROR_DOMAIN = "NimbleMTXError";
    private static final long serialVersionUID = 1L;

    public NimbleMTXError() {
    }

    public NimbleMTXError(Code code, String string2) {
        super(ERROR_DOMAIN, code.intValue(), string2, null);
    }

    public NimbleMTXError(Code code, String string2, Throwable throwable) {
        super(ERROR_DOMAIN, code.intValue(), string2, throwable);
    }

    public static enum Code {
        BILLING_NOT_AVAILABLE(20000),
        ITEM_ALREADY_OWNED(20001),
        ITEM_NOT_OWNED(20002),
        USER_CANCELED(20003),
        VERIFICATION_ERROR(20004),
        GET_NONCE_ERROR(20005),
        NON_CRITICAL_INTERRUPTION(20006),
        INTERNAL_STATE(20007),
        TRANSACTION_PENDING(20008),
        TRANSACTION_NOT_RESUMABLE(20009),
        UNRECOGNIZED_TRANSACTION_ID(20010),
        INVALID_TRANSACTION_STATE(20011),
        UNABLE_TO_CONSTRUCT_REQUEST(20012),
        PLATFORM_ERROR(20013),
        INVALID_SERVER_RESPONSE(20014),
        ERROR_GETTING_PREPURCHASE_INFO(20015),
        ITEM_UNAVAILABLE(20016),
        INVALID_SKU(20017),
        TRANSACTION_DEFERRED(20018),
        TRANSACTION_SUPERSEDED(20019);

        private int m_value;

        private Code(int n3) {
            this.m_value = n3;
        }

        public int intValue() {
            return this.m_value;
        }
    }
}

