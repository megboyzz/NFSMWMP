package com.ea.nimble.friends;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.NetworkConnectionHandle;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsUtility.class */
class NimbleFriendsUtility {
    NimbleFriendsUtility() {
    }

    protected static JSONArray cutJSONArray(JSONArray jSONArray, int i) {
        return null;
    }

    protected static boolean isNimbleComponentAvailable(String str) {
        boolean z = false;
        if (Base.getComponent(str) != null) {
            z = true;
        }
        return z;
    }

    public static HashMap<String, Object> parseBodyJSONData(NetworkConnectionHandle networkConnectionHandle) throws Error {
        InputStream dataStream = networkConnectionHandle.getResponse().getDataStream();
        if (dataStream == null || dataStream.toString().length() == 0) {
            throw new NimbleFriendsError(networkConnectionHandle.getResponse().getStatusCode(), networkConnectionHandle.getResponse().getError().getMessage());
        }
        Scanner useDelimiter = new Scanner(dataStream).useDelimiter("\\A");
        String str = "";
        if (useDelimiter.hasNext()) {
            str = useDelimiter.next();
        }
        useDelimiter.close();
        HashMap<String, Object> hashMap = null;
        if (str != null) {
            hashMap = null;
            if (str.length() > 0) {
                hashMap = (HashMap) new GsonBuilder().serializeNulls().create().fromJson(str, new TypeToken<HashMap<String, Object>>() { // from class: com.ea.nimble.friends.NimbleFriendsUtility.1
                }.getType());
            }
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ArrayList<UserInfoFromIdentity> parseJSONObjectToArrayOfUserInfo(JSONObject jSONObject) {
        ArrayList<UserInfoFromIdentity> arrayList = null;
        if (jSONObject != null) {
            if (jSONObject.optJSONObject("pidInfos") == null) {
                arrayList = null;
            } else {
                JSONArray optJSONArray = jSONObject.optJSONObject("pidInfos").optJSONArray("pidInfo");
                arrayList = null;
                if (optJSONArray != null) {
                    ArrayList<UserInfoFromIdentity> arrayList2 = new ArrayList<>();
                    int i = 0;
                    while (true) {
                        arrayList = arrayList2;
                        if (i >= optJSONArray.length()) {
                            break;
                        }
                        try {
                            UserInfoFromIdentity userInfoFromIdentity = new UserInfoFromIdentity(optJSONArray.getJSONObject(i));
                            if (!(userInfoFromIdentity.getPidId() == null || userInfoFromIdentity.getPidId() == "" || userInfoFromIdentity.getExternalRefValue() == null || userInfoFromIdentity.getExternalRefValue() == "")) {
                                arrayList2.add(userInfoFromIdentity);
                            }
                        } catch (JSONException e) {
                        }
                        i++;
                    }
                }
            }
        }
        return arrayList;
    }
}
