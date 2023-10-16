package com.verizon.vcast.apps;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;
import java.util.Date;

public class InAppPurchasor extends InAppPurchasorInternal {
    public static final int ERROR_CONTENT_HANDLER = 100;
    public static final int ERROR_GENERAL = 106;
    public static final int ERROR_ILLEGAL_ARGUMENT = 101;
    public static final int ERROR_SECURITY = 102;
    public static final int ERROR_UNABLE_TO_CONNECT_TO_CDS = 107;
    public static final int INVALID_SUBSCIPTION_ID = 7;
    public static final int LIST_REQ_FAILED = 9;
    public static final int LIST_REQ_OK = 8;
    public static final int PURCHASE_FAILED = 4;
    public static final int PURCHASE_INITIATION_OK = 10;
    public static final int PURCHASE_OK = 3;
    public static final int SUBSCRIPTION_CANCELLATION_FAILED = 6;
    public static final int SUBSCRIPTION_CANCELLED = 5;
    private static InAppPurchasor instance = null;

    public class DiscoveryParameters {
        private boolean ascendingOrder;
        private int maxResults;
        private String sortBy;
        private int startIndex;

        public DiscoveryParameters() {
        }

        public int getMaxResults() {
            return this.maxResults;
        }

        public String getSortBy() {
            return this.sortBy;
        }

        public int getStartIndex() {
            return this.startIndex;
        }

        public boolean isAscendingOrder() {
            return this.ascendingOrder;
        }

        public void setAscendingOrder(boolean z) {
            this.ascendingOrder = z;
        }

        public void setMaxResults(int i) {
            this.maxResults = i;
        }

        public void setSortBy(String str) {
            this.sortBy = str;
        }

        public void setStartIndex(int i) {
            this.startIndex = i;
        }

        public String toString() {
            return "Start Index:" + this.startIndex + "\n" + "maxResults:" + this.maxResults + "\n" + "ascendingOrder:" + this.ascendingOrder + "\n" + "sortBy:" + this.sortBy;
        }
    }

    public static class InAppContentOffers {
        private Offer[] offers;
        private Integer result;
        private Integer totalSize;

        public InAppContentOffers() {
        }

        public Offer[] getOffers() {
            return this.offers;
        }

        public Integer getResult() {
            return this.result;
        }

        public Integer getTotalSize() {
            return this.totalSize;
        }

        public void setOffers(Offer[] offerArr) {
            this.offers = offerArr;
        }

        public void setResult(Integer num) {
            this.result = num;
        }

        public void setTotalSize(Integer num) {
            this.totalSize = num;
        }

        public String toString() {
            return "Result:" + this.result + " \n" + "Total Size:" + this.totalSize + " \n" + "Offer:" + Arrays.deepToString(this.offers);
        }
    }

    public static class InAppContents {
        private Item[] items;
        private Integer result;
        private Integer totalSize;

        public InAppContents() {
        }

        public Item[] getItems() {
            return this.items;
        }

        public Integer getResult() {
            return this.result;
        }

        public Integer getTotalSize() {
            return this.totalSize;
        }

        public void setItems(Item[] itemArr) {
            this.items = itemArr;
        }

        public void setResult(Integer num) {
            this.result = num;
        }

        /* access modifiers changed from: package-private */
        public void setTotalSize(Integer num) {
            this.totalSize = num;
        }

        public String toString() {
            return "Result:" + this.result + " \n" + "Total Size:" + this.totalSize + " \n" + "Offer:" + Arrays.deepToString(this.items);
        }
    }

    public static class Item {
        private String ageRating;
        private String itemDescription;
        private String itemID;
        private String itemName;
        private float suggestedPrice;
        private String suggestedPriceType;

        public Item() {
        }

        public String getAgeRating() {
            return this.ageRating;
        }

        public String getItemDescription() {
            return this.itemDescription;
        }

        public String getItemID() {
            return this.itemID;
        }

        public String getItemName() {
            return this.itemName;
        }

        public float getSuggestedPrice() {
            return this.suggestedPrice;
        }

        public String getSuggestedPriceType() {
            return this.suggestedPriceType;
        }

