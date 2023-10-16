package com.verizon.vcast.apps;

import androidx.core.view.MotionEventCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.ea.nimble.Global;
import com.google.android.gms.wallet.WalletConstants;
import java.util.Random;
import java.util.Stack;

/* access modifiers changed from: package-private */
public class VzwBigInt {
    private static final int BITS_PER_BYTE = 8;
    private static final int BYTES_PER_INT = 4;
    private static final long IMASK = 4294967295L;
    public static final VzwBigInt ONE = valueOf(1);
    private static final VzwBigInt THREE = valueOf(3);
    private static final VzwBigInt TWO = valueOf(2);
    private static final int[] ZERO_MAGNITUDE = new int[0];
    public static final VzwBigInt ZERO = new VzwBigInt(0, ZERO_MAGNITUDE);
    private static final byte[] bitCounts;
    private static final int[][] primeLists = {new int[]{3, 5, 7, 11, 13, 17, 19, 23}, new int[]{29, 31, 37, 41, 43}, new int[]{47, 53, 59, 61, 67}, new int[]{71, 73, 79, 83}, new int[]{89, 97, 101, 103}, new int[]{107, 109, 113, 3}, new int[]{131, 137, 139, 149}, new int[]{151, 157, 163, 167}, new int[]{173, 179, 181, 191}, new int[]{193, 197, 199, 211}, new int[]{223, 227, 229}, new int[]{233, 239, 241}, new int[]{251, 257, 263}, new int[]{269, 271, 277}, new int[]{281, 283, 293}, new int[]{307, 311, 313}, new int[]{317, 331, 337}, new int[]{347, 349, 353}, new int[]{359, 367, 373}, new int[]{379, 383, 389}, new int[]{397, 401, WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR}, new int[]{419, 421, 431}, new int[]{433, 439, 443}, new int[]{449, 457, 461}, new int[]{463, 467, 479}, new int[]{487, 491, 499}, new int[]{503, 509, 521}, new int[]{523, 541, 547}, new int[]{557, 563, 569}, new int[]{571, 577, 587}, new int[]{593, 599, 601}, new int[]{607, 613, 617}, new int[]{619, 631, 641}, new int[]{643, 647, 653}, new int[]{659, 661, 673}, new int[]{677, 683, 691}, new int[]{701, 709, 719}, new int[]{727, 733, 739}, new int[]{743, 751, 757}, new int[]{761, 769, 773}, new int[]{787, 797, 809}, new int[]{811, 821, 823}, new int[]{827, 829, 839}, new int[]{853, 857, 859}, new int[]{863, 877, 881}, new int[]{883, 887, 907}, new int[]{911, 919, 929}, new int[]{937, 941, 947}, new int[]{953, 967, 971}, new int[]{977, 983, 991}, new int[]{997, 1009, 1013}, new int[]{1019, 1021, 1031}};
    private static int[] primeProducts = new int[primeLists.length];
    private static final byte[] rndMask = {-1, Byte.MAX_VALUE, 63, 31, 15, 7, 3, 1};
    private long mQuote;
    private int[] magnitude;
    private int nBitLength;
    private int nBits;
    private int sign;

    static {
        int[] iArr;
        ZERO.nBits = 0;
        ZERO.nBitLength = 0;
        ONE.nBits = 1;
        ONE.nBitLength = 1;
        TWO.nBits = 1;
        TWO.nBitLength = 2;
        for (int i = 0; i < primeLists.length; i++) {
            int i2 = 1;
            for (int i3 : primeLists[i]) {
                i2 *= i3;
            }
            primeProducts[i] = i2;
        }
        byte[] bArr = new byte[256];
        bArr[1] = 1;
        bArr[2] = 1;
        bArr[3] = 2;
        bArr[4] = 1;
        bArr[5] = 2;
        bArr[6] = 2;
        bArr[7] = 3;
        bArr[8] = 1;
        bArr[9] = 2;
        bArr[10] = 2;
        bArr[11] = 3;
        bArr[12] = 2;
        bArr[13] = 3;
        bArr[14] = 3;
        bArr[15] = 4;
        bArr[16] = 1;
        bArr[17] = 2;
        bArr[18] = 2;
        bArr[19] = 3;
        bArr[20] = 2;
        bArr[21] = 3;
        bArr[22] = 3;
        bArr[23] = 4;
        bArr[24] = 2;
        bArr[25] = 3;
        bArr[26] = 3;
        bArr[27] = 4;
        bArr[28] = 3;
        bArr[29] = 4;
        bArr[30] = 4;
        bArr[31] = 5;
        bArr[32] = 1;
        bArr[33] = 2;
        bArr[34] = 2;
        bArr[35] = 3;
        bArr[36] = 2;
        bArr[37] = 3;
        bArr[38] = 3;
        bArr[39] = 4;
        bArr[40] = 2;
        bArr[41] = 3;
        bArr[42] = 3;
        bArr[43] = 4;
        bArr[44] = 3;
        bArr[45] = 4;
        bArr[46] = 4;
        bArr[47] = 5;
        bArr[48] = 2;
        bArr[49] = 3;
        bArr[50] = 3;
        bArr[51] = 4;
        bArr[52] = 3;
        bArr[53] = 4;
        bArr[54] = 4;
        bArr[55] = 5;
        bArr[56] = 3;
        bArr[57] = 4;
        bArr[58] = 4;
        bArr[59] = 5;
        bArr[60] = 4;
        bArr[61] = 5;
        bArr[62] = 5;
        bArr[63] = 6;
        bArr[64] = 1;
        bArr[65] = 2;
        bArr[66] = 2;
        bArr[67] = 3;
        bArr[68] = 2;
        bArr[69] = 3;
        bArr[70] = 3;
        bArr[71] = 4;
        bArr[72] = 2;
        bArr[73] = 3;
        bArr[74] = 3;
        bArr[75] = 4;
        bArr[76] = 3;
        bArr[77] = 4;
        bArr[78] = 4;
        bArr[79] = 5;
        bArr[80] = 2;
        bArr[81] = 3;
        bArr[82] = 3;
        bArr[83] = 4;
        bArr[84] = 3;
        bArr[85] = 4;
        bArr[86] = 4;
        bArr[87] = 5;
        bArr[88] = 3;
        bArr[89] = 4;
        bArr[90] = 4;
        bArr[91] = 5;
        bArr[92] = 4;
        bArr[93] = 5;
        bArr[94] = 5;
        bArr[95] = 6;
        bArr[96] = 2;
        bArr[97] = 3;
        bArr[98] = 3;
        bArr[99] = 4;
        bArr[100] = 3;
        bArr[101] = 4;
        bArr[102] = 4;
        bArr[103] = 5;
        bArr[104] = 3;
        bArr[105] = 4;
        bArr[106] = 4;
        bArr[107] = 5;
        bArr[108] = 4;
        bArr[109] = 5;
        bArr[110] = 5;
        bArr[111] = 6;
        bArr[112] = 3;
        bArr[113] = 4;
        bArr[114] = 4;
        bArr[115] = 5;
        bArr[116] = 4;
        bArr[117] = 5;
        bArr[118] = 5;
        bArr[119] = 6;
        bArr[120] = 4;
        bArr[121] = 5;
        bArr[122] = 5;
        bArr[123] = 6;
        bArr[124] = 5;
        bArr[125] = 6;
        bArr[126] = 6;
        bArr[127] = 7;
        bArr[128] = 1;
        bArr[129] = 2;
        bArr[130] = 2;
        bArr[131] = 3;
        bArr[132] = 2;
        bArr[133] = 3;
        bArr[134] = 3;
        bArr[135] = 4;
        bArr[136] = 2;
        bArr[137] = 3;
        bArr[138] = 3;
        bArr[139] = 4;
        bArr[140] = 3;
        bArr[141] = 4;
        bArr[142] = 4;
        bArr[143] = 5;
        bArr[144] = 2;
        bArr[145] = 3;
        bArr[146] = 3;
        bArr[147] = 4;
        bArr[148] = 3;
        bArr[149] = 4;
        bArr[150] = 4;
        bArr[151] = 5;
        bArr[152] = 3;
        bArr[153] = 4;
        bArr[154] = 4;
        bArr[155] = 5;
        bArr[156] = 4;
        bArr[157] = 5;
        bArr[158] = 5;
        bArr[159] = 6;
        bArr[160] = 2;
        bArr[161] = 3;
        bArr[162] = 3;
        bArr[163] = 4;
        bArr[164] = 3;
        bArr[165] = 4;
        bArr[166] = 4;
        bArr[167] = 5;
        bArr[168] = 3;
        bArr[169] = 4;
        bArr[170] = 4;
        bArr[171] = 5;
        bArr[172] = 4;
        bArr[173] = 5;
        bArr[174] = 5;
        bArr[175] = 6;
        bArr[176] = 3;
        bArr[177] = 4;
        bArr[178] = 4;
        bArr[179] = 5;
        bArr[180] = 4;
        bArr[181] = 5;
        bArr[182] = 5;
        bArr[183] = 6;
        bArr[184] = 4;
        bArr[185] = 5;
        bArr[186] = 5;
        bArr[187] = 6;
        bArr[188] = 5;
        bArr[189] = 6;
        bArr[190] = 6;
        bArr[191] = 7;
        bArr[192] = 2;
        bArr[193] = 3;
        bArr[194] = 3;
        bArr[195] = 4;
        bArr[196] = 3;
        bArr[197] = 4;
        bArr[198] = 4;
        bArr[199] = 5;
        bArr[200] = 3;
        bArr[201] = 4;
        bArr[202] = 4;
        bArr[203] = 5;
        bArr[204] = 4;
        bArr[205] = 5;
        bArr[206] = 5;
        bArr[207] = 6;
        bArr[208] = 3;
        bArr[209] = 4;
        bArr[210] = 4;
        bArr[211] = 5;
        bArr[212] = 4;
        bArr[213] = 5;
        bArr[214] = 5;
        bArr[215] = 6;
        bArr[216] = 4;
        bArr[217] = 5;
        bArr[218] = 5;
        bArr[219] = 6;
        bArr[220] = 5;
        bArr[221] = 6;
        bArr[222] = 6;
        bArr[223] = 7;
        bArr[224] = 3;
        bArr[225] = 4;
        bArr[226] = 4;
        bArr[227] = 5;
        bArr[228] = 4;
        bArr[229] = 5;
        bArr[230] = 5;
        bArr[231] = 6;
        bArr[232] = 4;
        bArr[233] = 5;
        bArr[234] = 5;
        bArr[235] = 6;
        bArr[236] = 5;
        bArr[237] = 6;
        bArr[238] = 6;
        bArr[239] = 7;
        bArr[240] = 4;
        bArr[241] = 5;
        bArr[242] = 5;
        bArr[243] = 6;
        bArr[244] = 5;
        bArr[245] = 6;
        bArr[246] = 6;
        bArr[247] = 7;
        bArr[248] = 5;
        bArr[249] = 6;
        bArr[250] = 6;
        bArr[251] = 7;
        bArr[252] = 6;
        bArr[253] = 7;
        bArr[254] = 7;
        bArr[255] = 8;
        bitCounts = bArr;
    }

