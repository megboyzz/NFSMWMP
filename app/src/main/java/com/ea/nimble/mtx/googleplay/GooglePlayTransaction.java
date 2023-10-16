/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.mtx.googleplay;

import com.ea.nimble.Error;
import com.ea.nimble.Utility;
import com.ea.nimble.mtx.INimbleMTX;
import com.ea.nimble.mtx.NimbleMTXTransaction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GooglePlayTransaction
implements NimbleMTXTransaction,
Externalizable {
    Map<String, Serializable> mAdditionalInfo;
    GooglePlayCatalogItem mCatalogItem = null;
    String mDeveloperPayload = "";
    Exception mError = null;
    GooglePlayTransactionState mFailedState = null;
    INimbleMTX.FinalizeTransactionCallback mFinalizeCallback = null;
    GooglePlayTransactionState mGooglePlayTransactionState = GooglePlayTransactionState.UNDEFINED;
    boolean mIsRecorded = false;
    INimbleMTX.ItemGrantedCallback mItemGrantedCallback = null;
    String mItemSku = "";
    String mNonce = "";
    float mPriceDecimal = 0.0f;
    INimbleMTX.PurchaseTransactionCallback mPurchaseCallback = null;
    String mReceipt = "";
    Date mTimeStamp = null;
    String mTransactionId = "";
    NimbleMTXTransaction.TransactionType mTransactionType = NimbleMTXTransaction.TransactionType.PURCHASE;

    public GooglePlayTransaction() {
        this.mAdditionalInfo = new HashMap<String, Serializable>();
    }

    @Override
    public Map<String, Object> getAdditionalInfo() {
        return new HashMap<String, Object>(this.mAdditionalInfo);
    }

    public GooglePlayCatalogItem getCatalogItem() {
        return this.mCatalogItem;
    }

    public String getDeveloperPayload() {
        return this.mDeveloperPayload;
    }

    @Override
    public Exception getError() {
        return this.mError;
    }

    @Override
    public String getItemSku() {
        return this.mItemSku;
    }

    public String getNonce() {
        return this.mNonce;
    }

    @Override
    public float getPriceDecimal() {
        return this.mPriceDecimal;
    }

    @Override
    public String getReceipt() {
        return this.mReceipt;
    }

    @Override
    public Date getTimeStamp() {
        return this.mTimeStamp;
    }

    @Override
    public String getTransactionId() {
        return this.mTransactionId;
    }

    @Override
    public NimbleMTXTransaction.TransactionState getTransactionState() {
        switch (this.mGooglePlayTransactionState.ordinal()) {
            default: {
                return NimbleMTXTransaction.TransactionState.UNDEFINED;
            }
            case 1: {
                return NimbleMTXTransaction.TransactionState.USER_INITIATED;
            }
            case 2: {
                return NimbleMTXTransaction.TransactionState.WAITING_FOR_PREPURCHASE_INFO;
            }
            case 3: {
                return NimbleMTXTransaction.TransactionState.WAITING_FOR_PLATFORM_RESPONSE;
            }
            case 4: {
                return NimbleMTXTransaction.TransactionState.WAITING_FOR_VERIFICATION;
            }
            case 5: {
                return NimbleMTXTransaction.TransactionState.WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT;
            }
            case 6: {
                return NimbleMTXTransaction.TransactionState.WAITING_FOR_PLATFORM_CONSUMPTION;
            }
            case 7: {
                return NimbleMTXTransaction.TransactionState.COMPLETE;
            }
            case 8: 
        }
        return NimbleMTXTransaction.TransactionState.UNDEFINED;
    }

    @Override
    public NimbleMTXTransaction.TransactionType getTransactionType() {
        return this.mTransactionType;
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.mTransactionId = objectInput.readUTF();
        this.mItemSku = objectInput.readUTF();
        this.mGooglePlayTransactionState = (GooglePlayTransactionState)((Object)objectInput.readObject());
        this.mTransactionType = (NimbleMTXTransaction.TransactionType)((Object)objectInput.readObject());
        this.mPriceDecimal = objectInput.readFloat();
        this.mTimeStamp = (Date)objectInput.readObject();
        this.mReceipt = objectInput.readUTF();
        this.mNonce = objectInput.readUTF();
        this.mError = (Error)objectInput.readObject();
        this.mCatalogItem = (GooglePlayCatalogItem)objectInput.readObject();
        this.mAdditionalInfo = (Map)objectInput.readObject();
        this.mDeveloperPayload = objectInput.readUTF();
        try {
            this.mFailedState = (GooglePlayTransactionState)((Object)objectInput.readObject());
            this.mIsRecorded = objectInput.readBoolean();
            return;
        }
        catch (IOException iOException) {
            return;
        }
    }

    public String toString() {
        return "GooglePlayTransaction: SKU(" + this.getItemSku() + ") " + "State(" + this.mGooglePlayTransactionState.toString() + ") " + "Receipt(" + this.getReceipt() + ") " + "TimeStamp(" + this.getTimeStamp() + ")";
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeUTF(Utility.safeString(this.mTransactionId));
        objectOutput.writeUTF(Utility.safeString(this.mItemSku));
        objectOutput.writeObject((Object)this.mGooglePlayTransactionState);
        objectOutput.writeObject((Object)this.mTransactionType);
        objectOutput.writeFloat(this.mPriceDecimal);
        objectOutput.writeObject(this.mTimeStamp);
        objectOutput.writeUTF(Utility.safeString(this.mReceipt));
        objectOutput.writeUTF(Utility.safeString(this.mNonce));
        objectOutput.writeObject(this.mError);
        objectOutput.writeObject(this.mCatalogItem);
        objectOutput.writeObject(this.mAdditionalInfo);
        objectOutput.writeUTF(Utility.safeString(this.mDeveloperPayload));
        objectOutput.writeObject((Object)this.mFailedState);
        objectOutput.writeBoolean(this.mIsRecorded);
    }

    public enum GooglePlayTransactionState {
        UNDEFINED("UNDEFINED"),
        USER_INITIATED("USER_INITIATED"),
        WAITING_FOR_NONCE("WAITING_FOR_NONCE"),
        WAITING_FOR_GOOGLEPLAY_ACTIVITY_RESPONSE("WAITING_FOR_GOOGLEPLAY_ACTIVITY_RESPONSE"),
        WAITING_FOR_SYNERGY_VERIFICATION("WAITING_FOR_SYNERGY_VERIFICATION"),
        WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT("WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT"),
        WAITING_FOR_GOOGLEPLAY_CONSUMPTION("WAITING_FOR_GOOGLEPLAY_CONSUMPTION"),
        COMPLETE("COMPLETE");

        private String title;

        GooglePlayTransactionState(String title) {
            this.title = title;
        }
    }
}

