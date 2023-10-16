package com.verizon.vcast.apps;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class LicenseAuthenticatorInternal {
    protected static final String EMA_KEYWORD_PREFIX = "ema_";
    private static final int EMA_LICENSE_SIGNATURE_LENGTH = 172;
    protected static final String EMA_ODP_IDENTIFIER = "com.vzw.appstore.android";
    protected static final int LICENSE_SERVICE_STARTED = -1;
    private static final byte ODP_ERROR_GENERAL = 3;
    private static final byte ODP_ERROR_ITEM_NOT_FOUND = 1;
    private static final byte ODP_ERROR_ITEM_USE_BLOCKED = 5;
    private static final byte ODP_ERROR_LICENSE_VALIDATION_FAILED = 2;
    protected static final String ODP_INTENT_CLASSNAME = "com.verizon.vcast.apps.VCastAppsLicenseService";
    private static final int ODP_TYPE_EMA = 1;
    private static final int ODP_TYPE_VCAST = 0;
    protected static final String TAG = LicenseAuthenticator.class.getSimpleName();
    protected static final String VCAST_GM_ODP_IDENTIFIER = "com.gravitymobile.app.hornbill";
    private static final int VCAST_LICENSE_SIGNATURE_LENGTH = 128;
    protected static final String VCAST_UIE_ODP_IDENTIFIER = "com.verizon.vcast.apps";
    protected static Context callingContext = null;
    private volatile DatabaseHelper db = null;
    List<String> inAppLicenses = null;
    private int indexOfInApp;
    private volatile Object initLock = new Object();
    protected volatile IVCastAppsLicenseService licenseService = null;
    protected volatile LicenseServiceConnection licenseServiceConnection;
    private volatile boolean serviceStarted = false;

    /* access modifiers changed from: private */
    public class LicenseServiceConnection implements ServiceConnection {
        private LicenseServiceConnection() {
        }

        /* synthetic */ LicenseServiceConnection(LicenseAuthenticatorInternal licenseAuthenticatorInternal, LicenseServiceConnection licenseServiceConnection) {
            this();
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LicenseAuthenticatorInternal.this.licenseService = IVCastAppsLicenseService.Stub.asInterface(iBinder);
            synchronized (LicenseAuthenticatorInternal.this.initLock) {
                LicenseAuthenticatorInternal.this.initLock.notifyAll();
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            LicenseAuthenticatorInternal.this.licenseService = null;
            LicenseAuthenticatorInternal.this.licenseServiceConnection = null;
            synchronized (LicenseAuthenticatorInternal.this.initLock) {
                LicenseAuthenticatorInternal.this.initLock.notifyAll();
            }
        }
    }

    private long convertToLong(String str) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        try {
            int indexOf = str.indexOf("-");
            i = Integer.parseInt(str.substring(0, indexOf));
            int i7 = indexOf + 1;
            int indexOf2 = str.indexOf("-", indexOf + 1);
            i2 = Integer.parseInt(str.substring(i7, indexOf2)) - 1;
            int i8 = indexOf2 + 1;
            int indexOf3 = str.indexOf("T", indexOf2 + 1);
            i3 = Integer.parseInt(str.substring(i8, indexOf3));
            int i9 = indexOf3 + 1;
            int indexOf4 = str.indexOf(":", indexOf3 + 1);
            i4 = Integer.parseInt(str.substring(i9, indexOf4));
            int i10 = indexOf4 + 1;
            int indexOf5 = str.indexOf(":", indexOf4 + 1);
            i5 = Integer.parseInt(str.substring(i10, indexOf5));
            i6 = Integer.parseInt(str.substring(indexOf5 + 1));
        } catch (Exception e) {
            i = 1970;
            i2 = 0;
            i3 = 1;
            i4 = 0;
            i5 = 0;
            i6 = 0;
        }
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        instance.set(Calendar.YEAR, i);
        instance.set(Calendar.MONTH, i2);
        instance.set(Calendar.DAY_OF_MONTH, i3);
        instance.set(Calendar.HOUR_OF_DAY, i4);
        instance.set(Calendar.MINUTE, i5);
        instance.set(Calendar.SECOND, i6);
        return instance.getTime().getTime();
    }

    private long getBestCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long savedNetworkTime = getSavedNetworkTime();
        return savedNetworkTime > currentTimeMillis ? savedNetworkTime : currentTimeMillis;
    }

    private List<Node> getChildByName(Node node, String str) {
        ArrayList arrayList = new ArrayList();
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeName().equals(str)) {
                arrayList.add(item);
            }
        }
        return arrayList;
    }

    private int getErrorCode(byte[] bArr) {
        switch (bArr[0]) {
            case 1:
                return 51;
            case 2:
                return 52;
            case 3:
                return 107;
            case 4:
            default:
                return 106;
            case 5:
                return 60;
        }
    }

    private String getMDN() {
        TelephonyManager telephonyManager = (TelephonyManager) callingContext.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = telephonyManager.getPhoneType();
        switch (phoneType) {
            case 1:
            case 2:
                return telephonyManager.getLine1Number();
            default:
                Log.i("VZWLicense", "LicenseAuthenticator.getMDN(): Unsupported phoneType " + phoneType);
                return null;
        }
    }

    private String getMEID() {
        TelephonyManager telephonyManager = (TelephonyManager) callingContext.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneType = telephonyManager.getPhoneType();
        switch (phoneType) {
            case 1:
            case 2:
                return telephonyManager.getDeviceId();
            default:
                Log.i("VZWLicense", "LicenseAuthenticator.getMEID(): Unsupported phoneType " + phoneType);
                return null;
        }
    }

    private String getMapValue(String str, String str2) {
        try {
            int indexOf = str2.indexOf("<" + str + ">") + 2 + str.length();
            int indexOf2 = str2.indexOf("</" + str + ">");
            if (indexOf < 0 || indexOf2 < 0) {
                return null;
            }
            return str2.substring(indexOf, indexOf2);
        } catch (Exception e) {
            return null;
        }
    }

    private long getSavedNetworkTime() {
        Long l = 0L;
        if (this.db == null) {
            this.db = new DatabaseHelper(LicenseAuthenticator.callingContext);
        }
        try {
            Iterator<String> it = this.db.selectAll().iterator();
            if (it.hasNext()) {
                l = Long.valueOf(Long.parseLong(it.next()));
            }
        } catch (Throwable th) {
            System.err.print(th);
            th.printStackTrace();
        }
        Log.d("VZWLicense", "getSavedNetworkTime()=" + Long.toString(l));
        return l;
    }

    private boolean isEmaSource(String str) {
        return str.contains("<Source>") && getMapValue("Source", str).equalsIgnoreCase("EMA");
    }

    private boolean isSignatureValid(byte[] bArr, byte[] bArr2, int i) {
        VzwRSAKyParam vzwRSAKyParam = null;
        Log.i(TAG, "Validating Signature");
        if (i == 1) {
            try {
                vzwRSAKyParam = new VzwRSAKyParam(false, new VzwBigInt("107493806210625235577865383583966213583203769054180884203670357027270028071413938146414253505380813234565169065101353106586121476194778366775987951307055104758706320089544509318953312117460811040627008525057894596490834350638622651605940563447702952001450779562939078801196325903274126043191010876664350998971"), new VzwBigInt("65537"));
            } catch (Exception e) {
                return false;
            }
        } else {
            vzwRSAKyParam = new VzwRSAKyParam(false, new VzwBigInt("113751985209425220389904911573418596054906626841732881629605928013581871729424978229159252758527107263930688983674762647850445576382155543169643622580638253014102297871653770954828091210266102863696922720690032125039249372686528361690969551775463749111601961827967139084754353180300766686096391389069084141699"), new VzwBigInt("65537"));
        }
        VzwPKCS1Encd vzwPKCS1Encd = new VzwPKCS1Encd(new VzwRSAEng());
        vzwPKCS1Encd.init(false, vzwRSAKyParam);
        byte[] processBlock = new byte[0];
        try {
            processBlock = vzwPKCS1Encd.processBlock(bArr, 0, bArr.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        VzwSHA vzwSHA = new VzwSHA();
        byte[] bArr3 = new byte[20];
        vzwSHA.update(bArr2, 0, bArr2.length);
        vzwSHA.doFinal(bArr3, 0);
        if (processBlock.length == 20) {
            for (int i2 = 0; i2 < bArr3.length; i2++) {
                if (bArr3[i2] != processBlock[i2]) {
                    Log.e(TAG, "Signature Validation Failed");
                    return false;
                }
            }
            Log.i(TAG, "Signature Validation Passed");
            return true;
        }
        Log.e(TAG, "Signature Validation Failed");
        return false;
    }

    private int validateLicense(Node node, String str, String str2) throws Exception {
        String str3 = null;
        String str4 = null;
        String str5 = null;
        String str6 = null;
        String str7 = null;
        String str8 = null;
        String str9 = null;
        String str10 = null;
        boolean z = false;
        boolean z2 = false;
        long j = 0;
        long j2 = 0;
        long j3 = 0;
        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            try {
                Node item = childNodes.item(i);
                String nodeName = item.getNodeName();
                if (nodeName.equals("MDN")) {
                    str3 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("MEID")) {
                    str4 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("UniqueDeviceId")) {
                    str10 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("IsLocked")) {
                    z = Boolean.parseBoolean(item.getFirstChild().getNodeValue());
                } else if (nodeName.equals("IsSubscriptionExpired")) {
                    str5 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("IsLimitedTimeExpired")) {
                    str6 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("IsPurchaseRequired")) {
                    str7 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("Keyword")) {
                    str8 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("PriceType")) {
                    str9 = item.getFirstChild().getNodeValue();
                } else if (nodeName.equals("IssueTime")) {
                    j = convertToLong(item.getFirstChild().getNodeValue());
                } else if (nodeName.equals("NextCheckTime")) {
                    j2 = convertToLong(item.getFirstChild().getNodeValue());
                } else if (nodeName.equals("PurchaseDate")) {
                    j3 = convertToLong(item.getFirstChild().getNodeValue());
                }
            } catch (Exception e) {
                Log.e(TAG, "Cannot fetch:" + ((String) null) + "from license.");
            }
        }
        if (str9.equalsIgnoreCase("Subscription")) {
            if (!str5.equalsIgnoreCase("FALSE")) {
                Log.e(TAG, "License Validation Failed: Subscription Expired");
                return 50;
            } else if (!str7.equalsIgnoreCase("FALSE")) {
                Log.e(TAG, "License Validation Failed: Purchase is Required");
                return 50;
            }
        } else if (str9.equalsIgnoreCase("Per Period")) {
            if (!str6.equalsIgnoreCase("FALSE")) {
                Log.e(TAG, "License Validation Failed: Limited Time Expired");
                return 50;
            } else if (!str7.equalsIgnoreCase("FALSE")) {
                Log.e(TAG, "License Validation Failed: Purchase is Required");
                return 50;
            }
        } else if (str9.equalsIgnoreCase("First Download") || str9.equalsIgnoreCase("Free")) {
            if (!str7.equalsIgnoreCase("FALSE")) {
                Log.e(TAG, "License Validation Failed: Purchase is Required");
                return 50;
            }
        } else if (str9.equalsIgnoreCase("Trial Period")) {
            z2 = true;
            if (!str6.equalsIgnoreCase("FALSE")) {
                Log.e(TAG, "License Validation Failed: Limited Time Expired");
                return 50;
            }
        }
        String mdn = getMDN();
        String meid = getMEID();
        if (!str3.equalsIgnoreCase(mdn)) {
            Log.e(TAG, "License Validation Failed: MDN from license (" + str3 + ") does not match MDN of device (" + mdn + ")");
            return 50;
        }
        if (str2.equals(EMA_ODP_IDENTIFIER)) {
            if (str4 != null && !str4.equalsIgnoreCase(meid)) {
                Log.e(TAG, "License Validation Failed: MEID from license (" + str4 + ") does not match MEID of device (" + meid + ")");
                return 50;
            } else if (str10 != null && !str10.equalsIgnoreCase(meid)) {
                Log.e(TAG, "License Validation Failed: UniqueDeviceId from license (" + str4 + ") does not match MEID of device (" + meid + ")");
                return 50;
            }
        }
        if (str != null && !str8.equalsIgnoreCase(str)) {
            Log.e(TAG, "License Validation Failed: Keyword from license (" + str8 + ") does not match keyword (" + str + ")");
            return 50;
        } else if (z) {
            Log.e(TAG, "License Validation Failed: The app is 'Remote Blocked'");
            return 50;
        } else {
            long bestCurrentTime = getBestCurrentTime();
            if (bestCurrentTime < j) {
                Log.e(TAG, "License Validation Failed: CurrentTime < LastIssueTime. CurrentTime: " + bestCurrentTime + ", LastIssueTime: " + j);
                return 50;
            } else if (bestCurrentTime < j3) {
                Log.e(TAG, "License Validation Failed: CurrentTime < PurchaseDate. CurrentTime: " + bestCurrentTime + ", PurchaseDate: " + j3);
                return 50;
            } else if (bestCurrentTime > j2) {
                Log.e(TAG, "License Validation Failed: CurrentTime > NextCheckTime. CurrentTime: " + bestCurrentTime + ", NextCheckTime: " + j2);
                return 50;
            } else if (z2) {
                Log.e(TAG, "License Validation OK: LICENSE_TRIAL_OK");
                return 1;
            } else {
                Log.i(TAG, "License Validation OK: LICENSE_OK");
                return 0;
            }
        }
    }

    /* access modifiers changed from: protected */
    public LicenseAuthenticator.CheckLicenseResult checkLicenseInternal(String str, String str2, String str3, String str4, boolean z, LicenseAuthenticator.CheckLicenseResult checkLicenseResult) {
        byte[] bArr = null;
        int initLicenseService = initLicenseService(str, str2);
        if (initLicenseService == -1) {
            initLicenseService = 106;
            try {
                long time = new Date().getTime();
                if (!z || str != EMA_ODP_IDENTIFIER) {
                    Log.d(TAG, "Calling getLicense() with: keyword=" + str3);
                    bArr = this.licenseService.getLicense(str3);
                } else {
                    Log.d(TAG, "Calling getRemoteLicense() with: keyword=" + str3 + ", isRemoteLockEnabled=" + z);
                    bArr = this.licenseService.getRemoteLicense(str3, z);
                }
                Log.d(TAG, "getRemoteLicense() took " + (new Date().getTime() - time) + " milliseconds and returned:" + new String(bArr));
                checkLicenseResult.setLicense(new String(bArr));
            } catch (RemoteException e) {
                Log.e("LicenseAuthenticator", "Error fetching license from remote service", e);
                initLicenseService = 100;
            }
            try {
                saveNetworkTime();
                initLicenseService = validateLicense(bArr, str3, str4, str);
            } catch (RemoteException e2) {
                checkLicenseResult.setResult(107);
            } catch (Exception e3) {
                Log.e("LicenseAuthenticator", "General failure", e3);
            }
        }
        checkLicenseResult.setResult(Integer.valueOf(initLicenseService));
        if (str4 != null) {
            try {
                this.inAppLicenses = getInAppLicenses(new String(bArr));
                checkLicenseResult.setLicense(this.inAppLicenses.get(this.indexOfInApp));
            } catch (Exception e4) {
                Log.e(TAG, "Failed to extract in app specific license", e4);
            }
        }
        return checkLicenseResult;
    }

    /* access modifiers changed from: protected */
    public void cleanupDB() {
        if (this.db != null) {
            this.db.cleanup();
            this.db = null;
        }
    }

    public List<String> getInAppLicenses(String str) {
        int indexOf = str.indexOf("<InAppList>");
        if (indexOf == -1) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        int i = indexOf;
        while (str.indexOf("<InApp>", i) != -1) {
            int indexOf2 = str.indexOf("<InApp>", i);
            int indexOf3 = str.indexOf("</InApp>", indexOf2);
            arrayList.add(str.substring(indexOf2, indexOf3 + 8));
            i = indexOf3;
        }
        return arrayList;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0094, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0095, code lost:
        android.util.Log.e("LicenseAuthenticator", "Security error connecting to remote service", r3);
        android.util.Log.e("LicenseAuthenticator", "Likely cause: missing service permission. Required permissions:", r3);
        android.util.Log.e("LicenseAuthenticator", "  android.permission.READ_PHONE_STATE");
        android.util.Log.e("LicenseAuthenticator", "  android.permission.START_BACKGROUND_SERVICE");
        android.util.Log.e("LicenseAuthenticator", "  com.verizon.vcast.apps.VCAST_APPS_LICENSE_SERVICE");
        android.util.Log.e("LicenseAuthenticator", "  com.verizon.vcast.apps.ACCESS_NETWORK_STATE");
        android.util.Log.e("LicenseAuthenticator", "  android.permission.INTERNET");
        android.util.Log.e("LicenseAuthenticator", "  com.vzw.appstore.android.permission");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00d1, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00d2, code lost:
        android.util.Log.e("LicenseAuthenticator", "initting license service failed", r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:?, code lost:
        return 102;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        return 106;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0094 A[ExcHandler: SecurityException (r3v0 'e' java.lang.SecurityException A[CUSTOM_DECLARE]), Splitter:B:8:0x0032] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int initLicenseService(java.lang.String r13, java.lang.String r14) throws java.lang.SecurityException {
        /*
        // Method dump skipped, instructions count: 234
        */
        throw new UnsupportedOperationException("Method not decompiled: com.verizon.vcast.apps.LicenseAuthenticatorInternal.initLicenseService(java.lang.String, java.lang.String):int");
    }

    /* access modifiers changed from: protected */
    public boolean isEmaKeyword(String str) {
        boolean z = str.startsWith(EMA_KEYWORD_PREFIX) && str.split("[_]").length > 2;
        Log.i(TAG, "isKeyWord: " + str);
        return z;
    }

    /* access modifiers changed from: protected */
    public boolean isOdpInstalled(String str) {
        boolean z = false;
        try {
            callingContext.getPackageManager().getApplicationInfo(str, PackageManager.GET_UNINSTALLED_PACKAGES);
            z = true;
            Log.i(TAG, String.valueOf(str) + " is installed");
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, String.valueOf(str) + " is not installed");
            return z;
        }
    }

    /* access modifiers changed from: protected */
    public void saveNetworkTime() throws RemoteException {
        long currentTimeMillis = System.currentTimeMillis();
        try {
            currentTimeMillis = this.licenseService.getTime();
        } catch (RemoteException e) {
            Log.e("LicenseAuthenticator", "Error fetching network time from remote service", e);
        }
        if (currentTimeMillis == -1) {
            Log.e("LicenseAuthenticator", "Failure to fetch network time from CDS servers");
            throw new RemoteException();
        }
        long j = 0;
        if (this.db == null) {
            this.db = new DatabaseHelper(LicenseAuthenticator.callingContext);
        }
        try {
            long savedNetworkTime = getSavedNetworkTime();
            j = currentTimeMillis > savedNetworkTime ? currentTimeMillis : savedNetworkTime;
            if (j != savedNetworkTime) {
                this.db.deleteAll();
                this.db.insert(Long.toString(j));
            }
        } catch (Throwable th) {
            Log.e("LicenseAuthenticator", "Error saving network time to Database", th);
        }
        Log.d("VZWLicense", "saveNetworkTime()=" + Long.toString(j));
    }

    public void shutDownLicenseService() {
        Log.i(TAG, "shutDownLicenseService()");
        cleanupDB();
        try {
            if (this.licenseServiceConnection != null) {
                callingContext.unbindService(this.licenseServiceConnection);
                this.licenseServiceConnection = null;
                this.serviceStarted = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to shutdown license service.", e);
        }
    }

    /* access modifiers changed from: protected */
    public int validateLicense(byte[] bArr, String str, String str2, String str3) throws SecurityException {
        int i;
        byte[] bArr2;
        byte[] bArr3;
        Log.d(TAG, "License XML: [" + new String(bArr) + "]");
        if (bArr.length == 1) {
            return getErrorCode(bArr);
        }
        try {
            if (isEmaSource(new String(bArr))) {
                i = 1;
                bArr2 = new byte[(bArr.length - 172)];
                bArr3 = new byte[EMA_LICENSE_SIGNATURE_LENGTH];
            } else {
                i = 0;
                bArr2 = new byte[(bArr.length - 128)];
                bArr3 = new byte[128];
            }
            System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
            System.arraycopy(bArr, bArr2.length, bArr3, 0, bArr3.length);
            if (!isSignatureValid(bArr3, bArr2, i)) {
                Log.e(TAG, "License Validation Failed: license signature is not valid");
                return 50;
            }
            DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource inputSource = new InputSource();
            inputSource.setCharacterStream(new StringReader(new String(bArr2)));
            NodeList elementsByTagName = newDocumentBuilder.parse(inputSource).getElementsByTagName("License");
            if (elementsByTagName == null) {
                Log.e(TAG, "License Validation Failed: License element not found in license xml.");
                return 50;
            } else if (str2 == null) {
                return validateLicense(elementsByTagName.item(0), str, str3);
            } else {
                Node item = elementsByTagName.item(0);
                if (item == null) {
                    Log.e(TAG, "License Validation Failed: License element not found in license xml.");
                    return 50;
                }
                List<Node> childByName = getChildByName(item, "InAppList");
                if (childByName == null || childByName.size() == 0) {
                    Log.e(TAG, "License Validation Failed: InAppList element not found in license xml.");
                    return 50;
                }
                List<Node> childByName2 = getChildByName(childByName.get(0), "InApp");
                for (int i2 = 0; i2 < childByName2.size(); i2++) {
                    List<Node> childByName3 = getChildByName(childByName2.get(i2), "InAppSKU");
                    if (childByName3.size() != 0 && childByName3.get(0).getFirstChild().getNodeValue().equals(str2)) {
                        this.indexOfInApp = i2;
                        return validateLicense(childByName2.get(i2), null, str3);
                    }
                }
                Log.e(TAG, "License Validation Failed: General failure");
                return 50;
            }
        } catch (SecurityException e) {
            Log.e("LicenseAuthenticator", "Security error connecting to remote service", e);
            Log.e("LicenseAuthenticator", "Likely cause: missing service permission. Required permissions:", e);
            Log.e("LicenseAuthenticator", "  android.permission.READ_PHONE_STATE", e);
            Log.e("LicenseAuthenticator", "  android.permission.START_BACKGROUND_SERVICE", e);
            Log.e("LicenseAuthenticator", "  com.verizon.vcast.apps.VCAST_APPS_LICENSE_SERVICE", e);
            Log.e("LicenseAuthenticator", "  com.verizon.vcast.apps.ACCESS_NETWORK_STATE", e);
            Log.e("LicenseAuthenticator", "  android.permission.INTERNET", e);
            Log.e("LicenseAuthenticator", "  com.vzw.appstore.android.permission", e);
            return 102;
        } catch (Exception e2) {
            Log.e("LicenseAuthenticator", "General failure", e2);
            return 106;
        }
    }
}
