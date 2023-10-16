package com.ea.nimble.identity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.Utility;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityConfig.class */
public class NimbleIdentityConfig implements LogSource {
    private static final double DEFAULT_DATA_EXPIRY_INTERVAL = 3600.0d;
    private static final String META_DATA_TAG_CLIENT_ID = "com.ea.nimble.identity.client_id";
    private static final String META_DATA_TAG_CLIENT_SECRET = "com.ea.nimble.identity.client_secret";
    static final String NOTIFICATION_IDENTITY_CONFIGURATION_CHANGE = "nimble.notification.identity.configuration.change";
    private boolean m_autoRefresh;
    private String m_clientId;
    private String m_clientSecret;
    private String m_connectServerUrl;
    private String m_portalServerUrl;
    private String m_proxyServerUrl;
    private boolean m_ready;
    private double m_expiryInterval = DEFAULT_DATA_EXPIRY_INTERVAL;
    private BroadcastReceiver m_receiver = new BroadcastReceiver() { // from class: com.ea.nimble.identity.NimbleIdentityConfig.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NimbleIdentityConfig.this.onUpdate();
        }
    };

    /* JADX WARN: Code restructure failed: missing block: B:22:0x00ac, code lost:
        if (r0.length() <= 0) goto L_0x00af;
     */
    /* JADX WARN: Code restructure failed: missing block: B:33:0x00fe, code lost:
        if (r0.length() <= 0) goto L_0x0101;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onUpdate() {
        /*
            Method dump skipped, instructions count: 438
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.identity.NimbleIdentityConfig.onUpdate():void");
    }

    public boolean getAutoRefresh() {
        return this.m_autoRefresh;
    }

    public String getClientId() {
        return this.m_clientId;
    }

    public String getClientSecret() {
        return this.m_clientSecret;
    }

    public String getConnectServerUrl() {
        return this.m_connectServerUrl;
    }

    public double getExpiryInterval() {
        return this.m_expiryInterval;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "Identity";
    }

    public String getPortalServerUrl() {
        return this.m_portalServerUrl;
    }

    public String getProxyServerUrl() {
        return this.m_proxyServerUrl;
    }

    public void initialize() {
        this.m_ready = false;
        this.m_autoRefresh = false;
        if (SynergyEnvironment.getComponent().isDataAvailable()) {
            onUpdate();
        }
        Utility.registerReceiver(SynergyEnvironment.NOTIFICATION_STARTUP_ENVIRONMENT_DATA_CHANGED, this.m_receiver);
        Log.Helper.LOGV(this, "Configuration initiailized", new Object[0]);
    }

    public boolean isReady() {
        return this.m_ready;
    }

    public void setAutoRefresh(boolean z) {
        this.m_autoRefresh = z;
    }

    void uninitialize() {
        Utility.unregisterReceiver(this.m_receiver);
    }
}