    private VzwBigInt() {
        this.nBits = -1;
        this.nBitLength = -1;
        this.mQuote = -1;
    }

    public VzwBigInt(int i, int i2, Random random) throws ArithmeticException {
        this.nBits = -1;
        this.nBitLength = -1;
        this.mQuote = -1;
        if (i < 2) {
            throw new ArithmeticException("bitLength < 2");
        }
        this.sign = 1;
        this.nBitLength = i;
        if (i == 2) {
            this.magnitude = random.nextInt() < 0 ? TWO.magnitude : THREE.magnitude;
            return;
        }
        int i3 = (i + 7) / 8;
        int i4 = (i3 * 8) - i;
        byte b = rndMask[i4];
        byte[] bArr = new byte[i3];
        while (true) {
            nextRndBytes(random, bArr);
            bArr[0] = (byte) (bArr[0] & b);
            bArr[0] = (byte) (bArr[0] | ((byte) (1 << (7 - i4))));
            int i5 = i3 - 1;
            bArr[i5] = (byte) (bArr[i5] | 1);
            this.magnitude = makeMagnitude(bArr, 1);
            this.nBits = -1;
            this.mQuote = -1;
            if (i2 < 1 || isProbablePrime(i2)) {
                return;
            }
            if (i > 32) {
                for (int i6 = 0; i6 < 10000; i6++) {
                    int nextInt = ((random.nextInt() >>> 1) % (i - 2)) + 33;
                    int[] iArr = this.magnitude;
                    int length = this.magnitude.length - (nextInt >>> 5);
                    iArr[length] = iArr[length] ^ (1 << (nextInt & 31));
                    int[] iArr2 = this.magnitude;
                    int length2 = this.magnitude.length - 1;
                    iArr2[length2] = iArr2[length2] ^ (random.nextInt() << 1);
                    this.mQuote = -1;
                    if (isProbablePrime(i2)) {
                        return;
                    }
                }
                continue;
            }
        }
    }

    public VzwBigInt(int i, Random random) throws IllegalArgumentException {
        int i2 = 0;
        this.nBits = -1;
        this.nBitLength = -1;
        this.mQuote = -1;
        if (i < 0) {
            throw new IllegalArgumentException("numBits must be non-negative");
        }
        this.nBits = -1;
        this.nBitLength = -1;
        if (i == 0) {
            this.magnitude = ZERO_MAGNITUDE;
            return;
        }
        int i3 = (i + 7) / 8;
        byte[] bArr = new byte[i3];
        nextRndBytes(random, bArr);
        bArr[0] = (byte) (bArr[0] & rndMask[(i3 * 8) - i]);
        this.magnitude = makeMagnitude(bArr, 1);
        this.sign = this.magnitude.length >= 1 ? 1 : i2;
    }

    public VzwBigInt(int i, byte[] bArr) throws NumberFormatException {
        this.nBits = -1;
        this.nBitLength = -1;
        this.mQuote = -1;
        if (i < -1 || i > 1) {
            throw new NumberFormatException("Invalid sign value");
        } else if (i == 0) {
            this.sign = 0;
            this.magnitude = new int[0];
        } else {
            this.magnitude = makeMagnitude(bArr, 1);
            this.sign = i;
        }
    }

    private VzwBigInt(int i, int[] iArr) {
        this.nBits = -1;
        this.nBitLength = -1;
        this.mQuote = -1;
        if (iArr.length > 0) {
            this.sign = i;
            int i2 = 0;
            while (i2 < iArr.length && iArr[i2] == 0) {
                i2++;
            }
            if (i2 == 0) {
                this.magnitude = iArr;
                return;
            }
            int[] iArr2 = new int[(iArr.length - i2)];
            System.arraycopy(iArr, i2, iArr2, 0, iArr2.length);
            this.magnitude = iArr2;
            if (iArr2.length == 0) {
                this.sign = 0;
                return;
            }
            return;
        }
        this.magnitude = iArr;
        this.sign = 0;
    }

    public VzwBigInt(String str) throws NumberFormatException {
        this(str, 10);
    }

    public VzwBigInt(String str, int i) throws NumberFormatException {
        this.nBits = -1;
        this.nBitLength = -1;
        this.mQuote = -1;
        if (str.length() == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        } else if (i < 2 || i > 36) {
            throw new NumberFormatException("Radix out of range");
        } else {
            int i2 = 0;
            this.sign = 1;
            if (str.charAt(0) == '-') {
                if (str.length() == 1) {
                    throw new NumberFormatException("Zero length BigInteger");
                }
                this.sign = -1;
                i2 = 1;
            }
            while (i2 < str.length() && Character.digit(str.charAt(i2), i) == 0) {
                i2++;
            }
            if (i2 >= str.length()) {
                this.sign = 0;
                this.magnitude = new int[0];
                return;
            }
            VzwBigInt vzwBigInt = ZERO;
            VzwBigInt valueOf = valueOf((long) i);
            while (i2 < str.length()) {
                vzwBigInt = vzwBigInt.multiply(valueOf).add(valueOf((long) Character.digit(str.charAt(i2), i)));
                i2++;
            }
            this.magnitude = vzwBigInt.magnitude;
        }
    }

    public VzwBigInt(byte[] bArr) throws NumberFormatException {
        this.nBits = -1;
        this.nBitLength = -1;
        this.mQuote = -1;
        if (bArr.length == 0) {
            throw new NumberFormatException("Zero length BigInteger");
        }
        this.sign = 1;
        if (bArr[0] < 0) {
            this.sign = -1;
        }
        this.magnitude = makeMagnitude(bArr, this.sign);
        if (this.magnitude.length == 0) {
            this.sign = 0;
        }
    }

    private long _extEuclid(long j, long j2, long[] jArr) {
        long j3 = 1;
        long j4 = j;
        long j5 = 0;
        long j6 = j2;
        while (j6 > 0) {
            long j7 = j4 / j6;
            long j8 = j3 - (j5 * j7);
            j3 = j5;
            j5 = j8;
            long j9 = j4 - (j6 * j7);
            j4 = j6;
            j6 = j9;
        }
        jArr[0] = j3;
        jArr[1] = (j4 - (j3 * j)) / j2;
        return j4;
    }

