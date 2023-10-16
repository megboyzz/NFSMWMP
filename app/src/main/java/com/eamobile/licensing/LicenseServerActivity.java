/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.content.Context
 *  android.os.Handler
 *  android.os.HandlerThread
 */
package com.eamobile.licensing;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import java.util.ArrayList;

public class LicenseServerActivity {
    private static boolean G;
    private static Activity H;
    private static int M = 0;
    private static int N = 0;
    public static final String a = "com.android.vending.licensing.eapref";
    static final int k = -1;
    static final int l = 13;
    static final int m = 14;
    static final int n = 15;
    static final int o = 16;
    static final int p = 17;
    static final int q = 18;
    static final int r = 19;
    static final int s = 20;
    static final int t = 21;
    protected static final String u = "licenseserver/";
    protected static Object v;
    private static LicenseServerActivity y;
    private Object A;
    private String B;
    private Context C;
    private String D;
    private Handler E = null;
    private boolean F = false;
    private Object I;
    private Object J;
    private Object K;
    private String L;
    private String O = "en";
    public Object b;
    public Object c;
    byte[] d;
    String e;
    public Handler f = null;
    Handler g = null;
    public Handler h = null;
    public Handler i = null;
    public String j;
    ILicenseServerActivityCallback w = null;
    ArrayList x = new ArrayList();
    private Object z;

    static {
        y = null;
        G = false;
        M = -1;
        N = -1;
    }

    private LicenseServerActivity() {
    }

    static /* synthetic */ Handler a(LicenseServerActivity licenseServerActivity) {
        return licenseServerActivity.E;
    }

    static /* synthetic */ Object a(LicenseServerActivity licenseServerActivity, Object as2) {
        licenseServerActivity.A = as2;
        return as2;
    }

    static /* synthetic */ String a(LicenseServerActivity licenseServerActivity, String string2) {
        licenseServerActivity.D = string2;
        return string2;
    }

    protected static Activity b() {
        return H;
    }

    static /* synthetic */ String b(LicenseServerActivity licenseServerActivity) {
        return licenseServerActivity.L;
    }

    static /* synthetic */ Object c(LicenseServerActivity licenseServerActivity) {
        return new Object();
    }

    static /* synthetic */ String d(LicenseServerActivity licenseServerActivity) {
        return licenseServerActivity.D;
    }

    static /* synthetic */ Context e(LicenseServerActivity licenseServerActivity) {
        return licenseServerActivity.C;
    }

    static /* synthetic */ String f(LicenseServerActivity licenseServerActivity) {
        return licenseServerActivity.B;
    }

    static /* synthetic */ Object g(LicenseServerActivity licenseServerActivity) {
        return licenseServerActivity.A;
    }

    private void g() {
        if (this.A != null) {
            this.A = null;
        }
        H = null;
        y = null;
    }

    public static LicenseServerActivity getInstance() {
        if (y != null) return y;
        y = new LicenseServerActivity();
        return y;
    }

    private void h() {
        if (this.J == null) return;
    }

    public boolean LastCheckPointCheck() {
        return true;
    }

    public void a() {

    }

    /*
     * Exception decompiling
     */
    public void a(int var1_1) {

    }

    protected void a(Context context) {
    }

    public void c() {
    }

    public int d() {
        return M;
    }

    public void destroyLicenseServerActvity() {
        this.h();
        G = false;
        M = -1;
        this.g();
    }

    protected int e() {
        return N;
    }

    protected void f() {
        this.h();
        G = false;
        M = -1;
        this.g();
    }

    public void initLicenseServerActivity(Activity object, ILicenseServerActivityCallback iLicenseServerActivityCallback, Context context, byte[] object2, String string2, String string3, String string4) {
        if (this.F) {
            return;
        }
        this.F = true;
        this.L = string4;
        if (this.L == null) {
            this.L = "001";
        }
        this.B = string3;
        this.C = context;
        this.d = object2;
        this.e = string2;
        HandlerThread waitting_thread = new HandlerThread("waitting thread");
        waitting_thread.start();
        this.f = new Handler(waitting_thread.getLooper());
        if (H == null) {
            H = object;
            this.w = iLicenseServerActivityCallback;
        }
        if (v == null) {
            this.O = context.getResources().getConfiguration().locale.toString();
        }
        this.a(context);
        if (G) return;
        G = true;
        this.a(20);
    }
}

