/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Error;
import com.ea.nimble.NimbleConfiguration;

public interface ISynergyEnvironment {
    public static final int NETWORK_CONNECTION_NONE = 1;
    public static final int NETWORK_CONNECTION_UNKNOWN = 0;
    public static final int NETWORK_CONNECTION_WIFI = 2;
    public static final int NETWORK_CONNECTION_WIRELESS = 3;
    public static final int SYNERGY_APP_VERSION_OK = 0;
    public static final int SYNERGY_APP_VERSION_UPDATE_RECOMMENDED = 1;
    public static final int SYNERGY_APP_VERSION_UPDATE_REQUIRED = 2;

    public Error checkAndInitiateSynergyEnvironmentUpdate();

    public String getEADeviceId();

    public String getEAHardwareId();

    public String getGosMdmAppKey();

    public int getLatestAppVersionCheckResult();

    public String getNexusClientId();

    public String getNexusClientSecret();

    public String getProductId();

    public String getSellId();

    public String getServerUrlWithKey(String var1);

    public String getSynergyDirectorServerUrl(NimbleConfiguration var1);

    public String getSynergyId();

    public int getTrackingPostInterval();

    public boolean isDataAvailable();

    public boolean isUpdateInProgress();
}

