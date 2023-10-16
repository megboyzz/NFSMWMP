/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Handler
 *  android.os.Looper
 *  android.os.SystemClock
 */
package com.ea.nimble;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.ea.nimble.Log;

public class Timer {
    private static Handler s_handler = new Handler(Looper.getMainLooper());
    private long m_fireTime;
    private long m_pauseTime;
    private boolean m_paused;
    private boolean m_running;
    private Runnable m_task;
    private Runnable m_taskToRun;
    private long m_timeInterval;

    public Timer(Runnable runnable) {
        this.m_task = runnable;
        this.m_running = false;
        this.m_paused = false;
    }

    static /* synthetic */ boolean access$102(Timer timer, boolean bl2) {
        timer.m_running = bl2;
        return bl2;
    }

    static /* synthetic */ long access$302(Timer timer, long l2) {
        timer.m_fireTime = l2;
        return l2;
    }

    public void cancel() {
        if (!this.m_running) return;
        if (!this.m_paused) {
            s_handler.removeCallbacks(this.m_taskToRun);
        }
        this.m_running = false;
    }

    public void fire() {
        this.cancel();
        this.m_task.run();
        if (!(this.m_taskToRun instanceof RepeatingTask)) return;
        if (this.m_paused) {
            this.m_fireTime = this.m_pauseTime + this.m_timeInterval;
            return;
        }
        this.m_fireTime = SystemClock.uptimeMillis() + this.m_timeInterval;
        s_handler.postDelayed(this.m_taskToRun, this.m_timeInterval);
        this.m_running = true;
    }

    public boolean isPaused() {
        return this.m_paused;
    }

    public boolean isRunning() {
        return this.m_running;
    }

    public void pause() {
        if (this.m_paused) return;
        if (!this.m_running) return;
        this.m_pauseTime = SystemClock.uptimeMillis();
        s_handler.removeCallbacks(this.m_taskToRun);
        this.m_paused = true;
    }

    public void resume() {
        if (!this.m_paused) return;
        if (!this.m_running) return;
        this.m_fireTime += SystemClock.uptimeMillis() - this.m_pauseTime;
        s_handler.postAtTime(this.m_taskToRun, this.m_fireTime);
        this.m_paused = false;
    }

    public void schedule(double d2, boolean bl2) {
        this.cancel();
        if (d2 < 0.1) {
            if (bl2) {
                Log.Helper.LOGES(null, "Timer scheduled to repeat for %.2f seconds, running only once", d2);
            }
            if (Looper.myLooper() == Looper.getMainLooper()) {
                this.m_task.run();
                return;
            }
            s_handler.post(this.m_task);
            return;
        }
        this.m_timeInterval = (long)(1000.0 * d2);
        this.m_fireTime = SystemClock.uptimeMillis() + this.m_timeInterval;
        this.m_taskToRun = bl2 ? new RepeatingTask() : new SingleRunTask();
        s_handler.postDelayed(this.m_taskToRun, this.m_timeInterval);
        this.m_running = true;
    }

    private class RepeatingTask
    implements Runnable {
        private RepeatingTask() {
        }

        @Override
        public void run() {
            if (Timer.this.m_paused) return;
            if (!Timer.this.m_running) return;
            Timer.this.m_task.run();
            Timer.access$302(Timer.this, SystemClock.uptimeMillis() + Timer.this.m_fireTime);
            s_handler.postDelayed((Runnable)this, Timer.this.m_timeInterval);
        }
    }

    private class SingleRunTask
    implements Runnable {
        private SingleRunTask() {
        }

        @Override
        public void run() {
            if (Timer.this.m_paused) return;
            if (!Timer.this.m_running) return;
            Timer.access$102(Timer.this, false);
            Timer.this.m_task.run();
        }
    }
}

