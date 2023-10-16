package com.ea.nimble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.internal.Utility;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.WebDialog;
import com.google.android.gms.plus.PlusShare;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: stdlib.jar:com/ea/nimble/FacebookImpl.class */
class FacebookImpl extends Component implements IApplicationLifecycle.ActivityEventCallbacks, IApplicationLifecycle.ActivityLifecycleCallbacks, IFacebook, LogSource {
    static boolean bHasSeenOpening;
    private static Session.StatusCallback m_sessionCallback;
    private HashMap<Activity, UiLifecycleHelper> m_fbHelpers;
    private static String TAG = "Facebook";
    private static List<IFacebook.FacebookCallback> m_callbackQueue = new ArrayList();
    private Map<String, Object> userInfo = null;
    private AtomicBoolean isUserGraphReady = new AtomicBoolean(true);

    private String getFacebookSDKVersion() {
        try {
            return String.valueOf(getClass().getClassLoader().loadClass("com.facebook.FacebookSdkVersion").getField("BUILD").get(null));
        } catch (ClassNotFoundException e) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        } catch (IllegalAccessException e2) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        } catch (IllegalArgumentException e3) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        } catch (NoSuchFieldException e4) {
            Log.Helper.LOGW(TAG, "Unable to get FB SDK version.");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.Component
    public void cleanup() {
        ApplicationLifecycle.getComponent().unregisterActivityLifecycleCallbacks(this);
        ApplicationLifecycle.getComponent().unregisterActivityEventCallbacks(this);
    }

    @Override // com.ea.nimble.IFacebook
    public String getAccessToken() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            return activeSession.getAccessToken();
        }
        return null;
    }

    @Override // com.ea.nimble.IFacebook
    public String getApplicationId() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            return activeSession.getApplicationId();
        }
        return null;
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return Facebook.COMPONENT_ID;
    }

    @Override // com.ea.nimble.IFacebook
    public Date getExpirationDate() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            return activeSession.getExpirationDate();
        }
        return null;
    }

    @Override // com.ea.nimble.IFacebook
    public Map<String, Object> getGraphUser() {
        if (this.isUserGraphReady.get()) {
            return this.userInfo;
        }
        return null;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "Facebook";
    }

    @Override // com.ea.nimble.IFacebook
    public boolean hasOpenSession() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            return activeSession.isOpened();
        }
        return false;
    }

    @Override // com.ea.nimble.IFacebook
    public void login(List<String> list, IFacebook.FacebookCallback facebookCallback) {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            if (!activeSession.isOpened() || !Utility.isSubset(list, activeSession.getPermissions())) {
                activeSession.removeCallback(m_sessionCallback);
            } else {
                facebookCallback.callback(this, true, null);
                return;
            }
        }
        Activity currentActivity = ApplicationEnvironment.getCurrentActivity();
        Session session = new Session(currentActivity);
        Session.setActiveSession(session);
        Session.OpenRequest openRequest = new Session.OpenRequest(currentActivity);
        openRequest.setPermissions(list);
        if (facebookCallback != null) {
            m_callbackQueue.add(facebookCallback);
        }
        openRequest.setCallback(m_sessionCallback);
        session.openForRead(openRequest);
    }

    @Override // com.ea.nimble.IFacebook
    public void logout() {
        Session activeSession = Session.getActiveSession();
        if (activeSession != null) {
            activeSession.closeAndClearTokenInformation();
            Session.setActiveSession((Session) null);
        }
        this.userInfo = null;
        this.isUserGraphReady.set(false);
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
        UiLifecycleHelper uiLifecycleHelper = this.m_fbHelpers.get(activity);
        UiLifecycleHelper uiLifecycleHelper2 = uiLifecycleHelper;
        if (uiLifecycleHelper == null) {
            uiLifecycleHelper2 = new UiLifecycleHelper(activity, m_sessionCallback);
            this.m_fbHelpers.put(activity, uiLifecycleHelper2);
        }
        uiLifecycleHelper2.onCreate(bundle);
        if (!Utility.isNullOrEmpty(getAccessToken()) && !hasOpenSession()) {
            Activity currentActivity = ApplicationEnvironment.getCurrentActivity();
            Session session = new Session(currentActivity);
            Session.setActiveSession(session);
            session.openForRead(new Session.OpenRequest(currentActivity));
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        UiLifecycleHelper uiLifecycleHelper = this.m_fbHelpers.get(activity);
        if (uiLifecycleHelper != null) {
            uiLifecycleHelper.onDestroy();
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        UiLifecycleHelper uiLifecycleHelper = this.m_fbHelpers.get(activity);
        if (uiLifecycleHelper != null) {
            uiLifecycleHelper.onPause();
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
        UiLifecycleHelper uiLifecycleHelper = this.m_fbHelpers.get(activity);
        if (uiLifecycleHelper != null) {
            uiLifecycleHelper.onActivityResult(i, i2, intent);
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        UiLifecycleHelper uiLifecycleHelper = this.m_fbHelpers.get(activity);
        if (uiLifecycleHelper != null) {
            uiLifecycleHelper.onResume();
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        UiLifecycleHelper uiLifecycleHelper = this.m_fbHelpers.get(activity);
        if (uiLifecycleHelper != null) {
            uiLifecycleHelper.onSaveInstanceState(bundle);
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public boolean onBackPressed() {
        return true;
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public void onWindowFocusChanged(boolean z) {
    }

    @Override // com.ea.nimble.IFacebook
    public void refreshSession(final String str, Date date) {
        if (!str.equals(getAccessToken()) || (date != null && date.after(getExpirationDate()))) {
            Log.Helper.LOGI(this, "Refresh Facebook token from %s to %s", getAccessToken(), str);
            new Session(ApplicationEnvironment.getCurrentActivity()).open(AccessToken.createFromExistingAccessToken(str, date, new Date(), AccessTokenSource.CLIENT_TOKEN, Session.getActiveSession().getPermissions()), new Session.StatusCallback() { // from class: com.ea.nimble.FacebookImpl.2
                public void call(Session session, SessionState sessionState, Exception exc) {
                    if (exc == null && (sessionState == SessionState.OPENED || sessionState == SessionState.OPENED_TOKEN_UPDATED)) {
                        Session.getActiveSession().close();
                        Session.setActiveSession(session);
                        return;
                    }
                    Log.Helper.LOGE(this, "Invalid Facebook accessToken " + str + " from Origin");
                }
            });
        }
    }

    @Override
    public void restore() {
        ApplicationLifecycle.getComponent().registerActivityEventCallbacks(this);
        ApplicationLifecycle.getComponent().registerActivityLifecycleCallbacks(this);
    }

    public void retrieveCurrentUserProfile(IFacebook.FacebookFriendsCallback facebookFriendsCallback) {
    }

    @Override // com.ea.nimble.IFacebook
    public void retrieveFriends(final int i, final int i2, final IFacebook.FacebookFriendsCallback facebookFriendsCallback) {
        if (facebookFriendsCallback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.ea.nimble.FacebookImpl.4
                @Override // java.lang.Runnable
                public void run() {
                    Bundle bundle = new Bundle();
                    bundle.putInt("offset", i);
                    bundle.putInt("limit", i2);
                    bundle.putString("fields", "name,id,picture.type(normal)");
                    Request.executeBatchAsync(new Request[]{new Request(Session.getActiveSession(), "me/friends", bundle, HttpMethod.GET, new Request.Callback() { // from class: com.ea.nimble.FacebookImpl.4.1
                        public void onCompleted(Response response) {
                            Log.Helper.LOGD(this, response.toString());
                            if (response.getError() != null) {
                                facebookFriendsCallback.callback(Facebook.getComponent(), null, new NimbleFacebookError(NimbleFacebookError.Code.FBSERVER_ERROR, response.getError().toString()));
                                return;
                            }
                            GraphObject graphObject = response.getGraphObject();
                            if (graphObject != null) {
                                try {
                                    facebookFriendsCallback.callback(Facebook.getComponent(), graphObject.getInnerJSONObject().getJSONArray("data"), null);
                                } catch (JSONException e) {
                                    Log.Helper.LOGE(this, "JSON Exception encountered when parsing the facebook FQL query");
                                    facebookFriendsCallback.callback(Facebook.getComponent(), null, new NimbleFacebookError(NimbleFacebookError.Code.RESPONSE_PARSE_ERROR.intValue(), e.toString()));
                                }
                            } else {
                                facebookFriendsCallback.callback(Facebook.getComponent(), null, null);
                            }
                        }
                    })});
                }
            });
        }
    }

    @Override // com.ea.nimble.IFacebook
    public void sendAppRequest(final String str, final String str2, final String str3, final IFacebook.FacebookCallback facebookCallback) {
        ApplicationEnvironment.getCurrentActivity().runOnUiThread(new Runnable() { // from class: com.ea.nimble.FacebookImpl.3
            @Override // java.lang.Runnable
            public void run() {
                Activity currentActivity = ApplicationEnvironment.getCurrentActivity();
                Bundle bundle = new Bundle();
                bundle.putString(PlusShare.KEY_CONTENT_DEEP_LINK_METADATA_TITLE, str2);
                bundle.putString("message", str3);
                bundle.putString("to", str);
                WebDialog.Builder builder = new WebDialog.Builder(currentActivity, Session.getActiveSession(), "apprequests", bundle);
                builder.setOnCompleteListener(new WebDialog.OnCompleteListener() { // from class: com.ea.nimble.FacebookImpl.3.1
                    public void onComplete(Bundle bundle2, FacebookException facebookException) {
                        IFacebook.FacebookCallback facebookCallback2 = facebookCallback;
                        FacebookImpl facebookImpl = FacebookImpl.this;
                        boolean z = facebookException == null;
                        if (facebookException == null || (facebookException instanceof FacebookOperationCanceledException)) {
                            facebookException = null;
                        }
                        facebookCallback2.callback(facebookImpl, z, facebookException);
                    }
                });
                WebDialog build = builder.build();
                build.getWindow().setFlags(1024, 1024);
                build.show();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.Component
    public void setup() {
        Log.Helper.LOGV(TAG, "Currently using FB SDK version " + getFacebookSDKVersion() + ".");
        try {
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
        this.m_fbHelpers = new HashMap<>();
        bHasSeenOpening = false;
        m_sessionCallback = new Session.StatusCallback() { // from class: com.ea.nimble.FacebookImpl.1
            public void call(Session session, SessionState sessionState, Exception exc) {
                IFacebook.FacebookCallback facebookCallback;
                boolean z;
                if (sessionState == SessionState.OPENED || sessionState == SessionState.OPENED_TOKEN_UPDATED) {
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() { // from class: com.ea.nimble.FacebookImpl.1.1
                        public void onCompleted(GraphUser graphUser, Response response) {
                            Log.Helper.LOGI(this, "Facebook GraphUser data received");
                            if (response.getError() != null || graphUser == null) {
                                Log.Helper.LOGE(this, "Failed to retrieve graph user info");
                                return;
                            }
                            Log.Helper.LOGI(this, "Facebook graph user info successfully retrieved");
                            FacebookImpl.this.userInfo = graphUser.asMap();
                            try {
                                FacebookImpl.this.userInfo.put("avatar", "https://graph.facebook.com/" + FacebookImpl.this.userInfo.get("id").toString() + "/picture");
                                FacebookImpl.this.isUserGraphReady.set(true);
                            } catch (Exception e2) {
                                Log.Helper.LOGE(this, "Failed to get facebook user id");
                                FacebookImpl.this.isUserGraphReady.set(true);
                            }
                        }
                    });
                }
                if (sessionState == SessionState.CLOSED || sessionState == SessionState.OPENED || sessionState == SessionState.OPENED_TOKEN_UPDATED) {
                    com.ea.nimble.Utility.sendBroadcast(IFacebook.NIMBLE_NOTIFICATION_FACEBOOK_STATUS_CHANGED, null);
                }
                if (sessionState == SessionState.OPENING || sessionState == SessionState.CLOSED) {
                    FacebookImpl.bHasSeenOpening = true;
                } else if ((sessionState != SessionState.CLOSED_LOGIN_FAILED || FacebookImpl.bHasSeenOpening) && FacebookImpl.m_callbackQueue != null) {
                    if (FacebookImpl.m_callbackQueue.size() >= 1 && FacebookImpl.m_callbackQueue.size() <= 1 && (facebookCallback = (IFacebook.FacebookCallback) FacebookImpl.m_callbackQueue.get(0)) != null) {
                        FacebookImpl facebookImpl = FacebookImpl.this;
                        if (exc == null) {
                            z = true;
                            if (sessionState != SessionState.OPENED) {
                                if (sessionState == SessionState.OPENED_TOKEN_UPDATED) {
                                    z = true;
                                }
                            }
                            facebookCallback.callback(facebookImpl, z, exc);
                        }
                        z = false;
                        facebookCallback.callback(facebookImpl, z, exc);
                    }
                    FacebookImpl.m_callbackQueue.clear();
                }
            }
        };
    }
}
