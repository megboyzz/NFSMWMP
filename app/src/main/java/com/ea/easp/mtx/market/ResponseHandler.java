package com.ea.easp.mtx.market;

import android.app.PendingIntent;
import android.content.Intent;
import com.ea.easp.Debug;
import com.ea.easp.mtx.market.BillingService;
import com.ea.easp.mtx.market.Consts;
import com.ea.easp.mtx.market.Security;
import java.util.ArrayList;

public class ResponseHandler {
    private static final String TAG = "ResponseHandler";
    private static PurchaseObserver sPurchaseObserver;

    public static void buyPageIntentResponse(PendingIntent pendingIntent, Intent intent) {
        if (sPurchaseObserver == null) {
            Debug.Log.w(TAG, "buyPageIntentResponse(): UI is not running");
        } else {
            sPurchaseObserver.startBuyPageActivity(pendingIntent, intent);
        }
    }

    public static void checkBillingSupportedResponse(boolean z) {
        if (sPurchaseObserver != null) {
            sPurchaseObserver.onBillingSupported(z);
        }
    }

    public static void purchaseResponse(ArrayList<Security.VerifiedPurchase> arrayList, boolean z, String str, String str2) {
        if (sPurchaseObserver != null) {
            sPurchaseObserver.onPurchaseStateChange(arrayList, z, str, str2);
        }
    }

    public static synchronized void register(PurchaseObserver purchaseObserver) {
        synchronized (ResponseHandler.class) {
            sPurchaseObserver = purchaseObserver;
        }
    }

    public static void responseCodeReceived(BillingService.IRequestPurchase iRequestPurchase, Consts.ResponseCode responseCode) {
        if (sPurchaseObserver != null) {
            sPurchaseObserver.onRequestPurchaseResponse(iRequestPurchase, responseCode);
        }
    }

    public static void responseCodeReceived(BillingService.IRestoreTransactions iRestoreTransactions, Consts.ResponseCode responseCode) {
        if (sPurchaseObserver != null) {
            sPurchaseObserver.onRestoreTransactionsResponse(iRestoreTransactions, responseCode);
        }
    }

    public static synchronized void unregister(PurchaseObserver purchaseObserver) {
        synchronized (ResponseHandler.class) {
            sPurchaseObserver = null;
        }
    }
}
