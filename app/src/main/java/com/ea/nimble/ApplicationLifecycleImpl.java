/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.app.Activity
 *  android.content.Intent
 *  android.os.Build$VERSION
 *  android.os.Bundle
 */
package com.ea.nimble;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Iterator;

class ApplicationLifecycleImpl
extends Component
implements IApplicationLifecycle,
LogSource {
    private static final boolean RESTART_ON_CONFIG_CHANGE;
    private ArrayList<IApplicationLifecycle.ActivityEventCallbacks> m_activityEventCallbacks;
    private ArrayList<IApplicationLifecycle.ActivityLifecycleCallbacks> m_activityLifecycleCallbacks;
    private ArrayList<IApplicationLifecycle.ApplicationLifecycleCallbacks> m_applicationLifecycleCallbacks;
    private BaseCore m_core;
    private int m_createdActivityCount;
    private int m_runningActivityCount;
    private State m_state;

    static {
        boolean bl2 = Build.VERSION.SDK_INT < 11;
        RESTART_ON_CONFIG_CHANGE = bl2;
    }

    ApplicationLifecycleImpl(BaseCore baseCore) {
        this.m_core = baseCore;
        this.m_state = State.INIT;
        this.m_createdActivityCount = 0;
        this.m_runningActivityCount = 0;
        this.m_activityLifecycleCallbacks = new ArrayList();
        this.m_activityEventCallbacks = new ArrayList();
        this.m_applicationLifecycleCallbacks = new ArrayList();
    }

    private void notifyApplicationLaunch(Intent intent) {
        Log.Helper.LOGD(this, "Application launch");
        Iterator<IApplicationLifecycle.ApplicationLifecycleCallbacks> iterator = this.m_applicationLifecycleCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onApplicationLaunch(intent);
        }
    }

    private void notifyApplicationQuit() {
        Iterator<IApplicationLifecycle.ApplicationLifecycleCallbacks> iterator = this.m_applicationLifecycleCallbacks.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                Log.Helper.LOGD(this, "Application quit");
                return;
            }
            iterator.next().onApplicationQuit();
        }
    }

    private void notifyApplicationResume() {
        Log.Helper.LOGD(this, "Application resume");
        Iterator<IApplicationLifecycle.ApplicationLifecycleCallbacks> iterator = this.m_applicationLifecycleCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onApplicationResume();
        }
    }

    private void notifyApplicationSuspend() {
        Iterator<IApplicationLifecycle.ApplicationLifecycleCallbacks> iterator = this.m_applicationLifecycleCallbacks.iterator();
        while (true) {
            if (!iterator.hasNext()) {
                Log.Helper.LOGD(this, "Application suspend");
                return;
            }
            iterator.next().onApplicationSuspend();
        }
    }

    @Override
    public String getComponentId() {
        return "com.ea.nimble.applicationlifecycle";
    }

    @Override
    public String getLogSourceTitle() {
        return "AppLifecycle";
    }

    @Override
    public boolean handleBackPressed() {
        boolean bl2 = true;
        for (ActivityEventCallbacks m_activityEventCallback : this.m_activityEventCallbacks) {
            if (m_activityEventCallback.onBackPressed()) continue;
            bl2 = false;
        }
        return bl2;
    }

    @Override
    public void notifyActivityCreate(Bundle bundle, Activity activity) {
        Log.Helper.LOGV(this, "Activity %s CREATE", activity.getLocalClassName());
        if (this.m_state == State.INIT || this.m_state == State.QUIT) {
            Log.Helper.LOGD(this, "Activity created clearly with state %s", this.m_state.toString());
            this.m_core.onApplicationLaunch(activity.getIntent());
            for (ActivityLifecycleCallbacks m_activityLifecycleCallback : this.m_activityLifecycleCallbacks) {
                m_activityLifecycleCallback.onActivityCreated(activity, bundle);
            }
            this.notifyApplicationLaunch(activity.getIntent());
            this.m_createdActivityCount = 1;
            this.m_state = State.LAUNCH;
            if (this.m_runningActivityCount != 0) {
                Log.Helper.LOGE(this, "Invalid running acitivity count %d", this.m_runningActivityCount);
                this.m_runningActivityCount = 0;
            }
        } else if (this.m_state == State.CONFIG_CHANGE) {
            if (ApplicationEnvironment.getCurrentActivity() != activity) {
                Log.Helper.LOGE(this, "Activity created with state CONFIG_CHANGE but different activity %s and %s", ApplicationEnvironment.getCurrentActivity().getLocalClassName(), activity.getLocalClassName());
            } else {
                Log.Helper.LOGD(this, "Activity created from CONFIG_CHANGE, activity configuration changed");
            }
            if (RESTART_ON_CONFIG_CHANGE) {
                this.m_core.onApplicationResume();
            }
            Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onActivityCreated(activity, bundle);
            }
            if (this.m_runningActivityCount != 0) {
                Log.Helper.LOGE(this, "Invalid running acitivity count %d", this.m_runningActivityCount);
                this.m_runningActivityCount = 0;
            }
        } else if (this.m_state == State.PAUSE) {
            Log.Helper.LOGD(this, "Activity created from PAUSE, normal activity switch");
            for (ActivityLifecycleCallbacks m_activityLifecycleCallback : this.m_activityLifecycleCallbacks) {
                m_activityLifecycleCallback.onActivityCreated(activity, bundle);
            }
            ++this.m_createdActivityCount;
        } else if (this.m_state == State.SUSPEND) {
            Log.Helper.LOGD(this, "Activity created from SUSPEND, external activity switch; (new) app restart");
            this.m_core.onApplicationResume();
            this.m_state = State.RESUME;
            Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onActivityCreated(activity, bundle);
            }
            ++this.m_createdActivityCount;
            if (this.m_runningActivityCount != 0) {
                Log.Helper.LOGE(this, "Invalid running acitivity count %d", this.m_runningActivityCount);
                this.m_runningActivityCount = 0;
            }
        } else {
            Log.Helper.LOGE(this, "Activity created with %s state, shouldn't happen", this.m_state.toString());
        }
        Log.Helper.LOGV(this, "State after created %s (%d, %d)", this.m_state.toString(), this.m_createdActivityCount, this.m_runningActivityCount);
    }

    @Override
    public void notifyActivityDestroy(Activity activity) {
        Log.Helper.LOGV(this, "Activity %s DESTROY", activity.getLocalClassName());
        for (ActivityLifecycleCallbacks m_activityLifecycleCallback : this.m_activityLifecycleCallbacks) {
            m_activityLifecycleCallback.onActivityDestroyed(activity);
        }
        if (this.m_state != State.CONFIG_CHANGE) {
            if (this.m_state != State.SUSPEND && this.m_state != State.RUN) {
                Log.Helper.LOGE(this, "Activity destroy on invalid state %s", this.m_state.toString());
            }
            --this.m_createdActivityCount;
            if (this.m_createdActivityCount == 0) {
                this.m_state = State.QUIT;
                this.notifyApplicationQuit();
                this.m_core.onApplicationQuit();
            }
        }
        Log.Helper.LOGV(this, "State after destroy %s (%d, %d)", this.m_state.toString(), this.m_createdActivityCount, this.m_runningActivityCount);
    }

    @Override
    public void notifyActivityPause(Activity activity) {
        Log.Helper.LOGV(this, "Activity %s PAUSE", activity.getLocalClassName());
        Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onActivityPaused(activity);
        }
        if (this.m_state != State.RUN) {
            Log.Helper.LOGE(this, "Activity pause on invalid state %s", activity.getLocalClassName());
        }
        this.m_state = State.PAUSE;
        Log.Helper.LOGV(this, "State after pause %s (%d, %d)", this.m_state.toString(), this.m_createdActivityCount, this.m_runningActivityCount);
    }

    @Override
    public void notifyActivityRestart(Activity activity) {
        Log.Helper.LOGV(this, "Activity %s RESTART", activity.getLocalClassName());
        ApplicationEnvironment.setCurrentActivity(activity);
        if (this.m_state == State.PAUSE) {
            Log.Helper.LOGD(this, "Activity restart from PAUSE, normal activity switch");
        } else if (this.m_state == State.SUSPEND) {
            this.m_core.onApplicationResume();
            this.m_state = State.RESUME;
            Log.Helper.LOGD(this, "Activity restart from SUSPEND, external activity switch; (new) app restart");
        } else {
            Log.Helper.LOGE(this, "Activity restart with invalid state %s", this.m_state.toString());
        }
        Log.Helper.LOGV(this, "State after restart %s (%d, %d)", this.m_state.toString(), this.m_createdActivityCount, this.m_runningActivityCount);
    }

    @Override
    public void notifyActivityRestoreInstanceState(Bundle bundle, Activity activity) {
        Log.Helper.LOGV(this, "Activity %s RESTORE_STATE", activity.getLocalClassName());
    }

    @Override
    public void notifyActivityResult(int n2, int n3, Intent intent, Activity activity) {
        Iterator<IApplicationLifecycle.ActivityEventCallbacks> iterator = this.m_activityEventCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onActivityResult(activity, n2, n3, intent);
        }
    }

    @Override
    public void notifyActivityResume(Activity activity) {
        Log.Helper.LOGV(this, "Activity %s RESUME", activity.getLocalClassName());
        ApplicationEnvironment.setCurrentActivity(activity);
        Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onActivityResumed(activity);
        }
        if (this.m_state != State.PAUSE) {
            Log.Helper.LOGE(this, "Activity resume on invalid state %s", this.m_state.toString());
            Log.Helper.LOGE(this, "<NOTE>Please double check if the game's activity hooks ApplicationLifecycle.onActivityRestart() correctly.");
        }
        this.m_state = State.RUN;
        Log.Helper.LOGV(this, "State after resume %s (%d, %d)", this.m_state.toString(), this.m_createdActivityCount, this.m_runningActivityCount);
    }

    @Override
    public void notifyActivityRetainNonConfigurationInstance() {
        if (!RESTART_ON_CONFIG_CHANGE) return;
        if (this.m_state != State.SUSPEND) {
            Log.Helper.LOGW(this, "configuration change should happen between onStop() and onDestroy(), but state is %s", this.m_state.toString());
        }
        this.m_state = State.CONFIG_CHANGE;
    }

    @Override
    public void notifyActivitySaveInstanceState(Bundle bundle, Activity activity) {
        Log.Helper.LOGV(this, "Activity %s SAVE_STATE", activity.getLocalClassName());
        Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onActivitySaveInstanceState(activity, bundle);
        }
    }

    @Override
    public void notifyActivityStart(Activity activity) {
        Log.Helper.LOGV(this, "Activity %s START", activity.getLocalClassName());
        ApplicationEnvironment.setCurrentActivity(activity);
        if (this.m_state == State.LAUNCH) {
            this.m_state = State.PAUSE;
            Log.Helper.LOGD(this, "Activity start with LAUNCH state, normal app start");
        } else if (this.m_state == State.RESUME) {
            Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onActivityStarted(activity);
            }
            this.notifyApplicationResume();
            this.m_state = State.PAUSE;
            Log.Helper.LOGD(this, "Activity start with RESUME state, set to PAUSE");
        } else if (this.m_state == State.CONFIG_CHANGE) {
            Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onActivityStarted(activity);
            }
            if (RESTART_ON_CONFIG_CHANGE) {
                this.notifyApplicationResume();
            }
            this.m_state = State.PAUSE;
            Log.Helper.LOGD(this, "Activity start with CONFIG_CHANGE state, set to PAUSE");
        } else if (this.m_state == State.PAUSE) {
            Log.Helper.LOGD(this, "Activity start with PAUSE state, normal activity switch");
        } else {
            Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
            while (iterator.hasNext()) {
                iterator.next().onActivityStarted(activity);
            }
            Log.Helper.LOGE(this, "Activity start with invalid state %s", this.m_state.toString());
        }
        ++this.m_runningActivityCount;
        Log.Helper.LOGV(this, "State after start %s (%d, %d)", this.m_state.toString(), this.m_createdActivityCount, this.m_runningActivityCount);
    }

    @Override
    @SuppressLint(value={"NewApi"})
    public void notifyActivityStop(Activity activity) {
        Log.Helper.LOGV(this, "Activity %s STOP", activity.getLocalClassName());
        Iterator<IApplicationLifecycle.ActivityLifecycleCallbacks> iterator = this.m_activityLifecycleCallbacks.iterator();
        while (iterator.hasNext()) {
            iterator.next().onActivityStopped(activity);
        }
        --this.m_runningActivityCount;
        if (!RESTART_ON_CONFIG_CHANGE && activity.isChangingConfigurations()) {
            this.m_state = State.CONFIG_CHANGE;
        } else {
            if (this.m_runningActivityCount == 0) {
                if (this.m_state != State.PAUSE && this.m_state != State.SUSPEND) {
                    Log.Helper.LOGW(this, "Interesting case %s, HIGHLIGHT!!", new Object[]{this.m_state});
                }
                this.m_state = State.SUSPEND;
                if (!activity.isFinishing()) {
                    this.notifyApplicationSuspend();
                    this.m_core.onApplicationSuspend();
                }
            } else if (this.m_state == State.PAUSE) {
                this.m_state = State.SUSPEND;
                if (!activity.isFinishing()) {
                    Log.Helper.LOGW(this, "running activity count may be messed");
                    this.notifyApplicationSuspend();
                    this.m_core.onApplicationSuspend();
                }
            } else if (this.m_state != State.RUN) {
                Log.Helper.LOGE(this, "Activity stop on invalid state %s", this.m_state.toString());
            }
            if (ApplicationEnvironment.getCurrentActivity() == activity) {
                ApplicationEnvironment.setCurrentActivity(null);
            }
        }
        Log.Helper.LOGV(this, "State after stop %s (%d, %d)", this.m_state.toString(), this.m_createdActivityCount, this.m_runningActivityCount);
    }

    @Override
    public void notifyActivityWindowFocusChanged(boolean bl2, Activity object) {
    }

    @Override
    public void registerActivityEventCallbacks(IApplicationLifecycle.ActivityEventCallbacks activityEventCallbacks) {
        this.m_activityEventCallbacks.add(activityEventCallbacks);
    }

    @Override
    public void registerActivityLifecycleCallbacks(IApplicationLifecycle.ActivityLifecycleCallbacks activityLifecycleCallbacks) {
        this.m_activityLifecycleCallbacks.add(activityLifecycleCallbacks);
    }

    @Override
    public void registerApplicationLifecycleCallbacks(IApplicationLifecycle.ApplicationLifecycleCallbacks applicationLifecycleCallbacks) {
        this.m_applicationLifecycleCallbacks.add(applicationLifecycleCallbacks);
    }

    @Override
    protected void teardown() {
        this.m_activityLifecycleCallbacks.clear();
        this.m_activityEventCallbacks.clear();
        this.m_applicationLifecycleCallbacks.clear();
    }

    @Override
    public void unregisterActivityEventCallbacks(IApplicationLifecycle.ActivityEventCallbacks activityEventCallbacks) {
        this.m_activityEventCallbacks.remove(activityEventCallbacks);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(IApplicationLifecycle.ActivityLifecycleCallbacks activityLifecycleCallbacks) {
        this.m_activityLifecycleCallbacks.remove(activityLifecycleCallbacks);
    }

    @Override
    public void unregisterApplicationLifecycleCallbacks(IApplicationLifecycle.ApplicationLifecycleCallbacks applicationLifecycleCallbacks) {
        this.m_applicationLifecycleCallbacks.remove(applicationLifecycleCallbacks);
    }

    private static enum State {
        INIT,
        LAUNCH,
        RESUME,
        RUN,
        PAUSE,
        SUSPEND,
        QUIT,
        CONFIG_CHANGE;

    }
}

