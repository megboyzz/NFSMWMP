/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.googleplay;

import com.ea.nimble.mtx.NimbleCatalogItem;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public class GooglePlayCatalogItem
extends NimbleCatalogItem
implements Externalizable {
    Map<String, Object> mAdditionalInfo;
    String mDescription;
    boolean mIsFree;
    NimbleCatalogItem.ItemType mItemType;
    float mPriceDecimal;
    String mPriceWithCurrencyAndFormat;
    String mSku;
    String mTitle;
    String mUrl;

    public GooglePlayCatalogItem() {
        this.mSku = "";
        this.mTitle = "";
        this.mDescription = "";
        this.mPriceDecimal = 0.0f;
        this.mPriceWithCurrencyAndFormat = "";
        this.mItemType = NimbleCatalogItem.ItemType.UNKNOWN;
        this.mUrl = "";
        this.mIsFree = false;
        this.mAdditionalInfo = new HashMap<String, Object>();
    }

    public GooglePlayCatalogItem(GooglePlayCatalogItem googlePlayCatalogItem) {
        this.mSku = new String(googlePlayCatalogItem.mSku);
        this.mTitle = new String(googlePlayCatalogItem.mTitle);
        this.mDescription = new String(googlePlayCatalogItem.mDescription);
        this.mPriceDecimal = googlePlayCatalogItem.mPriceDecimal;
        this.mPriceWithCurrencyAndFormat = new String(googlePlayCatalogItem.mPriceWithCurrencyAndFormat);
        this.mItemType = googlePlayCatalogItem.mItemType;
        this.mUrl = new String(googlePlayCatalogItem.mUrl);
        this.mIsFree = googlePlayCatalogItem.mIsFree;
        this.mAdditionalInfo = new HashMap<String, Object>(googlePlayCatalogItem.mAdditionalInfo);
    }

    @Override
    public Map<String, Object> getAdditionalInfo() {
        return this.mAdditionalInfo;
    }

    @Override
    public String getDescription() {
        return this.mDescription;
    }

    @Override
    public NimbleCatalogItem.ItemType getItemType() {
        return this.mItemType;
    }

    @Override
    public String getMetaDataUrl() {
        return this.mUrl;
    }

    @Override
    public float getPriceDecimal() {
        return this.mPriceDecimal;
    }

    @Override
    public String getPriceWithCurrencyAndFormat() {
        return this.mPriceWithCurrencyAndFormat;
    }

    @Override
    public String getSku() {
        return this.mSku;
    }

    @Override
    public String getTitle() {
        return this.mTitle;
    }

    public boolean isFree() {
        return this.mIsFree;
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.mSku = objectInput.readUTF();
        this.mTitle = objectInput.readUTF();
        this.mDescription = objectInput.readUTF();
        this.mPriceDecimal = objectInput.readFloat();
        this.mPriceWithCurrencyAndFormat = objectInput.readUTF();
        this.mItemType = (NimbleCatalogItem.ItemType)((Object)objectInput.readObject());
        this.mIsFree = objectInput.readBoolean();
        this.mAdditionalInfo = (Map)objectInput.readObject();
    }

    public String toString() {
        return "SKU(" + this.mSku + ") Title(" + this.mTitle + ") Price(" + this.mPriceDecimal + ") Currency(" + this.mAdditionalInfo.get("localCurrency") + ") PriceStr(" + this.mPriceWithCurrencyAndFormat + ") ItemType(" + (Object)((Object)this.mItemType) + ")";
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeUTF(this.mSku);
        objectOutput.writeUTF(this.mTitle);
        objectOutput.writeUTF(this.mDescription);
        objectOutput.writeFloat(this.mPriceDecimal);
        objectOutput.writeUTF(this.mPriceWithCurrencyAndFormat);
        objectOutput.writeObject((Object)this.mItemType);
        objectOutput.writeBoolean(this.mIsFree);
        objectOutput.writeObject(this.mAdditionalInfo);
    }
}

