package com.verizon.vcast.apps;

/* access modifiers changed from: package-private */
public interface VzwAsymBlkCipher {
    int getInputBlockSize();

    int getOutputBlockSize();

    void init(boolean z, VzwRSAKyParam vzwRSAKyParam);

    byte[] processBlock(byte[] bArr, int i, int i2) throws Exception;
}
