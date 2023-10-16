/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.catalog.synergy;

import com.ea.nimble.mtx.NimbleCatalogItem;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class SynergyCatalogItem
extends NimbleCatalogItem
implements Serializable {
    private static final long serialVersionUID = 1L;
    Map<String, Object> m_additionalInfo = new HashMap<String, Object>();
    String m_description;
    String m_formattedPrice;
    boolean m_isFree;
    String m_metaDataUrl;
    float m_price;
    String m_sku;
    String m_title;
    NimbleCatalogItem.ItemType m_type;

    public SynergyCatalogItem() {
    }

    public SynergyCatalogItem(String string2) {
        this();
        this.m_sku = string2;
    }

    @Override
    public Map<String, Object> getAdditionalInfo() {
        return this.m_additionalInfo;
    }

    @Override
    public String getDescription() {
        return this.m_description;
    }

    @Override
    public NimbleCatalogItem.ItemType getItemType() {
        return this.m_type;
    }

    @Override
    public String getMetaDataUrl() {
        return this.m_metaDataUrl;
    }

    @Override
    public float getPriceDecimal() {
        return this.m_price;
    }

    @Override
    public String getPriceWithCurrencyAndFormat() {
        return this.m_formattedPrice;
    }

    @Override
    public String getSku() {
        return this.m_sku;
    }

    @Override
    public String getTitle() {
        return this.m_title;
    }

    public boolean isFree() {
        return this.m_isFree;
    }

    public void setDescription(String string2) {
        this.m_description = string2;
    }

    public void setPriceDecimal(float f2) {
        this.m_price = f2;
    }

    public void setPriceWithCurrencyAndFormat(String string2) {
        this.m_formattedPrice = string2;
    }

    public void setTitle(String string2) {
        this.m_title = string2;
    }
}

