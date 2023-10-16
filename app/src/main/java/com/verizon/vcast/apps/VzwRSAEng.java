package com.verizon.vcast.apps;

/* access modifiers changed from: package-private */
public class VzwRSAEng implements VzwAsymBlkCipher {
    private VzwRSACreEng core;

    VzwRSAEng() {
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public void init(boolean z, VzwRSAKyParam vzwRSAKyParam) {
        if (this.core == null) {
            this.core = new VzwRSACreEng();
        }
        this.core.init(z, vzwRSAKyParam);
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public byte[] processBlock(byte[] bArr, int i, int i2) {
        if (this.core != null) {
            return this.core.convertOutput(this.core.processBlock(this.core.convertInput(bArr, i, i2)));
        }
        throw new IllegalStateException("RSA engine not initialised");
    }
}
