package com.verizon.vcast.apps;

/* access modifiers changed from: package-private */
public class VzwPKCS1Encd implements VzwAsymBlkCipher {
    private static final int HEADER_LENGTH = 10;
    public static final String STRICT_LENGTH_ENABLED_PROPERTY = "org.bouncycastle.pkcs1.strict";
    private VzwAsymBlkCipher engine;
    private boolean useStrictLength = useStrict();

    public VzwPKCS1Encd(VzwAsymBlkCipher vzwAsymBlkCipher) {
        this.engine = vzwAsymBlkCipher;
    }

    private byte[] decodeBlock(byte[] bArr, int i, int i2) throws Exception {
        byte b;
        byte[] processBlock = this.engine.processBlock(bArr, i, i2);
        if (processBlock.length < getOutputBlockSize()) {
            throw new Exception("block truncated");
        }
        byte b2 = processBlock[0];
        if (b2 != 1 && b2 != 2) {
            throw new Exception("unknown block type");
        } else if (!this.useStrictLength || processBlock.length == this.engine.getOutputBlockSize()) {
            int i3 = 1;
            while (i3 != processBlock.length && (b = processBlock[i3]) != 0) {
                if (b2 != 1 || b == -1) {
                    i3++;
                } else {
                    throw new Exception("block padding incorrect");
                }
            }
            int i4 = i3 + 1;
            if (i4 > processBlock.length || i4 < 10) {
                throw new Exception("no data in block");
            }
            byte[] bArr2 = new byte[(processBlock.length - i4)];
            System.arraycopy(processBlock, i4, bArr2, 0, bArr2.length);
            return bArr2;
        } else {
            throw new Exception("block incorrect size");
        }
    }

    private boolean useStrict() {
        String property = System.getProperty(STRICT_LENGTH_ENABLED_PROPERTY);
        return property == null || property.equals("true");
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public int getInputBlockSize() {
        return this.engine.getInputBlockSize();
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public int getOutputBlockSize() {
        return this.engine.getOutputBlockSize() - 10;
    }

    public VzwAsymBlkCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public void init(boolean z, VzwRSAKyParam vzwRSAKyParam) {
        this.engine.init(z, vzwRSAKyParam);
    }

    @Override // com.verizon.vcast.apps.VzwAsymBlkCipher
    public byte[] processBlock(byte[] bArr, int i, int i2) throws Exception {
        return decodeBlock(bArr, i, i2);
    }
}