    private long _modInverse(long j, long j2) throws ArithmeticException {
        if (j2 < 0) {
            throw new ArithmeticException("Modulus must be positive");
        }
        long[] jArr = new long[2];
        if (_extEuclid(j, j2, jArr) != 1) {
            throw new ArithmeticException("Numbers not relatively prime.");
        }
        if (jArr[0] < 0) {
            jArr[0] = jArr[0] + j2;
        }
        return jArr[0];
    }

    private int[] add(int[] iArr, int[] iArr2) {
        int length = iArr.length - 1;
        long j = 0;
        int length2 = iArr2.length - 1;
        int i = length;
        while (length2 >= 0) {
            long j2 = j + (((long) iArr[i]) & IMASK) + (((long) iArr2[length2]) & IMASK);
            iArr[i] = (int) j2;
            j = j2 >>> 32;
            length2--;
            i--;
        }
        while (i >= 0 && j != 0) {
            long j3 = j + (((long) iArr[i]) & IMASK);
            iArr[i] = (int) j3;
            j = j3 >>> 32;
            i--;
        }
        return iArr;
    }

    private VzwBigInt addToMagnitude(int[] iArr) {
        int[] iArr2;
        int[] iArr3;
        int i = 1;
        if (this.magnitude.length < iArr.length) {
            iArr2 = iArr;
            iArr3 = this.magnitude;
        } else {
            iArr2 = this.magnitude;
            iArr3 = iArr;
        }
        int i2 = Integer.MAX_VALUE;
        if (iArr2.length == iArr3.length) {
            i2 = Integer.MAX_VALUE - iArr3[0];
        }
        if (!((iArr2[0] ^ ExploreByTouchHelper.INVALID_ID) >= i2)) {
            i = 0;
        }
        int[] iArr4 = new int[(iArr2.length + i)];
        System.arraycopy(iArr2, 0, iArr4, i, iArr2.length);
        return new VzwBigInt(this.sign, add(iArr4, iArr3));
    }

    static int bitLen(int i) {
        if (i >= 32768) {
            return i < 8388608 ? i < 524288 ? i < 131072 ? i < 65536 ? 16 : 17 : i < 262144 ? 18 : 19 : i < 2097152 ? i < 1048576 ? 20 : 21 : i < 4194304 ? 22 : 23 : i < 134217728 ? i < 33554432 ? i < 16777216 ? 24 : 25 : i < 67108864 ? 26 : 27 : i < 536870912 ? i < 268435456 ? 28 : 29 : i < 1073741824 ? 30 : 31;
        }
        if (i >= 128) {
            return i < 2048 ? i < 512 ? i < 256 ? 8 : 9 : i < 1024 ? 10 : 11 : i < 8192 ? i < 4096 ? 12 : 13 : i < 16384 ? 14 : 15;
        }
        if (i >= 8) {
            return i < 32 ? i < 16 ? 4 : 5 : i < 64 ? 6 : 7;
        }
        if (i >= 2) {
            return i < 4 ? 2 : 3;
        }
        if (i < 1) {
            return i < 0 ? 32 : 0;
        }
        return 1;
    }

    private int bitLength(int i, int[] iArr) {
        int i2 = 1;
        if (iArr.length == 0) {
            return 0;
        }
        while (i != iArr.length && iArr[i] == 0) {
            i++;
        }
        if (i == iArr.length) {
            return 0;
        }
        int length = (((iArr.length - i) - 1) * 32) + bitLen(iArr[i]);
        if (this.sign < 0) {
            boolean z = ((bitCounts[iArr[i] & MotionEventCompat.ACTION_MASK] + bitCounts[(iArr[i] >> 8) & MotionEventCompat.ACTION_MASK]) + bitCounts[(iArr[i] >> 16) & MotionEventCompat.ACTION_MASK]) + bitCounts[(iArr[i] >> 24) & MotionEventCompat.ACTION_MASK] == 1;
            for (int i3 = i + 1; i3 < iArr.length && z; i3++) {
                z = iArr[i3] == 0;
            }
            if (!z) {
                i2 = 0;
            }
            length -= i2;
        }
        return length;
    }

    private int compareNoLeadingZeroes(int i, int[] iArr, int i2, int[] iArr2) {
        int i3 = -1;
        int length = (iArr.length - iArr2.length) - (i - i2);
        if (length != 0) {
            return length < 0 ? -1 : 1;
        }
        while (i < iArr.length) {
            int i4 = i + 1;
            int i5 = iArr[i];
            int i6 = i2 + 1;
            int i7 = iArr2[i2];
            if (i5 != i7) {
                if ((i5 ^ ExploreByTouchHelper.INVALID_ID) >= (i7 ^ ExploreByTouchHelper.INVALID_ID)) {
                    i3 = 1;
                }
                return i3;
            }
            i2 = i6;
            i = i4;
        }
        return 0;
    }

    private int compareTo(int i, int[] iArr, int i2, int[] iArr2) {
        while (i != iArr.length && iArr[i] == 0) {
            i++;
        }
        while (i2 != iArr2.length && iArr2[i2] == 0) {
            i2++;
        }
        return compareNoLeadingZeroes(i, iArr, i2, iArr2);
    }

    private int[] divide(int[] iArr, int[] iArr2) {
        int[] iArr3;
        int[] iArr4;
        int compareTo;
        int compareTo2 = compareTo(0, iArr, 0, iArr2);
        if (compareTo2 > 0) {
            int bitLength = bitLength(0, iArr) - bitLength(0, iArr2);
            if (bitLength > 1) {
                iArr3 = shiftLeft(iArr2, bitLength - 1);
                iArr4 = shiftLeft(ONE.magnitude, bitLength - 1);
                if (bitLength % 32 == 0) {
                    int[] iArr5 = new int[((bitLength / 32) + 1)];
                    System.arraycopy(iArr4, 0, iArr5, 1, iArr5.length - 1);
                    iArr5[0] = 0;
                    iArr4 = iArr5;
                }
            } else {
                iArr3 = new int[iArr.length];
                System.arraycopy(iArr2, 0, iArr3, iArr3.length - iArr2.length, iArr2.length);
                iArr4 = new int[]{1};
            }
            int[] iArr6 = new int[iArr4.length];
            subtract(0, iArr, 0, iArr3);
            System.arraycopy(iArr4, 0, iArr6, 0, iArr4.length);
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            while (true) {
                int compareTo3 = compareTo(i, iArr, i2, iArr3);
                while (compareTo3 >= 0) {
                    subtract(i, iArr, i2, iArr3);
                    add(iArr4, iArr6);
                    compareTo3 = compareTo(i, iArr, i2, iArr3);
                }
                compareTo = compareTo(i, iArr, 0, iArr2);
                if (compareTo <= 0) {
                    break;
                }
                if (iArr[i] == 0) {
                    i++;
                }
                int bitLength2 = bitLength(i2, iArr3) - bitLength(i, iArr);
                if (bitLength2 == 0) {
                    shiftRightOneInPlace(i2, iArr3);
                    shiftRightOneInPlace(i3, iArr6);
                } else {
                    shiftRightInPlace(i2, iArr3, bitLength2);
                    shiftRightInPlace(i3, iArr6, bitLength2);
                }
                if (iArr3[i2] == 0) {
                    i2++;
                }
                if (iArr6[i3] == 0) {
                    i3++;
                }
            }
            if (compareTo != 0) {
                return iArr4;
            }
            add(iArr4, ONE.magnitude);
            for (int i4 = i; i4 != iArr.length; i4++) {
                iArr[i4] = 0;
            }
            return iArr4;
        } else if (compareTo2 == 0) {
            return new int[]{1};
        } else {
            return new int[]{0};
        }
    }

    private static VzwBigInt extEuclid(VzwBigInt vzwBigInt, VzwBigInt vzwBigInt2, VzwBigInt vzwBigInt3, VzwBigInt vzwBigInt4) {
        VzwBigInt vzwBigInt5 = ONE;
        VzwBigInt vzwBigInt6 = vzwBigInt;
        VzwBigInt vzwBigInt7 = ZERO;
        VzwBigInt vzwBigInt8 = vzwBigInt2;
        while (vzwBigInt8.sign > 0) {
            VzwBigInt[] divideAndRemainder = vzwBigInt6.divideAndRemainder(vzwBigInt8);
            VzwBigInt subtract = vzwBigInt5.subtract(vzwBigInt7.multiply(divideAndRemainder[0]));
            vzwBigInt5 = vzwBigInt7;
            vzwBigInt7 = subtract;
            vzwBigInt6 = vzwBigInt8;
            vzwBigInt8 = divideAndRemainder[1];
        }
        if (vzwBigInt3 != null) {
            vzwBigInt3.sign = vzwBigInt5.sign;
            vzwBigInt3.magnitude = vzwBigInt5.magnitude;
        }
        if (vzwBigInt4 != null) {
            VzwBigInt divide = vzwBigInt6.subtract(vzwBigInt5.multiply(vzwBigInt)).divide(vzwBigInt2);
            vzwBigInt4.sign = divide.sign;
            vzwBigInt4.magnitude = divide.magnitude;
        }
        return vzwBigInt6;
    }

