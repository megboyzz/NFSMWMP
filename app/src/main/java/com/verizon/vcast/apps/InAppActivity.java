package com.verizon.vcast.apps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.verizon.vcast.apps.InAppPurchasor;
import java.util.Iterator;

public abstract class InAppActivity extends Activity {
    private static final String TAG = "InAppActivity";
    public static final int purchaseInAppContentRequestCode = 4684978;
    protected InAppPurchasor inAppPurchasor;

    private void reportPurchaseError(int i) {
        InAppPurchasor inAppPurchasor2 = this.inAppPurchasor;
        inAppPurchasor2.getClass();
        InAppPurchasor.PurchaseInAppContentResult purchaseInAppContentResult = new InAppPurchasor.PurchaseInAppContentResult();
        purchaseInAppContentResult.setResult(Integer.valueOf(i));
        Log.e(TAG, "Purchase Response is invalid.");
        onPurchaseResult(purchaseInAppContentResult);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i != 4684978) {
            return;
        }
        if (intent == null) {
            reportPurchaseError(4);
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            reportPurchaseError(4);
            return;
        }
        Bundle bundle = extras.getBundle("bundle");
        if (bundle == null) {
            reportPurchaseError(4);
            return;
        }
        PurchaseInAppContentResult purchaseInAppContentResult = (PurchaseInAppContentResult) bundle.getParcelable("purchaseResult");
        if (purchaseInAppContentResult == null) {
            reportPurchaseError(4);
            return;
        }
        InAppPurchasor.PurchaseInAppContentResult convertPurchaseInAppContentResult = new APIUtils(this.inAppPurchasor).convertPurchaseInAppContentResult(purchaseInAppContentResult);
        if (convertPurchaseInAppContentResult.getResult().intValue() == 3) {
            try {
                Iterator<String> it = new LicenseAuthenticatorInternal().getInAppLicenses(convertPurchaseInAppContentResult.getLicense()).iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String next = it.next();
                    int indexOf = next.indexOf("<RCID>");
                    int indexOf2 = next.indexOf("</RCID>");
                    if (indexOf != -1 && indexOf2 != -1 && next.substring(indexOf + 6, indexOf2).trim().equals(this.inAppPurchasor.itemIDBeingPurchased)) {
                        convertPurchaseInAppContentResult.setLicense(next);
                        if (convertPurchaseInAppContentResult.getPurchaseID() == null) {
                            int indexOf3 = next.indexOf("<PurchaseId>");
                            int indexOf4 = next.indexOf("</PurchaseId>");
                            if (indexOf != -1 && indexOf2 != -1) {
                                convertPurchaseInAppContentResult.setPurchaseID(next.substring(indexOf3 + 12, indexOf4));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("InAppPurchasor", "Cannot get specific in app license and populate purchase id.", e);
            }
        }
        onPurchaseResult(convertPurchaseInAppContentResult);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.inAppPurchasor = InAppPurchasor.getInstance(this);
    }

    /* access modifiers changed from: protected */
    public abstract void onPurchaseResult(InAppPurchasor.PurchaseInAppContentResult purchaseInAppContentResult);
}
