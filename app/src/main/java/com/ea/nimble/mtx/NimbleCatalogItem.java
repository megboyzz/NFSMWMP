/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx;

import java.util.Map;

public abstract class NimbleCatalogItem {
    public abstract Map<String, Object> getAdditionalInfo();

    public abstract String getDescription();

    public abstract ItemType getItemType();

    public abstract String getMetaDataUrl();

    public abstract float getPriceDecimal();

    public abstract String getPriceWithCurrencyAndFormat();

    public abstract String getSku();

    public abstract String getTitle();

    public static enum ItemType {
        UNKNOWN,
        NONCONSUMABLE,
        CONSUMABLE;

    }
}

