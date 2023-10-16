package com.ea.easp.mtx.market;

import android.app.Activity;
import android.content.Intent;
import com.ea.easp.Debug;
import com.ea.easp.TaskLauncher;
import com.ea.easp.mtx.market.amazon.AmazonBillingService;
import com.ea.easp.mtx.market.android.AndroidBillingService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MarketJNI {
    static final int REQUEST_BILLING_SURRORTED = 0;
    static final int REQUEST_PURCHASE = 1;
    static final int REQUEST_RESTORE = 2;
    private static final String TAG = "MarketJNI";
    private ActiveRequest mActiveRequest = ActiveRequest.NONE;
    private Activity mActivity;
    private BillingService mBillingService;
    private MarketJNIPurchaseObserver mMarketJNIPurchaseObserver;
    private int mNonceRequestNextID = 0;
    private HashMap<Integer, Object> mSentNonceRequests = new HashMap<>();
    private StoreType mStoreType = StoreType.UNKNOWN;
    private TaskLauncher mTaskLauncher;

    private enum ActiveRequest {
        NONE,
        PURCHASE,
        RESTORE
    }

    /* access modifiers changed from: private */
    public class MarketJNIPurchaseObserver extends PurchaseObserver {
        public MarketJNIPurchaseObserver() {
            super(MarketJNI.this.mActivity);
        }

        private void notifyJNIAboutFailInGLThread(final int ErrorCode, final int RequestID, final String ErrorDescription) {
            MarketJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                @Override
                public void run() {
                    MarketJNI.this.onRequestFailJNI(ErrorCode, RequestID, ErrorDescription);
                }
            });
        }

        @Override
        public void onBillingSupported(final boolean mSupported) {
            Debug.Log.i(MarketJNI.TAG, "supported: " + mSupported);
            MarketJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                @Override
                public void run() {
                    MarketJNI.this.onBillingSupportedSucceedJNI(mSupported);
                }
            });
        }

        @Override
        public void onPurchaseStateChange(final ArrayList<Security.VerifiedPurchase> mPurchases,
                                          final boolean mVerified,
                                          final String mSignedData,
                                          final String mSignature) {
            Debug.Log.i(MarketJNI.TAG, "onPurchaseStateChange()  purchases size: " + mPurchases.size());
            MarketJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                @Override
                public void run() {
                    if (mPurchases.size() != 0) {
                        MarketJNI.this.purchasesListSizeJNI(mPurchases.size(), mSignedData, mSignature);
                        Iterator<Security.VerifiedPurchase> it = mPurchases.iterator();
                        while (it.hasNext()) {
                            Security.VerifiedPurchase next = it.next();
                            Debug.Log.d(MarketJNI.TAG, "purchaseState: " + next.purchaseState);
                            Debug.Log.d(MarketJNI.TAG, "notificationId:" + next.notificationId);
                            Debug.Log.d(MarketJNI.TAG, "productId:" + next.productId);
                            Debug.Log.d(MarketJNI.TAG, "orderId:" + next.orderId);
                            Debug.Log.d(MarketJNI.TAG, "developerPayload:" + next.developerPayload);
                            MarketJNI.this.onPurchaseStateChangeJNI(next.purchaseState.ordinal(), next.productId, next.purchaseTime, next.developerPayload);
                        }
                    } else if (!mVerified) {
                        Debug.Log.i(MarketJNI.TAG, "onPurchaseStateChange() purchases list is empty: treat it as error");
                        MarketJNI.this.onRequestFailJNI(1, -999998, "purchase transaction signature were not verified, so list of purchases is empty.");
                    } else {
                        Debug.Log.i(MarketJNI.TAG, "onPurchaseStateChange() purchases list is empty, but transaction is verified, treat as success restoring.");
                        MarketJNI.this.purchasesListSizeJNI(-1, mSignedData, mSignature);
                        MarketJNI.this.onPurchaseStateChangeJNI(0, null, 0, null);
                    }
                }
            });
        }

        @Override
        public void onRequestPurchaseResponse(BillingService.IRequestPurchase iRequestPurchase, Consts.ResponseCode responseCode) {
            Debug.Log.d(MarketJNI.TAG, iRequestPurchase.getProductId() + ": " + responseCode);
            if (responseCode == Consts.ResponseCode.RESULT_OK) {
                Debug.Log.i(MarketJNI.TAG, "purchase request response is RESULT_OK");
            } else if (responseCode == Consts.ResponseCode.RESULT_USER_CANCELED) {
                Debug.Log.i(MarketJNI.TAG, "user canceled purchase");
                notifyJNIAboutFailInGLThread(1, -999997, "dismissed purchase dialog");
            } else {
                Debug.Log.i(MarketJNI.TAG, "purchase error: " + responseCode);
                notifyJNIAboutFailInGLThread(1, -999996, "purchase error: " + responseCode);
            }
        }

        @Override // com.ea.easp.mtx.market.PurchaseObserver
        public void onRestoreTransactionsResponse(BillingService.IRestoreTransactions iRestoreTransactions, Consts.ResponseCode responseCode) {
            if (responseCode == Consts.ResponseCode.RESULT_OK) {
                Debug.Log.i(MarketJNI.TAG, "restore request was successfully sent to server");
            } else if (responseCode == Consts.ResponseCode.RESULT_USER_CANCELED) {
                Debug.Log.i(MarketJNI.TAG, "user canceled restore");
                notifyJNIAboutFailInGLThread(2, -999994, "user canceled restore");
            } else {
                Debug.Log.d(MarketJNI.TAG, "RestoreTransactions error: " + responseCode);
                notifyJNIAboutFailInGLThread(2, -999994, "RestoreTransactions error: " + responseCode);
            }
        }
    }

    public enum StoreType {
        UNKNOWN,
        ANDROID,
        AMAZON,
        VERIZON
    }

    public MarketJNI(Activity activity, TaskLauncher taskLauncher, StoreType storeType) {
        this.mActivity = activity;
        this.mTaskLauncher = taskLauncher;
        this.mStoreType = storeType;
    }

    /* access modifiers changed from: package-private */
    public int getNextNonceRequestID() {
        int i = this.mNonceRequestNextID;
        this.mNonceRequestNextID++;
        return i;
    }

    public void getNonce(Object obj) {
        final int nextNonceRequestID = getNextNonceRequestID();
        this.mSentNonceRequests.put(nextNonceRequestID, obj);

        this.mTaskLauncher.runInGLThread(new Runnable() {
            @Override
            public void run() {
                MarketJNI.this.getNonceJNI(nextNonceRequestID);
            }
        });
    }

    public native void getNonceJNI(int i);

    public void init() {
        Debug.Log.i(TAG, "init(): MarketJNI init.");
        initEASPMTXJNI();
        this.mMarketJNIPurchaseObserver = new MarketJNIPurchaseObserver();
        if (this.mStoreType == StoreType.AMAZON) {
            Debug.Log.i(TAG, "init(): Creating AmazonBillingService.");
            this.mBillingService = new AmazonBillingService(new SecurityJNI(this), this.mActivity);
        } else if (this.mStoreType == StoreType.VERIZON) {
            Debug.Log.i(TAG, "init(): Verizon billing not supported.");
        } else if (this.mStoreType == StoreType.ANDROID) {
            this.mBillingService = new AndroidBillingService(new SecurityJNI(this));
        } else {
            Debug.Log.i(TAG, "init(): StoreType not set at MarketJNI initialization. Delaying instantiation.");
            this.mBillingService = null;
        }
        if (this.mBillingService != null) {
            this.mBillingService.setContext(this.mActivity);
        }
        ResponseHandler.register(this.mMarketJNIPurchaseObserver);
    }

    public native void initEASPMTXJNI();

    public void isBillingSupported() {
        this.mTaskLauncher.runInUIThread(new Runnable() {
            /* class com.ea.easp.mtx.market.MarketJNI.AnonymousClass1 */

            /* access modifiers changed from: package-private */
            public Runnable makeErrorHandler() {
                return new Runnable() {
                    /* class com.ea.easp.mtx.market.MarketJNI.AnonymousClass1.AnonymousClass1 */

                    public void run() {
                        Debug.Log.e(MarketJNI.TAG, "checkBillingSupported(): fail to connect to billing service");
                        MarketJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                            /* class com.ea.easp.mtx.market.MarketJNI.AnonymousClass1.AnonymousClass1.AnonymousClass1 */

                            public void run() {
                                MarketJNI.this.onRequestFailJNI(0, -999998, "fail to connect to Android Market");
                            }
                        });
                    }
                };
            }

            public void run() {
                if (MarketJNI.this.mBillingService == null) {
                    throw new RuntimeException("Android billing service not set. Set with SetStoreType.");
                }
                MarketJNI.this.mBillingService.checkBillingSupported(makeErrorHandler());
            }
        });
    }

    public native void onBillingSupportedSucceedJNI(boolean z);

    public void onNonceResult(boolean z, final long mNonce, final int mRequestDataKey) {
        if (z) {
            final Object mRequestData = this.mSentNonceRequests.get(mRequestDataKey);
            this.mTaskLauncher.runInUIThread(new Runnable() {
                @Override
                public void run() {
                    if (MarketJNI.this.mBillingService == null) {
                        throw new RuntimeException("Android billing service not set. Set with SetStoreType.");
                    }
                    MarketJNI.this.mBillingService.OnNonceSucceed(mNonce, mRequestData);
                }
            });
        } else {
            onRequestFailJNI(1, -999993, "fail to get nonce");
        }
        this.mSentNonceRequests.remove(Integer.valueOf(mRequestDataKey));
    }

    public native void onPurchaseStateChangeJNI(int i, String str, long j, String str2);

    public native void onRequestFailJNI(int i, int i2, String str);

    public void onVerify(final boolean mVerified, final String mSignedData, final String mSignature, final int mRequestID) {
        this.mTaskLauncher.runInUIThread(new Runnable() {
            @Override
            public void run() {
                if (MarketJNI.this.mBillingService == null) {
                    throw new RuntimeException("Android billing service not set. Set with SetStoreType.");
                }
                MarketJNI.this.mBillingService.OnVerifyMarketResponse(mVerified, mSignedData, mSignature, mRequestID);
            }
        });
    }

    public void purchase(final String mProductID, final String mExtraParams, final String mPayload) {
        this.mTaskLauncher.runInUIThread(new Runnable() {

            public Runnable makeErrorHandler() {
                return () -> {
                    Debug.Log.e(MarketJNI.TAG, "purchase(): fail to connect to Market");
                    MarketJNI.this.mTaskLauncher.runInGLThread(() -> MarketJNI.this.onRequestFailJNI(1, -999998, "fail to connect to Android Market"));
                };
            }

            @Override
            public void run() {
                if (MarketJNI.this.mBillingService == null) {
                    throw new RuntimeException("Android billing service not set. Set with SetStoreType.");
                }
                MarketJNI.this.mBillingService.requestPurchase(mProductID, mExtraParams, mPayload, makeErrorHandler());
            }
        });
    }

    public native void purchasesListSizeJNI(int i, String str, String str2);

    public void restoreTransactions(final long mNonce) {
        this.mTaskLauncher.runInUIThread(new Runnable() {

            public Runnable makeErrorHandler() {
                return new Runnable() {
                    @Override
                    public void run() {
                        Debug.Log.e(MarketJNI.TAG, "restoreTransactions(): fail to connect to Market");
                        MarketJNI.this.mTaskLauncher.runInGLThread(new Runnable() {
                            /* class com.ea.easp.mtx.market.MarketJNI.AnonymousClass5R.AnonymousClass1.AnonymousClass1 */

                            public void run() {
                                MarketJNI.this.onRequestFailJNI(2, -999998, "fail to connect to Android Market");
                            }
                        });
                    }
                };
            }

            @Override
            public void run() {
                if (MarketJNI.this.mBillingService == null) {
                    throw new RuntimeException("Android billing service not set. Set with SetStoreType.");
                }
                MarketJNI.this.mBillingService.restoreTransactions(mNonce, makeErrorHandler());
            }
        });
    }

    public void setStoreType(int i) {
        if (this.mStoreType != StoreType.UNKNOWN) {
            Debug.Log.i(TAG, "setStoreType: StoreType already set!");
        } else if (this.mBillingService != null) {
            Debug.Log.i(TAG, "setStoreType: BillingService already created!");
        } else {
            if (StoreType.values()[i] == StoreType.AMAZON) {
                Debug.Log.i(TAG, "setStoreType: Creating Amazon Billing Service.");
                this.mBillingService = new AmazonBillingService(new SecurityJNI(this), this.mActivity);
            } else if (StoreType.values()[i] == StoreType.ANDROID) {
                Debug.Log.i(TAG, "setStoreType: Creating Android Billing Service.");
                this.mBillingService = new AndroidBillingService(new SecurityJNI(this));
            } else {
                Debug.Log.i(TAG, "setStoreType: StoreType not supported!");
                return;
            }
            this.mStoreType = StoreType.values()[i];
            this.mBillingService.setContext(this.mActivity);
        }
    }

    public void shutdown() {
        ResponseHandler.unregister(this.mMarketJNIPurchaseObserver);
        if (this.mBillingService != null) {
            this.mBillingService.shutdown();
            Intent intent = new Intent();
            intent.setClass(this.mActivity, this.mBillingService.getClass());
            this.mActivity.stopService(intent);
        }
        shutdownEASPMTXJNI();
    }

    public native void shutdownEASPMTXJNI();

    public void verify(final String mSignedData, final String mSignature, final int mRequestID, final int mStoreType) {
        this.mTaskLauncher.runInGLThread(new Runnable() {
            @Override
            public void run() {
                MarketJNI.this.verifyJNI(mSignedData, mSignature, mRequestID, mStoreType);
            }
        });
    }

    public native void verifyJNI(String SignedData, String Signature, int RequestID, int StoreType);
}
