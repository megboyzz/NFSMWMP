package com.verizon.vcast.apps;

class VzwRSACreEng {
    private boolean forEncryption;
    private VzwRSAKyParam key;

    VzwRSACreEng() {
    }

    public VzwBigInt convertInput(byte[] bArr, int i, int i2) {
        byte[] bArr2;
        if (i2 > getInputBlockSize() + 1) {
            throw new IllegalStateException("input too large for RSA cipher.");
        } else if (i2 != getInputBlockSize() + 1 || this.forEncryption) {
            if (i == 0 && i2 == bArr.length) {
                bArr2 = bArr;
            } else {
                bArr2 = new byte[i2];
                System.arraycopy(bArr, i, bArr2, 0, i2);
            }
            VzwBigInt vzwBigInt = new VzwBigInt(1, bArr2);
            if (vzwBigInt.compareTo(this.key.getModulus()) < 0) {
                return vzwBigInt;
            }
            throw new IllegalStateException("input too large for RSA cipher.");
        } else {
            throw new IllegalStateException("input too large for RSA cipher.");
        }
    }

    public byte[] convertOutput(VzwBigInt vzwBigInt) {
        byte[] byteArray = vzwBigInt.toByteArray();
        if (this.forEncryption) {
            if (byteArray[0] == 0 && byteArray.length > getOutputBlockSize()) {
                byte[] bArr = new byte[(byteArray.length - 1)];
                System.arraycopy(byteArray, 1, bArr, 0, bArr.length);
                return bArr;
            } else if (byteArray.length < getOutputBlockSize()) {
                byte[] bArr2 = new byte[getOutputBlockSize()];
                System.arraycopy(byteArray, 0, bArr2, bArr2.length - byteArray.length, byteArray.length);
                return bArr2;
            }
        } else if (byteArray[0] == 0) {
            byte[] bArr3 = new byte[(byteArray.length - 1)];
            System.arraycopy(byteArray, 1, bArr3, 0, bArr3.length);
            return bArr3;
        }
        return byteArray;
    }

    public int getInputBlockSize() {
        int bitLength = this.key.getModulus().bitLength();
        return this.forEncryption ? ((bitLength + 7) / 8) - 1 : (bitLength + 7) / 8;
    }

    public int getOutputBlockSize() {
        int bitLength = this.key.getModulus().bitLength();
        return this.forEncryption ? (bitLength + 7) / 8 : ((bitLength + 7) / 8) - 1;
    }

    public void init(boolean z, VzwRSAKyParam vzwRSAKyParam) {
        this.key = vzwRSAKyParam;
        this.forEncryption = z;
    }

    public VzwBigInt processBlock(VzwBigInt vzwBigInt) {
        return vzwBigInt.modPow(this.key.getExponent(), this.key.getModulus());
    }
}