        public void setAgeRating(String str) {
            this.ageRating = str;
        }

        public void setItemDescription(String str) {
            this.itemDescription = str;
        }

        public void setItemID(String str) {
            this.itemID = str;
        }

        public void setItemName(String str) {
            this.itemName = str;
        }

        public void setSuggestedPrice(float f) {
            this.suggestedPrice = f;
        }

        public void setSuggestedPriceType(String str) {
            this.suggestedPriceType = str;
        }

        public String toString() {
            return "Item ID:" + this.itemID + "\n" + "Item Name:" + this.itemName + "\n" + "Item Description:" + this.itemDescription + "\n" + "Age Rating:" + this.ageRating;
        }
    }

    public static class Offer {
        private float maxPrice;
        private float minPrice;
        private String offerID;
        private String priceLine;
        private String priceType;
        private String pricingTerms;

        public Offer() {
        }

        public float getMaxPrice() {
            return this.maxPrice;
        }

        public float getMinPrice() {
            return this.minPrice;
        }

        public String getOfferID() {
            return this.offerID;
        }

        public String getPriceLine() {
            return this.priceLine;
        }

        public String getPriceType() {
            return this.priceType;
        }

        public String getPricingTerms() {
            return this.pricingTerms;
        }

        public void setMaxPrice(float f) {
            this.maxPrice = f;
        }

        public void setMinPrice(float f) {
            this.minPrice = f;
        }

        public void setOfferID(String str) {
            this.offerID = str;
        }

        public void setPriceLine(String str) {
            this.priceLine = str;
        }

        public void setPriceType(String str) {
            this.priceType = str;
        }

        public void setPricingTerms(String str) {
            this.pricingTerms = str;
        }

        public String toString() {
            return "Offer ID:" + this.offerID + "\n" + "Max Price:" + this.maxPrice + "\n" + "Min Price:" + this.minPrice + "\n" + "Price Line:" + this.priceLine + "\n" + "Price Type:" + this.priceType + "\n" + "Pricing Terms:" + this.pricingTerms;
        }
    }

    public static class Purchase {
        private String inAppName;
        private Item item;
        private float price;
        private String priceLine;
        private String priceType;
        private String pricingTerms;
        private Date purchaseDate;
        private String purchaseID;
        private String sku;

        public Purchase() {
        }

        public String getInAppName() {
            return this.inAppName;
        }

        public Item getItem() {
            return this.item;
        }

        public float getPrice() {
            return this.price;
        }

        public String getPriceLine() {
            return this.priceLine;
        }

        public String getPriceType() {
            return this.priceType;
        }

        public String getPricingTerms() {
            return this.pricingTerms;
        }

        public Date getPurchaseDate() {
            return this.purchaseDate;
        }

        public String getPurchaseID() {
            return this.purchaseID;
        }

        public String getSku() {
            return this.sku;
        }

        public void setInAppName(String str) {
            this.inAppName = str;
        }

        public void setItem(Item item2) {
            this.item = item2;
        }

        public void setPrice(float f) {
            this.price = f;
        }

        public void setPriceLine(String str) {
            this.priceLine = str;
        }

        public void setPriceType(String str) {
            this.priceType = str;
        }

        public void setPricingTerms(String str) {
            this.pricingTerms = str;
        }

        public void setPurchaseDate(Date date) {
            this.purchaseDate = date;
        }

        public void setPurchaseID(String str) {
            this.purchaseID = str;
        }

        public void setSku(String str) {
            this.sku = str;
        }

        public String toString() {
            return "Offer ID:" + this.purchaseID + "\n" + "In App Name:" + this.inAppName + "\n" + "SKU:" + this.sku + "\n" + "Price Line:" + this.priceLine + "\n" + "Price Type:" + this.priceType + "\n" + "Pricing Terms:" + this.pricingTerms + "\n" + "Purchase Date:" + this.purchaseDate + "\n" + "Item: " + this.item + "\n";
        }
    }

    public static class PurchaseInAppContentResult {
        private String license;
        private String purchaseID;
        private Integer result;

        public PurchaseInAppContentResult() {
        }

        public String getLicense() {
            return this.license;
        }

        public String getPurchaseID() {
            return this.purchaseID;
        }

