package com.ea.nimble;

import android.app.backup.BackupAgent;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.Context;
import android.os.ParcelFileDescriptor;

import com.ea.ironmonkey.devmenu.util.Observer;

import java.io.FileOutputStream;
import java.io.IOException;

/* loaded from: stdlib.jar:com/ea/nimble/PersistenceService.class */
public class PersistenceService {
    private static final String APPLICATION_PERSISTENCE_ID = "[APPLICATION]";
    public static final String COMPONENT_ID = "com.ea.nimble.persistence";
    private static final String NIMBLE_COMPONENT_PERSISTENCE_ID_TEMPLATE = "[COMPONENT]%s";

    /* loaded from: stdlib.jar:com/ea/nimble/PersistenceService$PersistenceBackupAgent.class */
    public static class PersistenceBackupAgent extends BackupAgent {
        @Override // android.app.backup.BackupAgent
        public void onBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) throws IOException {
            synchronized (Persistence.s_dataLock) {
                PersistenceService.writeBackup(parcelFileDescriptor, backupDataOutput, parcelFileDescriptor2, this);
            }
        }

        @Override // android.app.backup.BackupAgent
        public void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) throws IOException {
            synchronized (Persistence.s_dataLock) {
                PersistenceService.readBackup(backupDataInput, i, parcelFileDescriptor, this);
            }
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/PersistenceService$PersistenceMergePolicy.class */
    public enum PersistenceMergePolicy {
        OVERWRITE,
        SOURCE_FIRST,
        TARGET_FIRST
    }

    public static void cleanReferenceToPersistence(String str, Persistence.Storage storage) {
        if (!Utility.validString(str)) {
            Log.Helper.LOGF("Persistence", "Invalid componentId " + str + " for component persistence", new Object[0]);
        } else {
            getComponent().cleanPersistenceReference(String.format(NIMBLE_COMPONENT_PERSISTENCE_ID_TEMPLATE, str), storage);
        }
    }

    public static Persistence getAppPersistence(Persistence.Storage storage) {
        return getComponent().getPersistence(APPLICATION_PERSISTENCE_ID, storage);
    }

    public static IPersistenceService getComponent() {
        return BaseCore.getInstance().getPersistenceService();
    }

    public static Persistence getPersistenceForNimbleComponent(String str, Persistence.Storage storage) {
        if (Utility.validString(str)) {
            return getComponent().getPersistence(String.format(NIMBLE_COMPONENT_PERSISTENCE_ID_TEMPLATE, str), storage);
        }
        Log.Helper.LOGF("Persistence", "Invalid componentId " + str + " for component persistence", new Object[0]);
        return null;
    }

    static void readBackup(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor, Context context) throws IOException {
        Throwable th;
        while (backupDataInput.readNextHeader()) {
            String key = backupDataInput.getKey();
            int dataSize = backupDataInput.getDataSize();
            byte[] bArr = new byte[dataSize];
            backupDataInput.readEntityData(bArr, 0, dataSize);
            FileOutputStream fileOutputStream = null;
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(Persistence.getPersistencePath(key, Persistence.Storage.DOCUMENT, context));
                try {
                    fileOutputStream2.write(bArr);
                    if (fileOutputStream2 != null) {
                        fileOutputStream2.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
        if (ApplicationEnvironment.isMainApplicationRunning()) {
            for (Persistence persistence : ((PersistenceServiceImpl) getComponent()).m_persistences.values()) {
                if (persistence.getBackUp()) {
                    persistence.restore(false, null);
                }
            }
        }
    }

    public static void removePersistenceForNimbleComponent(String str, Persistence.Storage storage) {
        if (!Utility.validString(str)) {
            Log.Helper.LOGF("Persistence", "Invalid componentId " + str + " for component persistence", new Object[0]);
        } else {
            getComponent().removePersistence(String.format(NIMBLE_COMPONENT_PERSISTENCE_ID_TEMPLATE, str), storage);
        }
    }


    static void writeBackup(android.os.ParcelFileDescriptor r6, android.app.backup.BackupDataOutput r7, android.os.ParcelFileDescriptor r8, android.content.Context r9) throws java.io.IOException {
        Observer.onCallingMethod();
    }
}
