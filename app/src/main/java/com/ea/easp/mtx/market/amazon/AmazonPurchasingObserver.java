package com.ea.easp.mtx.market.amazon;

import android.app.Activity;

/* access modifiers changed from: package-private */
public class AmazonPurchasingObserver {
    private AmazonPurchasingEventHandler mEventHandler = null;

    /* access modifiers changed from: package-private */
    public interface AmazonPurchasingEventHandler {
        void onItemDataResponse(Object itemDataResponse);

        void onPurchaseResponse(Object purchaseResponse);

        void onPurchaseUpdatesResponse(Object purchaseUpdatesResponse);
    }

    public AmazonPurchasingObserver(Activity activity, AmazonPurchasingEventHandler amazonPurchasingEventHandler) {
        this.mEventHandler = amazonPurchasingEventHandler;
    }

    public void onItemDataResponse(Object itemDataResponse) {
        if (this.mEventHandler != null) {
            this.mEventHandler.onItemDataResponse(itemDataResponse);
        }
    }

     // com.amazon.inapp.purchasing.PurchasingObserver, com.amazon.inapp.purchasing.BasePurchasingObserver
    public void onPurchaseResponse(Object purchaseResponse) {
        if (this.mEventHandler != null) {
            this.mEventHandler.onPurchaseResponse(purchaseResponse);
        }
    }

    // com.amazon.inapp.purchasing.PurchasingObserver, com.amazon.inapp.purchasing.BasePurchasingObserver
    public void onPurchaseUpdatesResponse(Object purchaseUpdatesResponse) {
        if (this.mEventHandler != null) {
            this.mEventHandler.onPurchaseUpdatesResponse(purchaseUpdatesResponse);
        }
    }

    // com.amazon.inapp.purchasing.PurchasingObserver, com.amazon.inapp.purchasing.BasePurchasingObserver
    public void onSdkAvailable(boolean z) {
        System.out.println((z ? "sandbox" : "production") + " mode");
    }
}
