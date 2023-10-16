package com.eamobile.download;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.PowerManager;

public class LockManager {
    private static final String ADC_WAKE_LOCK_TAG = "ADCWakeLock";
    private static final String ADC_WIFI_LOCK_TAG = "ADCWifiLock";
    private Context context;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private WifiManager.WifiLock wifiLock;
    private WifiManager wifiManager = null;

    public LockManager(Context context2) {
        this.context = context2;
    }

    public boolean acquireWakeLock() {
        try {
            if (this.powerManager == null) {
                this.powerManager = (PowerManager) this.context.getSystemService("power");
            }
            if (this.powerManager != null && this.wakeLock == null) {
                this.wakeLock = this.powerManager.newWakeLock(6, ADC_WAKE_LOCK_TAG);
            }
            if (this.wakeLock == null) {
                Logging.DEBUG_OUT("[ERROR] While acquiring WakeLock (LockManager.acquireWakeLock()).");
                return false;
            } else if (!this.wakeLock.isHeld()) {
                this.wakeLock.acquire();
                Logging.DEBUG_OUT("LockManager.acquireWakeLock() successfully called.");
                return true;
            } else {
                Logging.DEBUG_OUT("LockManager.acquireWakeLock() - wakeLock already acquired.");
                return false;
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred in LockManager.acquireWakeLock().");
            Logging.DEBUG_OUT_STACK(e);
            return false;
        }
    }

    public boolean acquireWifiLock() {
        try {
            if (this.wifiManager == null) {
                this.wifiManager = (WifiManager) this.context.getSystemService("wifi");
            }
            if (this.wifiManager != null && this.wifiLock == null) {
                this.wifiLock = this.wifiManager.createWifiLock(1, ADC_WIFI_LOCK_TAG);
            }
            if (this.wifiLock == null) {
                Logging.DEBUG_OUT("[ERROR] While acquiring WifiLock (LockManager.acquireWifiLock()).");
                return false;
            } else if (!this.wifiLock.isHeld()) {
                this.wifiLock.acquire();
                Logging.DEBUG_OUT("LockManager.acquireWifiLock() successfully called.");
                return true;
            } else {
                Logging.DEBUG_OUT("LockManager.acquireWifiLock() - wifiLock already acquired.");
                return false;
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred in LockManager.acquireWifiLock().");
            Logging.DEBUG_OUT_STACK(e);
            return false;
        }
    }

    public void releaseWakeLock() {
        if (this.wakeLock != null && this.wakeLock.isHeld()) {
            this.wakeLock.release();
            this.wakeLock = null;
            Logging.DEBUG_OUT("LockManager.releaseWakeLock() successfully called.");
        }
    }

    public void releaseWifiLock() {
        if (this.wifiLock != null && this.wifiLock.isHeld()) {
            this.wifiLock.release();
            this.wifiLock = null;
            Logging.DEBUG_OUT("LockManager.releaseWifiLock() successfully called.");
        }
    }
}
