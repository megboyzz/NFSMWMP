package com.eamobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import com.eamobile.download.Logging;

public class WifiReceiver extends BroadcastReceiver {
    private int wifiLevel = 0;
    private WifiManager wifiManager;
    private String wifiName = "";

    public WifiReceiver() {
        setWifiManager();
    }

    private void setWifiManager() {
        Logging.DEBUG_OUT("Calling setWifiManager...");
        try {
            if (DownloadActivityInternal.getInstance() != null) {
                this.wifiManager = (WifiManager) DownloadActivityInternal.getInstance().getSystemService("wifi");
            } else {
                this.wifiManager = null;
            }
        } catch (Exception e) {
            this.wifiManager = null;
            Logging.DEBUG_OUT("[ERROR] An exception occurred in WifiReceiver while trying to get WifiManager.");
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    public int getWifiLevel() {
        return this.wifiLevel;
    }

    public String getWifiName() {
        return this.wifiName;
    }

    public void onReceive(Context context, Intent intent) {
        updateWifiInfo();
    }

    public void updateWifiInfo() {
        if (this.wifiManager == null) {
            setWifiManager();
        }
        if (this.wifiManager != null && this.wifiManager.isWifiEnabled()) {
            WifiInfo connectionInfo = this.wifiManager.getConnectionInfo();
            this.wifiName = connectionInfo.getSSID();
            int calculateSignalLevel = WifiManager.calculateSignalLevel(connectionInfo.getRssi(), 3);
            if (this.wifiLevel != calculateSignalLevel) {
                this.wifiLevel = calculateSignalLevel;
                Logging.DEBUG_OUT("Wifi connection: " + this.wifiName + " (signal strength: " + this.wifiLevel + "/3)");
            }
        }
    }
}
