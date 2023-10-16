/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Base64
 */
package com.ea.nimble.mtx.catalog.synergy;

import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.IApplicationEnvironment;
import com.ea.nimble.IHttpRequest;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.SynergyIdManager;
import com.ea.nimble.SynergyNetwork;
import com.ea.nimble.SynergyNetworkConnectionCallback;
import com.ea.nimble.SynergyRequest;
import com.ea.nimble.Utility;
import com.ea.nimble.mtx.NimbleCatalogItem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SynergyCatalog
implements LogSource {
    public static final String MTX_INFO_KEY_CURRENCY = "localCurrency";
    private static final String SYNERGY_API_GET_AVAILABLE_ITEMS = "/product/api/core/getAvailableItems";
    private static final String SYNERGY_API_GET_CATEGORIES = "/product/api/core/getMTXGameCategories";
    private static final String SYNERGY_API_GET_DOWNLOAD_URL = "/product/api/core/getDownloadItemUrl";
    private static final String SYNERGY_API_GET_NONCE = "/drm/api/core/getNonce";
    private static final String SYNERGY_API_GET_PURCHASED_ITEMS = "/drm/api/core/getPurchasedItems";
    private String m_itemSkuPrefix;
    private int m_itemsLoadingBinaryData = 0;

    public SynergyCatalog(StoreType storeType) {
        if (storeType == StoreType.AMAZON) {
            this.m_itemSkuPrefix = ApplicationEnvironment.getComponent().getApplicationBundleId() + ".";
            return;
        }
        this.m_itemSkuPrefix = "";
    }

    static /* synthetic */ int access$106(SynergyCatalog synergyCatalog) {
        int n2;
        synergyCatalog.m_itemsLoadingBinaryData = n2 = synergyCatalog.m_itemsLoadingBinaryData - 1;
        return n2;
    }

    private SynergyCatalogItem createItemFromMap(Map<String, Object> object) {
        SynergyCatalogItem synergyCatalogItem = new SynergyCatalogItem();
        synergyCatalogItem.m_sku = this.m_itemSkuPrefix + object.get("sellId");
        synergyCatalogItem.m_title = (String)object.get("title");
        NimbleCatalogItem.ItemType itemType = (Boolean)object.get("consumable") != false ? NimbleCatalogItem.ItemType.CONSUMABLE : NimbleCatalogItem.ItemType.NONCONSUMABLE;
        synergyCatalogItem.m_type = itemType;
        synergyCatalogItem.m_description = (String)object.get("desc");
        synergyCatalogItem.m_metaDataUrl = (String)object.get("packUrl");
        synergyCatalogItem.m_isFree = (Boolean)object.get("free");
        synergyCatalogItem.m_additionalInfo.putAll((Map<String, Object>)object);
        return synergyCatalogItem;
    }

    private void downloadContent(String string2, final DataCallback dataCallback) {
        try {
            URL uRL = new URL(string2);
            Network.getComponent().sendGetRequest(uRL, null, networkConnectionHandle -> {
                if (networkConnectionHandle.getResponse().getError() == null) {
                    dataCallback.callback(networkConnectionHandle.getResponse().getDataStream(), null);
                    return;
                }
                dataCallback.callback(null, networkConnectionHandle.getResponse().getError());
            });
        }
        catch (MalformedURLException malformedURLException) {
            Log.Helper.LOGE(this, "Invalid url: " + string2);
        }
    }

    private void getDownloadUrlForItem(SynergyCatalogItem object, StringCallback object2) {

    }

    public void downloadItem(SynergyCatalogItem synergyCatalogItem, final DataCallback dataCallback) {
        this.getDownloadUrlForItem(synergyCatalogItem, new StringCallback(){

            @Override
            public void callback(String string2, Exception exception) {
                if (exception == null) {
                    SynergyCatalog.this.downloadContent(string2, dataCallback);
                    return;
                }
                this.callback(null, exception);
            }
        });
    }

    public void getCategories(CategoryCallback object) {}

    public void getItemCatalog(ItemCallback object) {
        Object object2 = (SynergyRequest.SynergyRequestPreparingCallback) synergyRequest -> {
            IApplicationEnvironment iApplicationEnvironment = ApplicationEnvironment.getComponent();
            Object object1 = SynergyEnvironment.getComponent();
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("masterSellId", null);
            hashMap.put("typeSubstr", "1");
            hashMap.put("apiVer", "1.0.0");
            hashMap.put("ver", iApplicationEnvironment.getApplicationVersion());
            object1 = Utility.validString(SynergyIdManager.getComponent().getSynergyId()) ? SynergyIdManager.getComponent().getSynergyId() : "0";
            hashMap.put("uid", (String) object1);
            hashMap.put("sdkVer", "1.23.14.1217");
            hashMap.put("langCode", iApplicationEnvironment.getShortApplicationLanguageCode());
            synergyRequest.urlParameters = hashMap;
            synergyRequest.send();
        };
        object2 = new SynergyRequest(SYNERGY_API_GET_AVAILABLE_ITEMS, IHttpRequest.Method.GET, (SynergyRequest.SynergyRequestPreparingCallback)object2);
        SynergyNetwork.getComponent().sendRequest((SynergyRequest)object2, (SynergyNetworkConnectionCallback)object);
    }

    public String getItemSkuPrefix() {
        return this.m_itemSkuPrefix;
    }

    @Override
    public String getLogSourceTitle() {
        return "SynergyCatalog";
    }

    public void getNonce(StringCallback object){}

    public void getPurchasedItems(ItemSkuCallback object) {}

    public void loadBinaryDataForItems(Collection<SynergyCatalogItem> object, final CompletionCallback completionCallback) {
        if (this.m_itemsLoadingBinaryData != 0) {
            Log.Helper.LOGE(this, "Error: items already loading binary data");
            return;
        }
        for (SynergyCatalogItem catalogItem : object) {
            String string2;
            final SynergyCatalogItem synergyCatalogItem = catalogItem;
            if (synergyCatalogItem.m_additionalInfo.get("binaryData") != null || (string2 = synergyCatalogItem.getMetaDataUrl()) == null)
                continue;
            try {
                URL uRL = new URL(string2);
                ++this.m_itemsLoadingBinaryData;
                Network.getComponent().sendGetRequest(uRL, null, new NetworkConnectionCallback() {

                    /*
                     * Enabled unnecessary exception pruning
                     */
                    @Override
                    public void callback(NetworkConnectionHandle object) {
                        InputStream inputStream = object.getResponse().getDataStream();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            int n2;
                            byte[] byArray = new byte[4096];
                            while ((n2 = inputStream.read(byArray, 0, byArray.length)) != -1) {
                                ((ByteArrayOutputStream) object).write(byArray, 0, n2);
                            }
                            ((OutputStream) object).flush();
                        } catch (IOException iOException) {
                            Log.Helper.LOGE(this, "Error reading binary data");
                        }
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        synergyCatalogItem.getAdditionalInfo().put("binaryData", bytes);
                        SynergyCatalog.access$106(SynergyCatalog.this);
                        if (SynergyCatalog.this.m_itemsLoadingBinaryData != 0) return;
                        completionCallback.callback(null);
                    }
                });
            } catch (MalformedURLException malformedURLException) {
                Log.Helper.LOGE(this, "Error: Malformed item url: " + string2);
            }
        }
    }

    public static interface CategoryCallback {
        public void callback(Set<ItemCategory> var1, Exception var2);
    }

    public static interface CompletionCallback {
        public void callback(Exception var1);
    }

    public static interface DataCallback {
        public void callback(InputStream var1, Exception var2);
    }

    public static interface ItemCallback {
        public void callback(List<SynergyCatalogItem> var1, Exception var2);
    }

    public static interface ItemSkuCallback {
        public void callback(List<String> var1, Exception var2);
    }

    public static enum StoreType {
        GOOGLE,
        AMAZON;

    }

    public static interface StringCallback {
        public void callback(String var1, Exception var2);
    }
}

