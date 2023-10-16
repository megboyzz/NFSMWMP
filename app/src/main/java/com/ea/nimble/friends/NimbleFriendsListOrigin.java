package com.ea.nimble.friends;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.HttpRequest;
import com.ea.nimble.IHttpRequest;
import com.ea.nimble.Log;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentity;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import com.ea.nimble.identity.NimbleIdentityPidInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/* loaded from: stdlib.jar:com/ea/nimble/friends/.class */
public class NimbleFriendsListOrigin extends NimbleFriendsListImpl {
    private NimbleFriendsError lastError = null;
    private static String GET_FRIENDS_URI_PARAMS = "/friends/2/users/%s/friends?start=%d&size=%d&names=%s";
    private static String GET_FRIENDS_AVATAR_URI = "/avatar/user/%s/avatars";

    /* JADX INFO: Access modifiers changed from: package-private */
    public NimbleFriendsListOrigin() {
        this.LOG_SOURCE_TITLE = "";
        this.m_authenticatorId = Global.NIMBLE_AUTHENTICATOR_ORIGIN;
        Log.Helper.LOGV(this, "Constructed");
        this.m_pageSize = 20;
        this.m_nimbleFriendsList = new NimbleFriendsList(this, NimbleFriendsList.FriendsListType.ALL_FRIENDS);
        this.m_nimblePlayedFriendsList = new NimbleFriendsList(this, NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS);
    }

    private String getMdmAppKey() {
        return SynergyEnvironment.getComponent().getGosMdmAppKey();
    }

