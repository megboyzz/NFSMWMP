/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.ActivityManager
 *  android.app.ActivityManager$MemoryInfo
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.util.Log
 */
package com.ea.nimble.tracking;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.Component;
import com.ea.nimble.EASPDataLoader;
import com.ea.nimble.IHttpResponse;
import com.ea.nimble.Log.Helper;
import com.ea.nimble.LogSource;
import com.ea.nimble.Network;
import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.SynergyNetwork;
import com.ea.nimble.SynergyNetworkConnectionCallback;
import com.ea.nimble.SynergyNetworkConnectionHandle;
import com.ea.nimble.SynergyRequest;
import com.ea.nimble.Utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

abstract class NimbleTrackingImplBase
extends Component
implements LogSource,
ITracking {
    private static final int DATA_VERSION_CURRENT = 3;
    private static final int DEFAULT_MAX_QUEUE_LENGTH = 3;
    protected static final double DEFAULT_POST_INTERVAL = 1.0;
    protected static final double DEFAULT_REPOST_MULTIPLIER = 2.0;
    protected static final double DEFAULT_RETRY_DELAY = 1.0;
    protected static final double MAX_POST_RETRY_DELAY = 300.0;
    private static final int MAX_QUEUED_EVENTS = 50;
    private static final int MAX_QUEUED_SESSIONS = 50;
    protected static final double NOW_POST_INTERVAL = 0.0;
    private static final String ORIGIN_LOGIN_STATUS_STRING_AUTO_LOGGING_IN = "autoLogin";
    private static final String ORIGIN_LOGIN_STATUS_STRING_LIVE_USER = "live";
    private static final String ORIGIN_NOTIFICATION_LOGIN_STATUS_UPDATE_KEY_STATUS = "STATUS";
    private static final String PERSISTENCE_CURRENT_SESSION_ID = "currentSessionObject";
    private static final String PERSISTENCE_ENABLE_FLAG = "trackingEnabledFlag";
    private static final String PERSISTENCE_EVENT_QUEUE_ID = "eventQueue";
    private static final String PERSISTENCE_FIRST_SESSION_ID_NUMBER = "firstSessionIDNumber";
    private static final String PERSISTENCE_LAST_SESSION_ID_NUMBER = "lastSessionIDNumber";
    private static final String PERSISTENCE_LOGGED_IN_TO_ORIGIN_ID = "loggedInToOrigin";
    private static final String PERSISTENCE_QUEUED_SESSIONS_ID = "queuedSessionObjects";
    private static final String PERSISTENCE_SAVED_SESSION_ID_NUMBER = "savedSession";
    private static final String PERSISTENCE_SESSION_DATA_ID = "sessionData";
    private static final String PERSISTENCE_TOTAL_SESSION_COUNT = "totalSessionCount";
    private static final String PERSISTENCE_TRACKING_ATTRIBUTES = "trackingAttributes";
    private static final String PERSISTENCE_VERSION_ID = "dataVersion";
    private static final String SESSION_FILE_FORMAT = "%sSession%d";
    protected TrackingBaseSessionObject m_currentSessionObject;
    protected ArrayList<SessionData> m_customSessionData;
    private boolean m_enable = true;
    private long m_firstSessionIDNumber = 0L;
    private boolean m_isPostPending = false;
    private boolean m_isRequestInProgress = false;
    private long m_lastSessionIDNumber = -1L;
    protected boolean m_loggedInToOrigin = false;
    private int m_maxQueueLength = 3;
    private BroadcastReceiver m_networkStatusChangedReceiver = null;
    private OriginLoginStatusChangedReceiver m_originLoginStatusChangedReceiver = null;
    private ArrayList<Map<String, String>> m_pendingEvents;
    private double m_postInterval = 1.0;
    protected double m_postRetryDelay;
    private ScheduledFuture<?> m_postTimer;
    private StartupRequestsFinishedReceiver m_receiver = null;
    private ArrayList<TrackingBaseSessionObject> m_sessionsToPost = new ArrayList();
    protected NimbleTrackingThreadManager m_threadManager;
    private long m_totalSessions = 0L;
    protected HashMap<String, String> m_trackingAttributes;

    NimbleTrackingImplBase() {
        this.m_pendingEvents = new ArrayList();
        this.m_customSessionData = new ArrayList();
        this.m_currentSessionObject = new TrackingBaseSessionObject();
        this.m_trackingAttributes = new HashMap();
    }

    private void addCurrentSessionObjectToBackOfQueue() {
        ++this.m_lastSessionIDNumber;
        ++this.m_totalSessions;
        if (this.m_sessionsToPost.size() >= this.m_maxQueueLength) {
            this.saveSessionToFile(this.m_currentSessionObject, this.m_lastSessionIDNumber);
        } else {
            this.m_sessionsToPost.add(this.m_currentSessionObject);
        }
        this.saveToPersistence();
    }

    private void configureTrackingOnFirstInstall() {
        Helper.LOGD(this, "First Install. Look for App Settings to enable/disable tracking");
        try {
            String string2 = ApplicationEnvironment.getCurrentActivity().getPackageManager().getApplicationInfo((String)ApplicationEnvironment.getCurrentActivity().getPackageName(), (int)128).metaData.getString("com.ea.nimble.tracking.defaultEnable");
            if (Utility.validString(string2)) {
                if (string2.equalsIgnoreCase("enable")) {
                    Helper.LOGD(this, "Default App Setting : Enable Tracking");
                    this.m_enable = true;
                    return;
                }
                if (!string2.equalsIgnoreCase("disable")) return;
                Helper.LOGD(this, "Default App Setting : Disable Tracking");
                this.m_enable = false;
                return;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        Log.e((String)"Nimble", (String)"WARNING! Cannot find valid TrackingEnable from AndroidManifest.xml");
    }

    private void dropExtraSessions() {
        if (this.dropExtraSessions(true)) return;
        Helper.LOGD(this, "Failed to drop enough sessions. Dropping sessions without checking canDropSession.");
        if (this.dropExtraSessions(false)) return;
        Helper.LOGE(this, "Still unable to drop enough sessions. Remaining number: " + (this.m_lastSessionIDNumber - this.m_firstSessionIDNumber + 1L));
    }

    private boolean dropExtraSessions(boolean bl2) {
        long l2;
        if (this.m_totalSessions < 50L) {
            return true;
        }
        Helper.LOGD(this, "Current number of sessions (%d) has reached maximum (%d). Removing old sessions.", this.m_totalSessions, 50);
        ArrayList<TrackingBaseSessionObject> arrayList = new ArrayList<TrackingBaseSessionObject>();
        for (l2 = this.m_firstSessionIDNumber; l2 <= this.m_lastSessionIDNumber; ++l2) {
            TrackingBaseSessionObject trackingBaseSessionObject;
            long l3 = l2 - this.m_firstSessionIDNumber;
            if (l3 < (long)this.m_sessionsToPost.size()) {
                trackingBaseSessionObject = this.m_sessionsToPost.get((int)l3);
            } else {
                TrackingBaseSessionObject trackingBaseSessionObject2;
                trackingBaseSessionObject = trackingBaseSessionObject2 = this.loadSessionFromFile(l2);
                if (trackingBaseSessionObject2 == null) continue;
            }
            if (arrayList.size() == 0 || this.isSameSession(arrayList.get(arrayList.size() - 1), trackingBaseSessionObject)) {
                arrayList.add(trackingBaseSessionObject);
                continue;
            }
            if (!bl2 || this.canDropSession(arrayList)) {
                this.dropSessions(arrayList, l2 - 1L);
            }
            arrayList.clear();
            if (this.m_totalSessions < 50L) {
                this.fillSessionsToPost();
                return true;
            }
            arrayList.add(trackingBaseSessionObject);
        }
        if (arrayList.size() > 0 && (!bl2 || this.canDropSession(arrayList))) {
            this.dropSessions(arrayList, l2 - 1L);
        }
        this.fillSessionsToPost();
        if (this.m_totalSessions >= 50L) return false;
        return true;
    }

    private void dropSessions(ArrayList<TrackingBaseSessionObject> arrayList, long l2) {
        for (int i2 = 0; i2 < arrayList.size(); ++i2) {
            this.m_sessionsToPost.remove(arrayList.get(i2));
            PersistenceService.removePersistenceForNimbleComponent(this.getFilenameForSessionID(l2 - (long)i2), Persistence.Storage.DOCUMENT);
        }
        if (l2 - (long)arrayList.size() + 1L == this.m_firstSessionIDNumber) {
            this.m_firstSessionIDNumber += (long)arrayList.size();
        }
        this.m_totalSessions -= (long)arrayList.size();
        this.saveToPersistence();
    }

    private void fillSessionsToPost() {
        int n2 = this.m_sessionsToPost.size();
        while (n2 < this.m_maxQueueLength) {
            long l2 = this.m_firstSessionIDNumber + (long)n2;
            if (l2 > this.m_lastSessionIDNumber) {
                return;
            }
            TrackingBaseSessionObject trackingBaseSessionObject = this.loadSessionFromFile(l2);
            if (trackingBaseSessionObject != null) {
                this.m_sessionsToPost.add(trackingBaseSessionObject);
                PersistenceService.removePersistenceForNimbleComponent(this.getFilenameForSessionID(l2), Persistence.Storage.DOCUMENT);
            } else {
                ++this.m_firstSessionIDNumber;
            }
            ++n2;
        }
    }

    private String getFilenameForSessionID(long l2) {
        if (l2 >= 0L) return String.format(Locale.US, SESSION_FILE_FORMAT, this.getComponentId(), l2);
        Helper.LOGE(this, "Trying to find the filename for an invalid sessionID!");
        return null;
    }

    private boolean isAbleToPostEvent(boolean bl2) {
        if (!this.m_enable) {
            return false;
        }
        if (!(bl2 || ApplicationEnvironment.isMainApplicationRunning() && ApplicationEnvironment.getCurrentActivity() != null)) {
            Helper.LOGD(this, "isAbleToPostEvent - return because the app is in background");
            return false;
        }
        if (Network.getComponent().getStatus() != Network.Status.OK) {
            if (this.m_networkStatusChangedReceiver != null) return false;
            Helper.LOGD(this, "Network status not OK for event post. Adding receiver for network status change.");
            this.killPostTimer();
            this.m_networkStatusChangedReceiver = new BroadcastReceiver(){

                public void onReceive(Context context, Intent intent) {
                    if (!intent.getAction().equals("nimble.notification.networkStatusChanged")) return;
                    NimbleTrackingImplBase.this.m_threadManager.runInWorkerThread(new Runnable(){

                        @Override
                        public void run() {
                            NimbleTrackingImplBase.this.onNetworkStatusChange();
                        }
                    });
                }
            };
            Utility.registerReceiver("nimble.notification.networkStatusChanged", this.m_networkStatusChangedReceiver);
            return false;
        }
        if (SynergyEnvironment.getComponent().isDataAvailable()) return true;
        this.m_isPostPending = true;
        this.addObserverForSynergyEnvironmentUpdateFinished();
        return false;
    }

    private void killPostTimer() {
        if (this.m_postTimer == null) return;
        this.m_postTimer.cancel(false);
        this.m_postTimer = null;
    }

    private TrackingBaseSessionObject loadSessionFromFile(long l2) {
        Object object = PersistenceService.getPersistenceForNimbleComponent(this.getFilenameForSessionID(l2), Persistence.Storage.DOCUMENT);
        if (object == null) return null;
        if ((object = ((Persistence)object).getValue(PERSISTENCE_SAVED_SESSION_ID_NUMBER)) == null) return null;
        if (object.getClass() != TrackingBaseSessionObject.class) return null;
        return (TrackingBaseSessionObject)object;
    }

    private void logEvent(Tracking.Event iterator, boolean bl2) {
        List<Map<String, String>> maps = this.convertEvent((Tracking.Event) ((Object) iterator));
        if (maps != null && !maps.isEmpty()) {
            for (Map map : maps) {
                this.m_currentSessionObject.events.add(map);
                Helper.LOGD(this, "Logged event, %s: \n", map);
            }
            this.saveToPersistence();
            if (!bl2 && (this.m_postTimer == null || this.m_postTimer.isDone() && !this.m_isRequestInProgress) && this.isAbleToPostEvent(false)) {
                this.resetPostTimer();
            }
        }
        boolean bl3 = bl2;
        if (this.m_currentSessionObject.events.size() >= 50) {
            Helper.LOGD(this, "Current number of events (%d) has reached maximum (%d). Posting event queue now.", this.m_currentSessionObject.events.size(), 50);
            bl3 = true;
        }
        if (!bl3) return;
        this.killPostTimer();
        this.packageCurrentSession();
        this.postPendingEvents(bl2);
    }

    private void onNetworkStatusChange() {
        if (Network.getComponent().getStatus() != Network.Status.OK) return;
        Helper.LOGD(this, "Network status restored, kicking off event post.");
        Utility.unregisterReceiver(this.m_networkStatusChangedReceiver);
        this.m_networkStatusChangedReceiver = null;
        this.resetPostTimer(0.0);
    }

    private void onOriginLoginStatusChanged(Intent object) {
        if (object.getExtras() == null) {
            Helper.LOGI(this, "Login status updated event received without extras bundle. Marking NOT logged in to Origin.");
            this.m_loggedInToOrigin = false;
            return;
        }
        if (!(object.getExtras().getString(ORIGIN_NOTIFICATION_LOGIN_STATUS_UPDATE_KEY_STATUS)).equals(ORIGIN_LOGIN_STATUS_STRING_LIVE_USER) && !object.equals(ORIGIN_LOGIN_STATUS_STRING_AUTO_LOGGING_IN)) {
            Helper.LOGI(this, "Login status update, FALSE");
            this.m_loggedInToOrigin = false;
            return;
        }
        Helper.LOGI(this, "Login status update, TRUE");
        this.m_loggedInToOrigin = true;
    }

    private void onPostComplete(SynergyNetworkConnectionHandle synergyNetworkConnectionHandle, TrackingBaseSessionObject trackingBaseSessionObject) {
        if (synergyNetworkConnectionHandle == null || synergyNetworkConnectionHandle.getResponse() == null) {
            Helper.LOGE(this, "No response exists in this post!");
            return;
        }
        boolean bl2 = false;
        double d2 = 1.0;
        if (synergyNetworkConnectionHandle.getResponse().getError() == null) {
            this.removeSessionAndFillQueue(trackingBaseSessionObject);
            this.m_postRetryDelay = 1.0;
            d2 = this.m_postInterval;
        } else {
            IHttpResponse iHttpResponse = synergyNetworkConnectionHandle.getResponse().getHttpResponse();
            if (iHttpResponse != null && (iHttpResponse.getStatusCode() == 400 || iHttpResponse.getStatusCode() == 415)) {
                Helper.LOGE(this, "Received HTTP status %d. Discarding post.", iHttpResponse.getStatusCode());
                this.removeSessionAndFillQueue(trackingBaseSessionObject);
                this.m_postRetryDelay = 1.0;
                d2 = this.m_postInterval;
            } else {
                Helper.LOGE(this, "Failed to send tracking events. Error: %s", synergyNetworkConnectionHandle.getResponse().getError().getLocalizedMessage());
                bl2 = true;
            }
        }
        Helper.LOGI(this, "Telemetry post request finished, resetting isRequestInProgress flag to false.");
        this.m_isRequestInProgress = false;
        if (bl2) {
            d2 = this.m_postRetryDelay;
            this.m_postRetryDelay *= 2.0;
            if (this.m_postRetryDelay > 300.0) {
                this.m_postRetryDelay = 300.0;
            }
            Helper.LOGI(this, "Posting a retry with delay of %s due to failed send. Queue size: %d", d2, this.m_sessionsToPost.size());
            this.resetPostTimer(d2, false);
            return;
        }
        if (this.m_sessionsToPost != null && !this.m_sessionsToPost.isEmpty()) {
            Helper.LOGI(this, "More items found in the queue. Post the next one now. Queue size: %d", this.m_sessionsToPost.size());
            this.resetPostTimer(0.0, false);
            return;
        }
        Helper.LOGI(this, "No more items found in the queue. Wait on the timer. Queue size: %d", this.m_sessionsToPost.size());
        this.resetPostTimer(d2, true);
    }

    private void onStartupRequestsFinished(Intent object) {
        if (object.getExtras() == null) return;
        if (!object.getExtras().getString("result").equals("1")) return;
        int n2 = SynergyEnvironment.getComponent().getTrackingPostInterval();
        this.m_postInterval = n2 < 0 || n2 == -1 ? 1.0 : (double)n2;
        if (this.m_sessionsToPost != null) {
            for (TrackingBaseSessionObject trackingBaseSessionObject : this.m_sessionsToPost) {
                if (trackingBaseSessionObject == null || trackingBaseSessionObject.sessionData == null) continue;
                Object object2 = trackingBaseSessionObject.sessionData.get("sellId");
                if (object2 != null && object2 instanceof String && (((String)object2).equals("") || ((String)object2).equals("0"))) {
                    object2 = SynergyEnvironment.getComponent().getSellId();
                    trackingBaseSessionObject.sessionData.put("sellId", Utility.safeString((String)object2));
                    if (object2 == null || ((String)object2).equals("") || ((String)object2).equals("0")) {
                        Helper.LOGE(this, "Sell Id was still null after synergy update");
                    }
                }
                if ((object2 = trackingBaseSessionObject.sessionData.get("hwId")) != null && object2 instanceof String && ((String)object2).equals("")) {
                    object2 = SynergyEnvironment.getComponent().getEAHardwareId();
                    trackingBaseSessionObject.sessionData.put("hwId", Utility.safeString((String)object2));
                    if (object2 == null || ((String)object2).equals("")) {
                        Helper.LOGE(this, "Hardware Id was still null after synergy update");
                    }
                }
                if ((object2 = trackingBaseSessionObject.sessionData.get("deviceId")) == null || !(object2 instanceof String) || !((String)object2).equals("") && !((String)object2).equals("0")) continue;
                object2 = SynergyEnvironment.getComponent().getEADeviceId();
                trackingBaseSessionObject.sessionData.put("deviceId", Utility.safeString((String)object2));
                if (object2 != null && !((String)object2).equals("") && !((String)object2).equals("0")) continue;
                Helper.LOGE(this, "Device Id was still null after synergy update");
            }
        }
        Helper.LOGI(this, "Synergy environment update successful. Removing observer and re-attempting event post.");
        if (this.m_receiver != null) {
            Utility.unregisterReceiver(this.m_receiver);
            this.m_receiver = null;
        }
        if (!this.m_isPostPending) return;
        this.m_isPostPending = false;
        this.resetPostTimer(0.0);
    }

    private void postIntervalTimerExpired(boolean bl2) {
        if (bl2) {
            this.packageCurrentSession();
        }
        this.postPendingEvents(false);
    }

    private void postPendingEvents(boolean bl2) {
        if (!this.isAbleToPostEvent(bl2)) {
            return;
        }
        if (this.m_sessionsToPost == null || this.m_sessionsToPost.size() <= 0) {
            Helper.LOGD(this, "No tracking sessions to post.");
            return;
        }
        TrackingBaseSessionObject trackingBaseSessionObject = this.m_sessionsToPost.get(0);
        while (trackingBaseSessionObject == null) {
            this.removeSessionAndFillQueue(null);
            if (this.m_sessionsToPost.size() <= 0) {
                Helper.LOGD(this, "No valid tracking sessions to post.");
                return;
            }
            trackingBaseSessionObject = this.m_sessionsToPost.get(0);
        }
        SynergyRequest synergyRequest = this.createPostRequest(trackingBaseSessionObject);
        if (synergyRequest == null) return;
        synergyRequest.httpRequest.runInBackground = bl2;
        Helper.LOGD(this, "Event queue marshalled. Incrementing repost count from %d to %d", trackingBaseSessionObject.repostCount, trackingBaseSessionObject.repostCount + 1);
        ++trackingBaseSessionObject.repostCount;
        this.m_isRequestInProgress = true;
        final NimbleTrackingThreadManager nimbleTrackingThreadManager = NimbleTrackingThreadManager.acquireInstance();
        try {
            TrackingBaseSessionObject finalTrackingBaseSessionObject = trackingBaseSessionObject;
            SynergyNetwork.getComponent().sendRequest(synergyRequest, new SynergyNetworkConnectionCallback(){

                @Override
                public void callback(final SynergyNetworkConnectionHandle synergyNetworkConnectionHandle) {
                    nimbleTrackingThreadManager.runInWorkerThread(new Runnable(){

                        @Override
                        public void run() {
                            NimbleTrackingImplBase.this.onPostComplete(synergyNetworkConnectionHandle, finalTrackingBaseSessionObject);
                        }
                    });
                    NimbleTrackingThreadManager.releaseInstance();
                }
            });
            return;
        }
        catch (OutOfMemoryError outOfMemoryError) {
            Activity activity = ApplicationEnvironment.getCurrentActivity();
            if (activity != null) {
                ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
                ((ActivityManager)activity.getSystemService(Activity.ACTIVITY_SERVICE)).getMemoryInfo(memoryInfo);
                long l2 = memoryInfo.availMem / 0x100000L;
                Helper.LOGI(this, "OutOfMemoryError with " + l2 + " MB left. Dropping current session");
            } else {
                Helper.LOGI(this, "Out of memory. Dropping current session");
            }
            NimbleTrackingThreadManager.releaseInstance();
            double d2 = this.m_postInterval;
            this.removeSessionAndFillQueue(trackingBaseSessionObject);
            this.m_postRetryDelay = 1.0;
            this.m_isRequestInProgress = false;
            if (this.m_sessionsToPost != null && !this.m_sessionsToPost.isEmpty()) {
                Helper.LOGI(this, "More items found in the queue. Post the next one now. Queue size: %d", this.m_sessionsToPost.size());
                this.resetPostTimer(0.0, false);
                return;
            }
            Helper.LOGI(this, "No more items found in the queue. Wait on the timer. Queue size: %d", this.m_sessionsToPost.size());
            this.resetPostTimer(d2, true);
            return;
        }
    }

    private void removeSessionAndFillQueue(TrackingBaseSessionObject trackingBaseSessionObject) {
        this.m_sessionsToPost.remove(trackingBaseSessionObject);
        ++this.m_firstSessionIDNumber;
        --this.m_totalSessions;
        this.fillSessionsToPost();
        this.saveToPersistence();
    }

    private void resetPostTimer() {
        this.resetPostTimer(this.m_postInterval);
    }

    private void resetPostTimer(double d2, boolean bl2) {
        double d3;
        double d4 = d3 = d2;
        if (d3 < 0.0) {
            Helper.LOGE(this, "resetPostTimer called with an invalid period: period < 0.0. Timer reset with period 0.0 instead");
            d4 = 0.0;
        }
        Helper.LOGI(this, "Resetting event post timer for %s seconds.", d2);
        this.killPostTimer();
        this.m_postTimer = this.m_threadManager.createTimer(d4, new PostTask(bl2));
    }

    private void saveSessionDataToPersistent() {
        Persistence persistence = PersistenceService.getPersistenceForNimbleComponent(this.getComponentId(), Persistence.Storage.CACHE);
        Helper.LOGI(this, "Saving event queue to persistence.");
        persistence.setValue(PERSISTENCE_SESSION_DATA_ID, this.m_customSessionData);
        persistence.synchronize();
    }

    /*
     * Unable to fully structure code
     */
    private void saveSessionToFile(TrackingBaseSessionObject var1_1, long var2_3) {
        String var4_4 = this.getFilenameForSessionID(var2_3);
        Persistence var5_5 = PersistenceService.getPersistenceForNimbleComponent(var4_4, Persistence.Storage.DOCUMENT);
        if (var5_5.getBackUp()) {
            var5_5.setBackUp(false);
        }
        try {
            var5_5.setValue("savedSession", var1_1);
            var5_5.synchronize();
        }
        catch (OutOfMemoryError var1_2) {
            Helper.LOGE(this, "OutOfMemoryError occurred while saving a session object to file. Exception: %s", var1_2.getLocalizedMessage());
        }
        PersistenceService.cleanReferenceToPersistence(var4_4, Persistence.Storage.DOCUMENT);
    }

    /*
     * Exception decompiling
     */
    private void saveToPersistence() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 3[CATCHBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         *     at the.bytecode.club.bytecodeviewer.decompilers.impl.CFRDecompiler.decompileToZip(CFRDecompiler.java:306)
         *     at the.bytecode.club.bytecodeviewer.resources.ResourceDecompiling.lambda$null$1(ResourceDecompiling.java:114)
         *     at the.bytecode.club.bytecodeviewer.resources.ResourceDecompiling$$Lambda$144/691390785.run(Unknown Source)
         *     at java.lang.Thread.run(Unknown Source)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    public void addCustomSessionData(String string2, String string3) {
        if (!Utility.validString(string2)) return;
        if (!Utility.validString(string3)) {
            return;
        }
        SessionData sessionData = new SessionData();
        sessionData.key = string2;
        sessionData.value = string3;
        this.m_customSessionData.add(sessionData);
        this.saveSessionDataToPersistent();
    }

    protected void addObserverForSynergyEnvironmentUpdateFinished() {
        if (this.m_receiver != null) return;
        this.m_receiver = new StartupRequestsFinishedReceiver();
        Utility.registerReceiver("nimble.environment.notification.startup_requests_finished", this.m_receiver);
    }

    protected boolean canDropSession(List<TrackingBaseSessionObject> list) {
        return true;
    }

    @Override
    protected void cleanup() {
        this.killPostTimer();
        EASPDataLoader.deleteDatFile(EASPDataLoader.getTrackingDatFilePath());
    }

    @Override
    public void clearCustomSessionData() {
        this.m_customSessionData.clear();
        this.saveSessionDataToPersistent();
    }

    protected abstract List<Map<String, String>> convertEvent(Tracking.Event var1);

    protected abstract SynergyRequest createPostRequest(TrackingBaseSessionObject var1);

    @Override
    public boolean getEnable() {
        return this.m_enable;
    }

    @Override
    public String getLogSourceTitle() {
        return "TrackingBase";
    }

    protected abstract String getPersistenceIdentifier();

    protected boolean isSameSession(TrackingBaseSessionObject trackingBaseSessionObject, TrackingBaseSessionObject trackingBaseSessionObject2) {
        return false;
    }

    @Override
    public void logEvent(String string2, Map<String, String> map) {
        if (!this.m_enable) {
            return;
        }
        boolean bl2 = false;
        if (string2.equals("NIMBLESTANDARD::SESSION_END")) {
            Helper.LOGD(this, "Logging session end event, " + string2 + ". Posting event queue now.");
            bl2 = true;
        }
        Tracking.Event event = new Tracking.Event();
        event.type = string2;
        event.parameters = map;
        event.timestamp = new Date();
        this.logEvent(event, bl2);
    }

    protected abstract void packageCurrentSession();

    protected void queueCurrentEventsForPost() {
        Helper.LOGI(this, "queueCurrentEventsForPost called. Starting queue size: %d", this.m_sessionsToPost.size());
        if (this.m_sessionsToPost == null) {
            this.m_sessionsToPost = new ArrayList();
        }
        if (this.m_currentSessionObject == null) {
            Helper.LOGE(this, "Unexpected state, currentSessionObject is null.");
        } else if (this.m_currentSessionObject.countOfEvents() == 0) {
            Helper.LOGE(this, "Unexpected state, currentSessionObject events list is null or empty.");
        } else {
            this.addCurrentSessionObjectToBackOfQueue();
            this.dropExtraSessions();
        }
        this.m_currentSessionObject = new TrackingBaseSessionObject(new HashMap<String, Object>(this.m_currentSessionObject.sessionData));
    }

    protected void resetPostTimer(double d2) {
        this.resetPostTimer(d2, true);
    }

    /*
     * Exception decompiling
     */
    @Override
    protected void restore() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         *     at the.bytecode.club.bytecodeviewer.decompilers.impl.CFRDecompiler.decompileToZip(CFRDecompiler.java:306)
         *     at the.bytecode.club.bytecodeviewer.resources.ResourceDecompiling.lambda$null$1(ResourceDecompiling.java:114)
         *     at the.bytecode.club.bytecodeviewer.resources.ResourceDecompiling$$Lambda$144/691390785.run(Unknown Source)
         *     at java.lang.Thread.run(Unknown Source)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    protected void resume() {
        if (this.getEnable()) {
            this.resetPostTimer();
        }
        if (this.m_originLoginStatusChangedReceiver == null) {
            this.m_originLoginStatusChangedReceiver = new OriginLoginStatusChangedReceiver();
            Utility.registerReceiver("nimble.notification.LoginStatusChanged", this.m_originLoginStatusChangedReceiver);
        }
        this.m_postRetryDelay = 1.0;
    }

    @Override
    public void setEnable(boolean bl2) {
        Object object = bl2 ? "ENABLED" : "DISABLED";
        Helper.LOGI(this, "setEnable called. enable = %s", object);
        if (this.m_enable == bl2) {
            return;
        }
        if (!bl2) {
            object = new HashMap();
            ((HashMap)object).put("eventType", "NIMBLESTANDARD::USER_TRACKING_OPTOUT");
            this.logEvent("NIMBLESTANDARD::USER_TRACKING_OPTOUT", (Map<String, String>)object);
            this.packageCurrentSession();
            this.postPendingEvents(false);
            if (this.m_currentSessionObject.countOfEvents() > 0) {
                Helper.LOGI(this, "Removing %d remaining events that couldn't be sent from queue.", this.m_currentSessionObject.countOfEvents());
            }
            this.m_currentSessionObject = new TrackingBaseSessionObject();
            if (this.m_sessionsToPost != null && this.m_sessionsToPost.size() > 0) {
                Helper.LOGI(this, "Removing unposted sessions.");
                this.m_sessionsToPost.clear();
            }
            this.killPostTimer();
        } else {
            this.resetPostTimer();
        }
        this.m_enable = bl2;
        this.saveToPersistence();
    }

    @Override
    public void setTrackingAttribute(String string2, String string3) {
        if (!Utility.validString(string2)) return;
        if (!Utility.validString(string3)) return;
        this.m_trackingAttributes.put(string2, string3);
    }

    @Override
    protected void setup() {
        this.m_postRetryDelay = 1.0;
        this.m_threadManager = NimbleTrackingThreadManager.acquireInstance();
    }

    @Override
    protected void suspend() {
        if (this.m_networkStatusChangedReceiver != null) {
            Utility.unregisterReceiver(this.m_networkStatusChangedReceiver);
            this.m_networkStatusChangedReceiver = null;
        }
        if (this.m_originLoginStatusChangedReceiver != null) {
            Utility.unregisterReceiver(this.m_originLoginStatusChangedReceiver);
            this.m_originLoginStatusChangedReceiver = null;
        }
        this.killPostTimer();
        this.saveToPersistence();
        EASPDataLoader.deleteDatFile(EASPDataLoader.getTrackingDatFilePath());
    }

    @Override
    protected void teardown() {
        NimbleTrackingThreadManager.releaseInstance();
        this.m_threadManager = null;
    }

    private class OriginLoginStatusChangedReceiver
    extends BroadcastReceiver {
        private OriginLoginStatusChangedReceiver() {
        }

        public void onReceive(Context context, final Intent intent) {
            if (!intent.getAction().equals("nimble.notification.LoginStatusChanged")) return;
            NimbleTrackingImplBase.this.m_threadManager.runInWorkerThread(new Runnable(){

                @Override
                public void run() {
                    NimbleTrackingImplBase.this.onOriginLoginStatusChanged(intent);
                }
            });
        }
    }

    private class PostTask
    implements Runnable {
        private boolean m_packageEventsOnExpiry = false;

        public PostTask(boolean bl2) {
            this.m_packageEventsOnExpiry = bl2;
        }

        @Override
        public void run() {
            NimbleTrackingImplBase.this.postIntervalTimerExpired(this.m_packageEventsOnExpiry);
        }
    }

    public static class SessionData
    implements Serializable {
        private static final long serialVersionUID = 465486L;
        String key;
        String value;
    }

    private class StartupRequestsFinishedReceiver
    extends BroadcastReceiver {
        private StartupRequestsFinishedReceiver() {
        }

        public void onReceive(Context context, final Intent intent) {
            if (!intent.getAction().equals("nimble.environment.notification.startup_requests_finished")) return;
            NimbleTrackingImplBase.this.m_threadManager.runInWorkerThread(new Runnable(){

                @Override
                public void run() {
                    NimbleTrackingImplBase.this.onStartupRequestsFinished(intent);
                }
            });
        }
    }
}

