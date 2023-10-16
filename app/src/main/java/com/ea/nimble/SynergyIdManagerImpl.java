/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 */
package com.ea.nimble;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;
import java.util.HashMap;

class SynergyIdManagerImpl
extends Component
implements ISynergyIdManager,
LogSource {
    private static final String ANONYMOUS_ID_PERSISTENCE_DATA_ID = "anonymousId";
    private static final String AUTHENTICATOR_PERSISTENCE_DATA_ID = "authenticator";
    private static final String CURRENT_ID_PERSISTENCE_DATA_ID = "currentId";
    private static final String SYNERGY_ID_MANAGER_ANONYMOUS_ID_PERSISTENCE_ID = "com.ea.nimble.synergyidmanager.anonymousId";
    private static final String VERSION_PERSISTENCE_DATA_ID = "dataVersion";
    private String m_anonymousSynergyId;
    private String m_authenticatorIdentifier;
    private String m_currentSynergyId;
    private BroadcastReceiver m_receiver = new SynergyIdManagerReceiver();

    SynergyIdManagerImpl() {
    }

    public static ISynergyIdManager getComponent() {
        return (ISynergyIdManager)((Object)Base.getComponent("com.ea.nimble.synergyidmanager"));
    }

    private void onSynergyEnvironmentStartupRequestsFinished() {
        if (SynergyEnvironment.getComponent() != null) {
            Log.Helper.LOGD(this, "onSynergyEnvironmentStartupRequestsFinished - Process the notification, everything looks okay");
            this.setAnonymousSynergyId(SynergyEnvironment.getComponent().getSynergyId());
            if (Utility.validString(this.m_currentSynergyId)) return;
            this.setCurrentSynergyId(this.m_anonymousSynergyId);
            return;
        }
        Log.Helper.LOGI(this, "onSynergyEnvironmentStartupRequestsFinished - Aborted because we were unable to get SynergyEnvironment");
    }

    private void restoreFromPersistent() {
        Persistence persistence = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.synergyidmanager", Persistence.Storage.CACHE);
        if (persistence != null) {
            Log.Helper.LOGD("Loaded persistence data version,  %s.", persistence.getStringValue(VERSION_PERSISTENCE_DATA_ID));
            this.m_currentSynergyId = persistence.getStringValue(CURRENT_ID_PERSISTENCE_DATA_ID);
            this.m_authenticatorIdentifier = persistence.getStringValue(AUTHENTICATOR_PERSISTENCE_DATA_ID);
            Log.Helper.LOGD(this, "Loaded Synergy ID, %s, with authenticator, %s.", this.m_currentSynergyId, this.m_authenticatorIdentifier);
        } else {
            Log.Helper.LOGE(this, "Could not get persistence object to load from.");
        }
        if ((persistence = PersistenceService.getPersistenceForNimbleComponent(SYNERGY_ID_MANAGER_ANONYMOUS_ID_PERSISTENCE_ID, Persistence.Storage.DOCUMENT)) != null) {
            Log.Helper.LOGD(this, "Loaded persistence data version, %s.", Utility.safeString(persistence.getStringValue(VERSION_PERSISTENCE_DATA_ID)));
            this.m_anonymousSynergyId = persistence.getStringValue(ANONYMOUS_ID_PERSISTENCE_DATA_ID);
            Log.Helper.LOGD(this, "Loaded anonymous Synergy ID, %s.", Utility.safeString(this.m_anonymousSynergyId));
            return;
        }
        Log.Helper.LOGE(this, "Could not get anonymous Synergy ID persistence object to load from.");
    }

    private void saveDataToPersistent() {
        Persistence persistence = PersistenceService.getPersistenceForNimbleComponent(SYNERGY_ID_MANAGER_ANONYMOUS_ID_PERSISTENCE_ID, Persistence.Storage.DOCUMENT);
        if (persistence != null) {
            Log.Helper.LOGD("Saving anonymous Synergy ID, %s, to persistent.", this.m_anonymousSynergyId);
            persistence.setValue(VERSION_PERSISTENCE_DATA_ID, (Serializable)((Object)"1.0.0"));
            persistence.setValue(ANONYMOUS_ID_PERSISTENCE_DATA_ID, (Serializable)((Object)this.m_anonymousSynergyId));
            persistence.setBackUp(true);
            persistence.synchronize();
        } else {
            Log.Helper.LOGE(this, "Could not get anonymous Synergy ID persistence object to save to.");
        }
        if ((persistence = PersistenceService.getPersistenceForNimbleComponent("com.ea.nimble.synergyidmanager", Persistence.Storage.CACHE)) != null) {
            Log.Helper.LOGD(this, "Saving current Synergy ID, %s, and authenticator, %s, to persistent.", this.m_currentSynergyId, this.m_authenticatorIdentifier);
            persistence.setValue(VERSION_PERSISTENCE_DATA_ID, (Serializable)((Object)"1.0.0"));
            persistence.setValue(CURRENT_ID_PERSISTENCE_DATA_ID, (Serializable)((Object)this.m_currentSynergyId));
            persistence.setValue(AUTHENTICATOR_PERSISTENCE_DATA_ID, (Serializable)((Object)this.m_authenticatorIdentifier));
            persistence.synchronize();
            return;
        }
        Log.Helper.LOGE(this, "Could not get persistence object to save to.");
    }

    private void setAnonymousSynergyId(String object) {
        if (Utility.validString(this.m_anonymousSynergyId) && !Utility.validString((String)object)) {
            Log.Helper.LOGE(this, "Attempt to set invalid anonymous Synergy ID over existing ID, %s. Ignoring attempt.", this.m_anonymousSynergyId);
            return;
        }
        String string2 = this.m_anonymousSynergyId;
        this.m_anonymousSynergyId = object;
        this.saveDataToPersistent();
        if (Utility.validString(string2) && (Utility.validString(string2) && !string2.equals(this.m_anonymousSynergyId) || Utility.validString(this.m_anonymousSynergyId) && !this.m_anonymousSynergyId.equals(string2))) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("previousSynergyId", Utility.safeString(string2));
            hashMap.put("currentSynergyId", Utility.safeString(this.m_anonymousSynergyId));
            Utility.sendBroadcast("nimble.synergyidmanager.notification.anonymous_synergy_id_changed", hashMap);
        }
        if (this.m_authenticatorIdentifier != null) return;
        this.setCurrentSynergyId(this.m_anonymousSynergyId);
    }

    private void setCurrentSynergyId(String object) {
        if (Utility.validString(this.m_currentSynergyId) && !Utility.validString((String)object)) {
            Log.Helper.LOGE(this, "Attempt to set invalid current Synergy ID over existing ID, %s. Ignoring attempt.", this.m_currentSynergyId);
            return;
        }
        String string2 = this.m_currentSynergyId;
        this.m_currentSynergyId = object;
        this.saveDataToPersistent();
        if (!Utility.validString(string2)) return;
        if (!Utility.validString(string2) || string2.equals(this.m_currentSynergyId)) {
            if (!Utility.validString(this.m_currentSynergyId)) return;
            if (this.m_currentSynergyId.equals(string2)) return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("previousSynergyId", Utility.safeString(string2));
        hashMap.put("currentSynergyId", Utility.safeString(this.m_currentSynergyId));
        Utility.sendBroadcast("nimble.synergyidmanager.notification.synergy_id_changed", hashMap);
    }

    private void sleep() {
        Utility.unregisterReceiver(this.m_receiver);
        this.saveDataToPersistent();
    }

    private void wakeup() {
        this.restoreFromPersistent();
        if (!Utility.validString(this.m_anonymousSynergyId)) {
            this.setAnonymousSynergyId(SynergyEnvironment.getComponent().getSynergyId());
        }
        if (!Utility.validString(this.m_currentSynergyId)) {
            this.setCurrentSynergyId(this.m_anonymousSynergyId);
        }
        Utility.registerReceiver("nimble.environment.notification.startup_requests_finished", this.m_receiver);
    }

    @Override
    protected void cleanup() {
        this.sleep();
    }

    @Override
    public String getAnonymousSynergyId() {
        if (!Utility.validString(this.m_anonymousSynergyId)) return SynergyEnvironment.getComponent().getSynergyId();
        return this.m_anonymousSynergyId;
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.synergyidmanager";
    }

    @Override
    public String getLogSourceTitle() {
        return "SynergyId";
    }

    @Override
    public String getSynergyId() {
        if (!Utility.validString(this.m_currentSynergyId)) return this.getAnonymousSynergyId();
        return this.m_currentSynergyId;
    }

    @Override
    public SynergyIdManagerError login(String string2, String string3) {
        if (this.m_authenticatorIdentifier != null) {
            return new SynergyIdManagerError(SynergyIdManagerError.Code.UNEXPECTED_LOGIN_STATE.intValue(), "Already logged in with authenticator, " + this.m_authenticatorIdentifier);
        }
        if (!Utility.validString(string2)) return new SynergyIdManagerError(SynergyIdManagerError.Code.INVALID_ID.intValue(), "Synergy ID must be numeric digits.");
        if (!Utility.isOnlyDecimalCharacters(string2)) {
            return new SynergyIdManagerError(SynergyIdManagerError.Code.INVALID_ID.intValue(), "Synergy ID must be numeric digits.");
        }
        if (!Utility.validString(string3)) {
            return new SynergyIdManagerError(SynergyIdManagerError.Code.MISSING_AUTHENTICATOR.intValue(), "Authenticator string required for login API.");
        }
        this.m_authenticatorIdentifier = string3;
        this.setCurrentSynergyId(string2);
        return null;
    }

    @Override
    public SynergyIdManagerError logout(String string2) {
        if (this.m_authenticatorIdentifier == null) {
            return new SynergyIdManagerError(SynergyIdManagerError.Code.UNEXPECTED_LOGIN_STATE.intValue(), "Already logged out.");
        }
        if (!Utility.validString(string2)) {
            return new SynergyIdManagerError(SynergyIdManagerError.Code.MISSING_AUTHENTICATOR.intValue(), "Authenticator string required for logout API.");
        }
        if (!this.m_authenticatorIdentifier.equals(string2)) {
            return new SynergyIdManagerError(SynergyIdManagerError.Code.AUTHENTICATOR_CONFLICT.intValue(), "Logout must be performed by the same authenticator that logged in, " + this.m_authenticatorIdentifier);
        }
        this.setCurrentSynergyId(this.m_anonymousSynergyId);
        this.m_authenticatorIdentifier = null;
        return null;
    }

    @Override
    protected void restore() {
        this.wakeup();
    }

    @Override
    protected void resume() {
        this.wakeup();
    }

    @Override
    protected void suspend() {
        this.sleep();
    }

    private class SynergyIdManagerReceiver
    extends BroadcastReceiver {
        private SynergyIdManagerReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if (!ApplicationEnvironment.isMainApplicationRunning()) return;
            if (ApplicationEnvironment.getCurrentActivity() == null) return;
            SynergyIdManagerImpl.this.onSynergyEnvironmentStartupRequestsFinished();
        }
    }
}

