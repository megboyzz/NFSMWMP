package com.verizon.vcast.apps;

/* access modifiers changed from: package-private */
public interface VzwDigest {
    int doFinal(byte[] bArr, int i);

    String getAlgorithmName();

    int getByteLength();

    int getDigestSize();

    void reset();

    void update(byte b);

    void update(byte[] bArr, int i, int i2);
}