    private String getOriginAvatarsUrlFromSynergy() {
        String serverUrlWithKey = SynergyEnvironment.getComponent().getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_ORIGIN_AVATAR);
        if (serverUrlWithKey == null || serverUrlWithKey.length() <= 0) {
            return null;
        }
        String str = serverUrlWithKey;
        if (serverUrlWithKey.charAt(serverUrlWithKey.length() - 1) == '/') {
            str = serverUrlWithKey.substring(0, serverUrlWithKey.length() - 1);
        }
        return str;
    }

    private String getOriginFriendsUrlFromSynergy() {
        String serverUrlWithKey = SynergyEnvironment.getComponent().getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_EADP_FRIENDS_HOST);
        if (serverUrlWithKey == null || serverUrlWithKey.length() <= 0) {
            return null;
        }
        String str = serverUrlWithKey;
        if (serverUrlWithKey.charAt(serverUrlWithKey.length() - 1) == '/') {
            str = serverUrlWithKey.substring(0, serverUrlWithKey.length() - 1);
        }
        return str;
    }

    private HttpRequest makeGetFriendsAvatarInfoRequest(String str, String str2, List<String> list) {
        MalformedURLException e;
        String originAvatarsUrlFromSynergy = getOriginAvatarsUrlFromSynergy();
        if (str == null || str.length() <= 0 || list == null || list.size() <= 0 || list.size() > 20 || originAvatarsUrlFromSynergy == null || originAvatarsUrlFromSynergy.length() <= 0) {
            return null;
        }
        HttpRequest httpRequest = null;
        StringBuilder sb = new StringBuilder();
        for (String str3 : list) {
            sb.append(str3);
            sb.append(";");
        }
        sb.deleteCharAt(sb.length() - 1);
        try {
            HttpRequest httpRequest2 = new HttpRequest(new URL(originAvatarsUrlFromSynergy + String.format(GET_FRIENDS_AVATAR_URI, sb.toString())));
            httpRequest2.method = IHttpRequest.Method.GET;
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("AuthToken", str);
            httpRequest2.headers = hashMap;
            return httpRequest2;
        } catch (MalformedURLException e3) {
            e = e3;
        }
        return new HttpRequest();
    }

    private HttpRequest makeGetFriendsRequest(int i, int i2, boolean z, String str, String str2, String str3) {
        MalformedURLException e;
        String originFriendsUrlFromSynergy = getOriginFriendsUrlFromSynergy();
        String mdmAppKey = getMdmAppKey();
        if (str == null || str.length() <= 0) {
            Log.Helper.LOGE(this, "Failed to create GOS friends request because access token for Origin is null or invalid");
            return null;
        } else if (originFriendsUrlFromSynergy == null || originFriendsUrlFromSynergy.length() <= 0) {
            Log.Helper.LOGE(this, "Failed to create GOS friends request because GOS request URL is null or invalid");
            return null;
        } else if (mdmAppKey == null || mdmAppKey.length() <= 0) {
            Log.Helper.LOGE(this, "Failed to create GOS friends request because MDM App Key is null or invalid");
            return null;
        } else if (str3 == null || str3.length() <= 0) {
            Log.Helper.LOGE(this, "Failed to create GOS friends request because Origin user's PID is null or invalid");
            return null;
        } else {
            String str4 = str2 + " " + str;
            HttpRequest httpRequest = null;
            try {
                HttpRequest httpRequest2 = new HttpRequest(new URL(originFriendsUrlFromSynergy + (z ? String.format(GET_FRIENDS_URI_PARAMS, str3, Integer.valueOf(i), Integer.valueOf(i2), "true") : String.format(GET_FRIENDS_URI_PARAMS, str3, Integer.valueOf(i), Integer.valueOf(i2), "false"))));
                httpRequest2.method = IHttpRequest.Method.GET;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Authorization", str4);
                hashMap.put("X-Application-Key", mdmAppKey);
                hashMap.put("X-Api-Version", "2");
                httpRequest2.headers = hashMap;
                return httpRequest2;
            } catch (MalformedURLException e3) {
                e = e3;
            }
        }
        return new HttpRequest();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<NimbleUser> parseAvatarInfoXml(NetworkConnectionHandle networkConnectionHandle) throws Error {
        Exception e;
        this.lastError = null;
        int statusCode = networkConnectionHandle.getResponse().getStatusCode();
        if (statusCode != 200) {
            switch (statusCode) {
                case Log.LEVEL_WARN /* 400 */:
                    this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR, String.format("Avatar Info HTTP Resonse Error. Description: %s", "The indicated parameter is empty or invalid."));
                    break;
                case 401:
                    this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR, String.format("Avatar Info HTTP Resonse Error. Description: %s", "The specified AuthToken is empty or invalid."));
                    break;
                default:
                    this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR, "Avatar Info HTTP Resonse Error");
                    break;
            }
            throw this.lastError;
        }
        InputStream dataStream = networkConnectionHandle.getResponse().getDataStream();
        if (dataStream == null || dataStream.toString().length() == 0) {
            throw new NimbleFriendsError(networkConnectionHandle.getResponse().getStatusCode(), networkConnectionHandle.getResponse().getError().getMessage());
        }
        try {
            XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
            newPullParser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", false);
            newPullParser.setInput(dataStream, null);
            NimbleUser nimbleUser = null;
            ArrayList<NimbleUser> arrayList = null;
            for (int eventType = newPullParser.getEventType(); eventType != 1; eventType = newPullParser.next()) {
                switch (eventType) {
                    case 0:
                        try {
                            arrayList = new ArrayList<>();
                            break;
                        } catch (Exception e2) {
                            e = e2;
                            Log.Helper.LOGE(this, String.format("Parsing of GOS Avatar Info XML raised an exception. Details: %s", e.getMessage()));
                            e.printStackTrace();
                            this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_ERROR_PARSING_HTTP_RESPONSE, "Failed to parse the response XML from GOS Avatar service");
                            throw this.lastError;
                        }
                    case 2:
                        String name = newPullParser.getName();
                        if (name.equalsIgnoreCase("user")) {
                            nimbleUser = new NimbleUser();
                            try {
                                nimbleUser.setAuthenticatorId(Global.NIMBLE_AUTHENTICATOR_ORIGIN);
                                break;
                            } catch (Exception e3) {
                                e = e3;
                                Log.Helper.LOGE(this, String.format("Parsing of GOS Avatar Info XML raised an exception. Details: %s", e.getMessage()));
                                e.printStackTrace();
                                this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_ERROR_PARSING_HTTP_RESPONSE, "Failed to parse the response XML from GOS Avatar service");
                                throw this.lastError;
                            }
                        } else if (nimbleUser == null) {
                            continue;
                        } else if (name.equalsIgnoreCase("userId")) {
                            String nextText = newPullParser.nextText();
                            nimbleUser.setUserId(nextText);
                            nimbleUser.setPid(nextText);
                            continue;
                        } else if (name.equalsIgnoreCase("link")) {
                            nimbleUser.setImageUrl(newPullParser.nextText());
                            continue;
                        } else {
                            continue;
                        }
                    case 3:
                        if (newPullParser.getName().equalsIgnoreCase("user") && nimbleUser != null) {
                            arrayList.add(nimbleUser);
                            nimbleUser = null;
                            continue;
                        }
                        break;
                }
            }
            return arrayList;
        } catch (Exception e4) {
            e = e4;
        }
        return new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<NimbleUser> parseBodyJSONData(NetworkConnectionHandle networkConnectionHandle) throws Error {
        InputStream dataStream = networkConnectionHandle.getResponse().getDataStream();
        this.lastError = null;
        if (dataStream == null || dataStream.toString().length() == 0) {
            throw new NimbleFriendsError(networkConnectionHandle.getResponse().getStatusCode(), networkConnectionHandle.getResponse().getError().getMessage());
        }
        Scanner useDelimiter = new Scanner(dataStream).useDelimiter("\\A");
        String str = "";
        if (useDelimiter.hasNext()) {
            str = useDelimiter.next();
        }
        useDelimiter.close();
        ArrayList<NimbleUser> arrayList = new ArrayList<>();
        if (str == null || str.length() <= 0) {
            Log.Helper.LOGE(this, "Generic Server error when retrieving GOS Friends.");
            this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR, "Generic Server error when retrieving GOS Friends.");
            return arrayList;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            ArrayList<NimbleUser> arrayList2 = arrayList;
            if (jSONObject != null) {
                if (jSONObject.optJSONObject("error") == null) {
                    JSONArray optJSONArray = jSONObject.optJSONArray("entries");
                    if (optJSONArray != null && optJSONArray.length() > 0) {
                        int i = 0;
                        while (true) {
                            arrayList2 = arrayList;
                            if (i >= optJSONArray.length()) {
                                break;
                            }
                            JSONObject jSONObject2 = optJSONArray.getJSONObject(i);
                            if (jSONObject2 != null) {
                                NimbleUser createNimbleUser = createNimbleUser(jSONObject2);
                                if (createNimbleUser.getUserId() != null && createNimbleUser.getUserId().length() > 0) {
                                    arrayList.add(createNimbleUser);
                                }
                            }
                            i++;
                        }
                    } else if (optJSONArray == null || optJSONArray.length() != 0) {
                        JSONObject jSONObject3 = jSONObject.getJSONObject("error");
                        if (jSONObject3 != null) {
                            int optInt = jSONObject3.optInt("code", -1);
                            String optString = jSONObject3.optString("type", "");
                            Log.Helper.LOGE(this, String.format("Server error when retrieving GOS Friends. Code = %d, Message = %s", Integer.valueOf(optInt), optString));
                            this.lastError = new NimbleFriendsError(optInt, optString);
                            return arrayList;
                        }
                        Log.Helper.LOGE(this, "Generic Server error when retrieving GOS Friends.");
                        this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR, "Generic Server error when retrieving GOS Friends.");
                        return arrayList;
                    } else {
                        Log.Helper.LOGD(this, "GOS response indicates there are no friends for this Origin user");
                        this.lastError = null;
                        return arrayList;
                    }
                } else {
                    JSONObject optJSONObject = jSONObject.optJSONObject("error");
                    String optString2 = optJSONObject.optString("type");
                    int optInt2 = optJSONObject.optInt("code", -1);
                    if (optString2 != null && optString2.length() > 0) {
                        this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR, String.format("Code: %d, Type: %s", Integer.valueOf(optInt2), optString2));
                    }
                    arrayList2 = null;
                }
            }
            return arrayList2;
        } catch (JSONException e) {
            Log.Helper.LOGE(this, String.format("Exception when parsing JSON response. Error: %s", e.getMessage()));
            this.lastError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_SERVER_RESPONSE_ERROR, e.getMessage());
            return arrayList;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendGosRefreshAvatarsRequest(String str, String str2, List<String> list, final NimbleFriendsList.FriendsListType friendsListType, final NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, final NimbleFriendsRefreshScope nimbleFriendsRefreshScope) {
        try {
            HttpRequest makeGetFriendsAvatarInfoRequest = makeGetFriendsAvatarInfoRequest(str, str2, list);
            if (makeGetFriendsAvatarInfoRequest == null) {
                Log.Helper.LOGE(this, "Failed to create HTTP Request for GOS getAvatarInfo");
                invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_FAILED_TO_CREATE_GOS_REQUEST, "Failed to create HTTP Request for GOS getAvatarInfo", friendsListType);
                return;
            }
            Network.getComponent().sendRequest(makeGetFriendsAvatarInfoRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.friends..4
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {

                }
            });
        } catch (Exception e) {
            Log.Helper.LOGE(this, String.format("Exception raised when creating GoS Avatar URL refresh request. Exception: %s", e.getMessage()));
            invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_ERROR_PARSING_HTTP_RESPONSE, "Failed to process request for Avatar Info"), friendsListType);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendGosRefreshFriendsRequest(int i, int i2, String str, String str2, String str3, final NimbleFriendsList.FriendsListType friendsListType, final NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, final NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo) {
        try {
            HttpRequest makeGetFriendsRequest = makeGetFriendsRequest(i, i2, true, str, str2, str3);
            if (makeGetFriendsRequest == null) {
                try {
                    Log.Helper.LOGE(this, "Failed to create HTTP Request for GOS getFriendsList");
                    invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_FAILED_TO_CREATE_GOS_REQUEST, "Failed to create HTTP Request for GOS getFriendsList", friendsListType);
                } catch (Exception e) {
                    String format = String.format("Authenticator (%s) does not support Identity Refresh for Friends List", this.m_authenticatorId);
                    Log.Helper.LOGE(this, format);
                    invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_SUPPORTED, format, friendsListType);
                }
            } else {
                Network.getComponent().sendRequest(makeGetFriendsRequest, networkConnectionHandle -> { });
            }
        } catch (Exception e2) {
        }
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl
    protected NimbleUser createNimbleUser(JSONObject jSONObject) {
        NimbleUser nimbleUser = new NimbleUser();
        try {
            nimbleUser.setAuthenticatorId(this.m_authenticatorId);
            nimbleUser.setDisplayName(jSONObject.optString("displayName", ""));
            nimbleUser.setFriendType(jSONObject.optString("friendType", ""));
            nimbleUser.setUserId(String.valueOf(jSONObject.optLong("userId")));
            nimbleUser.setPersonaId(String.valueOf(jSONObject.optLong("personaId")));
            nimbleUser.setPid(String.valueOf(jSONObject.optLong("userId")));
            if (jSONObject.optLong("timestamp", 0) != 0) {
                nimbleUser.setRefreshTimestamp(new Date(jSONObject.optLong("timestamp") * 1000));
            }
            return nimbleUser;
        } catch (Exception e) {
            Log.Helper.LOGW(this, String.format("Exception when parsing JSON for Friends. Message: %s", e.getMessage()));
            return nimbleUser;
        }
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl, com.ea.nimble.LogSource
    public /* bridge */ /* synthetic */ String getLogSourceTitle() {
        return super.getLogSourceTitle();
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl
    protected void refreshFriendsListBasicInfo(final int i, final int i2, String str, final NimbleFriendsList.FriendsListType friendsListType, final NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, final NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo) {
        if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
            Log.Helper.LOGE(this, "Origin Friends Service does not support getting friends with PlayedCurrentGame flag");
            invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_FRIENDS_LIST_TYPE_UNSUPPORTED, "Origin Friends Service does not support getting friends with PlayedCurrentGame flag", friendsListType);
        } else if (!isNimbleComponentAvailable("com.ea.nimble.identity")) {
            Log.Helper.LOGE(this, "Unable to refresh friends Identity information because NimbleIdentity is not available");
            invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Unable to refresh friends Identity information because NimbleIdentity is not available", friendsListType);
        } else {
            INimbleIdentity iNimbleIdentity = (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
            NimbleIdentityPidInfo pidInfo = iNimbleIdentity.getAuthenticatorById(this.m_authenticatorId).getPidInfo();
            String str2 = null;
            if (pidInfo != null) {
                str2 = pidInfo.getPid();
            }
            if (!Utility.validString(str2)) {
                invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_LOGGED_IN, "Origin PID for the current user is not available.", friendsListType);
            } else {
                // from class: com.ea.nimble.friends..1
// com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback
                iNimbleIdentity.getAuthenticatorById(this.m_authenticatorId).requestAccessToken((iNimbleIdentityAuthenticator, str3, str4, error) -> {

                });
            }
        }
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl
    protected void refreshFriendsListIdentityInfo(ArrayList<String> arrayList, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshScope nimbleFriendsRefreshScope, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback) {
        Log.Helper.LOGI(this, "Origin Friends Service does not support IdentityInfo refresh because Origin friends already have Identity information");
        invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_TYPE_UNSUPPORTED, "Origin Friends Service does not support IdentityInfo refresh because Origin friends already have Identity information", friendsListType);
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl
    protected void refreshFriendsListImageUrl(final List<String> list, final NimbleFriendsList.FriendsListType friendsListType, final NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, final NimbleFriendsRefreshScope nimbleFriendsRefreshScope) {
        if (!isNimbleComponentAvailable("com.ea.nimble.identity")) {
            Log.Helper.LOGE(this, "Unable to refresh friends Avatar information because NimbleIdentity is not available");
            invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Unable to refresh friends Avatar information because NimbleIdentity is not available", friendsListType);
            return;
        }
        INimbleIdentity iNimbleIdentity = (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
        if (iNimbleIdentity == null) {
            Log.Helper.LOGE(this, "Identity Component not found. Not able to get friends Avatars.");
            invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Identity Component not found. Not able to get friends Avatars.", friendsListType);
            return;
        }
        iNimbleIdentity.getAuthenticatorById(this.m_authenticatorId).requestAccessToken(new INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback() { // from class: com.ea.nimble.friends..3
            @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback
            public void AccessTokenCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str, String str2, Error error) {

            }
        });
    }
}
