package com.ea.nimble.friends;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.NetworkConnectionHandle;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        return null;
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
