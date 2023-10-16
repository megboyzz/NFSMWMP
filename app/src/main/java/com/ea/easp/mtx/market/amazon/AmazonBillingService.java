package com.ea.easp.mtx.market.amazon;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;

import com.ea.easp.Debug;
import com.ea.easp.mtx.market.BillingService;
import com.ea.easp.mtx.market.Consts;
import com.ea.easp.mtx.market.ResponseHandler;
import com.ea.easp.mtx.market.Security;
import com.ea.easp.mtx.market.SecurityJNI;
import com.ea.nimble.Global;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class AmazonBillingService implements BillingService, AmazonPurchasingObserver.AmazonPurchasingEventHandler {
    private static final String TAG = "AmazonBillingService";
    private static SecurityJNI mSecurityJNI;
    private static HashMap<String, BillingRequest> mSentRequests = new HashMap<>();
    private Context mContext = null;
    private AmazonPurchasingObserver mObserver = null;

    @Override
    public void onItemDataResponse(Object itemDataResponse) {

    }

    @Override
    public void onPurchaseResponse(Object purchaseResponse) {

    }

    @Override
    public void onPurchaseUpdatesResponse(Object purchaseUpdatesResponse) {

    }

    /* access modifiers changed from: package-private */
    public abstract class BillingRequest {
        protected Runnable mErrorHandler = null;
        protected String mRequestId;

        public BillingRequest(Runnable runnable) {
            this.mErrorHandler = runnable;
        }

        /* access modifiers changed from: protected */
        public boolean handleRunErrorWhenConnected(Exception exc) {
            Debug.Log.e(AmazonBillingService.TAG, getClass() + " failed.", exc);
            if (this.mErrorHandler == null) {
                return false;
            }
            this.mErrorHandler.run();
            return false;
        }

        /* access modifiers changed from: protected */
        public void logResponseCode(String str, Bundle bundle) {
            Debug.Log.i(AmazonBillingService.TAG, str + " received " + Consts.ResponseCode.valueOf(bundle.getInt("RESPONSE_CODE")).toString());
        }

        /* access modifiers changed from: protected */
        public void onRemoteException(RemoteException remoteException) {
            Debug.Log.w(AmazonBillingService.TAG, "remote billing service crashed");
            AmazonBillingService.this.mObserver = null;
        }

        /* access modifiers changed from: protected */
        public void responseCodeReceived(Consts.ResponseCode responseCode) {
        }

        /* access modifiers changed from: protected */
        public abstract String run() throws RemoteException;

        public boolean runRequest() {
            try {
                this.mRequestId = run();
                Debug.Log.d(AmazonBillingService.TAG, "request id: " + this.mRequestId);
                if (this.mRequestId != null && !this.mRequestId.equals("")) {
                    AmazonBillingService.mSentRequests.put(this.mRequestId, this);
                }
                return true;
            } catch (Exception e) {
                Debug.Log.e(AmazonBillingService.TAG, "Exception : " + e);
                handleRunErrorWhenConnected(e);
                return false;
            }
        }
    }

    class CheckBillingSupported extends BillingRequest {
        public CheckBillingSupported(Runnable runnable) {
            super(runnable);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.amazon.AmazonBillingService.BillingRequest
        public String run() throws RemoteException {
            ResponseHandler.checkBillingSupportedResponse(true);
            return Global.NOTIFICATION_DICTIONARY_RESULT_SUCCESS;
        }
    }

    /* access modifiers changed from: package-private */
    public class RequestPurchase extends BillingRequest implements BillingService.IRequestPurchase {
        private final String mDeveloperPayload;
        private final String mProductId;

        public RequestPurchase(AmazonBillingService amazonBillingService, String str, Runnable runnable) {
            this(str, null, runnable);
        }

        public RequestPurchase(String str, String str2, Runnable runnable) {
            super(runnable);
            this.mProductId = AmazonBillingService.this.mContext.getPackageName() + "." + str;
            this.mDeveloperPayload = str2;
        }

        public String getDeveloperPayload() {
            return this.mDeveloperPayload;
        }

        @Override // com.ea.easp.mtx.market.BillingService.IRequestPurchase
        public String getProductId() {
            return this.mProductId;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.amazon.AmazonBillingService.BillingRequest
        public void responseCodeReceived(Consts.ResponseCode responseCode) {
            ResponseHandler.responseCodeReceived(this, responseCode);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.amazon.AmazonBillingService.BillingRequest
        public String run() throws RemoteException {
            return "";
        }
    }

    class RestoreTransactions extends BillingRequest implements BillingService.IRestoreTransactions {
        public RestoreTransactions(Runnable runnable) {
            super(runnable);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.amazon.AmazonBillingService.BillingRequest
        public void onRemoteException(RemoteException remoteException) {
            super.onRemoteException(remoteException);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.amazon.AmazonBillingService.BillingRequest
        public void responseCodeReceived(Consts.ResponseCode responseCode) {
            ResponseHandler.responseCodeReceived(this, responseCode);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.amazon.AmazonBillingService.BillingRequest
        public String run() throws RemoteException {
            return "";
        }
    }

    public AmazonBillingService(SecurityJNI securityJNI, Context context) {
        this.mContext = context;
        this.mObserver = new AmazonPurchasingObserver((Activity) context, this);
        mSecurityJNI = securityJNI;
    }

    private void sendPurchases(String str, boolean z) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString("requestId");
            jSONObject.getString("userId");
            JSONArray jSONArray = jSONObject.getJSONArray("receipts");
            BillingRequest billingRequest = mSentRequests.get(string);
            ArrayList arrayList = new ArrayList();
            String str2 = "";
            if (billingRequest instanceof RequestPurchase) {
                str2 = ((RequestPurchase) billingRequest).getDeveloperPayload();
            }
            for (int i = 0; i < jSONArray.length(); i++) {
                arrayList.add(new Security.VerifiedPurchase(Consts.PurchaseState.PURCHASED, "", jSONArray.getJSONObject(i).getString("sku").substring(this.mContext.getPackageName().length() + 1), "", 0, str2));
            }
            ResponseHandler.purchaseResponse(arrayList, z, "", "");
        } catch (Exception e) {
            ResponseHandler.purchaseResponse(null, false, "", "");
        }
    }

    private void verifyReceipts(String str, String str2, Collection<Integer> collection) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("requestId", str);
            jSONObject.put("userId", str2);
            JSONArray jSONArray = new JSONArray();

            jSONObject.put("receipts", jSONArray);
            mSecurityJNI.verify(jSONObject.toString(), "", -1, Consts.STORE_AMAZON);
        } catch (Exception e) {
            ResponseHandler.purchaseResponse(null, false, "", "");
        }
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void OnNonceSucceed(long j, Object obj) {
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void OnVerifyMarketResponse(boolean z, String str, String str2, int i) {
        Debug.Log.w(TAG, "OnVerifyMarketResponse, verified=" + z + " signature=" + str2 + " startId=" + i);
        Debug.Log.w(TAG, "OnVerifyMarketResponse, signedData=" + str);
        sendPurchases(str, z);
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void checkBillingSupported(Runnable runnable) {
        new CheckBillingSupported(runnable).runRequest();
    }



    @Override // com.ea.easp.mtx.market.BillingService
    public void requestPurchase(String str, String str2, String str3, Runnable runnable) {
        new RequestPurchase(str, str3, runnable).runRequest();
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void restoreTransactions(long j, Runnable runnable) {
        new RestoreTransactions(runnable).runRequest();
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void setContext(Context context) {
        this.mContext = context;
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void shutdown() {
    }
}
