/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.BroadcastReceiver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.support.v4.content.LocalBroadcastManager
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 */
package com.ea.nimble;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.XmlResourceParser;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public final class Utility {
    private Utility() {
    }

    public static String SHA256HashString(String object) {
        MessageDigest object2;
        if (object == null) {
            return null;
        }
        try {
            object2 = MessageDigest.getInstance("SHA-256");
            object2.reset();
        }
        catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            Log.Helper.LOGES(null, "Can't find SHA-256 algorithm");
            return null;
        }
        byte[] digest = object2.digest(object.getBytes());
        StringBuffer stringBuffer = new StringBuffer();
        int n2 = 0;

        while (n2 < digest.length) {
            stringBuffer.append(Integer.toString((digest[n2] & 0xFF) + 256, 16).substring(1));
            ++n2;
        }
        return stringBuffer.toString();
    }

    public static String bytesToHexString(byte[] byArray) {
        char[] cArray = "0123456789ABCDEF".toCharArray();
        char[] cArray2 = new char[byArray.length * 2];
        int n2 = 0;
        while (n2 < byArray.length) {
            int n3 = byArray[n2] & 0xFF;
            cArray2[n2 * 2] = cArray[n3 >> 4];
            cArray2[n2 * 2 + 1] = cArray[n3 & 0xF];
            ++n2;
        }
        return new String(cArray2);
    }

    public static List<Object> convertJSONArrayToList(JSONArray jSONArray) {
        ArrayList<Object> arrayList = new ArrayList<>();
        if (jSONArray != null)
            for (int i=0;i<jSONArray.length();i++)
                try {
                    arrayList.add(jSONArray.get(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        return arrayList;
    }

    public static Map<String, Object> convertJSONObjectStringToMap(String object) {
        try {
            return Utility.convertJSONObjectToMap(new JSONObject(object));
        }
        catch (JSONException jSONException) {
            jSONException.printStackTrace();
            return new HashMap<String, Object>();
        }
    }

    public static Map<String, Object> convertJSONObjectToMap(JSONObject jSONObject) {

        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keys = jSONObject.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            Object value = null;
            try {
                value = jSONObject.get(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            if (value instanceof JSONArray) {
                value = convertJSONArrayToList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = convertJSONObjectToMap((JSONObject) value);
            }
            map.put(key, value);
        }   return map;
    }

    public static String convertObjectToJSONString(Object object) {
        return "";
    }

    public static boolean getTestResult() {
        if (5 + 5 != 10) return false;
        return true;
    }

    public static String getUTCDateStringFormat(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(date);
    }

    public static boolean isOnlyDecimalCharacters(String string2) {
        if (string2 == null) {
            return false;
        }
        int n2 = 0;
        while (n2 < string2.length()) {
            if (!Character.isDigit(string2.charAt(n2))) return false;
            ++n2;
        }
        return true;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled unnecessary exception pruning
     */
    public static LinkedHashMap parseXmlFile(int i) {
        XmlResourceParser xmlResourceParser = null;
        LinkedHashMap linkedHashMap;
        try {
            xmlResourceParser = null;
            XmlResourceParser xmlResourceParser2 = null;
            try {
                XmlResourceParser xml = ApplicationEnvironment.getComponent().getApplicationContext().getResources().getXml(i);
                LinkedHashMap linkedHashMap2 = new LinkedHashMap();
                String str = null;
                int eventType = xml.getEventType();
                while (eventType != 1) {
                    if (eventType == 2) {
                        str = xml.getName();
                    } else {
                        str = str;
                        if (eventType == 4) {
                            linkedHashMap2.put(str, xml.getText());
                            str = str;
                        }
                    }
                    eventType = xml.next();
                }
                xmlResourceParser2 = xml;
                xmlResourceParser = xml;
                xml.close();
                linkedHashMap = linkedHashMap2;
                xml.close();
                return linkedHashMap2;
            } catch (Exception e) {
                Log.Helper.LOGES(null, "Error reading xml file: " + e.toString());
                if (xmlResourceParser2 != null) {
                    xmlResourceParser2.close();
                }
                linkedHashMap = null;
            }
            return linkedHashMap;
        } catch (Throwable th) {
            if (xmlResourceParser != null) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    public static String readStringFromStream(InputStream closeable) throws IOException {
        int n2;
        byte[] cArray = new byte[8192];
        StringWriter stringWriter = new StringWriter();
        while ((n2 = closeable.read(cArray, 0, 4096)) > 0) {
            stringWriter.write(Arrays.toString(cArray), 0, n2);
        }
        return stringWriter.toString();
    }

    public static void registerReceiver(String string2, BroadcastReceiver broadcastReceiver) {
        IntentFilter intentFilter = new IntentFilter(string2);
        LocalBroadcastManager
                .getInstance(
                        ApplicationEnvironment
                                .getComponent()
                                .getApplicationContext()
                )
                .registerReceiver(broadcastReceiver, intentFilter);
    }

    public static String safeString(String string2) {
        String string3 = string2;
        if (string2 != null) return string3;
        return "";
    }

    public static void sendBroadcast(String string2, Map<String, String> map) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ApplicationEnvironment.getComponent().getApplicationContext());
        Intent intent = new Intent(string2);
        if (map != null) {
            for (String string3 : map.keySet()) {
                intent.putExtra(string3, map.get(string3));
            }
        }
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void sendBroadcastSerializable(String string2, Map<String, Serializable> map) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(ApplicationEnvironment.getComponent().getApplicationContext());
        Intent intent = new Intent(string2);
        if (map != null) {
            for (String string3 : map.keySet()) {
                intent.putExtra(string3, map.get(string3));
            }
        }
        localBroadcastManager.sendBroadcast(intent);
    }

    public static boolean stringsAreEquivalent(String string2, String string3) {
        if (string2 != null && string3 != null) {
            return string2.compareTo(string3) == 0;
        }
        if (string2 == string3) return true;
        return false;
    }

    public static void unregisterReceiver(BroadcastReceiver broadcastReceiver) {
        LocalBroadcastManager.getInstance(ApplicationEnvironment.getComponent().getApplicationContext()).unregisterReceiver(broadcastReceiver);
    }

    public static boolean validString(String string2) {
        if (string2 == null) return false;
        if (string2.length() <= 0) return false;
        return true;
    }
}

