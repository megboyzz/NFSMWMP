/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.tracking;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackingBaseSessionObject
implements Externalizable {
    private static final long serialVersionUID = 1L;
    public List<Map<String, String>> events = new ArrayList<Map<String, String>>();
    public int repostCount;
    public Map<String, Object> sessionData;

    public TrackingBaseSessionObject() {
        this.sessionData = new HashMap<String, Object>();
        this.repostCount = 0;
    }

    public TrackingBaseSessionObject(Map<String, Object> map) {
        this.sessionData = map;
        this.repostCount = 0;
    }

    public int countOfEvents() {
        if (this.events != null) return this.events.size();
        return 0;
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.events = (List)objectInput.readObject();
        this.sessionData = (Map)objectInput.readObject();
        this.repostCount = objectInput.readInt();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.events);
        objectOutput.writeObject(this.sessionData);
        objectOutput.writeInt(this.repostCount);
    }
}

