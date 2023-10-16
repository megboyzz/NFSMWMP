package com.ea.nimble;

import android.text.TextUtils;

import com.ea.ironmonkey.devmenu.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class NetworkConnection implements LogSource, NetworkConnectionHandle, Runnable {
    private static int MAXIMUM_RAW_DATA_LENGTH = 1048576;
    static int s_loggingIdCount = 100;
    private NetworkConnectionCallback m_completionCallback;
    private Date m_connectionStartTimestamp;
    private NetworkConnectionCallback m_headerCallback;
    private String m_loggingId;
    private NetworkImpl m_manager;
    private IOperationalTelemetryDispatch m_otDispatch;
    private NetworkConnectionCallback m_progressCallback;
    private HttpRequest m_request;
    private String m_requestDataForLog;
    private HttpResponse m_response;
    private StringBuilder m_responseDataForLog;
    private Thread m_thread;

    public NetworkConnection(NetworkImpl networkImpl, HttpRequest httpRequest) {
        this(networkImpl, httpRequest, null);
    }

    public NetworkConnection(NetworkImpl networkImpl, HttpRequest httpRequest, IOperationalTelemetryDispatch iOperationalTelemetryDispatch) {
        this.m_manager = networkImpl;
        this.m_thread = null;
        this.m_request = httpRequest;
        this.m_response = new HttpResponse();
        this.m_headerCallback = null;
        this.m_progressCallback = null;
        this.m_completionCallback = null;
        this.m_connectionStartTimestamp = null;
        this.m_otDispatch = iOperationalTelemetryDispatch;
        this.m_loggingId = String.valueOf(s_loggingIdCount);
        int i = s_loggingIdCount;
        s_loggingIdCount = i + 1;
        if (i >= 1000) {
            s_loggingIdCount = 100;
        }
    }

    private String beautifyJSONString(String jsonString) {
        try {
            JSONTokener tokener = new JSONTokener(jsonString);
            JSONObject jsonObject = new JSONObject(tokener);
            return jsonObject.toString(4);
        } catch (JSONException e) {
            return jsonString;
        }
    }

    
    private void downloadToBuffer(java.net.HttpURLConnection r7) throws java.io.IOException {
        Observer.onCallingMethod();
    }
    
    private void downloadToBufferWithError(java.net.HttpURLConnection r7) {
        Observer.onCallingMethod();
    }

    /* JADX WARN: Finally extract failed */
    private void downloadToFile(HttpURLConnection httpURLConnection) throws IOException {
        if (!skipDownloadForOverwritePolicy(httpURLConnection)) {
            File file = new File(this.m_request.targetFilePath);
            File file2 = new File(ApplicationEnvironment.getComponent().getCachePath() + File.separator + file.getName());
            boolean z = file2.exists() && this.m_request.overwritePolicy.contains(IHttpRequest.OverwritePolicy.RESUME_DOWNLOAD);
            InputStream inputStream = httpURLConnection.getInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file2, z);
            byte[] bArr = new byte[65536];
            Log.Helper.LOGI(this, "Started File Download for " + file.toString());
            while (true) {
                try {
                    int read = inputStream.read(bArr);
                    if (read < 0) {
                        break;
                    } else if (read == 0) {
                        Thread.yield();
                    } else {
                        fileOutputStream.write(bArr, 0, read);
                        this.m_response.downloadedContentLength += (long) read;
                        if (this.m_progressCallback != null) {
                            this.m_progressCallback.callback(this);
                        }
                    }
                } catch (Throwable th) {
                    inputStream.close();
                    fileOutputStream.close();
                    throw th;
                }
            }
            inputStream.close();
            fileOutputStream.close();
            if (file.exists() && !file.delete()) {
                Log.Helper.LOGE(this, "Failed to delete existed target file " + file);
            }
            if (!file2.renameTo(file)) {
                Log.Helper.LOGI(this, "Failed to move temp file " + file2 + " to target file " + file);
                Log.Helper.LOGI(this, "Using fallback, and copying file instead " + file2 + "to target file " + file);
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileChannel fileChannel = null;
                FileChannel fileChannel2 = null;
                FileChannel fileChannel3 = null;
                FileChannel fileChannel4 = null;
                try {
                    try {
                        FileChannel channel = new FileInputStream(file2).getChannel();
                        FileChannel channel2 = new FileOutputStream(file).getChannel();
                        fileChannel4 = channel2;
                        fileChannel2 = channel;
                        fileChannel3 = channel2;
                        fileChannel = channel;
                        channel2.transferFrom(channel, 0, channel.size());
                        if (channel != null) {
                            channel.close();
                        }
                        if (channel2 != null) {
                            channel2.close();
                        }
                        if (file2.exists()) {
                            file2.delete();
                        }
                    } catch (Exception e) {
                        fileChannel3 = fileChannel4;
                        fileChannel = fileChannel2;
                        Log.Helper.LOGE(this, "ERROR while copying file, " + e);
                        if (fileChannel2 != null) {
                            fileChannel2.close();
                        }
                        if (fileChannel4 != null) {
                            fileChannel4.close();
                        }
                        if (file2.exists()) {
                            file2.delete();
                        }
                    }
                } catch (Throwable th2) {
                    if (fileChannel != null) {
                        fileChannel.close();
                    }
                    if (fileChannel3 != null) {
                        fileChannel3.close();
                    }
                    if (file2.exists()) {
                        file2.delete();
                    }
                    throw th2;
                }
            }
        }
    }

    private void finish() {
        this.m_response.isCompleted = true;
        logOperationalTelemetryResponse();
        if (this.m_completionCallback != null) {
            this.m_completionCallback.callback(this);
        }
        synchronized (this) {
            notifyAll();
        }
        this.m_manager.removeConnection(this);
    }

    private void httpRecv(HttpURLConnection httpURLConnection) throws IOException, Error {
        InputStream errorStream;
        try {
            errorStream = httpURLConnection.getInputStream();
        } catch (Exception e) {
            try {
                errorStream = httpURLConnection.getErrorStream();
            } catch (Exception e2) {
                throw new Error(Error.Code.NETWORK_CONNECTION_ERROR, "Exception when getting error stream from HTTP connection.", e2);
            }
        }
        this.m_response.url = httpURLConnection.getURL();
        try {
            this.m_response.statusCode = httpURLConnection.getResponseCode();
            this.m_response.expectedContentLength = (long) httpURLConnection.getContentLength();
            this.m_response.lastModified = httpURLConnection.getLastModified();
            for (Map.Entry<String, List<String>> entry : httpURLConnection.getHeaderFields().entrySet()) {
                this.m_response.headers.put(entry.getKey(), TextUtils.join(", ", entry.getValue()));
            }
            boolean z = this.m_response.expectedContentLength > ((long) MAXIMUM_RAW_DATA_LENGTH);
            boolean validString = Utility.validString(this.m_request.targetFilePath);
            if (!z || validString) {
                this.m_response.downloadedContentLength = 0;
                if (this.m_headerCallback != null) {
                    this.m_headerCallback.callback(this);
                }
                if (validString && errorStream != null) {
                    downloadToFile(httpURLConnection);
                } else if (this.m_response.expectedContentLength != 0) {
                    if (this.m_response.data == null) {
                        this.m_response.data = new ByteBufferIOStream((int) this.m_response.expectedContentLength);
                    } else {
                        this.m_response.data.clear();
                    }
                    if (this.m_response.statusCode == 200) {
                        downloadToBuffer(httpURLConnection);
                    } else {
                        downloadToBufferWithError(httpURLConnection);
                    }
                }
                if (!(this.m_response.statusCode == 200 || (validString && this.m_response.statusCode == 206))) {
                    throw new HttpError(this.m_response.statusCode, "Request " + this + " failed for HTTP error");
                }
                return;
            }
            throw new Error(Error.Code.NETWORK_OVERSIZE_DATA, "Request " + this + " is oversize, please provide a local file path to download it as file.");
        } finally {
            if (errorStream != null) {
                errorStream.close();
            }
            logCommunication();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v0, types: [java.net.HttpURLConnection] */
    /* JADX WARN: Type inference failed for: r8v1, types: [java.io.OutputStream] */
    /* JADX WARN: Type inference failed for: r8v10, types: [java.lang.Object, java.lang.String] */
    /* JADX WARN: Type inference failed for: r8v11 */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v3 */
    /* JADX WARN: Type inference failed for: r8v7 */
    /* JADX WARN: Type inference failed for: r8v8 */
    private void httpSend(HttpURLConnection httpURLConnection) throws IOException {
        String str;
        this.m_connectionStartTimestamp = new Date();
        if (this.m_request.headers != null) {
            Iterator<String> it = this.m_request.headers.keySet().iterator();
            while (it.hasNext()) {
                str = it.next();
                httpURLConnection.setRequestProperty(str, this.m_request.headers.get(str));
            }
        }
        if (this.m_request.getMethod() == IHttpRequest.Method.POST || this.m_request.getMethod() == IHttpRequest.Method.PUT) {
            byte[] byteArray = this.m_request.data.toByteArray();
            if (byteArray == null || byteArray.length <= 0) {
                logRequest();
                return;
            }
            try {
                prepareRequestLog(byteArray);
                logRequest();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setFixedLengthStreamingMode(byteArray.length);
                OutputStream outputStream = null;
                try {
                    OutputStream outputStream2 = httpURLConnection.getOutputStream();
                    outputStream = outputStream2;
                    outputStream2.write(byteArray);
                    if (outputStream2 != null) {
                        outputStream2.close();
                    }
                } catch (Exception e) {
                    StringWriter stringWriter = new StringWriter();
                    e.printStackTrace(new PrintWriter(stringWriter));
                    Log.Helper.LOGE(this, "Exception in network connection:" + stringWriter.toString());
                    if (outputStream != null) {
                        outputStream.close();
                    }
                }
            } catch (Throwable th) {
            }
        } else {
            logRequest();
        }
    }

    private void logCommunication() {
        if (Log.getComponent().getThresholdLevel() <= 100) {
            int i = 4096;
            if (this.m_requestDataForLog != null) {
                i = 4096 + this.m_requestDataForLog.length();
            }
            int i2 = i;
            if (this.m_responseDataForLog != null) {
                i2 = i + this.m_responseDataForLog.length();
            }
            StringBuilder sb = new StringBuilder(i2);
            sb.append(String.format("\n>>>> CONNECTION ID %s FINISHED >>>>>>>>>>>>>>\n", this.m_loggingId));
            sb.append("\n>>>> REQUEST >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
            sb.append("REQUEST: ").append(this.m_request.method.toString());
            sb.append(' ').append(this.m_request.url.toString()).append('\n');
            boolean z = false;
            boolean z2 = false;
            if (this.m_request.headers != null) {
                z2 = false;
                if (this.m_request.headers.size() > 0) {
                    Iterator<String> it = this.m_request.headers.keySet().iterator();
                    while (true) {
                        z2 = z;
                        if (!it.hasNext()) {
                            break;
                        }
                        String next = it.next();
                        if (next != null) {
                            sb.append("REQ HEADER: ").append(next);
                            String safeString = Utility.safeString(this.m_request.headers.get(next));
                            sb.append(" VALUE: ").append(safeString).append('\n');
                            if (next.equals("Content-Type") && (safeString.contains("application/json") || safeString.contains("text/json"))) {
                                z = true;
                            }
                        }
                    }
                }
            }
            if (this.m_requestDataForLog != null && this.m_requestDataForLog.length() > 0) {
                sb.append("REQ BODY:\n");
                String str = this.m_requestDataForLog.toString();
                String str2 = str;
                if (z2) {
                    str2 = beautifyJSONString(str);
                }
                sb.append(str2).append('\n');
            }
            sb.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n");
            sb.append(">>>> RESPONSE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
            sb.append("RESP URL: ").append(this.m_response.url.toString()).append('\n');
            sb.append("RESP STATUS: ").append(this.m_response.statusCode).append('\n');
            boolean z3 = false;
            boolean z4 = false;
            if (this.m_response.headers != null) {
                z4 = false;
                if (this.m_response.headers.size() > 0) {
                    Iterator<String> it2 = this.m_response.headers.keySet().iterator();
                    while (true) {
                        z4 = z3;
                        if (!it2.hasNext()) {
                            break;
                        }
                        String next2 = it2.next();
                        if (next2 != null) {
                            sb.append("RESP HEADER: ").append(next2);
                            String safeString2 = Utility.safeString(this.m_response.headers.get(next2));
                            sb.append(" VALUE: ").append(safeString2).append('\n');
                            if (next2.equals("Content-Type") && (safeString2.contains("application/json") || safeString2.contains("text/json"))) {
                                z3 = true;
                            }
                        }
                    }
                }
            }
            sb.append("RESP BODY:\n");
            String str3 = "<Empty>: there is no response body for this call";
            try {
                if (this.m_responseDataForLog != null) {
                    str3 = this.m_responseDataForLog.toString();
                }
            } catch (Exception e) {
                Log.Helper.LOGE(this, "Attempting to process the response body failed.");
                str3 = "<Empty>: there is no response body for this call";
                if (this.m_response != null) {
                    str3 = "<Empty>: there is no response body for this call";
                    if (this.m_response.getError() != null) {
                        str3 = "<Empty>: there is no response body for this call";
                        if (this.m_response.getError().getMessage() != null) {
                            str3 = this.m_response.getError().getMessage();
                        }
                    }
                }
            }
            String str4 = str3;
            if (z4) {
                str4 = beautifyJSONString(str3);
            }
            sb.append(str4).append('\n');
            sb.append("<<<< CONNECTION FINISHED <<<<<<<<<<<<<<<<<<<<<");
            Log.Helper.LOGV(this, sb.toString());
        }
    }

    private void logRequest() {
        if (Log.getComponent().getThresholdLevel() <= 100) {
            int i = 2048;
            if (this.m_requestDataForLog != null) {
                i = 2048 + this.m_requestDataForLog.length();
            }
            StringBuilder sb = new StringBuilder(i);
            sb.append(String.format("\n>>>> CONNECTION ID %s BEGIN >>>>>>>>>>>>>>>>>\n", this.m_loggingId));
            sb.append("REQUEST: ").append(this.m_request.method.toString());
            sb.append(' ').append(this.m_request.url.toString()).append('\n');
            boolean z = false;
            boolean z2 = false;
            if (this.m_request.headers != null) {
                z2 = false;
                if (this.m_request.headers.size() > 0) {
                    Iterator<String> it = this.m_request.headers.keySet().iterator();
                    while (true) {
                        z2 = z;
                        if (!it.hasNext()) {
                            break;
                        }
                        String next = it.next();
                        sb.append("REQ HEADER: ").append(next);
                        String str = this.m_request.headers.get(next);
                        sb.append(" VALUE: ").append(str).append('\n');
                        if (next.equals("Content-Type") && (str.contains("application/json") || str.contains("text/json"))) {
                            z = true;
                        }
                    }
                }
            }
            if (this.m_requestDataForLog != null && this.m_requestDataForLog.length() > 0) {
                sb.append("REQ BODY:\n");
                String str2 = this.m_requestDataForLog.toString();
                String str3 = str2;
                if (z2) {
                    str3 = beautifyJSONString(str2);
                }
                sb.append(str3).append('\n');
            }
            sb.append("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            Log.Helper.LOGV(this, sb.toString());
        }
    }

    private String multiplyStringNTimes(String str, int i) {
        StringBuilder sb = new StringBuilder(str.length() * i);
        for (int i2 = 0; i2 < i; i2++) {
            sb.append(str);
        }
        return sb.toString();
    }

    private void prepareRequestLog(byte[] bArr) {
        if (Log.getComponent().getThresholdLevel() <= 100) {
            try {
                this.m_requestDataForLog = new String(bArr, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                this.m_requestDataForLog = null;
            }
        }
    }

    private void prepareResponseLog() {
        if (Log.getComponent().getThresholdLevel() <= 100) {
            this.m_responseDataForLog = new StringBuilder(this.m_response.expectedContentLength > 0 ? (int) this.m_response.expectedContentLength : 4096);
        }
    }

    private void prepareResponseLog(byte[] bArr, int i, int i2) {
        if (Log.getComponent().getThresholdLevel() <= 100 && this.m_responseDataForLog != null) {
            try {
                this.m_responseDataForLog.append(new String(bArr, i, i2, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                this.m_responseDataForLog = null;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x003a, code lost:
        if (r5.m_request.overwritePolicy.contains(com.ea.nimble.IHttpRequest.OverwritePolicy.LENGTH_CHECK) == false) goto L_0x003d;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private boolean skipDownloadForOverwritePolicy(java.net.HttpURLConnection r6) {
        /*
            r5 = this;
            r0 = 1
            r8 = r0
            java.io.File r0 = new java.io.File
            r1 = r0
            r2 = r5
            com.ea.nimble.HttpRequest r2 = r2.m_request
            java.lang.String r2 = r2.targetFilePath
            r1.<init>(r2)
            r6 = r0
            r0 = r6
            boolean r0 = r0.exists()
            if (r0 != 0) goto L_0x001c
            r0 = 0
            r7 = r0
        L_0x001a:
            r0 = r7
            return r0
        L_0x001c:
            r0 = r6
            long r0 = r0.length()
            r1 = r5
            com.ea.nimble.HttpResponse r1 = r1.m_response
            long r1 = r1.expectedContentLength
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 == 0) goto L_0x003d
            r0 = r8
            r7 = r0
            r0 = r5
            com.ea.nimble.HttpRequest r0 = r0.m_request
            java.util.EnumSet<com.ea.nimble.IHttpRequest$OverwritePolicy> r0 = r0.overwritePolicy
            com.ea.nimble.IHttpRequest$OverwritePolicy r1 = com.ea.nimble.IHttpRequest.OverwritePolicy.LENGTH_CHECK
            boolean r0 = r0.contains(r1)
            if (r0 != 0) goto L_0x001a
        L_0x003d:
            r0 = r8
            r7 = r0
            r0 = r6
            long r0 = r0.lastModified()
            r1 = r5
            com.ea.nimble.HttpResponse r1 = r1.m_response
            long r1 = r1.lastModified
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 < 0) goto L_0x001a
            r0 = r8
            r7 = r0
            r0 = r5
            com.ea.nimble.HttpRequest r0 = r0.m_request
            java.util.EnumSet<com.ea.nimble.IHttpRequest$OverwritePolicy> r0 = r0.overwritePolicy
            com.ea.nimble.IHttpRequest$OverwritePolicy r1 = com.ea.nimble.IHttpRequest.OverwritePolicy.DATE_CHECK
            boolean r0 = r0.contains(r1)
            if (r0 == 0) goto L_0x001a
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.NetworkConnection.skipDownloadForOverwritePolicy(java.net.HttpURLConnection):boolean");
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public void cancel() {
        synchronized (this) {
            if (this.m_thread != null) {
                this.m_thread.interrupt();
            } else {
                finishWithError(new Error(Error.Code.NETWORK_OPERATION_CANCELLED, "Network connection " + toString() + " is cancelled"));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void cancelForAppSuspend() {
        cancel();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishWithError(Exception exc) {
        if (this.m_response.isCompleted) {
            Log.Helper.LOGI(this, "Finished connection %s skipped an error %s", toString(), exc.toString());
            return;
        }
        Log.Helper.LOGW(this, "Running connection number %s with name %s failed for error %s", this.m_loggingId, toString(), exc.toString());
        this.m_response.error = exc;
        finish();
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public NetworkConnectionCallback getCompletionCallback() {
        return this.m_completionCallback;
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public NetworkConnectionCallback getHeaderCallback() {
        return this.m_headerCallback;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "Network";
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public NetworkConnectionCallback getProgressCallback() {
        return this.m_progressCallback;
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public HttpRequest getRequest() {
        return this.m_request;
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public HttpResponse getResponse() {
        return this.m_response;
    }

    void logOperationalTelemetryResponse() {
        if (this.m_request == null || this.m_request.url == null) {
            Log.Helper.LOGE(this, "Empty request object and/or request URL for OT logging.");
        } else if (this.m_response == null) {
            Log.Helper.LOGE(this, "Empty response object for OT logging.");
        } else if (!BaseCore.getInstance().isActive()) {
            Log.Helper.LOGV(this, "BaseCore not active for operational telemetry logging.");
        } else {
            if (this.m_otDispatch == null) {
                this.m_otDispatch = OperationalTelemetryDispatch.getComponent();
                if (this.m_otDispatch == null) {
                    Log.Helper.LOGV(this, "OperationalTelemetry Component not active for operational telemetry logging.");
                    return;
                }
            }
            HashMap hashMap = new HashMap();
            String protocol = this.m_request.url.getProtocol();
            String path = this.m_request.url.getPath();
            String query = this.m_request.url.getQuery();
            String host = this.m_request.url.getHost();
            int i = this.m_response.statusCode;
            String url = this.m_request.url.toString();
            String str = Global.NOTIFICATION_DICTIONARY_RESULT_FAIL;
            if (this.m_connectionStartTimestamp != null) {
                try {
                    Date date = new Date();
                    str = Global.NOTIFICATION_DICTIONARY_RESULT_FAIL;
                    if (date != null) {
                        str = String.valueOf(date.getTime() - this.m_connectionStartTimestamp.getTime());
                    }
                } catch (Exception e) {
                    Log.Helper.LOGE(this, "Unable to allocate new Date object to calculate response time.");
                    str = Global.NOTIFICATION_DICTIONARY_RESULT_FAIL;
                }
            }
            Exception error = this.m_response.getError();
            boolean z = false;
            if (error != null) {
                if (error instanceof Error) {
                    Error error2 = (Error) error;
                    int code = error2.getCode();
                    hashMap.put("NIMBLE_ERROR_DOMAIN", error2.getDomain());
                    hashMap.put("NIMBLE_ERROR_CODE", String.valueOf(code));
                    z = error2.getDomain().equals(Error.ERROR_DOMAIN) && code == Error.Code.NETWORK_TIMEOUT.intValue();
                } else {
                    hashMap.put("NIMBLE_ERROR_DOMAIN", error.getClass().getName());
                    z = false;
                }
            }
            hashMap.put("CONNECTIONID", this.m_loggingId);
            hashMap.put("URL_ABSOLUTE", url);
            hashMap.put("URL_PROTOCOL", protocol);
            hashMap.put("URL_PATH", path);
            hashMap.put("URL_QUERY", query);
            hashMap.put("URL_HOST", host);
            hashMap.put("RESPONSE_TIME_MS", str);
            hashMap.put("HTTP_STATUS_CODE", String.valueOf(i));
            hashMap.put("REQUEST_TIMED_OUT", String.valueOf(z));
            this.m_otDispatch.logEvent("com.ea.nimble.network", hashMap);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            try {
                try {
                    try {
                        try {
                            try {
                                try {
                                    if (this.m_response.isCompleted) {
                                        synchronized (this) {
                                            try {
                                                this.m_thread = null;
                                            } catch (Throwable th) {
                                                throw th;
                                            }
                                        }
                                    } else if (Thread.interrupted()) {
                                        throw new InterruptedIOException();
                                    } else {
                                        synchronized (this) {
                                            try {
                                                this.m_thread = Thread.currentThread();
                                            } catch (Throwable th2) {
                                                throw th2;
                                            }
                                        }
                                        HttpURLConnection httpURLConnection = (HttpURLConnection) this.m_request.getUrl().openConnection();
                                        httpURLConnection.setRequestMethod(this.m_request.method.toString());
                                        httpURLConnection.setConnectTimeout((int) (this.m_request.timeout * 1000.0d));
                                        httpURLConnection.setReadTimeout((int) (this.m_request.timeout * 1000.0d));
                                        httpURLConnection.setRequestProperty("Connection", "close");
                                        if (Thread.interrupted()) {
                                            throw new InterruptedIOException();
                                        }
                                        httpSend(httpURLConnection);
                                        if (Thread.interrupted()) {
                                            throw new InterruptedIOException();
                                        }
                                        httpRecv(httpURLConnection);
                                        finish();
                                        synchronized (this) {
                                            try {
                                                this.m_thread = null;
                                            } catch (Throwable th3) {
                                                throw th3;
                                            }
                                        }
                                    }
                                } catch (Throwable th4) {
                                    synchronized (this) {
                                        try {
                                            this.m_thread = null;
                                            throw th4;
                                        } catch (Throwable th5) {
                                            throw th5;
                                        }
                                    }
                                }
                            } catch (InterruptedIOException e) {
                                finishWithError(new Error(Error.Code.NETWORK_OPERATION_CANCELLED, "Connection " + toString() + " is cancelled", e));
                                synchronized (this) {
                                    try {
                                        this.m_thread = null;
                                    } catch (Throwable th6) {
                                        throw th6;
                                    }
                                }
                            }
                        } catch (Error e2) {
                            finishWithError(e2);
                            synchronized (this) {
                                try {
                                    this.m_thread = null;
                                } catch (Throwable th7) {
                                    throw th7;
                                }
                            }
                        }
                    } catch (SocketTimeoutException e3) {
                        finishWithError(new Error(Error.Code.NETWORK_TIMEOUT, "Connection " + toString() + " timed out", e3));
                        synchronized (this) {
                            try {
                                this.m_thread = null;
                            } catch (Throwable th8) {
                                throw th8;
                            }
                        }
                    }
                } catch (ClassCastException e4) {
                    finishWithError(new Error(Error.Code.NETWORK_UNSUPPORTED_CONNECTION_TYPE, "Request " + toString() + " failed for unsupported connection type" + this.m_request.getUrl().getProtocol(), e4));
                    synchronized (this) {
                        try {
                            this.m_thread = null;
                        } catch (Throwable th9) {
                            throw th9;
                        }
                    }
                }
            } catch (UnknownHostException e5) {
                Network.Status status = this.m_manager.getStatus();
                if (status != Network.Status.OK) {
                    finishWithError(new Error(Error.Code.NETWORK_NO_CONNECTION, "No network connection, network status " + status.toString(), e5));
                } else {
                    finishWithError(new Error(Error.Code.NETWORK_UNREACHABLE, "Request " + toString() + " failed for unreachable host", e5));
                }
                synchronized (this) {
                    try {
                        this.m_thread = null;
                    } catch (Throwable th10) {
                        throw th10;
                    }
                }
            }
        } catch (IOException e6) {
            finishWithError(new Error(Error.Code.NETWORK_CONNECTION_ERROR, "Connection " + toString() + " failed with I/O exception", e6));
            synchronized (this) {
                try {
                    this.m_thread = null;
                } catch (Throwable th11) {
                    throw th11;
                }
            }
        } catch (Exception e7) {
            finishWithError(new Error(Error.Code.SYSTEM_UNEXPECTED, "Unexpected error.", e7));
            synchronized (this) {
                try {
                    this.m_thread = null;
                } catch (Throwable th12) {
                    throw th12;
                }
            }
        }
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public void setCompletionCallback(NetworkConnectionCallback networkConnectionCallback) {
        this.m_completionCallback = networkConnectionCallback;
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public void setHeaderCallback(NetworkConnectionCallback networkConnectionCallback) {
        this.m_headerCallback = networkConnectionCallback;
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public void setProgressCallback(NetworkConnectionCallback networkConnectionCallback) {
        this.m_progressCallback = networkConnectionCallback;
    }

    @Override // com.ea.nimble.NetworkConnectionHandle
    public void waitOn() {
        synchronized (this) {
            while (!this.m_response.isCompleted) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
