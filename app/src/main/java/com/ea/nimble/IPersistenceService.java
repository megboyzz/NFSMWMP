/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;

public interface IPersistenceService {
    public void cleanPersistenceReference(String var1, Persistence.Storage var2);

    public Persistence getPersistence(String var1, Persistence.Storage var2);

    public void migratePersistence(String var1, Persistence.Storage var2, String var3, PersistenceService.PersistenceMergePolicy var4);

    public void removePersistence(String var1, Persistence.Storage var2);
}

