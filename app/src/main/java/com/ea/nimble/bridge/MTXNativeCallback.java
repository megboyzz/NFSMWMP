/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.bridge;

import com.ea.nimble.bridge.BaseNativeCallback;
import com.ea.nimble.mtx.INimbleMTX;
import com.ea.nimble.mtx.NimbleMTXTransaction;

public class MTXNativeCallback
implements INimbleMTX.FinalizeTransactionCallback,
INimbleMTX.ItemGrantedCallback,
INimbleMTX.PurchaseTransactionCallback {
    private int m_id;

    public MTXNativeCallback(int n2) {
        this.m_id = n2;
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }

    @Override
    public void finalizeComplete(NimbleMTXTransaction nimbleMTXTransaction) {
        BaseNativeCallback.nativeCallback(this.m_id, nimbleMTXTransaction);
    }

    @Override
    public void itemGrantedComplete(NimbleMTXTransaction nimbleMTXTransaction) {
        BaseNativeCallback.nativeCallback(this.m_id, nimbleMTXTransaction);
    }

    @Override
    public void purchaseComplete(NimbleMTXTransaction nimbleMTXTransaction) {
        BaseNativeCallback.nativeCallback(this.m_id, nimbleMTXTransaction, true);
    }

    @Override
    public void unverifiedReceiptReceived(NimbleMTXTransaction nimbleMTXTransaction) {
        BaseNativeCallback.nativeCallback(this.m_id, nimbleMTXTransaction, false);
    }
}

