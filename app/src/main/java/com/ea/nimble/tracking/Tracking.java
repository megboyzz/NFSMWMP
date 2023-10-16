/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.tracking;

import com.ea.nimble.Base;
import com.ea.nimble.tracking.ITracking;
import com.ea.nimble.tracking.TrackingWrangler;
import java.util.Date;
import java.util.Map;

public class Tracking {
    public static final String COMPONENT_ID = "com.ea.nimble.tracking";
    public static final String EVENT_APPRESUME_FROMEBISU = "NIMBLESTANDARD::APPRESUME_FROMEBISU";
    public static final String EVENT_APPRESUME_FROMPUSH = "NIMBLESTANDARD::APPRESUME_FROMPUSH";
    public static final String EVENT_APPRESUME_FROMURL = "NIMBLESTANDARD::APPRESUME_FROMURL";
    public static final String EVENT_APPRESUME_NORMAL = "NIMBLESTANDARD::APPRESUME_NORMAL";
    public static final String EVENT_APPSTART_AFTERINSTALL = "NIMBLESTANDARD::APPSTART_AFTERINSTALL";
    public static final String EVENT_APPSTART_AFTERUPGRADE = "NIMBLESTANDARD::APPSTART_AFTERUPGRADE";
    public static final String EVENT_APPSTART_FROMPUSH = "NIMBLESTANDARD::APPSTART_FROMPUSH";
    public static final String EVENT_APPSTART_FROMURL = "NIMBLESTANDARD::APPSTART_FROMURL";
    public static final String EVENT_APPSTART_NORMAL = "NIMBLESTANDARD::APPSTART_NORMAL";
    public static final String EVENT_LEVEL_UP = "NIMBLESTANDARD::LEVEL_UP";
    public static final String EVENT_MTX_FREEITEM_DOWNLOADED = "NIMBLESTANDARD::MTX_FREEITEM_DOWNLOADED";
    public static final String EVENT_MTX_ITEM_BEGIN_PURCHASE = "NIMBLESTANDARD::MTX_ITEM_BEGIN_PURCHASE";
    public static final String EVENT_MTX_ITEM_PURCHASED = "NIMBLESTANDARD::MTX_ITEM_PURCHASED";
    public static final String EVENT_PN_DEVICE_REGISTERED = "NIMBLESTANDARD::PN_DEVICE_REGISTERED";
    public static final String EVENT_PN_DISPLAY_OPT_IN = "NIMBLESTANDARD::PN_DISPLAY_OPT_IN";
    public static final String EVENT_PN_RECEIVED = "NIMBLESTANDARD::PN_RECEIVED";
    public static final String EVENT_PN_SHOWN_TO_USER = "NIMBLESTANDARD::PN_SHOWN_TO_USER";
    public static final String EVENT_PN_USER_CLICKED_OK = "NIMBLESTANDARD::PN_USER_CLICKED_OK";
    public static final String EVENT_PN_USER_OPT_IN = "NIMBLESTANDARD::PN_USER_OPT_IN";
    public static final String EVENT_REFERRERID_RECEIVED = "NIMBLESTANDARD::REFERRER_ID_RECEIVED";
    public static final String EVENT_SESSION_END = "NIMBLESTANDARD::SESSION_END";
    public static final String EVENT_SESSION_START = "NIMBLESTANDARD::SESSION_START";
    public static final String EVENT_SESSION_TIME = "NIMBLESTANDARD::SESSION_TIME";
    public static final String EVENT_TUTORIAL_COMPLETE = "NIMBLESTANDARD::TUTORIAL_COMPLETE";
    public static final String EVENT_USER_REGISTERED = "NIMBLESTANDARD::USER_REGISTERED";
    public static final String EVENT_USER_TRACKING_OPTOUT = "NIMBLESTANDARD::USER_TRACKING_OPTOUT";
    public static final String KEY_DURATION = "NIMBLESTANDARD::KEY_DURATION";
    public static final String KEY_GAMEPLAY_DURATION = "NIMBLESTANDARD::KEY_GAMEPLAY_DURATION";
    public static final String KEY_MTX_CURRENCY = "NIMBLESTANDARD::KEY_MTX_CURRENCY";
    public static final String KEY_MTX_PRICE = "NIMBLESTANDARD::KEY_MTX_PRICE";
    public static final String KEY_MTX_SELLID = "NIMBLESTANDARD::KEY_MTX_SELLID";
    public static final String KEY_PN_DATE_OF_BIRTH = "NIMBLESTANDARD::KEY_PN_DATE_OF_BIRTH";
    public static final String KEY_PN_DEVICE_ID = "NIMBLESTANDARD::KEY_PN_DEVICE_ID";
    public static final String KEY_PN_DISABLED_FLAG = "NIMBLESTANDARD::KEY_PN_DISABLED_FLAG";
    public static final String KEY_PN_MESSAGE_ID = "NIMBLESTANDARD::KEY_PN_MESSAGE_ID";
    public static final String KEY_PN_MESSAGE_TYPE = "NIMBLESTANDARD::KEY_PN_MESSAGE_TYPE";
    public static final String KEY_REFERRER_ID = "NIMBLESTANDARD::KEY_REFERRER_ID";
    public static final String KEY_USERNAME = "NIMBLESTANDARD::KEY_USERNAME";
    public static final String KEY_USER_LEVEL = "NIMBLESTANDARD::KEY_USER_LEVEL";
    public static final String NIMBLE_TRACKING_ATTRIBUTE_PROGRESSION_LEVEL = "NIMBLESTANDARD::ATTRIBUTE_PROGRESSION_LEVEL";
    public static final String NIMBLE_TRACKING_DEFAULTENABLE = "com.ea.nimble.tracking.defaultEnable";
    public static final String NIMBLE_TRACKING_EVENT_IDENTITY_LOGIN = "NIMBLESTANDARD::IDENTITY_LOGIN";
    public static final String NIMBLE_TRACKING_EVENT_IDENTITY_LOGOUT = "NIMBLESTANDARD::IDENTITY_LOGOUT";
    public static final String NIMBLE_TRACKING_EVENT_IDENTITY_MIGRATION = "NIMBLESTANDARD::IDENTITY_MIGRATION";
    public static final String NIMBLE_TRACKING_EVENT_IDENTITY_MIGRATION_STARTED = "NIMBLESTANDARD::IDENTITY_MIGRATION_STARTED";
    public static final String NIMBLE_TRACKING_KEY_IDENTITY_MAP_SOURCE = "NIMBLESTANDARD::KEY_IDENTITY_SOURCE";
    public static final String NIMBLE_TRACKING_KEY_IDENTITY_MAP_TARGET = "NIMBLESTANDARD::KEY_IDENTITY_TARGET";
    public static final String NIMBLE_TRACKING_KEY_IDENTITY_PIDMAP_LOGIN = "NIMBLESTANDARD::KEY_IDENTITY_PIDMAP_LOGIN";
    public static final String NIMBLE_TRACKING_KEY_IDENTITY_PIDMAP_LOGOUT = "NIMBLESTANDARD::KEY_IDENTITY_PIDMAP_LOGOUT";
    public static final String NIMBLE_TRACKING_KEY_MIGRATION_GAME_TRIGGERED = "NIMBLESTANDARD::KEY_MIGRATION_GAME_TRIGGERED";
    public static final String NIMBLE_TRACKING_KEY_PN_MESSAGEID = "NIMBLESTANDARD::KEY_PN_MESSAGE_ID";
    private static final String SESSION_END_EVENT_PREFIX = "SESSION_END";
    private static final String SESSION_RESUME_EVENT_PREFIX = "APPRESUME_";
    private static final String SESSION_START_EVENT_PREFIX = "APPSTART_";
    private static final String STANDARD_EVENT_PREFIX = "NIMBLESTANDARD::";

    public static ITracking getComponent() {
        return (ITracking)((Object)Base.getComponent(COMPONENT_ID));
    }

    private static void initialize() {
        Base.registerComponent(new TrackingWrangler(), COMPONENT_ID);
    }

    public static boolean isNimbleStandardEvent(String string2) {
        if (string2 != null) return string2.startsWith(STANDARD_EVENT_PREFIX);
        return false;
    }

    public static boolean isSessionEndEvent(String string2) {
        if (string2 != null) return string2.startsWith(SESSION_END_EVENT_PREFIX, STANDARD_EVENT_PREFIX.length());
        return false;
    }

    public static boolean isSessionStartEvent(String string2) {
        if (string2 == null) {
            return false;
        }
        if (string2.startsWith(SESSION_START_EVENT_PREFIX, STANDARD_EVENT_PREFIX.length())) return true;
        if (string2.equals(EVENT_SESSION_START)) return true;
        if (!string2.startsWith(SESSION_RESUME_EVENT_PREFIX, STANDARD_EVENT_PREFIX.length())) return false;
        return true;
    }

    public static class Event {
        Map<String, String> parameters;
        Date timestamp;
        String type;
    }
}

