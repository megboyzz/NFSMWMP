package com.ea.nimble.identity;

import android.annotation.SuppressLint;

import com.ea.nimble.Error;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityUtility.class */
class NimbleIdentityUtility {
    public static final String NIMBLE_IDENTITY_DEVICE_UNIQUE_IDENTIFIER = "nimble.identity.device.unique.identifier";
    static int counter = 0;

    NimbleIdentityUtility() {
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String getCurrentTimeString() {
        return new SimpleDateFormat("yyyy-MM-DD HH:mm:ss").format(new Date());
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String getTimeString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(date);
    }

    public static Map<String, Object> parseBodyJSONData(NetworkConnectionHandle networkConnectionHandle) throws Error {
        return Utility.convertJSONObjectToMap(parseJsonResponse(networkConnectionHandle));
    }

    public static JSONObject parseJsonResponse(NetworkConnectionHandle networkConnectionHandle) throws Error {
        Exception error = networkConnectionHandle.getResponse().getError();
        if (error == null) {
            InputStream dataStream = networkConnectionHandle.getResponse().getDataStream();
            if (dataStream == null) {
                throw new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Cannot understand server response, expecting JSON string but get invalid data");
            }
            try {
                try {
                    JSONObject jSONObject = new JSONObject(Utility.readStringFromStream(dataStream));
                    if (!Utility.validString((String) jSONObject.opt("error"))) {
                        return jSONObject;
                    }
                    throw NimbleIdentityError.createWithData(Utility.convertJSONObjectToMap(jSONObject));
                } catch (JSONException e) {
                    throw new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Invalid JSON received from server", e);
                }
            } catch (IOException e2) {
                throw new Error(Error.Code.NETWORK_INVALID_SERVER_RESPONSE, "Error reading server response", e2);
            }
        } else if (error instanceof Error) {
            throw ((Error) error);
        } else {
            throw new Error(Error.Code.UNKNOWN, "Unknown error while parsing network response", error);
        }
    }

    public static HashMap<String, String> parseRedirectURLParameters(NetworkConnectionHandle networkConnectionHandle) {
        String url = networkConnectionHandle.getResponse().getUrl().toString();
        HashMap<String, String> hashMap = new HashMap<>();
        String str = "";
        if (url.split("\\?").length == 2) {
            str = url.split("\\?")[0];
        }
        String[] split = str.split("&");
        for (String str2 : split) {
            if (str2.split("=").length == 2) {
                hashMap.put(str2.split("=")[0], str2.split("=")[1]);
            }
        }
        return hashMap;
    }

    public static ByteArrayOutputStream toJSONString(HashMap<String, Object> hashMap) {
        return null;
    }
}
