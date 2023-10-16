package com.ea.easp.mtx.market.android;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.android.vending.billing.IMarketBillingService;
import com.ea.easp.Debug;
import com.ea.easp.mtx.market.BillingService;
import com.ea.easp.mtx.market.Consts;
import com.ea.easp.mtx.market.ResponseHandler;
import com.ea.easp.mtx.market.Security;
import com.ea.easp.mtx.market.SecurityJNI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class AndroidBillingService extends Service implements ServiceConnection, BillingService {
    private static final String TAG = "AndroidBillingService";
    private static LinkedList<BillingRequest> mPendingRequests = new LinkedList<>();
    private static SecurityJNI mSecurityJNI;
    private static HashMap<Long, BillingRequest> mSentRequests = new HashMap<>();
    private static IMarketBillingService mService;

    /* access modifiers changed from: package-private */
    public abstract class BillingRequest {
        protected boolean mAddRequestToPendingQueue;
        protected long mRequestId;
        private final int mStartId;

        public BillingRequest(int i) {
            this.mStartId = i;
        }

        public boolean getNeedAddRequestToPendingQueue() {
            return this.mAddRequestToPendingQueue;
        }

        public int getStartId() {
            return this.mStartId;
        }

        /* access modifiers changed from: protected */
        public abstract boolean handleRunErrorWhenConnected(Exception exc);

        /* access modifiers changed from: protected */
        public void logResponseCode(String str, Bundle bundle) {
            Debug.Log.i(AndroidBillingService.TAG, str + " received " + Consts.ResponseCode.valueOf(bundle.getInt("RESPONSE_CODE")).toString());
        }

        /* access modifiers changed from: protected */
        public Bundle makeRequestBundle(String str) {
            Bundle bundle = new Bundle();
            bundle.putString(Consts.BILLING_REQUEST_METHOD, str);
            bundle.putInt(Consts.BILLING_REQUEST_API_VERSION, 1);
            bundle.putString(Consts.BILLING_REQUEST_PACKAGE_NAME, AndroidBillingService.this.getPackageName());
            return bundle;
        }

        /* access modifiers changed from: protected */
        public void onRemoteException(RemoteException remoteException) {
            Debug.Log.e(AndroidBillingService.TAG, "remote billing service crashed.", remoteException);
            IMarketBillingService unused = AndroidBillingService.mService = null;
        }

        /* access modifiers changed from: protected */
        public void responseCodeReceived(Consts.ResponseCode responseCode) {
        }

        /* access modifiers changed from: protected */
        public abstract long run() throws Exception;

        public boolean runIfConnected() {
            Debug.Log.d(AndroidBillingService.TAG, "runIfConnected(): " + getClass().getSimpleName());
            if (AndroidBillingService.mService != null) {
                try {
                    this.mRequestId = run();
                    Debug.Log.d(AndroidBillingService.TAG, "request id: " + this.mRequestId);
                    if (this.mRequestId >= 0) {
                        AndroidBillingService.mSentRequests.put(Long.valueOf(this.mRequestId), this);
                    }
                    return true;
                } catch (RemoteException e) {
                    this.mAddRequestToPendingQueue = handleRunErrorWhenConnected(e);
                    onRemoteException(e);
                } catch (Exception e2) {
                    this.mAddRequestToPendingQueue = handleRunErrorWhenConnected(e2);
                    Debug.Log.i(AndroidBillingService.TAG, "unbind service");
                    AndroidBillingService.this.unbind();
                    Debug.Log.i(AndroidBillingService.TAG, "set service pointer to null to be able rebind.");
                    IMarketBillingService unused = AndroidBillingService.mService = null;
                }
            }
            return false;
        }

        public boolean runRequest() {
            this.mAddRequestToPendingQueue = true;
            if (runIfConnected()) {
                return true;
            }
            if (!this.mAddRequestToPendingQueue || !AndroidBillingService.this.bindToMarketBillingService()) {
                return false;
            }
            AndroidBillingService.mPendingRequests.add(this);
            return true;
        }
    }

    class CheckBillingSupported extends BillingRequest {
        Runnable mErrorHandler;

        public CheckBillingSupported(Runnable runnable) {
            super(-1);
            this.mErrorHandler = runnable;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public boolean handleRunErrorWhenConnected(Exception exc) {
            Debug.Log.e(AndroidBillingService.TAG, "CheckBillingSupported failed.", exc);
            if (this.mErrorHandler == null) {
                return false;
            }
            this.mErrorHandler.run();
            return false;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public long run() throws RemoteException {
            int i = AndroidBillingService.mService.sendBillingRequest(makeRequestBundle("CHECK_BILLING_SUPPORTED")).getInt("RESPONSE_CODE", -1);
            Debug.Log.i(AndroidBillingService.TAG, "CheckBillingSupported response code: " + Consts.ResponseCode.valueOf(i));
            ResponseHandler.checkBillingSupportedResponse(i == Consts.ResponseCode.RESULT_OK.ordinal());
            return Consts.BILLING_RESPONSE_INVALID_REQUEST_ID;
        }
    }

    /* access modifiers changed from: package-private */
    public class ConfirmNotifications extends BillingRequest {
        final String[] mNotifyIds;

        public ConfirmNotifications(int i, String[] strArr) {
            super(i);
            this.mNotifyIds = strArr;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public boolean handleRunErrorWhenConnected(Exception exc) {
            Debug.Log.e(AndroidBillingService.TAG, "ConfirmNotifications failed.", exc);
            return true;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public long run() throws RemoteException {
            Bundle makeRequestBundle = makeRequestBundle("CONFIRM_NOTIFICATIONS");
            makeRequestBundle.putStringArray(Consts.BILLING_REQUEST_NOTIFY_IDS, this.mNotifyIds);
            Bundle sendBillingRequest = AndroidBillingService.mService.sendBillingRequest(makeRequestBundle);
            logResponseCode("confirmNotifications", sendBillingRequest);
            return sendBillingRequest.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
        }
    }

    /* access modifiers changed from: package-private */
    public class GetPurchaseInformation extends BillingRequest {
        long mNonce;
        final String[] mNotifyIds;

        public GetPurchaseInformation(long j, int i, String[] strArr) {
            super(i);
            this.mNotifyIds = strArr;
            this.mNonce = j;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public boolean handleRunErrorWhenConnected(Exception exc) {
            Debug.Log.e(AndroidBillingService.TAG, "GetPurchaseInformation failed.", exc);
            return true;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public void onRemoteException(RemoteException remoteException) {
            super.onRemoteException(remoteException);
            Security.removeNonce(this.mNonce);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public long run() throws RemoteException {
            Security.addNonce(this.mNonce);
            Bundle makeRequestBundle = makeRequestBundle("GET_PURCHASE_INFORMATION");
            makeRequestBundle.putLong(Consts.BILLING_REQUEST_NONCE, this.mNonce);
            makeRequestBundle.putStringArray(Consts.BILLING_REQUEST_NOTIFY_IDS, this.mNotifyIds);
            Bundle sendBillingRequest = AndroidBillingService.mService.sendBillingRequest(makeRequestBundle);
            logResponseCode("getPurchaseInformation", sendBillingRequest);
            return sendBillingRequest.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
        }
    }

    /* access modifiers changed from: private */
    public class NonceRequestDataGetPurchaseInformation {
        public String mNotifyId;
        public int mStartId;

        public NonceRequestDataGetPurchaseInformation(int i, String str) {
            this.mStartId = i;
            this.mNotifyId = str;
        }
    }

    class RequestPurchase extends BillingRequest implements BillingService.IRequestPurchase {
        public final String mDeveloperPayload;
        Runnable mErrorHandler;
        public final String mProductId;

        public RequestPurchase(AndroidBillingService androidBillingService, String str, Runnable runnable) {
            this(str, null, runnable);
        }

        public RequestPurchase(String str, String str2, Runnable runnable) {
            super(-1);
            this.mProductId = str;
            this.mDeveloperPayload = str2;
            this.mErrorHandler = runnable;
        }

        public String getDeveloperPayload() {
            return this.mDeveloperPayload;
        }

        @Override // com.ea.easp.mtx.market.BillingService.IRequestPurchase
        public String getProductId() {
            return this.mProductId;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public boolean handleRunErrorWhenConnected(Exception exc) {
            Debug.Log.e(AndroidBillingService.TAG, "RequestPurchase failed.", exc);
            if (this.mErrorHandler == null) {
                return false;
            }
            this.mErrorHandler.run();
            return false;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public void responseCodeReceived(Consts.ResponseCode responseCode) {
            ResponseHandler.responseCodeReceived(this, responseCode);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public long run() throws Exception {
            Bundle makeRequestBundle = makeRequestBundle("REQUEST_PURCHASE");
            makeRequestBundle.putString(Consts.BILLING_REQUEST_ITEM_ID, this.mProductId);
            if (this.mDeveloperPayload != null) {
                makeRequestBundle.putString(Consts.BILLING_REQUEST_DEVELOPER_PAYLOAD, this.mDeveloperPayload);
            }
            Bundle sendBillingRequest = AndroidBillingService.mService.sendBillingRequest(makeRequestBundle);
            PendingIntent pendingIntent = (PendingIntent) sendBillingRequest.getParcelable(Consts.BILLING_RESPONSE_PURCHASE_INTENT);
            if (pendingIntent == null) {
                Debug.Log.e(AndroidBillingService.TAG, "Error with requestPurchase");
                throw new Exception("sendBillingRequest(): PURCHASE INTENT is null.");
            }
            ResponseHandler.buyPageIntentResponse(pendingIntent, new Intent());
            long j = sendBillingRequest.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
            if (j >= 0) {
                return j;
            }
            throw new Exception("sendBillingRequest(): invalid request ID");
        }
    }

    class RestoreTransactions extends BillingRequest implements BillingService.IRestoreTransactions {
        Runnable mErrorHandler;
        long mNonce;

        public RestoreTransactions(long j, Runnable runnable) {
            super(-1);
            this.mNonce = j;
            this.mErrorHandler = runnable;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public boolean handleRunErrorWhenConnected(Exception exc) {
            Debug.Log.e(AndroidBillingService.TAG, "RestoreTransactions failed.", exc);
            if (this.mErrorHandler == null) {
                return false;
            }
            this.mErrorHandler.run();
            return false;
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public void onRemoteException(RemoteException remoteException) {
            super.onRemoteException(remoteException);
            Security.removeNonce(this.mNonce);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public void responseCodeReceived(Consts.ResponseCode responseCode) {
            ResponseHandler.responseCodeReceived(this, responseCode);
        }

        /* access modifiers changed from: protected */
        @Override // com.ea.easp.mtx.market.android.AndroidBillingService.BillingRequest
        public long run() throws Exception {
            Security.addNonce(this.mNonce);
            Bundle makeRequestBundle = makeRequestBundle("RESTORE_TRANSACTIONS");
            makeRequestBundle.putLong(Consts.BILLING_REQUEST_NONCE, this.mNonce);
            Bundle sendBillingRequest = AndroidBillingService.mService.sendBillingRequest(makeRequestBundle);
            logResponseCode("restoreTransactions", sendBillingRequest);
            long j = sendBillingRequest.getLong(Consts.BILLING_RESPONSE_REQUEST_ID, Consts.BILLING_RESPONSE_INVALID_REQUEST_ID);
            if (j >= 0) {
                return j;
            }
            throw new Exception("sendBillingRequest(): invalid request ID");
        }
    }

    public AndroidBillingService() {
    }

    public AndroidBillingService(SecurityJNI securityJNI) {
        mSecurityJNI = securityJNI;
    }

    private boolean bindToMarketBillingService() {
        try {
            Debug.Log.i(TAG, "binding to Market billing service");
            if (bindService(new Intent(Consts.MARKET_BILLING_SERVICE_ACTION), this, Context.BIND_AUTO_CREATE)) {
                return true;
            }
            Debug.Log.e(TAG, "Could not bind to service.");
            return false;
        } catch (SecurityException e) {
            Debug.Log.e(TAG, "Security exception: " + e);
        }
        return false;
    }

    private void checkResponseCode(long j, Consts.ResponseCode responseCode) {
        BillingRequest billingRequest = mSentRequests.get(Long.valueOf(j));
        if (billingRequest != null) {
            Debug.Log.d(TAG, billingRequest.getClass().getSimpleName() + ": " + responseCode);
            billingRequest.responseCodeReceived(responseCode);
        }
        mSentRequests.remove(Long.valueOf(j));
    }

    private boolean confirmNotifications(int i, String[] strArr) {
        return new ConfirmNotifications(i, strArr).runRequest();
    }

    private void createPurchasesFromJsonAndSendThemToHandler(boolean z, String str, String str2, int i) {
        Debug.Log.d(TAG, "createPurchasesFromJsonAndSendThemToHandler()");
        ArrayList<Security.VerifiedPurchase> createVerifiedPurchasesList = Security.createVerifiedPurchasesList(str, z);
        if (createVerifiedPurchasesList == null) {
            Debug.Log.d(TAG, "...createPurchasesFromJsonAndSendThemToHandler(): purchases is null");
            return;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<Security.VerifiedPurchase> it = createVerifiedPurchasesList.iterator();
        while (it.hasNext()) {
            Security.VerifiedPurchase next = it.next();
            if (next.notificationId != null) {
                arrayList.add(next.notificationId);
            }
        }
        ResponseHandler.purchaseResponse(createVerifiedPurchasesList, z, str, str2);
        if (!arrayList.isEmpty()) {
            Debug.Log.d(TAG, "sending confirmation of receiving of " + arrayList.size() + " notifications()");
            confirmNotifications(i, (String[]) arrayList.toArray(new String[arrayList.size()]));
        }
        Debug.Log.d(TAG, "...createPurchasesFromJsonAndSendThemToHandler()");
    }

    private void getNonce(Object obj) {
        if (mSecurityJNI != null) {
            mSecurityJNI.getNonce(obj);
        } else {
            Debug.Log.e(TAG, "getNonce(): fail to send 'get nonce' request.");
        }
    }

    private boolean getPurchaseInformation(long j, int i, String[] strArr) {
        return new GetPurchaseInformation(j, i, strArr).runRequest();
    }

    private void purchaseStateChanged(int i, String str, String str2) {
        Debug.Log.d(TAG, "purchaseStateChanged()");
        if (str == null) {
            Debug.Log.e(TAG, "purchaseStateChanged(): signedData is null");
        } else if (!TextUtils.isEmpty(str2)) {
            verifyMarketResponse(str, str2, i);
        } else {
            Debug.Log.d(TAG, "signature is empty: \"" + str2 + "\"");
            createPurchasesFromJsonAndSendThemToHandler(false, str, str2, i);
        }
        Debug.Log.d(TAG, "...purchaseStateChanged()");
    }

    private void runPendingRequests() {
        int i = -1;
        while (true) {
            BillingRequest peek = mPendingRequests.peek();
            if (peek != null) {
                if (peek.runIfConnected()) {
                    mPendingRequests.remove();
                    if (i < peek.getStartId()) {
                        i = peek.getStartId();
                    }
                } else {
                    bindToMarketBillingService();
                    return;
                }
            } else if (i >= 0) {
                Debug.Log.i(TAG, "stopping service, startId: " + i);
                stopSelf(i);
                return;
            } else {
                return;
            }
        }
    }

    private void verifyMarketResponse(String str, String str2, int i) {
        if (mSecurityJNI != null) {
            mSecurityJNI.verify(str, str2, i, Consts.STORE_ANDROID);
        } else {
            Debug.Log.e(TAG, "verifyMarketResponse(): fail to send transaction verification request.");
        }
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void OnNonceSucceed(long j, Object obj) {
        if (obj instanceof NonceRequestDataGetPurchaseInformation) {
            NonceRequestDataGetPurchaseInformation nonceRequestDataGetPurchaseInformation = (NonceRequestDataGetPurchaseInformation) obj;
            getPurchaseInformation(j, nonceRequestDataGetPurchaseInformation.mStartId, new String[]{nonceRequestDataGetPurchaseInformation.mNotifyId});
            return;
        }
        Debug.Log.e(TAG, "OnNonceSucceed(): unknown type of requestData");
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void OnVerifyMarketResponse(boolean z, String str, String str2, int i) {
        createPurchasesFromJsonAndSendThemToHandler(z, str, str2, i);
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void checkBillingSupported(Runnable runnable) {
        new CheckBillingSupported(runnable).runRequest();
    }

    public void handleCommand(Intent intent, int i) {
        if (intent == null) {
            Debug.Log.d(TAG, "handleCommand() null intent");
            return;
        }
        String action = intent.getAction();
        Debug.Log.i(TAG, "handleCommand() action: " + action);
        if (Consts.ACTION_CONFIRM_NOTIFICATION.equals(action)) {
            String[] stringArrayExtra = intent.getStringArrayExtra(Consts.NOTIFICATION_ID);
            Debug.Log.w(TAG, "Confirm notifications called from BillingService.handleCommand()");
            confirmNotifications(i, stringArrayExtra);
        } else if (Consts.ACTION_GET_PURCHASE_INFORMATION.equals(action)) {
            getNonce(new NonceRequestDataGetPurchaseInformation(i, intent.getStringExtra(Consts.NOTIFICATION_ID)));
        } else if (Consts.ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
            purchaseStateChanged(i, intent.getStringExtra(Consts.INAPP_SIGNED_DATA), intent.getStringExtra(Consts.INAPP_SIGNATURE));
        } else if (Consts.ACTION_RESPONSE_CODE.equals(action)) {
            checkResponseCode(intent.getLongExtra(Consts.INAPP_REQUEST_ID, -1), Consts.ResponseCode.valueOf(intent.getIntExtra(Consts.INAPP_RESPONSE_CODE, Consts.ResponseCode.RESULT_ERROR.ordinal())));
        }
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Debug.Log.d(TAG, "Billing service connected");
        mService = IMarketBillingService.Stub.asInterface(iBinder);
        runPendingRequests();
    }

    public void onServiceDisconnected(ComponentName componentName) {
        Debug.Log.w(TAG, "Billing service disconnected");
        mService = null;
    }

    public void onStart(Intent intent, int i) {
        handleCommand(intent, i);
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void requestPurchase(String str, String str2, String str3, Runnable runnable) {
        new RequestPurchase(str, str3, runnable).runRequest();
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void restoreTransactions(long j, Runnable runnable) {
        new RestoreTransactions(j, runnable).runRequest();
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void setContext(Context context) {
        attachBaseContext(context);
    }

    @Override // com.ea.easp.mtx.market.BillingService
    public void shutdown() {
        Debug.Log.d(TAG, "shutdown()");
        unbind();
        stopSelf();
    }

    public void unbind() {
        try {
            unbindService(this);
        } catch (IllegalArgumentException e) {
            Debug.Log.d(TAG, "Billing service: unbind() failed");
        }
    }
}
