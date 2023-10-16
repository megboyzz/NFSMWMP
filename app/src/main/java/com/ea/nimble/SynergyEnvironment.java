/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Base;
import com.ea.nimble.ISynergyEnvironment;

public class SynergyEnvironment {
    public static final String COMPONENT_ID = "com.ea.nimble.synergyEnvironment";
    public static final int INVALID_INT_VALUE = -1;
    public static final String NOTIFICATION_APP_VERSION_CHECK_FINISHED = "nimble.environment.notification.app_version_check_finished";
    public static final String NOTIFICATION_RESTORED_FROM_PERSISTENT = "nimble.environment.notification.restored_from_persistent";
    public static final String NOTIFICATION_STARTUP_ENVIRONMENT_DATA_CHANGED = "nimble.environment.notification.startup_environment_data_changed";
    public static final String NOTIFICATION_STARTUP_REQUESTS_FINISHED = "nimble.environment.notification.startup_requests_finished";
    public static final String NOTIFICATION_STARTUP_REQUESTS_STARTED = "nimble.environment.notification.startup_requests_started";
    public static final String SERVER_URL_KEY_AKAMAI = "akamai.url";
    public static final String SERVER_URL_KEY_DYNAMIC_MORE_GAMES = "dmg.url";
    public static final String SERVER_URL_KEY_EADP_FRIENDS_HOST = "eadp.friends.host";
    public static final String SERVER_URL_KEY_ENS = "ens.url";
    public static final String SERVER_URL_KEY_IDENTITY_CONNECT = "nexus.connect";
    public static final String SERVER_URL_KEY_IDENTITY_PORTAL = "nexus.portal";
    public static final String SERVER_URL_KEY_IDENTITY_PROXY = "nexus.proxy";
    public static final String SERVER_URL_KEY_MAYHEM = "mayhem.url";
    public static final String SERVER_URL_KEY_ORIGIN_AVATAR = "avatars.url";
    public static final String SERVER_URL_KEY_ORIGIN_CASUAL_APP = "origincasualapp.url";
    public static final String SERVER_URL_KEY_ORIGIN_CASUAL_SERVER = "origincasualserver.url";
    public static final String SERVER_URL_KEY_ORIGIN_FRIENDS = "friends.url";
    public static final String SERVER_URL_KEY_SYNERGY_CENTRAL_IP_GEOLOCATION = "geoip.url";
    public static final String SERVER_URL_KEY_SYNERGY_DIRECTOR = "synergy.director";
    public static final String SERVER_URL_KEY_SYNERGY_DRM = "synergy.drm";
    public static final String SERVER_URL_KEY_SYNERGY_MESSAGE_TO_USER = "synergy.m2u";
    public static final String SERVER_URL_KEY_SYNERGY_PRODUCT = "synergy.product";
    public static final String SERVER_URL_KEY_SYNERGY_S2S = "synergy.s2s";
    public static final String SERVER_URL_KEY_SYNERGY_TRACKING = "synergy.tracking";
    public static final String SERVER_URL_KEY_SYNERGY_USER = "synergy.user";

    public static ISynergyEnvironment getComponent() {
        return (ISynergyEnvironment)((Object)Base.getComponent(COMPONENT_ID));
    }
}

