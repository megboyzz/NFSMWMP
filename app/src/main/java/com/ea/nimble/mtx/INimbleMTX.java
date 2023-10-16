/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx;

import com.ea.nimble.Error;
import com.ea.nimble.mtx.NimbleCatalogItem;
import com.ea.nimble.mtx.NimbleMTXTransaction;
import java.util.List;
import java.util.Map;

public interface INimbleMTX {
    public static final String NIMBLE_NOTIFICATION_MTX_REFRESH_CATALOG_FINISHED = "nimble.notification.mtx.refreshcatalogfinished";
    public static final String NIMBLE_NOTIFICATION_MTX_RESTORE_PURCHASED_TRANSACTIONS_FINISHED = "nimble.notification.mtx.restorepurchasedtransactionsfinished";
    public static final String NIMBLE_NOTIFICATION_MTX_TRANSACTIONS_RECOVERED = "nimble.notification.mtx.transactionsrecovered";
    public static final String NOTIFICATION_DICTIONARY_KEY_TRANSACTIONID = "TRANSACTION_ID";

    public Error finalizeTransaction(String var1, FinalizeTransactionCallback var2);

    public List<NimbleCatalogItem> getAvailableCatalogItems();

    public List<NimbleMTXTransaction> getPendingTransactions();

    public List<NimbleMTXTransaction> getPurchasedTransactions();

    public List<NimbleMTXTransaction> getRecoveredTransactions();

    public Error itemGranted(String var1, NimbleCatalogItem.ItemType var2, ItemGrantedCallback var3);

    public Error purchaseItem(String var1, PurchaseTransactionCallback var2);

    public void refreshAvailableCatalogItems();

    public void restorePurchasedTransactions();

    public Error resumeTransaction(String var1, PurchaseTransactionCallback var2, ItemGrantedCallback var3, FinalizeTransactionCallback var4);

    public void setPlatformParameters(Map<String, String> var1);

    public static interface FinalizeTransactionCallback {
        public void finalizeComplete(NimbleMTXTransaction var1);
    }

    public static interface ItemGrantedCallback {
        public void itemGrantedComplete(NimbleMTXTransaction var1);
    }

    public static interface PurchaseTransactionCallback {
        public void purchaseComplete(NimbleMTXTransaction var1);

        public void unverifiedReceiptReceived(NimbleMTXTransaction var1);
    }
}