        public Integer getResult() {
            return this.result;
        }

        public void setLicense(String str) {
            this.license = str;
        }

        public void setPurchaseID(String str) {
            this.purchaseID = str;
        }

        public void setResult(Integer num) {
            this.result = num;
        }

        public String toString() {
            return "Result:" + this.result + " \n" + "License:" + this.license + " \n" + "purchaseID:" + this.purchaseID;
        }
    }

    public class PurchaseParameters {
        private Integer contentSize;
        private String inAppName;
        private String offerID;
        private float price;
        private String priceLine;
        private String priceType;
        private String pricingTerms;
        private String sku;

        public PurchaseParameters() {
        }

        public Integer getContentSize() {
            return this.contentSize;
        }

        public String getInAppName() {
            return this.inAppName;
        }

        public String getOfferID() {
            return this.offerID;
        }

        public float getPrice() {
            return this.price;
        }

        public String getPriceLine() {
            return this.priceLine;
        }

        public String getPriceType() {
            return this.priceType;
        }

        public String getPricingTerms() {
            return this.pricingTerms;
        }

        public String getSku() {
            return this.sku;
        }

        public void setContentSize(Integer num) {
            this.contentSize = num;
        }

        public void setInAppName(String str) {
            this.inAppName = str;
        }

        public void setOfferID(String str) {
            this.offerID = str;
        }

        public void setPrice(float f) {
            this.price = f;
        }

        public void setPriceLine(String str) {
            this.priceLine = str;
        }

        public void setPriceType(String str) {
            this.priceType = str;
        }

        public void setPricingTerms(String str) {
            this.pricingTerms = str;
        }

        public void setSku(String str) {
            this.sku = str;
        }

        public String toString() {
            return "In App Name:" + this.inAppName + "\n" + "SKU:" + this.sku + "\n" + "Price:" + this.price + "\n" + "Content Size:" + this.contentSize + "\n" + "Offer Id:" + this.offerID + "\n" + "Price Type:" + this.priceType + "\n" + "Price Line:" + this.priceLine + "\n" + "Pricing Terms:" + this.pricingTerms;
        }
    }

    public static class PurchasedInAppContents {
        private Purchase[] purchases;
        private Integer result;
        private Integer totalSize;

        public PurchasedInAppContents() {
        }

        public Purchase[] getPurchases() {
            return this.purchases;
        }

        public Integer getResult() {
            return this.result;
        }

        public Integer getTotalSize() {
            return this.totalSize;
        }

        public void setPurchases(Purchase[] purchaseArr) {
            this.purchases = purchaseArr;
        }

        public void setResult(Integer num) {
            this.result = num;
        }

        public void setTotalSize(Integer num) {
            this.totalSize = num;
        }

        public String toString() {
            return "Result:" + this.result + " \n" + "Total Size:" + this.totalSize + " \n" + "Purchases:" + Arrays.deepToString(this.purchases);
        }
    }

    private InAppPurchasor(Context context) {
        this.c = context;
        this.apiUtils = new APIUtils(this);
        this.isVcastUIEInstalled = isOdpInstalled("com.verizon.vcast.apps");
        this.isVcastGMInstalled = isOdpInstalled("com.gravitymobile.app.hornbill");
        if (!this.isVcastGMInstalled || this.isVcastUIEInstalled) {
            this.serviceManager = new RemoteServiceManager(context, "com.verizon.vcast.apps", "com.verizon.vcast.apps.VCastInAppService");
        } else {
            this.serviceManager = new RemoteServiceManager(context, "com.gravitymobile.app.hornbill", "com.verizon.vcast.apps.VCastInAppService");
        }
    }

    public static InAppPurchasor getInstance(Context context) {
        return instance == null ? new InAppPurchasor(context) : instance;
    }

