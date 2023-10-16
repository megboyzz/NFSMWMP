package com.eamobile;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;

import com.eamobile.download.AssetManager;
import com.eamobile.download.Constants;
import com.eamobile.download.Device;
import com.eamobile.download.DeviceData;
import com.eamobile.download.DownloadFileData;
import com.eamobile.download.DownloadProgress;
import com.eamobile.download.LocalZipExtractorEvent;
import com.eamobile.download.Logging;
import com.eamobile.download.MemoryStatus;
import com.eamobile.download.RemoteZipExtractorEvent;
import com.eamobile.download.ZipExtractor;
import com.eamobile.views.CheckUpdatesView;
import com.eamobile.views.CheckingHostIpView;
import com.eamobile.views.ContactingServerView;
import com.eamobile.views.CustomView;
import com.eamobile.views.DeletingAssetsView;
import com.eamobile.views.DownloadFailedView;
import com.eamobile.views.DownloadMsgView;
import com.eamobile.views.DownloadProgressView;
import com.eamobile.views.InvalidAssetVersionView;
import com.eamobile.views.NetworkUnavailableView;
import com.eamobile.views.ServerErrorView;
import com.eamobile.views.Show3GView;
import com.eamobile.views.ShowBGView;
import com.eamobile.views.ShowWifiView;
import com.eamobile.views.SpaceUnavailableView;
import com.eamobile.views.UnSupportedDeviceView;
import com.eamobile.views.UpdatesFoundView;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.xml.parsers.DocumentBuilderFactory;

public class DownloadActivityInternal {
    static boolean ALTERNATIVE_DATA_FOLDER = false;
    static boolean CUSTOM_PROGRESS_BAR = true;
    static boolean DELETE_ASSETS_ON_UPDATE = false;
    static boolean DISABLE_3G = false;
    static String DOWNLOAD_URL = null;
    static final String DOWNLOAD_URL_CONFIG_FILE = "DownloadURL.indicate";
    static boolean DO_NOT_OPEN_STORAGE_SETTINGS = false;
    public static final int ERROR_ASSETS_NOT_FOUND = 5002;
    public static final int ERROR_CHECKSUM_MATCH_FAILED = -11;
    public static final int ERROR_CHECKSUM_NOT_FOUND = -10;
    public static final int ERROR_CONNECTION_UNAVAILABLE = -16;
    public static final int ERROR_CORRUPTED_ZIP = -12;
    public static final int ERROR_DOWNLOAD_TIMEOUT = -1;
    public static final int ERROR_FILE_LIST_RETRIEVE_FAILED = -15;
    public static final int ERROR_MISSING_CHECKSUMS = -13;
    public static final int ERROR_UNEXPECTED_SERVER_ERROR = -14;
    public static final int ERROR_UNSUPPORTED_ASSET_VERSION = -17;
    public static final int ERROR_UNSUPPORTED_DEVICE = 5001;
    private static final int ERROR_ZIP_CHECKSUM_MATCH_FAILED = -4;
    private static final int ERROR_ZIP_CHECKSUM_NOT_FOUND = -3;
    private static final int ERROR_ZIP_EXCEPTION = -1;
    private static final int ERROR_ZIP_NO_ENTRIES = -2;
    private static final String[] EXPECTED_PERMISSIONS = {
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.CHANGE_NETWORK_STATE",
            "android.permission.INTERNET",
            "android.permission.READ_PHONE_STATE",
            "android.permission.WAKE_LOCK"
    };
    private static boolean FORCE_WAKE_DURING_DOWNLOAD = false;
    static int MASTER_SELL_ID = 0;
    static String MIN_ASSET_VERSION_REQUIRED = null;
    private static final int NO_ZIP_ERRORS = 1;
    static int NUMBER_OF_HOURS_TO_UPDATE_CHECKING = 0;
    static int PRODUCT_ID = 0;
    static boolean REDOWNLOAD_ON_SCREEN_SIZE_CHANGE = false;
    private static final String RESOURCES_PATH = "downloadcontent/";
    static boolean RETRIEVE_FULL_SCREEN_RESOLUTION = false;
    public static final int STATE_INVALID = -1;
    public static final int STATE_SHOW_DOWNLOAD_MSG = 1;
    public static final int STATE_DOWNLOADING_ASSETS = 2;
    public static final int STATE_SUCCESS = 3;
    public static final int STATE_SPACE_UNAVAILABLE = 4;
    public static final int STATE_FAILURE = 5;
    public static final int STATE_SHOW_WIFI_DIALOG = 6;
    public static final int STATE_3G_UNAVAILABLE = 7;
    public static final int STATE_CHECK_UPDATES = 8;
    public static final int STATE_UPDATES_FOUND = 9;
    public static final int STATE_SHOW_3G_DIALOG = 10;
    public static final int STATE_BG_VIEW = 11;
    public static final int STATE_UNSUPPORTED_DEVICE = 12;
    public static final int STATE_SERVER_ERROR = 13;
    public static final int STATE_CONTACTING_SERVER = 14;
    public static final int STATE_CHECKING_HOST_IP = 15;
    public static final int STATE_SHOW_DELETING_ASSETS = 16;
    private static final String[] STATE_STRINGS = {
            "",
            "STATE_SHOW_DOWNLOAD_MSG",
            "STATE_DOWNLOADING_ASSETS",
            "STATE_SUCCESS",
            "STATE_SPACE_UNAVAILABLE",
            "STATE_FAILURE",
            "STATE_SHOW_WIFI_DIALOG",
            "STATE_3G_UNAVAILABLE",
            "STATE_CHECK_UPDATES",
            "STATE_UPDATES_FOUND",
            "STATE_SHOW_3G_DIALOG",
            "STATE_BG_VIEW",
            "STATE_UNSUPPORTED_DEVICE",
            "STATE_SERVER_ERROR",
            "STATE_CONTACTING_SERVER",
            "STATE_CHECKING_HOST_IP",
            "STATE_SHOW_DELETING_ASSETS"
    };
    static int TIMEOUT = 10000;
    static int TOTAL_SPACE_MB = 0;
    static int TOTAL_SPACE_MB_MIN = 0;
    static boolean UNCOMPRESS_ZIP_ON_DEVICE = false;
    static boolean UNSAFE_ASSET_DELETION_ON_UPDATE = false;
    static boolean USE_INTERNAL_STORAGE = false;
    static boolean USE_OLD_PROGRESS_BAR = false;
    private static volatile boolean changingState = false;
    protected static DownloadProgress downloadProgress;
    private static int height;
    private static Activity instance;
    private static boolean isDownloadRange = false;
    private static boolean isInitialized = false;
    protected static Language language;
    private static ArrayList<Integer> mErrorList = new ArrayList<>();
    static DownloadActivityInternal mMainActivity = null;
    private static int pState = -1;
    private static int pStatePrev = -1;
    private static String resolution = "";
    private static long spaceAvailableToDownload = -1;
    private static long spaceNeededToDownload = -1;
    private static int totalDownloadSizeMB = 0;
    static boolean unknownHostExceptionTryAgain = true;
    private static int width;
    private String activityAssetPath;
    private boolean activityUseExternal;
    private AssetManager assetManager;
    String bgFileName;
    private Bitmap bmpBg;
    private boolean callSetAssetPathAux;
    private CheckUpdatesView checkUpdatesView;
    private CheckingHostIpView checkingHostIpView;
    private boolean configLoaded;
    private ContactingServerView contactingServerView;
    private DeletingAssetsView deletingAssetsView;
    private Device deviceFallback;
    private DownloadFailedView downloadFailedView;
    private DownloadFileData[] downloadFileData;
    private DownloadMsgView downloadMsgView;
    private DownloadProgressView downloadProgressView;
    String glExtensions;
    private InvalidAssetVersionView invalidAssetVersionView;
    Context mContext;
    IDownloadActivity mDownloadActivity;
    Handler mHandler;
    private String mLocale;
    private NetworkUnavailableView networkUnavailableView;
    private ArrayList<Device> overrideDevices;
    private CustomView pCurrentView;
    protected int percent_downloaded;
    private ServerErrorView serverErrorView;
    private Show3GView show3GView;
    private ShowBGView showBGView;
    private ShowWifiView showWifiView;
    private SpaceUnavailableView spaceUnavailableView;
    private UnSupportedDeviceView unSupportedDeviceView;
    private UpdatesFoundView updatesFoundView;
    private WifiReceiver wifiReceiver;

    public DownloadActivityInternal(Context context) {
        this(context, "title.png");
    }

    public DownloadActivityInternal(Context context, String str) {
        this.overrideDevices = new ArrayList<>();
        this.deviceFallback = null;
        this.downloadFileData = null;
        this.mLocale = "en";
        this.percent_downloaded = 0;
        this.mDownloadActivity = null;
        this.glExtensions = null;
        this.bmpBg = null;
        this.callSetAssetPathAux = false;
        this.activityAssetPath = "";
        this.activityUseExternal = false;
        this.configLoaded = false;
        Logging.DEBUG_INIT();
        this.mHandler = new Handler();
        if (mMainActivity == null) {
            mMainActivity = this;
        }
        if (this.assetManager == null) {
            this.assetManager = new AssetManager();
        }
        this.mContext = context;
        this.bgFileName = str;
        Logging.DEBUG_OUT("DownloadActivityInternal.init()");
        initScreens(context);
    }

    private void checkBackgroundImage() {
        Logging.DEBUG_OUT("Calling: DownloadActivityInternal checkBackgroundImage()");
        if (this.mContext != null && getBackgroundBitmap() == null) {
            try {
                InputStream open = this.mContext.getAssets().open(getResourcesPath() + this.bgFileName);
                if (open != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inTempStorage = new byte[4096];
                    setBackgroundBitmap(Bitmap.createBitmap(BitmapFactory.decodeStream(open, null, options)));
                    open.close();
                }
                Logging.DEBUG_OUT("\tCreating background image");
            } catch (IOException e) {
                setBackgroundBitmap(null);
            }
        }
    }

    private void checkLanguageChange() {
        Logging.DEBUG_OUT("Calling: DownloadActivityInternal checkLanguageChange()");
        if (this.mContext != null && !this.mContext.getResources().getConfiguration().locale.toString().equalsIgnoreCase(this.mLocale)) {
            language = new Language();
            this.mLocale = this.mContext.getResources().getConfiguration().locale.toString();
            String language2 = this.mContext.getResources().getConfiguration().locale.getLanguage();
            Logging.DEBUG_OUT("\tLocale: " + this.mLocale);
            Logging.DEBUG_OUT("\tLanguage: " + language2);
            if (!language.loadStrings(this.mLocale) && !language.loadStrings(language2)) {
                this.mLocale = "en";
                language.loadStrings("en");
            }
        }
    }

