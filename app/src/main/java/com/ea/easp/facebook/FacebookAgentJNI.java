package com.ea.easp.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ea.easp.Debug;
import com.ea.easp.TaskLauncher;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionEvents;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class FacebookAgentJNI {
    private static final String kMODULE_TAG = "EASP jFBAgentJNI";
    private Activity mActivity;
    private String mApplicationID;
    private AsyncFacebookRunner mAsyncRunner;
    private Facebook mFacebook;
    private TaskLauncher mTaskLauncher;

    public class CommonDialogListener implements Facebook.DialogListener {
        public CommonDialogListener() {
        }

        @Override
        public void onCancel() {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(FacebookAgentJNI.this::onDialogCancel);
        }

        @Override
        public void onComplete(final Bundle parameters) {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(() -> {
                String[] strArr = new String[parameters.size()];
                String[] strArr2 = new String[parameters.size()];
                FacebookAgentJNI.convertBundlesStringKeyValueArray(parameters, strArr, strArr2);
                FacebookAgentJNI.this.onDialogComplete(strArr, strArr2);
            });
        }


        @Override
        public void onError(final DialogError dialogError) {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(() -> FacebookAgentJNI.this.onDialogError(dialogError.getErrorCode(), dialogError.getFailingUrl(), dialogError.getMessage()));
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onFacebookError(final FacebookError facebookError) {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(() -> FacebookAgentJNI.this.onDialogFacebookError(facebookError.getErrorCode(), facebookError.getErrorType(), facebookError.getMessage()));
        }
    }

    private final class ExtendAccessTokenListener implements Facebook.ServiceListener, com.ea.easp.facebook.ExtendAccessTokenListener {
        private ExtendAccessTokenListener() {
        }

        @Override
        public void onComplete(Bundle bundle) {
            final String accessToken = bundle.getString("Facebook.ACCESS_TOKEN");
            final long accessTokenExpiresMS = bundle.getLong("Facebook.EXPIRES", 0);
            Debug.Log.i(FacebookAgentJNI.kMODULE_TAG, "ExtendAccessTokenListener.onComplete(): bundle = " + bundle.toString());

            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                @Override
                public void run() {
                    FacebookAgentJNI.this.onExtendAccessTokenJNI(accessToken, accessTokenExpiresMS);
                }
            });
        }

        @Override // com.facebook.android.Facebook.ServiceListener
        public void onError(Error error) {
            Debug.Log.e(FacebookAgentJNI.kMODULE_TAG, "ExtendAccessTokenListener.onError(): e = " + error.toString());
        }

        @Override // com.facebook.android.Facebook.ServiceListener
        public void onFacebookError(FacebookError facebookError) {
            Debug.Log.e(FacebookAgentJNI.kMODULE_TAG, "ExtendAccessTokenListener.onFacebookError(): e = " + facebookError.toString());
        }
    }

    private final class LoginDialogListener implements Facebook.DialogListener {
        private LoginDialogListener() {
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onCancel() {
            SessionEvents.onLoginError("Action Canceled");
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onComplete(Bundle bundle) {
            SessionEvents.onLoginSuccess();
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onError(DialogError dialogError) {
            SessionEvents.onLoginError(dialogError.getMessage());
        }

        @Override // com.facebook.android.Facebook.DialogListener
        public void onFacebookError(FacebookError facebookError) {
            SessionEvents.onLoginError(facebookError.getMessage());
        }
    }

    private class LogoutRequestListener implements AsyncFacebookRunner.RequestListener {
        private LogoutRequestListener() {
        }

        private final void onLogoutFinish() {
            /* class com.ea.easp.facebook.FacebookAgentJNI.LogoutRequestListener.AnonymousClass1 */
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(SessionEvents::onLogoutFinish);
        }

        @Override // com.facebook.android.AsyncFacebookRunner.RequestListener
        public void onComplete(String str, Object state) {
            Debug.Log.i(FacebookAgentJNI.kMODULE_TAG, "LogoutRequestListener::onComplete()");
            onLogoutFinish();
        }

        @Override // com.facebook.android.BaseRequestListener, com.facebook.android.AsyncFacebookRunner.RequestListener
        public void onFacebookError(FacebookError facebookError, Object state) {
            Debug.Log.e(FacebookAgentJNI.kMODULE_TAG, "LogoutRequestListener::onFacebookError" + facebookError.getMessage());
            onLogoutFinish();
        }

        @Override // com.facebook.android.BaseRequestListener, com.facebook.android.AsyncFacebookRunner.RequestListener
        public void onFileNotFoundException(FileNotFoundException fileNotFoundException, Object state) {
            Debug.Log.e(FacebookAgentJNI.kMODULE_TAG, "LogoutRequestListener::onFileNotFoundException" + fileNotFoundException.getMessage());
            onLogoutFinish();
        }

        @Override // com.facebook.android.BaseRequestListener, com.facebook.android.AsyncFacebookRunner.RequestListener
        public void onIOException(IOException iOException, Object state) {
            Debug.Log.e(FacebookAgentJNI.kMODULE_TAG, "LogoutRequestListener::onIOException" + iOException.getMessage());
            onLogoutFinish();
        }

        @Override // com.facebook.android.BaseRequestListener, com.facebook.android.AsyncFacebookRunner.RequestListener
        public void onMalformedURLException(MalformedURLException malformedURLException, Object state) {
            Debug.Log.e(FacebookAgentJNI.kMODULE_TAG, "LogoutRequestListener::onMalformedURLException" + malformedURLException.getMessage());
            onLogoutFinish();
        }


    }

    public class SampleAuthListener implements SessionEvents.AuthListener {
        public SampleAuthListener() {
        }

        @Override // com.facebook.android.SessionEvents.AuthListener
        public void onAuthFail(final String mError) {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(() -> FacebookAgentJNI.this.onAuthFailJNI(mError));
        }

        @Override // com.facebook.android.SessionEvents.AuthListener
        public void onAuthSucceed() {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                /* class com.ea.easp.facebook.FacebookAgentJNI.SampleAuthListener.AnonymousClass1 */

                public void run() {
                    FacebookAgentJNI.this.onAuthSucceedJNI(FacebookAgentJNI.this.mFacebook.getAccessToken(), FacebookAgentJNI.this.mFacebook.getAccessExpires());
                }
            });
        }
    }

    public class SampleLogoutListener implements SessionEvents.LogoutListener {
        public SampleLogoutListener() {
        }

        @Override // com.facebook.android.SessionEvents.LogoutListener
        public void onLogoutBegin() {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                /* class com.ea.easp.facebook.FacebookAgentJNI.SampleLogoutListener.AnonymousClass1 */

                public void run() {
                    FacebookAgentJNI.this.onLogoutBeginJNI();
                }
            });
        }

        @Override // com.facebook.android.SessionEvents.LogoutListener
        public void onLogoutFinish() {
            FacebookAgentJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                /* class com.ea.easp.facebook.FacebookAgentJNI.SampleLogoutListener.AnonymousClass2 */

                public void run() {
                    FacebookAgentJNI.this.onLogoutFinishJNI();
                }
            });
        }
    }

    public FacebookAgentJNI(Activity activity, TaskLauncher taskLauncher) {
        this.mActivity = activity;
        this.mTaskLauncher = taskLauncher;
    }

    /* access modifiers changed from: private */
    public static void convertBundlesStringKeyValueArray(Bundle bundle, String[] strArr, String[] strArr2) {
        if (strArr == null || strArr2 == null) {
            Debug.Log.e(kMODULE_TAG, "convertBundlesStringKeyValueArray(): bundleKeys and bundleValues must exist at this point.");
        } else if (strArr.length == bundle.size() && strArr2.length == bundle.size()) {
            int i = 0;
            for (String str : bundle.keySet()) {
                strArr[i] = str;
                strArr2[i] = bundle.getString(str);
                i++;
            }
        } else {
            Debug.Log.e(kMODULE_TAG, "convertBundlesStringKeyValueArray(): bundleKeys and bundleValues must be empty.");
        }
    }

    private static Bundle convertStringKeyValueArrayToBundle(String[] strArr, String[] strArr2) {
        Bundle bundle = new Bundle();
        if (strArr.length != strArr2.length) {
            Debug.Log.e(kMODULE_TAG, "convertStringKeyValueArrayToBundle(): lengths of bundleKeys and bundleValues must be equal.");
        }
        for (int i = 0; i != strArr.length; i++) {
            bundle.putString(strArr[i], strArr2[i]);
        }
        return bundle;
    }

    public void authorizeCallback(int i, int i2, Intent intent) {
        if (this.mFacebook != null) {
            this.mFacebook.authorizeCallback(i, i2, intent);
        }
    }

    public void dialog(final String action, String[] strArr, String[] strArr2) {

        final Bundle parameters = convertStringKeyValueArrayToBundle(strArr, strArr2);

        this.mTaskLauncher.runInUIThread(new Runnable() {

            @Override
            public void run() {
                FacebookAgentJNI.this.initFacebookSDKIfNeeded();
                FacebookAgentJNI.this.mFacebook.dialog(FacebookAgentJNI.this.mActivity, action, parameters, new CommonDialogListener());
            }
        });
    }

    public void extendAccessTokenIfNeeded() {
        this.mTaskLauncher.runInUIThread(new Runnable() {
            /* class com.ea.easp.facebook.FacebookAgentJNI.AnonymousClass1 */
            @Override
            public void run() {
                FacebookAgentJNI.this.initFacebookSDKIfNeeded();
                FacebookAgentJNI.this.mFacebook.extendAccessToken(FacebookAgentJNI.this.mActivity, new ExtendAccessTokenListener());
            }
        });
    }

    public void facebookLogin(final String permissions) {
        this.mTaskLauncher.runInUIThread(new Runnable() {
            @Override
            public void run() {
                FacebookAgentJNI.this.initFacebookSDKIfNeeded();
                String[] split = permissions.split(",");
                Debug.Log.i(FacebookAgentJNI.kMODULE_TAG, "request permissions:");
                for (int i = 0; i != split.length; i++) {
                    Debug.Log.i(FacebookAgentJNI.kMODULE_TAG, "    " + split[i]);
                }
                FacebookAgentJNI.this.mFacebook.authorize(FacebookAgentJNI.this.mActivity, split, new LoginDialogListener());
            }
        });
    }

    public void facebookLogout() {
        this.mTaskLauncher.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (FacebookAgentJNI.this.mAsyncRunner != null) {
                    FacebookAgentJNI.this.mAsyncRunner.logout(FacebookAgentJNI.this.mActivity, new LogoutRequestListener());
                } else {
                    Debug.Log.e(FacebookAgentJNI.kMODULE_TAG, "facebookLogout(): not logged in. Logout notification will not be sent.");
                }
            }
        });
    }

    public void init() {
        initJNI();
    }

    /* access modifiers changed from: package-private */
    public void initFacebookSDKIfNeeded() {
        if (this.mFacebook == null) {
            Debug.Log.d(kMODULE_TAG, "initFacebookSDKIfNeeded(): app id = " + this.mApplicationID);
            this.mFacebook = new Facebook(this.mApplicationID);
            this.mAsyncRunner = new AsyncFacebookRunner(this.mFacebook);
            SessionEvents.addAuthListener(new SampleAuthListener());
            SessionEvents.addLogoutListener(new SampleLogoutListener());
        }
    }

    public native void initJNI();

    public native void onAuthFailJNI(String str);

    public native void onAuthSucceedJNI(String str, long j);

    public native void onDialogCancel();

    public native void onDialogComplete(String[] strArr, String[] strArr2);

    public native void onDialogError(int i, String str, String str2);

    public native void onDialogFacebookError(int i, String str, String str2);

    public native void onExtendAccessTokenJNI(String str, long j);

    public native void onLogoutBeginJNI();

    public native void onLogoutFinishJNI();

    public void setAccessToken(final String accessToken, final long accessTokenExpiresMS) {
        this.mTaskLauncher.runInUIThread(new Runnable() {
            @Override
            public void run() {
                FacebookAgentJNI.this.initFacebookSDKIfNeeded();
                FacebookAgentJNI.this.mFacebook.setAccessToken(accessToken);
                FacebookAgentJNI.this.mFacebook.setAccessExpires(accessTokenExpiresMS);
            }
        });
    }

    public void setApplicationID(String str) {
        this.mApplicationID = str;
    }

    public void shutdown() {
        shutdownJNI();
    }

    public native void shutdownJNI();
}
