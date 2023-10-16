/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PersistenceServiceImpl
extends Component
implements IPersistenceService,
LogSource {
    private Encryptor m_encryptor;
    protected ConcurrentMap<String, Persistence> m_persistences;

    /*
     * Enabled unnecessary exception pruning
     */
    private Persistence loadPersistenceById(String object, Persistence.Storage storage) {
        Object object2 = Persistence.s_dataLock;
        synchronized (object2) {
            String string2 = (String)object + "-" + storage.toString();
            Persistence persistence = (Persistence)this.m_persistences.get(string2);
            if (persistence != null) {
                return persistence;
            }
            if (!new File(Persistence.getPersistencePath(object, storage)).exists()) {
                return null;
            }
            Persistence persistence1 = new Persistence(object, storage, this.m_encryptor);
            persistence1.restore(false, null);
            this.m_persistences.put(string2, persistence1);
            return persistence1;
        }
    }

    private void synchronize() {
        for (Persistence persistence : this.m_persistences.values()) {
            persistence.synchronize();
        }
    }

    @Override
    public void cleanPersistenceReference(String string2, Persistence.Storage storage) {
        if (!Utility.validString(string2)) {
            Log.Helper.LOGF(this, "Invalid identifier " + string2 + " for persistence");
            return;
        }
        Object object = Persistence.s_dataLock;
        synchronized (object) {
            this.m_persistences.remove(string2 + "-" + storage.toString());
            return;
        }
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.persistence";
    }

    @Override
    public String getLogSourceTitle() {
        return "Persistence";
    }

    /*
     * Enabled unnecessary exception pruning
     */
    @Override
    public Persistence getPersistence(String string2, Persistence.Storage storage) {
        if (!Utility.validString(string2)) {
            Log.Helper.LOGF(this, "Invalid identifier " + string2 + " for persistence");
            return null;
        }
        Object object = Persistence.s_dataLock;
        synchronized (object) {
            Persistence persistence = this.loadPersistenceById(string2, storage);
            if (persistence != null) {
                return persistence;
            }
            persistence = new Persistence(string2, storage, this.m_encryptor);
            this.m_persistences.put(string2 + "-" + storage.toString(), persistence);
            return persistence;
        }
    }

    /*
     * Enabled unnecessary exception pruning
     */
    @Override
    public void migratePersistence(String object, Persistence.Storage object2, String string2, PersistenceService.PersistenceMergePolicy persistenceMergePolicy) {
        if (!Utility.validString((String)object) || !Utility.validString(string2)) {
            Log.Helper.LOGF(this, "Invalid identifiers " + (String)object + " or " + string2 + " for component persistence");
            return;
        }
        Persistence.Storage object3 = (Persistence.Storage) Persistence.s_dataLock;
        synchronized (object3) {
            String string3 = string2 + "-" + ((Enum)object2).toString();
            Persistence persistence = this.loadPersistenceById(object, object2);
            if (persistence == null) {
                if (persistenceMergePolicy != PersistenceService.PersistenceMergePolicy.OVERWRITE) return;
                this.m_persistences.remove(string3);
                new File(Persistence.getPersistencePath(string2, object2)).delete();
                return;
            }
            Persistence persistence2 = this.loadPersistenceById(string2, object2);
            if (persistence2 == null) {
                Persistence persistence1 = new Persistence(persistence, string2);
                this.m_persistences.put(string3, persistence);
                persistence1.synchronize();
            } else {
                persistence2.merge(persistence, persistenceMergePolicy);
            }
        }
    }

    @Override
    public void removePersistence(String string2, Persistence.Storage storage) {
        if (!Utility.validString(string2)) {
            Log.Helper.LOGF(this, "Invalid identifier " + string2 + " for persistence");
            return;
        }
        this.cleanPersistenceReference(string2, storage);
    }

    @Override
    public void setup() {
        this.m_persistences = new ConcurrentHashMap<>();
        this.m_encryptor = new Encryptor();
    }

    @Override
    public void suspend() {
        this.synchronize();
    }

    @Override
    public void teardown() {
        this.synchronize();
        Object object = Persistence.s_dataLock;
        synchronized (object) {
            this.m_persistences = null;
            this.m_encryptor = null;
            return;
        }
    }
}