    private void checkPermissions() {
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            Logging.DEBUG_OUT(" ");
            Logging.DEBUG_OUT("[uses-permission tags]");
            Logging.DEBUG_OUT("Checking permissions for package: " + this.mContext.getPackageName());
            List asList = Arrays.asList(packageManager.getPackageInfo(this.mContext.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
            boolean z = false;
            for (int i = 0; i < EXPECTED_PERMISSIONS.length; i++) {
                if (!asList.contains(EXPECTED_PERMISSIONS[i])) {
                    z = true;
                    Logging.DEBUG_OUT("\tPermission " + EXPECTED_PERMISSIONS[i] + " is missing.");
                }
            }
            if (z) {
                Logging.DEBUG_OUT(" ");
                Logging.DEBUG_OUT("!!!!! WARNING !!!!!");
                Logging.DEBUG_OUT("\tOne or more expected permissions is missing. Please check uses-permission tags in AndroidManifest.xml");
                Logging.DEBUG_OUT("!!!!! WARNING !!!!!");
            } else {
                Logging.DEBUG_OUT("\tPermissions OK.");
            }
            Logging.DEBUG_OUT(" ");
        } catch (Exception e) {
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    private boolean checkZipExtractorResult(int i) {
        switch (i) {
            case ERROR_ZIP_CHECKSUM_MATCH_FAILED /*{ENCODED_INT: -4}*/:
                recordError(-11);
                return false;
            case ERROR_ZIP_CHECKSUM_NOT_FOUND /*{ENCODED_INT: -3}*/:
                setStateChecksumError();
                return false;
            case -2:
            case -1:
                recordError(-12);
                return false;
            default:
                return true;
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void cleanState(int i) {
        switch (i) {
            case 1:
                this.pCurrentView = this.downloadMsgView;
                break;
            case 2:
                this.pCurrentView = this.downloadProgressView;
                break;
            case 3:
                if (!checkLocalAssetVersion()) {
                    this.pCurrentView = this.invalidAssetVersionView;
                    break;
                } else {
                    Logging.DEBUG_OUT("onResult assetPath = " + this.assetManager.getAssetPath() + " result = " + -1);
                    this.mDownloadActivity.onResult(this.assetManager.getAssetPath(), -1);
                    Logging.DEBUG_CLOSE();
                    return;
                }
            case 4:
                this.pCurrentView = this.spaceUnavailableView;
                break;
            case 5:
                this.pCurrentView = this.downloadFailedView;
                break;
            case 6:
                this.pCurrentView = this.showWifiView;
                break;
            case 7:
                this.pCurrentView = this.networkUnavailableView;
                break;
            case 8:
                this.pCurrentView = this.checkUpdatesView;
                break;
            case 9:
                this.pCurrentView = this.updatesFoundView;
                break;
            case 10:
                this.pCurrentView = this.show3GView;
                break;
            case 11:
                this.pCurrentView = this.showBGView;
                break;
            case 12:
                this.pCurrentView = this.unSupportedDeviceView;
                break;
            case 13:
                this.pCurrentView = this.serverErrorView;
                break;
            case 14:
                this.pCurrentView = this.contactingServerView;
                break;
            case 15:
                this.pCurrentView = this.checkingHostIpView;
                break;
            case 16:
                this.pCurrentView = this.deletingAssetsView;
                break;
        }
        try {
            this.pCurrentView.clean();
        } catch (Exception e) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred while cleaning state:" + e);
        }
    }

    private void cleanStates() {
        if (this.downloadMsgView != null) {
            this.downloadMsgView.clean();
        }
        if (this.showWifiView != null) {
            this.showWifiView.clean();
        }
        if (this.networkUnavailableView != null) {
            this.networkUnavailableView.clean();
        }
        if (this.downloadProgressView != null) {
            this.downloadProgressView.clean();
        }
        if (this.downloadFailedView != null) {
            this.downloadFailedView.clean();
        }
        if (this.spaceUnavailableView != null) {
            this.spaceUnavailableView.clean();
        }
        if (this.checkUpdatesView != null) {
            this.checkUpdatesView.clean();
        }
        if (this.updatesFoundView != null) {
            this.updatesFoundView.clean();
        }
        if (this.show3GView != null) {
            this.show3GView.clean();
        }
        if (this.showBGView != null) {
            this.showBGView.clean();
        }
        if (this.unSupportedDeviceView != null) {
            this.unSupportedDeviceView.clean();
        }
        if (this.serverErrorView != null) {
            this.serverErrorView.clean();
        }
        if (this.contactingServerView != null) {
            this.contactingServerView.clean();
        }
        if (this.checkingHostIpView != null) {
            this.checkingHostIpView.clean();
        }
        if (this.invalidAssetVersionView != null) {
            this.invalidAssetVersionView.clean();
        }
        if (this.deletingAssetsView != null) {
            this.deletingAssetsView.clean();
        }
        this.showBGView = null;
        this.show3GView = null;
        this.updatesFoundView = null;
        this.checkUpdatesView = null;
        this.downloadMsgView = null;
        this.showWifiView = null;
        this.networkUnavailableView = null;
        this.downloadProgressView = null;
        this.downloadFailedView = null;
        this.spaceUnavailableView = null;
        this.unSupportedDeviceView = null;
        this.serverErrorView = null;
        this.contactingServerView = null;
        this.checkingHostIpView = null;
        this.invalidAssetVersionView = null;
        this.deletingAssetsView = null;
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 8192);
        StringBuilder sb = new StringBuilder();
        try {
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine != null)
                    sb.append(readLine + "\n");
                else
                    break;
            }
        } catch (IOException e) {
            Logging.DEBUG_OUT("convertStreamToString Exception: " + e);
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            Logging.DEBUG_OUT("convertStreamToString Exception: " + e);
        }
        return sb.toString();
    }

    private boolean downloadAndValidateZipFile(DownloadFileData downloadFileData2, Hashtable<String, Long> hashtable) {
        String fileURL = downloadFileData2.getFileURL();
        Logging.DEBUG_OUT("Downloading Zip:" + fileURL);
        try {
            URLConnection openConnection = new URL(fileURL).openConnection();
            openConnection.setConnectTimeout(30000);
            openConnection.setReadTimeout(30000);
            InputStream inputStream = openConnection.getInputStream();
            if (inputStream == null) {
                return false;
            }
            return checkZipExtractorResult(new ZipExtractor().extractFiles(inputStream, hashtable, this.assetManager.getAssetPath(), TIMEOUT, new RemoteZipExtractorEvent(downloadProgress, downloadFileData2)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:105:0x031f A[SYNTHETIC, Splitter:B:105:0x031f] */
    /* JADX WARNING: Removed duplicated region for block: B:108:0x0324 A[SYNTHETIC, Splitter:B:108:0x0324] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0163 A[SYNTHETIC, Splitter:B:32:0x0163] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0168 A[SYNTHETIC, Splitter:B:35:0x0168] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean downloadOtherFile(com.eamobile.download.DownloadFileData r29, java.util.Hashtable<java.lang.String, java.lang.Long> r30) {
        /*
        // Method dump skipped, instructions count: 825
        */
        throw new UnsupportedOperationException("Method not decompiled: com.eamobile.DownloadActivityInternal.downloadOtherFile(com.eamobile.download.DownloadFileData, java.util.Hashtable):boolean");
    }

    private boolean extractAndValidateFilesFromZip(String str, Hashtable<String, Long> hashtable) {
        IOException e;
        Logging.DEBUG_OUT("Extracting files from Zip: " + str);
        try {
            FileInputStream fileInputStream = new FileInputStream(this.assetManager.getFilePath(str));
            try {
                Thread.sleep(1000);
                return checkZipExtractorResult(new ZipExtractor().extractFiles(fileInputStream, hashtable, this.assetManager.getAssetPath(), TIMEOUT, new LocalZipExtractorEvent()));
            } catch (InterruptedException e3) {
                return false;
            }
        } catch (IOException e4) {
            e = e4;
            e.printStackTrace();
            return false;
        }
    }

    private Properties generateAssetInfo() {
        Properties properties = new Properties();
        properties.setProperty("packageName", this.mContext.getPackageName());
        properties.setProperty("width", String.valueOf(width));
        properties.setProperty("height", String.valueOf(height));
        return properties;
    }

    /* JADX WARNING: Removed duplicated region for block: B:24:0x0085 A[SYNTHETIC, Splitter:B:24:0x0085] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a5 A[SYNTHETIC, Splitter:B:35:0x00a5] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.Hashtable<java.lang.String, java.lang.Long> getChecksumsHashtable(java.lang.String r17, com.eamobile.download.DownloadFileData[] r18) {
        /*
        // Method dump skipped, instructions count: 204
        */
        throw new UnsupportedOperationException("Method not decompiled: com.eamobile.DownloadActivityInternal.getChecksumsHashtable(java.lang.String, com.eamobile.download.DownloadFileData[]):java.util.Hashtable");
    }

    private Device getDevice(String str) {
        if (this.overrideDevices != null) {
            for (int i = 0; i < this.overrideDevices.size(); i++) {
                Device device = this.overrideDevices.get(i);
                if (device.getName().equals(str)) {
                    return device;
                }
            }
        }
        return null;
    }

    public static boolean getFlagLastReportDownload() {
        return downloadProgress.getFlagLastReportDownload();
    }

    public static boolean getForceWakeDuringDownload() {
        return FORCE_WAKE_DURING_DOWNLOAD;
    }

    protected static Activity getInstance() {
        return instance;
    }

    public static DownloadActivityInternal getMainActivity() {
        return mMainActivity;
    }

    private DownloadFileData getMatchingChecksumFile(String str, DownloadFileData[] downloadFileDataArr) {
        for (int i = 0; i < downloadFileDataArr.length; i++) {
            if (downloadFileDataArr[i].getType() == 2 && downloadFileDataArr[i].getFileName().contains(str)) {
                return downloadFileDataArr[i];
            }
        }
        return null;
    }

    public static long getRealDownloaded() {
        return downloadProgress.getRealDownloaded();
    }

    private String getResolution() {
        Device device = getDevice(getModel());
        if (device != null) {
            return device.getResolutionString();
        }
        String str = null;
        if (RETRIEVE_FULL_SCREEN_RESOLUTION) {
            str = getResolutionUsingUndocumentedMethods();
        }
        if (str == null) {
            str = getResolutionUsingDisplayMetrics();
        }
        if (instance.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            Logging.DEBUG_OUT("SCREEN_ORIENTATION_LANDSCAPE");
            return str;
        }
        Logging.DEBUG_OUT("SCREEN_ORIENTATION_PORTRAIT");
        return str;
    }

    private String getResolutionUsingDisplayMetrics() {
        Logging.DEBUG_OUT("Calling getResolutionUsingDisplayMetrics()...");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        instance.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Logging.DEBUG_OUT("displayMetrics: " + displayMetrics.toString());
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;
        return width + "x" + height;
    }

    private String getResolutionUsingUndocumentedMethods() {
        Logging.DEBUG_OUT("Calling getResolutionUsingUndocumentedMethods()...");

        Display defaultDisplay = instance.getWindowManager().getDefaultDisplay();
        width = defaultDisplay.getWidth();
        height = defaultDisplay.getHeight();

        if (instance.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            if (width < height) {
                int i = width;
                width = height;
                height = i;
            }
        } else if (width > height) {
            int i2 = width;
            width = height;
            height = i2;
        }
        return width + "x" + height;

    }

    public static String getResourcesPath() {
        return RESOURCES_PATH;
    }

    public static long getSizeDownloaded() {
        return downloadProgress.getSizeDownloaded();
    }

    public static int getTotalDownloadSizeMB() {
        return totalDownloadSizeMB;
    }

    public static String getTotalDownloadSizeMBString() {
        return Integer.toString(totalDownloadSizeMB);
    }

    public static boolean isInitialized() {
        return isInitialized;
    }

    private void loadConfigProperties() {
        try {
            Properties properties = new Properties();
            properties.load(instance.getAssets().open(getResourcesPath() + "config.properties"));
            MASTER_SELL_ID = Integer.parseInt(properties.getProperty("MASTER_SELL_ID").trim());
            TOTAL_SPACE_MB = Integer.parseInt(properties.getProperty("TOTAL_SPACE_MB").trim());
            PRODUCT_ID = Integer.parseInt(properties.getProperty("PRODUCT_ID").trim());
            Logging.DEBUG_OUT(" ");
            Logging.DEBUG_OUT("[config.properties]");
            Logging.DEBUG_OUT("\tMASTER_SELL_ID: " + MASTER_SELL_ID);
            Logging.DEBUG_OUT("\tPRODUCT_ID: " + PRODUCT_ID);
            Logging.DEBUG_OUT("\tTOTAL_SPACE_MB: " + TOTAL_SPACE_MB);
            String property = properties.getProperty("USE_INTERNAL_STORAGE");
            if (property != null) {
                USE_INTERNAL_STORAGE = Boolean.parseBoolean(property.trim());
            }
            Logging.DEBUG_OUT("\tUSE_INTERNAL_STORAGE: " + USE_INTERNAL_STORAGE);
            String property2 = properties.getProperty("ALTERNATIVE_DATA_FOLDER");
            if (property2 != null) {
                ALTERNATIVE_DATA_FOLDER = Boolean.parseBoolean(property2.trim());
            }
            Logging.DEBUG_OUT("\tALTERNATIVE_DATA_FOLDER: " + ALTERNATIVE_DATA_FOLDER);
            String property3 = properties.getProperty("DATA_FOLDER");
            if (property3 != null) {
                Logging.DEBUG_OUT("\tDATA_FOLDER: " + property3);
                setAssetPathAux(property3, true);
            } else {
                Logging.DEBUG_OUT("\tDATA_FOLDER not specified in config.properties");
                if (this.callSetAssetPathAux) {
                    setAssetPathAux(this.activityAssetPath, this.activityUseExternal);
                } else {
                    Logging.DEBUG_OUT("[ERROR] DATA_FOLDER not specified in config.properties and setAssetPath() was not called from game's activity.");
                }
            }
            String property4 = properties.getProperty("TIMEOUT");
            if (property4 != null) {
                try {
                    int parseInt = Integer.parseInt(property4.trim());
                    if (parseInt > 0) {
                        TIMEOUT = parseInt * 1000;
                        Logging.DEBUG_OUT("\tTIMEOUT read from config.properties");
                    }
                } catch (Exception e) {
                    Logging.DEBUG_OUT("\t" + e.toString());
                }
            }
            Logging.DEBUG_OUT("\tTIMEOUT: " + TIMEOUT + " milliseconds");
            String property5 = properties.getProperty("READ_DOWNLOAD_URL_FROM_SDCARD");
            if (property5 != null) {
                property5 = property5.trim();
            }
            if (Boolean.parseBoolean(property5)) {
                Logging.DEBUG_OUT("\tWill try to read DOWNLOAD_URL from SD card...");
                String readUrlFromFile = readUrlFromFile();
                if (readUrlFromFile != null) {
                    Logging.DEBUG_OUT("\t\tOK: DOWNLOAD_URL read from SD Card");
                    DOWNLOAD_URL = readUrlFromFile;
                } else {
                    Logging.DEBUG_OUT("\t\tFAILED. Will use DOWNLOAD_URL defined in config.properties");
                    DOWNLOAD_URL = properties.getProperty("DOWNLOAD_URL").trim();
                }
            } else {
                DOWNLOAD_URL = properties.getProperty("DOWNLOAD_URL").trim();
            }
            Logging.DEBUG_OUT("\tDOWNLOAD_URL: " + DOWNLOAD_URL);
            String property6 = properties.getProperty("UNCOMPRESS_ZIP_ON_DEVICE");
            if (property6 != null) {
                property6 = property6.trim();
            }
            UNCOMPRESS_ZIP_ON_DEVICE = Boolean.parseBoolean(property6);
            Logging.DEBUG_OUT("\tUNCOMPRESS_ZIP_ON_DEVICE: " + UNCOMPRESS_ZIP_ON_DEVICE);
            String property7 = properties.getProperty("CUSTOM_PROGRESS_BAR");
            if (property7 != null) {
                CUSTOM_PROGRESS_BAR = Boolean.parseBoolean(property7.trim());
            }
            Logging.DEBUG_OUT("\tCUSTOM_PROGRESS_BAR: " + CUSTOM_PROGRESS_BAR);
            String property8 = properties.getProperty("USE_OLD_PROGRESS_BAR");
            if (property8 != null) {
                USE_OLD_PROGRESS_BAR = Boolean.parseBoolean(property8.trim());
            }
            Logging.DEBUG_OUT("\tUSE_OLD_PROGRESS_BAR: " + USE_OLD_PROGRESS_BAR);
            String property9 = properties.getProperty("TOTAL_SPACE_MB_MIN");
            if (property9 != null) {
                try {
                    TOTAL_SPACE_MB_MIN = Integer.parseInt(property9.trim());
                } catch (Exception e2) {
                    Logging.DEBUG_OUT("\t" + e2.toString());
                }
            }
            Logging.DEBUG_OUT("\tTOTAL_SPACE_MB_MIN: " + TOTAL_SPACE_MB_MIN);
            if (TOTAL_SPACE_MB_MIN <= 0 || TOTAL_SPACE_MB <= TOTAL_SPACE_MB_MIN) {
                isDownloadRange = false;
            } else {
                isDownloadRange = true;
            }
            String property10 = properties.getProperty("DISABLE_3G");
            if (property10 != null) {
                DISABLE_3G = Boolean.parseBoolean(property10.trim());
            }
            Logging.DEBUG_OUT("\tDISABLE_3G: " + DISABLE_3G);
            String property11 = properties.getProperty("FORCE_WAKE_DURING_DOWNLOAD");
            if (property11 != null) {
                setForceWakeDuringDownload(Boolean.parseBoolean(property11.trim()));
            }
            Logging.DEBUG_OUT("\tFORCE_WAKE_DURING_DOWNLOAD: " + getForceWakeDuringDownload());
            String property12 = properties.getProperty("DO_NOT_OPEN_STORAGE_SETTINGS");
            if (property12 != null) {
                DO_NOT_OPEN_STORAGE_SETTINGS = Boolean.parseBoolean(property12.trim());
            }
            Logging.DEBUG_OUT("\tDO_NOT_OPEN_STORAGE_SETTINGS: " + DO_NOT_OPEN_STORAGE_SETTINGS);
            MIN_ASSET_VERSION_REQUIRED = properties.getProperty("MIN_ASSET_VERSION_REQUIRED");
            Logging.DEBUG_OUT("\tMIN_ASSET_VERSION_REQUIRED: " + MIN_ASSET_VERSION_REQUIRED);
            String property13 = properties.getProperty("DELETE_ASSETS_ON_UPDATE");
            if (property13 != null) {
                DELETE_ASSETS_ON_UPDATE = Boolean.parseBoolean(property13.trim());
            }
            Logging.DEBUG_OUT("\tDELETE_ASSETS_ON_UPDATE: " + DELETE_ASSETS_ON_UPDATE);
            String property14 = properties.getProperty("UNSAFE_ASSET_DELETION_ON_UPDATE");
            if (property14 != null) {
                UNSAFE_ASSET_DELETION_ON_UPDATE = Boolean.parseBoolean(property14.trim());
            }
            Logging.DEBUG_OUT("\tUNSAFE_ASSET_DELETION_ON_UPDATE: " + UNSAFE_ASSET_DELETION_ON_UPDATE);
            if (UNSAFE_ASSET_DELETION_ON_UPDATE) {
                Logging.DEBUG_OUT(" ");
                Logging.DEBUG_OUT("!!!!! WARNING !!!!!");
                Logging.DEBUG_OUT("\tUNSAFE_ASSET_DELETION_ON_UPDATE = true");
                Logging.DEBUG_OUT("\tADC will delete the entire download folder before an update");
                Logging.DEBUG_OUT("!!!!! WARNING !!!!!");
            }
            String property15 = properties.getProperty("REDOWNLOAD_ON_SCREEN_SIZE_CHANGE");
            if (property15 != null) {
                REDOWNLOAD_ON_SCREEN_SIZE_CHANGE = Boolean.parseBoolean(property15.trim());
            }
            Logging.DEBUG_OUT("\tREDOWNLOAD_ON_SCREEN_SIZE_CHANGE: " + REDOWNLOAD_ON_SCREEN_SIZE_CHANGE);
            String property16 = properties.getProperty("NUMBER_OF_HOURS_TO_UPDATE_CHECKING");
            if (property16 != null) {
                try {
                    NUMBER_OF_HOURS_TO_UPDATE_CHECKING = Integer.parseInt(property16.trim());
                    Logging.DEBUG_OUT("\tNUMBER_OF_HOURS_TO_UPDATE_CHECKING read from config.properties");
                } catch (Exception e3) {
                    Logging.DEBUG_OUT("\t" + e3.toString());
                }
            }
            Logging.DEBUG_OUT("\tNUMBER_OF_HOURS_TO_UPDATE_CHECKING: " + NUMBER_OF_HOURS_TO_UPDATE_CHECKING + " hours");
            String property17 = properties.getProperty("RETRIEVE_FULL_SCREEN_RESOLUTION");
            if (property17 != null) {
                RETRIEVE_FULL_SCREEN_RESOLUTION = Boolean.parseBoolean(property17.trim());
            }
            Logging.DEBUG_OUT("\tRETRIEVE_FULL_SCREEN_RESOLUTION: " + RETRIEVE_FULL_SCREEN_RESOLUTION);
            this.configLoaded = true;
            Logging.DEBUG_OUT(" ");
        } catch (Exception e4) {
            Logging.DEBUG_OUT("\tException while loading properties: config.properties" + e4);
        } finally {
            Logging.DEBUG_OUT(" ");
        }
    }

    private void loadOverrides() {
        InputStream inputStream = null;
        try {
            InputStream open = instance.getAssets().open(getResourcesPath() + "overrides.xml");
            Element documentElement = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(open).getDocumentElement();
            NodeList elementsByTagName = documentElement.getElementsByTagName("device");
            if (elementsByTagName != null && elementsByTagName.getLength() != 0) {
                for (int i = 0; i < elementsByTagName.getLength(); i++) {
                    Element element = (Element) elementsByTagName.item(i);
                    NodeList elementsByTagName2 = element.getElementsByTagName("resolution");
                    if (!(elementsByTagName2 == null || elementsByTagName2.getLength() == 0)) {
                        Element element2 = (Element) elementsByTagName2.item(0);
                        this.overrideDevices.add(new Device(element.getAttribute("name"), Integer.parseInt(element2.getAttribute("width")), Integer.parseInt(element2.getAttribute("height"))));
                    }
                }
                NodeList elementsByTagName3 = documentElement.getElementsByTagName("fallback");
                if (elementsByTagName3 != null && elementsByTagName3.getLength() != 0) {
                    String str = "";
                    Element element3 = (Element) elementsByTagName3.item(0);
                    NodeList elementsByTagName4 = element3.getElementsByTagName("forceDevice");
                    if (elementsByTagName4 != null && elementsByTagName4.getLength() > 0) {
                        str = ((Element) elementsByTagName4.item(0)).getAttribute("name");
                    }
                    NodeList elementsByTagName5 = element3.getElementsByTagName("resolution");
                    if (elementsByTagName5 != null && elementsByTagName5.getLength() != 0) {
                        Element element4 = (Element) elementsByTagName5.item(0);
                        this.deviceFallback = new Device(str, Integer.parseInt(element4.getAttribute("width")), Integer.parseInt(element4.getAttribute("height")));
                        if (open != null) {
                            try {
                                open.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (open != null) {
                        try {
                            open.close();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                } else if (open != null) {
                    try {
                        open.close();
                    } catch (Exception e3) {
                        e3.printStackTrace();
                    }
                }
            } else if (open != null) {
                try {
                    open.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
        } catch (FileNotFoundException e5) {
            if (0 != 0) {
                try {
                    inputStream.close();
                } catch (Exception e6) {
                    e6.printStackTrace();
                }
            }
        } catch (Exception e7) {
            e7.printStackTrace();
            if (0 != 0) {
                try {
                    inputStream.close();
                } catch (Exception e8) {
                    e8.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    inputStream.close();
                } catch (Exception e9) {
                    e9.printStackTrace();
                }
            }
            throw th;
        }
    }

    private void printADCLibInfo() {
        Logging.DEBUG_OUT(" ");
        Logging.DEBUG_OUT("[ADC lib info]");
        if (Constants.ADC_BUILD_LOCAL.equalsIgnoreCase("true")) {
            Logging.DEBUG_OUT("\t[WARNING] This version of ADC was built locally and should not be used in production.");
        }
        Logging.DEBUG_OUT("\tADC build version: @ADC_BUILD_VERSION_DYNAMIC_VALUE@");
        Logging.DEBUG_OUT("\tADC build time: @ADC_BUILD_TIME_DYNAMIC_VALUE@");
        Logging.DEBUG_OUT(" ");
    }

    private void printDeviceAndAppInfo() {
        Logging.DEBUG_OUT(" ");
        Logging.DEBUG_OUT("[Device/App info]");
        Logging.DEBUG_OUT("\tDevice: " + getDeviceString());
        Logging.DEBUG_OUT("\tBrand: " + getBrand());
        Logging.DEBUG_OUT("\tAndroid Unique ID: " + getAndroidUniqueId());
        Logging.DEBUG_OUT("\tApplication name: " + getApplicationName());
        Logging.DEBUG_OUT("\tAPK version: " + getAPKVersion());
        Logging.DEBUG_OUT(" ");
    }

    private String readUrlFromFile() {
        try {
            File file = new File(this.assetManager.getFilePath(DOWNLOAD_URL_CONFIG_FILE));
            if (file.exists()) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String readLine = bufferedReader.readLine();
                bufferedReader.close();
                return readLine;
            }
            Logging.DEBUG_OUT("\t\tDownloadURL.indicate does not exist.");
            return "";
        } catch (Exception e) {
            Logging.DEBUG_OUT("\t\tException while reading DownloadURL.indicate: " + e);
        }
        return "";
    }

    private JSONObject sendHttpPost(String str, JSONObject jSONObject) {
        InputStream inputStream = null;
        try {
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(defaultHttpClient.getParams(), 10000);
            HttpPost httpPost = new HttpPost(str);
            httpPost.setEntity(new StringEntity(jSONObject.toString()));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpEntity entity = defaultHttpClient.execute(httpPost).getEntity();
            if (entity != null) {
                InputStream content = entity.getContent();
                JSONObject jSONObject2 = new JSONObject(convertStreamToString(content));
                unknownHostExceptionTryAgain = true;
                if (content == null) {
                    return jSONObject2;
                }
                try {
                    content.close();
                    return jSONObject2;
                } catch (Exception e) {
                    return jSONObject2;
                }
            } else {
                return null;
            }
        } catch (Exception e3) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred in sendHttpPost while trying to obtain the file list.");
            Logging.DEBUG_OUT_STACK(e3);
            Logging.DEBUG_OUT("URL: " + str);
            Logging.DEBUG_OUT("jsonObjSend: " + jSONObject);
            if (unknownHostExceptionTryAgain) {
                Logging.DEBUG_OUT("Trying sendHttpPost again...");
                unknownHostExceptionTryAgain = false;
                JSONObject sendHttpPost = sendHttpPost(str, jSONObject);
                return sendHttpPost;
            } else {
                Logging.DEBUG_OUT("Already tried sendHttpPost after failure.");
            }
        } catch (Throwable th) {
            throw th;
        }
        return new JSONObject();
    }

    private void setAssetPathAux(String str, boolean z) {
        String str2 = "";
        String str3 = "";
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String absolutePath2 = this.mContext.getFilesDir().getAbsolutePath();
        String str4 = null;
        if (USE_INTERNAL_STORAGE) {
            str2 = str2 + this.mContext.getFilesDir().getAbsolutePath();
            str3 = str3 + absolutePath2;
        } else if (z) {
            str2 = str2 + absolutePath;
            str3 = str3 + absolutePath2;
        } else {
            Logging.DEBUG_OUT("User entered an absolute path.");
            if (str.startsWith(absolutePath)) {
                USE_INTERNAL_STORAGE = false;
                str4 = str.replaceFirst(absolutePath, absolutePath2);
            } else if (str.startsWith(absolutePath2)) {
                USE_INTERNAL_STORAGE = true;
                str4 = str.replaceFirst(absolutePath2, absolutePath);
            }
        }
        if (str != null) {
            str2 = str2 + str;
        }
        this.assetManager.setAssetPath(str2);
        if (ALTERNATIVE_DATA_FOLDER) {
            String str5 = str4 == null ? str3 + str : str4;
            this.assetManager.setAlternativeAssetPath(str5);
            Logging.DEBUG_OUT("\tsetAlternativeAssetPath(), mAlternativeAssetPath = " + str5);
        }
        Logging.DEBUG_OUT("\tsetAssetPath(), mAssetPath = " + str2);
    }

    public static void setFlagLastReportDownload(boolean z) {
        downloadProgress.setFlagLastReportDownload(z);
    }

    public static void setForceWakeDuringDownload(boolean z) {
        FORCE_WAKE_DURING_DOWNLOAD = z;
    }

    private boolean shouldSkipUpdateCheck() {
        Logging.DEBUG_OUT("DownloadActivityInternal shouldSkipUpdateCheck()");
        if (NUMBER_OF_HOURS_TO_UPDATE_CHECKING > 0) {
            Properties loadAssetInfo = this.assetManager.loadAssetInfo();
            if (loadAssetInfo != null) {
                String property = loadAssetInfo.getProperty("updateCheckLastTime");
                if (property != null) {
                    try {
                        long parseLong = Long.parseLong(property);
                        Logging.DEBUG_OUT("Last time checked for update: " + parseLong);
                        long time = new Date().getTime();
                        Logging.DEBUG_OUT("Current time: " + time);
                        long j = time - parseLong;
                        if (j < 0) {
                            Logging.DEBUG_OUT("Time travel not allowed!");
                            return false;
                        }
                        int i = (int) (j / 3600000);
                        Logging.DEBUG_OUT("Number of hours since last update checking: " + i);
                        Logging.DEBUG_OUT("Number of hours needed: " + NUMBER_OF_HOURS_TO_UPDATE_CHECKING);
                        return i < NUMBER_OF_HOURS_TO_UPDATE_CHECKING;
                    } catch (Exception e) {
                        Logging.DEBUG_OUT_STACK(e);
                        return false;
                    }
                } else {
                    Logging.DEBUG_OUT("Property updateCheckLastTime not found.");
                    return false;
                }
            } else {
                Logging.DEBUG_OUT("AssetInfo file could not be read.");
                return false;
            }
        } else {
            Logging.DEBUG_OUT("NUMBER_OF_HOURS_TO_UPDATE_CHECKING not defined or invalid.");
            return false;
        }
    }

    private boolean startDownloadingFiles(DownloadFileData[] downloadFileDataArr) {
        if (!isConnected()) {
            Logging.DEBUG_OUT("[ERROR] Connection unavailable");
            recordError(-16);
            return false;
        }
        boolean z = false;
        int i = 0;
        while (true) {
            try {
                if (i >= downloadFileDataArr.length) {
                    break;
                }
                DownloadFileData downloadFileData2 = downloadFileDataArr[i];
                if (downloadFileData2.getType() == 1) {
                    if (!UNCOMPRESS_ZIP_ON_DEVICE) {
                        Logging.DEBUG_OUT("File to download (ZIP): " + downloadFileData2.getFileName());
                        Logging.DEBUG_OUT("Files will be downloaded using a ZipInputStream: will NOT be ale to resume");
                        Hashtable<String, Long> checksumsHashtable = getChecksumsHashtable(downloadFileData2.getFileName(), downloadFileDataArr);
                        if (checksumsHashtable == null) {
                            Logging.DEBUG_OUT("[ERROR] Unable to download required checksums file: " + getMatchingChecksumFile(downloadFileData2.getFileName(), this.downloadFileData).getFileName() + " (" + resolution + ")");
                            recordError(-13);
                            return false;
                        } else if (!this.assetManager.isFileDownloaded(downloadFileData2.getFileName())) {
                            Logging.DEBUG_OUT("Downloading file: " + downloadFileData2.getFileName());
                            z = downloadAndValidateZipFile(downloadFileData2, checksumsHashtable);
                            if (!z) {
                                break;
                            }
                            this.assetManager.saveState(downloadFileData2.getFileName() + "\t" + downloadFileData2.getVersion(), null);
                            downloadProgress.setCurrentFile("n_" + downloadFileData2.getFileName(), (long) downloadFileData2.getSize());
                            downloadProgress.fillCurrentFileDownload(false);
                        } else {
                            Logging.DEBUG_OUT("File already downloaded:" + downloadFileData2.getFileName());
                            downloadProgress.setCurrentFile("n_" + downloadFileData2.getFileName(), (long) downloadFileData2.getSize());
                            downloadProgress.fillCurrentFileDownload(true);
                            setFlagLastReportDownload(false);
                            z = true;
                        }
                    } else {
                        Logging.DEBUG_OUT("File to download (ZIP): " + downloadFileData2.getFileName());
                        Logging.DEBUG_OUT("Zip will be downloaded and uncompressed on device: resume is possible");
                        Hashtable<String, Long> checksumsHashtable2 = getChecksumsHashtable(downloadFileData2.getFileName(), downloadFileDataArr);
                        if (checksumsHashtable2 == null) {
                            Logging.DEBUG_OUT("[ERROR] Unable to download required checksums file: " + getMatchingChecksumFile(downloadFileData2.getFileName(), this.downloadFileData).getFileName() + " (" + resolution + ")");
                            recordError(-13);
                            return false;
                        } else if (!this.assetManager.isFileDownloaded(downloadFileData2.getFileName())) {
                            Logging.DEBUG_OUT("Downloading file: " + downloadFileData2.getFileName());
                            z = false;
                            if (!downloadOtherFile(downloadFileData2, null)) {
                                break;
                            }
                            z = extractAndValidateFilesFromZip(downloadFileData2.getFileName(), checksumsHashtable2);
                            new File(this.assetManager.getFilePath(downloadFileData2.getFileName())).delete();
                            if (!z) {
                                recordError(-12);
                                break;
                            }
                            this.assetManager.saveState(downloadFileData2.getFileName() + "\t" + downloadFileData2.getVersion(), null);
                        } else {
                            Logging.DEBUG_OUT("File already downloaded:" + downloadFileData2.getFileName());
                            z = true;
                            downloadProgress.setCurrentFile("n_" + downloadFileData2.getFileName(), (long) downloadFileData2.getSize());
                            downloadProgress.fillCurrentFileDownload(true);
                            setFlagLastReportDownload(false);
                        }
                    }
                } else if (downloadFileData2.getType() == 3) {
                    Logging.DEBUG_OUT("File to download (NON-ZIP): " + downloadFileData2.getFileName());
                    Hashtable<String, Long> checksumsHashtable3 = getChecksumsHashtable(downloadFileData2.getFileName(), downloadFileDataArr);
                    if (checksumsHashtable3 == null) {
                        Logging.DEBUG_OUT("[ERROR] Unable to download required checksums file: " + getMatchingChecksumFile(downloadFileData2.getFileName(), this.downloadFileData).getFileName() + " (" + resolution + ")");
                        recordError(-13);
                        return false;
                    } else if (!this.assetManager.isFileDownloaded(downloadFileData2.getFileName())) {
                        Logging.DEBUG_OUT("Downloading file: " + downloadFileData2.getFileName());
                        z = downloadOtherFile(downloadFileData2, checksumsHashtable3);
                        if (!z) {
                            break;
                        }
                        this.assetManager.saveState(downloadFileData2.getFileName() + "\t" + downloadFileData2.getVersion(), null);
                    } else {
                        Logging.DEBUG_OUT("File already downloaded:" + downloadFileData2.getFileName());
                        z = true;
                        downloadProgress.setCurrentFile("n_" + downloadFileData2.getFileName(), (long) downloadFileData2.getSize());
                        downloadProgress.fillCurrentFileDownload(true);
                        setFlagLastReportDownload(false);
                    }
                } else {
                    continue;
                }
                i++;
            } catch (Exception e) {
                Logging.DEBUG_OUT("[ERROR] An exception occurred while downloading files: " + e);
                Logging.DEBUG_OUT_STACK(e);
                return false;
            }
        }
        if (z) {
            this.assetManager.saveStateDownloadFinished();
            this.assetManager.saveDownloadListFile(downloadFileDataArr);
            this.assetManager.saveAssetInfo(generateAssetInfo());
            this.percent_downloaded = 100;
            Logging.DEBUG_OUT("[FINISHED] All files downloaded successfully.");
            return z;
        }
        Logging.DEBUG_OUT("[ERROR] Assets download failed.");
        return z;
    }

    private void unregisterWifiReceiver() {
        try {
            if (this.wifiReceiver != null) {
                instance.unregisterReceiver(this.wifiReceiver);
            }
        } catch (Exception e) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred while unregistering WifiReceiver.");
            Logging.DEBUG_OUT_STACK(e);
        } finally {
            this.wifiReceiver = null;
        }
    }

    private int updateDownloadFilesData(boolean z) {
        String brand = getBrand();
        resolution = getResolution();
        String deviceString = getDeviceString();
        if (z) {
            if (this.deviceFallback == null) {
                return ERROR_UNSUPPORTED_DEVICE;
            }
            resolution = this.deviceFallback.getResolutionString();
            String name = this.deviceFallback.getName();
            if (!name.equals("")) {
                deviceString = name;
            }
        }
        String str = DOWNLOAD_URL;
        Logging.DEBUG_OUT(" ");
        Logging.DEBUG_OUT("[OVERRIDE DEVICE DATA]");
        Logging.DEBUG_OUT("Checking if device data have been overridden in onRetrievedDeviceData()...");
        try {
            DeviceData deviceData = new DeviceData();
            deviceData.setDeviceName(deviceString);
            deviceData.setBrandName(brand);
            deviceData.setResolution(width, height);
            deviceData.setGlExtensions(this.glExtensions);
            ((IDeviceData) instance).onRetrievedDeviceData(deviceData);
            int i = 0;
            if (!deviceString.equals(deviceData.getDeviceName())) {
                i = 0 + 1;
                Logging.DEBUG_OUT(i + ") Device name was overridden");
                Logging.DEBUG_OUT("\tFrom: " + deviceString);
                Logging.DEBUG_OUT("\tTo: " + deviceData.getDeviceName());
                deviceString = deviceData.getDeviceName();
            }
            if (!brand.equals(deviceData.getBrandName())) {
                i++;
                Logging.DEBUG_OUT(i + ") Brand name was overridden");
                Logging.DEBUG_OUT("\tFrom: " + brand);
                Logging.DEBUG_OUT("\tTo: " + deviceData.getBrandName());
                brand = deviceData.getBrandName();
            }
            width = deviceData.getWidth();
            height = deviceData.getHeight();
            String str2 = deviceData.getWidth() + "x" + deviceData.getHeight();
            if (!resolution.equals(str2)) {
                i++;
                Logging.DEBUG_OUT(i + ") Resolution was overridden");
                Logging.DEBUG_OUT("\tFrom: " + resolution);
                Logging.DEBUG_OUT("\tTo: " + str2);
                resolution = str2;
            }
            if (!this.glExtensions.equals(deviceData.getGlExtensions())) {
                i++;
                Logging.DEBUG_OUT(i + ") GL Extensions was overridden");
                Logging.DEBUG_OUT("\tFrom: " + this.glExtensions);
                Logging.DEBUG_OUT("\tTo: " + deviceData.getGlExtensions());
                this.glExtensions = deviceData.getGlExtensions();
            }
            if (i == 0) {
                Logging.DEBUG_OUT("No device data overridden.");
            }
        } catch (ClassCastException e) {
            Logging.DEBUG_OUT("onRetrievedDeviceData() not implemented.");
        } finally {
            Logging.DEBUG_OUT(" ");
        }
        String str3 = str + "androidContentWS/cms/android/gameasset/application/" + MASTER_SELL_ID + "/pId/" + PRODUCT_ID + "/version/" + getAPKVersion() + "/resolution/" + resolution + "/glext/device/" + deviceString + "/brand/" + brand + "?language=" + this.mLocale;
        Logging.DEBUG_OUT("[SENDING REQUEST]");
        Logging.DEBUG_OUT("DOWNLOAD DATA URL\n" + str3);
        Logging.DEBUG_OUT("TYPE: POST");
        JSONObject jSONObject = new JSONObject();
        this.downloadFileData = null;
        totalDownloadSizeMB = 0;
        try {
            Logging.DEBUG_OUT("PARAMETERS: ");
            jSONObject.put("glext", this.glExtensions);
            Logging.DEBUG_OUT("\tglext: " + this.glExtensions);
            Logging.DEBUG_OUT("ADDITIONAL INFORMATION:");
            Logging.DEBUG_OUT("\tProduct ID:" + PRODUCT_ID);
            Logging.DEBUG_OUT("\tSell ID:" + MASTER_SELL_ID);
            Logging.DEBUG_OUT("\tBrand:" + brand);
            Logging.DEBUG_OUT("\tDevice:" + deviceString);
            Logging.DEBUG_OUT("\tResolution:" + resolution);
            Logging.DEBUG_OUT("\tLanguage:" + this.mLocale);
            JSONObject sendHttpPost = sendHttpPost(str3, jSONObject);
            if (sendHttpPost == null) {
                return -15;
            }
            Logging.DEBUG_OUT("[JSON RESULT]\n" + sendHttpPost);
            if (sendHttpPost.has("responseCode")) {
                int i2 = sendHttpPost.getInt("responseCode");
                return i2 == 5001 ? ERROR_UNSUPPORTED_DEVICE : i2;
            }
            Logging.DEBUG_OUT("FILES: ");
            JSONArray jSONArray = sendHttpPost.getJSONArray("files");
            for (int i3 = 0; i3 < jSONArray.length(); i3++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i3);
                Logging.DEBUG_OUT("--------------------------------------");
                Logging.DEBUG_OUT("Filename:" + jSONObject2.getString("fileName"));
                Logging.DEBUG_OUT("Size (bytes): " + jSONObject2.getInt("fileSize"));
                Logging.DEBUG_OUT("Version: " + jSONObject2.getString("version"));
                Logging.DEBUG_OUT("Language: " + jSONObject2.getString("language"));
                Logging.DEBUG_OUT("URL: " + jSONObject2.getString("fileURL"));
                Logging.DEBUG_OUT("--------------------------------------");
            }
            this.downloadFileData = new DownloadFileData[jSONArray.length()];
            for (int i4 = 0; i4 < jSONArray.length(); i4++) {
                JSONObject jSONObject3 = jSONArray.getJSONObject(i4);
                String string = jSONObject3.getString("fileName");
                this.downloadFileData[i4] = new DownloadFileData(string, jSONObject3.getInt("fileSize"), jSONObject3.getString("version"), jSONObject3.getString("language"), jSONObject3.getString("fileURL"), 3);
                if (MIN_ASSET_VERSION_REQUIRED != null) {
                    if (this.assetManager.isVersionLower(this.downloadFileData[i4].getVersion(), MIN_ASSET_VERSION_REQUIRED)) {
                        return -17;
                    }
                }
                if (string.endsWith(".zip")) {
                    this.downloadFileData[i4].setType(1);
                } else if (string.endsWith(".checksums")) {
                    this.downloadFileData[i4].setType(2);
                }
            }
            try {
                spaceNeededToDownload = sendHttpPost.getLong("totalUncompressFilesSize");
                spaceNeededToDownload += getTotalDownloadSizeForNonZipFiles(this.downloadFileData);
            } catch (JSONException e2) {
                Logging.DEBUG_OUT(e2.toString());
            }
            if (this.downloadFileData != null) {
                totalDownloadSizeMB = getTotalDownloadSize(this.downloadFileData);
            }
            return 0;
        } catch (Exception e3) {
            Logging.DEBUG_OUT("[ERROR]An exception occurred in updateDownloadFilesData():");
            Logging.DEBUG_OUT_STACK(e3);
            return -15;
        }
    }

    /* access modifiers changed from: protected */
    public long calculateDownloaded(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return 0;
        }
        long j = 0;
        for (int i = 0; i < listFiles.length; i++) {
            j += listFiles[i].isDirectory() ? calculateDownloaded(listFiles[i]) : listFiles[i].length();
        }
        return j;
    }

    public boolean canFindHostIP() {
        try {
            Logging.DEBUG_OUT("Getting host name from URL: " + DOWNLOAD_URL);
            Logging.DEBUG_OUT("Host name: " + new URL(DOWNLOAD_URL).getHost());
            try {
                BasicHttpParams basicHttpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(basicHttpParams, 5000);
                HttpConnectionParams.setSoTimeout(basicHttpParams, 5000);
                return new DefaultHttpClient(basicHttpParams).execute(new HttpGet(DOWNLOAD_URL)).getStatusLine().getStatusCode() == 200;
            } catch (Exception e) {
                Logging.DEBUG_OUT_STACK(e);
                return false;
            }
        } catch (MalformedURLException e2) {
            Logging.DEBUG_OUT("Not found: URL is malformed");
            return false;
        }
    }

    public boolean canOpenStorageSettings() {
        return !DO_NOT_OPEN_STORAGE_SETTINGS;
    }

    public boolean checkForUpdates() {
        if (!ALTERNATIVE_DATA_FOLDER) {
            return this.assetManager.checkForUpdates(this.downloadFileData);
        }
        this.assetManager.useAlternativeAssetPath(false);
        if (!this.assetManager.assetsFoundLocally() || this.assetManager.checkForUpdates(this.downloadFileData)) {
            this.assetManager.useAlternativeAssetPath(true);
            if (!this.assetManager.assetsFoundLocally()) {
                Logging.DEBUG_OUT("[ERROR] Something very wrong happened: assets have been found previously, but cannot be found anymore.");
                this.assetManager.useAlternativeAssetPath(false);
                return true;
            } else if (this.assetManager.checkForUpdates(this.downloadFileData)) {
                Logging.DEBUG_OUT("Assets on alternative location and update found.");
                return true;
            } else {
                Logging.DEBUG_OUT("Assets on alternative location and no update NOT found.");
                return false;
            }
        } else {
            Logging.DEBUG_OUT("Assets on main location and update NOT found.");
            return false;
        }
    }

    public boolean checkLocalAssetVersion() {
        if (MIN_ASSET_VERSION_REQUIRED != null) {
            return this.assetManager.isAssetVersionCompatible(MIN_ASSET_VERSION_REQUIRED);
        }
        return true;
    }

    public boolean checkScreenSizeChange() {
        if (!REDOWNLOAD_ON_SCREEN_SIZE_CHANGE) {
            return false;
        }
        Logging.DEBUG_OUT("Checking for screen size change...");
        Properties loadAssetInfo = this.assetManager.loadAssetInfo();
        if (loadAssetInfo != null) {
            String property = loadAssetInfo.getProperty("width");
            String property2 = loadAssetInfo.getProperty("height");
            if (property == null || property2 == null) {
                Logging.DEBUG_OUT("No information found.");
                return false;
            }
            try {
                int parseInt = Integer.parseInt(property);
                int parseInt2 = Integer.parseInt(property2);
                Logging.DEBUG_OUT("Device width: " + width);
                Logging.DEBUG_OUT("Assets width: " + parseInt);
                Logging.DEBUG_OUT("Device height: " + height);
                Logging.DEBUG_OUT("Assets height: " + parseInt2);
                if (parseInt == width && parseInt2 == height) {
                    Logging.DEBUG_OUT("NO screen change detected.");
                    return false;
                }
                Logging.DEBUG_OUT("Screen change detected.");
                return true;
            } catch (Exception e) {
                Logging.DEBUG_OUT("Invalid information.");
                Logging.DEBUG_OUT_STACK(e);
                return false;
            }
        } else {
            Logging.DEBUG_OUT("No information found.");
            return false;
        }
    }

    public void checkServerContent(Boolean bool) {
        int updateDownloadFilesData = getMainActivity().updateDownloadFilesData(false);
        if ((updateDownloadFilesData == 5001 || updateDownloadFilesData == 5002) && this.deviceFallback != null) {
            updateDownloadFilesData = getMainActivity().updateDownloadFilesData(true);
        }
        if (this.downloadFileData != null && spaceNeededToDownload > 0) {
            spaceNeededToDownload -= this.assetManager.getTotalSize(this.assetManager.getDownloadList(this.downloadFileData));
        }
        boolean z = false;
        if (ALTERNATIVE_DATA_FOLDER) {
            this.assetManager.useAlternativeAssetPath(false);
            if (this.assetManager.assetsFoundLocally()) {
                z = true;
            } else {
                this.assetManager.useAlternativeAssetPath(true);
                if (this.assetManager.assetsFoundLocally()) {
                    z = true;
                } else {
                    this.assetManager.useAlternativeAssetPath(false);
                }
            }
        } else {
            z = this.assetManager.assetsFoundLocally();
        }
        if (!z) {
            Logging.DEBUG_OUT("Assets not found on the device. ADC will try to download from the server.");
            if (updateDownloadFilesData != 0) {
                recordError(updateDownloadFilesData);
                if (updateDownloadFilesData == 5001) {
                    Logging.DEBUG_OUT("[ERROR] Unsupported device: unable to find assets");
                    Logging.DEBUG_OUT("\tfor resolution: " + resolution);
                    Logging.DEBUG_OUT("\ton server: " + DOWNLOAD_URL);
                    setState(12);
                    return;
                }
                if (updateDownloadFilesData == -15) {
                    Logging.DEBUG_OUT("[ERROR] Failed to retrieve download file list");
                    if (bool.booleanValue()) {
                        Logging.DEBUG_OUT("Ignoring ERROR_FILE_LIST_RETRIEVE_FAILED and going to STATE_SHOW_WIFI_DIALOG.");
                        setState(6);
                        return;
                    }
                } else {
                    Logging.DEBUG_OUT("[ERROR] Error from Server: " + updateDownloadFilesData);
                }
                this.serverErrorView.setErrorCode(updateDownloadFilesData);
                setState(13);
            } else if (!chooseAvailableMemory()) {
                setState(4);
            } else if (getState() != 2) {
                setState(1);
            }
        } else {
            Logging.DEBUG_OUT("checkServerContent(): assets found on the device.");
            if (updateDownloadFilesData == 0) {
                long time = new Date().getTime();
                Logging.DEBUG_OUT("Saving update checking timestamp: " + time);
                Properties loadAssetInfo = this.assetManager.loadAssetInfo();
                if (loadAssetInfo == null) {
                    loadAssetInfo = new Properties();
                }
                loadAssetInfo.setProperty("updateCheckLastTime", "" + time);
                this.assetManager.saveAssetInfo(loadAssetInfo);
                setState(8);
                return;
            }
            if (updateDownloadFilesData == -15) {
                Logging.DEBUG_OUT("[ERROR] Failed to retrieve download file list.");
            } else if (updateDownloadFilesData == 5001 || updateDownloadFilesData == 5002) {
                Logging.DEBUG_OUT("[ERROR] Assets found on the device but server returned error code " + updateDownloadFilesData + " while checking for assets.");
            }
            setState(11);
        }
    }

    public boolean chooseAvailableMemory() {
        if (!ALTERNATIVE_DATA_FOLDER) {
            return isSpaceAvailableForDownload();
        }
        if (isSpaceAvailableForDownload()) {
            this.assetManager.useAlternativeAssetPath(false);
            Logging.DEBUG_OUT("Memory space is available in main location.");
            return true;
        } else if (isSpaceAvailableForAlternativeDownload()) {
            this.assetManager.useAlternativeAssetPath(true);
            Logging.DEBUG_OUT("Memory space is available only in alternative location.");
            return true;
        } else {
            Logging.DEBUG_OUT("Memory space is not available.");
            return false;
        }
    }

    public void deleteAssets() {
        if (UNSAFE_ASSET_DELETION_ON_UPDATE) {
            this.assetManager.deleteEntireDownloadFolder();
        } else {
            this.assetManager.deleteAssets();
        }
    }

    public void destroyDownloadActvity() {
        if (getBackgroundBitmap() != null) {
            getBackgroundBitmap().recycle();
            setBackgroundBitmap(null);
        }
        unregisterWifiReceiver();
        cleanStates();
        isInitialized = false;
        instance = null;
        mMainActivity = null;
        downloadProgress = null;
        this.assetManager = null;
        pState = -1;
    }

    /* access modifiers changed from: protected */
    public String getAPKVersion() {
        try {
            return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    /* access modifiers changed from: protected */
    public String getAndroidUniqueId() {
        String string = Settings.Secure.getString(instance.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = ((TelephonyManager) instance.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        return string != null ? "androidId=" + string + "&imei=" + deviceId : "imei=" + deviceId;
    }

    public String getApplicationName() {
        try {
            return instance.getString(instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    public String getAvailableSpaceForDownload() {
        return "" + ((spaceAvailableToDownload / 1024) / 1024);
    }

    public Bitmap getBackgroundBitmap() {
        return this.bmpBg;
    }

    /* access modifiers changed from: protected */
    public String getBrand() {
        String str = Build.BRAND;
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }

    /* access modifiers changed from: protected */
    public String getDeviceString() {
        String str = getManufacturer() + "-" + getModel();
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            Logging.DEBUG_OUT("getDeviceString Encode Exception:" + e);
            return str;
        }
    }

    public int getLastError() {
        if (mErrorList.size() == 0) {
            return 0;
        }
        return mErrorList.get(mErrorList.size() - 1).intValue();
    }

    /* access modifiers changed from: protected */
    public String getManufacturer() {
        try {
            return Build.MANUFACTURER;
        } catch (Exception e) {
            Logging.DEBUG_OUT("getManufacturer Exception:" + e);
            return "Unknown";
        }
    }

    /* access modifiers changed from: protected */
    public String getModel() {
        try {
            return Build.MODEL;
        } catch (Exception e) {
            Logging.DEBUG_OUT("getModel Exception:" + e);
            return "Unknown";
        }
    }

    public int getNumErrors() {
        return mErrorList.size();
    }

    public int getPercentDownloaded() {
        double sizeDownloaded = (double) (((float) ((getSizeDownloaded() / 1024) / 1024)) / ((float) totalDownloadSizeMB));
        if (this.percent_downloaded < 100) {
            this.percent_downloaded = (int) (100.0d * sizeDownloaded);
        } else {
            this.percent_downloaded = 100;
        }
        return this.percent_downloaded;
    }

    /* access modifiers changed from: protected */
    public int getPreviousState() {
        return pStatePrev;
    }

    public String getRequiredSpaceForDownload() {
        return spaceNeededToDownload > 0 ? "" + ((spaceNeededToDownload / 1024) / 1024) : "" + TOTAL_SPACE_MB;
    }

    public String getSpaceRangeForDownload() {
        return spaceNeededToDownload > 0 ? "" + ((spaceNeededToDownload / 1024) / 1024) : !isDownloadRange ? "" + TOTAL_SPACE_MB : "" + TOTAL_SPACE_MB_MIN + "-" + TOTAL_SPACE_MB;
    }

    public int getState() {
        return pState;
    }

    public String getStateName() {
        return pState != -1 ? STATE_STRINGS[pState] : "STATE_INVALID";
    }

    public int getTotalDownloadSize(DownloadFileData[] downloadFileDataArr) {
        totalDownloadSizeMB = 0;
        int i = 0;
        for (DownloadFileData downloadFileData2 : downloadFileDataArr) {
            try {
                i += downloadFileData2.getSize();
            } catch (Exception e) {
                Logging.DEBUG_OUT("[ERROR] An exception occurred while calculating download size:" + e);
            }
        }
        totalDownloadSizeMB = (i / 1024) / 1024;
        return totalDownloadSizeMB;
    }

    public long getTotalDownloadSizeForNonZipFiles(DownloadFileData[] downloadFileDataArr) {
        long j = 0;
        for (int i = 0; i < downloadFileDataArr.length; i++) {
            try {
                if (downloadFileDataArr[i].getType() != 1) {
                    j += (long) downloadFileDataArr[i].getSize();
                }
            } catch (Exception e) {
                Logging.DEBUG_OUT("[ERROR] An exception occurred while calculating download size for non-zip files:");
                Logging.DEBUG_OUT_STACK(e);
            }
        }
        return j;
    }

    public WifiReceiver getWifiReceiver() {
        return this.wifiReceiver;
    }

    public void init(Activity activity, IDownloadActivity iDownloadActivity, Context context, Object obj) {
        Logging.DEBUG_OUT("Calling: DownloadActivityInternal init()");
        this.mContext = context;
        if (instance == null) {
            instance = activity;
            this.mDownloadActivity = iDownloadActivity;
        }
        printADCLibInfo();
        printDeviceAndAppInfo();
        loadConfigProperties();
        loadOverrides();
        checkPermissions();
        checkLanguageChange();
        checkBackgroundImage();
        if (downloadProgress == null) {
            downloadProgress = new DownloadProgress();
        }
        if (obj != null) {
            try {
                if (obj instanceof GL10) {
                    this.glExtensions = ((GL10) obj).glGetString(7939);
                } else if (obj instanceof GL11) {
                    this.glExtensions = ((GL11) obj).glGetString(7939);
                }
            } catch (Exception e) {
                Logging.DEBUG_OUT("[ERROR] An exception occurred while trying to get GL Extensions:" + e);
            }
        }
        if (this.wifiReceiver == null) {
            this.wifiReceiver = new WifiReceiver();
            instance.registerReceiver(this.wifiReceiver, new IntentFilter("android.net.wifi.RSSI_CHANGED"));
            this.wifiReceiver.updateWifiInfo();
        }
        if (!isInitialized) {
            isInitialized = true;
            boolean z = false;
            if (ALTERNATIVE_DATA_FOLDER) {
                this.assetManager.useAlternativeAssetPath(false);
                if (this.assetManager.assetsFoundLocally()) {
                    z = true;
                } else {
                    this.assetManager.useAlternativeAssetPath(true);
                    if (this.assetManager.assetsFoundLocally()) {
                        z = true;
                    } else {
                        this.assetManager.useAlternativeAssetPath(false);
                    }
                }
            } else {
                z = this.assetManager.assetsFoundLocally();
            }
            ADCTelemetry.getInstance().onCreate(this.assetManager.getAssetPath());
            if (!z) {
                setState(14);
            } else if (shouldSkipUpdateCheck()) {
                setState(11);
            } else {
                setState(14);
            }
        } else {
            setState(pState);
        }
    }

    /* access modifiers changed from: protected */
    public void initScreens(Context context) {
        if (this.downloadMsgView == null) {
            this.downloadMsgView = new DownloadMsgView(context);
        }
        if (this.showWifiView == null) {
            this.showWifiView = new ShowWifiView(context);
        }
        if (this.networkUnavailableView == null) {
            this.networkUnavailableView = new NetworkUnavailableView(context);
        }
        if (this.downloadProgressView == null) {
            this.downloadProgressView = new DownloadProgressView(context);
        }
        if (this.downloadFailedView == null) {
            this.downloadFailedView = new DownloadFailedView(context);
        }
        if (this.spaceUnavailableView == null) {
            this.spaceUnavailableView = new SpaceUnavailableView(context);
        }
        if (this.checkUpdatesView == null) {
            this.checkUpdatesView = new CheckUpdatesView(context);
        }
        if (this.updatesFoundView == null) {
            this.updatesFoundView = new UpdatesFoundView(context);
        }
        if (this.show3GView == null) {
            this.show3GView = new Show3GView(context);
        }
        if (this.showBGView == null) {
            this.showBGView = new ShowBGView(context);
        }
        if (this.unSupportedDeviceView == null) {
            this.unSupportedDeviceView = new UnSupportedDeviceView(context);
        }
        if (this.serverErrorView == null) {
            this.serverErrorView = new ServerErrorView(context);
        }
        if (this.contactingServerView == null) {
            this.contactingServerView = new ContactingServerView(context);
        }
        if (this.checkingHostIpView == null) {
            this.checkingHostIpView = new CheckingHostIpView(context);
        }
        if (this.invalidAssetVersionView == null) {
            this.invalidAssetVersionView = new InvalidAssetVersionView(context);
        }
        if (this.deletingAssetsView == null) {
            this.deletingAssetsView = new DeletingAssetsView(context);
        }
    }

    /* access modifiers changed from: protected */
    public int is3G() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager)
                instance
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return (activeNetworkInfo ==
                null ||
                !activeNetworkInfo.isConnected() ||
                !activeNetworkInfo.isAvailable() ||
                activeNetworkInfo.getType() != 0)
                ? 0 : 1;
    }

    public boolean is3GDisabled() {
        return DISABLE_3G;
    }

    public boolean isAmazonDevice() {
        return getManufacturer().equalsIgnoreCase("amazon");
    }

    /* access modifiers changed from: protected */
    public boolean isConnected() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager)
                instance
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return activeNetworkInfo !=
                null &&
                activeNetworkInfo.isConnected() &&
                activeNetworkInfo.isAvailable();
    }

    /* access modifiers changed from: protected */
    public boolean isSpaceAvailableForAlternativeDownload() {
        long availableInternalMemorySize;
        long j = spaceNeededToDownload;
        if (j <= 0) {
            j = ((((long) TOTAL_SPACE_MB) * 1024) * 1024) - calculateDownloaded(new File(this.assetManager.getAssetPath()));
            if (j <= 0) {
                j = 1048576;
            }
            spaceNeededToDownload = j;
        }
        if (USE_INTERNAL_STORAGE) {
            availableInternalMemorySize = MemoryStatus.getAvailableExternalMemorySize();
            Logging.DEBUG_OUT("Using alternative location: External Storage is " + availableInternalMemorySize);
        } else {
            availableInternalMemorySize = MemoryStatus.getAvailableInternalMemorySize();
            Logging.DEBUG_OUT("Using alternative location: Internal Storage is " + availableInternalMemorySize);
        }
        return availableInternalMemorySize >= j;
    }

    /* access modifiers changed from: protected */
    public boolean isSpaceAvailableForDownload() {
        long availableExternalMemorySize;
        long j = spaceNeededToDownload;
        if (j <= 0) {
            j = ((((long) TOTAL_SPACE_MB) * 1024) * 1024) - calculateDownloaded(new File(this.assetManager.getAssetPath()));
            if (j <= 0) {
                j = 1048576;
            }
            spaceNeededToDownload = j;
        }
        if (USE_INTERNAL_STORAGE) {
            availableExternalMemorySize = MemoryStatus.getAvailableInternalMemorySize();
            Logging.DEBUG_OUT("Using main location: Internal Storage is " + availableExternalMemorySize);
        } else {
            availableExternalMemorySize = MemoryStatus.getAvailableExternalMemorySize();
            Logging.DEBUG_OUT("Using main location: External Storage is " + availableExternalMemorySize);
        }
        return availableExternalMemorySize >= j;
    }

    public boolean isWifiAvailable() {
        WifiManager wifiManager = (WifiManager)
                instance
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        boolean isWifiEnabled = wifiManager.isWifiEnabled();
        return isWifiEnabled ? wifiManager.getConnectionInfo().getSupplicantState() == SupplicantState.COMPLETED : isWifiEnabled;
    }

    public void onDestroy() {
        Logging.DEBUG_OUT("DownloadActivityInternal.onDestroy()");
        unregisterWifiReceiver();
        cleanStates();
        isInitialized = false;
        instance = null;
        downloadProgress = null;
        this.assetManager = null;
        mMainActivity = null;
        language = null;
    }

    public void onPause() {
        Logging.DEBUG_OUT("DownloadActivityInternal.onPause()");
        if (this.pCurrentView != null) {
            this.pCurrentView.pause();
        }
    }

    public void onResume() {
        Logging.DEBUG_OUT("DownloadActivityInternal.onResume()");
        if (getState() == 6) {
            startWifiDownload(true);
        } else if (getState() != 4) {
            resumeState(getState());
        } else if (chooseAvailableMemory()) {
            cleanState(4);
            if (!this.assetManager.assetsFoundLocally()) {
                setState(1);
            } else {
                setState(8);
            }
        } else {
            resumeState(4);
        }
    }

    public void onWindowFocusChanged(boolean z) {
        Logging.DEBUG_OUT("DownloadActivityInternal.onWindowFocusChanged(focus == " + z + ")");
        /*
        if (instance != null) {
            instance.getWindow().getDecorView().setSystemUiVisibility(5894);
        }
        */
    }

    public void recordError(int i) {
        mErrorList.add(Integer.valueOf(i));
        Logging.DEBUG_OUT("ERROR OCCURRED: " + i + " (total: " + mErrorList.size() + ")");
    }

    public void resumeState(int i) {
        Logging.DEBUG_OUT("DownloadActivityInternal resumeState: " + (i == -1 ? "STATE_INVALID" : STATE_STRINGS[i]));
        switch (i) {
            case 1:
                this.pCurrentView = this.downloadMsgView;
                break;
            case 2:
                this.pCurrentView = this.downloadProgressView;
                break;
            case 3:
                if (!checkLocalAssetVersion()) {
                    this.pCurrentView = this.invalidAssetVersionView;
                    break;
                } else {
                    Logging.DEBUG_OUT("onResult assetPath = " + this.assetManager.getAssetPath() + " result = " + -1);
                    this.mDownloadActivity.onResult(this.assetManager.getAssetPath(), -1);
                    Logging.DEBUG_CLOSE();
                    return;
                }
            case 4:
                this.pCurrentView = this.spaceUnavailableView;
                break;
            case 5:
                this.pCurrentView = this.downloadFailedView;
                break;
            case 6:
                this.pCurrentView = this.showWifiView;
                break;
            case 7:
                this.pCurrentView = this.networkUnavailableView;
                break;
            case 8:
                this.pCurrentView = this.checkUpdatesView;
                break;
            case 9:
                this.pCurrentView = this.updatesFoundView;
                break;
            case 10:
                this.pCurrentView = this.show3GView;
                break;
            case 11:
                this.pCurrentView = this.showBGView;
                break;
            case 12:
                this.pCurrentView = this.unSupportedDeviceView;
                break;
            case 13:
                this.pCurrentView = this.serverErrorView;
                break;
            case 14:
                this.pCurrentView = this.contactingServerView;
                break;
            case 15:
                this.pCurrentView = this.checkingHostIpView;
                break;
            case 16:
                this.pCurrentView = this.deletingAssetsView;
                break;
        }
        try {
            this.mHandler.postDelayed(new Runnable() {
                /* class com.eamobile.DownloadActivityInternal.AnonymousClass3 */

                public void run() {
                    Logging.DEBUG_OUT("DownloadActivityInternal resumeState - Making a new runnable to resume.");
                    if (DownloadActivityInternal.instance != null && DownloadActivityInternal.this.pCurrentView != null) {
                        Logging.DEBUG_OUT("resumeState: pCurrentView = " + DownloadActivityInternal.this.pCurrentView);
                        Logging.DEBUG_OUT("resumeState: before calling pCurrentView.resume()");
                        DownloadActivityInternal.this.pCurrentView.resume();
                        Logging.DEBUG_OUT("resumeState: after calling pCurrentView.resume()");
                    }
                }
            }, 20);
            pState = i;
        } catch (Exception e) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred while resuming State:");
            Logging.DEBUG_OUT_STACK(e);
        }
    }

    public void setAssetPath(String str, boolean z) {
        if (this.configLoaded) {
            setAssetPathAux(str, z);
            return;
        }
        this.callSetAssetPathAux = true;
        this.activityAssetPath = str;
        this.activityUseExternal = z;
    }

    public void setBackgroundBitmap(Bitmap bitmap) {
        this.bmpBg = bitmap;
    }

    public void setState(int i) {
        switch (i) {
            case STATE_SHOW_DOWNLOAD_MSG:
                this.pCurrentView = this.downloadMsgView;
                break;
            case STATE_DOWNLOADING_ASSETS:
                this.pCurrentView = this.downloadProgressView;
                break;
            case STATE_SUCCESS:
                if (!checkLocalAssetVersion()) {
                    this.pCurrentView = this.invalidAssetVersionView;
                    break;
                } else {
                    Logging.DEBUG_OUT("onResult assetPath = " + this.assetManager.getAssetPath() + " result = " + -1);
                    this.mDownloadActivity.onResult(this.assetManager.getAssetPath(), -1);
                    ADCTelemetry.getInstance().sendTelemetry(1);
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                    ADCTelemetry.getInstance().onDestroy();
                    Logging.DEBUG_CLOSE();
                    return;
                }
            case STATE_SPACE_UNAVAILABLE:
                this.pCurrentView = this.spaceUnavailableView;
                break;
            case STATE_FAILURE:
                this.downloadFailedView.setErrorCode(getLastError());
                ADCTelemetry.getInstance().sendTelemetry(3, "MINOR ERROR - error code=" + Integer.toString(getLastError()));
                this.pCurrentView = this.downloadFailedView;
                break;
            case STATE_SHOW_WIFI_DIALOG:
                this.pCurrentView = this.showWifiView;
                break;
            case STATE_3G_UNAVAILABLE:
                this.pCurrentView = this.networkUnavailableView;
                break;
            case STATE_CHECK_UPDATES:
                this.pCurrentView = this.checkUpdatesView;
                break;
            case STATE_UPDATES_FOUND:
                this.pCurrentView = this.updatesFoundView;
                break;
            case STATE_SHOW_3G_DIALOG:
                this.pCurrentView = this.show3GView;
                break;
            case STATE_BG_VIEW:
                this.pCurrentView = this.showBGView;
                break;
            case STATE_UNSUPPORTED_DEVICE:
                ADCTelemetry.getInstance().sendTelemetry(3, "CRITICAL ERROR - error code=" + getLastError());
                this.pCurrentView = this.unSupportedDeviceView;
                break;
            case STATE_SERVER_ERROR:
                ADCTelemetry.getInstance().sendTelemetry(3, "CRITICAL ERROR - error code=" + getLastError());
                this.pCurrentView = this.serverErrorView;
                break;
            case STATE_CONTACTING_SERVER:
                this.pCurrentView = this.contactingServerView;
                break;
            case STATE_CHECKING_HOST_IP:
                this.pCurrentView = this.checkingHostIpView;
                break;
            case STATE_SHOW_DELETING_ASSETS:
                this.pCurrentView = this.deletingAssetsView;
                break;
        }
        try {

            Runnable r0 = new Runnable() {
                @Override
                public void run() {
                    Logging.DEBUG_OUT("DownloadActivityInternal setState - Making a new runnable to init.");
                    if (!(DownloadActivityInternal.instance == null || DownloadActivityInternal.this.pCurrentView == null)) {
                        DownloadActivityInternal.instance.runOnUiThread(new Runnable() {

                            public void run() {
                                Logging.DEBUG_OUT("setState: pCurrentView = " + DownloadActivityInternal.this.pCurrentView);
                                Logging.DEBUG_OUT("setState: before calling pCurrentView.init()");
                                DownloadActivityInternal.this.pCurrentView.init();
                                Logging.DEBUG_OUT("setState: after calling pCurrentView.init()");
                                DownloadActivityInternal.instance.setContentView(DownloadActivityInternal.this.pCurrentView);
                                Logging.DEBUG_OUT("setState: after calling setContentView");
                            }
                        });
                    }
                    DownloadActivityInternal.changingState = false;
                }
            };
            changingState = true;
            this.mHandler.postDelayed(r0, 20);
            pStatePrev = pState;
            pState = i;
        } catch (Exception e2) {
            Logging.DEBUG_OUT("[ERROR] An exception occurred in setState:");
            Logging.DEBUG_OUT_STACK(e2);
        }
    }

    public void setStateChecksumError() {
        recordError(-10);
        this.serverErrorView.setErrorCode(getLastError());
        setState(13);
    }

    /* access modifiers changed from: protected */
    public void start3GManager() {
        instance.startActivity(new Intent("android.settings.NETWORK_OPERATOR_SETTINGS"));
    }

    public void startDataManagement() {
        instance.startActivity(new Intent("android.settings.MEMORY_CARD_SETTINGS"));
    }

    public boolean startDownload() {
        Logging.DEBUG_OUT("DownloadActivityInternal.startDownload()");
        this.assetManager.saveStateDownloadStarted();
        if (this.downloadFileData == null || this.downloadFileData.length <= 0) {
            checkServerContent(false);
            return false;
        }
        totalDownloadSizeMB = getTotalDownloadSize(this.downloadFileData);
        Logging.DEBUG_OUT("Total Download Size: " + totalDownloadSizeMB + " MB");
        boolean startDownloadingFiles = startDownloadingFiles(this.downloadFileData);
        Logging.DEBUG_OUT("startDownload expectedResult: " + startDownloadingFiles);
        return startDownloadingFiles;
    }

    public void startGameActivity(int i) {
        ADCTelemetry.getInstance().sendTelemetry(4);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        ADCTelemetry.getInstance().onDestroy();
        Logging.DEBUG_OUT("onResult assetPath = " + this.assetManager.getAssetPath() + " result = " + i);
        this.mDownloadActivity.onResult(this.assetManager.getAssetPath(), i);
        Logging.DEBUG_CLOSE();
    }

    public void startWifiDownload(final boolean z) {
        Thread r0 = new Thread() {

            @Override
            public void run() {
                do {
                } while (DownloadActivityInternal.changingState);
                if (DownloadActivityInternal.this.isWifiAvailable()) {
                    Logging.DEBUG_OUT("checking wifi: OK");
                    if (z) {
                        DownloadActivityInternal.this.cleanState(6);
                        if (DownloadActivityInternal.getTotalDownloadSizeMB() == 0) {
                            DownloadActivityInternal.this.setState(14);
                        } else {
                            DownloadActivityInternal.this.setState(2);
                        }
                    } else {
                        DownloadActivityInternal.this.setState(2);
                    }
                } else {
                    Logging.DEBUG_OUT("checking wifi: FAILED");
                    if (z) {
                        DownloadActivityInternal.this.resumeState(6);
                    } else {
                        DownloadActivityInternal.this.setState(6);
                    }
                }
            }
        };
        Logging.DEBUG_OUT("startWifiDownload");
        setState(15);
        r0.start();
    }

    public void startWifiManager() {
        try {
            instance.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
        } catch (ActivityNotFoundException e) {
            Logging.DEBUG_OUT("[ERROR] Unable to find an Activity to open Wifi settings.");
            instance.startActivity(new Intent("android.settings.SETTINGS"));
        }
    }

    public boolean test3GNetwork() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager)
                instance
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected() || !activeNetworkInfo.isAvailable()) {
            return false;
        }
        return activeNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI
                && activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    public boolean testNetwork(int[] iArr) {
        iArr[0] = -1;
        NetworkInfo activeNetworkInfo = ((ConnectivityManager)
                instance
                .getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected() || !activeNetworkInfo.isAvailable()) {
            return false;
        }
        iArr[0] = activeNetworkInfo.getType();
        return true;
    }

    public void updateDownload() {
        this.assetManager.prepareSDCard();
        if (DELETE_ASSETS_ON_UPDATE) {
            if (UNSAFE_ASSET_DELETION_ON_UPDATE) {
                Logging.DEBUG_OUT(" ");
                Logging.DEBUG_OUT("!!!!! WARNING !!!!!");
                Logging.DEBUG_OUT("Deleting assets without any additional checking:");
                Logging.DEBUG_OUT("!!!!! WARNING !!!!!");
                Logging.DEBUG_OUT(" ");
                setState(16);
                return;
            }
            Logging.DEBUG_OUT("Checking for asset deletion:");
            Properties loadAssetInfo = this.assetManager.loadAssetInfo();
            if (loadAssetInfo != null) {
                String property = loadAssetInfo.getProperty("packageName");
                if (property != null) {
                    Logging.DEBUG_OUT("packageName read from AssetInfo.indicate: " + property);
                    Logging.DEBUG_OUT("application packageName: " + this.mContext.getPackageName());
                    if (property.equals(this.mContext.getPackageName())) {
                        Logging.DEBUG_OUT("packageNames match");
                        setState(16);
                        return;
                    }
                    Logging.DEBUG_OUT("packageNames DO NOT match");
                } else {
                    Logging.DEBUG_OUT("packageName not found");
                }
            } else {
                Logging.DEBUG_OUT("properties not found");
            }
        }
        setState(1);
    }

    public boolean useCustomProgressBar() {
        return CUSTOM_PROGRESS_BAR;
    }

    public boolean useOldProgressBar() {
        return USE_OLD_PROGRESS_BAR;
    }
}
