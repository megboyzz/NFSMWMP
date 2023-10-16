package com.verizon.vcast.apps;

/* access modifiers changed from: package-private */
public class VzwSHA extends VzwGenDigest {
    private static final int DIGEST_LENGTH = 20;
    private static final int Y1 = 1518500249;
    private static final int Y2 = 1859775393;
    private static final int Y3 = -1894007588;
    private static final int Y4 = -899497514;
    private int H1;
    private int H2;
    private int H3;
    private int H4;
    private int H5;
    private int[] X;
    private int xOff;

    public VzwSHA() {
        this.X = new int[80];
        reset();
    }

    public VzwSHA(VzwSHA vzwSHA) {
        super(vzwSHA);
        this.X = new int[80];
        this.H1 = vzwSHA.H1;
        this.H2 = vzwSHA.H2;
        this.H3 = vzwSHA.H3;
        this.H4 = vzwSHA.H4;
        this.H5 = vzwSHA.H5;
        System.arraycopy(vzwSHA.X, 0, this.X, 0, vzwSHA.X.length);
        this.xOff = vzwSHA.xOff;
    }

    private int f(int i, int i2, int i3) {
        return (i & i2) | ((i ^ -1) & i3);
    }

    private int g(int i, int i2, int i3) {
        return (i & i2) | (i & i3) | (i2 & i3);
    }

    private int h(int i, int i2, int i3) {
        return (i ^ i2) ^ i3;
    }

    @Override // com.verizon.vcast.apps.VzwDigest
    public int doFinal(byte[] bArr, int i) {
        finish();
        VzwPack.intToBigEndian(this.H1, bArr, i);
        VzwPack.intToBigEndian(this.H2, bArr, i + 4);
        VzwPack.intToBigEndian(this.H3, bArr, i + 8);
        VzwPack.intToBigEndian(this.H4, bArr, i + 12);
        VzwPack.intToBigEndian(this.H5, bArr, i + 16);
        reset();
        return 20;
    }

    @Override // com.verizon.vcast.apps.VzwDigest
    public String getAlgorithmName() {
        return "SHA-1";
    }

    @Override // com.verizon.vcast.apps.VzwDigest
    public int getDigestSize() {
        return 20;
    }

