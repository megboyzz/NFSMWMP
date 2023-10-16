package com.ea.easp.mtx.market;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import com.ea.easp.Debug;
import com.ea.easp.mtx.market.BillingService;
import com.ea.easp.mtx.market.Consts;
import com.ea.easp.mtx.market.Security;
import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class PurchaseObserver {
    private static final Class[] START_INTENT_SENDER_SIG = {IntentSender.class, Intent.class, Integer.TYPE, Integer.TYPE, Integer.TYPE};
    private static final String TAG = "PurchaseObserver";
    private final Activity mActivity;
    private Method mStartIntentSender;
    private Object[] mStartIntentSenderArgs = new Object[5];

    public PurchaseObserver(Activity activity) {
        this.mActivity = activity;
        initCompatibilityLayer();
    }

    private void initCompatibilityLayer() {
        try {
            this.mStartIntentSender = this.mActivity.getClass().getMethod("startIntentSender", START_INTENT_SENDER_SIG);
        } catch (SecurityException e) {
            this.mStartIntentSender = null;
        } catch (NoSuchMethodException e2) {
            this.mStartIntentSender = null;
        }
    }

    public abstract void onBillingSupported(boolean z);

    public abstract void onPurchaseStateChange(ArrayList<Security.VerifiedPurchase> arrayList, boolean z, String str, String str2);

    public abstract void onRequestPurchaseResponse(BillingService.IRequestPurchase iRequestPurchase, Consts.ResponseCode responseCode);

    public abstract void onRestoreTransactionsResponse(BillingService.IRestoreTransactions iRestoreTransactions, Consts.ResponseCode responseCode);

    /* access modifiers changed from: package-private */
    public void startBuyPageActivity(PendingIntent pendingIntent, Intent intent) {
        if (this.mStartIntentSender != null) {
            try {
                this.mStartIntentSenderArgs[0] = pendingIntent.getIntentSender();
                this.mStartIntentSenderArgs[1] = intent;
                this.mStartIntentSenderArgs[2] = 0;
                this.mStartIntentSenderArgs[3] = 0;
                this.mStartIntentSenderArgs[4] = 0;
                this.mStartIntentSender.invoke(this.mActivity, this.mStartIntentSenderArgs);
            } catch (Exception e) {
                Debug.Log.e(TAG, "error starting activity", e);
            }
        } else {
            try {
                pendingIntent.send(this.mActivity, 0, intent);
            } catch (PendingIntent.CanceledException e2) {
                Debug.Log.e(TAG, "error starting activity", e2);
            }
        }
    }
}
