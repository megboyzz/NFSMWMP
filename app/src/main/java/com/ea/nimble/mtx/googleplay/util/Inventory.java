/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.googleplay.util;

import com.ea.nimble.mtx.googleplay.util.Purchase;
import com.ea.nimble.mtx.googleplay.util.SkuDetails;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    Map<String, Purchase> mPurchaseMap;
    Map<String, SkuDetails> mSkuMap = new HashMap<String, SkuDetails>();

    Inventory() {
        this.mPurchaseMap = new HashMap<String, Purchase>();
    }

    void addPurchase(Purchase purchase) {
        this.mPurchaseMap.put(purchase.getSku(), purchase);
    }

    void addSkuDetails(SkuDetails skuDetails) {
        this.mSkuMap.put(skuDetails.getSku(), skuDetails);
    }

    public void erasePurchase(String string2) {
        if (!this.mPurchaseMap.containsKey(string2)) return;
        this.mPurchaseMap.remove(string2);
    }

    public List<String> getAllOwnedSkus() {
        return new ArrayList<String>(this.mPurchaseMap.keySet());
    }

    public List<Purchase> getAllPurchases() {
        return new ArrayList<Purchase>(this.mPurchaseMap.values());
    }

    public List<SkuDetails> getAllSkuDetails() {
        return new ArrayList<SkuDetails>(this.mSkuMap.values());
    }

    public Purchase getPurchase(String string2) {
        return this.mPurchaseMap.get(string2);
    }

    public SkuDetails getSkuDetails(String string2) {
        return this.mSkuMap.get(string2);
    }

    public boolean hasDetails(String string2) {
        return this.mSkuMap.containsKey(string2);
    }

    public boolean hasPurchase(String string2) {
        return this.mPurchaseMap.containsKey(string2);
    }
}

