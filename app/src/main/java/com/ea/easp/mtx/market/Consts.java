package com.ea.easp.mtx.market;

public class Consts {
    public static final String ACTION_CONFIRM_NOTIFICATION = "com.ea.easp.mtx.market.CONFIRM_NOTIFICATION";
    public static final String ACTION_GET_PURCHASE_INFORMATION = "com.ea.easp.mtx.market.GET_PURCHASE_INFORMATION";
    public static final String ACTION_NOTIFY = "com.android.vending.billing.IN_APP_NOTIFY";
    public static final String ACTION_PURCHASE_STATE_CHANGED = "com.android.vending.billing.PURCHASE_STATE_CHANGED";
    public static final String ACTION_RESPONSE_CODE = "com.android.vending.billing.RESPONSE_CODE";
    public static final String ACTION_RESTORE_TRANSACTIONS = "com.ea.easp.mtx.market.RESTORE_TRANSACTIONS";
    public static final String BILLING_REQUEST_API_VERSION = "API_VERSION";
    public static final String BILLING_REQUEST_DEVELOPER_PAYLOAD = "DEVELOPER_PAYLOAD";
    public static final String BILLING_REQUEST_ITEM_ID = "ITEM_ID";
    public static final String BILLING_REQUEST_METHOD = "BILLING_REQUEST";
    public static final String BILLING_REQUEST_NONCE = "NONCE";
    public static final String BILLING_REQUEST_NOTIFY_IDS = "NOTIFY_IDS";
    public static final String BILLING_REQUEST_PACKAGE_NAME = "PACKAGE_NAME";
    public static long BILLING_RESPONSE_INVALID_REQUEST_ID = -1;
    public static final String BILLING_RESPONSE_PURCHASE_INTENT = "PURCHASE_INTENT";
    public static final String BILLING_RESPONSE_REQUEST_ID = "REQUEST_ID";
    public static final String BILLING_RESPONSE_RESPONSE_CODE = "RESPONSE_CODE";
    public static final String INAPP_REQUEST_ID = "request_id";
    public static final String INAPP_RESPONSE_CODE = "response_code";
    public static final String INAPP_SIGNATURE = "inapp_signature";
    public static final String INAPP_SIGNED_DATA = "inapp_signed_data";
    public static final String MARKET_BILLING_SERVICE_ACTION = "com.android.vending.billing.MarketBillingService.BIND";
    public static final String NOTIFICATION_ID = "notification_id";
    public static int STORE_AMAZON = 2;
    public static int STORE_ANDROID = 1;
    public static int STORE_UNKNOWN = 0;
    public static int STORE_VERIZON = 3;

    public enum PurchaseState {
        PURCHASED,
        CANCELED,
        REFUNDED;

        public static PurchaseState valueOf(int i) {
            PurchaseState[] values = values();
            return (i < 0 || i >= values.length) ? CANCELED : values[i];
        }
    }

    public enum ResponseCode {
        RESULT_OK,
        RESULT_USER_CANCELED,
        RESULT_SERVICE_UNAVAILABLE,
        RESULT_BILLING_UNAVAILABLE,
        RESULT_ITEM_UNAVAILABLE,
        RESULT_DEVELOPER_ERROR,
        RESULT_ERROR;

        public static ResponseCode valueOf(int i) {
            ResponseCode[] values = values();
            return (i < 0 || i >= values.length) ? RESULT_ERROR : values[i];
        }
    }
}
