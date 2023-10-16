package com.verizon.vcast.apps;

/* access modifiers changed from: package-private */
public abstract class VzwGenDigest implements VzwDigest {
    private static final int BYTE_LENGTH = 64;
    private long byteCount;
    private byte[] xBuf;
    private int xBufOff;

    protected VzwGenDigest() {
        this.xBuf = new byte[4];
        this.xBufOff = 0;
    }

    protected VzwGenDigest(VzwGenDigest vzwGenDigest) {
        this.xBuf = new byte[vzwGenDigest.xBuf.length];
        System.arraycopy(vzwGenDigest.xBuf, 0, this.xBuf, 0, vzwGenDigest.xBuf.length);
        this.xBufOff = vzwGenDigest.xBufOff;
        this.byteCount = vzwGenDigest.byteCount;
    }

    public void finish() {
        long j = this.byteCount << 3;
        update(Byte.MIN_VALUE);
        while (this.xBufOff != 0) {
            update((byte) 0);
        }
        processLength(j);
        processBlock();
    }

    @Override // com.verizon.vcast.apps.VzwDigest
    public int getByteLength() {
        return 64;
    }

    /* access modifiers changed from: protected */
    public abstract void processBlock();

    /* access modifiers changed from: protected */
    public abstract void processLength(long j);

    /* access modifiers changed from: protected */
    public abstract void processWord(byte[] bArr, int i);

    @Override // com.verizon.vcast.apps.VzwDigest
    public void reset() {
        this.byteCount = 0;
        this.xBufOff = 0;
        for (int i = 0; i < this.xBuf.length; i++) {
            this.xBuf[i] = 0;
        }
    }

    @Override // com.verizon.vcast.apps.VzwDigest
    public void update(byte b) {
        byte[] bArr = this.xBuf;
        int i = this.xBufOff;
        this.xBufOff = i + 1;
        bArr[i] = b;
        if (this.xBufOff == this.xBuf.length) {
            processWord(this.xBuf, 0);
            this.xBufOff = 0;
        }
        this.byteCount++;
    }

    @Override // com.verizon.vcast.apps.VzwDigest
    public void update(byte[] bArr, int i, int i2) {
        while (this.xBufOff != 0 && i2 > 0) {
            update(bArr[i]);
            i++;
            i2--;
        }
        while (i2 > this.xBuf.length) {
            processWord(bArr, i);
            i += this.xBuf.length;
            i2 -= this.xBuf.length;
            this.byteCount += (long) this.xBuf.length;
        }
        while (i2 > 0) {
            update(bArr[i]);
            i++;
            i2--;
        }
    }
}
