package com.verizon.vcast.apps;

import android.content.Context;
import android.util.Log;

public final class LicenseAuthenticator extends LicenseAuthenticatorInternal {
    public static final int ERROR_CONTENT_HANDLER = 100;
    public static final int ERROR_GENERAL = 106;
    public static final int ERROR_ILLEGAL_ARGUMENT = 101;
    public static final int ERROR_SECURITY = 102;
    public static final int ERROR_UNABLE_TO_CONNECT_TO_CDS = 107;
    public static final int ITEM_NOT_FOUND = 51;
    public static final int ITEM_USE_BLOCKED = 60;
    public static final int LICENSE_NOT_FOUND = 52;
    public static final int LICENSE_OK = 0;
    public static final int LICENSE_TRIAL_OK = 1;
    public static final int LICENSE_VALIDATION_FAILED = 50;

    public class CheckLicenseResult {
        public String license;
        public Integer result;

        public CheckLicenseResult() {
        }

        public String getLicense() {
            return this.license;
        }

        public Integer getResult() {
            return this.result;
        }

        /* access modifiers changed from: package-private */
        public void setLicense(String str) {
            this.license = str;
        }

        /* access modifiers changed from: package-private */
        public void setResult(Integer num) {
            this.result = num;
        }

        public String toString() {
            return "result code:" + this.result + ", license:" + this.license;
        }
    }

    public LicenseAuthenticator(Context context) {
        callingContext = context;
    }

    public synchronized CheckLicenseResult checkContentLicense(String str) {
        return checkInAppContentLicense(str, null);
    }

    public synchronized CheckLicenseResult checkContentLicense(String str, boolean z) {
        return checkInAppContentLicense(str, null, z);
    }

    public synchronized CheckLicenseResult checkInAppContentLicense(String str, String str2) {
        return checkInAppContentLicense(str, str2, false);
    }

    public synchronized CheckLicenseResult checkInAppContentLicense(String str, String str2, boolean z) {
        CheckLicenseResult checkLicenseResult;
        Log.i("LicenseAuthenticator", "begin checkLicense()");
        checkLicenseResult = new CheckLicenseResult();
        String str3 = null;
        if (str != null) {
            try {
                if (!str.equals("")) {
                    boolean isOdpInstalled = isOdpInstalled("com.vzw.appstore.android");
                    boolean z2 = false;
                    if (isOdpInstalled("com.gravitymobile.app.hornbill")) {
                        z2 = true;
                        str3 = "com.gravitymobile.app.hornbill";
                    }
                    if (isOdpInstalled("com.verizon.vcast.apps")) {
                        z2 = true;
                        str3 = "com.verizon.vcast.apps";
                    }
                    if (isEmaKeyword(str) && !isOdpInstalled) {
                        checkLicenseResult.setResult(100);
                    } else if (isEmaKeyword(str) && isOdpInstalled) {
                        checkLicenseResult = checkLicenseInternal("com.vzw.appstore.android", "com.verizon.vcast.apps.VCastAppsLicenseService", str, str2, z, checkLicenseResult);
                        Log.i("LicenseAuthenticator", "checkLicense() finished.  Trying to shutDownLicenseService()");
                        shutDownLicenseService();
                    } else if (!isEmaKeyword(str)) {
                        if (isOdpInstalled) {
                            CheckLicenseResult checkLicenseInternal = checkLicenseInternal("com.vzw.appstore.android", "com.verizon.vcast.apps.VCastAppsLicenseService", str, str2, z, checkLicenseResult);
                            if (checkLicenseInternal.getResult().intValue() == 0 || checkLicenseInternal.getResult().intValue() == 1 || !z2) {
                                Log.i("LicenseAuthenticator", "checkLicense() finished.  Trying to shutDownLicenseService()");
                                shutDownLicenseService();
                                checkLicenseResult = checkLicenseInternal;
                            } else {
                                shutDownLicenseService();
                            }
                        }
                        if (z2) {
                            checkLicenseResult = checkLicenseInternal(str3, "com.verizon.vcast.apps.VCastAppsLicenseService", str, str2, z, checkLicenseResult);
                            Log.i("LicenseAuthenticator", "checkLicense() finished.  Trying to shutDownLicenseService()");
                            shutDownLicenseService();
                        } else {
                            checkLicenseResult.setResult(100);
                            Log.i("LicenseAuthenticator", "checkLicense() finished.  Trying to shutDownLicenseService()");
                            shutDownLicenseService();
                        }
                    } else {
                        checkLicenseResult.setResult(106);
                        Log.i("LicenseAuthenticator", "checkLicense() finished.  Trying to shutDownLicenseService()");
                        shutDownLicenseService();
                    }
                }
            } finally {
                Log.i("LicenseAuthenticator", "checkLicense() finished.  Trying to shutDownLicenseService()");
                shutDownLicenseService();
            }
        }
        checkLicenseResult.setResult(101);
        Log.i("LicenseAuthenticator", "checkLicense() finished.  Trying to shutDownLicenseService()");
        shutDownLicenseService();
        return checkLicenseResult;
    }

    public synchronized int checkLicense(String str) {
        new CheckLicenseResult();
        return checkContentLicense(str).result.intValue();
    }

    public synchronized int checkLicense(String str, boolean z) {
        new CheckLicenseResult();
        return checkContentLicense(str, z).result.intValue();
    }

    public CheckLicenseResult checkTestContentLicense(String str, CheckLicenseResult checkLicenseResult) {
        return checkLicenseResult;
    }

    public CheckLicenseResult checkTestInAppContentLicense(String str, String str2, CheckLicenseResult checkLicenseResult) {
        return checkLicenseResult;
    }

    public int checkTestLicense(String str, int i) {
        return i;
    }
}
