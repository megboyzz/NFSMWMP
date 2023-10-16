package com.verizon.vcast.apps;

abstract class VzwPack {
    VzwPack() {
    }

    public static int bigEndianToInt(byte[] bArr, int i) {
        int i2 = i + 1;
        int i3 = i2 + 1;
        return (bArr[i] << 24) | ((bArr[i2] & 255) << 16) | ((bArr[i3] & 255) << 8) | (bArr[i3 + 1] & 255);
    }

    public static long bigEndianToLong(byte[] bArr, int i) {
        return ((((long) bigEndianToInt(bArr, i)) & 4294967295L) << 32) | (((long) bigEndianToInt(bArr, i + 4)) & 4294967295L);
    }

    public static void intToBigEndian(int i, byte[] bArr, int i2) {
        bArr[i2] = (byte) (i >>> 24);
        int i3 = i2 + 1;
        bArr[i3] = (byte) (i >>> 16);
        int i4 = i3 + 1;
        bArr[i4] = (byte) (i >>> 8);
        bArr[i4 + 1] = (byte) i;
    }

    public static void longToBigEndian(long j, byte[] bArr, int i) {
        intToBigEndian((int) (j >>> 32), bArr, i);
        intToBigEndian((int) (4294967295L & j), bArr, i + 4);
    }
}
