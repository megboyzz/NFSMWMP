package com.ea.easp.mtx.market;

import android.content.Context;

public interface BillingService {

    public interface IRequestPurchase {
        String getProductId();
    }

    public interface IRestoreTransactions {
    }

    void OnNonceSucceed(long j, Object obj);

    void OnVerifyMarketResponse(boolean z, String str, String str2, int i);

    void checkBillingSupported(Runnable runnable);

    void requestPurchase(String str, String str2, String str3, Runnable runnable);

    void restoreTransactions(long j, Runnable runnable);

    void setContext(Context context);

    void shutdown();
}
