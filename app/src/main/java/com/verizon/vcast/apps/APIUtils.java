package com.verizon.vcast.apps;

public class APIUtils {
    InAppPurchasor iap;

    APIUtils(InAppPurchasor inAppPurchasor) {
        this.iap = inAppPurchasor;
    }

    public DiscoveryParameters convertDiscoveryParameters(InAppPurchasor.DiscoveryParameters discoveryParameters) {
        DiscoveryParameters discoveryParameters2 = new DiscoveryParameters();
        discoveryParameters2.ascendingOrder = discoveryParameters.isAscendingOrder();
        discoveryParameters2.maxResults = discoveryParameters.getMaxResults();
        discoveryParameters2.sortBy = discoveryParameters.getSortBy();
        discoveryParameters2.startIndex = discoveryParameters.getStartIndex();
        return discoveryParameters2;
    }

    public InAppPurchasor.InAppContentOffers convertGetInAppContentOfferResult(InAppContentOffers inAppContentOffers) {
        InAppPurchasor inAppPurchasor = this.iap;
        inAppPurchasor.getClass();
        InAppPurchasor.InAppContentOffers inAppContentOffers2 = new InAppPurchasor.InAppContentOffers();
        inAppContentOffers2.setResult(inAppContentOffers.result);
        inAppContentOffers2.setTotalSize(inAppContentOffers.totalSize);
        InAppPurchasor.Offer[] offerArr = new InAppPurchasor.Offer[inAppContentOffers.offers.length];
        for (int i = 0; i < inAppContentOffers.offers.length; i++) {
            offerArr[i] = convertOffer(inAppContentOffers.offers[i]);
        }
        inAppContentOffers2.setOffers(offerArr);
        return inAppContentOffers2;
    }

    public InAppPurchasor.InAppContents convertGetInAppContentsResult(InAppContents inAppContents) {
        InAppPurchasor inAppPurchasor = this.iap;
        inAppPurchasor.getClass();
        InAppPurchasor.InAppContents inAppContents2 = new InAppPurchasor.InAppContents();
        inAppContents2.setResult(inAppContents.result);
        inAppContents2.setTotalSize(inAppContents.totalSize);
        inAppContents2.setItems(convertItemArray(inAppContents.items));
        return inAppContents2;
    }

    public InAppPurchasor.PurchasedInAppContents convertGetPurchasedInAppContentsResult(PurchasedInAppContents purchasedInAppContents) {
        InAppPurchasor inAppPurchasor = this.iap;
        inAppPurchasor.getClass();
        InAppPurchasor.PurchasedInAppContents purchasedInAppContents2 = new InAppPurchasor.PurchasedInAppContents();
        purchasedInAppContents2.setResult(purchasedInAppContents.result);
        purchasedInAppContents2.setTotalSize(purchasedInAppContents.totalSize);
        purchasedInAppContents2.setPurchases(convertPurchaseArray(purchasedInAppContents.purchases));
        return purchasedInAppContents2;
    }

    public InAppPurchasor.Item convertItem(Item item) {
        InAppPurchasor inAppPurchasor = this.iap;
        inAppPurchasor.getClass();
        InAppPurchasor.Item item2 = new InAppPurchasor.Item();
        item2.setAgeRating(item.ageRating);
        item2.setItemDescription(item.itemDescription);
        item2.setItemID(item.itemID);
        item2.setItemName(item.itemName);
        return item2;
    }

    public InAppPurchasor.Item[] convertItemArray(Item[] itemArr) {
        InAppPurchasor.Item[] itemArr2 = new InAppPurchasor.Item[itemArr.length];
        for (int i = 0; i < itemArr.length; i++) {
            itemArr2[i] = convertItem(itemArr[i]);
        }
        return itemArr2;
    }

    public InAppPurchasor.Offer convertOffer(Offer offer) {
        InAppPurchasor inAppPurchasor = this.iap;
        inAppPurchasor.getClass();
        InAppPurchasor.Offer offer2 = new InAppPurchasor.Offer();
        offer2.setOfferID(offer.offerID);
        offer2.setMaxPrice(offer.maxPrice);
        offer2.setMinPrice(offer.minPrice);
        offer2.setPriceLine(offer.priceLine);
        offer2.setPriceType(offer.priceType);
        offer2.setPricingTerms(offer.pricingTerms);
        return offer2;
    }

    public InAppPurchasor.Purchase convertPurchase(Purchase purchase) {
        if (purchase == null) {
            return null;
        }
        InAppPurchasor inAppPurchasor = this.iap;
        inAppPurchasor.getClass();
        InAppPurchasor.Purchase purchase2 = new InAppPurchasor.Purchase();
        purchase2.setInAppName(purchase.inAppName);
        purchase2.setItem(convertItem(purchase.item));
        purchase2.setPrice(purchase.price);
        purchase2.setPriceLine(purchase.priceLine);
        purchase2.setPriceType(purchase.priceType);
        purchase2.setPricingTerms(purchase.pricingTerms);
        purchase2.setPurchaseDate(purchase.purchaseDate);
        purchase2.setSku(purchase.sku);
        purchase2.setPurchaseID(purchase.purchaseID);
        return purchase2;
    }

    public InAppPurchasor.Purchase[] convertPurchaseArray(Purchase[] purchaseArr) {
        if (purchaseArr == null) {
            return null;
        }
        InAppPurchasor.Purchase[] purchaseArr2 = new InAppPurchasor.Purchase[purchaseArr.length];
        for (int i = 0; i < purchaseArr.length; i++) {
            purchaseArr2[i] = convertPurchase(purchaseArr[i]);
        }
        return purchaseArr2;
    }

    public InAppPurchasor.PurchaseInAppContentResult convertPurchaseInAppContentResult(PurchaseInAppContentResult purchaseInAppContentResult) {
        InAppPurchasor inAppPurchasor = this.iap;
        inAppPurchasor.getClass();
        InAppPurchasor.PurchaseInAppContentResult purchaseInAppContentResult2 = new InAppPurchasor.PurchaseInAppContentResult();
        purchaseInAppContentResult2.setLicense(purchaseInAppContentResult.license);
        purchaseInAppContentResult2.setPurchaseID(purchaseInAppContentResult.purchaseID);
        purchaseInAppContentResult2.setResult(purchaseInAppContentResult.result);
        return purchaseInAppContentResult2;
    }

    public PurchaseParameters convertPurchaseParameters(InAppPurchasor.PurchaseParameters purchaseParameters) {
        PurchaseParameters purchaseParameters2 = new PurchaseParameters();
        purchaseParameters2.contentSize = Integer.valueOf(purchaseParameters.getContentSize() == null ? 0 : purchaseParameters.getContentSize().intValue());
        purchaseParameters2.inAppName = purchaseParameters.getInAppName();
        purchaseParameters2.offerID = purchaseParameters.getOfferID();
        purchaseParameters2.price = purchaseParameters.getPrice();
        purchaseParameters2.sku = purchaseParameters.getSku();
        purchaseParameters2.priceType = purchaseParameters.getPriceType();
        purchaseParameters2.priceLine = purchaseParameters.getPriceLine();
        purchaseParameters2.pricingTerms = purchaseParameters.getPricingTerms();
        return purchaseParameters2;
    }
}
