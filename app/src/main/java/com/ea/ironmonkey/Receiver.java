package com.ea.ironmonkey;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ea.easp.TaskLauncher;
import com.ea.games.nfs13_na.C2DMReceiver;
import java.net.URLDecoder;
import java.util.Set;

public class Receiver extends BroadcastReceiver {
    private static final String TAG = "Receiver";
    private TaskLauncher taskLauncher_;

    public Receiver(TaskLauncher taskLauncher) {
        Log.i(TAG, "Constuctor()");
        this.taskLauncher_ = taskLauncher;
        initC2DMJNI();
        while (!C2DMReceiver.unhandledMessages.isEmpty()) {
            handleMessage(C2DMReceiver.unhandledMessages.get(0));
        }
        C2DMReceiver.unhandledMessages.clear();
    }

    private void handleError(final String errorID) {
        this.taskLauncher_.runInGLThread(new Runnable() {
            @Override
            public void run() {
                Receiver.this.onErrorJNI(errorID);
            }
        });
    }

    private void handleMessage(final Bundle messageParts) {
        C2DMReceiver.unhandledMessages.remove(messageParts);
        this.taskLauncher_.runInGLThread(new Runnable() {
            @Override
            public void run() {
                String str;
                if (messageParts != null) {
                    Set<String> keySet = messageParts.keySet();
                    Receiver.this.onMessagePartsCountJNI(messageParts.size());
                    for (String str2 : keySet) {
                        try {
                            str = URLDecoder.decode(messageParts.getString(str2), "UTF-8");
                        } catch (Exception e) {
                            str = "";
                            Log.w(Receiver.TAG, "Receiver ERROR in DECODING the message");
                        }
                        Receiver.this.onMessagePartJNI(str2, str);
                    }
                    return;
                }
                Log.w(Receiver.TAG, "C2DM MESSAGE HAS NO EXTRAS");
            }
        });
    }

    private void handleRegistrationID(final String registrationID) {
        this.taskLauncher_.runInGLThread(new Runnable() {
            @Override
            public void run() {
                Receiver.this.onRegisterJNI(registrationID);
            }
        });
    }

    private native void initC2DMJNI();

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private native void onErrorJNI(String str);

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private native void onMessagePartJNI(String str, String str2);

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private native void onMessagePartsCountJNI(int i);

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private native void onRegisterJNI(String str);

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private native void onUnRegisterJNI();

    private native void shutdownC2DMJNI();

    public void onDestroy() {
        shutdownC2DMJNI();
    }

    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive()..." + intent.getAction());
        String action = intent.getAction();
        if (C2DMConstants.ACTION_REGISTER.equals(action)) {
            handleRegistrationID(intent.getStringExtra(C2DMConstants.EXTRA_REGISTRATION_ID));
        } else if (C2DMConstants.ACTION_UNREGISTER.equals(action)) {
            this.taskLauncher_.runInGLThread(new Runnable() {
                /* class com.ea.ironmonkey.Receiver.AnonymousClass1 */

                public void run() {
                    Receiver.this.onUnRegisterJNI();
                }
            });
        } else if (C2DMConstants.ACTION_MESSAGE.equals(action)) {
            handleMessage(intent.getExtras());
        } else if (C2DMConstants.ACTION_ERROR.equals(action)) {
            handleError(intent.getStringExtra(C2DMConstants.EXTRA_ERROR_ID));
        } else {
            Log.w(TAG, "unexpected action: " + action);
        }
        Log.i(TAG, "...onReceive()");
    }
}