    public synchronized int cancelInAppContentSubscription(String str, String str2, String str3) {
        int i;
        Log.i("InAppPurchasor", "begin cancelInAppContentSubscription()");
        if (str != null) {
            try {
                if (!str.equals("") && str2 != null && !str2.equals("") && str3 != null && !str3.equals("")) {
                    i = this.serviceManager.validateService();
                    if (i == 0) {
                        try {
                            int cancelInAppContentSubscription = IVCastInAppService.Stub.asInterface(this.serviceManager.getServiceBinder()).cancelInAppContentSubscription(str, str2, str3);
                            Log.i("LicenseAuthenticator", "cancelInAppContentSubscription() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                            i = cancelInAppContentSubscription;
                        } catch (RemoteException e) {
                            Log.e("InAppPurchasor", "Error getting content offers from remote service", e);
                            i = 106;
                            Log.i("LicenseAuthenticator", "cancelInAppContentSubscription() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                        }
                    }
                }
            } finally {
                Log.i("LicenseAuthenticator", "cancelInAppContentSubscription() finished.  Trying to shutDownRemoteService()");
                this.serviceManager.shutDownRemoteService(this.c);
            }
        }
        i = 101;
        Log.i("LicenseAuthenticator", "cancelInAppContentSubscription() finished.  Trying to shutDownRemoteService()");
        this.serviceManager.shutDownRemoteService(this.c);
        return i;
    }

    public synchronized InAppContentOffers getInAppContentOffer(String str, String str2) {
        Throwable th;
        InAppContentOffers inAppContentOffers;
        Log.i("InAppPurchasor", "begin getInAppContentOffer()");
        if (str != null) {
            try {
                if (!str.equals("") && str2 != null && !str2.equals("")) {
                    int validateService = this.serviceManager.validateService();
                    if (validateService != 0) {
                        inAppContentOffers = new InAppContentOffers();
                        inAppContentOffers.result = validateService;
                        Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
                        this.serviceManager.shutDownRemoteService(this.c);
                    } else {
                        try {
                            InAppContentOffers convertGetInAppContentOfferResult = this.apiUtils.convertGetInAppContentOfferResult(IVCastInAppService.Stub.asInterface(this.serviceManager.getServiceBinder()).getInAppContentOffer(str, str2));
                            Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                            inAppContentOffers = convertGetInAppContentOfferResult;
                        } catch (RemoteException e) {
                            Log.e("InAppPurchasor", "Error getting content offers from remote service", e);
                            inAppContentOffers = new InAppContentOffers();
                            inAppContentOffers.result = 101;
                            Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                        }
                    }
                }
            } catch (Throwable th2) {
                Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
                this.serviceManager.shutDownRemoteService(this.c);
            }
        }
        inAppContentOffers = new InAppContentOffers();
        try {
            inAppContentOffers.result = 101;
            Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
            this.serviceManager.shutDownRemoteService(this.c);
        } catch (Throwable th3) {
            Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
            this.serviceManager.shutDownRemoteService(this.c);
        }
        return inAppContentOffers;
    }

    public synchronized InAppContents getInAppContents(String str, DiscoveryParameters discoveryParameters) {
        Throwable th;
        InAppContents inAppContents;
        Log.i("InAppPurchasor", "begin getInAppContents()");
        if (str != null) {
            try {
                if (!str.equals("") && discoveryParameters != null) {
                    int validateService = this.serviceManager.validateService();
                    if (validateService != 0) {
                        inAppContents = new InAppContents();
                        inAppContents.result = Integer.valueOf(validateService);
                        Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                        this.serviceManager.shutDownRemoteService(this.c);
                    } else {
                        IVCastInAppService asInterface = IVCastInAppService.Stub.asInterface(this.serviceManager.getServiceBinder());
                        try {
                            InAppContents convertGetInAppContentsResult = this.apiUtils.convertGetInAppContentsResult(asInterface.getInAppContents(str, this.apiUtils.convertDiscoveryParameters(discoveryParameters)));
                            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                            inAppContents = convertGetInAppContentsResult;
                        } catch (RemoteException e) {
                            Log.e("InAppPurchasor", "Error getting content offers from remote service", e);
                            inAppContents = new InAppContents();
                            inAppContents.result = 101;
                            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                        }
                    }
                }
            } catch (Throwable th2) {
                Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                this.serviceManager.shutDownRemoteService(this.c);
            }
        }
        inAppContents = new InAppContents();
        try {
            inAppContents.result = 101;
            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
            this.serviceManager.shutDownRemoteService(this.c);
        } catch (Throwable th3) {
            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
            this.serviceManager.shutDownRemoteService(this.c);
        }
        return inAppContents;
    }

    public synchronized PurchasedInAppContents getPurchasedInAppContents(String str, DiscoveryParameters discoveryParameters) {
        Throwable th;
        PurchasedInAppContents purchasedInAppContents;
        Log.i("InAppPurchasor", "begin getPurchasedInAppContents()");
        if (str != null) {
            try {
                if (!str.equals("") && discoveryParameters != null) {
                    int validateService = this.serviceManager.validateService();
                    if (validateService != 0) {
                        purchasedInAppContents = new PurchasedInAppContents();
                        purchasedInAppContents.result = Integer.valueOf(validateService);
                        Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                        this.serviceManager.shutDownRemoteService(this.c);
                    } else {
                        IVCastInAppService asInterface = IVCastInAppService.Stub.asInterface(this.serviceManager.getServiceBinder());
                        try {
                            PurchasedInAppContents convertGetPurchasedInAppContentsResult = this.apiUtils.convertGetPurchasedInAppContentsResult(asInterface.getPurchasedInAppContents(str, this.apiUtils.convertDiscoveryParameters(discoveryParameters)));
                            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                            purchasedInAppContents = convertGetPurchasedInAppContentsResult;
                        } catch (RemoteException e) {
                            Log.e("InAppPurchasor", "Error getting content offers from remote service", e);
                            purchasedInAppContents = new PurchasedInAppContents();
                            purchasedInAppContents.result = 101;
                            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                            this.serviceManager.shutDownRemoteService(this.c);
                        }
                    }
                }
            } catch (Throwable th2) {
                Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
                this.serviceManager.shutDownRemoteService(this.c);
            }
        }
        purchasedInAppContents = new PurchasedInAppContents();
        try {
            purchasedInAppContents.result = 101;
            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
            this.serviceManager.shutDownRemoteService(this.c);
        } catch (Throwable th3) {
            Log.i("LicenseAuthenticator", "getInAppContents() finished.  Trying to shutDownRemoteService()");
            this.serviceManager.shutDownRemoteService(this.c);
        }
        return purchasedInAppContents;
    }

    /* JADX INFO: finally extract failed */
    public synchronized int purchaseInAppContent(String str, String str2, PurchaseParameters purchaseParameters) {
        int i;
        Log.i("InAppPurchasor", "begin PurchaseInAppContentResult()");
        if (str != null) {
            try {
                if (!str.equals("") && str2 != null && !str2.equals("")) {
                    try {
                        com.verizon.vcast.apps.PurchaseParameters convertPurchaseParameters = this.apiUtils.convertPurchaseParameters(purchaseParameters);
                        Intent intent = new Intent();
                        if (this.isVcastUIEInstalled) {
                            intent.setComponent(new ComponentName("com.verizon.vcast.apps", "com.vzw.inapp.VZWInAppPurchase"));
                        } else {
                            intent.setComponent(new ComponentName("com.gravitymobile.app.hornbill", "com.vzw.inapp.VZWInAppPurchase"));
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("keyword", str);
                        bundle.putString("itemID", str2);
                        bundle.putParcelable("purchaseParameters", convertPurchaseParameters);
                        intent.putExtra("purchaseInAppContentArguments", bundle);
                        ((Activity) this.c).startActivityForResult(intent, InAppActivity.purchaseInAppContentRequestCode);
                        Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
                        this.serviceManager.shutDownRemoteService(this.c);
                        this.itemIDBeingPurchased = str2;
                        i = 10;
                    } catch (Exception e) {
                        Log.e("InAppPurchasor", "Error intiating In App Purchase", e);
                        i = 106;
                        Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
                        this.serviceManager.shutDownRemoteService(this.c);
                    }
                }
            } catch (Throwable th) {
                Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
                this.serviceManager.shutDownRemoteService(this.c);
                throw th;
            }
        }
        i = 101;
        Log.i("LicenseAuthenticator", "getInAppContentOffer() finished.  Trying to shutDownRemoteService()");
        this.serviceManager.shutDownRemoteService(this.c);
        return i;
    }
}
