package com.ea.easp.mtx.market.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ea.easp.Debug;
import com.ea.easp.mtx.market.Consts;

public class BillingReceiver extends BroadcastReceiver {
    private static final String TAG = "BillingReceiver";

    private void checkResponseCode(Context context, long j, int i) {
        Intent intent = new Intent(Consts.ACTION_RESPONSE_CODE);
        intent.setClass(context, AndroidBillingService.class);
        intent.putExtra(Consts.INAPP_REQUEST_ID, j);
        intent.putExtra(Consts.INAPP_RESPONSE_CODE, i);
        context.startService(intent);
    }

    private void notify(Context context, String str) {
        Intent intent = new Intent(Consts.ACTION_GET_PURCHASE_INFORMATION);
        intent.setClass(context, AndroidBillingService.class);
        intent.putExtra(Consts.NOTIFICATION_ID, str);
        context.startService(intent);
    }

    private void purchaseStateChanged(Context context, String str, String str2) {
        Debug.Log.d(TAG, "purchaseStateChanged()...");
        if (str != null) {
            Debug.Log.d(TAG, "signedData is \"" + str + "\"");
        }
        Intent intent = new Intent(Consts.ACTION_PURCHASE_STATE_CHANGED);
        intent.setClass(context, AndroidBillingService.class);
        intent.putExtra(Consts.INAPP_SIGNED_DATA, str);
        intent.putExtra(Consts.INAPP_SIGNATURE, str2);
        context.startService(intent);
        Debug.Log.d(TAG, "...purchaseStateChanged()");
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Consts.ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
            purchaseStateChanged(context, intent.getStringExtra(Consts.INAPP_SIGNED_DATA), intent.getStringExtra(Consts.INAPP_SIGNATURE));
        } else if (Consts.ACTION_NOTIFY.equals(action)) {
            String stringExtra = intent.getStringExtra(Consts.NOTIFICATION_ID);
            Debug.Log.i(TAG, "notifyId: " + stringExtra);
            notify(context, stringExtra);
        } else if (Consts.ACTION_RESPONSE_CODE.equals(action)) {
            checkResponseCode(context, intent.getLongExtra(Consts.INAPP_REQUEST_ID, -1), intent.getIntExtra(Consts.INAPP_RESPONSE_CODE, Consts.ResponseCode.RESULT_ERROR.ordinal()));
        } else {
            Debug.Log.w(TAG, "unexpected action: " + action);
        }
    }
}
