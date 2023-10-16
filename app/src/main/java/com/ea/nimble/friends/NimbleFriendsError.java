package com.ea.nimble.friends;

import com.ea.nimble.Error;
import com.google.android.gms.games.GamesActivityResultCodes;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsError.class */
public class NimbleFriendsError extends Error {
    public static final String NIMBLE_FRIENDS_ERROR_DOMAIN = "NimbleFriendsError";
    private static final long serialVersionUID = 1;

    /* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsError$Code.class */
    public enum Code {
        NIMBLE_FRIENDS_FACEBOOK_NOT_AVAILABLE(90000),
        NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE(90001),
        NIMBLE_FRIENDS_FACEBOOK_USER_NOT_LOGGED_IN(90002),
        NIMBLE_FRIENDS_EMAIL_NOT_AVAILABLE(90003),
        NIMBLE_FRIENDS_SMS_NOT_AVAILABLE(90004),
        NIMBLE_FRIENDS_EMAIL_LAST_REQUEST_NOT_FINISHED(90005),
        NIMBLE_FRIENDS_SMS_LAST_REQUEST_NOT_FINISHED(90006),
        NIMBLE_FRIENDS_SMS_NOT_SENT_OUT(90007),
        NIMBLE_FRIENDS_NO_TARGETS_PROVIDED(90008),
        NIMBLE_FRIENDS_SERVER_RETURNED_ERROR(90009),
        NIMBLE_FRIENDS_REFRESH_SCOPE_INVALID(GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED),
        NIMBLE_FRIENDS_REFRESH_SCOPE_RANGE_EXCEED_LIMIT(GamesActivityResultCodes.RESULT_SIGN_IN_FAILED),
        NIMBLE_FRIENDS_REFRESH_SCOPE_INVALID_START_INDEX(GamesActivityResultCodes.RESULT_LICENSE_FAILED),
        NIMBLE_FRIENDS_REFRESH_FRIENDS_PROVIDER_NOT_AVAILABLE(GamesActivityResultCodes.RESULT_APP_MISCONFIGURED),
        NIMBLE_FRIENDS_REFRESH_NO_USER_IDS_LIST(GamesActivityResultCodes.RESULT_LEFT_ROOM),
        NIMBLE_FRIENDS_REFRESH_IDENTITY_SERVER_ERROR(10006),
        NIMBLE_FRIENDS_REFRESH_IDENTITY_SERVER_EMPTY_RESPONSE(10007),
        NIMBLE_FRIENDS_REFRESH_FRIENDS_LIST_EMPTY(10008),
        NIMBLE_FRIENDS_REFRESH_FRIENDS_LIST_NOT_UPDATED(10009),
        NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_SUPPORTED(10010),
        NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_LOGGED_IN(10011),
        NIMBLE_FRIENDS_REFRESH_SCOPE_FAILED_TO_CREATE_GOS_REQUEST(10012),
        NIMBLE_FRIENDS_FAILED_TO_CREATE_GOS_REQUEST(10012),
        NIMBLE_FRIENDS_REFRESH_SCOPE_ERROR_PARSING_HTTP_RESPONSE(10013),
        NIMBLE_FRIENDS_REFRESH_SCOPE_EMPTY_HTTP_RESPONSE(10014),
        NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR(10015),
        NIMBLE_FRIENDS_REFRESH_SCOPE_FRIENDS_LIST_TYPE_UNSUPPORTED(10016),
        NIMBLE_FRIENDS_REFRESH_TYPE_UNSUPPORTED(10017),
        NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR(10018),
        NIMBLE_FRIENDS_UNKNOWN_ERROR(0);
        
        private int m_value;

        Code(int i) {
            this.m_value = i;
        }

        public int intValue() {
            return this.m_value;
        }
    }

    public NimbleFriendsError(int i, String str) {
        super(NIMBLE_FRIENDS_ERROR_DOMAIN, i, str, null);
    }

    public NimbleFriendsError(int i, String str, Throwable th) {
        super(NIMBLE_FRIENDS_ERROR_DOMAIN, i, str, th);
    }

    public NimbleFriendsError(Code code, String str) {
        super(NIMBLE_FRIENDS_ERROR_DOMAIN, code.intValue(), str, null);
    }
}
