/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx;

import java.util.Date;
import java.util.Map;

public interface NimbleMTXTransaction {
    public Map<String, Object> getAdditionalInfo();

    public Exception getError();

    public String getItemSku();

    public float getPriceDecimal();

    public String getReceipt();

    public Date getTimeStamp();

    public String getTransactionId();

    public TransactionState getTransactionState();

    public TransactionType getTransactionType();

    public static enum TransactionState {
        UNDEFINED,
        USER_INITIATED,
        WAITING_FOR_PREPURCHASE_INFO,
        WAITING_FOR_PLATFORM_RESPONSE,
        WAITING_FOR_VERIFICATION,
        WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT,
        WAITING_FOR_PLATFORM_CONSUMPTION,
        COMPLETE;

    }

    public static enum TransactionType {
        PURCHASE,
        RESTORE;

    }
}

