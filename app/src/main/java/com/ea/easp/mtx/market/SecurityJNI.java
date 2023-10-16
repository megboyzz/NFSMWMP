package com.ea.easp.mtx.market;

public class SecurityJNI {
    private static final String TAG = "SecurityJNI";
    private MarketJNI mMarketJNI;

    SecurityJNI(MarketJNI marketJNI) {
        this.mMarketJNI = marketJNI;
    }

    public void getNonce(Object obj) {
        this.mMarketJNI.getNonce(obj);
    }

    public void verify(String str, String str2, int i, int i2) {
        this.mMarketJNI.verify(str, str2, i, i2);
    }
}
