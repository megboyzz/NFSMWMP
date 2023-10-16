/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.annotation.SuppressLint
 *  android.os.Build$VERSION
 */
package com.ea.nimble;

import android.annotation.SuppressLint;

import com.ea.ironmonkey.devmenu.util.Observer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;

class Encryptor {
    private static int ENCRYPTION_KEY_LENGHT = 128;
    private static int ENCRYPTION_KEY_ROUND = 997;
    private Cipher m_decryptor;
    private Cipher m_encryptor;

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Could not resolve type clashes
     * Unable to fully structure code
     */
    @SuppressLint(value={"NewApi"})
    private void initialize() throws GeneralSecurityException {
        Observer.onCallingMethod();
    }

    public ObjectInputStream encryptInputStream(InputStream inputStream) throws IOException, GeneralSecurityException {
        if (this.m_encryptor != null) {
            if (this.m_encryptor != null) return new ObjectInputStream(new CipherInputStream(inputStream, this.m_decryptor));
        }
        this.initialize();
        return new ObjectInputStream(new CipherInputStream(inputStream, this.m_decryptor));
    }

    public ObjectOutputStream encryptOutputStream(OutputStream outputStream) throws IOException, GeneralSecurityException {
        if (this.m_encryptor != null) {
            if (this.m_encryptor != null) return new ObjectOutputStream(new CipherOutputStream(outputStream, this.m_encryptor));
        }
        this.initialize();
        return new ObjectOutputStream(new CipherOutputStream(outputStream, this.m_encryptor));
    }
}

