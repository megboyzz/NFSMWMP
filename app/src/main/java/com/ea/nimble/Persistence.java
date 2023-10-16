package com.ea.nimble;

import android.app.backup.BackupManager;
import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/Persistence.class */
public class Persistence implements LogSource {
    private static int PERSISTENCE_VERSION = -1;
    static final Object s_dataLock = new Object();
    private boolean m_backUp;
    private boolean m_changed;
    private Map<String, byte[]> m_content;
    private boolean m_encryption;
    private Encryptor m_encryptor;
    private String m_identifier;
    private Storage m_storage;
    private Timer m_synchronizeTimer;

    public static class AnonymousClass2 {
        static final int[] $SwitchMap$com$ea$nimble$Persistence$Storage = new int[Storage.values().length];
        static final int[] $SwitchMap$com$ea$nimble$PersistenceService$PersistenceMergePolicy = new int[10];

        /* loaded from: stdlib.jar:com/ea/nimble/Persistence$Storage.class */
    }

    public enum Storage {
        DOCUMENT,
        CACHE,
        TEMP
    }

    public Persistence(Persistence persistence, String str) {
        this.m_synchronizeTimer = new Timer(new Runnable() { // from class: com.ea.nimble.Persistence.1
            @Override // java.lang.Runnable
            public void run() {
                Persistence.this.synchronize();
            }
        });
        this.m_content = new HashMap(persistence.m_content);
        this.m_identifier = str;
        this.m_storage = persistence.m_storage;
        this.m_encryptor = persistence.m_encryptor;
        this.m_encryption = persistence.m_encryption;
        this.m_backUp = persistence.m_backUp;
        flagChange();
    }

    public Persistence(String str, Storage storage, Encryptor encryptor) {
        this.m_synchronizeTimer = new Timer(new Runnable() { // from class: com.ea.nimble.Persistence.1
            @Override // java.lang.Runnable
            public void run() {
                Persistence.this.synchronize();
            }
        });
        this.m_content = new HashMap();
        this.m_identifier = str;
        this.m_storage = storage;
        this.m_encryptor = encryptor;
        this.m_encryption = false;
        this.m_backUp = false;
        this.m_changed = false;
    }

    private void clearSynchronizeTimer() {
        synchronized (s_dataLock) {
            this.m_synchronizeTimer.cancel();
        }
    }

