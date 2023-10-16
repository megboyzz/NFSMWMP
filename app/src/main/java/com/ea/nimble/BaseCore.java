package com.ea.nimble;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.os.Looper;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

public class BaseCore implements IApplicationLifecycle.ApplicationLifecycleCallbacks {
    public static final String NIMBLE_COMPONENT_LIST = "setting::components";
    public static final String NIMBLE_LOG_SETTING = "setting::log";
    public static final String NIMBLE_SERVER_CONFIG = "com.ea.nimble.configuration";
    protected static BaseCore s_core;
    protected static boolean s_coreDestroyed = false;
    protected ApplicationEnvironmentImpl m_applicationEnvironment;
    protected IApplicationLifecycle m_applicationLifecycle;
    protected ComponentManager m_componentManager;
    protected NimbleConfiguration m_configuration;
    protected LogImpl m_log;
    protected PersistenceServiceImpl m_persistenceService;
    protected State m_state;

    public static class AnonymousClass2 {
        static final int[] $SwitchMap$com$ea$nimble$BaseCore$State = new int[State.values().length];

        static {
            try {
                $SwitchMap$com$ea$nimble$BaseCore$State[State.INACTIVE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$ea$nimble$BaseCore$State[State.MANUAL_TEARDOWN.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$ea$nimble$BaseCore$State[State.DESTROY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$ea$nimble$BaseCore$State[State.AUTO_SETUP.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$ea$nimble$BaseCore$State[State.MANUAL_SETUP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$ea$nimble$BaseCore$State[State.QUITTING.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/BaseCore$State.class */
    public enum State {
        INACTIVE,
        AUTO_SETUP,
        MANUAL_SETUP,
        MANUAL_TEARDOWN,
        QUITTING,
        DESTROY
    }

    BaseCore() {
    }

    private void destroy() {
        Log.Helper.LOGD(this, "NIMBLE DESTROY for Android will keep Core and Static components alive", new Object[0]);
    }

    public static BaseCore getInstance() {
        if (s_core == null) {
            if (s_coreDestroyed) {
                throw new AssertionError("Cannot revive destroyed BaseCore, please utilizesetupNimble() and tearDownNimble() explicitly to extend longevity to match your expectation.");
            }
            android.util.Log.d(Global.NIMBLE_ID, String.format("NIMBLE VERSION %s (Build %s)", "1.23.14.1217", "1.23.14.1217"));
            s_core = new BaseCore();
            s_core.initialize();
        }
        return s_core;
    }

    private void initialize() {
        this.m_state = State.INACTIVE;
        loadConfiguration();
        this.m_componentManager = new ComponentManager();
        this.m_applicationLifecycle = new ApplicationLifecycleImpl(this);
        this.m_applicationEnvironment = new ApplicationEnvironmentImpl(this);
        this.m_log = (LogImpl) Log.getComponent();
        this.m_log.connectToCore(this);
        this.m_persistenceService = new PersistenceServiceImpl();
        NetworkImpl networkImpl = new NetworkImpl();
        SynergyEnvironmentImpl synergyEnvironmentImpl = new SynergyEnvironmentImpl(this);
        SynergyNetworkImpl synergyNetworkImpl = new SynergyNetworkImpl();
        SynergyIdManagerImpl synergyIdManagerImpl = new SynergyIdManagerImpl();
        OperationalTelemetryDispatchImpl operationalTelemetryDispatchImpl = new OperationalTelemetryDispatchImpl();
        this.m_componentManager.registerComponent(this.m_applicationEnvironment, ApplicationEnvironment.COMPONENT_ID);
        this.m_componentManager.registerComponent(this.m_log, Log.COMPONENT_ID);
        this.m_componentManager.registerComponent(this.m_persistenceService, PersistenceService.COMPONENT_ID);
        this.m_componentManager.registerComponent(networkImpl, "com.ea.nimble.network");
        this.m_componentManager.registerComponent(synergyIdManagerImpl, SynergyIdManager.COMPONENT_ID);
        this.m_componentManager.registerComponent(synergyEnvironmentImpl, SynergyEnvironment.COMPONENT_ID);
        this.m_componentManager.registerComponent(synergyNetworkImpl, SynergyNetwork.COMPONENT_ID);
        this.m_componentManager.registerComponent(operationalTelemetryDispatchImpl, OperationalTelemetryDispatch.COMPONENT_ID);
        for (String str : getSettings(NIMBLE_COMPONENT_LIST).values()) {
            try {
                Method declaredMethod = Class.forName(str).getDeclaredMethod("initialize", new Class[0]);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(null, new Object[0]);
            } catch (ClassNotFoundException e) {
                Log.Helper.LOGD(this, "Component " + str + " not found", new Object[0]);
            } catch (IllegalAccessException e2) {
                Log.Helper.LOGE(this, "Method " + str + ".initialize() is not accessible", new Object[0]);
            } catch (IllegalArgumentException e3) {
                Log.Helper.LOGE(this, "Method " + str + ".initialize() should take no arguments", new Object[0]);
            } catch (NoSuchMethodException e4) {
                Log.Helper.LOGE(this, "No method " + str + ".initialize()", new Object[0]);
            } catch (NullPointerException e5) {
                Log.Helper.LOGE(this, "Method " + str + ".initialize() should be static", new Object[0]);
            } catch (InvocationTargetException e6) {
                Log.Helper.LOGE(this, "Method " + str + ".initialize() threw an exception", new Object[0]);
                e6.printStackTrace();
            }
        }
        try {
            if (!isAppSigned(ApplicationEnvironment.getComponent().getApplicationContext())) {
                android.util.Log.e(Global.NIMBLE_ID, "This application is NOT signed with a valid certificate. MTX may not work correctly with this application");
            } else {
                android.util.Log.i(Global.NIMBLE_ID, "This application is signed with a valid certificate.");
            }
        } catch (Exception e7) {
            android.util.Log.e(Global.NIMBLE_ID, String.format("Unable to verify application signature. Message: %s", e7.getMessage()));
        }
    }

    protected static void injectMock(BaseCore baseCore) {
        if (baseCore == null) {
            s_core = null;
            s_coreDestroyed = false;
            return;
        }
        s_core = baseCore;
        s_coreDestroyed = false;
    }

    private boolean isAppSigned(Context context) {
        return true;
    }

    private void loadConfiguration() {
        try {
            String string = ApplicationEnvironment.getCurrentActivity().getPackageManager().getApplicationInfo(ApplicationEnvironment.getCurrentActivity().getPackageName(), 128).metaData.getString(NIMBLE_SERVER_CONFIG);
            if (Utility.validString(string)) {
                this.m_configuration = NimbleConfiguration.fromName(string);
                if (this.m_configuration != NimbleConfiguration.UNKNOWN) {
                    if (this.m_configuration != NimbleConfiguration.CUSTOMIZED) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
        }
        android.util.Log.e(Global.NIMBLE_ID, "WARNING! Cannot find valid NimbleConfiguration from AndroidManifest.xml");
        this.m_configuration = NimbleConfiguration.LIVE;
    }

    public BaseCore activeValidate() {
        switch (AnonymousClass2.$SwitchMap$com$ea$nimble$BaseCore$State[this.m_state.ordinal()]) {
            case 1:
                Log.Helper.LOGF(this, "Access NimbleBaseCore before setup, call setupNimble() explicitly to activate it.", new Object[0]);
                return null;
            case 2:
                Log.Helper.LOGF(this, "Access NimbleBaseCore after clean up, call setupNimble() explicitly again to activate it.", new Object[0]);
                return null;
            case 3:
                Log.Helper.LOGF(this, "Accessing component after destroy, only static components are available right now.", new Object[0]);
                return null;
            default:
                return this;
        }
    }

    public IApplicationEnvironment getApplicationEnvironment() {
        return this.m_applicationEnvironment;
    }

    public IApplicationLifecycle getApplicationLifecycle() {
        return this.m_applicationLifecycle;
    }

    public ComponentManager getComponentManager() {
        return this.m_componentManager;
    }

    public NimbleConfiguration getConfiguration() {
        return this.m_configuration;
    }

    public ILog getLog() {
        return this.m_log;
    }

    public IPersistenceService getPersistenceService() {
        return this.m_persistenceService;
    }

    public Map<String, String> getSettings(String str) {
        int identifier;
        if (str == NIMBLE_LOG_SETTING) {
            int identifier2 = ApplicationEnvironment.getComponent().getApplicationContext().getResources().getIdentifier("nimble_log", "xml", ApplicationEnvironment.getCurrentActivity().getPackageName());
            if (identifier2 == 0) {
                return null;
            }
            return Utility.parseXmlFile(identifier2);
        } else if (str != NIMBLE_COMPONENT_LIST || (identifier = ApplicationEnvironment.getComponent().getApplicationContext().getResources().getIdentifier("components", "xml", ApplicationEnvironment.getCurrentActivity().getPackageName())) == 0) {
            return null;
        } else {
            return Utility.parseXmlFile(identifier);
        }
    }

    public boolean isActive() {
        return this.m_state == State.AUTO_SETUP || this.m_state == State.MANUAL_SETUP;
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ApplicationLifecycleCallbacks
    public void onApplicationLaunch(Intent intent) {
        if (this.m_state == State.INACTIVE || this.m_state == State.DESTROY) {
            this.m_componentManager.setup();
            Utility.sendBroadcast(Global.NOTIFICATION_COMPONENT_INDEPENDENT_SETUP_FINISHED, null);
            this.m_state = State.AUTO_SETUP;
            try {
                this.m_componentManager.restore();
            } catch (AssertionError e) {
                this.m_state = State.INACTIVE;
                throw e;
            }
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ApplicationLifecycleCallbacks
    public void onApplicationQuit() {
        switch (AnonymousClass2.$SwitchMap$com$ea$nimble$BaseCore$State[this.m_state.ordinal()]) {
            case 1:
                Log.Helper.LOGF(this, "No app start before app quit, something must be wrong.", new Object[0]);
                return;
            case 2:
            default:
                return;
            case 3:
            case 6:
                Log.Helper.LOGF(this, "Double app quit, something must be wrong.", new Object[0]);
                return;
            case 4:
                this.m_componentManager.suspend();
                return;
            case 5:
                this.m_componentManager.suspend();
                return;
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ApplicationLifecycleCallbacks
    public void onApplicationResume() {
        if (this.m_state == State.MANUAL_SETUP || this.m_state == State.AUTO_SETUP) {
            this.m_componentManager.resume();
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ApplicationLifecycleCallbacks
    public void onApplicationSuspend() {
        if (this.m_state == State.MANUAL_SETUP || this.m_state == State.AUTO_SETUP) {
            this.m_componentManager.suspend();
        }
    }

    public void restartWithConfiguration(final NimbleConfiguration nimbleConfiguration) {
        Log.Helper.LOGE(this, ">>>>>>>>>>>>>>>>>>>>>>", new Object[0]);
        Log.Helper.LOGE(this, "restartWithConfiguration should not be used in an integration. This function is for QA testing purposes.", new Object[0]);
        Log.Helper.LOGE(this, ">>>>>>>>>>>>>>>>>>>>>>", new Object[0]);
        if (nimbleConfiguration == NimbleConfiguration.UNKNOWN) {
            Log.Helper.LOGE(this, "Cannot restart nimble with unknown configuration", new Object[0]);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.ea.nimble.BaseCore.1
                /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
                @Override // java.lang.Runnable
                public void run() {
                    switch (AnonymousClass2.$SwitchMap$com$ea$nimble$BaseCore$State[BaseCore.this.m_state.ordinal()]) {
                        case 1:
                        case 2:
                        case 3:
                            Log.Helper.LOGF(this, "Should not happen, getInstance should ensure active instance", new Object[0]);
                            break;
                        case 4:
                        case 5:
                            BaseCore.this.m_componentManager.cleanup();
                            BaseCore.this.m_componentManager.teardown();
                            BaseCore.this.m_configuration = nimbleConfiguration;
                            BaseCore.this.m_componentManager.setup();
                            Utility.sendBroadcast(Global.NOTIFICATION_COMPONENT_INDEPENDENT_SETUP_FINISHED, null);
                            BaseCore.this.m_componentManager.restore();
                            return;
                        case 6:
                            break;
                        default:
                            return;
                    }
                    Log.Helper.LOGF(this, "Cannot restart Nimble when app is quiting", new Object[0]);
                }
            });
        }
    }

    public void setup() {
        switch (AnonymousClass2.$SwitchMap$com$ea$nimble$BaseCore$State[this.m_state.ordinal()]) {
            case 1:
            case 2:
                this.m_componentManager.setup();
                this.m_state = State.MANUAL_SETUP;
                Utility.sendBroadcast(Global.NOTIFICATION_COMPONENT_INDEPENDENT_SETUP_FINISHED, null);
                this.m_componentManager.restore();
                return;
            case 3:
            case 5:
            case 6:
                Log.Helper.LOGF(this, "Multiple setupNimble() calls without teardownNimble().", new Object[0]);
                return;
            case 4:
                this.m_state = State.MANUAL_SETUP;
                return;
            default:
                return;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void teardown() {
        switch (AnonymousClass2.$SwitchMap$com$ea$nimble$BaseCore$State[this.m_state.ordinal()]) {
            case 1:
            case 4:
                Log.Helper.LOGF(this, "Cannot teardownNimble() before setupNimble().", new Object[0]);
                break;
            case 2:
            case 3:
                break;
            case 5:
                this.m_componentManager.cleanup();
                this.m_state = State.MANUAL_TEARDOWN;
                this.m_componentManager.teardown();
                return;
            case 6:
                this.m_state = State.DESTROY;
                destroy();
                return;
            default:
                return;
        }
        Log.Helper.LOGF(this, "Multiple teardownNimble() calls without setupNibmle().", new Object[0]);
    }
}