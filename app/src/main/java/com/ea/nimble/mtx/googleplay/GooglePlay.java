package com.ea.nimble.mtx.googleplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import com.ea.nimble.ApplicationEnvironment;
import com.ea.nimble.ApplicationLifecycle;
import com.ea.nimble.Base;
import com.ea.nimble.Component;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.IApplicationEnvironment;
import com.ea.nimble.IApplicationLifecycle;
import com.ea.nimble.IHttpRequest;
import com.ea.nimble.ISynergyEnvironment;
import com.ea.nimble.ISynergyIdManager;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Network;
import com.ea.nimble.NimbleConfiguration;
import com.ea.nimble.Persistence;
import com.ea.nimble.PersistenceService;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.SynergyIdManager;
import com.ea.nimble.SynergyNetwork;
import com.ea.nimble.SynergyRequest;
import com.ea.nimble.SynergyServerError;
import com.ea.nimble.Timer;
import com.ea.nimble.Utility;
import com.ea.nimble.mtx.INimbleMTX;
import com.ea.nimble.mtx.NimbleCatalogItem;
import com.ea.nimble.mtx.NimbleMTXError;
import com.ea.nimble.mtx.NimbleMTXTransaction;
import com.ea.nimble.mtx.catalog.synergy.SynergyCatalog;
import com.ea.nimble.mtx.catalog.synergy.SynergyCatalogItem;
import com.ea.nimble.mtx.googleplay.util.IabHelper;
import com.ea.nimble.mtx.googleplay.util.IabResult;
import com.ea.nimble.mtx.googleplay.util.Inventory;
import com.ea.nimble.mtx.googleplay.util.Purchase;
import com.ea.nimble.mtx.googleplay.util.SkuDetails;
import com.ea.nimble.tracking.ITracking;
import com.ea.nimble.tracking.Tracking;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class GooglePlay extends Component implements IApplicationLifecycle.ActivityEventCallbacks, LogSource, INimbleMTX, IabHelper.OnIabPurchaseFinishedListener {
    private static final double CACHE_EXPIRE_TIME = 3600.0d;
    private static final String CACHE_TIMESTAMP_KEY = "cacheTimestamp";
    public static final String COMPONENT_ID = "com.ea.nimble.mtx.googleplay";
    private static final double DEFERRED_CALLBACK_DELAY = 0.1d;
    private static final double MAX_REQUEST_RETRY_DELAY = 300.0d;
    private static final String PERSISTENCE_CATALOG_ITEMS = "catalogItems";
    private static final String PERSISTENCE_PENDING_TRANSACTIONS = "pendingTransactions";
    private static final String PERSISTENCE_PURCHASED_TRANSACTIONS = "purchasedTransactions";
    private static final String PERSISTENCE_RECOVERED_TRANSACTIONS = "recoveredTransactions";
    private static final String PERSISTENCE_UNRECORDED_TRANSACTIONS = "unrecordedTransactions";
    private static final String SYNERGY_API_VERIFY_AND_RECORD_GOOGLEPLAY_PURCHASE = "/drm/api/android/verifyAndRecordPurchase";
    IabHelper mGooglePlayIabHelper;
    String m_appPublicKey;
    private Long m_cacheTimestamp;
    private boolean m_restoreInProgress;
    SynergyCatalog m_synergyCatalog;
    private boolean m_verificationEnabled;
    public static int GOOGLEPLAY_ACTIVITY_RESULT_REQUEST_CODE = 987654;
    public static String GOOGLEPLAY_PLATFORM_PARAMETER_APPLICATION_PUBLIC_KEY = "GOOGLEPLAY_APPLICATION_PUBLIC_KEY";
    public static String GOOGLEPLAY_ADDITIONALINFO_KEY_ORDERID = "orderId";
    public static String GOOGLEPLAY_ADDITIONALINFO_KEY_PURCHASETIME = "purchaseTime";
    public static String GOOGLEPLAY_ADDITIONALINFO_KEY_PURCHASESTATE = "purchaseState";
    public static String GOOGLEPLAY_ADDITIONALINFO_KEY_TOKEN = "token";
    public static String GOOGLEPLAY_ADDITIONALINFO_KEY_PURCHASEDATA = "purchaseData";
    public static String GOOGLEPLAY_ADDITIONALINFO_KEY_RECEIPT = "receipt";
    private ItemRestorer m_itemRestorer = new ItemRestorer();
    private TransactionRecorder m_transactionRecorder = new TransactionRecorder();
    HashMap<String, GooglePlayTransaction> mPendingTransactions = new HashMap<>();
    HashMap<String, GooglePlayTransaction> mPurchasedTransactions = new HashMap<>();
    HashMap<String, GooglePlayTransaction> mRecoveredTransactions = new HashMap<>();
    HashMap<String, GooglePlayCatalogItem> mCatalogItems = new HashMap<>();
    ArrayList<GooglePlayTransaction> mUnrecordedTransactions = new ArrayList<>();

    /* loaded from: stdlib.jar:com/ea/nimble/mtx/googleplay/GooglePlay$GetNonceCallback.class */
    public interface GetNonceCallback {
        void onGetNonceComplete(GooglePlayTransaction googlePlayTransaction, String str, Error error);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: stdlib.jar:com/ea/nimble/mtx/googleplay/GooglePlay$ItemRestorer.class */
    public class ItemRestorer extends BroadcastReceiver implements Runnable {
        private double m_requestRetryDelay;
        private Timer m_timer;

        private ItemRestorer() {
            this.m_timer = new Timer(this);
            this.m_requestRetryDelay = 1.0d;
        }

        public void cancel() {
            GooglePlay.this.m_restoreInProgress = false;
            this.m_timer.cancel();
            this.m_requestRetryDelay = 1.0d;
            Utility.unregisterReceiver(this);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getExtras().getString(Global.NOTIFICATION_DICTIONARY_KEY_RESULT).equals(Global.NOTIFICATION_DICTIONARY_RESULT_FAIL)) {
                Log.Helper.LOGD(GooglePlay.this, "Catalog refresh failed with restore pending. Retrying in " + this.m_requestRetryDelay + " seconds.");
                if (!this.m_timer.isRunning()) {
                    this.m_timer.schedule(this.m_requestRetryDelay, false);
                    return;
                }
                return;
            }
            this.m_timer.cancel();
            Utility.unregisterReceiver(this);
            GooglePlay.this.m_restoreInProgress = false;
            GooglePlay.this.restorePurchasedTransactionsImpl(false);
        }

        public void restoreItems() {
            if (!GooglePlay.this.m_restoreInProgress) {
                if (GooglePlay.this.mCatalogItems == null || GooglePlay.this.mCatalogItems.isEmpty()) {
                    GooglePlay.this.m_restoreInProgress = true;
                    Log.Helper.LOGD(GooglePlay.this, "Restore pending but catalog is unavailable. Initiating refresh now.");
                    Utility.registerReceiver(INimbleMTX.NIMBLE_NOTIFICATION_MTX_REFRESH_CATALOG_FINISHED, this);
                    GooglePlay.this.refreshAvailableCatalogItems();
                    return;
                }
                GooglePlay.this.restorePurchasedTransactionsImpl(false);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.m_requestRetryDelay = Math.min(this.m_requestRetryDelay * 2.0d, 300.0d);
            GooglePlay.this.refreshAvailableCatalogItems();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: stdlib.jar:com/ea/nimble/mtx/googleplay/GooglePlay$PurchaseTransactionVerifier.class */
    public class PurchaseTransactionVerifier {
        private PurchaseTransactionVerifier() {
        }

        public void verifyTransaction(GooglePlayTransaction googlePlayTransaction) {
            GooglePlay.this.updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_NONCE);
            GooglePlay.this.networkCallGetNonceFromSynergy(googlePlayTransaction, new GetNonceCallback() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.PurchaseTransactionVerifier.1
                @Override // com.ea.nimble.mtx.googleplay.GooglePlay.GetNonceCallback
                public void onGetNonceComplete(final GooglePlayTransaction googlePlayTransaction2, String str, Error error) {
                    if (googlePlayTransaction2.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_NONCE) {
                        if (GooglePlay.this.updateTransactionRecordWithNonce(googlePlayTransaction2, str, error)) {
                            GooglePlay.this.networkCallRecordPurchase(googlePlayTransaction2, new VerifyCallback() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.PurchaseTransactionVerifier.1.1
                                @Override // com.ea.nimble.mtx.googleplay.GooglePlay.VerifyCallback
                                public void onVerificationComplete(Exception exc) {
                                    if (googlePlayTransaction2.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_SYNERGY_VERIFICATION) {
                                        if (exc != null) {
                                            Log.Helper.LOGE(this, "Error making recordPurchase call to Synergy: " + exc);
                                            googlePlayTransaction2.mError = new NimbleMTXError(NimbleMTXError.Code.VERIFICATION_ERROR, "Synergy verification error", exc);
                                            googlePlayTransaction2.mFailedState = googlePlayTransaction2.mGooglePlayTransactionState;
                                            GooglePlay.this.updateGooglePlayTransactionRecordState(googlePlayTransaction2, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
                                        } else {
                                            GooglePlay.this.updateGooglePlayTransactionRecordState(googlePlayTransaction2, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT);
                                        }
                                        if (googlePlayTransaction2 != null && googlePlayTransaction2.mPurchaseCallback != null) {
                                            googlePlayTransaction2.mPurchaseCallback.purchaseComplete(googlePlayTransaction2);
                                        }
                                    }
                                }
                            });
                        } else if (googlePlayTransaction2 != null && googlePlayTransaction2.mPurchaseCallback != null) {
                            googlePlayTransaction2.mPurchaseCallback.purchaseComplete(googlePlayTransaction2);
                        }
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: stdlib.jar:com/ea/nimble/mtx/googleplay/GooglePlay$RestoreTransactionVerifier.class */
    public class RestoreTransactionVerifier {
        private AtomicInteger m_transactionsVerifying;
        private ArrayList<GooglePlayTransaction> m_verifiedTransactions;

        private RestoreTransactionVerifier() {
        }

        public void verifyTransactions(List<GooglePlayTransaction> list) {
            this.m_transactionsVerifying = new AtomicInteger(0);
            this.m_verifiedTransactions = new ArrayList<>();
            if (list.size() == 0) {
                GooglePlay.this.onRestoreComplete(this.m_verifiedTransactions);
                return;
            }
            for (GooglePlayTransaction googlePlayTransaction : list) {
                this.m_transactionsVerifying.incrementAndGet();
                GooglePlay.this.mPendingTransactions.put(googlePlayTransaction.getTransactionId(), googlePlayTransaction);
                GooglePlay.this.updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_NONCE);
                GooglePlay.this.networkCallGetNonceFromSynergy(googlePlayTransaction, new GetNonceCallback() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.RestoreTransactionVerifier.1
                    @Override // com.ea.nimble.mtx.googleplay.GooglePlay.GetNonceCallback
                    public void onGetNonceComplete(final GooglePlayTransaction googlePlayTransaction2, String str, Error error) {
                        if (GooglePlay.this.updateTransactionRecordWithNonce(googlePlayTransaction2, str, error)) {
                            GooglePlay.this.networkCallRecordPurchase(googlePlayTransaction2, new VerifyCallback() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.RestoreTransactionVerifier.1.1
                                @Override // com.ea.nimble.mtx.googleplay.GooglePlay.VerifyCallback
                                public void onVerificationComplete(Exception exc) {
                                    if (exc != null) {
                                        googlePlayTransaction2.mError = new NimbleMTXError(NimbleMTXError.Code.VERIFICATION_ERROR, "Synergy verification error", exc);
                                    }
                                    RestoreTransactionVerifier.this.m_verifiedTransactions.add(googlePlayTransaction2);
                                    if (RestoreTransactionVerifier.this.m_transactionsVerifying.decrementAndGet() == 0) {
                                        GooglePlay.this.onRestoreComplete(RestoreTransactionVerifier.this.m_verifiedTransactions);
                                    }
                                }
                            });
                        } else if (RestoreTransactionVerifier.this.m_transactionsVerifying.decrementAndGet() == 0) {
                            GooglePlay.this.onRestoreComplete(RestoreTransactionVerifier.this.m_verifiedTransactions);
                        }
                    }
                });
            }
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/mtx/googleplay/GooglePlay$TransactionRecorder.class */
    public class TransactionRecorder extends BroadcastReceiver implements GetNonceCallback, Runnable {
        private double m_requestRetryDelay;
        private Timer m_timer;

        private TransactionRecorder() {
            this.m_requestRetryDelay = 1.0d;
            this.m_timer = new Timer(this);
        }

        public void cancel() {
            this.m_timer.cancel();
            this.m_requestRetryDelay = 1.0d;
            Utility.unregisterReceiver(this);
        }

        @Override // com.ea.nimble.mtx.googleplay.GooglePlay.GetNonceCallback
        public void onGetNonceComplete(final GooglePlayTransaction googlePlayTransaction, String str, Error error) {
            if (!GooglePlay.this.isCancelledError(error)) {
                if (GooglePlay.this.updateTransactionRecordWithNonce(googlePlayTransaction, str, error)) {
                    GooglePlay.this.networkCallRecordPurchase(googlePlayTransaction, new VerifyCallback() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.TransactionRecorder.1
                        @Override // com.ea.nimble.mtx.googleplay.GooglePlay.VerifyCallback
                        public void onVerificationComplete(Exception exc) {
                            if (!GooglePlay.this.isCancelledError(exc)) {
                                if (googlePlayTransaction.mIsRecorded) {
                                    GooglePlay.this.mUnrecordedTransactions.remove(googlePlayTransaction);
                                    GooglePlay.this.saveUnrecordedTransactionsToPersistence();
                                    TransactionRecorder.this.m_requestRetryDelay = 1.0d;
                                } else if (!TransactionRecorder.this.m_timer.isRunning()) {
                                    TransactionRecorder.this.m_timer.schedule(TransactionRecorder.this.m_requestRetryDelay, false);
                                }
                            }
                        }
                    });
                } else if (!this.m_timer.isRunning()) {
                    this.m_timer.schedule(this.m_requestRetryDelay, false);
                }
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.Helper.LOGD(GooglePlay.this, "Received network notification");
            if (Network.getComponent().getStatus() == Network.Status.OK) {
                Log.Helper.LOGD(GooglePlay.this, "Network status is OK, unregistering receiver and attempting to record transactions");
                Utility.unregisterReceiver(this);
                recordTransactions();
                return;
            }
            Log.Helper.LOGD(GooglePlay.this, "Attempted to recordTransaction but network state was not OK. Aborting and eating my transaction.");
        }

        public void recordTransactions() {
            if (GooglePlay.this.mUnrecordedTransactions == null || GooglePlay.this.mUnrecordedTransactions.size() == 0) {
                Log.Helper.LOGD(GooglePlay.this, "No transactions to record");
                return;
            }
            Log.Helper.LOGD(GooglePlay.this, "Attempting to record transactions");
            if (ApplicationEnvironment.getCurrentActivity() == null) {
                Log.Helper.LOGD(GooglePlay.this, "Main application not running, ignoring record");
            } else if (Network.getComponent().getStatus() != Network.Status.OK) {
                Log.Helper.LOGD(GooglePlay.this, "Waiting for Network connectivity");
                Utility.registerReceiver(Global.NOTIFICATION_NETWORK_STATUS_CHANGE, this);
            } else {
                Iterator<GooglePlayTransaction> it = GooglePlay.this.mUnrecordedTransactions.iterator();
                while (it.hasNext()) {
                    GooglePlay.this.networkCallGetNonceFromSynergy(it.next(), this);
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            this.m_requestRetryDelay = Math.min(this.m_requestRetryDelay * 2.0d, 300.0d);
            recordTransactions();
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/mtx/googleplay/GooglePlay$VerifyCallback.class */
    public interface VerifyCallback {
        void onVerificationComplete(Exception exc);
    }

    private GooglePlay() {
    }

    public void broadcastLocalEvent(String str, String str2, String str3, Bundle bundle) {
        LocalBroadcastManager instance = LocalBroadcastManager.getInstance(ApplicationEnvironment.getComponent().getApplicationContext());
        Intent intent = new Intent();
        intent.setAction(str);
        Bundle bundle2 = bundle != null ? new Bundle(bundle) : new Bundle();
        if (str2 == null) {
            bundle2.putString(Global.NOTIFICATION_DICTIONARY_KEY_RESULT, Global.NOTIFICATION_DICTIONARY_RESULT_SUCCESS);
        } else {
            bundle2.putString("error", str2);
            bundle2.putString(Global.NOTIFICATION_DICTIONARY_KEY_RESULT, Global.NOTIFICATION_DICTIONARY_RESULT_FAIL);
        }
        if (str3 != null) {
            bundle2.putString(INimbleMTX.NOTIFICATION_DICTIONARY_KEY_TRANSACTIONID, str3);
        }
        intent.putExtras(bundle2);
        instance.sendBroadcast(intent);
    }

    public Map<String, Serializable> createAdditionalInfoBundleFromIabPurchase(Purchase purchase) {
        HashMap hashMap = new HashMap();
        if (purchase != null) {
            hashMap.put(GOOGLEPLAY_ADDITIONALINFO_KEY_ORDERID, purchase.getOrderId());
            hashMap.put(GOOGLEPLAY_ADDITIONALINFO_KEY_PURCHASETIME, Long.valueOf(purchase.getPurchaseTime()));
            hashMap.put(GOOGLEPLAY_ADDITIONALINFO_KEY_PURCHASESTATE, Integer.valueOf(purchase.getPurchaseState()));
            hashMap.put(GOOGLEPLAY_ADDITIONALINFO_KEY_TOKEN, purchase.getToken());
            hashMap.put(GOOGLEPLAY_ADDITIONALINFO_KEY_PURCHASEDATA, purchase.getOriginalJson());
            hashMap.put(GOOGLEPLAY_ADDITIONALINFO_KEY_RECEIPT, purchase.getSignature());
        }
        return hashMap;
    }

    public GooglePlayError createGooglePlayErrorFromIabResult(IabResult iabResult) {
        GooglePlayError.Code code;
        if (iabResult == null || iabResult.getResponse() == 0) {
            return null;
        }
        switch (iabResult.getResponse()) {
            case IabHelper.IABHELPER_UNKNOWN_ERROR /* -1009 */:
                code = GooglePlayError.Code.IABHELPER_UNKNOWN_ERROR;
                break;
            case IabHelper.IABHELPER_BAD_STATE_ERROR /* -1008 */:
                code = GooglePlayError.Code.IABHELPER_BAD_STATE_ERROR;
                break;
            case IabHelper.IABHELPER_MISSING_TOKEN /* -1007 */:
                code = GooglePlayError.Code.IABHELPER_MISSING_TOKEN;
                break;
            case IabHelper.IABHELPER_UNKNOWN_PURCHASE_RESPONSE /* -1006 */:
                code = GooglePlayError.Code.IABHELPER_UNKNOWN_PURCHASE_RESPONSE;
                break;
            case IabHelper.IABHELPER_USER_CANCELLED /* -1005 */:
                code = GooglePlayError.Code.IABHELPER_USER_CANCELLED;
                break;
            case IabHelper.IABHELPER_SEND_INTENT_FAILED /* -1004 */:
                code = GooglePlayError.Code.IABHELPER_SEND_INTENT_FAILED;
                break;
            case IabHelper.IABHELPER_VERIFICATION_FAILED /* -1003 */:
                code = GooglePlayError.Code.IABHELPER_VERIFICATION_FAILED;
                break;
            case IabHelper.IABHELPER_BAD_RESPONSE /* -1002 */:
                code = GooglePlayError.Code.IABHELPER_BAD_RESPONSE;
                break;
            case IabHelper.IABHELPER_REMOTE_EXCEPTION /* -1001 */:
                code = GooglePlayError.Code.IABHELPER_REMOTE_EXCEPTION;
                break;
            case IabHelper.IABHELPER_ERROR_BASE /* -1000 */:
                code = GooglePlayError.Code.IABHELPER_ERROR_BASE;
                break;
            case 1:
                code = GooglePlayError.Code.BILLING_RESPONSE_RESULT_USER_CANCELED;
                break;
            case 3:
                code = GooglePlayError.Code.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE;
                break;
            case 4:
                code = GooglePlayError.Code.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE;
                break;
            case 5:
                code = GooglePlayError.Code.BILLING_RESPONSE_RESULT_DEVELOPER_ERROR;
                break;
            case 6:
                code = GooglePlayError.Code.BILLING_RESPONSE_RESULT_ERROR;
                break;
            case 7:
                code = GooglePlayError.Code.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;
                break;
            case 8:
                code = GooglePlayError.Code.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED;
                break;
            default:
                code = GooglePlayError.Code.UNKNOWN;
                break;
        }
        return new GooglePlayError(code, iabResult.getMessage());
    }

    private void createIabHelper() {
        this.mGooglePlayIabHelper = new IabHelper(ApplicationEnvironment.getComponent().getApplicationContext(), getAppPublicKey());
        this.mGooglePlayIabHelper.enableDebugLogging(true);
        this.mGooglePlayIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.1
            @Override // com.ea.nimble.mtx.googleplay.util.IabHelper.OnIabSetupFinishedListener
            public void onIabSetupFinished(IabResult iabResult) {
                Log.Helper.LOGD(this, "InAppBilling helper setup finished.");
                if (!iabResult.isSuccess()) {
                    Log.Helper.LOGD(this, "Error setting up InAppBilling helper: " + iabResult);
                } else {
                    GooglePlay.this.m_itemRestorer.restoreItems();
                }
            }
        }, new IabHelper.OnIabBroadcastListener() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.2
            @Override // com.ea.nimble.mtx.googleplay.util.IabHelper.OnIabBroadcastListener
            public void receivedBroadcast() {
                Log.Helper.LOGD(this, "Received PURCHASES_UPDATED broadcast - resolving transactions");
                GooglePlay.this.restorePurchasedTransactions();
            }
        });
    }

    public NimbleMTXError createNimbleMTXErrorWithGooglePlayError(GooglePlayError googlePlayError, String str) {
        NimbleMTXError.Code code = NimbleMTXError.Code.PLATFORM_ERROR;
        NimbleMTXError.Code code2 = code;
        if (googlePlayError != null) {
            int code3 = googlePlayError.getCode();
            if (code3 == GooglePlayError.Code.BILLING_RESPONSE_RESULT_USER_CANCELED.intValue() || code3 == GooglePlayError.Code.IABHELPER_USER_CANCELLED.intValue()) {
                code2 = NimbleMTXError.Code.USER_CANCELED;
            } else if (code3 == GooglePlayError.Code.BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE.intValue()) {
                code2 = NimbleMTXError.Code.BILLING_NOT_AVAILABLE;
            } else if (code3 == GooglePlayError.Code.BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE.intValue()) {
                code2 = NimbleMTXError.Code.ITEM_UNAVAILABLE;
            } else if (code3 == GooglePlayError.Code.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED.intValue()) {
                code2 = NimbleMTXError.Code.ITEM_ALREADY_OWNED;
            } else {
                code2 = code;
                if (code3 == GooglePlayError.Code.BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED.intValue()) {
                    code2 = NimbleMTXError.Code.ITEM_NOT_OWNED;
                }
            }
        }
        return new NimbleMTXError(code2, str, googlePlayError);
    }

    private void deferredConsumeCallback(final INimbleMTX.ItemGrantedCallback itemGrantedCallback, final NimbleMTXTransaction nimbleMTXTransaction) {
        new Timer(new Runnable() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.11
            @Override // java.lang.Runnable
            public void run() {
                itemGrantedCallback.itemGrantedComplete(nimbleMTXTransaction);
            }
        }).schedule(DEFERRED_CALLBACK_DELAY, false);
    }

    private HashMap<String, GooglePlayTransaction> filterForResumablePurchaseTransactions(HashMap<String, GooglePlayTransaction> hashMap) {
        HashMap<String, GooglePlayTransaction> hashMap2 = new HashMap<>();
        if (hashMap != null) {
            for (GooglePlayTransaction googlePlayTransaction : hashMap.values()) {
                if (googlePlayTransaction.mTransactionType == NimbleMTXTransaction.TransactionType.PURCHASE && (googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_NONCE || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GOOGLEPLAY_ACTIVITY_RESPONSE || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_SYNERGY_VERIFICATION || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GOOGLEPLAY_CONSUMPTION || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.COMPLETE)) {
                    hashMap2.put(googlePlayTransaction.getTransactionId(), googlePlayTransaction);
                }
            }
        }
        return hashMap2;
    }

    private List<GooglePlayTransaction> findRecoveredTransactionsWithItemSku(String str) {
        ArrayList arrayList = new ArrayList();
        if (str != null) {
            for (GooglePlayTransaction googlePlayTransaction : this.mRecoveredTransactions.values()) {
                if (googlePlayTransaction.getItemSku().equals(str)) {
                    arrayList.add(googlePlayTransaction);
                }
            }
        }
        return arrayList;
    }

    private String generateDeveloperPayloadForTransaction(GooglePlayTransaction googlePlayTransaction) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddHHmmss", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date());
    }

    public String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private String getAppPublicKey() {
        return this.m_appPublicKey;
    }

    private GooglePlayCatalogItem getCatalogItemBySku(String str) {
        return this.mCatalogItems.get(str);
    }

    public static GooglePlay getComponent() {
        return (GooglePlay) Base.getComponent(COMPONENT_ID);
    }

    public void getGooglePlayPricingForPendingCatalogItems(List<GooglePlayCatalogItem> list) {
        if (list == null || list.isEmpty()) {
            broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_REFRESH_CATALOG_FINISHED, "Empty catalog item list for GooglePlay catalog query.", null, null);
            return;
        }

        ApplicationEnvironment.getCurrentActivity().runOnUiThread(
                new Lambdas(list, this.mGooglePlayIabHelper, this)
        );
    }

    class Lambdas implements Runnable, IabHelper.QueryInventoryFinishedListener{

        List<GooglePlayCatalogItem> mCatalogItemList;
        IabHelper mIabHelper;
        GooglePlay mParentNimbleGooglePlayObject;

        public Lambdas(List<GooglePlayCatalogItem> mCatalogItemList, IabHelper mIabHelper, GooglePlay mParentNimbleGooglePlayObject) {
            this.mCatalogItemList = mCatalogItemList;
            this.mIabHelper = mIabHelper;
            this.mParentNimbleGooglePlayObject = mParentNimbleGooglePlayObject;
        }

        @Override
        public void onQueryInventoryFinished(IabResult iabResult, Inventory inventory) {
            NimbleMTXError nimbleMTXError;
            Log.Helper.LOGD(this, "GooglePlayCatalogUpdateListener onQueryInventoryFinished");
            if (iabResult.isFailure()) {
                Log.Helper.LOGD(this, "Query inventory error: " + iabResult.getMessage());
                nimbleMTXError = GooglePlay.this.createNimbleMTXErrorWithGooglePlayError(GooglePlay.this.createGooglePlayErrorFromIabResult(iabResult), "GooglePlay catalog query error");
            } else {
                HashSet hashSet = new HashSet();
                for (GooglePlayCatalogItem googlePlayCatalogItem : this.mCatalogItemList) {
                    SkuDetails skuDetails = inventory.getSkuDetails(googlePlayCatalogItem.mSku);
                    if (skuDetails != null) {
                        googlePlayCatalogItem.mTitle = skuDetails.getTitle();
                        googlePlayCatalogItem.mDescription = skuDetails.getDescription();
                        googlePlayCatalogItem.mPriceWithCurrencyAndFormat = skuDetails.getPrice();
                        googlePlayCatalogItem.mPriceDecimal = Utility.validString(skuDetails.getPriceMicros()) ? Float.parseFloat(skuDetails.getPriceMicros()) / 1000000.0f : BitmapDescriptorFactory.HUE_RED;
                        googlePlayCatalogItem.mAdditionalInfo.put(SynergyCatalog.MTX_INFO_KEY_CURRENCY, skuDetails.getCurrencyCode());
                    } else {
                        Log.Helper.LOGE(this, "Could not get SKU details from GooglePlay for SKU: %s. Removing from results.", googlePlayCatalogItem.mSku);
                        hashSet.add(googlePlayCatalogItem);
                    }
                }
                nimbleMTXError = null;
                if (hashSet.size() > 0) {
                    this.mCatalogItemList.removeAll(hashSet);
                    nimbleMTXError = null;
                }
            }
            this.mParentNimbleGooglePlayObject.onGooglePlayCatalogItemsRefreshed(this.mCatalogItemList, true, nimbleMTXError);
        }

        void queryGooglePlay() {
            ArrayList arrayList = new ArrayList();
            for (GooglePlayCatalogItem googlePlayCatalogItem : this.mCatalogItemList) {
                arrayList.add(googlePlayCatalogItem.mSku);
            }
            Log.Helper.LOGD(this, "Making GooglePlay inventory query for skus: " + arrayList);
            this.mIabHelper.queryInventoryAsync(false, true, arrayList, this);
        }

        @Override
        public void run() {
            try {
                queryGooglePlay();
            } catch (IllegalStateException e) {
                onQueryInventoryFinished(new IabResult(3, "Billing is unavailable"), null);
            }
        }
    }

    private void googlePlayCallPurchaseItem(GooglePlayTransaction googlePlayTransaction) {
        googlePlayTransaction.mDeveloperPayload = generateDeveloperPayloadForTransaction(googlePlayTransaction);
        updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GOOGLEPLAY_ACTIVITY_RESPONSE);
        this.mGooglePlayIabHelper.launchPurchaseFlow(ApplicationEnvironment.getCurrentActivity(), googlePlayTransaction.getItemSku(), GOOGLEPLAY_ACTIVITY_RESULT_REQUEST_CODE, this, googlePlayTransaction.mDeveloperPayload);
    }

    private void googlePlayConsumeItem(GooglePlayTransaction googlePlayTransaction) {
        try {
            this.mGooglePlayIabHelper.consumeAsync(new Purchase(googlePlayTransaction), new IabHelper.OnConsumeFinishedListener() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.10
                @Override // com.ea.nimble.mtx.googleplay.util.IabHelper.OnConsumeFinishedListener
                public void onConsumeFinished(Purchase purchase, IabResult iabResult) {
                    if (iabResult.isFailure()) {
                        Log.Helper.LOGE(this, "GooglePlay consume item failed, item SKU: " + purchase.getSku());
                        GooglePlayTransaction googlePlayTransaction2 = GooglePlay.this.mPendingTransactions.get(purchase.getNimbleMTXTransactionId());
                        if (googlePlayTransaction2 != null) {
                            googlePlayTransaction2.mError = GooglePlay.this.createNimbleMTXErrorWithGooglePlayError(GooglePlay.this.createGooglePlayErrorFromIabResult(iabResult), "GooglePlay item consumption error.");
                            googlePlayTransaction2.mFailedState = googlePlayTransaction2.mGooglePlayTransactionState;
                            GooglePlay.this.updateGooglePlayTransactionRecordState(googlePlayTransaction2, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
                            if (googlePlayTransaction2.mItemGrantedCallback != null) {
                                googlePlayTransaction2.mItemGrantedCallback.itemGrantedComplete(googlePlayTransaction2);
                            } else {
                                Log.Helper.LOGE(this, "Transaction does not have a consume callback to notify game of the finalize error.");
                            }
                        }
                    } else {
                        Log.Helper.LOGD(this, "GooglePlay consume item success, item SKU: " + purchase.getSku());
                        GooglePlayTransaction googlePlayTransaction3 = GooglePlay.this.mPendingTransactions.get(purchase.getNimbleMTXTransactionId());
                        if (googlePlayTransaction3 != null) {
                            GooglePlay.this.updateGooglePlayTransactionRecordState(googlePlayTransaction3, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
                            if (googlePlayTransaction3.mItemGrantedCallback != null) {
                                googlePlayTransaction3.mItemGrantedCallback.itemGrantedComplete(googlePlayTransaction3);
                            } else {
                                Log.Helper.LOGE(this, "Transaction does not have a consume callback to notify game.");
                            }
                        } else {
                            Log.Helper.LOGE(this, "Unable to find consumed transaction to remove.");
                        }
                    }
                }
            });
        } catch (Exception e) {
            Log.Helper.LOGE(this, "Unable to construct a Purchase object from GooglePlayTransaction. Unable to consume item with Google Play!");
            if (googlePlayTransaction.mItemGrantedCallback != null) {
                googlePlayTransaction.mError = new NimbleMTXError(NimbleMTXError.Code.UNABLE_TO_CONSTRUCT_REQUEST, "Unable to construct Purchase object from GooglePlayTransaction.");
                googlePlayTransaction.mFailedState = googlePlayTransaction.mGooglePlayTransactionState;
                deferredConsumeCallback(googlePlayTransaction.mItemGrantedCallback, googlePlayTransaction);
                return;
            }
            Log.Helper.LOGE(this, "ItemGrantedCallback not set, no way to notify game.");
        }
    }

    private static void initialize() {
        Base.registerComponent(new GooglePlay(), COMPONENT_ID);
    }

    private boolean isCacheExpired() {
        return this.m_cacheTimestamp == null || ((double) (System.currentTimeMillis() - this.m_cacheTimestamp.longValue())) > 3600000.0d;
    }

    public boolean isCancelledError(Throwable th) {
        if (!(th instanceof Error)) {
            return false;
        }
        Error error = (Error) th;
        return (error.getDomain().equals(Error.ERROR_DOMAIN) && error.getCode() == Error.Code.NETWORK_OPERATION_CANCELLED.intValue()) || isCancelledError(error.getCause());
    }

    private boolean isTransactionPending() {
        return isTransactionPending(true);
    }

    private boolean isTransactionPending(boolean z) {
        if (this.mPendingTransactions.size() == 0) {
            return z && this.mRecoveredTransactions.size() != 0;
        }
        return true;
    }

    private void loadFromPersistence() {
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(COMPONENT_ID, Persistence.Storage.DOCUMENT);
        Serializable value = persistenceForNimbleComponent.getValue(PERSISTENCE_CATALOG_ITEMS);
        if (value != null && value.getClass() == HashMap.class) {
            this.mCatalogItems = new HashMap<>((HashMap) value);
            Log.Helper.LOGD(this, "Restored %d catalog items from persistence.", Integer.valueOf(this.mCatalogItems.size()));
        }
        Serializable value2 = persistenceForNimbleComponent.getValue(PERSISTENCE_PURCHASED_TRANSACTIONS);
        if (value2 == null || value2.getClass() != HashMap.class) {
            Log.Helper.LOGD(this, "No purchased transactions to restore.");
        } else {
            this.mPurchasedTransactions = new HashMap<>((HashMap) value2);
            Log.Helper.LOGD(this, "%d purchased transactions restored from persistence.", Integer.valueOf(this.mPurchasedTransactions.size()));
        }
        Serializable value3 = persistenceForNimbleComponent.getValue(PERSISTENCE_RECOVERED_TRANSACTIONS);
        if (value3 == null || value3.getClass() != HashMap.class) {
            Log.Helper.LOGD(this, "No recovered transactions to restore.");
        } else {
            this.mRecoveredTransactions = new HashMap<>((HashMap) value3);
            Log.Helper.LOGD(this, "%d recovered transactions restored from persistence.", Integer.valueOf(this.mRecoveredTransactions.size()));
        }
        Serializable value4 = persistenceForNimbleComponent.getValue(PERSISTENCE_PENDING_TRANSACTIONS);
        if (value4 == null || value4.getClass() != HashMap.class) {
            Log.Helper.LOGD(this, "No pending transactions to restore.");
        } else {
            HashMap<String, GooglePlayTransaction> hashMap = new HashMap<>((HashMap) value4);
            Log.Helper.LOGD(this, "%d pending transactions restored from persistence.", Integer.valueOf(hashMap.size()));
            if (this.mRecoveredTransactions == null) {
                this.mRecoveredTransactions = new HashMap<>();
            }
            this.mRecoveredTransactions.putAll(filterForResumablePurchaseTransactions(hashMap));
            for (GooglePlayTransaction googlePlayTransaction : this.mRecoveredTransactions.values()) {
                this.mPendingTransactions.remove(googlePlayTransaction.getTransactionId());
            }
        }
        Serializable value5 = persistenceForNimbleComponent.getValue(PERSISTENCE_UNRECORDED_TRANSACTIONS);
        if (value5 != null && (value5 instanceof ArrayList)) {
            this.mUnrecordedTransactions = (ArrayList) value5;
            this.m_transactionRecorder.recordTransactions();
        }
        this.m_cacheTimestamp = (Long) persistenceForNimbleComponent.getValue(CACHE_TIMESTAMP_KEY);
        if (this.mRecoveredTransactions != null && this.mRecoveredTransactions.size() > 0) {
            Log.Helper.LOGD(this, "Recovered transactions: " + this.mRecoveredTransactions);
            broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_TRANSACTIONS_RECOVERED, null, null, null);
        }
    }

    public void networkCallGetNonceFromSynergy(final GooglePlayTransaction googlePlayTransaction, final GetNonceCallback getNonceCallback) {
        try {
            this.m_synergyCatalog.getNonce(new SynergyCatalog.StringCallback() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.7
                @Override // com.ea.nimble.mtx.catalog.synergy.SynergyCatalog.StringCallback
                public void callback(String str, Exception exc) {
                    if (exc == null) {
                        getNonceCallback.onGetNonceComplete(googlePlayTransaction, str, null);
                        return;
                    }
                    getNonceCallback.onGetNonceComplete(googlePlayTransaction, null, new NimbleMTXError(NimbleMTXError.Code.GET_NONCE_ERROR, "Synergy getNonce request error.", exc));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            getNonceCallback.onGetNonceComplete(googlePlayTransaction, null, new NimbleMTXError(NimbleMTXError.Code.GET_NONCE_ERROR, "Error making Synergy getNonce request", e));
        }
    }

    public void networkCallRecordPurchase(final GooglePlayTransaction googlePlayTransaction, final VerifyCallback verifyCallback) {
        updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_SYNERGY_VERIFICATION);

        SynergyNetwork
                .getComponent()
                .sendRequest(
                        new SynergyRequest(SYNERGY_API_VERIFY_AND_RECORD_GOOGLEPLAY_PURCHASE, IHttpRequest.Method.POST, (synergyRequest)->{
                            String str;
                            String str2;
                            Map<String, String> pidMap;
                            String gameSpecifiedPlayerId;
                            IApplicationEnvironment component = ApplicationEnvironment.getComponent();
                            ISynergyEnvironment component2 = SynergyEnvironment.getComponent();
                            ISynergyIdManager component3 = SynergyIdManager.getComponent();
                            HashMap<String, Object> hashMap = new HashMap();
                            boolean z = googlePlayTransaction.getCatalogItem() != null && googlePlayTransaction.getCatalogItem().isFree();
                            hashMap.put("transactionId", googlePlayTransaction.getAdditionalInfo().get(GooglePlay.GOOGLEPLAY_ADDITIONALINFO_KEY_ORDERID));
                            hashMap.put("price", (int) (googlePlayTransaction.getPriceDecimal() * 100.0f));
                            hashMap.put("currency", googlePlayTransaction.getAdditionalInfo().get(SynergyCatalog.MTX_INFO_KEY_CURRENCY));
                            hashMap.put("restore", googlePlayTransaction.getTransactionType() == NimbleMTXTransaction.TransactionType.RESTORE);
                            try {
                                hashMap.put("receipt", googlePlayTransaction.getAdditionalInfo().get(GooglePlay.GOOGLEPLAY_ADDITIONALINFO_KEY_PURCHASEDATA));
                            } catch (Exception e) {
                                Log.Helper.LOGE(this, "Exception creating JSON body for /recordPurchase");
                                hashMap.put("receipt", "");
                            }
                            hashMap.put("itemSellId", googlePlayTransaction.getItemSku().substring(GooglePlay.this.m_synergyCatalog.getItemSkuPrefix().length()));
                            hashMap.put("masterSellId", component2.getSellId());
                            hashMap.put("hwId", component2.getEAHardwareId());
                            hashMap.put("signature", googlePlayTransaction.getReceipt());
                            hashMap.put("nonce", googlePlayTransaction.getNonce());
                            hashMap.put("isFree", z+"");
                            String synergyId = component3.getSynergyId();
                            if (!(synergyId == null || synergyId == "")) {
                                hashMap.put("synergyUid", Utility.safeString(synergyId));
                            }
                            HashMap<String, String> hashMap2 = new HashMap();
                            String string = Settings.Secure.getString(component.getApplicationContext().getContentResolver(), "android_id");
                            if (!(component == null || (gameSpecifiedPlayerId = component.getGameSpecifiedPlayerId()) == null || gameSpecifiedPlayerId.length() <= 0)) {
                                hashMap.put("gamePlayerId", gameSpecifiedPlayerId);
                            }
                            String str3 = "";
                            try {
                                String googleAdvertisingId = ApplicationEnvironment.getComponent().getGoogleAdvertisingId();
                                str3 = googleAdvertisingId;
                                str = "true";
                                str3 = googleAdvertisingId;
                                if (!ApplicationEnvironment.getComponent().isLimitAdTrackingEnabled()) {
                                    str = "false";
                                    str3 = googleAdvertisingId;
                                }
                            } catch (Exception e2) {
                                Log.Helper.LOGW(this, "Exception when getting advertising ID for Android");
                                str = "true";
                            }
                            hashMap2.put("eaDeviceId", Utility.safeString(component2.getEADeviceId()));
                            hashMap2.put("androidId", Utility.safeString(string));
                            hashMap2.put("advertiserId", Utility.safeString(str3));
                            hashMap2.put("limitAdTracking", Utility.safeString(str));
                            hashMap2.put("macHash", Utility.safeString(Utility.SHA256HashString(component.getMACAddress())));
                            hashMap2.put("aut", Utility.safeString(""));
                            hashMap.put("didMap", Utility.convertObjectToJSONString(hashMap2));
                            HashMap<String, String> hashMap3 = new HashMap();
                            hashMap3.put("timestamp", Utility.safeString(Utility.getUTCDateStringFormat(googlePlayTransaction.getTimeStamp())));
                            hashMap3.put("bundleId", Utility.safeString(component.getApplicationBundleId()));
                            hashMap3.put("appName", Utility.safeString(component.getApplicationName()));
                            hashMap3.put("appVersion", Utility.safeString(component.getApplicationVersion()));
                            hashMap3.put("appLanguage", Utility.safeString(ApplicationEnvironment.getComponent().getShortApplicationLanguageCode()));
                            hashMap3.put("countryCode", Utility.safeString(Locale.getDefault().getCountry()));
                            try {
                                Context applicationContext = ApplicationEnvironment.getComponent().getApplicationContext();
                                ApplicationInfo applicationInfo = applicationContext.getPackageManager().getApplicationInfo(applicationContext.getPackageName(), 128);
                                str2 = null;
                                if (applicationInfo.metaData != null) {
                                    str2 = applicationInfo.metaData.getString("com.facebook.sdk.ApplicationId");
                                }
                            } catch (PackageManager.NameNotFoundException e3) {
                                str2 = null;
                            }
                            if (Utility.validString(str2)) {
                                hashMap3.put("fbAppId", str2);
                            }
                            try {
                                Cursor query = ApplicationEnvironment.getComponent().getApplicationContext().getContentResolver().query(Uri.parse("content://com.facebook.katana.provider.AttributionIdProvider"), null, null, null, null);
                                if (query != null) {
                                    query.moveToFirst();
                                    hashMap3.put("fbAttrId", query.getString(0));
                                }
                            } catch (IllegalStateException e4) {
                                e4.printStackTrace();
                            } catch (Exception e5) {
                                e5.printStackTrace();
                            }
                            hashMap.put("appInfo", Utility.convertObjectToJSONString(hashMap3));
                            HashMap hashMap4 = new HashMap();
                            hashMap4.put("systemName", "Android");
                            hashMap4.put("limitAdTracking", str);
                            PackageManager packageManager = component.getApplicationContext().getPackageManager();
                            TelephonyManager telephonyManager = (TelephonyManager) component.getApplicationContext().getSystemService("phone");
                            if (packageManager.checkPermission("android.permission.READ_PHONE_STATE", component.getApplicationContext().getPackageName()) == 0) {
                                hashMap4.put("imei", Utility.safeString(telephonyManager.getDeviceId()));
                            }
                            hashMap.put("deviceInfo", Utility.convertObjectToJSONString(hashMap4));
                            hashMap.put("schemaVer", "2");
                            hashMap.put("clientApiVersion", "2.0.0");
                            synergyRequest.jsonData = hashMap;
                            synergyRequest.baseUrl = component2.getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_SYNERGY_DRM);
                            synergyRequest.send();
                        }), (synergyNetworkConnectionHandle)->{
                            Exception error = synergyNetworkConnectionHandle.getResponse().getError();
                            if (error == null) {
                                Log.Helper.LOGD(this, "recordPurchase response: " + synergyNetworkConnectionHandle.getResponse().getJsonData().toString());
                                googlePlayTransaction.mIsRecorded = true;
                            } else if (error instanceof Error) {
                                Error error2 = (Error) error;
                                if (!(!error2.getDomain().equals(SynergyServerError.ERROR_DOMAIN) || error2.getCode() == SynergyServerError.Code.AMAZON_SERVER_CONNECTION_ERROR.intValue() || error2.getCode() == SynergyServerError.Code.APPLE_SERVER_CONNECTION_ERROR.intValue())) {
                                    Log.Helper.LOGD(GooglePlay.this, "Transaction " + googlePlayTransaction.mTransactionId + " failed to record with error: " + error2);
                                    googlePlayTransaction.mIsRecorded = true;
                                }
                            }
                            if (verifyCallback != null && ApplicationEnvironment.isMainApplicationRunning() && ApplicationEnvironment.getCurrentActivity() != null) {
                                verifyCallback.onVerificationComplete(error);
                            }
                        });
    }

   private class AnonClass1 implements SynergyRequest.SynergyRequestPreparingCallback{
       @Override
       public void prepareRequest(SynergyRequest var1) {

       }
   }

    public void onRestoreComplete(List<GooglePlayTransaction> list) {
        boolean z = false;
        HashMap<String, GooglePlayTransaction> hashMap = new HashMap<>();
        for (GooglePlayTransaction googlePlayTransaction : list) {
            if (googlePlayTransaction.mError != null) {
                Log.Helper.LOGE(this, "Error making recordPurchase call to Synergy: " + googlePlayTransaction.mError);
                googlePlayTransaction.mFailedState = googlePlayTransaction.mGooglePlayTransactionState;
            }
            updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
            boolean z2 = false;
            if (this.mCatalogItems != null) {
                z2 = false;
                if (this.mCatalogItems.size() > 0) {
                    GooglePlayCatalogItem googlePlayCatalogItem = this.mCatalogItems.get(googlePlayTransaction.getItemSku());
                    z2 = false;
                    if (googlePlayCatalogItem != null) {
                        z2 = false;
                        if (googlePlayCatalogItem.getItemType() == NimbleCatalogItem.ItemType.CONSUMABLE) {
                            Log.Helper.LOGD(this, "Consumable transaction restored, sku: " + googlePlayCatalogItem.getSku());
                            z2 = true;
                            for (GooglePlayTransaction googlePlayTransaction2 : findRecoveredTransactionsWithItemSku(googlePlayTransaction.getItemSku())) {
                                googlePlayTransaction2.mError = new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_SUPERSEDED, "Transaction has been superseded by restored transaction");
                                googlePlayTransaction2.mFailedState = googlePlayTransaction2.mGooglePlayTransactionState;
                                googlePlayTransaction2.mGooglePlayTransactionState = GooglePlayTransaction.GooglePlayTransactionState.COMPLETE;
                            }
                        }
                    }
                }
            }
            if (z2) {
                z = true;
                this.mRecoveredTransactions.put(googlePlayTransaction.getTransactionId(), googlePlayTransaction);
                googlePlayTransaction.mTransactionType = NimbleMTXTransaction.TransactionType.PURCHASE;
                updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT);
            } else {
                hashMap.put(googlePlayTransaction.getTransactionId(), googlePlayTransaction);
                z = z;
                if (!this.m_verificationEnabled) {
                    this.mUnrecordedTransactions.add(googlePlayTransaction);
                    this.m_transactionRecorder.recordTransactions();
                    z = z;
                }
            }
            this.mPendingTransactions.remove(googlePlayTransaction.getTransactionId());
        }
        savePendingTransactionsToPersistence();
        this.mPurchasedTransactions = hashMap;
        savePurchasedTransactionsToPersistence();
        saveUnrecordedTransactionsToPersistence();
        this.m_restoreInProgress = false;
        Log.Helper.LOGD(this, "All RESTORE transactions processed. Raising REFRESH_PURCHASED_ITEMS_FINISHED notification.");
        broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_RESTORE_PURCHASED_TRANSACTIONS_FINISHED, null, null, null);
        if (z) {
            broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_TRANSACTIONS_RECOVERED, null, null, null);
        }
    }

    public void restorePurchasedTransactionsImpl(boolean z) {
        if (this.m_restoreInProgress) {
            Log.Helper.LOGD(this, "restorePurchasedTransactions called while restore already in progress. Aborting.");
        } else if (isTransactionPending(z)) {
            Log.Helper.LOGD(this, "restorePurchasedTransactions called while transactions still pending.");
            Log.Helper.LOGD(this, "pendingTransactions: " + this.mPendingTransactions);
            Log.Helper.LOGD(this, "recoveredTransactions: " + this.mRecoveredTransactions);
            broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_RESTORE_PURCHASED_TRANSACTIONS_FINISHED, "Can't restore purchases while transaction is pending", null, null);
        } else {
            Log.Helper.LOGD(this, "restorePurchasedTransactions called.");
            this.m_restoreInProgress = true;
            try {
                this.mGooglePlayIabHelper.queryInventoryAsync(true, true, new IabHelper.QueryInventoryFinishedListener() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.4
                    @Override // com.ea.nimble.mtx.googleplay.util.IabHelper.QueryInventoryFinishedListener
                    public void onQueryInventoryFinished(IabResult iabResult, Inventory inventory) {
                        Log.Helper.LOGD(this, "restorePurchasedTransactions onQueryInventoryFinished");
                        GooglePlayError googlePlayError = null;
                        if (iabResult == null || iabResult.isFailure()) {
                            Log.Helper.LOGE(this, "Error with GooglePlay purchased item query. %s", iabResult.getMessage());
                            googlePlayError = GooglePlay.this.createGooglePlayErrorFromIabResult(iabResult);
                        }
                        ArrayList arrayList = new ArrayList();
                        if (!(inventory == null || inventory.getAllPurchases() == null)) {
                            for (Purchase purchase : inventory.getAllPurchases()) {
                                GooglePlayTransaction googlePlayTransaction = new GooglePlayTransaction();
                                googlePlayTransaction.mTransactionType = NimbleMTXTransaction.TransactionType.RESTORE;
                                googlePlayTransaction.mTransactionId = GooglePlay.this.generateTransactionId();
                                googlePlayTransaction.mItemSku = Utility.safeString(purchase.getSku());
                                googlePlayTransaction.mNonce = Utility.safeString(purchase.getDeveloperPayload());
                                googlePlayTransaction.mReceipt = Utility.safeString(purchase.getSignature());
                                googlePlayTransaction.mAdditionalInfo = GooglePlay.this.createAdditionalInfoBundleFromIabPurchase(purchase);
                                googlePlayTransaction.mGooglePlayTransactionState = GooglePlayTransaction.GooglePlayTransactionState.UNDEFINED;
                                SkuDetails skuDetails = inventory.getSkuDetails(purchase.getSku());
                                try {
                                    googlePlayTransaction.mPriceDecimal = Float.parseFloat(skuDetails.getPriceMicros()) / 1000000.0f;
                                } catch (NullPointerException e) {
                                    Log.Helper.LOGE(this, "Error: got passed a null pointer when trying to parse the catalog prices.");
                                    googlePlayTransaction.mPriceDecimal = BitmapDescriptorFactory.HUE_RED;
                                } catch (NumberFormatException e2) {
                                    Log.Helper.LOGE(this, "Error: got passed an invalid float string when trying to parse the catalog prices.");
                                    googlePlayTransaction.mPriceDecimal = BitmapDescriptorFactory.HUE_RED;
                                }
                                googlePlayTransaction.mAdditionalInfo.put(SynergyCatalog.MTX_INFO_KEY_CURRENCY, skuDetails.getCurrencyCode());
                                googlePlayTransaction.mTimeStamp = new Date();
                                arrayList.add(googlePlayTransaction);
                            }
                        }
                        if (arrayList.size() == 0) {
                            Log.Helper.LOGD(this, "No restored transactions to verify. Raising REFRESH_PURCHASED_ITEMS_FINISHED notification.");
                            GooglePlay.this.m_restoreInProgress = false;
                            GooglePlay.this.broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_RESTORE_PURCHASED_TRANSACTIONS_FINISHED, googlePlayError != null ? googlePlayError.getMessage() : null, null, null);
                        } else if (GooglePlay.this.m_verificationEnabled) {
                            new RestoreTransactionVerifier().verifyTransactions(arrayList);
                        } else {
                            GooglePlay.this.onRestoreComplete(arrayList);
                        }
                    }
                });
            } catch (IllegalStateException e) {
                this.m_restoreInProgress = false;
                Log.Helper.LOGD(this, "IAB Helper not setup. Check for Google Play account");
                broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_RESTORE_PURCHASED_TRANSACTIONS_FINISHED, "ERROR_IAB_NULL", null, null);
            }
        }
    }

    private void saveCatalogToPersistence() {
        HashMap hashMap = new HashMap(this.mCatalogItems);
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(COMPONENT_ID, Persistence.Storage.DOCUMENT);
        Log.Helper.LOGD(this, "Saving %d catalog items to persistence.", Integer.valueOf(hashMap.size()));
        persistenceForNimbleComponent.setValue(PERSISTENCE_CATALOG_ITEMS, hashMap);
        this.m_cacheTimestamp = Long.valueOf(System.currentTimeMillis());
        persistenceForNimbleComponent.setValue(CACHE_TIMESTAMP_KEY, this.m_cacheTimestamp);
        persistenceForNimbleComponent.synchronize();
    }

    private void savePendingTransactionsToPersistence() {
        HashMap hashMap = new HashMap(this.mPendingTransactions);
        HashMap hashMap2 = new HashMap(this.mRecoveredTransactions);
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(COMPONENT_ID, Persistence.Storage.DOCUMENT);
        Log.Helper.LOGD(this, "Saving %d pending and %d previously recovered transactions to persistence.", Integer.valueOf(hashMap.size()), Integer.valueOf(hashMap2.size()));
        persistenceForNimbleComponent.setValue(PERSISTENCE_PENDING_TRANSACTIONS, hashMap);
        persistenceForNimbleComponent.setValue(PERSISTENCE_RECOVERED_TRANSACTIONS, hashMap2);
        persistenceForNimbleComponent.synchronize();
    }

    private void savePurchasedTransactionsToPersistence() {
        HashMap hashMap = new HashMap(this.mPurchasedTransactions);
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(COMPONENT_ID, Persistence.Storage.DOCUMENT);
        Log.Helper.LOGD(this, "Saving %d purchased transactions to persistence.", Integer.valueOf(hashMap.size()));
        persistenceForNimbleComponent.setValue(PERSISTENCE_PURCHASED_TRANSACTIONS, hashMap);
        persistenceForNimbleComponent.synchronize();
    }

    public void saveUnrecordedTransactionsToPersistence() {
        ArrayList arrayList = new ArrayList(this.mUnrecordedTransactions);
        Persistence persistenceForNimbleComponent = PersistenceService.getPersistenceForNimbleComponent(COMPONENT_ID, Persistence.Storage.DOCUMENT);
        Log.Helper.LOGD(this, "Saving %d unrecorded transactions to persistence.", Integer.valueOf(arrayList.size()));
        persistenceForNimbleComponent.setValue(PERSISTENCE_UNRECORDED_TRANSACTIONS, arrayList);
        persistenceForNimbleComponent.synchronize();
    }

    public void updateGooglePlayTransactionRecordState(GooglePlayTransaction googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState googlePlayTransactionState) {
        googlePlayTransaction.mGooglePlayTransactionState = googlePlayTransactionState;
        googlePlayTransaction.mTimeStamp = new Date();
        savePendingTransactionsToPersistence();
    }

    public boolean updateTransactionRecordWithNonce(GooglePlayTransaction googlePlayTransaction, String str, Error error) {
        NimbleMTXError nimbleMTXError;
        if (error != null || str == null || str.length() == 0) {
            if (error != null) {
                Log.Helper.LOGE(this, "Error making getNonce call to Synergy: " + error);
                nimbleMTXError = new NimbleMTXError(NimbleMTXError.Code.VERIFICATION_ERROR, "Synergy getNonce error", error);
            } else {
                Log.Helper.LOGE(this, "No nonce in Synergy response for getNonce.");
                nimbleMTXError = new NimbleMTXError(NimbleMTXError.Code.VERIFICATION_ERROR, "No nonce in Synergy response");
            }
            if (googlePlayTransaction != null) {
                googlePlayTransaction.mError = nimbleMTXError;
                googlePlayTransaction.mFailedState = googlePlayTransaction.mGooglePlayTransactionState;
                updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
                return false;
            }
            Log.Helper.LOGE(this, "No transaction record in getNonce error handling. No callback to call.");
            return false;
        }
        googlePlayTransaction.mNonce = str;
        return true;
    }

    @Override // com.ea.nimble.Component
    public void cleanup() {
        Log.Helper.LOGD(this, "Component cleanup");
        if (this.mGooglePlayIabHelper != null) {
            this.mGooglePlayIabHelper.dispose();
            this.mGooglePlayIabHelper = null;
        }
        ApplicationLifecycle.getComponent().unregisterActivityEventCallbacks(this);
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public Error finalizeTransaction(String str, INimbleMTX.FinalizeTransactionCallback finalizeTransactionCallback) {
        Component component;
        boolean z = true;
        GooglePlayTransaction googlePlayTransaction = this.mPendingTransactions.get(str);
        if (googlePlayTransaction == null) {
            String str2 = "Could not find transaction by Id to perform finalize, id: " + str;
            Log.Helper.LOGE(this, str2);
            return new NimbleMTXError(NimbleMTXError.Code.UNRECOGNIZED_TRANSACTION_ID, str2);
        }
        if (googlePlayTransaction.mGooglePlayTransactionState != GooglePlayTransaction.GooglePlayTransactionState.COMPLETE) {
            Log.Helper.LOGW(this, "Finalize called on unfinished transaction, for sku, %s.", googlePlayTransaction.getItemSku());
        }
        googlePlayTransaction.mFinalizeCallback = finalizeTransactionCallback;
        if (googlePlayTransaction.getError() == null && (component = Base.getComponent(Tracking.COMPONENT_ID)) != null) {
            ITracking iTracking = (ITracking) component;
            HashMap hashMap = new HashMap();
            hashMap.put(Tracking.KEY_MTX_SELLID, googlePlayTransaction.getItemSku());
            hashMap.put(Tracking.KEY_MTX_PRICE, String.valueOf(googlePlayTransaction.getPriceDecimal()));
            if (googlePlayTransaction.getAdditionalInfo().get(SynergyCatalog.MTX_INFO_KEY_CURRENCY) != null) {
                hashMap.put(Tracking.KEY_MTX_CURRENCY, googlePlayTransaction.getAdditionalInfo().get(SynergyCatalog.MTX_INFO_KEY_CURRENCY).toString());
            } else {
                Log.Helper.LOGD(this, "Currency information not currently available; using local currency instead.");
                hashMap.put(Tracking.KEY_MTX_CURRENCY, Currency.getInstance(Locale.getDefault()).toString());
            }
            iTracking.logEvent(Tracking.EVENT_MTX_ITEM_PURCHASED, hashMap);
        }
        this.mPendingTransactions.remove(str);
        updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
        if (googlePlayTransaction.getError() == null) {
            this.mPurchasedTransactions.put(str, googlePlayTransaction);
            savePurchasedTransactionsToPersistence();
        }
        if (googlePlayTransaction.mIsRecorded || googlePlayTransaction.mError != null) {
            z = false;
        }
        if (z) {
            this.mUnrecordedTransactions.add(googlePlayTransaction);
            saveUnrecordedTransactionsToPersistence();
        }
        if (googlePlayTransaction.mFinalizeCallback != null) {
            googlePlayTransaction.mFinalizeCallback.finalizeComplete(googlePlayTransaction);
        }
        if (!z) {
            return null;
        }
        this.m_transactionRecorder.recordTransactions();
        return null;
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public List<NimbleCatalogItem> getAvailableCatalogItems() {
        if (this.mCatalogItems == null || isCacheExpired()) {
            return null;
        }
        ArrayList arrayList = new ArrayList(this.mCatalogItems.values());
        Collections.sort(arrayList, new Comparator<NimbleCatalogItem>() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.1SkuComparator
            public int compare(NimbleCatalogItem nimbleCatalogItem, NimbleCatalogItem nimbleCatalogItem2) {
                return nimbleCatalogItem.getSku().compareTo(nimbleCatalogItem2.getSku());
            }
        });
        return arrayList;
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return COMPONENT_ID;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "MTX Google";
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public List<NimbleMTXTransaction> getPendingTransactions() {
        if (this.mPendingTransactions == null) {
            return null;
        }
        return new ArrayList(this.mPendingTransactions.values());
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public List<NimbleMTXTransaction> getPurchasedTransactions() {
        return new ArrayList(this.mPurchasedTransactions.values());
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public List<NimbleMTXTransaction> getRecoveredTransactions() {
        if (this.mRecoveredTransactions == null) {
            return null;
        }
        return new ArrayList(this.mRecoveredTransactions.values());
    }

    public NimbleMTXTransaction getTransaction(String str) {
        if (this.mPendingTransactions == null) {
            return null;
        }
        return this.mPendingTransactions.get(str);
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public Error itemGranted(String str, NimbleCatalogItem.ItemType itemType, INimbleMTX.ItemGrantedCallback itemGrantedCallback) {
        GooglePlayTransaction googlePlayTransaction = this.mPendingTransactions.get(str);
        if (googlePlayTransaction == null) {
            Log.Helper.LOGE(this, "Could not find transaction by Id to perform item grant, id: " + str);
            return new NimbleMTXError(NimbleMTXError.Code.UNRECOGNIZED_TRANSACTION_ID, "Could not find transaction to perform item grant.");
        }
        if (googlePlayTransaction.mGooglePlayTransactionState != GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT) {
            Log.Helper.LOGW(this, "Transaction in unexpected state for item grant. Transaction state: %s", googlePlayTransaction.mGooglePlayTransactionState);
        }
        if (itemGrantedCallback == null) {
            Log.Helper.LOGE(this, "itemGranted called with empty callback parameter.");
            return new Error(Error.Code.MISSING_CALLBACK, "Missing callback in itemGranted call.");
        }
        googlePlayTransaction.mItemGrantedCallback = itemGrantedCallback;
        if (!(googlePlayTransaction.mCatalogItem == null || googlePlayTransaction.mCatalogItem.getItemType() != NimbleCatalogItem.ItemType.CONSUMABLE || itemType == NimbleCatalogItem.ItemType.CONSUMABLE)) {
            Log.Helper.LOGW(this, "Game called item grant for SKU, %s, and indicated NOT consumable, though cached catalog data indicates the item is a consumable.", googlePlayTransaction.getItemSku());
        }
        if (googlePlayTransaction.getError() != null) {
            Log.Helper.LOGW(this, "Transaction for item SKU, %s, granted by game, despite an error. Clearing error from transaction.", googlePlayTransaction.getItemSku());
            googlePlayTransaction.mError = null;
        }
        if (itemType == NimbleCatalogItem.ItemType.CONSUMABLE) {
            updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GOOGLEPLAY_CONSUMPTION);
            googlePlayConsumeItem(googlePlayTransaction);
            return null;
        }
        updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
        deferredConsumeCallback(googlePlayTransaction.mItemGrantedCallback, googlePlayTransaction);
        return null;
    }

    public void networkCallGetAvailableItems() {
        try {
            this.m_synergyCatalog.getItemCatalog(new SynergyCatalog.ItemCallback() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.5
                @Override // com.ea.nimble.mtx.catalog.synergy.SynergyCatalog.ItemCallback
                public void callback(List<SynergyCatalogItem> list, Exception exc) {
                    if (exc == null) {
                        LinkedList linkedList = new LinkedList();
                        for (SynergyCatalogItem synergyCatalogItem : list) {
                            GooglePlayCatalogItem googlePlayCatalogItem = new GooglePlayCatalogItem();
                            googlePlayCatalogItem.mItemType = synergyCatalogItem.getItemType();
                            googlePlayCatalogItem.mAdditionalInfo = synergyCatalogItem.getAdditionalInfo();
                            googlePlayCatalogItem.mDescription = synergyCatalogItem.getDescription();
                            googlePlayCatalogItem.mSku = synergyCatalogItem.getSku();
                            googlePlayCatalogItem.mTitle = synergyCatalogItem.getTitle();
                            googlePlayCatalogItem.mUrl = synergyCatalogItem.getMetaDataUrl();
                            googlePlayCatalogItem.mIsFree = synergyCatalogItem.isFree();
                            linkedList.add(googlePlayCatalogItem);
                        }
                        if (Base.getConfiguration() != NimbleConfiguration.LIVE) {
                            GooglePlayCatalogItem googlePlayCatalogItem2 = new GooglePlayCatalogItem();
                            googlePlayCatalogItem2.mSku = "android.test.purchased";
                            googlePlayCatalogItem2.mTitle = "GP Purchased";
                            googlePlayCatalogItem2.mDescription = "GP Purchased Desc";
                            googlePlayCatalogItem2.mUrl = "";
                            googlePlayCatalogItem2.mItemType = NimbleCatalogItem.ItemType.NONCONSUMABLE;
                            linkedList.add(googlePlayCatalogItem2);
                            GooglePlayCatalogItem googlePlayCatalogItem3 = new GooglePlayCatalogItem();
                            googlePlayCatalogItem3.mSku = "android.test.canceled";
                            googlePlayCatalogItem3.mTitle = "GP Canceled";
                            googlePlayCatalogItem3.mDescription = "GP Canceled Desc";
                            googlePlayCatalogItem3.mUrl = "";
                            googlePlayCatalogItem3.mItemType = NimbleCatalogItem.ItemType.NONCONSUMABLE;
                            linkedList.add(googlePlayCatalogItem3);
                            GooglePlayCatalogItem googlePlayCatalogItem4 = new GooglePlayCatalogItem();
                            googlePlayCatalogItem4.mSku = "android.test.refunded";
                            googlePlayCatalogItem4.mTitle = "GP Refunded";
                            googlePlayCatalogItem4.mDescription = "GP Refunded Desc";
                            googlePlayCatalogItem4.mUrl = "";
                            googlePlayCatalogItem4.mItemType = NimbleCatalogItem.ItemType.NONCONSUMABLE;
                            linkedList.add(googlePlayCatalogItem4);
                            GooglePlayCatalogItem googlePlayCatalogItem5 = new GooglePlayCatalogItem();
                            googlePlayCatalogItem5.mSku = "android.test.item_unavailable";
                            googlePlayCatalogItem5.mTitle = "GP Unavailable";
                            googlePlayCatalogItem5.mDescription = "GP Unavailable Desc";
                            googlePlayCatalogItem5.mUrl = "";
                            googlePlayCatalogItem5.mItemType = NimbleCatalogItem.ItemType.NONCONSUMABLE;
                            linkedList.add(googlePlayCatalogItem5);
                        }
                        GooglePlay.this.getGooglePlayPricingForPendingCatalogItems(linkedList);
                        return;
                    }
                    Log.Helper.LOGE(this, "GetAvailableItems error: " + exc.toString());
                    GooglePlay.this.broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_REFRESH_CATALOG_FINISHED, "Error making Synergy getAvailableItems call.", null, null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public void onActivityResult(Activity activity, int i, int i2, Intent intent) {
        Log.Helper.LOGD(this, "onActivityResult");
        if (this.mGooglePlayIabHelper == null || !this.mGooglePlayIabHelper.isServiceAvailable()) {
            Log.Helper.LOGE(this, "GooglePlayIabHelper is not created or service is not currently available!");
        } else {
            this.mGooglePlayIabHelper.handleActivityResult(i, i2, intent);
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public boolean onBackPressed() {
        return true;
    }

    public void onGooglePlayCatalogItemsRefreshed(List<GooglePlayCatalogItem> list, boolean z, Exception exc) {
        if (exc == null) {
            Log.Helper.LOGD(this, "GooglePlayCatalog Updated.");
            if (z) {
                this.mCatalogItems.clear();
            }
            for (GooglePlayCatalogItem googlePlayCatalogItem : list) {
                this.mCatalogItems.put(googlePlayCatalogItem.getSku(), googlePlayCatalogItem);
            }
            saveCatalogToPersistence();
        } else {
            Log.Helper.LOGE(this, "Error updating GooglePlay Catalog: " + exc);
        }
        broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_REFRESH_CATALOG_FINISHED, exc != null ? exc.toString() : null, null, null);
    }

    @Override // com.ea.nimble.mtx.googleplay.util.IabHelper.OnIabPurchaseFinishedListener
    public void onIabPurchaseFinished(IabResult iabResult, Purchase purchase) {
        GooglePlayTransaction googlePlayTransaction;
        Log.Helper.LOGD(this, "IAB Purchase finished: " + iabResult + ", purchase: " + purchase);
        GooglePlayTransaction googlePlayTransaction2 = null;
        GooglePlayTransaction googlePlayTransaction3 = null;
        if (purchase == null) {
            Iterator<GooglePlayTransaction> it = this.mPendingTransactions.values().iterator();
            while (true) {
                googlePlayTransaction = googlePlayTransaction2;
                if (!it.hasNext()) {
                    break;
                }
                GooglePlayTransaction next = it.next();
                if (next.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GOOGLEPLAY_ACTIVITY_RESPONSE) {
                    if (googlePlayTransaction2 == null) {
                        googlePlayTransaction2 = next;
                    } else {
                        Log.Helper.LOGE(this, "More than one transaction record in WAITING_FOR_GOOGLE_PLAY_RESPONSE state found! Using: " + googlePlayTransaction2.getItemSku() + ". Additional transaction for: " + next.getItemSku());
                    }
                }
            }
        } else {
            String developerPayload = purchase.getDeveloperPayload();
            String orderId = purchase.getOrderId();
            Iterator<GooglePlayTransaction> it2 = this.mPendingTransactions.values().iterator();
            while (true) {
                googlePlayTransaction = googlePlayTransaction3;
                if (!it2.hasNext()) {
                    break;
                }
                GooglePlayTransaction next2 = it2.next();
                if (!Utility.validString(next2.mDeveloperPayload) || !next2.mDeveloperPayload.equals(developerPayload)) {
                    if (!Utility.validString(orderId) && !Utility.validString(developerPayload) && next2.mItemSku.equals(purchase.getSku())) {
                        Log.Helper.LOGD(this, "Found a matching SKU for pending transaction without orderId or developer payload, this must be a code redemption!");
                        if (googlePlayTransaction3 == null) {
                            googlePlayTransaction3 = next2;
                        } else {
                            Log.Helper.LOGE(this, "Multiple transactions that look like code redemptions found!  Using the first one. TransactionA: " + googlePlayTransaction3.toString() + " TransactionB: " + next2.toString());
                        }
                    }
                } else if (googlePlayTransaction3 == null) {
                    googlePlayTransaction3 = next2;
                } else {
                    Log.Helper.LOGE(this, "Multiple transactions with the same developerPayload found!  Using the first one. TransactionA: " + googlePlayTransaction3.toString() + " TransactionB: " + next2.toString());
                }
            }
        }
        if (googlePlayTransaction == null) {
            Log.Helper.LOGE(this, "Transaction record could not be found for purchase");
            GooglePlayTransaction googlePlayTransaction4 = new GooglePlayTransaction();
            googlePlayTransaction4.mTransactionId = generateTransactionId();
            googlePlayTransaction4.mTransactionType = NimbleMTXTransaction.TransactionType.PURCHASE;
            if (purchase == null || purchase.getSku() == null) {
                Log.Helper.LOGE(this, "No pre-existing transaction record, Google Play activity result has no SKU. No way to recover.");
                new NimbleMTXError(NimbleMTXError.Code.INTERNAL_STATE, "Unrecoverable purchase notification from GooglePlay. No SKU.");
                return;
            }
            Log.Helper.LOGD(this, "No transaction record for this purchase, creating one now. ItemSku(" + purchase.getSku() + ")");
            googlePlayTransaction4.mDeveloperPayload = purchase.getDeveloperPayload();
            googlePlayTransaction4.mItemSku = purchase.getSku();
            googlePlayTransaction4.mAdditionalInfo = createAdditionalInfoBundleFromIabPurchase(purchase);
            this.mPendingTransactions.put(googlePlayTransaction4.mTransactionId, googlePlayTransaction4);
        } else if (iabResult == null || iabResult.isFailure()) {
            NimbleMTXError nimbleMTXError = null;
            if (iabResult != null) {
                nimbleMTXError = null;
                if (0 == 0) {
                    Log.Helper.LOGD(this, "Error purchasing: " + iabResult);
                    nimbleMTXError = createNimbleMTXErrorWithGooglePlayError(createGooglePlayErrorFromIabResult(iabResult), "GooglePlay purchase error");
                }
            }
            googlePlayTransaction.mError = nimbleMTXError;
            googlePlayTransaction.mFailedState = googlePlayTransaction.mGooglePlayTransactionState;
            Log.Helper.LOGD(this, "MTX_GOOGLE: Purchase error. Purchase object is: " + purchase);
            googlePlayTransaction.mAdditionalInfo = createAdditionalInfoBundleFromIabPurchase(purchase);
            updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.COMPLETE);
            if (googlePlayTransaction.mPurchaseCallback != null) {
                try {
                    googlePlayTransaction.mPurchaseCallback.purchaseComplete(googlePlayTransaction);
                } catch (Exception e) {
                    Log.Helper.LOGE(this, "MTX_GOOGLE: Unhandled exception in mPurchaseCallback: " + e);
                    e.printStackTrace();
                }
            }
        } else {
            Log.Helper.LOGD(this, "GooglePlay Purchase successful.");
            googlePlayTransaction.mReceipt = purchase.getSignature();
            if (googlePlayTransaction.mReceipt == null || googlePlayTransaction.mReceipt.length() == 0) {
                Log.Helper.LOGW(this, "Purchase has an empty signature string. Setting to \"xxxxx\" for test.");
                googlePlayTransaction.mReceipt = "xxxxxxxxxxxxxxxxxxxxxxxxx";
            }
            googlePlayTransaction.mAdditionalInfo = createAdditionalInfoBundleFromIabPurchase(purchase);
            GooglePlayCatalogItem catalogItemBySku = getCatalogItemBySku(purchase.getSku());
            if (catalogItemBySku != null) {
                googlePlayTransaction.mPriceDecimal = catalogItemBySku.getPriceDecimal();
                Object obj = catalogItemBySku.getAdditionalInfo().get(SynergyCatalog.MTX_INFO_KEY_CURRENCY);
                if (obj != null) {
                    googlePlayTransaction.mAdditionalInfo.put(SynergyCatalog.MTX_INFO_KEY_CURRENCY, obj.toString());
                } else {
                    Log.Helper.LOGD(this, "Currency information not currently available; using local currency instead.");
                    googlePlayTransaction.mAdditionalInfo.put(SynergyCatalog.MTX_INFO_KEY_CURRENCY, Currency.getInstance(Locale.getDefault()).toString());
                }
            } else {
                Log.Helper.LOGW(this, "Purchased item not found in catalog, could not get price or currency of item.");
            }
            if (this.m_verificationEnabled) {
                updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_SYNERGY_VERIFICATION);
                if (googlePlayTransaction.mPurchaseCallback != null) {
                    googlePlayTransaction.mPurchaseCallback.unverifiedReceiptReceived(googlePlayTransaction);
                } else {
                    Log.Helper.LOGE(this, "Transaction missing callback,  cannot notify game of unverified receipt");
                }
                new PurchaseTransactionVerifier().verifyTransaction(googlePlayTransaction);
                return;
            }
            updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT);
            if (googlePlayTransaction.mPurchaseCallback != null) {
                googlePlayTransaction.mPurchaseCallback.purchaseComplete(googlePlayTransaction);
            } else {
                Log.Helper.LOGE(this, "Transaction missing callback,  cannot notify game of completed purchase");
            }
        }
    }

    @Override // com.ea.nimble.IApplicationLifecycle.ActivityEventCallbacks
    public void onWindowFocusChanged(boolean z) {
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public Error purchaseItem(String str, INimbleMTX.PurchaseTransactionCallback purchaseTransactionCallback) {
        if (this.mGooglePlayIabHelper == null || !this.mGooglePlayIabHelper.isServiceAvailable()) {
            return new NimbleMTXError(NimbleMTXError.Code.BILLING_NOT_AVAILABLE, "IabHelper is not set up");
        }
        if (purchaseTransactionCallback == null) {
            return new Error(Error.Code.MISSING_CALLBACK, "Missing purchase callback");
        }
        if (isTransactionPending()) {
            Log.Helper.LOGD(this, "purchaseItem called while transactions still pending.");
            Log.Helper.LOGD(this, "pendingTransactions: " + this.mPendingTransactions);
            Log.Helper.LOGD(this, "recoveredTransactions: " + this.mRecoveredTransactions);
            return new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_PENDING, "Another transaction is still outstanding.");
        } else if (this.m_restoreInProgress) {
            Log.Helper.LOGD(this, "purchaseItem called while restore is in progress.");
            return new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_PENDING, "Can't purchase item while restore is in progress.");
        } else {
            String generateTransactionId = generateTransactionId();
            GooglePlayTransaction googlePlayTransaction = new GooglePlayTransaction();
            googlePlayTransaction.mItemSku = str;
            googlePlayTransaction.mTransactionId = generateTransactionId;
            googlePlayTransaction.mTransactionType = NimbleMTXTransaction.TransactionType.PURCHASE;
            googlePlayTransaction.mPurchaseCallback = purchaseTransactionCallback;
            GooglePlayCatalogItem googlePlayCatalogItem = this.mCatalogItems.get(str);
            if (googlePlayCatalogItem != null) {
                googlePlayTransaction.mCatalogItem = new GooglePlayCatalogItem(googlePlayCatalogItem);
            }
            this.mPendingTransactions.put(generateTransactionId, googlePlayTransaction);
            updateGooglePlayTransactionRecordState(googlePlayTransaction, GooglePlayTransaction.GooglePlayTransactionState.USER_INITIATED);
            Component component = Base.getComponent(Tracking.COMPONENT_ID);
            if (component != null) {
                ITracking iTracking = (ITracking) component;
                HashMap hashMap = new HashMap();
                hashMap.put(Tracking.KEY_MTX_SELLID, googlePlayTransaction.getItemSku());
                iTracking.logEvent(Tracking.EVENT_MTX_ITEM_BEGIN_PURCHASE, hashMap);
            }
            googlePlayCallPurchaseItem(googlePlayTransaction);
            return null;
        }
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public void refreshAvailableCatalogItems() {
        if (this.mGooglePlayIabHelper == null || !this.mGooglePlayIabHelper.isServiceAvailable()) {
            Log.Helper.LOGW(this, "refreshAvailable returning because billing is unavailable");
            broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_REFRESH_CATALOG_FINISHED, String.valueOf(NimbleMTXError.Code.BILLING_NOT_AVAILABLE), null, null);
        } else if (Network.getComponent().getStatus() != Network.Status.OK) {
            Log.Helper.LOGW(this, "refreshAvailable returning because there is no network connection");
            broadcastLocalEvent(INimbleMTX.NIMBLE_NOTIFICATION_MTX_REFRESH_CATALOG_FINISHED, String.valueOf(Error.Code.NETWORK_NO_CONNECTION), null, null);
        } else {
            networkCallGetAvailableItems();
        }
    }

    @Override // com.ea.nimble.Component
    public void restore() {
        Log.Helper.LOGD(this, "Component restore");
        this.m_synergyCatalog = new SynergyCatalog(SynergyCatalog.StoreType.GOOGLE);
        loadFromPersistence();
        Utility.registerReceiver(Global.NOTIFICATION_LANGUAGE_CHANGE, new BroadcastReceiver() { // from class: com.ea.nimble.mtx.googleplay.GooglePlay.3
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.Helper.LOGD(this, "refreshing catalog items after language change");
                GooglePlay.this.refreshAvailableCatalogItems();
            }
        });
        ApplicationLifecycle.getComponent().registerActivityEventCallbacks(this);
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public void restorePurchasedTransactions() {
        restorePurchasedTransactionsImpl(true);
    }

    @Override // com.ea.nimble.Component
    public void resume() {
        Log.Helper.LOGD(this, "Component resume");
        if (!this.mGooglePlayIabHelper.isServiceAvailable()) {
            this.mGooglePlayIabHelper.dispose();
            createIabHelper();
        }
        for (GooglePlayTransaction googlePlayTransaction : this.mPendingTransactions.values()) {
            if (googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_SYNERGY_VERIFICATION || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_NONCE) {
                new PurchaseTransactionVerifier().verifyTransaction(googlePlayTransaction);
            }
        }
        this.m_transactionRecorder.recordTransactions();
        this.m_itemRestorer.restoreItems();
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public Error resumeTransaction(String str, INimbleMTX.PurchaseTransactionCallback purchaseTransactionCallback, INimbleMTX.ItemGrantedCallback itemGrantedCallback, INimbleMTX.FinalizeTransactionCallback finalizeTransactionCallback) {
        GooglePlayTransaction googlePlayTransaction;
        Log.Helper.LOGV(this, "Resuming transaction id, %s.", Utility.safeString(str));
        if (str == null) {
            return new NimbleMTXError(NimbleMTXError.Code.UNRECOGNIZED_TRANSACTION_ID, "Null transaction ID");
        }
        if (this.m_restoreInProgress) {
            Log.Helper.LOGD(this, "resumeTransaction called while restore is in progress.");
            return new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_PENDING, "Can't resume transaction while restore is in progress.");
        }
        if (this.mPendingTransactions.size() > 0) {
            GooglePlayTransaction googlePlayTransaction2 = this.mPendingTransactions.get(str);
            if (googlePlayTransaction2 == null) {
                Log.Helper.LOGD(this, "Resume called while transactions are pending: " + this.mPendingTransactions);
                return new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_PENDING, "Another transaction is pending. It needs to finish and finalize first.");
            } else if (googlePlayTransaction2.mError == null) {
                return new Error(Error.Code.INVALID_ARGUMENT, "Cannot resume a pending transaction with no error");
            } else {
                googlePlayTransaction = googlePlayTransaction2;
                if (googlePlayTransaction2.mTransactionType == NimbleMTXTransaction.TransactionType.RESTORE) {
                    return new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_NOT_RESUMABLE, "Cannot resume a restore transaction. Only Purchase transactions can be resumed.");
                }
            }
        } else {
            googlePlayTransaction = this.mRecoveredTransactions.remove(str);
            if (googlePlayTransaction == null) {
                return new NimbleMTXError(NimbleMTXError.Code.UNRECOGNIZED_TRANSACTION_ID, "No transaction for given transaction ID.");
            }
            if (googlePlayTransaction.mTransactionType == NimbleMTXTransaction.TransactionType.RESTORE) {
                return new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_NOT_RESUMABLE, "Cannot resume a restore transaction. Only Purchase transactions can be resumed.");
            }
            this.mPendingTransactions.put(googlePlayTransaction.getTransactionId(), googlePlayTransaction);
        }
        if (googlePlayTransaction.mError != null && (!(googlePlayTransaction.mError instanceof Error) || ((Error) googlePlayTransaction.mError).getCode() != NimbleMTXError.Code.TRANSACTION_SUPERSEDED.intValue())) {
            Log.Helper.LOGD(this, "Resuming transaction that failed in state " + googlePlayTransaction.mFailedState + " with error " + googlePlayTransaction.mError);
            googlePlayTransaction.mGooglePlayTransactionState = googlePlayTransaction.mFailedState;
            googlePlayTransaction.mFailedState = null;
            googlePlayTransaction.mError = null;
        }
        if (googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.USER_INITIATED || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GOOGLEPLAY_ACTIVITY_RESPONSE) {
            googlePlayTransaction.mFailedState = googlePlayTransaction.mGooglePlayTransactionState;
            googlePlayTransaction.mGooglePlayTransactionState = GooglePlayTransaction.GooglePlayTransactionState.COMPLETE;
            googlePlayTransaction.mError = new NimbleMTXError(NimbleMTXError.Code.NON_CRITICAL_INTERRUPTION, "MTX transaction interrupted before account charged.");
        }
        googlePlayTransaction.mPurchaseCallback = purchaseTransactionCallback;
        googlePlayTransaction.mFinalizeCallback = finalizeTransactionCallback;
        googlePlayTransaction.mItemGrantedCallback = itemGrantedCallback;
        savePendingTransactionsToPersistence();
        if (googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_NONCE || googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_SYNERGY_VERIFICATION) {
            new PurchaseTransactionVerifier().verifyTransaction(googlePlayTransaction);
            return null;
        } else if (googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GAME_TO_CONFIRM_ITEM_GRANT) {
            if (googlePlayTransaction.mPurchaseCallback == null) {
                return new Error(Error.Code.MISSING_CALLBACK, "Resumed transaction not given purchase callback.");
            }
            googlePlayTransaction.mPurchaseCallback.purchaseComplete(googlePlayTransaction);
            return null;
        } else if (googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.WAITING_FOR_GOOGLEPLAY_CONSUMPTION) {
            if (googlePlayTransaction.mItemGrantedCallback == null) {
                return new Error(Error.Code.MISSING_CALLBACK, "Resumed transaction not given item granted callback.");
            }
            googlePlayConsumeItem(googlePlayTransaction);
            return null;
        } else if (googlePlayTransaction.mGooglePlayTransactionState == GooglePlayTransaction.GooglePlayTransactionState.COMPLETE) {
            finalizeTransaction(str, googlePlayTransaction.mFinalizeCallback);
            return null;
        } else {
            Log.Helper.LOGE(this, "ResumeTransaction called on a transaction that can't be resumed: " + googlePlayTransaction);
            return new NimbleMTXError(NimbleMTXError.Code.TRANSACTION_NOT_RESUMABLE, "Transaction not in a resumable state.");
        }
    }

    @Override // com.ea.nimble.mtx.INimbleMTX
    public void setPlatformParameters(Map<String, String> map) {
        String str;
        if (map != null && (str = map.get(GOOGLEPLAY_PLATFORM_PARAMETER_APPLICATION_PUBLIC_KEY)) != null) {
            this.m_appPublicKey = str;
            if (this.mGooglePlayIabHelper != null) {
                this.mGooglePlayIabHelper.setApplicationPublicKey(this.m_appPublicKey);
            }
        }
    }

    @Override // com.ea.nimble.Component
    public void setup() {
        Log.Helper.LOGD(this, "Component setup");
        if (this.mGooglePlayIabHelper == null) {
            createIabHelper();
        }
        try {
            if ("false".equalsIgnoreCase(ApplicationEnvironment.getCurrentActivity().getPackageManager().getApplicationInfo(ApplicationEnvironment.getCurrentActivity().getPackageName(), 128).metaData.getString("com.ea.nimble.mtx.enableVerification"))) {
                this.m_verificationEnabled = false;
                return;
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        this.m_verificationEnabled = true;
    }

    @Override // com.ea.nimble.Component
    public void suspend() {
        Log.Helper.LOGD(this, "Component suspend");
        this.m_transactionRecorder.cancel();
        this.m_itemRestorer.cancel();
    }

    @Override // com.ea.nimble.Component
    public void teardown() {
        this.m_transactionRecorder.cancel();
        this.m_itemRestorer.cancel();
    }
}
