package com.eamobile;

import android.os.Build;
import com.eamobile.download.Logging;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ADCTelemetry {
    public static final int CANCEL = 4;
    public static final int COMPLETE = 1;
    public static final int FAILED = 3;
    public static final int RETRY = 2;
    public static final int START = 0;
    private static ADCTelemetry instance = null;
    private DownloadActivityInternal downloadActivityInternal;
    private boolean downloadStarted;
    private Queue<TelemetryQueueElement> eventQueue;
    private String pathToQueue;
    private TelemetryQueueThread queueThread;
    private String uniqueToken;

    /* access modifiers changed from: private */
    public class TelemetryQueueElement {
        public String jsonEvent;
        public int nrOfRetries;

        public TelemetryQueueElement(int i, String str) {
            this.nrOfRetries = i;
            this.jsonEvent = str;
        }
    }

    private class TelemetryQueueThread extends Thread {
        private boolean running = true;

        private TelemetryQueueThread() {
        }

        public void run() {
            while (this.running) {
                try {
                    TelemetryQueueElement telemetryQueueElement = (TelemetryQueueElement) ADCTelemetry.this.eventQueue.poll();
                    if (telemetryQueueElement != null) {
                        Logging.DEBUG_OUT("ADCTelemetry - TelemetryQueueThread creating new TelemetrySendThread");
                        new TelemetrySendThread(telemetryQueueElement.jsonEvent, telemetryQueueElement.nrOfRetries).start();
                    }
                    try {
                        sleep(1000);
                    } catch (Exception e) {
                    }
                } catch (Exception e2) {
                    Logging.DEBUG_OUT("ADCTelemetry - TelemetryQueueThread " + e2.toString());
                }
            }
        }

        public synchronized void stopThread() {
            this.running = false;
        }
    }

    private class TelemetrySendThread extends Thread {
        private String jsonEvent = null;
        private String message;
        private int retries;
        private int state;

        public TelemetrySendThread(int i, String str) {
            this.state = i;
            this.message = new Date(System.currentTimeMillis()).toString() + " : " + str;
        }

        public TelemetrySendThread(String str, int i) {
            this.jsonEvent = str;
            this.retries = i;
        }

        private String createJSON() {
            String str;
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("token", ADCTelemetry.this.uniqueToken);
                jSONObject.put("status", this.state);
                jSONObject.put("errMsg", this.message);
                if (this.state == 0) {
                    DownloadActivityInternal unused = ADCTelemetry.this.downloadActivityInternal;
                    jSONObject.put("prodID", DownloadActivityInternal.PRODUCT_ID);
                    DownloadActivityInternal unused2 = ADCTelemetry.this.downloadActivityInternal;
                    jSONObject.put("sellID", DownloadActivityInternal.MASTER_SELL_ID);
                    DownloadActivityInternal unused3 = ADCTelemetry.this.downloadActivityInternal;
                    jSONObject.put("language", DownloadActivityInternal.language.getCurrentLanguage());
                    jSONObject.put("is3G", ADCTelemetry.this.downloadActivityInternal.is3G());
                    jSONObject.put("device", ADCTelemetry.this.downloadActivityInternal.getDeviceString());
                    jSONObject.put("firmware", Build.VERSION.RELEASE);
                    jSONObject.put("textureCompression", ADCTelemetry.this.downloadActivityInternal.glExtensions);
                    jSONObject.put("version", ADCTelemetry.this.downloadActivityInternal.getAPKVersion());
                    DownloadActivityInternal unused4 = ADCTelemetry.this.downloadActivityInternal;
                    if (DownloadActivityInternal.MIN_ASSET_VERSION_REQUIRED == null) {
                        str = "";
                    } else {
                        DownloadActivityInternal unused5 = ADCTelemetry.this.downloadActivityInternal;
                        str = DownloadActivityInternal.MIN_ASSET_VERSION_REQUIRED;
                    }
                    jSONObject.put("minVersion", str);
                }
            } catch (JSONException e) {
            }
            Logging.DEBUG_OUT("ADCTelemetry - The following JSON will be send: " + jSONObject.toString());
            return jSONObject.toString();
        }

        private String createURL() {
            StringBuilder sb = new StringBuilder();
            DownloadActivityInternal unused = ADCTelemetry.this.downloadActivityInternal;
            String sb2 = sb.append(DownloadActivityInternal.DOWNLOAD_URL).append("androidContentWS/cms/android/gameasset/application/telemetry?").append(UUID.randomUUID().toString()).toString();
            Logging.DEBUG_OUT("ADCTelemetry - Web service url: " + sb2);
            return sb2;
        }

        private void sendHttpPost(String str, int i) {
            boolean z = false;
            try {
                DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
                HttpParams params = defaultHttpClient.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 450);
                HttpConnectionParams.setSoTimeout(params, 450);
                HttpPost httpPost = new HttpPost(createURL());
                httpPost.setEntity(new StringEntity(str));
                httpPost.setHeader("Accept", "text/plain");
                httpPost.setHeader("Content-type", "application/json");
                Logging.DEBUG_OUT("ADCTelemetry - Calling web service.");
                HttpResponse execute = defaultHttpClient.execute(httpPost);
                int statusCode = execute.getStatusLine().getStatusCode();
                Logging.DEBUG_OUT("ADCTelemetry - Received status code " + Integer.toString(statusCode));
                if (statusCode != 200) {
                    z = true;
                } else {
                    HttpEntity entity = execute.getEntity();
                    if (entity != null) {
                        String entityUtils = EntityUtils.toString(entity);
                        Logging.DEBUG_OUT("ADCTelemetry - Received body string " + entityUtils);
                        if (!entityUtils.equals("OK")) {
                            z = true;
                            i++;
                        }
                    } else {
                        Logging.DEBUG_OUT("ADCTelemetry - Received empty body string");
                        z = true;
                        i++;
                    }
                }
                if (!z || i >= 5) {
                    Logging.DEBUG_OUT("ADCTelemetry - Telemetry was send with success");
                    return;
                }
                Logging.DEBUG_OUT("ADCTelemetry - Send failed. Adding event to the queue.");
                try {
                    ADCTelemetry.this.eventQueue.add(new TelemetryQueueElement(i, str));
                } catch (Exception e) {
                    Logging.DEBUG_OUT("ADCTelemetry - Received exception " + e.toString());
                }
            } catch (Exception e2) {
                Logging.DEBUG_OUT("ADCTelemetry - Received exception " + e2.toString());
                if (1 == 0 || i >= 5) {
                    Logging.DEBUG_OUT("ADCTelemetry - Telemetry was send with success");
                    return;
                }
                Logging.DEBUG_OUT("ADCTelemetry - Send failed. Adding event to the queue.");
                try {
                    ADCTelemetry.this.eventQueue.add(new TelemetryQueueElement(i, str));
                } catch (Exception e3) {
                    Logging.DEBUG_OUT("ADCTelemetry - Received exception " + e3.toString());
                }
            } catch (Throwable th) {
                if (0 == 0 || i >= 5) {
                    Logging.DEBUG_OUT("ADCTelemetry - Telemetry was send with success");
                } else {
                    Logging.DEBUG_OUT("ADCTelemetry - Send failed. Adding event to the queue.");
                    try {
                        ADCTelemetry.this.eventQueue.add(new TelemetryQueueElement(i, str));
                    } catch (Exception e4) {
                        Logging.DEBUG_OUT("ADCTelemetry - Received exception " + e4.toString());
                    }
                }
                throw th;
            }
        }

        public void run() {
            Logging.DEBUG_OUT("ADCTelemetry - Inside TelemetrySendThread - before web service call");
            if (this.jsonEvent == null) {
                Logging.DEBUG_OUT("ADCTelemetry - Inside TelemetrySendThread calling sendHttpPost with retry = 0");
                sendHttpPost(createJSON(), 0);
            } else {
                Logging.DEBUG_OUT("ADCTelemetry - Inside TelemetrySendThread calling sendHttpPost with retry = " + Integer.toString(this.retries));
                sendHttpPost(this.jsonEvent, this.retries);
            }
            Logging.DEBUG_OUT("ADCTelemetry - Inside TelemetrySendThread - after web service call");
        }
    }

    private ADCTelemetry() {
        this.eventQueue = null;
        this.queueThread = null;
        this.downloadActivityInternal = null;
        this.downloadStarted = false;
        this.downloadActivityInternal = DownloadActivityInternal.getMainActivity();
        this.uniqueToken = UUID.randomUUID().toString();
    }

    public static synchronized ADCTelemetry getInstance() {
        ADCTelemetry aDCTelemetry;
        synchronized (ADCTelemetry.class) {
            if (instance == null) {
                instance = new ADCTelemetry();
            }
            aDCTelemetry = instance;
        }
        return aDCTelemetry;
    }

    private String getQueueFileName(String str) {
        return str + "/queue.file";
    }

    private void loadQueue(String str, Queue<TelemetryQueueElement> queue) {
        Logging.DEBUG_OUT("ADCTelemetry - Loading queue.");
        File file = new File(getQueueFileName(str));
        if (file.exists()) {
            Logging.DEBUG_OUT("ADCTelemetry - Found queue file!.");
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file), 8192);
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine != null) {
                        Logging.DEBUG_OUT("ADCTelemetry - New queue line: " + readLine);
                        queue.add(new TelemetryQueueElement(Integer.parseInt(readLine.substring(0, 1)), readLine.substring(1)));
                    } else {
                        bufferedReader.close();
                        Logging.DEBUG_OUT("ADCTelemetry - Done loading queue file!.");
                        return;
                    }
                }
            } catch (Exception e) {
            }
        } else {
            Logging.DEBUG_OUT("ADCTelemetry - Queue file doesn't exists.");
        }
    }

    private void saveQueue(String str, Queue<TelemetryQueueElement> queue) {
        File file = new File(getQueueFileName(str));
        if (file.exists()) {
            Logging.DEBUG_OUT("ADCTelemetry - Deleting queue file before saving the updated version.");
            file.delete();
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(getQueueFileName(str), true), 8192);
            TelemetryQueueElement poll = queue.poll();
            if (poll == null) {
                Logging.DEBUG_OUT("ADCTelemetry - No events to save to the queue file");
            }
            while (poll != null) {
                String str2 = Integer.toString(poll.nrOfRetries) + poll.jsonEvent + "\n";
                Logging.DEBUG_OUT("ADCTelemetry - Saving line in queue file : " + str2);
                bufferedWriter.write(str2);
                poll = queue.poll();
            }
            bufferedWriter.close();
        } catch (Exception e) {
        }
    }

    public void onCreate(String str) {
    }

    public void onDestroy() {
    }

    public void sendTelemetry(int i) {
        sendTelemetry(i, "");
    }

    public void sendTelemetry(int i, String str) {
        if (i == 0) {
            this.downloadActivityInternal.mDownloadActivity.onDownloadEvent(0);
        } else if (i == 4) {
            this.downloadActivityInternal.mDownloadActivity.onDownloadEvent(1);
        }
    }
}
