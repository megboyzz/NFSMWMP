/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.googleplay;

import com.ea.nimble.Error;

class GooglePlayError
extends Error {
    public static final String ERROR_DOMAIN = "GooglePlayError";
    private static final long serialVersionUID = 1L;

    public GooglePlayError() {
    }

    public GooglePlayError(Code code, String string2) {
        super(ERROR_DOMAIN, code.intValue(), string2, null);
    }

    public GooglePlayError(Code code, String string2, Throwable throwable) {
        super(ERROR_DOMAIN, code.intValue(), string2, throwable);
    }

    public static enum Code {
        BILLING_RESPONSE_RESULT_USER_CANCELED(1),
        BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE(3),
        BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE(4),
        BILLING_RESPONSE_RESULT_DEVELOPER_ERROR(5),
        BILLING_RESPONSE_RESULT_ERROR(6),
        BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED(7),
        BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED(8),
        IABHELPER_ERROR_BASE(-1000),
        IABHELPER_REMOTE_EXCEPTION(-1001),
        IABHELPER_BAD_RESPONSE(-1002),
        IABHELPER_VERIFICATION_FAILED(-1003),
        IABHELPER_SEND_INTENT_FAILED(-1004),
        IABHELPER_USER_CANCELLED(-1005),
        IABHELPER_UNKNOWN_PURCHASE_RESPONSE(-1006),
        IABHELPER_MISSING_TOKEN(-1007),
        IABHELPER_BAD_STATE_ERROR(-1008),
        IABHELPER_UNKNOWN_ERROR(-1009),
        UNKNOWN(10003);

        private int m_value;

        private Code(int n3) {
            this.m_value = n3;
        }

        public int intValue() {
            return this.m_value;
        }
    }
}

