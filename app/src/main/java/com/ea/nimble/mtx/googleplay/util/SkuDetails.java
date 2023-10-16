/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ea.nimble.mtx.googleplay.util;

import org.json.JSONException;
import org.json.JSONObject;

public class SkuDetails {
    String mCurrencyCode;
    String mDescription;
    String mJson;
    String mPrice;
    String mPriceMicros;
    String mSku;
    String mTitle;
    String mType;

    public SkuDetails(String string2) throws JSONException {
        this.mJson = string2;
        JSONObject jsonObject = new JSONObject(this.mJson);
        this.mSku = jsonObject.optString("productId");
        this.mType = jsonObject.optString("type");
        this.mPrice = jsonObject.optString("price");
        this.mPriceMicros = jsonObject.optString("price_amount_micros");
        this.mCurrencyCode = jsonObject.optString("price_currency_code");
        this.mTitle = jsonObject.optString("title");
        this.mDescription = jsonObject.optString("description");
    }

    public String getCurrencyCode() {
        return this.mCurrencyCode;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getPrice() {
        return this.mPrice;
    }

    public String getPriceMicros() {
        return this.mPriceMicros;
    }

    public String getSku() {
        return this.mSku;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getType() {
        return this.mType;
    }

    public String toString() {
        return "SkuDetails:" + this.mJson;
    }
}

