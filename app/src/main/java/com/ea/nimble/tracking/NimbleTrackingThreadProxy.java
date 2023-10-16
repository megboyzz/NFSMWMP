/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.tracking;

import com.ea.nimble.Component;
import com.ea.nimble.tracking.ITracking;
import com.ea.nimble.tracking.NimbleTrackingImplBase;
import com.ea.nimble.tracking.NimbleTrackingThreadManager;
import java.util.Map;

public abstract class NimbleTrackingThreadProxy
extends Component
implements ITracking {
    private NimbleTrackingImplBase m_impl;
    private NimbleTrackingThreadManager m_threadManager;

    protected NimbleTrackingThreadProxy(NimbleTrackingImplBase nimbleTrackingImplBase) {
        this.m_impl = nimbleTrackingImplBase;
    }

    @Override
    public void addCustomSessionData(final String string2, final String string3) {
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.addCustomSessionData(string2, string3);
            }
        });
    }

    @Override
    protected void cleanup() {
        this.m_threadManager.runInWorkerThread(true, new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.cleanup();
            }
        });
    }

    @Override
    public void clearCustomSessionData() {
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.clearCustomSessionData();
            }
        });
    }

    @Override
    public String getComponentId() {
        return this.m_impl.getComponentId();
    }

    @Override
    public boolean getEnable() {
        return this.m_impl.getEnable();
    }

    @Override
    public void logEvent(final String string2, final Map<String, String> map) {
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.logEvent(string2, map);
            }
        });
    }

    @Override
    protected void restore() {
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.restore();
            }
        });
    }

    @Override
    protected void resume() {
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.resume();
            }
        });
    }

    @Override
    public void setEnable(final boolean bl2) {
        this.m_threadManager.runInWorkerThread(true, new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.setEnable(bl2);
            }
        });
    }

    @Override
    public void setTrackingAttribute(final String string2, final String string3) {
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.setTrackingAttribute(string2, string3);
            }
        });
    }

    @Override
    protected void setup() {
        this.m_threadManager = NimbleTrackingThreadManager.acquireInstance();
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.setup();
            }
        });
    }

    @Override
    protected void suspend() {
        this.m_threadManager.runInWorkerThread(new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.suspend();
            }
        });
    }

    @Override
    protected void teardown() {
        this.m_threadManager.runInWorkerThread(true, new Runnable(){

            @Override
            public void run() {
                NimbleTrackingThreadProxy.this.m_impl.teardown();
            }
        });
        NimbleTrackingThreadManager.releaseInstance();
        this.m_threadManager = null;
    }
}

