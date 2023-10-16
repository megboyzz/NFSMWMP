package com.verizon.vcast.apps;

/* access modifiers changed from: package-private */
public class VzwRSAKyParam {
    private VzwBigInt exponent;
    private VzwBigInt modulus;
    boolean privateKey;

    public VzwRSAKyParam(boolean z, VzwBigInt vzwBigInt, VzwBigInt vzwBigInt2) {
        this.privateKey = z;
        this.modulus = vzwBigInt;
        this.exponent = vzwBigInt2;
    }

    public VzwBigInt getExponent() {
        return this.exponent;
    }

    public VzwBigInt getModulus() {
        return this.modulus;
    }

    public boolean isPrivate() {
        return this.privateKey;
    }
}
