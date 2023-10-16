package com.ea.easp.mtx.market;

import com.ea.easp.Debug;
import com.ea.easp.mtx.market.Consts;
import com.ea.easp.mtx.market.util.Base64;
import com.ea.easp.mtx.market.util.Base64DecoderException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Security {
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String TAG = "Security";
    private static HashSet<Long> sKnownNonces = new HashSet<>();

    public static class VerifiedPurchase {
        public String developerPayload;
        public String notificationId;
        public String orderId;
        public String productId;
        public Consts.PurchaseState purchaseState;
        public long purchaseTime;

        public VerifiedPurchase(Consts.PurchaseState purchaseState2, String str, String str2, String str3, long j, String str4) {
            this.purchaseState = purchaseState2;
            this.notificationId = str;
            this.productId = str2;
            this.orderId = str3;
            this.purchaseTime = j;
            this.developerPayload = str4;
        }
    }

    public static long addNonce(long j) {
        sKnownNonces.add(Long.valueOf(j));
        return j;
    }

    public static ArrayList<VerifiedPurchase> createVerifiedPurchasesList(String str, boolean z) {
        if (str == null) {
            Debug.Log.e(TAG, "data is null");
            return null;
        }
        Debug.Log.i(TAG, "signedData: " + str + ", verified " + z);
        int i = 0;
        try {
            JSONObject jSONObject = new JSONObject(str);
            long optLong = jSONObject.optLong("nonce");
            JSONArray optJSONArray = jSONObject.optJSONArray("orders");
            if (optJSONArray != null) {
                i = optJSONArray.length();
            }
            if (!isNonceKnown(optLong)) {
                Debug.Log.w(TAG, "createVerifiedPurchasesList(): Nonce not found: " + optLong);
                return null;
            }
            ArrayList<VerifiedPurchase> arrayList = new ArrayList<>();
            for (int i2 = 0; i2 < i; i2++) {
                try {
                    JSONObject jSONObject2 = optJSONArray.getJSONObject(i2);
                    Consts.PurchaseState valueOf = Consts.PurchaseState.valueOf(jSONObject2.getInt("purchaseState"));
                    String string = jSONObject2.getString("productId");
                    jSONObject2.getString("packageName");
                    long j = jSONObject2.getLong("purchaseTime");
                    String optString = jSONObject2.optString("orderId", "");
                    String optString2 = jSONObject2.optString("notificationId", null);
                    String optString3 = jSONObject2.optString("developerPayload", null);
                    if (valueOf != Consts.PurchaseState.PURCHASED || z) {
                        arrayList.add(new VerifiedPurchase(valueOf, optString2, string, optString, j, optString3));
                    }
                } catch (JSONException e) {
                    Debug.Log.e(TAG, "createVerifiedPurchasesList(): JSON exception: ", e);
                    arrayList = null;
                }
            }
            removeNonce(optLong);
            return arrayList;
        } catch (JSONException e2) {
            Debug.Log.e(TAG, "createVerifiedPurchasesList(): JSON malformed. Reason: " + e2.getMessage());
            return null;
        }
    }

    public static PublicKey generatePublicKey(String str) {
        try {
            return KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePublic(new X509EncodedKeySpec(Base64.decode(str)));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e2) {
            Debug.Log.e(TAG, "Invalid key specification.");
            throw new IllegalArgumentException(e2);
        } catch (Base64DecoderException e3) {
            Debug.Log.e(TAG, "Base64 decoding failed.");
            throw new IllegalArgumentException(e3);
        }
    }

    public static boolean isNonceKnown(long j) {
        return sKnownNonces.contains(Long.valueOf(j));
    }

    public static void removeNonce(long j) {
        sKnownNonces.remove(Long.valueOf(j));
    }

    public static boolean verify(PublicKey publicKey, String str, String str2) {
        Debug.Log.i(TAG, "signature: " + str2);
        try {
            Signature instance = Signature.getInstance(SIGNATURE_ALGORITHM);
            instance.initVerify(publicKey);
            instance.update(str.getBytes());
            if (instance.verify(Base64.decode(str2))) {
                return true;
            }
            Debug.Log.e(TAG, "Signature verification failed.");
            return false;
        } catch (NoSuchAlgorithmException e) {
            Debug.Log.e(TAG, "NoSuchAlgorithmException.");
            return false;
        } catch (InvalidKeyException e2) {
            Debug.Log.e(TAG, "Invalid key specification.");
            return false;
        } catch (SignatureException e3) {
            Debug.Log.e(TAG, "Signature exception.");
            return false;
        } catch (Base64DecoderException e4) {
            Debug.Log.e(TAG, "Base64 decoding failed.");
            return false;
        }
    }
}
