/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 */
package com.ea.nimble;

import android.content.Context;

public interface IApplicationEnvironment {
    public int getAgeCompliance();

    public String getApplicationBundleId();

    public Context getApplicationContext();

    public String getApplicationLanguageCode();

    public String getApplicationName();

    public String getApplicationVersion();

    public String getCachePath();

    public String getCarrier();

    public String getDeviceBrand();

    public String getDeviceCodename();

    public String getDeviceFingerprint();

    public String getDeviceManufacturer();

    public String getDeviceModel();

    public String getDeviceString();

    public String getDocumentPath();

    public String getGameSpecifiedPlayerId();

    public String getGoogleAdvertisingId();

    public String getGoogleEmail();

    public boolean getIadAttribution();

    public String getMACAddress();

    public String getOsVersion();

    public String getShortApplicationLanguageCode();

    public String getTempPath();

    public boolean isAppCracked();

    public boolean isDeviceRooted();

    public boolean isLimitAdTrackingEnabled();

    public void refreshAgeCompliance();

    public void setApplicationBundleId(String var1);

    public void setApplicationLanguageCode(String var1);

    public void setGameSpecifiedPlayerId(String var1);
}

