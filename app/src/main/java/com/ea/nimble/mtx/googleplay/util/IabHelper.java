/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.PendingIntent
 *  android.content.BroadcastReceiver
 *  android.content.ComponentName
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.content.IntentSender$SendIntentException
 *  android.content.ServiceConnection
 *  android.content.pm.ResolveInfo
 *  android.os.Bundle
 *  android.os.Handler
 *  android.os.IBinder
 *  android.os.Looper
 *  android.os.RemoteException
 *  android.text.TextUtils
 *  org.json.JSONException
 */
package com.ea.nimble.mtx.googleplay.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.ea.ironmonkey.devmenu.util.Observer;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IabHelper
implements LogSource {
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
    public static final String GET_SKU_DETAILS_ITEM_TYPE_LIST = "ITEM_TYPE_LIST";
    public static final int IABHELPER_BAD_RESPONSE = -1002;
    public static final int IABHELPER_BAD_STATE_ERROR = -1008;
    public static final int IABHELPER_ERROR_BASE = -1000;
    public static final int IABHELPER_MISSING_TOKEN = -1007;
    public static final int IABHELPER_REMOTE_EXCEPTION = -1001;
    public static final int IABHELPER_SEND_INTENT_FAILED = -1004;
    public static final int IABHELPER_UNKNOWN_ERROR = -1009;
    public static final int IABHELPER_UNKNOWN_PURCHASE_RESPONSE = -1006;
    public static final int IABHELPER_USER_CANCELLED = -1005;
    public static final int IABHELPER_VERIFICATION_FAILED = -1003;
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    boolean mAsyncInProgress = false;
    AsyncOperation mAsyncOperation;
    LinkedList<AsyncOperation> mAsyncRequestQueue;
    Context mContext;
    boolean mDebugLog = false;
    String mDebugTag = "IabHelper";
    OnIabPurchaseFinishedListener mPurchaseListener;
    IabPurchaseUpdateReceiver mPurchaseUpdateReceiver = null;
    int mRequestCode;
    ServiceConnection mServiceConn;
    boolean mSetupDone = false;
    String mSignatureBase64 = null;

    public IabHelper(Context context, String string2) {
        this.mContext = context.getApplicationContext();
        if (this.mContext == null) {
            this.logError("IabHelper initializing with null application context!");
        }
        this.mSignatureBase64 = string2;
        this.logDebug("IAB helper created.");
        this.mAsyncRequestQueue = new LinkedList();
    }

    private void checkAndPopAsyncQueue() {
        if (this.mAsyncRequestQueue.size() <= 0) return;
        AsyncOperation asyncOperation = this.mAsyncRequestQueue.removeFirst();
        this.flagStartAsync(asyncOperation);
        asyncOperation.run();
    }

    private void enqueueOperation(AsyncOperation asyncOperation) {
        this.logDebug("Enqueuing operation " + asyncOperation.getName() + " to execute after current async op, " + this.mAsyncOperation.getName() + ", is finished.");
        this.mAsyncRequestQueue.add(asyncOperation);
    }

    public static String getResponseDesc(int n2) {
        String[] stringArray = "0:OK/1:User Canceled/2:Unknown/3:Billing Unavailable/4:Item unavailable/5:Developer Error/6:Error/7:Item Already Owned/8:Item not owned".split("/");
        String[] stringArray2 = "0:OK/-1001:Remote exception during initialization/-1002:Bad response received/-1003:Purchase signature verification failed/-1004:Send intent failed/-1005:User cancelled/-1006:Unknown purchase response/-1007:Missing token/-1008:Unknown error".split("/");
        if (n2 <= -1000) {
            int n3 = -1000 - n2;
            if (n3 < 0) return String.valueOf(n2) + ":Unknown IAB Helper Error";
            if (n3 >= stringArray2.length) return String.valueOf(n2) + ":Unknown IAB Helper Error";
            return stringArray2[n3];
        }
        if (n2 < 0) return String.valueOf(n2) + ":Unknown";
        if (n2 < stringArray.length) return stringArray[n2];
        return String.valueOf(n2) + ":Unknown";
    }

    private void startOrQueueRunnable(AsyncOperation asyncOperation) {
        synchronized (this) {
            if (this.isAsyncInProgress()) {
                this.enqueueOperation(asyncOperation);
            } else {
                this.flagStartAsync(asyncOperation);
                asyncOperation.run();
            }
            return;
        }
    }

    void checkSetupDone(String string2) {
        if (this.mSetupDone) return;
        this.logError("Illegal state for operation (" + string2 + "): IAB helper is not set up.");
        throw new IllegalStateException("IAB helper is not set up. Can't perform operation: " + string2);
    }

    void consume(Purchase purchase) throws IabException {
        String string2;
        String string3;
        this.checkSetupDone("consume");
        string3 = purchase.getToken();
        string2 = purchase.getSku();
        if (string3 == null || string3.equals("")) {
            this.logError("Can't consume " + string2 + ". No token.");
            throw new IabException(-1007, "PurchaseInfo is missing token for sku: " + string2 + " " + purchase);
        }
        this.logDebug("Consuming sku: " + string2 + ", token: " + string3);
        int n2 = 0;
        this.logDebug("Successfully consumed sku: " + string2);
    }

    public void consumeAsync(Purchase purchase, OnConsumeFinishedListener onConsumeFinishedListener) {
        ArrayList<Purchase> arrayList = new ArrayList<Purchase>();
        arrayList.add(purchase);
        this.consumeAsyncInternal(arrayList, onConsumeFinishedListener, null);
    }

    public void consumeAsync(List<Purchase> list, OnConsumeMultiFinishedListener onConsumeMultiFinishedListener) {
        this.consumeAsyncInternal(list, null, onConsumeMultiFinishedListener);
    }

    void consumeAsyncInternal(final List<Purchase> list, final OnConsumeFinishedListener onConsumeFinishedListener, OnConsumeMultiFinishedListener onConsumeMultiFinishedListener) {
        Looper looper;
        Looper looper2 = looper = Looper.myLooper();
        if (looper == null) {
            looper2 = Looper.getMainLooper();
        }
        final Handler h = new Handler(looper2);
        this.startOrQueueRunnable(new AsyncOperation("consume", true, new Runnable(){
            final /* synthetic */ Handler handler = h;
            /* synthetic */ final OnConsumeMultiFinishedListener val$multiListener = onConsumeMultiFinishedListener;

            @Override
            public void run() {
                final ArrayList<IabResult> arrayList = new ArrayList<IabResult>();
                for (Purchase purchase : list) {
                    try {
                        IabHelper.this.consume(purchase);
                        arrayList.add(new IabResult(0, "Successful consume of sku " + purchase.getSku()));
                    }
                    catch (IabException iabException) {
                        arrayList.add(iabException.getResult());
                    }
                }
                IabHelper.this.flagEndAsync();
                if (onConsumeFinishedListener != null) {
                    this.handler.post(() -> onConsumeFinishedListener.onConsumeFinished((Purchase)list.get(0), (IabResult)arrayList.get(0)));
                }
                if (this.val$multiListener == null) return;
                this.handler.post(() -> val$multiListener.onConsumeMultiFinished(list, arrayList));
            }
        }));
    }

    public void dispose() {
        this.logDebug("Disposing.");
        this.mSetupDone = false;
        if (this.mServiceConn == null) return;
        this.logDebug("Unbinding from service.");
        if (this.mContext != null) {
            this.mContext.unbindService(this.mServiceConn);
        }
        this.mServiceConn = null;
        this.mPurchaseListener = null;
    }

    public void enableDebugLogging(boolean bl2) {
        this.mDebugLog = bl2;
    }

    public void enableDebugLogging(boolean bl2, String string2) {
        this.mDebugLog = bl2;
        this.mDebugTag = string2;
    }

    void flagEndAsync() {
        synchronized (this) {
            this.logDebug("Ending async operation: " + this.mAsyncOperation.getName());
            this.mAsyncOperation = null;
            this.mAsyncInProgress = false;
            this.checkAndPopAsyncQueue();
            return;
        }
    }

    void flagStartAsync(AsyncOperation asyncOperation) {
        if (this.mAsyncInProgress) {
            throw new IllegalStateException("Can't start async operation (" + asyncOperation.getName() + ") because another async operation(" + this.mAsyncOperation.getName() + ") is in progress.");
        }
        this.mAsyncOperation = asyncOperation;
        this.mAsyncInProgress = true;
        this.logDebug("Starting async operation: " + asyncOperation.getName());
    }

    @Override
    public String getLogSourceTitle() {
        return "MTX Google IABHelper";
    }

    int getResponseCodeFromBundle(Bundle object) {
        if (object.get(RESPONSE_CODE) == null) {
            this.logDebug("Bundle with null response code, assuming OK (known issue)");
            return 0;
        }
        this.logError("Unexpected type for bundle response code.");
        this.logError(object.getClass().getName());
        throw new RuntimeException("Unexpected type for bundle response code: " + object.getClass().getName());
    }

    int getResponseCodeFromIntent(Intent object) {
        if (object.getExtras().get(RESPONSE_CODE) == null) {
            this.logError("Intent with no response code, assuming OK (known issue)");
            return 0;
        }
        this.logError("Unexpected type for intent response code.");
        this.logError(object.getClass().getName());
        throw new RuntimeException("Unexpected type for intent response code: " + object.getClass().getName());
    }

    public boolean handleActivityResult(int n2, int n3, Intent object) {
        this.logDebug("handleActivityResult...");
        if (n2 != this.mRequestCode) {
            return false;
        }
        this.checkSetupDone("handleActivityResult");
        this.flagEndAsync();
        if (object == null) {
            this.logError("Null data in IAB activity result.");
            IabResult result = new IabResult(-1002, "Null data in IAB result");
            if (this.mPurchaseListener == null) return true;
            this.mPurchaseListener.onIabPurchaseFinished(result, null);
            return true;
        }
        n2 = this.getResponseCodeFromIntent((Intent)object);
        String string2 = object.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
        String string3 = object.getStringExtra(RESPONSE_INAPP_SIGNATURE);
        if (n3 == -1 && n2 == 0) {
            this.logDebug("Successful resultcode from purchase activity.");
            this.logDebug("Purchase data: " + string2);
            this.logDebug("Data signature: " + string3);
            this.logDebug("Extras: " + object.getExtras());
            if (string2 == null || string3 == null) {
                this.logError("BUG: either purchaseData or dataSignature is null.");
                this.logDebug("Extras: " + object.getExtras().toString());
                IabResult iabResult = new IabResult(-1009, "IAB returned null purchaseData or dataSignature");
                if (this.mPurchaseListener == null) return true;
                this.mPurchaseListener.onIabPurchaseFinished(iabResult, null);
                return true;
            }
            try {
                Purchase purchase = new Purchase(string2, string3);
                if (this.mPurchaseListener == null) return true;
                this.mPurchaseListener.onIabPurchaseFinished(new IabResult(0, "Success"), purchase);
                return true;
            }
            catch (JSONException jSONException) {
                this.logError("Failed to parse purchase data.");
                jSONException.printStackTrace();
                IabResult iabResult = new IabResult(-1002, "Failed to parse purchase data.");
                if (this.mPurchaseListener == null) return true;
                this.mPurchaseListener.onIabPurchaseFinished(iabResult, null);
                return true;
            }
        }
        if (n3 == -1) {
            this.logDebug("Result code was OK but in-app billing response was not OK: " + IabHelper.getResponseDesc(n2));
            if (this.mPurchaseListener == null) return true;
            IabResult iabResult = new IabResult(n2, "Problem purchashing item.");
            this.mPurchaseListener.onIabPurchaseFinished(iabResult, null);
            return true;
        }
        if (n3 == 0) {
            this.logDebug("Google Play Activity canceled - Response: " + IabHelper.getResponseDesc(n2));
            IabResult iabResult = new IabResult(n2, "Problem purchashing item.");
            if (this.mPurchaseListener == null) return true;
            this.mPurchaseListener.onIabPurchaseFinished(iabResult, null);
            return true;
        }
        this.logError("Purchase failed. Result code: " + Integer.toString(n3) + ". Response: " + IabHelper.getResponseDesc(n2));
        IabResult iabResult = new IabResult(-1006, "Unknown purchase response.");
        if (this.mPurchaseListener == null) return true;
        this.mPurchaseListener.onIabPurchaseFinished(iabResult, null);
        return true;
    }

    boolean isAsyncInProgress() {
        return this.mAsyncInProgress;
    }

    public boolean isServiceAvailable() {
        return this.mSetupDone;
    }

    public void launchPurchaseFlow(Activity activity, String string2, int n2, OnIabPurchaseFinishedListener onIabPurchaseFinishedListener) {
        this.launchPurchaseFlow(activity, string2, n2, onIabPurchaseFinishedListener, "");
    }

    public void launchPurchaseFlow(final Activity activity, final String string2, final int n2, final OnIabPurchaseFinishedListener onIabPurchaseFinishedListener, final String string3) {
        synchronized (this) {
            this.startOrQueueRunnable(new AsyncOperation("launchPurchaseFlow", false, new Runnable(){

                /*
                 * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
                 * Enabled unnecessary exception pruning
                 */
                @Override
                public void run() {
                    Object object = new Object();
                    IabHelper.this.logDebug("Constructing buy intent for " + string2);
                    Bundle bundle = new Bundle();
                    int n22 = IabHelper.this.getResponseCodeFromBundle(bundle);
                    if (n22 != 0) {
                        IabHelper.this.logDebug("BuyIntent Bundle: " + object);
                        IabHelper.this.logError("Unable to buy item, Error response: " + IabHelper.getResponseDesc(n22));
                        IabHelper.this.flagEndAsync();
                        object = new IabResult(n22, "Unable to buy item");
                        OnIabPurchaseFinishedListener onIabPurchaseFinishedListener2 = onIabPurchaseFinishedListener;
                        if (onIabPurchaseFinishedListener2 == null) return;
                        try {
                            onIabPurchaseFinishedListener.onIabPurchaseFinished((IabResult) object, null);
                        } catch (Exception exception) {
                            IabHelper.this.logError("Uncaught exception in listener's onIabPurchaseFinished: " + exception);
                        }
                    }
                }
            }));
        }
    }

    void logDebug(String string2) {
        Log.Helper.LOGD(this, string2);
    }

    void logError(String string2) {
        Log.Helper.LOGE(this, string2);
    }

    void logWarn(String string2) {
        Log.Helper.LOGW(this, string2);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    public Inventory queryInventory(boolean bl2, boolean bl3, List<String> list) throws IabException {
        int n2 = 0;
        Inventory inventory;
        try {
            this.checkSetupDone("queryInventory");
            inventory = new Inventory();
            if (bl2 && (n2 = this.queryPurchases(inventory)) != 0) {
                throw new IabException(n2, "Error refreshing inventory (querying owned items).");
            }
        }
        catch (RemoteException remoteException) {
            throw new IabException(-1001, "Remote exception while refreshing inventory.", remoteException);
        }
        catch (JSONException jSONException) {
            throw new IabException(-1002, "Error parsing JSON response while refreshing inventory.", jSONException);
        }
        catch (IllegalStateException illegalStateException) {
            throw new IabException(-1008, "IabHelper in a bad state (billing service not connected, application context is null, etc.", illegalStateException);
        }
        if (!bl3) return inventory;
        {
            try {
                n2 = this.querySkuDetails(inventory, list);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (n2 == 0) return inventory;
            throw new IabException(n2, "Error refreshing inventory (querying prices of items).");
        }
    }

    public void queryInventoryAsync(QueryInventoryFinishedListener queryInventoryFinishedListener) {
        this.queryInventoryAsync(true, true, null, queryInventoryFinishedListener);
    }

    public void queryInventoryAsync(boolean bl2, boolean bl3, QueryInventoryFinishedListener queryInventoryFinishedListener) {
        this.queryInventoryAsync(bl2, bl3, null, queryInventoryFinishedListener);
    }

    public void queryInventoryAsync(final boolean bl2, final boolean bl3, final List<String> list, QueryInventoryFinishedListener queryInventoryFinishedListener) {
        Looper looper;
        Looper looper2 = looper = Looper.myLooper();
        if (looper == null) {
            looper2 = Looper.getMainLooper();
        }
        final Handler handler = new Handler(looper2);
        final QueryInventoryFinishedListener queryInventoryFinishedListener1 = queryInventoryFinishedListener;
        this.startOrQueueRunnable(new AsyncOperation("queryInventory", true, new Runnable(){
            final Handler handler2 = handler;
            final QueryInventoryFinishedListener listener = queryInventoryFinishedListener1;

            @Override
            public void run() {
                IabResult iabResult = new IabResult(0, "Inventory refresh successful."); // var2_1
                Inventory inventory = null;
                try {
                    inventory = IabHelper.this.queryInventory(bl2, bl3, list);
                    IabHelper.this.flagEndAsync();
                }
                catch (IabException var2_2) {
                    iabResult = var2_2.getResult();
                }
                IabResult finalIabResult = iabResult;
                Inventory finalInventory = inventory;
                this.handler2.post(() -> listener.onQueryInventoryFinished(finalIabResult, finalInventory));
            }
        }));
    }

    int queryPurchases(Inventory inventory) throws JSONException, RemoteException, IllegalStateException {
        Observer.onCallingMethod();
        return 0;
    }

    int querySkuDetails(Inventory inventory, List<String> object) throws RemoteException, JSONException, IllegalStateException {
        Observer.onCallingMethod();
        return 0;
    }

    public void setApplicationPublicKey(String string2) {
        this.mSignatureBase64 = string2;
    }

    public void startSetup(final OnIabSetupFinishedListener onIabSetupFinishedListener, final OnIabBroadcastListener onIabBroadcastListener) {
        if (this.mSetupDone) {
            throw new IllegalStateException("IAB helper is already set up.");
        }
        this.logDebug("Starting in-app billing setup.");
        this.mServiceConn = new ServiceConnection(){
            public void onServiceConnected(ComponentName var1_1, IBinder var2_3) {
               Observer.onCallingMethod();
            }

            public void onServiceDisconnected(ComponentName componentName) {
                Observer.onCallingMethod();
            }
        };
        this.logDebug("...Starting in-app billing setup.");
        this.logDebug("Binding service...");
        if (onIabBroadcastListener == null) {
            this.logError("Unable to get ResolveInfo for InAppBillingService intent. Cannot bind to InAppBillinbService");
            this.mServiceConn = null;
            return;
        }
        //this.logDebug("PackageName = " + ((ResolveInfo)onIabBroadcastListener).serviceInfo.packageName);
        //this.logDebug("ClassName = " + ((ResolveInfo)onIabBroadcastListener).serviceInfo.name);
        /*if (this.mContext.bindService((Intent)onIabSetupFinishedListener, this.mServiceConn, Context.BIND_AUTO_CREATE)) {
            this.logDebug("Success - Bind to InAppBillingService");
            return;
        }*/
        this.logError("Failed to Bind to InAppBillingService");
        this.mServiceConn = null;
    }

    private class AsyncOperation {
        private String m_name;
        private Runnable m_operation;
        private boolean m_runInNewThread;

        public AsyncOperation(String string2, boolean bl2, Runnable runnable) {
            this.m_name = string2;
            this.m_runInNewThread = bl2;
            this.m_operation = runnable;
            IabHelper.this.checkSetupDone(this.m_name);
        }

        public String getName() {
            return this.m_name;
        }

        public void run() {
            if (this.m_runInNewThread) {
                new Thread(this.m_operation).start();
                return;
            }
            this.m_operation.run();
        }
    }

    public static class IabPurchaseUpdateReceiver
    extends BroadcastReceiver {
        private final OnIabBroadcastListener mListener;

        public IabPurchaseUpdateReceiver(OnIabBroadcastListener onIabBroadcastListener) {
            this.mListener = onIabBroadcastListener;
        }

        public void onReceive(Context context, Intent intent) {
            if (this.mListener == null) return;
            this.mListener.receivedBroadcast();
        }
    }

    public static interface OnConsumeFinishedListener {
        public void onConsumeFinished(Purchase var1, IabResult var2);
    }

    public static interface OnConsumeMultiFinishedListener {
        public void onConsumeMultiFinished(List<Purchase> var1, List<IabResult> var2);
    }

    public static interface OnIabBroadcastListener {
        public void receivedBroadcast();
    }

    public static interface OnIabPurchaseFinishedListener {
        public void onIabPurchaseFinished(IabResult var1, Purchase var2);
    }

    public static interface OnIabSetupFinishedListener {
        public void onIabSetupFinished(IabResult var1);
    }

    public static interface QueryInventoryFinishedListener {
        public void onQueryInventoryFinished(IabResult var1, Inventory var2);
    }
}