    private VzwBigInt flipExistingBit(int i) {
        int[] iArr = new int[this.magnitude.length];
        System.arraycopy(this.magnitude, 0, iArr, 0, iArr.length);
        int length = (iArr.length - 1) - (i >>> 5);
        iArr[length] = iArr[length] ^ (1 << (i & 31));
        return new VzwBigInt(this.sign, iArr);
    }

    private long getMQuote() {
        if (this.mQuote != -1) {
            return this.mQuote;
        }
        if ((this.magnitude[this.magnitude.length - 1] & 1) == 0) {
            return -1;
        }
        this.mQuote = _modInverse(((long) ((this.magnitude[this.magnitude.length - 1] ^ -1) | 1)) & IMASK, 4294967296L);
        return this.mQuote;
    }

    private int[] inc(int[] iArr) {
        int length = iArr.length - 1;
        long j = (((long) iArr[length]) & IMASK) + 1;
        iArr[length] = (int) j;
        long j2 = j >>> 32;
        for (int i = length - 1; i >= 0 && j2 != 0; i--) {
            long j3 = j2 + (((long) iArr[i]) & IMASK);
            iArr[i] = (int) j3;
            j2 = j3 >>> 32;
        }
        return iArr;
    }

    private int[] lastNBits(int i) {
        if (i < 1) {
            return ZERO_MAGNITUDE;
        }
        int min = Math.min((i + 31) / 32, this.magnitude.length);
        int[] iArr = new int[min];
        System.arraycopy(this.magnitude, this.magnitude.length - min, iArr, 0, min);
        int i2 = i % 32;
        if (i2 == 0) {
            return iArr;
        }
        iArr[0] = iArr[0] & ((-1 << i2) ^ -1);
        return iArr;
    }

    private int[] makeMagnitude(byte[] bArr, int i) {
        if (i >= 0) {
            int i2 = 0;
            while (i2 < bArr.length && bArr[i2] == 0) {
                i2++;
            }
            if (i2 >= bArr.length) {
                return new int[0];
            }
            int length = ((bArr.length - i2) + 3) / 4;
            int length2 = (bArr.length - i2) % 4;
            if (length2 == 0) {
                length2 = 4;
            }
            int[] iArr = new int[length];
            int i3 = 0;
            int i4 = 0;
            for (int i5 = i2; i5 < bArr.length; i5++) {
                i3 = (i3 << 8) | (bArr[i5] & 255);
                length2--;
                if (length2 <= 0) {
                    iArr[i4] = i3;
                    i4++;
                    length2 = 4;
                    i3 = 0;
                }
            }
            return iArr;
        }
        int i6 = 0;
        while (i6 < bArr.length - 1 && bArr[i6] == 255) {
            i6++;
        }
        int length3 = bArr.length;
        boolean z = false;
        if (bArr[i6] == 128) {
            int i7 = i6 + 1;
            while (i7 < bArr.length && bArr[i7] == 0) {
                i7++;
            }
            if (i7 == bArr.length) {
                length3++;
                z = true;
            }
        }
        int i8 = ((length3 - i6) + 3) / 4;
        int i9 = (length3 - i6) % 4;
        if (i9 == 0) {
            i9 = 4;
        }
        int[] iArr2 = new int[i8];
        int i10 = 0;
        int i11 = 0;
        if (z && i9 - 1 <= 0) {
            i11 = 0 + 1;
            i9 = 4;
        }
        for (int i12 = i6; i12 < bArr.length; i12++) {
            i10 = (i10 << 8) | ((bArr[i12] ^ -1) & MotionEventCompat.ACTION_MASK);
            i9--;
            if (i9 <= 0) {
                iArr2[i11] = i10;
                i11++;
                i9 = 4;
                i10 = 0;
            }
        }
        int[] inc = inc(iArr2);
        if (inc[0] != 0) {
            return inc;
        }
        int[] iArr3 = new int[(inc.length - 1)];
        System.arraycopy(inc, 1, iArr3, 0, iArr3.length);
        return iArr3;
    }

    private int[] multiply(int[] iArr, int[] iArr2, int[] iArr3) {
        long j;
        int length = iArr3.length;
        if (length >= 1) {
            int length2 = iArr.length - iArr2.length;
            while (true) {
                length--;
                long j2 = ((long) iArr3[length]) & IMASK;
                j = 0;
                for (int length3 = iArr2.length - 1; length3 >= 0; length3--) {
                    long j3 = j + ((((long) iArr2[length3]) & IMASK) * j2) + (((long) iArr[length2 + length3]) & IMASK);
                    iArr[length2 + length3] = (int) j3;
                    j = j3 >>> 32;
                }
                length2--;
                if (length < 1) {
                    break;
                }
                iArr[length2] = (int) j;
            }
            if (length2 >= 0) {
                iArr[length2] = (int) j;
            }
        }
        return iArr;
    }

    private void multiplyMonty(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, long j) {
        int length = iArr4.length;
        int i = length - 1;
        long j2 = ((long) iArr3[length - 1]) & IMASK;
        for (int i2 = 0; i2 <= length; i2++) {
            iArr[i2] = 0;
        }
        for (int i3 = length; i3 > 0; i3--) {
            long j3 = ((long) iArr2[i3 - 1]) & IMASK;
            long j4 = ((((((long) iArr[length]) & IMASK) + ((j3 * j2) & IMASK)) & IMASK) * j) & IMASK;
            long j5 = j3 * j2;
            long j6 = j4 * (((long) iArr4[length - 1]) & IMASK);
            long j7 = (j5 >>> 32) + (j6 >>> 32) + ((((((long) iArr[length]) & IMASK) + (IMASK & j5)) + (IMASK & j6)) >>> 32);
            for (int i4 = i; i4 > 0; i4--) {
                long j8 = j3 * (((long) iArr3[i4 - 1]) & IMASK);
                long j9 = j4 * (((long) iArr4[i4 - 1]) & IMASK);
                long j10 = (((long) iArr[i4]) & IMASK) + (IMASK & j8) + (IMASK & j9) + (IMASK & j7);
                j7 = (j7 >>> 32) + (j8 >>> 32) + (j9 >>> 32) + (j10 >>> 32);
                iArr[i4 + 1] = (int) j10;
            }
            long j11 = j7 + (((long) iArr[0]) & IMASK);
            iArr[1] = (int) j11;
            iArr[0] = (int) (j11 >>> 32);
        }
        if (compareTo(0, iArr, 0, iArr4) >= 0) {
            subtract(0, iArr, 0, iArr4);
        }
        System.arraycopy(iArr, 1, iArr2, 0, length);
    }

    private void nextRndBytes(Random random, byte[] bArr) {
        int length = bArr.length;
        int i = 0;
        int i2 = 0;
        while (true) {
            int i3 = 0;
            while (i3 < 4) {
                if (i != length) {
                    i2 = i3 == 0 ? random.nextInt() : i2 >> 8;
                    i++;
                    bArr[i] = (byte) i2;
                    i3++;
                } else {
                    return;
                }
            }
            i = i;
        }
    }

    public static VzwBigInt probablePrime(int i, Random random) {
        return new VzwBigInt(i, 100, random);
    }

    private boolean quickPow2Check() {
        return this.sign > 0 && this.nBits == 1;
    }

    private int remainder(int i) {
        long j = 0;
        for (int i2 = 0; i2 < this.magnitude.length; i2++) {
            j = ((j << 32) | (((long) this.magnitude[i2]) & IMASK)) % ((long) i);
        }
        return (int) j;
    }

