package com.ea.nimble.identity;

import com.ea.nimble.Error;
import com.ea.nimble.tracking.NimbleTrackingS2SImpl;

import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityError.class */
public class NimbleIdentityError extends Error {
    public static final String NIMBLE_IDENTITY_ERROR_DOMAIN = "NimbleIdentityError";
    private static final long serialVersionUID = 1;

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityError$NimbleIdentityErrorCode.class */
    public enum NimbleIdentityErrorCode {
        NIMBLE_IDENTITY_ERROR_USER_CANCELLED(100),
        NIMBLE_IDENTITY_ERROR_UNSUPPORTED_ACTION(NimbleTrackingS2SImpl.EVENT_APPSTARTED_AFTERINSTALL),
        NIMBLE_IDENTITY_ERROR_UNAUTHENTICATED(1001),
        NIMBLE_IDENTITY_ERROR_SESSION_EXPIRED(1002),
        NIMBLE_IDENTITY_ERROR_INVALID_LOGINPARAMS(1003),
        NIMBLE_IDENTITY_ERROR_REFRESH_USER_INFO_FROM_FIRST_PARTY(1101),
        NIMBLE_IDENTITY_ERROR_REFRESH_USER_INFO_FROM_PID_INFO(1102),
        NIMBLE_IDENTITY_ERROR_BAD_CLIENT_ID(1500),
        NIMBLE_IDENTITY_ERROR_BAD_CLIENT_SECRET(1501),
        NIMBLE_IDENTITY_ERROR_INVALID_REQUEST(1502),
        NIMBLE_IDENTITY_ERROR_INVALID_OAUTH_INFO(1503),
        NIMBLE_IDENTITY_ERROR_MIGRATION_SOURCE_INVALID(9101),
        NIMBLE_IDENTITY_ERROR_MIGRATION_TARGET_INVALID(9102),
        NIMBLE_IDENTITY_ERROR_MIGRATION_NOT_AUTHENTICATED(9103),
        NIMBLE_IDENTITY_ERROR_MIGRATION_NO_ACCESS_TOKENS(9104),
        NIMBLE_IDENTITY_ERROR_MIGRATION_NO_URL(9105),
        NIMBLE_IDENTITY_ERROR_MIGRATION_FAILED(9106),
        NIMBLE_IDENTITY_ERROR_UNKNOWN(Integer.MAX_VALUE);
        
        private int m_value;

        NimbleIdentityErrorCode(int i) {
            this.m_value = i;
        }

        public int intValue() {
            return this.m_value;
        }
    }

    public NimbleIdentityError(int i, String str) {
        super(NIMBLE_IDENTITY_ERROR_DOMAIN, i, str, null);
    }

    public NimbleIdentityError(int i, String str, Throwable th) {
        super(Error.ERROR_DOMAIN, i, str, th);
    }

    public NimbleIdentityError(NimbleIdentityErrorCode nimbleIdentityErrorCode, String str) {
        super(NIMBLE_IDENTITY_ERROR_DOMAIN, nimbleIdentityErrorCode.intValue(), str, null);
    }

    public static NimbleIdentityError createWithData(Map<String, Object> map) {
        String str = (String) map.get("error");
        NimbleIdentityErrorCode parseErrorCode = parseErrorCode(str);
        String str2 = (String) map.get("error_description");
        String str3 = str2;
        if (str2 == null) {
            str3 = str;
        }
        return new NimbleIdentityError(parseErrorCode, str3);
    }

    private static NimbleIdentityErrorCode parseErrorCode(String str) {
        return str.equals("invalid_request") ? NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_INVALID_REQUEST : str.equals("invalid_oauth_info") ? NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_INVALID_OAUTH_INFO : NimbleIdentityErrorCode.NIMBLE_IDENTITY_ERROR_UNKNOWN;
    }

    public boolean isError(int i) {
        return getCode() == i;
    }
}
