/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Intent
 *  android.os.Bundle
 */
package com.ea.nimble.tracking;

import android.content.Intent;
import android.os.Bundle;

import com.ea.ironmonkey.devmenu.util.Observer;
import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.ApplicationLifecycle;
import com.ea.nimble.Base;
import com.ea.nimble.Component;
import com.ea.nimble.IApplicationLifecycle;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.SynergyEnvironment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class TrackingEventWrangler
extends Component
implements IApplicationLifecycle.ApplicationLifecycleCallbacks,
LogSource {
    private static final String APP_VERSION_PERSISTENCE_ID = "applicationBundleVersion";
    public static final String COMPONENT_ID = "com.ea.nimble.tracking.eventwrangler";
    private Long m_sessionStartTimestamp;

    private TrackingEventWrangler() {
    }

    private void addPushTNGTrackingParams(Bundle bundle, Map<String, String> map) {
        if (bundle == null) return;
        if (bundle.isEmpty()) return;
        if (bundle.containsKey("pushId")) {
            map.put("NIMBLESTANDARD::KEY_PN_MESSAGE_ID", bundle.getString("pushId"));
        }
        if (bundle.containsKey("pnType")) {
            map.put("NIMBLESTANDARD::KEY_PN_MESSAGE_TYPE", bundle.getString("pnType"));
        }
        if (map == null) return;
        if (map.isEmpty()) return;
        map.put("NIMBLESTANDARD::KEY_PN_DEVICE_ID", SynergyEnvironment.getComponent().getEADeviceId());
    }

    private static void initialize() {
        Base.registerComponent(new TrackingEventWrangler(), COMPONENT_ID);
    }

    private void logAndCheckEvent(String string2) {
        this.logAndCheckEvent(string2, null);
    }

    private void logAndCheckEvent(String string2, Map<String, String> map) {
        Object object;
        if (Tracking.isSessionStartEvent(string2)) {
            if (this.m_sessionStartTimestamp != null) {
                Log.Helper.LOGE(this, "Pre-existing session start timestamp found while logging new session start! Overwriting previous session start timestamp.");
            } else {
                Log.Helper.LOGD(this, "Marking session start time.");
            }
            this.m_sessionStartTimestamp = System.currentTimeMillis();
        } else if (Tracking.isSessionEndEvent(string2)) {
            if (this.m_sessionStartTimestamp == null) {
                Log.Helper.LOGE(this, "No session start timestamp found while logging new session end! Skip logging 'session time' event.");
            } else {
                double d2 = (double)(System.currentTimeMillis() - this.m_sessionStartTimestamp) / 1000.0;
                object = String.format(Locale.US, "%.0f", d2);
                Log.Helper.LOGD(this, "Logging session time, %s seconds.", object);
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("NIMBLESTANDARD::KEY_DURATION", (String)object);
                this.logAndCheckEvent("NIMBLESTANDARD::SESSION_TIME", hashMap);
                this.m_sessionStartTimestamp = null;
            }
        }
        ITracking component = (ITracking) Base.getComponent("com.ea.nimble.tracking");
        if (component == null) return;
        component.logEvent(string2, map);
    }

    @Override
    public void cleanup() {
        ApplicationLifecycle.getComponent().unregisterApplicationLifecycleCallbacks(this);
    }

    @Override
    public String getComponentId() {
        return COMPONENT_ID;
    }

    @Override
    public String getLogSourceTitle() {
        return "Tracking";
    }

    /*
     * Unable to fully structure code
     */
    @Override
    public void onApplicationLaunch(Intent var1_1) {
        Observer.onCallingMethod(Observer.Method.HARD_TO_RECOVER_LOGIC, Observer.Method.VERY_SUSPICIOUS_METHOD);
        /*
        EASPDataLoader.EASPDataBuffer var2_3;
        Object var3_6;
        block10: {
            block9: {
                block8: {
                    if (var1_1.getData() != null) {
                        this.logAndCheckEvent("NIMBLESTANDARD::APPSTART_FROMURL");
                        return;
                    }
                    if (var1_1.getStringExtra("PushNotification") != null || ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).getString("PushNotification", null) != null) {
                        Log.Helper.LOGI(this, "Awesome. PN launched me");
                        var2_2 = new HashMap<String, String>();
                        this.addPushTNGTrackingParams(var1_1.getExtras(), var2_2);
                        if (var2_2.isEmpty()) {
                            this.logAndCheckEvent("NIMBLESTANDARD::APPSTART_FROMPUSH");
                            return;
                        }
                        this.logAndCheckEvent("NIMBLESTANDARD::APPSTART_FROMPUSH", var2_2);
                        return;
                    }
                    var1_1 = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.tracking.eventwrangler", Persistence.Storage.CACHE);
                    var2_3 = var1_1.getStringValue("applicationBundleVersion");
                    var3_6 = ApplicationEnvironment.getComponent().getApplicationVersion();
                    Log.Helper.LOGD(this, "Current app version, %s. Cached app version, %s", var3_6, var2_3);
                    if (var2_3 != null) break block10;
                    var1_1.setValue("applicationBundleVersion", (Serializable)var3_6);
                    var1_1 = null;
                    try {
                        var1_1 = var2_3 = EASPDataLoader.loadDatFile(EASPDataLoader.getTrackingDatFilePath());
lbl22:
                        // 3 sources

                        while (var1_1 != null) {
                            break block8;
                        }
                        break block9;
                    }
                    catch (FileNotFoundException var2_4) {
                        Log.Helper.LOGD(this, "No EASP tracking file.");
                    }
                    catch (Exception var2_5) {
                        Log.Helper.LOGE(this, "Exception loading EASP tracking file.");
                        ** GOTO lbl22
                    }
                }
                Log.Helper.LOGD(this, "EASP tracking file found. Counting as app update.");
                this.logAndCheckEvent("NIMBLESTANDARD::APPSTART_AFTERUPGRADE");
                return;
            }
            this.logAndCheckEvent("NIMBLESTANDARD::APPSTART_AFTERINSTALL");
            return;
        }
        if (!var2_3.equals(var3_6)) {
            var1_1.setValue("applicationBundleVersion", (Serializable)var3_6);
            this.logAndCheckEvent("NIMBLESTANDARD::APPSTART_AFTERUPGRADE");
            return;
        }
        this.logAndCheckEvent("NIMBLESTANDARD::APPSTART_NORMAL");

         */
    }

    @Override
    public void onApplicationQuit() {
        this.logAndCheckEvent("NIMBLESTANDARD::SESSION_END");
    }

    @Override
    public void onApplicationResume() {
        if (ApplicationEnvironment.getCurrentActivity().getIntent().getData() != null) {
            this.logAndCheckEvent("NIMBLESTANDARD::APPRESUME_FROMURL");
            return;
        }
        if (ApplicationEnvironment.getComponent().getApplicationContext().getSharedPreferences("PushNotification", 0).getString("PushNotification", null) != null) {
            this.logAndCheckEvent("NIMBLESTANDARD::APPRESUME_FROMPUSH");
            return;
        }
        this.logAndCheckEvent("NIMBLESTANDARD::SESSION_START");
    }

    @Override
    public void onApplicationSuspend() {
        this.logAndCheckEvent("NIMBLESTANDARD::SESSION_END");
    }

    @Override
    public void restore() {
        ApplicationLifecycle.getComponent().registerApplicationLifecycleCallbacks(this);
    }
}