    private int[] remainder(int[] iArr, int[] iArr2) {
        int[] iArr3;
        int i = 0;
        while (i < iArr.length && iArr[i] == 0) {
            i++;
        }
        int i2 = 0;
        while (i2 < iArr2.length && iArr2[i2] == 0) {
            i2++;
        }
        int compareNoLeadingZeroes = compareNoLeadingZeroes(i, iArr, i2, iArr2);
        if (compareNoLeadingZeroes > 0) {
            int bitLength = bitLength(i2, iArr2);
            int bitLength2 = bitLength(i, iArr);
            int i3 = bitLength2 - bitLength;
            int i4 = 0;
            int i5 = bitLength;
            if (i3 > 0) {
                iArr3 = shiftLeft(iArr2, i3);
                i5 += i3;
            } else {
                int length = iArr2.length - i2;
                iArr3 = new int[length];
                System.arraycopy(iArr2, i2, iArr3, 0, length);
            }
            loop2:
            while (true) {
                if (i5 < bitLength2 || compareNoLeadingZeroes(i, iArr, i4, iArr3) >= 0) {
                    subtract(i, iArr, i4, iArr3);
                    while (true) {
                        if (iArr[i] == 0) {
                            i++;
                            if (i == iArr.length) {
                                break loop2;
                            }
                        } else {
                            compareNoLeadingZeroes = compareNoLeadingZeroes(i, iArr, i2, iArr2);
                            if (compareNoLeadingZeroes <= 0) {
                                break;
                            }
                            bitLength2 = (((iArr.length - i) - 1) * 32) + bitLen(iArr[i]);
                        }
                    }
                }
                int i6 = i5 - bitLength2;
                if (i6 < 2) {
                    shiftRightOneInPlace(i4, iArr3);
                    i5--;
                } else {
                    shiftRightInPlace(i4, iArr3, i6);
                    i5 -= i6;
                }
                while (iArr3[i4] == 0) {
                    i4++;
                }
            }
        }
        if (compareNoLeadingZeroes == 0) {
            for (int i7 = i; i7 < iArr.length; i7++) {
                iArr[i7] = 0;
            }
        }
        return iArr;
    }

    private int[] shiftLeft(int[] iArr, int i) {
        int[] iArr2;
        int i2 = i >>> 5;
        int i3 = i & 31;
        int length = iArr.length;
        if (i3 == 0) {
            int[] iArr3 = new int[(length + i2)];
            System.arraycopy(iArr, 0, iArr3, 0, length);
            return iArr3;
        }
        int i4 = 0;
        int i5 = 32 - i3;
        int i6 = iArr[0] >>> i5;
        if (i6 != 0) {
            iArr2 = new int[(length + i2 + 1)];
            iArr2[0] = i6;
            i4 = 0 + 1;
        } else {
            iArr2 = new int[(length + i2)];
        }
        int i7 = iArr[0];
        for (int i8 = 0; i8 < length - 1; i8++) {
            int i9 = iArr[i8 + 1];
            i4++;
            iArr2[i4] = (i7 << i3) | (i9 >>> i5);
            i7 = i9;
        }
        iArr2[i4] = iArr[length - 1] << i3;
        return iArr2;
    }

    private static void shiftRightInPlace(int i, int[] iArr, int i2) {
        int i3 = (i2 >>> 5) + i;
        int i4 = i2 & 31;
        int length = iArr.length - 1;
        if (i3 != i) {
            int i5 = i3 - i;
            for (int i6 = length; i6 >= i3; i6--) {
                iArr[i6] = iArr[i6 - i5];
            }
            for (int i7 = i3 - 1; i7 >= i; i7--) {
                iArr[i7] = 0;
            }
        }
        if (i4 != 0) {
            int i8 = 32 - i4;
            int i9 = iArr[length];
            for (int i10 = length; i10 >= i3 + 1; i10--) {
                int i11 = iArr[i10 - 1];
                iArr[i10] = (i9 >>> i4) | (i11 << i8);
                i9 = i11;
            }
            iArr[i3] = iArr[i3] >>> i4;
        }
    }

    private static void shiftRightOneInPlace(int i, int[] iArr) {
        int length = iArr.length - 1;
        int i2 = iArr[length];
        for (int i3 = length; i3 > i; i3--) {
            int i4 = iArr[i3 - 1];
            iArr[i3] = (i2 >>> 1) | (i4 << 31);
            i2 = i4;
        }
        iArr[i] = iArr[i] >>> 1;
    }

    private int[] square(int[] iArr, int[] iArr2) {
        int length = iArr.length - 1;
        for (int length2 = iArr2.length - 1; length2 != 0; length2--) {
            long j = ((long) iArr2[length2]) & IMASK;
            long j2 = j * j;
            long j3 = j2 >>> 32;
            long j4 = (j2 & IMASK) + (((long) iArr[length]) & IMASK);
            iArr[length] = (int) j4;
            long j5 = j3 + (j4 >> 32);
            for (int i = length2 - 1; i >= 0; i--) {
                length--;
                long j6 = (((long) iArr2[i]) & IMASK) * j;
                long j7 = j6 >>> 31;
                long j8 = ((2147483647L & j6) << 1) + (((long) iArr[length]) & IMASK) + j5;
                iArr[length] = (int) j8;
                j5 = j7 + (j8 >>> 32);
            }
            int i2 = length - 1;
            long j9 = j5 + (((long) iArr[i2]) & IMASK);
            iArr[i2] = (int) j9;
            int i3 = i2 - 1;
            if (i3 >= 0) {
                iArr[i3] = (int) (j9 >> 32);
            }
            length = i3 + length2;
        }
        long j10 = ((long) iArr2[0]) & IMASK;
        long j11 = j10 * j10;
        long j12 = j11 >>> 32;
        long j13 = (j11 & IMASK) + (((long) iArr[length]) & IMASK);
        iArr[length] = (int) j13;
        int i4 = length - 1;
        if (i4 >= 0) {
            iArr[i4] = (int) ((j13 >> 32) + j12 + ((long) iArr[i4]));
        }
        return iArr;
    }

    private int[] subtract(int i, int[] iArr, int i2, int[] iArr2) {
        int i3;
        int length = iArr.length;
        int length2 = iArr2.length;
        int i4 = 0;
        do {
            length--;
            length2--;
            long j = ((((long) iArr[length]) & IMASK) - (((long) iArr2[length2]) & IMASK)) + ((long) i4);
            iArr[length] = (int) j;
            i4 = (int) (j >> 63);
        } while (length2 > i2);
        if (i4 != 0) {
            do {
                length--;
                i3 = iArr[length] - 1;
                iArr[length] = i3;
            } while (i3 == -1);
        }
        return iArr;
    }

    public static VzwBigInt valueOf(long j) {
        if (j == 0) {
            return ZERO;
        }
        if (j < 0) {
            return j == Long.MIN_VALUE ? valueOf(-1 ^ j).not() : valueOf(-j).negate();
        }
        byte[] bArr = new byte[8];
        for (int i = 0; i < 8; i++) {
            bArr[7 - i] = (byte) ((int) j);
            j >>= 8;
        }
        return new VzwBigInt(bArr);
    }

    private void zero(int[] iArr) {
        for (int i = 0; i != iArr.length; i++) {
            iArr[i] = 0;
        }
    }

    public VzwBigInt abs() {
        return this.sign >= 0 ? this : negate();
    }

    public VzwBigInt add(VzwBigInt vzwBigInt) throws ArithmeticException {
        if (vzwBigInt.sign == 0 || vzwBigInt.magnitude.length == 0) {
            return this;
        }
        if (this.sign == 0 || this.magnitude.length == 0) {
            return vzwBigInt;
        }
        if (vzwBigInt.sign < 0) {
            if (this.sign > 0) {
                return subtract(vzwBigInt.negate());
            }
        } else if (this.sign < 0) {
            return vzwBigInt.subtract(negate());
        }
        return addToMagnitude(vzwBigInt.magnitude);
    }

    public VzwBigInt and(VzwBigInt vzwBigInt) {
        if (this.sign == 0 || vzwBigInt.sign == 0) {
            return ZERO;
        }
        int[] iArr = this.sign > 0 ? this.magnitude : add(ONE).magnitude;
        int[] iArr2 = vzwBigInt.sign > 0 ? vzwBigInt.magnitude : vzwBigInt.add(ONE).magnitude;
        boolean z = this.sign < 0 && vzwBigInt.sign < 0;
        int[] iArr3 = new int[Math.max(iArr.length, iArr2.length)];
        int length = iArr3.length - iArr.length;
        int length2 = iArr3.length - iArr2.length;
        int i = 0;
        while (i < iArr3.length) {
            int i2 = i >= length ? iArr[i - length] : 0;
            int i3 = i >= length2 ? iArr2[i - length2] : 0;
            if (this.sign < 0) {
                i2 ^= -1;
            }
            if (vzwBigInt.sign < 0) {
                i3 ^= -1;
            }
            iArr3[i] = i2 & i3;
            if (z) {
                iArr3[i] = iArr3[i] ^ -1;
            }
            i++;
        }
        VzwBigInt vzwBigInt2 = new VzwBigInt(1, iArr3);
        return z ? vzwBigInt2.not() : vzwBigInt2;
    }

    public VzwBigInt andNot(VzwBigInt vzwBigInt) {
        return and(vzwBigInt.not());
    }