    private void flagChange() {
        this.m_changed = true;
        synchronized (s_dataLock) {
            clearSynchronizeTimer();
            this.m_synchronizeTimer.schedule(0.5d, false);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0068  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static java.io.File getPersistenceDirectory(com.ea.nimble.Persistence.Storage r7) {
        return new File("");
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x008f  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x00bb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.io.File getPersistenceDirectory(com.ea.nimble.Persistence.Storage r7, android.content.Context r8) {
        /*
            Method dump skipped, instructions count: 241
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.Persistence.getPersistenceDirectory(com.ea.nimble.Persistence$Storage, android.content.Context):java.io.File");
    }

    public static String getPersistencePath(String str, Storage storage) {
        File persistenceDirectory = getPersistenceDirectory(storage);
        if (persistenceDirectory == null) {
            return null;
        }
        return persistenceDirectory + File.separator + str + ".dat";
    }

    public static String getPersistencePath(String str, Storage storage, Context context) {
        File persistenceDirectory = getPersistenceDirectory(storage, context);
        if (persistenceDirectory == null) {
            return null;
        }
        return persistenceDirectory + File.separator + str + ".dat";
    }

    private void loadPersistenceData(boolean z, Context context) {
        Throwable th;
        FileInputStream fileInputStream = null;
        String persistencePath = context == null ? getPersistencePath(this.m_identifier, this.m_storage) : getPersistencePath(this.m_identifier, this.m_storage, context);
        if (persistencePath != null) {
            File file = new File(persistencePath);
            if (!file.exists() || file.length() == 0) {
                Log.Helper.LOGD(this, "No persistence file for id[%s] to restore from storage %s", this.m_identifier, this.m_storage.toString());
                return;
            }
            try {
                Log.Helper.LOGD(this, "Loading persistence file size %d", file.length());
                fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(file);
                } catch (Exception e) {
                    e = e;
                }
            } catch (Throwable th2) {
                th = th2;
            }
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                if (objectInputStream.readInt() != PERSISTENCE_VERSION) {
                    throw new InvalidClassException("com.ea.nimble.Persistence", "Persistence version doesn't match");
                }
                this.m_encryption = objectInputStream.readBoolean();
                this.m_backUp = objectInputStream.readBoolean();
                if (!z) {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    ObjectInputStream encryptInputStream = this.m_encryption ? this.m_encryptor.encryptInputStream(bufferedInputStream) : new ObjectInputStream(bufferedInputStream);
                    this.m_content = (Map) encryptInputStream.readObject();
                    Log.Helper.LOGD(this, "Persistence file for id[%s] restored from storage %s", this.m_identifier, this.m_storage.toString());
                    encryptInputStream.close();
                }
                objectInputStream.close();
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Exception e3) {
                Log.Helper.LOGE(this, "Can't read persistence (%s) file, %s: %s", this.m_identifier, persistencePath, e3.toString());
                e3.printStackTrace();
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e4) {
                    }
                }
            } catch (Throwable th3) {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e5) {
                    }
                }
            }
        }
    }

    private void putValue(String str, Serializable serializable) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(serializable);
        objectOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        if (!byteArray.equals(this.m_content.get(str))) {
            this.m_content.put(str, byteArray);
            flagChange();
        }
    }

    private void savePersistenceData() {
        String persistencePath = getPersistencePath(this.m_identifier, this.m_storage);
        if (persistencePath != null) {
            File file = new File(persistencePath);

            try (FileOutputStream fileOutputStream = new FileOutputStream(file);
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                         this.m_encryption ? this.m_encryptor.encryptOutputStream(
                                 new BufferedOutputStream(fileOutputStream)) :
                                 new BufferedOutputStream(fileOutputStream))) {

                objectOutputStream.writeInt(PERSISTENCE_VERSION);
                objectOutputStream.writeBoolean(this.m_encryption);
                objectOutputStream.writeBoolean(this.m_backUp);
                objectOutputStream.writeObject(this.m_content);

                Log.Helper.LOGD(this, "Synchronize persistence for id[%s] in storage %s", this.m_identifier, this.m_storage.toString());
                Log.Helper.LOGD(this, "Saving persistence file size %d", file.length());

            } catch (Exception e) {
                Log.Helper.LOGE(this, "Fail to save persistence file for id[%s] in storage %s: %s", this.m_identifier, this.m_storage.toString(), e.toString());
            }
        }
    }

    public void addEntries(Object... objArr) {
        synchronized (s_dataLock) {
            String str = null;
            for (int i = 0; i < objArr.length; i++) {
                if (i % 2 == 0) {
                    try {
                        str = (String) objArr[i];
                        if (!Utility.validString(str)) {
                            throw new RuntimeException("Invalid key");
                        }
                    } catch (Exception e) {
                        Log.Helper.LOGF(this, "Invalid key in NimblePersistence.addEntries at index %d, not a string", Integer.valueOf(i));
                        return;
                    }
                } else {
                    try {
                        putValue(str, (Serializable) objArr[i]);
                    } catch (Exception e2) {
                        Log.Helper.LOGF(this, "Invalid value in NimblePersistence.addEntries for key %s at index %d", str, Integer.valueOf(i));
                        return;
                    }
                }
            }
        }
    }

    public void addEntriesFromMap(Map<String, Serializable> map) {
        synchronized (s_dataLock) {
            for (String str : map.keySet()) {
                if (!Utility.validString(str)) {
                    Log.Helper.LOGE(this, "Invalid key %s in NimblePersistence.addEntriesInDictionary, not a string, skip it", str);
                } else {
                    Serializable serializable = map.get(str);
                    if (serializable != null) {
                        try {
                            putValue(str, serializable);
                        } catch (IOException e) {
                        }
                    }
                    Log.Helper.LOGE(this, "Invalid value in NimblePersistence.addEntries for key %s", str);
                }
            }
        }
    }

    public void clean() {
        synchronized (s_dataLock) {
            File file = new File(getPersistencePath(this.m_identifier, this.m_storage));
            if (file.exists() && !file.delete()) {
                Log.Helper.LOGE(this, "Fail to clean persistence file for id[%s] in storage %s", this.m_identifier, this.m_storage.toString());
            }
        }
    }

    public boolean getBackUp() {
        return this.m_backUp;
    }

    public boolean getEncryption() {
        return this.m_encryption;
    }

    public String getIdentifier() {
        return this.m_identifier;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "Persistence";
    }

    public Storage getStorage() {
        return this.m_storage;
    }

    public String getStringValue(String str) {
        Serializable value = getValue(str);
        try {
            return (String) value;
        } catch (ClassCastException e) {
            Log.Helper.LOGF(this, "Invalid value type for getStringValueCall, value is " + value.getClass().getName());
            return null;
        }
    }

    public Serializable getValue(String str) {
        synchronized (s_dataLock) {
            byte[] bArr = this.m_content.get(str);
            if (bArr == null) {
                return null;
            }
            try {
                return (Serializable) new ObjectInputStream(new ByteArrayInputStream(bArr)).readObject();
            } catch (Exception e) {
                Log.Helper.LOGD(this, "PERSIST: Exception getting value, " + str + ":" + e);
                return null;
            }
        }
    }

    public void merge(Persistence persistence, PersistenceService.PersistenceMergePolicy persistenceMergePolicy) {
        switch (AnonymousClass2.$SwitchMap$com$ea$nimble$PersistenceService$PersistenceMergePolicy[persistenceMergePolicy.ordinal()]) {
            case 1:
                this.m_content = new HashMap(persistence.m_content);
                return;
            case 2:
                this.m_content.putAll(persistence.m_content);
                return;
            case 3:
                for (String str : persistence.m_content.keySet()) {
                    if (this.m_content.get(str) == null) {
                        this.m_content.put(str, persistence.m_content.get(str));
                    }
                }
                return;
            default:
                return;
        }
    }

    public void restore(boolean z, Context context) {
        synchronized (s_dataLock) {
            loadPersistenceData(z, context);
        }
    }

    public void setBackUp(boolean z) {
        if (this.m_storage != Storage.DOCUMENT) {
            Log.Helper.LOGF(this, "Error: Backup flag not supported for storage: " + this.m_storage);
        } else {
            this.m_backUp = z;
        }
    }

    public void setEncryption(boolean z) {
        if (z != this.m_encryption) {
            this.m_encryption = z;
            flagChange();
        }
    }

    public void setValue(String str, Serializable serializable) {
        synchronized (s_dataLock) {
            if (!Utility.validString(str)) {
                Log.Helper.LOGF(this, "NimblePersistence cannot accept an invalid string " + str + " as key");
            } else if (serializable == null) {
                if (this.m_content.get(str) != null) {
                    this.m_content.remove(str);
                    flagChange();
                }
            } else {
                try {
                    putValue(str, serializable);
                } catch (IOException e) {
                    Log.Helper.LOGF(this, "NimblePersistence cannot archive value " + serializable.toString());
                }
            }
        }
    }

    public void synchronize() {
        synchronized (s_dataLock) {
            if (!this.m_changed) {
                Log.Helper.LOGD(this, "Not synchronizing to persistence for id[%s] since there is no change", this.m_identifier);
                return;
            }
            clearSynchronizeTimer();
            savePersistenceData();
            if (this.m_backUp) {
                new BackupManager(ApplicationEnvironment.getComponent().getApplicationContext()).dataChanged();
            }
        }
    }

}
