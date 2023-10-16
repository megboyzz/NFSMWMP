/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.text.TextUtils
 *  android.util.Log
 */
package com.ea.nimble.mtx.googleplay.util;

import android.text.TextUtils;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Security {
    private static final String KEY_FACTORY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String TAG = "IABUtil/Security";

    public static PublicKey generatePublicKey(String object) {
        try {
            byte[] decode = Base64.decode(object);
            return KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePublic(new X509EncodedKeySpec(decode));
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new RuntimeException(noSuchAlgorithmException);
        }
        catch (InvalidKeySpecException invalidKeySpecException) {
            Log.e((String)TAG, (String)"Invalid key specification.");
            throw new IllegalArgumentException(invalidKeySpecException);
        }
        catch (Base64DecoderException base64DecoderException) {
            Log.e((String)TAG, (String)"Base64 decoding failed.");
            throw new IllegalArgumentException(base64DecoderException);
        }
    }

    public static boolean verify(PublicKey publicKey, String string2, String string3) {
        try {
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(string2.getBytes());
            if (signature.verify(Base64.decode(string3))) return true;
            Log.e((String)TAG, (String)"Signature verification failed.");
            return false;
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            Log.e((String)TAG, (String)"NoSuchAlgorithmException.");
            return false;
        }
        catch (InvalidKeyException invalidKeyException) {
            Log.e((String)TAG, (String)"Invalid key specification.");
            return false;
        }
        catch (SignatureException signatureException) {
            Log.e((String)TAG, (String)"Signature exception.");
            return false;
        }
        catch (Base64DecoderException base64DecoderException) {
            Log.e((String)TAG, (String)"Base64 decoding failed.");
            return false;
        }
    }

    public static boolean verifyPurchase(String string2, String string3, String string4) {
        if (string3 == null) {
            Log.e((String)TAG, (String)"data is null");
            return false;
        }
        if (TextUtils.isEmpty((CharSequence)string4)) return true;
        if (Security.verify(Security.generatePublicKey(string2), string3, string4)) return true;
        Log.w((String)TAG, (String)"signature does not match data.");
        return false;
    }
}