    public int bitCount() {
        if (this.nBits == -1) {
            if (this.sign < 0) {
                this.nBits = not().bitCount();
            } else {
                int i = 0;
                for (int i2 = 0; i2 < this.magnitude.length; i2++) {
                    i = i + bitCounts[this.magnitude[i2] & MotionEventCompat.ACTION_MASK] + bitCounts[(this.magnitude[i2] >> 8) & MotionEventCompat.ACTION_MASK] + bitCounts[(this.magnitude[i2] >> 16) & MotionEventCompat.ACTION_MASK] + bitCounts[(this.magnitude[i2] >> 24) & MotionEventCompat.ACTION_MASK];
                }
                this.nBits = i;
            }
        }
        return this.nBits;
    }

    public int bitLength() {
        if (this.nBitLength == -1) {
            if (this.sign == 0) {
                this.nBitLength = 0;
            } else {
                this.nBitLength = bitLength(0, this.magnitude);
            }
        }
        return this.nBitLength;
    }

    public byte byteValue() {
        return (byte) intValue();
    }

    public VzwBigInt clearBit(int i) throws ArithmeticException {
        if (i >= 0) {
            return !testBit(i) ? this : (this.sign <= 0 || i >= bitLength() + -1) ? andNot(ONE.shiftLeft(i)) : flipExistingBit(i);
        }
        throw new ArithmeticException("Bit address less than zero");
    }

    public int compareTo(VzwBigInt vzwBigInt) {
        if (this.sign < vzwBigInt.sign) {
            return -1;
        }
        if (this.sign > vzwBigInt.sign) {
            return 1;
        }
        if (this.sign == 0) {
            return 0;
        }
        return compareTo(0, this.magnitude, 0, vzwBigInt.magnitude) * this.sign;
    }

    public int compareTo(Object obj) {
        return compareTo((VzwBigInt) obj);
    }

    public VzwBigInt divide(VzwBigInt vzwBigInt) throws ArithmeticException {
        if (vzwBigInt.sign == 0) {
            throw new ArithmeticException("Divide by zero");
        } else if (this.sign == 0) {
            return ZERO;
        } else {
            if (vzwBigInt.compareTo(ONE) == 0) {
                return this;
            }
            int[] iArr = new int[this.magnitude.length];
            System.arraycopy(this.magnitude, 0, iArr, 0, iArr.length);
            return new VzwBigInt(this.sign * vzwBigInt.sign, divide(iArr, vzwBigInt.magnitude));
        }
    }