    /* access modifiers changed from: protected */
    @Override // com.verizon.vcast.apps.VzwGenDigest
    public void processBlock() {
        int i;
        for (int i2 = 16; i2 < 80; i2++) {
            int i3 = ((this.X[i2 - 3] ^ this.X[i2 - 8]) ^ this.X[i2 - 14]) ^ this.X[i2 - 16];
            this.X[i2] = (i3 << 1) | (i3 >>> 31);
        }
        int i4 = this.H1;
        int i5 = this.H2;
        int i6 = this.H3;
        int i7 = this.H4;
        int i8 = this.H5;
        int i9 = 0;
        int i10 = 0;
        while (true) {
            i = i9;
            if (i10 >= 4) {
                break;
            }
            int i11 = i + 1;
            int f = i8 + ((i4 << 5) | (i4 >>> 27)) + f(i5, i6, i7) + this.X[i] + Y1;
            int i12 = (i5 << 30) | (i5 >>> 2);
            int i13 = i11 + 1;
            int f2 = i7 + ((f << 5) | (f >>> 27)) + f(i4, i12, i6) + this.X[i11] + Y1;
            int i14 = (i4 << 30) | (i4 >>> 2);
            int i15 = i13 + 1;
            int f3 = i6 + ((f2 << 5) | (f2 >>> 27)) + f(f, i14, i12) + this.X[i13] + Y1;
            i8 = (f << 30) | (f >>> 2);
            int i16 = i15 + 1;
            i5 = i12 + ((f3 << 5) | (f3 >>> 27)) + f(f2, i8, i14) + this.X[i15] + Y1;
            i7 = (f2 << 30) | (f2 >>> 2);
            i9 = i16 + 1;
            i4 = i14 + ((i5 << 5) | (i5 >>> 27)) + f(f3, i7, i8) + this.X[i16] + Y1;
            i6 = (f3 << 30) | (f3 >>> 2);
            i10++;
        }
        int i17 = 0;
        while (i17 < 4) {
            int i18 = i + 1;
            int h = i8 + ((i4 << 5) | (i4 >>> 27)) + h(i5, i6, i7) + this.X[i] + Y2;
            int i19 = (i5 << 30) | (i5 >>> 2);
            int i20 = i18 + 1;
            int h2 = i7 + ((h << 5) | (h >>> 27)) + h(i4, i19, i6) + this.X[i18] + Y2;
            int i21 = (i4 << 30) | (i4 >>> 2);
            int i22 = i20 + 1;
            int h3 = i6 + ((h2 << 5) | (h2 >>> 27)) + h(h, i21, i19) + this.X[i20] + Y2;
            i8 = (h << 30) | (h >>> 2);
            int i23 = i22 + 1;
            i5 = i19 + ((h3 << 5) | (h3 >>> 27)) + h(h2, i8, i21) + this.X[i22] + Y2;
            i7 = (h2 << 30) | (h2 >>> 2);
            i4 = i21 + ((i5 << 5) | (i5 >>> 27)) + h(h3, i7, i8) + this.X[i23] + Y2;
            i6 = (h3 << 30) | (h3 >>> 2);
            i17++;
            i = i23 + 1;
        }
        int i24 = 0;
        while (i24 < 4) {
            int i25 = i + 1;
            int g = i8 + ((i4 << 5) | (i4 >>> 27)) + g(i5, i6, i7) + this.X[i] + Y3;
            int i26 = (i5 << 30) | (i5 >>> 2);
            int i27 = i25 + 1;
            int g2 = i7 + ((g << 5) | (g >>> 27)) + g(i4, i26, i6) + this.X[i25] + Y3;
            int i28 = (i4 << 30) | (i4 >>> 2);
            int i29 = i27 + 1;
            int g3 = i6 + ((g2 << 5) | (g2 >>> 27)) + g(g, i28, i26) + this.X[i27] + Y3;
            i8 = (g << 30) | (g >>> 2);
            int i30 = i29 + 1;
            i5 = i26 + ((g3 << 5) | (g3 >>> 27)) + g(g2, i8, i28) + this.X[i29] + Y3;
            i7 = (g2 << 30) | (g2 >>> 2);
            i4 = i28 + ((i5 << 5) | (i5 >>> 27)) + g(g3, i7, i8) + this.X[i30] + Y3;
            i6 = (g3 << 30) | (g3 >>> 2);
            i24++;
            i = i30 + 1;
        }
        int i31 = 0;
        while (i31 <= 3) {
            int i32 = i + 1;
            int h4 = i8 + ((i4 << 5) | (i4 >>> 27)) + h(i5, i6, i7) + this.X[i] + Y4;
            int i33 = (i5 << 30) | (i5 >>> 2);
            int i34 = i32 + 1;
            int h5 = i7 + ((h4 << 5) | (h4 >>> 27)) + h(i4, i33, i6) + this.X[i32] + Y4;
            int i35 = (i4 << 30) | (i4 >>> 2);
            int i36 = i34 + 1;
            int h6 = i6 + ((h5 << 5) | (h5 >>> 27)) + h(h4, i35, i33) + this.X[i34] + Y4;
            i8 = (h4 << 30) | (h4 >>> 2);
            int i37 = i36 + 1;
            i5 = i33 + ((h6 << 5) | (h6 >>> 27)) + h(h5, i8, i35) + this.X[i36] + Y4;
            i7 = (h5 << 30) | (h5 >>> 2);
            i4 = i35 + ((i5 << 5) | (i5 >>> 27)) + h(h6, i7, i8) + this.X[i37] + Y4;
            i6 = (h6 << 30) | (h6 >>> 2);
            i31++;
            i = i37 + 1;
        }
        this.H1 += i4;
        this.H2 += i5;
        this.H3 += i6;
        this.H4 += i7;
        this.H5 += i8;
        this.xOff = 0;
        for (int i38 = 0; i38 < 16; i38++) {
            this.X[i38] = 0;
        }
    }

    /* access modifiers changed from: protected */
    @Override // com.verizon.vcast.apps.VzwGenDigest
    public void processLength(long j) {
        if (this.xOff > 14) {
            processBlock();
        }
        this.X[14] = (int) (j >>> 32);
        this.X[15] = (int) (-1 & j);
    }

    /* access modifiers changed from: protected */
    @Override // com.verizon.vcast.apps.VzwGenDigest
    public void processWord(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = i2 + 1;
        this.X[this.xOff] = (bArr[i] << 24) | ((bArr[i2] & 255) << 16) | ((bArr[i3] & 255) << 8) | (bArr[i3 + 1] & 255);
        int i4 = this.xOff + 1;
        this.xOff = i4;
        if (i4 == 16) {
            processBlock();
        }
    }

    @Override // com.verizon.vcast.apps.VzwGenDigest, com.verizon.vcast.apps.VzwDigest
    public void reset() {
        super.reset();
        this.H1 = 1732584193;
        this.H2 = -271733879;
        this.H3 = -1732584194;
        this.H4 = 271733878;
        this.H5 = -1009589776;
        this.xOff = 0;
        for (int i = 0; i != this.X.length; i++) {
            this.X[i] = 0;
        }
    }
}
