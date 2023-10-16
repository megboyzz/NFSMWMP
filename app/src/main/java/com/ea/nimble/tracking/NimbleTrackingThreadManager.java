/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.tracking;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class NimbleTrackingThreadManager {
    private static NimbleTrackingThreadManager s_instance;
    private static int s_instanceRefs;
    private ScheduledExecutorService m_executor = new ScheduledThreadPoolExecutor(1){

        @Override
        protected void afterExecute(Runnable object, Throwable throwable) {}
    };

    NimbleTrackingThreadManager() {
    }

    static NimbleTrackingThreadManager acquireInstance() {
        if (s_instance == null) {
            s_instance = new NimbleTrackingThreadManager();
        }
        ++s_instanceRefs;
        return s_instance;
    }

    static void releaseInstance() {
        if (--s_instanceRefs != 0) return;
        s_instance.shutdown();
        s_instance = null;
    }

    private void shutdown() {
        this.m_executor.shutdown();
    }

    ScheduledFuture<?> createTimer(double d2, Runnable runnable) {
        return this.m_executor.schedule(runnable, (long)(1000.0 * d2), TimeUnit.MILLISECONDS);
    }

    void runInWorkerThread(Runnable runnable) {
        this.runInWorkerThread(false, runnable);
    }

    void runInWorkerThread(boolean bl2, Runnable object) {}
}