    public VzwBigInt[] divideAndRemainder(VzwBigInt vzwBigInt) throws ArithmeticException {
        if (vzwBigInt.sign == 0) {
            throw new ArithmeticException("Divide by zero");
        }
        VzwBigInt[] vzwBigIntArr = new VzwBigInt[2];
        if (this.sign == 0) {
            VzwBigInt vzwBigInt2 = ZERO;
            vzwBigIntArr[1] = vzwBigInt2;
            vzwBigIntArr[0] = vzwBigInt2;
        } else if (vzwBigInt.compareTo(ONE) == 0) {
            vzwBigIntArr[0] = this;
            vzwBigIntArr[1] = ZERO;
        } else {
            int[] iArr = new int[this.magnitude.length];
            System.arraycopy(this.magnitude, 0, iArr, 0, iArr.length);
            vzwBigIntArr[0] = new VzwBigInt(this.sign * vzwBigInt.sign, divide(iArr, vzwBigInt.magnitude));
            vzwBigIntArr[1] = new VzwBigInt(this.sign, iArr);
        }
        return vzwBigIntArr;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof VzwBigInt)) {
            return false;
        }
        VzwBigInt vzwBigInt = (VzwBigInt) obj;
        if (!(vzwBigInt.sign == this.sign && vzwBigInt.magnitude.length == this.magnitude.length)) {
            return false;
        }
        for (int i = 0; i < this.magnitude.length; i++) {
            if (vzwBigInt.magnitude[i] != this.magnitude[i]) {
                return false;
            }
        }
        return true;
    }

    public VzwBigInt flipBit(int i) throws ArithmeticException {
        if (i >= 0) {
            return (this.sign <= 0 || i >= bitLength() + -1) ? xor(ONE.shiftLeft(i)) : flipExistingBit(i);
        }
        throw new ArithmeticException("Bit address less than zero");
    }

    public VzwBigInt gcd(VzwBigInt vzwBigInt) {
        if (vzwBigInt.sign == 0) {
            return abs();
        }
        if (this.sign == 0) {
            return vzwBigInt.abs();
        }
        VzwBigInt vzwBigInt2 = this;
        VzwBigInt vzwBigInt3 = vzwBigInt;
        while (vzwBigInt3.sign != 0) {
            VzwBigInt mod = vzwBigInt2.mod(vzwBigInt3);
            vzwBigInt2 = vzwBigInt3;
            vzwBigInt3 = mod;
        }
        return vzwBigInt2;
    }

    public int getLowestSetBit() {
        if (this.sign == 0) {
            return -1;
        }
        int length = this.magnitude.length;
        do {
            length--;
            if (length <= 0) {
                break;
            }
        } while (this.magnitude[length] == 0);
        int i = this.magnitude[length];
        int i2 = (65535 & i) == 0 ? (16711680 & i) == 0 ? 7 : 15 : (i & MotionEventCompat.ACTION_MASK) == 0 ? 23 : 31;
        while (i2 > 0 && (i << i2) != Integer.MIN_VALUE) {
            i2--;
        }
        return ((this.magnitude.length - length) * 32) - (i2 + 1);
    }

    public int hashCode() {
        int length = this.magnitude.length;
        if (this.magnitude.length > 0) {
            length ^= this.magnitude[0];
            if (this.magnitude.length > 1) {
                length ^= this.magnitude[this.magnitude.length - 1];
            }
        }
        return this.sign < 0 ? length ^ -1 : length;
    }

    public int intValue() {
        if (this.magnitude.length == 0) {
            return 0;
        }
        return this.sign < 0 ? -this.magnitude[this.magnitude.length - 1] : this.magnitude[this.magnitude.length - 1];
    }

    public boolean isProbablePrime(int i) {
        if (i <= 0) {
            return true;
        }
        if (this.sign == 0) {
            return false;
        }
        VzwBigInt abs = abs();
        if (!abs.testBit(0)) {
            return abs.equals(TWO);
        }
        if (abs.equals(ONE)) {
            return false;
        }
        int min = Math.min(abs.bitLength() - 1, primeLists.length);
        for (int i2 = 0; i2 < min; i2++) {
            int remainder = abs.remainder(primeProducts[i2]);
            int[] iArr = primeLists[i2];
            for (int i3 = 0; i3 < iArr.length; i3++) {
                int i4 = iArr[i3];
                if (remainder % i4 == 0) {
                    return abs.bitLength() < 16 && abs.intValue() == i4;
                }
            }
        }
        VzwBigInt subtract = abs.subtract(ONE);
        int lowestSetBit = subtract.getLowestSetBit();
        VzwBigInt shiftRight = subtract.shiftRight(lowestSetBit);
        Random random = new Random();
        while (true) {
            VzwBigInt vzwBigInt = new VzwBigInt(abs.bitLength(), random);
            if (vzwBigInt.compareTo(ONE) > 0 && vzwBigInt.compareTo(subtract) < 0) {
                VzwBigInt modPow = vzwBigInt.modPow(shiftRight, abs);
                if (!modPow.equals(ONE)) {
                    int i5 = 0;
                    while (!modPow.equals(subtract)) {
                        i5++;
                        if (i5 == lowestSetBit) {
                            return false;
                        }
                        modPow = modPow.modPow(TWO, abs);
                        if (modPow.equals(ONE)) {
                            return false;
                        }
                    }
                }
                i -= 2;
                if (i <= 0) {
                    return true;
                }
            }
        }
    }

    public long longValue() {
        if (this.magnitude.length == 0) {
            return 0;
        }
        long j = this.magnitude.length > 1 ? (((long) this.magnitude[this.magnitude.length - 2]) << 32) | (((long) this.magnitude[this.magnitude.length - 1]) & IMASK) : ((long) this.magnitude[this.magnitude.length - 1]) & IMASK;
        return this.sign < 0 ? -j : j;
    }

    public VzwBigInt max(VzwBigInt vzwBigInt) {
        return compareTo(vzwBigInt) > 0 ? this : vzwBigInt;
    }

    public VzwBigInt min(VzwBigInt vzwBigInt) {
        return compareTo(vzwBigInt) < 0 ? this : vzwBigInt;
    }

    public VzwBigInt mod(VzwBigInt vzwBigInt) throws ArithmeticException {
        if (vzwBigInt.sign <= 0) {
            throw new ArithmeticException("BigInteger: modulus is not positive");
        }
        VzwBigInt remainder = remainder(vzwBigInt);
        return remainder.sign >= 0 ? remainder : remainder.add(vzwBigInt);
    }

    public VzwBigInt modInverse(VzwBigInt vzwBigInt) throws ArithmeticException {
        if (vzwBigInt.sign != 1) {
            throw new ArithmeticException("Modulus must be positive");
        }
        VzwBigInt vzwBigInt2 = new VzwBigInt();
        if (extEuclid(this, vzwBigInt, vzwBigInt2, null).equals(ONE)) {
            return vzwBigInt2.compareTo(ZERO) < 0 ? vzwBigInt2.add(vzwBigInt) : vzwBigInt2;
        }
        throw new ArithmeticException("Numbers not relatively prime.");
    }

    public VzwBigInt modPow(VzwBigInt vzwBigInt, VzwBigInt vzwBigInt2) throws ArithmeticException {
        if (vzwBigInt2.sign < 1) {
            throw new ArithmeticException("Modulus must be positive");
        } else if (vzwBigInt2.equals(ONE)) {
            return ZERO;
        } else {
            if (vzwBigInt.sign == 0) {
                return ONE;
            }
            if (this.sign == 0) {
                return ZERO;
            }
            int[] iArr = null;
            int[] iArr2 = null;
            boolean z = (vzwBigInt2.magnitude[vzwBigInt2.magnitude.length + -1] & 1) == 1;
            long j = 0;
            if (z) {
                j = vzwBigInt2.getMQuote();
                iArr = shiftLeft(vzwBigInt2.magnitude.length * 32).mod(vzwBigInt2).magnitude;
                z = iArr.length <= vzwBigInt2.magnitude.length;
                if (z) {
                    iArr2 = new int[(vzwBigInt2.magnitude.length + 1)];
                    if (iArr.length < vzwBigInt2.magnitude.length) {
                        int[] iArr3 = new int[vzwBigInt2.magnitude.length];
                        System.arraycopy(iArr, 0, iArr3, iArr3.length - iArr.length, iArr.length);
                        iArr = iArr3;
                    }
                }
            }
            if (!z) {
                if (this.magnitude.length <= vzwBigInt2.magnitude.length) {
                    iArr = new int[vzwBigInt2.magnitude.length];
                    System.arraycopy(this.magnitude, 0, iArr, iArr.length - this.magnitude.length, this.magnitude.length);
                } else {
                    VzwBigInt remainder = remainder(vzwBigInt2);
                    iArr = new int[vzwBigInt2.magnitude.length];
                    System.arraycopy(remainder.magnitude, 0, iArr, iArr.length - remainder.magnitude.length, remainder.magnitude.length);
                }
                iArr2 = new int[(vzwBigInt2.magnitude.length * 2)];
            }
            int[] iArr4 = new int[vzwBigInt2.magnitude.length];
            for (int i = 0; i < vzwBigInt.magnitude.length; i++) {
                int i2 = vzwBigInt.magnitude[i];
                int i3 = 0;
                if (i == 0) {
                    while (i2 > 0) {
                        i2 <<= 1;
                        i3++;
                    }
                    System.arraycopy(iArr, 0, iArr4, 0, iArr.length);
                    i2 <<= 1;
                    i3++;
                }
                while (i2 != 0) {
                    if (z) {
                        multiplyMonty(iArr2, iArr4, iArr4, vzwBigInt2.magnitude, j);
                    } else {
                        square(iArr2, iArr4);
                        remainder(iArr2, vzwBigInt2.magnitude);
                        System.arraycopy(iArr2, iArr2.length - iArr4.length, iArr4, 0, iArr4.length);
                        zero(iArr2);
                    }
                    i3++;
                    if (i2 < 0) {
                        if (z) {
                            multiplyMonty(iArr2, iArr4, iArr, vzwBigInt2.magnitude, j);
                        } else {
                            multiply(iArr2, iArr4, iArr);
                            remainder(iArr2, vzwBigInt2.magnitude);
                            System.arraycopy(iArr2, iArr2.length - iArr4.length, iArr4, 0, iArr4.length);
                            zero(iArr2);
                        }
                    }
                    i2 <<= 1;
                }
                while (i3 < 32) {
                    if (z) {
                        multiplyMonty(iArr2, iArr4, iArr4, vzwBigInt2.magnitude, j);
                    } else {
                        square(iArr2, iArr4);
                        remainder(iArr2, vzwBigInt2.magnitude);
                        System.arraycopy(iArr2, iArr2.length - iArr4.length, iArr4, 0, iArr4.length);
                        zero(iArr2);
                    }
                    i3++;
                }
            }
            if (z) {
                zero(iArr);
                iArr[iArr.length - 1] = 1;
                multiplyMonty(iArr2, iArr4, iArr, vzwBigInt2.magnitude, j);
            }
            VzwBigInt vzwBigInt3 = new VzwBigInt(1, iArr4);
            return vzwBigInt.sign <= 0 ? vzwBigInt3.modInverse(vzwBigInt2) : vzwBigInt3;
        }
    }

    public VzwBigInt multiply(VzwBigInt vzwBigInt) {
        if (this.sign == 0 || vzwBigInt.sign == 0) {
            return ZERO;
        }
        int[] iArr = new int[(((bitLength() + vzwBigInt.bitLength()) / 32) + 1)];
        if (vzwBigInt == this) {
            square(iArr, this.magnitude);
        } else {
            multiply(iArr, this.magnitude, vzwBigInt.magnitude);
        }
        return new VzwBigInt(this.sign * vzwBigInt.sign, iArr);
    }

    public VzwBigInt negate() {
        return this.sign == 0 ? this : new VzwBigInt(-this.sign, this.magnitude);
    }

    public VzwBigInt not() {
        return add(ONE).negate();
    }

    public VzwBigInt or(VzwBigInt vzwBigInt) {
        if (this.sign == 0) {
            return vzwBigInt;
        }
        if (vzwBigInt.sign == 0) {
            return this;
        }
        int[] iArr = this.sign > 0 ? this.magnitude : add(ONE).magnitude;
        int[] iArr2 = vzwBigInt.sign > 0 ? vzwBigInt.magnitude : vzwBigInt.add(ONE).magnitude;
        boolean z = this.sign < 0 || vzwBigInt.sign < 0;
        int[] iArr3 = new int[Math.max(iArr.length, iArr2.length)];
        int length = iArr3.length - iArr.length;
        int length2 = iArr3.length - iArr2.length;
        int i = 0;
        while (i < iArr3.length) {
            int i2 = i >= length ? iArr[i - length] : 0;
            int i3 = i >= length2 ? iArr2[i - length2] : 0;
            if (this.sign < 0) {
                i2 ^= -1;
            }
            if (vzwBigInt.sign < 0) {
                i3 ^= -1;
            }
            iArr3[i] = i2 | i3;
            if (z) {
                iArr3[i] = iArr3[i] ^ -1;
            }
            i++;
        }
        VzwBigInt vzwBigInt2 = new VzwBigInt(1, iArr3);
        if (z) {
            vzwBigInt2 = vzwBigInt2.not();
        }
        return vzwBigInt2;
    }

    public VzwBigInt pow(int i) throws ArithmeticException {
        if (i < 0) {
            throw new ArithmeticException("Negative exponent");
        } else if (this.sign == 0) {
            return i == 0 ? ONE : this;
        } else {
            VzwBigInt vzwBigInt = ONE;
            VzwBigInt vzwBigInt2 = this;
            while (i != 0) {
                if ((i & 1) == 1) {
                    vzwBigInt = vzwBigInt.multiply(vzwBigInt2);
                }
                i >>= 1;
                if (i != 0) {
                    vzwBigInt2 = vzwBigInt2.multiply(vzwBigInt2);
                }
            }
            return vzwBigInt;
        }
    }

    public VzwBigInt remainder(VzwBigInt vzwBigInt) throws ArithmeticException {
        int[] remainder;
        int i;
        if (vzwBigInt.sign == 0) {
            throw new ArithmeticException("BigInteger: Divide by zero");
        } else if (this.sign == 0) {
            return ZERO;
        } else {
            if (vzwBigInt.magnitude.length != 1 || (i = vzwBigInt.magnitude[0]) <= 0) {
                if (compareTo(0, this.magnitude, 0, vzwBigInt.magnitude) < 0) {
                    return this;
                }
                if (vzwBigInt.quickPow2Check()) {
                    remainder = lastNBits(vzwBigInt.abs().bitLength() - 1);
                } else {
                    int[] iArr = new int[this.magnitude.length];
                    System.arraycopy(this.magnitude, 0, iArr, 0, iArr.length);
                    remainder = remainder(iArr, vzwBigInt.magnitude);
                }
                return new VzwBigInt(this.sign, remainder);
            } else if (i == 1) {
                return ZERO;
            } else {
                int remainder2 = remainder(i);
                return remainder2 == 0 ? ZERO : new VzwBigInt(this.sign, new int[]{remainder2});
            }
        }
    }

    public VzwBigInt setBit(int i) throws ArithmeticException {
        if (i >= 0) {
            return testBit(i) ? this : (this.sign <= 0 || i >= bitLength() + -1) ? or(ONE.shiftLeft(i)) : flipExistingBit(i);
        }
        throw new ArithmeticException("Bit address less than zero");
    }

    public VzwBigInt shiftLeft(int i) {
        if (this.sign == 0 || this.magnitude.length == 0) {
            return ZERO;
        }
        if (i == 0) {
            return this;
        }
        if (i < 0) {
            return shiftRight(-i);
        }
        VzwBigInt vzwBigInt = new VzwBigInt(this.sign, shiftLeft(this.magnitude, i));
        if (this.nBits != -1) {
            vzwBigInt.nBits = this.sign > 0 ? this.nBits : this.nBits + i;
        }
        if (this.nBitLength != -1) {
            vzwBigInt.nBitLength = this.nBitLength + i;
        }
        return vzwBigInt;
    }

    public VzwBigInt shiftRight(int i) {
        if (i == 0) {
            return this;
        }
        if (i < 0) {
            return shiftLeft(-i);
        }
        if (i >= bitLength()) {
            return this.sign < 0 ? valueOf(-1) : ZERO;
        }
        int[] iArr = new int[this.magnitude.length];
        System.arraycopy(this.magnitude, 0, iArr, 0, iArr.length);
        shiftRightInPlace(0, iArr, i);
        return new VzwBigInt(this.sign, iArr);
    }

    public int signum() {
        return this.sign;
    }

    public VzwBigInt subtract(VzwBigInt vzwBigInt) {
        VzwBigInt vzwBigInt2;
        VzwBigInt vzwBigInt3;
        if (vzwBigInt.sign == 0 || vzwBigInt.magnitude.length == 0) {
            return this;
        }
        if (this.sign == 0 || this.magnitude.length == 0) {
            return vzwBigInt.negate();
        }
        if (this.sign != vzwBigInt.sign) {
            return add(vzwBigInt.negate());
        }
        int compareTo = compareTo(0, this.magnitude, 0, vzwBigInt.magnitude);
        if (compareTo == 0) {
            return ZERO;
        }
        if (compareTo < 0) {
            vzwBigInt2 = vzwBigInt;
            vzwBigInt3 = this;
        } else {
            vzwBigInt2 = this;
            vzwBigInt3 = vzwBigInt;
        }
        int[] iArr = new int[vzwBigInt2.magnitude.length];
        System.arraycopy(vzwBigInt2.magnitude, 0, iArr, 0, iArr.length);
        return new VzwBigInt(this.sign * compareTo, subtract(0, iArr, 0, vzwBigInt3.magnitude));
    }

    public boolean testBit(int i) throws ArithmeticException {
        if (i < 0) {
            throw new ArithmeticException("Bit position must not be negative");
        } else if (this.sign < 0) {
            return !not().testBit(i);
        } else {
            int i2 = i / 32;
            return i2 < this.magnitude.length && ((this.magnitude[(this.magnitude.length + -1) - i2] >> (i % 32)) & 1) > 0;
        }
    }

    public byte[] toByteArray() {
        if (this.sign == 0) {
            return new byte[1];
        }
        byte[] bArr = new byte[((bitLength() / 8) + 1)];
        int length = this.magnitude.length;
        int length2 = bArr.length;
        if (this.sign > 0) {
            while (length > 1) {
                length--;
                int i = this.magnitude[length];
                int i2 = length2 - 1;
                bArr[i2] = (byte) i;
                int i3 = i2 - 1;
                bArr[i3] = (byte) (i >>> 8);
                int i4 = i3 - 1;
                bArr[i4] = (byte) (i >>> 16);
                length2 = i4 - 1;
                bArr[length2] = (byte) (i >>> 24);
            }
            int i5 = this.magnitude[0];
            while ((i5 & -256) != 0) {
                length2--;
                bArr[length2] = (byte) i5;
                i5 >>>= 8;
            }
            bArr[length2 - 1] = (byte) i5;
            return bArr;
        }
        boolean z = true;
        while (length > 1) {
            length--;
            int i6 = this.magnitude[length] ^ -1;
            if (z) {
                i6++;
                z = i6 == 0;
            }
            int i7 = length2 - 1;
            bArr[i7] = (byte) i6;
            int i8 = i7 - 1;
            bArr[i8] = (byte) (i6 >>> 8);
            int i9 = i8 - 1;
            bArr[i9] = (byte) (i6 >>> 16);
            length2 = i9 - 1;
            bArr[length2] = (byte) (i6 >>> 24);
        }
        int i10 = this.magnitude[0];
        if (z) {
            i10--;
        }
        while ((i10 & -256) != 0) {
            length2--;
            bArr[length2] = (byte) (i10 ^ -1);
            i10 >>>= 8;
        }
        int i11 = length2 - 1;
        bArr[i11] = (byte) (i10 ^ -1);
        if (i11 <= 0) {
            return bArr;
        }
        bArr[i11 - 1] = -1;
        return bArr;
    }

    public String toString() {
        return toString(10);
    }

    public String toString(int i) {
        if (this.magnitude == null) {
            return "null";
        }
        if (this.sign == 0) {
            return Global.NOTIFICATION_DICTIONARY_RESULT_FAIL;
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (i == 16) {
            for (int i2 = 0; i2 < this.magnitude.length; i2++) {
                String str = "0000000" + Integer.toHexString(this.magnitude[i2]);
                stringBuffer.append(str.substring(str.length() - 8));
            }
        } else if (i == 2) {
            stringBuffer.append('1');
            for (int bitLength = bitLength() - 2; bitLength >= 0; bitLength--) {
                stringBuffer.append(testBit(bitLength) ? '1' : '0');
            }
        } else {
            Stack stack = new Stack();
            VzwBigInt vzwBigInt = new VzwBigInt(Integer.toString(i, i), i);
            for (VzwBigInt abs = abs(); !abs.equals(ZERO); abs = abs.divide(vzwBigInt)) {
                VzwBigInt mod = abs.mod(vzwBigInt);
                if (mod.equals(ZERO)) {
                    stack.push(Global.NOTIFICATION_DICTIONARY_RESULT_FAIL);
                } else {
                    stack.push(Integer.toString(mod.magnitude[0], i));
                }
            }
            while (!stack.empty()) {
                stringBuffer.append((String) stack.pop());
            }
        }
        String stringBuffer2 = stringBuffer.toString();
        while (stringBuffer2.length() > 1 && stringBuffer2.charAt(0) == '0') {
            stringBuffer2 = stringBuffer2.substring(1);
        }
        return stringBuffer2.length() == 0 ? Global.NOTIFICATION_DICTIONARY_RESULT_FAIL : this.sign == -1 ? "-" + stringBuffer2 : stringBuffer2;
    }

    public VzwBigInt xor(VzwBigInt vzwBigInt) {
        if (this.sign == 0) {
            return vzwBigInt;
        }
        if (vzwBigInt.sign == 0) {
            return this;
        }
        int[] iArr = this.sign > 0 ? this.magnitude : add(ONE).magnitude;
        int[] iArr2 = vzwBigInt.sign > 0 ? vzwBigInt.magnitude : vzwBigInt.add(ONE).magnitude;
        boolean z = (this.sign < 0 && vzwBigInt.sign >= 0) || (this.sign >= 0 && vzwBigInt.sign < 0);
        int[] iArr3 = new int[Math.max(iArr.length, iArr2.length)];
        int length = iArr3.length - iArr.length;
        int length2 = iArr3.length - iArr2.length;
        int i = 0;
        while (i < iArr3.length) {
            int i2 = i >= length ? iArr[i - length] : 0;
            int i3 = i >= length2 ? iArr2[i - length2] : 0;
            if (this.sign < 0) {
                i2 ^= -1;
            }
            if (vzwBigInt.sign < 0) {
                i3 ^= -1;
            }
            iArr3[i] = i2 ^ i3;
            if (z) {
                iArr3[i] = iArr3[i] ^ -1;
            }
            i++;
        }
        VzwBigInt vzwBigInt2 = new VzwBigInt(1, iArr3);
        if (z) {
            vzwBigInt2 = vzwBigInt2.not();
        }
        return vzwBigInt2;
    }
}
