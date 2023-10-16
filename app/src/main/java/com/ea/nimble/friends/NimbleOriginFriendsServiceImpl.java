package com.ea.nimble.friends;

import com.ea.nimble.Base;
import com.ea.nimble.Component;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.HttpRequest;
import com.ea.nimble.IHttpRequest;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Network;
import com.ea.nimble.NetworkConnectionCallback;
import com.ea.nimble.NetworkConnectionHandle;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.identity.INimbleIdentity;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import com.ea.nimble.identity.NimbleIdentityPidInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class NimbleOriginFriendsServiceImpl extends Component implements LogSource, INimbleOriginFriendsService {
    private static final String GET_RECEIVED_INVITATION_LIST_URI = "/friends/2/users/%s/invitations/inbound";
    private static final String GET_SENT_INVITATION_LIST_URI = "/friends/2/users/%s/invitations/outbound";
    private static final String POST_SEND_FRIEND_INVITATION_URI = "/friends/2/users/%s/invitations/outbound/%s";
    private static final String RESPOND_TO_FRIEND_INVITATION_URI = "/friends/2/users/%s/invitations/inbound/%s";
    private static final String SEARCH_USER_BY_DISPLAY_NAME_URI = "/proxy/identity/personas?displayName=%s*";
    private static final String SEARCH_USER_BY_EMAIL_URI = "/proxy/identity/pids?email=%s";
    private static int SMS_REQUEST_CODE = 6697;
    private static int EMAIL_REQUEST_CODE = 6698;

    public enum UserSearchCriteria {
        EMAIL,
        DISPLAY_NAME
    }

    private NimbleUser createNimbleUserFromGosJson(JSONObject jSONObject) {
        NimbleUser nimbleUser = new NimbleUser();
        try {
            nimbleUser.setAuthenticatorId(Global.NIMBLE_AUTHENTICATOR_ORIGIN);
            nimbleUser.setDisplayName(jSONObject.optString("displayName", ""));
            nimbleUser.setFriendType(jSONObject.optString("friendType", ""));
            nimbleUser.setUserId(String.valueOf(jSONObject.optLong("userId", 0)));
            nimbleUser.setPersonaId(String.valueOf(jSONObject.optLong("personaId", 0)));
            nimbleUser.setPid(String.valueOf(jSONObject.optLong("userId", 0)));
            if (jSONObject.optLong("timestamp", 0) != 0) {
                nimbleUser.setRefreshTimestamp(new Date(jSONObject.optLong("timestamp") * 1000));
            }
            return nimbleUser;
        } catch (Exception e) {
            Log.Helper.LOGW(this, String.format("Exception when parsing JSON response. Message: %s", e.getMessage()));
            return null;
        }
    }

    private NimbleUser createNimbleUserFromNexusJson(JSONObject jSONObject) {
        NimbleUser nimbleUser = new NimbleUser();
        try {
            nimbleUser.setAuthenticatorId(Global.NIMBLE_AUTHENTICATOR_ORIGIN);
            nimbleUser.setDisplayName(jSONObject.optString("displayName", ""));
            nimbleUser.setFriendType("");
            nimbleUser.setUserId(String.valueOf(jSONObject.optLong("pidId", 0)));
            nimbleUser.setPersonaId(String.valueOf(jSONObject.optLong("personaId", 0)));
            nimbleUser.setPid(String.valueOf(jSONObject.optLong("pidId", 0)));
            if (jSONObject.optLong("timestamp", 0) != 0) {
                nimbleUser.setRefreshTimestamp(new Date(jSONObject.optLong("timestamp") * 1000));
            }
            return nimbleUser;
        } catch (Exception e) {
            Log.Helper.LOGW(this, String.format("Exception when parsing JSON response. Message: %s", e.getMessage()));
            return null;
        }
    }

    protected static String getIdentityProxyUrlFromSynergy() {
        String str;
        String serverUrlWithKey = SynergyEnvironment.getComponent().getServerUrlWithKey(SynergyEnvironment.SERVER_URL_KEY_IDENTITY_PROXY);
        if (serverUrlWithKey == null || serverUrlWithKey.length() <= 0) {
            str = null;
        } else {
            str = serverUrlWithKey;
            if (serverUrlWithKey.charAt(serverUrlWithKey.length() - 1) == '/') {
                return serverUrlWithKey.substring(0, serverUrlWithKey.length() - 1);
            }
        }
        return str;
    }

    private String getMdmAppKey() {
        return SynergyEnvironment.getComponent().getGosMdmAppKey();
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

    private static void initialize() {
        Base.registerComponent(new NimbleOriginFriendsServiceImpl(), NimbleOriginFriendsService.NIMBLE_COMPONENT_ID_FRIENDS_ORIGIN);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeFriendInvitationCallbackWithCode(INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback, NimbleFriendsError.Code code, String str) {
        if (nimbleFriendInvitationCallback == null) {
            Log.Helper.LOGW(this, "Unable to invoke an error callback for Friends services because callback is null");
        } else {
            nimbleFriendInvitationCallback.onCallback(false, new NimbleFriendsError(code, str));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeFriendInvitationCallbackWithError(INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback, Error error) {
        if (nimbleFriendInvitationCallback == null) {
            Log.Helper.LOGW(this, "Unable to invoke an error callback for Friends services because callback is null");
        } else {
            nimbleFriendInvitationCallback.onCallback(false, error);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeFriendInvitationCallbackWithSuccess(INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        if (nimbleFriendInvitationCallback == null) {
            Log.Helper.LOGW(this, "Unable to invoke a success callback for Friends services because callback is null");
        } else {
            nimbleFriendInvitationCallback.onCallback(true, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeUserSearchCallbackWithCode(INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback, NimbleFriendsError.Code code, String str) {
        if (nimbleUserSearchCallback == null) {
            Log.Helper.LOGW(this, "Unable to invoke an error callback for Friends services because callback is null");
        } else {
            nimbleUserSearchCallback.onCallback(null, new NimbleFriendsError(code, str));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeUserSearchCallbackWithError(INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback, Error error) {
        if (nimbleUserSearchCallback == null) {
            Log.Helper.LOGW(this, "Unable to invoke an error callback for Friends services because callback is null");
        } else {
            nimbleUserSearchCallback.onCallback(null, error);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invokeUserSearchCallbackWithSuccess(INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback, ArrayList<NimbleUser> arrayList) {
        if (nimbleUserSearchCallback == null) {
            Log.Helper.LOGW(this, "Unable to invoke a success callback for Friends services because callback is null");
        } else {
            nimbleUserSearchCallback.onCallback(arrayList, null);
        }
    }

    private boolean isNimbleComponentAvailable(String str) {
        boolean z = false;
        if (Base.getComponent(str) != null) {
            z = true;
        }
        return z;
    }

    private HttpRequest makeFriendInvitationRequest(String str, String str2, String str3, String str4, INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        Exception e;
        MalformedURLException e2;
        String originFriendsUrlFromSynergy = getOriginFriendsUrlFromSynergy();
        String mdmAppKey = getMdmAppKey();
        if (originFriendsUrlFromSynergy == null || originFriendsUrlFromSynergy.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for sending friend invitation because friends endpoint URI is empty or null");
            return null;
        } else if (mdmAppKey == null || mdmAppKey.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for sending friend invitation because MDM app key is empty or null");
            return null;
        } else if (str2 == null || str2.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for sending friend invitation because access token is null or empty");
            return null;
        } else if (str == null || str.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for sending friend invitation because user's nucleus ID is null or empty");
            return null;
        } else if (str3 == null || str3.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for sending friend invitation because target user's nucleus ID is null or empty");
            return null;
        } else {
            HttpRequest httpRequest = null;
            HttpRequest httpRequest2 = null;
            String str5 = originFriendsUrlFromSynergy + String.format(POST_SEND_FRIEND_INVITATION_URI, str, str3);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("source=mobile");
            if (str4 != null && str4.length() > 0) {
                stringBuffer.append("&comment=");
                stringBuffer.append(str4);
            }
            try {
                HttpRequest httpRequest3 = new HttpRequest(new URL(str5));
                try {
                    httpRequest3.method = IHttpRequest.Method.POST;
                    byte[] bytes = stringBuffer.toString().getBytes("UTF-8");
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
                    byteArrayOutputStream.write(bytes);
                    httpRequest3.data = byteArrayOutputStream;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("X-AuthToken", str2);
                    hashMap.put("X-Application-Key", mdmAppKey);
                    hashMap.put("X-Api-Version", "2");
                    httpRequest3.headers = hashMap;
                    return httpRequest3;
                } catch (MalformedURLException e3) {
                    e2 = e3;
                    httpRequest2 = httpRequest3;
                    Log.Helper.LOGE(this, "Exception when creating HTTP request URL for send friend invitation. Exception: " + e2.getMessage());
                    return httpRequest2;
                } catch (Exception e4) {
                    e = e4;
                    httpRequest = httpRequest3;
                    Log.Helper.LOGE(this, "Exception when creating HTTP request URL for send friend invitation. Exception: " + e.getMessage());
                    return httpRequest;
                }
            } catch (MalformedURLException e5) {
                e2 = e5;
            } catch (Exception e6) {
                e = e6;
            }
        }
        return new HttpRequest();
    }

    private HttpRequest makeGetFriendInvitationListRequest(String str, String str2, String str3) {
        Exception e;
        MalformedURLException e2;
        String originFriendsUrlFromSynergy = getOriginFriendsUrlFromSynergy();
        String mdmAppKey = getMdmAppKey();
        if (originFriendsUrlFromSynergy == null || originFriendsUrlFromSynergy.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request because friends endpoint URI is empty or null");
            return null;
        } else if (mdmAppKey == null || mdmAppKey.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request because MDM app key is empty or null");
            return null;
        } else if (str3 == null || str3.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request friends API URI is empty or null");
            return null;
        } else if (str2 == null || str2.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request because access token is null or empty");
            return null;
        } else if (str == null || str.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request because user's nucleus ID is null or empty");
            return null;
        } else {
            HttpRequest httpRequest = null;
            HttpRequest httpRequest2 = null;
            try {
                HttpRequest httpRequest3 = new HttpRequest(new URL(originFriendsUrlFromSynergy + String.format(str3, str)));
                try {
                    httpRequest3.method = IHttpRequest.Method.GET;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("X-AuthToken", str2);
                    hashMap.put("X-Application-Key", mdmAppKey);
                    hashMap.put("X-Api-Version", "2");
                    httpRequest3.headers = hashMap;
                    return httpRequest3;
                } catch (Exception e4) {
                    e = e4;
                    httpRequest = httpRequest3;
                    Log.Helper.LOGE(this, "Exception when creating HTTP request URL. Exception: " + e.getMessage());
                    return httpRequest;
                }
            } catch (MalformedURLException e5) {
                e2 = e5;
            } catch (Exception e6) {
                e = e6;
            }
        }
        return new HttpRequest();
    }

    private HttpRequest makeRespondToFriendInvitationRequest(boolean z, String str, String str2, String str3) {
        Exception e;
        String originFriendsUrlFromSynergy = getOriginFriendsUrlFromSynergy();
        String mdmAppKey = getMdmAppKey();
        if (originFriendsUrlFromSynergy == null || originFriendsUrlFromSynergy.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for responding to an invitation because end point URI is null or empty");
            return null;
        } else if (mdmAppKey == null || mdmAppKey.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for responding to an invitation because MDM App Keyis null or empty");
            return null;
        } else if (str == null || str.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for responding to an invitation because nucleus ID is null or empty");
            return null;
        } else if (str2 == null || str2.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for responding to an invitation because friend ID is null or empty");
            return null;
        } else if (str3 == null || str3.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make HTTP request for responding to an invitation because access token is null or empty");
            return null;
        } else {
            HttpRequest httpRequest = null;
            HttpRequest httpRequest2 = null;
            try {
                HttpRequest httpRequest3 = new HttpRequest(new URL(originFriendsUrlFromSynergy + String.format(RESPOND_TO_FRIEND_INVITATION_URI, str, str2)));
                try {
                    if (z) {
                        httpRequest3.method = IHttpRequest.Method.POST;
                    } else {
                        httpRequest3.method = IHttpRequest.Method.DELETE;
                    }
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("X-AuthToken", str3);
                    hashMap.put("X-Application-Key", mdmAppKey);
                    hashMap.put("X-Api-Version", "2");
                    httpRequest3.headers = hashMap;
                    return httpRequest3;
                } catch (Exception e3) {
                    e = e3;
                    httpRequest = httpRequest3;
                    Log.Helper.LOGE(this, "Exception when creating HTTP request URL for responding to Friends request. Exception: " + e.getMessage());
                    return httpRequest;
                }
            } catch (MalformedURLException e4) {
                e = e4;
            } catch (Exception e5) {
                e = e5;
            }
        }
        return new HttpRequest();
    }

    private HttpRequest makeSearchUserRequest(String str, String str2, String str3, UserSearchCriteria userSearchCriteria) {
        Exception e;
        MalformedURLException e2;
        String identityProxyUrlFromSynergy = getIdentityProxyUrlFromSynergy();
        if (identityProxyUrlFromSynergy == null || identityProxyUrlFromSynergy.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make user search HTTP request because endpoint URI is empty or null");
            return null;
        } else if (str == null || str.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make user search HTTP request because AccessToken is empty or null");
            return null;
        } else if (str2 == null || str2.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make user search HTTP request TokenType is empty or null");
            return null;
        } else if (str3 == null || str3.length() <= 0) {
            Log.Helper.LOGW(this, "Cannot make user searchHTTP request because searchCriteria is null or empty");
            return null;
        } else {
            HttpRequest httpRequest = null;
            HttpRequest httpRequest2 = null;
            String str4 = identityProxyUrlFromSynergy + (userSearchCriteria == UserSearchCriteria.EMAIL ? String.format(SEARCH_USER_BY_EMAIL_URI, str3) : String.format(SEARCH_USER_BY_DISPLAY_NAME_URI, str3));
            String str5 = str2 + " " + str;
            try {
                HttpRequest httpRequest3 = new HttpRequest(new URL(str4));
                try {
                    httpRequest3.method = IHttpRequest.Method.GET;
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Authorization", str5);
                    hashMap.put("X-Include-Underage", "true");
                    hashMap.put("X-Expand-Results", "true");
                    httpRequest3.headers = hashMap;
                    return httpRequest3;
                } catch (Exception e4) {
                    e = e4;
                    httpRequest = httpRequest3;
                    Log.Helper.LOGE(this, "Exception when creating search user HTTP request URL. Exception: " + e.getMessage());
                    return httpRequest;
                }
            } catch (MalformedURLException e5) {
                e2 = e5;
            } catch (Exception e6) {
                e = e6;
            }
        }
        return new HttpRequest();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<NimbleUser> parseBodyJSONData(NetworkConnectionHandle networkConnectionHandle) throws Error {
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
        ArrayList<NimbleUser> arrayList = new ArrayList<>();
        if (str == null || str.length() <= 0) {
            Log.Helper.LOGE(this, "Generic Server error when retrieving GOS Friends.");
            throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "Generic Server error when retrieving GOS Friends.");
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            ArrayList<NimbleUser> arrayList2 = arrayList;
            if (jSONObject != null) {
                try {
                    if (jSONObject.optJSONObject("error") != null) {
                        JSONObject optJSONObject = jSONObject.optJSONObject("error");
                        String optString = optJSONObject.optString("type");
                        int optInt = optJSONObject.optInt("code", -1);
                        if (optString == null || optString.length() <= 0) {
                            arrayList2 = null;
                        } else {
                            throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, String.format("Code: %d, Type: %s", Integer.valueOf(optInt), optString));
                        }
                    } else {
                        JSONArray optJSONArray = jSONObject.optJSONArray("entries");
                        if (optJSONArray == null) {
                            JSONObject jSONObject2 = jSONObject.getJSONObject("error");
                            if (jSONObject2 != null) {
                                int optInt2 = jSONObject2.optInt("code", -1);
                                String optString2 = jSONObject2.optString("type", "");
                                Log.Helper.LOGE(this, String.format("Server error when retrieving GOS Friends. Code = %d, Message = %s", Integer.valueOf(optInt2), optString2));
                                throw new NimbleFriendsError(optInt2, optString2);
                            }
                            Log.Helper.LOGE(this, "Generic Server error when retrieving GOS Friends.");
                            throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "Generic Server error when retrieving GOS Friends.");
                        } else if (optJSONArray.length() <= 0) {
                            Log.Helper.LOGD(this, "No invitations found for your selected criteria");
                            return arrayList;
                        } else {
                            arrayList2 = arrayList;
                            if (optJSONArray != null) {
                                arrayList2 = arrayList;
                                if (optJSONArray.length() > 0) {
                                    int i = 0;
                                    while (true) {
                                        arrayList2 = arrayList;
                                        if (i >= optJSONArray.length()) {
                                            break;
                                        }
                                        JSONObject jSONObject3 = optJSONArray.getJSONObject(i);
                                        if (jSONObject3 != null) {
                                            NimbleUser createNimbleUserFromGosJson = createNimbleUserFromGosJson(jSONObject3);
                                            if (createNimbleUserFromGosJson.getUserId() != null && createNimbleUserFromGosJson.getUserId().length() > 0) {
                                                arrayList.add(createNimbleUserFromGosJson);
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e = e;
                    Log.Helper.LOGE(this, String.format("Exception when parsing JSON response. Error: %s", e.getMessage()));
                    throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, e.getMessage());
                }
            }
            return arrayList2;
        } catch (JSONException e2) {
        }
        return new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<NimbleUser> parseUserSearchByDisplayNameResponse(NetworkConnectionHandle networkConnectionHandle) throws Error {
        NimbleUser createNimbleUserFromNexusJson;
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
        ArrayList<NimbleUser> arrayList = new ArrayList<>();
        if (str == null || str.length() <= 0) {
            Log.Helper.LOGE(this, "Generic Server error when retrieving search user response.");
            throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "Generic Server error when retrieving search user response.");
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            ArrayList<NimbleUser> arrayList2 = arrayList;
            if (jSONObject != null) {
                if (jSONObject.optJSONObject("error") != null) {
                    String jSONObject2 = jSONObject.optJSONObject("error").toString();
                    if (jSONObject2 == null || jSONObject2.length() <= 0) {
                        arrayList2 = null;
                    } else {
                        throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, jSONObject2);
                    }
                } else {
                    JSONObject optJSONObject = jSONObject.optJSONObject("personas");
                    if (optJSONObject != null) {
                        JSONArray optJSONArray = optJSONObject.optJSONArray("persona");
                        if (optJSONArray != null && optJSONArray.length() > 0) {
                            int i = 0;
                            while (true) {
                                arrayList2 = arrayList;
                                if (i >= optJSONArray.length()) {
                                    break;
                                }
                                JSONObject optJSONObject2 = optJSONArray.optJSONObject(i);
                                if (!(optJSONObject2 == null || (createNimbleUserFromNexusJson = createNimbleUserFromNexusJson(optJSONObject2)) == null)) {
                                    arrayList.add(createNimbleUserFromNexusJson);
                                }
                                i++;
                            }
                        } else {
                            Log.Helper.LOGD(this, "Search response indicates that no user was found with the display name prefix that you searched for.");
                            return arrayList;
                        }
                    } else {
                        Log.Helper.LOGE(this, "Unable to parse response from server - no personas object found");
                        throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "Unable to parse response from server - no personas object found");
                    }
                }
            }
            return arrayList2;
        } catch (JSONException e2) {
        }
        return new ArrayList<>();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<NimbleUser> parseUserSearchByEmailResponse(NetworkConnectionHandle networkConnectionHandle) throws Error {
        NimbleUser createNimbleUserFromNexusJson;
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
        ArrayList<NimbleUser> arrayList = new ArrayList<>();
        if (str == null || str.length() <= 0) {
            Log.Helper.LOGE(this, "Generic Server error when retrieving search user response.");
            throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "Generic Server error when retrieving search user response.");
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            ArrayList<NimbleUser> arrayList2 = arrayList;
            if (jSONObject != null) {
                if (jSONObject.optJSONObject("error") != null) {
                    String jSONObject2 = jSONObject.optJSONObject("error").toString();
                    if (jSONObject2 == null || jSONObject2.length() <= 0) {
                        arrayList2 = null;
                    } else {
                        throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, jSONObject2);
                    }
                } else {
                    JSONObject optJSONObject = jSONObject.optJSONObject("pids");
                    if (optJSONObject != null) {
                        JSONArray optJSONArray = optJSONObject.optJSONArray("pid");
                        if (optJSONArray != null && optJSONArray.length() > 0) {
                            int i = 0;
                            while (true) {
                                arrayList2 = arrayList;
                                if (i >= optJSONArray.length()) {
                                    break;
                                }
                                JSONObject optJSONObject2 = optJSONArray.optJSONObject(i);
                                if (!(optJSONObject2 == null || (createNimbleUserFromNexusJson = createNimbleUserFromNexusJson(optJSONObject2)) == null)) {
                                    arrayList.add(createNimbleUserFromNexusJson);
                                }
                                i++;
                            }
                        } else {
                            Log.Helper.LOGD(this, "Search response indicates that no user was found with the email address that you searched for.");
                            return arrayList;
                        }
                    } else {
                        Log.Helper.LOGE(this, "Unable to parse response from server - no pids object found");
                        throw new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "Unable to parse response from server - no pids object found");
                    }
                }
            }
            return arrayList2;
        } catch (JSONException e2) {
        }
        return new ArrayList<>();
    }

    private void processInivtationListRequest(final String str, final INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        if (!isNimbleComponentAvailable("com.ea.nimble.identity")) {
            Log.Helper.LOGE(this, "Unable to process request because NimbleIdentity is not available");
            invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Unable to process request because NimbleIdentity is not available");
            return;
        }
        INimbleIdentity iNimbleIdentity = (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
        NimbleIdentityPidInfo pidInfo = iNimbleIdentity.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ORIGIN).getPidInfo();
        String str2 = null;
        if (pidInfo != null) {
            str2 = pidInfo.getPid();
        }
        if (str2 == null || str2.length() <= 0) {
            invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_LOGGED_IN, "Origin PID for the current user is not available.");
        } else {
            String finalStr = str2;
            iNimbleIdentity.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ORIGIN).requestAccessToken(new INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.3
                @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback
                public void AccessTokenCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str3, String str4, Error error) {
                    if (error != null) {
                        Log.Helper.LOGE(this, "Failed to refresh AccessToken - cannot process request.");
                        NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithError(nimbleUserSearchCallback, error);
                        return;
                    }
                    NimbleOriginFriendsServiceImpl.this.sendGetFriendInvitationListRequest(finalStr, str3, str, nimbleUserSearchCallback);
                }
            });
        }
    }

    private void processRespondToFriendInvitationRequest(final boolean z, final String str, final INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        if (!isNimbleComponentAvailable("com.ea.nimble.identity")) {
            Log.Helper.LOGE(this, "Unable to process request for responding to friend invitation because NimbleIdentity is not available");
            invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Unable to process request for responding to friend invitation because NimbleIdentity is not available");
            return;
        }
        INimbleIdentity iNimbleIdentity = (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
        NimbleIdentityPidInfo pidInfo = iNimbleIdentity.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ORIGIN).getPidInfo();
        String str2 = null;
        if (pidInfo != null) {
            str2 = pidInfo.getPid();
        }
        if (str2 == null || str2.length() <= 0) {
            invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_LOGGED_IN, "Origin PID for the current user is not available.");
        } else {
            String finalStr = str2;
            iNimbleIdentity.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ORIGIN).requestAccessToken(new INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.2
                @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback
                public void AccessTokenCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str3, String str4, Error error) {
                    if (error != null) {
                        Log.Helper.LOGE(this, "Failed to refresh AccessToken - cannot process request for responding to friend invitation.");
                        NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithError(nimbleFriendInvitationCallback, error);
                        return;
                    }
                    NimbleOriginFriendsServiceImpl.this.sendRespondToFriendInvitationRequest(z, finalStr, str, str3, nimbleFriendInvitationCallback);
                }
            });
        }
    }

    private void processSearchUserRequest(final String str, final UserSearchCriteria userSearchCriteria, final INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        if (!isNimbleComponentAvailable("com.ea.nimble.identity")) {
            Log.Helper.LOGE(this, "Unable to process request because NimbleIdentity is not available");
            invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Unable to process request because NimbleIdentity is not available");
            return;
        }
        ((INimbleIdentity) Base.getComponent("com.ea.nimble.identity")).getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ORIGIN).requestAccessToken(new INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.4
            @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback
            public void AccessTokenCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str2, String str3, Error error) {
                if (error != null) {
                    Log.Helper.LOGE(this, "Failed to refresh AccessToken - cannot process request.");
                    NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithError(nimbleUserSearchCallback, error);
                    return;
                }
                NimbleOriginFriendsServiceImpl.this.sendSearchUserRequest(str2, str3, str, userSearchCriteria, nimbleUserSearchCallback);
            }
        });
    }

    private void processSendFriendInvitationRequest(final String str, final String str2, final INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        if (!isNimbleComponentAvailable("com.ea.nimble.identity")) {
            Log.Helper.LOGE(this, "Unable to send friend request because NimbleIdentity is not available");
            invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Unable to send friend request because NimbleIdentity is not available");
            return;
        }
        INimbleIdentity iNimbleIdentity = (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
        NimbleIdentityPidInfo pidInfo = iNimbleIdentity.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ORIGIN).getPidInfo();
        String str3 = null;
        if (pidInfo != null) {
            str3 = pidInfo.getPid();
        }
        if (str3 == null || str3.length() <= 0) {
            invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_LOGGED_IN, "Origin PID for the current user is not available.");
        } else {
            String finalStr = str3;
            iNimbleIdentity.getAuthenticatorById(Global.NIMBLE_AUTHENTICATOR_ORIGIN).requestAccessToken(new INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.1
                @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleAuthenticatorAccessTokenCallback
                public void AccessTokenCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, String str4, String str5, Error error) {
                    if (error != null) {
                        Log.Helper.LOGE(this, "Failed to refresh AccessToken - cannot send friend invitation request.");
                        NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithError(nimbleFriendInvitationCallback, error);
                        return;
                    }
                    NimbleOriginFriendsServiceImpl.this.sendFriendInvitationRequest(finalStr, str4, str, str2, nimbleFriendInvitationCallback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendFriendInvitationRequest(String str, String str2, String str3, String str4, final INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        try {
            HttpRequest makeFriendInvitationRequest = makeFriendInvitationRequest(str, str2, str3, str4, nimbleFriendInvitationCallback);
            if (makeFriendInvitationRequest == null) {
                Log.Helper.LOGE(this, "Failed to create HTTP Request for sending friend invitation");
                invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_FAILED_TO_CREATE_GOS_REQUEST, "Failed to create HTTP Request for sending friend invitation");
                return;
            }
            Network.getComponent().sendRequest(makeFriendInvitationRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.5
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    try {
                        if (networkConnectionHandle.getResponse().getStatusCode() == 204) {
                            NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithSuccess(nimbleFriendInvitationCallback);
                            return;
                        }
                        Log.Helper.LOGD(this, "Server responded with an error for send friend invitation request");
                        NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_SERVER_RETURNED_ERROR, "Server responded with an error for send friend invitation request");
                    } catch (Exception e) {
                        Log.Helper.LOGE(this, e.getMessage());
                        NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.Helper.LOGE(this, e.getMessage());
            invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendGetFriendInvitationListRequest(String str, String str2, String str3, final INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        try {
            HttpRequest makeGetFriendInvitationListRequest = makeGetFriendInvitationListRequest(str, str2, str3);
            if (makeGetFriendInvitationListRequest == null) {
                Log.Helper.LOGE(this, "Failed to create HTTP Request for retrieving outbound friend invitation list");
                invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_FAILED_TO_CREATE_GOS_REQUEST, "Failed to create HTTP Request for retrieving outbound friend invitation list");
                return;
            }
            Network.getComponent().sendRequest(makeGetFriendInvitationListRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.7
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    try {
                        ArrayList parseBodyJSONData = NimbleOriginFriendsServiceImpl.this.parseBodyJSONData(networkConnectionHandle);
                        if (parseBodyJSONData == null) {
                            Log.Helper.LOGE(this, "Error in response from GOS server. Unable to get list of invitations");
                            NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "Error in response from GOS server. Unable to get list of invitations");
                        } else if (parseBodyJSONData.size() <= 0) {
                            Log.Helper.LOGD(this, "Server request was successful, but we did not find any pending invitations");
                            NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithSuccess(nimbleUserSearchCallback, parseBodyJSONData);
                        } else {
                            Log.Helper.LOGD(this, "Successful in retrieving invitation list from GOS");
                            NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithSuccess(nimbleUserSearchCallback, parseBodyJSONData);
                        }
                    } catch (Error e) {
                        Log.Helper.LOGE(this, "Error parsing response from GOS" + e.getMessage());
                        NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithError(nimbleUserSearchCallback, e);
                    } catch (Exception e2) {
                        Log.Helper.LOGE(this, e2.getMessage());
                        NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, e2.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.Helper.LOGE(this, e.getMessage());
            invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendRespondToFriendInvitationRequest(boolean z, String str, String str2, String str3, final INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        try {
            HttpRequest makeRespondToFriendInvitationRequest = makeRespondToFriendInvitationRequest(z, str, str2, str3);
            if (makeRespondToFriendInvitationRequest == null) {
                Log.Helper.LOGE(this, "Failed to create HTTP Request for respoding to friend invitation");
                invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_FAILED_TO_CREATE_GOS_REQUEST, "Failed to create HTTP Request for respoding to friend invitation");
                return;
            }
            Network.getComponent().sendRequest(makeRespondToFriendInvitationRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.6
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    try {
                        if (networkConnectionHandle.getResponse().getStatusCode() == 204) {
                            NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithSuccess(nimbleFriendInvitationCallback);
                            return;
                        }
                        Log.Helper.LOGD(this, "Error processing the respond to friend invitation request");
                        NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_SERVER_RETURNED_ERROR, "Error processing the respond to friend invitation request");
                    } catch (Exception e) {
                        Log.Helper.LOGE(this, e.getMessage());
                        NimbleOriginFriendsServiceImpl.this.invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            Log.Helper.LOGE(this, e.getMessage());
            invokeFriendInvitationCallbackWithCode(nimbleFriendInvitationCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, e.getMessage());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendSearchUserRequest(String str, String str2, String str3, final UserSearchCriteria userSearchCriteria, final INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        try {
            HttpRequest makeSearchUserRequest = makeSearchUserRequest(str, str2, str3, userSearchCriteria);
            if (makeSearchUserRequest == null) {
                Log.Helper.LOGE(this, "Failed to create HTTP Request for user search");
                invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_FAILED_TO_CREATE_GOS_REQUEST, "Failed to create HTTP Request for user search");
                return;
            }
            Network.getComponent().sendRequest(makeSearchUserRequest, new NetworkConnectionCallback() { // from class: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.8
                @Override // com.ea.nimble.NetworkConnectionCallback
                public void callback(NetworkConnectionHandle networkConnectionHandle) {
                    try {
                        ArrayList parseUserSearchByEmailResponse = userSearchCriteria == UserSearchCriteria.EMAIL ? NimbleOriginFriendsServiceImpl.this.parseUserSearchByEmailResponse(networkConnectionHandle) : NimbleOriginFriendsServiceImpl.this.parseUserSearchByDisplayNameResponse(networkConnectionHandle);
                        if (parseUserSearchByEmailResponse == null) {
                            Log.Helper.LOGE(this, "There was an error processing your user search request");
                            NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, "There was an error processing your user search request");
                        } else if (parseUserSearchByEmailResponse.size() <= 0) {
                            Log.Helper.LOGD(this, "No users found for your search criteria. Please try again with a different criteria.");
                            NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithSuccess(nimbleUserSearchCallback, parseUserSearchByEmailResponse);
                        } else {
                            Log.Helper.LOGD(this, "Found users with matching email or display name prefix");
                            NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithSuccess(nimbleUserSearchCallback, parseUserSearchByEmailResponse);
                        }
                    } catch (Error e) {
                        Log.Helper.LOGE(this, "Error parsing response for user search by email or displayName request" + e.getMessage());
                        NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithError(nimbleUserSearchCallback, e);
                    } catch (Exception e2) {
                        String str4 = "Error parsing response for user search by email or displayName request" + e2.getMessage();
                        Log.Helper.LOGE(this, str4);
                        NimbleOriginFriendsServiceImpl.this.invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, str4);
                    }
                }
            });
        } catch (Exception e) {
            String str4 = "Error parsing response for user search by email or displayName request" + e.getMessage();
            Log.Helper.LOGE(this, str4);
            invokeUserSearchCallbackWithCode(nimbleUserSearchCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_ORIGIN_SERVICES_SERVER_RESPONSE_ERROR, str4);
        }
    }

    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    public void acceptFriendInvitation(String str, INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        if (nimbleFriendInvitationCallback == null) {
            Log.Helper.LOGE(this, "Cannot process acceptFriendInvitation request because callback is null");
            return;
        }
        Log.Helper.LOGD(this, "Request: acceptFriendInvitation");
        processRespondToFriendInvitationRequest(true, str, nimbleFriendInvitationCallback);
    }

    @Override // com.ea.nimble.Component
    public void cleanup() {
        Log.Helper.LOGV(this, "Component cleanup");
    }

    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    public void declineFriendInvitation(String str, INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        if (nimbleFriendInvitationCallback == null) {
            Log.Helper.LOGE(this, "Cannot process declineFriendInvitation request because callback is null");
            return;
        }
        Log.Helper.LOGD(this, "Request: declineFriendInvitation");
        processRespondToFriendInvitationRequest(false, str, nimbleFriendInvitationCallback);
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return NimbleOriginFriendsService.NIMBLE_COMPONENT_ID_FRIENDS_ORIGIN;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "NimbleOriginFriendsService";
    }

    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    public void listFriendInvitationsReceived(INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        if (nimbleUserSearchCallback == null) {
            Log.Helper.LOGE(this, "Cannot process listFriendInvitationReceived request because callback is null");
            return;
        }
        Log.Helper.LOGD(this, "Request: listFriendInvitationReceived");
        processInivtationListRequest(GET_RECEIVED_INVITATION_LIST_URI, nimbleUserSearchCallback);
    }

    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    public void listFriendInvitationsSent(INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        if (nimbleUserSearchCallback == null) {
            Log.Helper.LOGE(this, "Cannot process listFriendInvitationSent request because callback is null");
            return;
        }
        Log.Helper.LOGD(this, "Request: listFriendInvitationSent");
        processInivtationListRequest(GET_SENT_INVITATION_LIST_URI, nimbleUserSearchCallback);
    }

    @Override // com.ea.nimble.Component
    public void restore() {
        Log.Helper.LOGV(this, "Component restore");
    }

    @Override // com.ea.nimble.Component
    public void resume() {
        Log.Helper.LOGV(this, "Component resume");
    }

    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    public void searchUserByDisplayName(String str, INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        if (nimbleUserSearchCallback == null) {
            Log.Helper.LOGE(this, "Cannot process searchUserByDisplayName request because callback is null");
            return;
        }
        Log.Helper.LOGD(this, String.format("searchUserByDisplayName API called with namePrefix = %s", str));
        processSearchUserRequest(str, UserSearchCriteria.DISPLAY_NAME, nimbleUserSearchCallback);
    }

    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    public void searchUserByEmail(String str, INimbleOriginFriendsService.NimbleUserSearchCallback nimbleUserSearchCallback) {
        if (nimbleUserSearchCallback == null) {
            Log.Helper.LOGE(this, "Cannot process searchUserByEmail request because callback is null");
            return;
        }
        Log.Helper.LOGD(this, String.format("searchUserByEmail API called with email = %s", str));
        processSearchUserRequest(str, UserSearchCriteria.EMAIL, nimbleUserSearchCallback);
    }

    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    public void sendFriendInvitation(String str, String str2, INimbleOriginFriendsService.NimbleFriendInvitationCallback nimbleFriendInvitationCallback) {
        if (nimbleFriendInvitationCallback == null) {
            Log.Helper.LOGE(this, "Cannot process sendFriendInvitation request because callback is null");
            return;
        }
        Log.Helper.LOGD(this, "Request: sendFriendInvitation");
        processSendFriendInvitationRequest(str, str2, nimbleFriendInvitationCallback);
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0052, code lost:
        if (r9.length() <= 0) goto L_0x0055;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0063, code lost:
        if (r10.length() <= 0) goto L_0x0066;
     */
    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void sendInvitationOverEmail(java.util.ArrayList<java.lang.String> r8, java.lang.String r9, java.lang.String r10, com.ea.nimble.friends.INimbleOriginFriendsService.NimbleFriendInvitationCallback r11) {
        /*
            r7 = this;
            r0 = r8
            if (r0 == 0) goto L_0x000b
            r0 = r8
            int r0 = r0.size()
            if (r0 > 0) goto L_0x0031
        L_0x000b:
            r0 = r7
            java.lang.String r1 = "Target user emails is null or empty. Cannot send email invite."
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]
            com.ea.nimble.Log.Helper.LOGE(r0, r1, r2)
            r0 = r11
            if (r0 == 0) goto L_0x0030
            r0 = r11
            r1 = 0
            com.ea.nimble.friends.NimbleFriendsError r2 = new com.ea.nimble.friends.NimbleFriendsError
            r3 = r2
            com.ea.nimble.friends.NimbleFriendsError$Code r4 = com.ea.nimble.friends.NimbleFriendsError.Code.NIMBLE_FRIENDS_NO_TARGETS_PROVIDED
            java.lang.String r5 = "Target user emails is null or empty. Cannot send email invite."
            r3.<init>(r4, r5)
            r0.onCallback(r1, r2)
        L_0x0030:
            return
        L_0x0031:
            r0 = 0
            r12 = r0
            r0 = r8
            if (r0 == 0) goto L_0x0048
            r0 = r8
            r1 = r8
            int r1 = r1.size()
            java.lang.String[] r1 = new java.lang.String[r1]
            java.lang.Object[] r0 = r0.toArray(r1)
            java.lang.String[] r0 = (java.lang.String[]) r0
            r12 = r0
        L_0x0048:
            r0 = r9
            if (r0 == 0) goto L_0x0055
            r0 = r9
            r8 = r0
            r0 = r9
            int r0 = r0.length()
            if (r0 > 0) goto L_0x0059
        L_0x0055:
            java.lang.String r0 = "Please accept my friend invitation"
            r8 = r0
        L_0x0059:
            r0 = r10
            if (r0 == 0) goto L_0x0066
            r0 = r10
            r9 = r0
            r0 = r10
            int r0 = r0.length()
            if (r0 > 0) goto L_0x006a
        L_0x0066:
            java.lang.String r0 = "Hi, I'll appreciate if you can accept my friend invitation."
            r9 = r0
        L_0x006a:
            android.content.Intent r0 = new android.content.Intent
            r1 = r0
            java.lang.String r2 = "android.intent.action.SEND"
            r1.<init>(r2)
            r10 = r0
            r0 = r10
            java.lang.String r1 = "message/rfc822"
            android.content.Intent r0 = r0.setType(r1)
            r0 = r10
            java.lang.String r1 = "android.intent.extra.EMAIL"
            r2 = r12
            android.content.Intent r0 = r0.putExtra(r1, r2)
            r0 = r10
            java.lang.String r1 = "android.intent.extra.SUBJECT"
            r2 = r8
            android.content.Intent r0 = r0.putExtra(r1, r2)
            r0 = r10
            java.lang.String r1 = "android.intent.extra.TEXT"
            r2 = r9
            android.content.Intent r0 = r0.putExtra(r1, r2)
            android.app.Activity r0 = com.ea.nimble.ApplicationEnvironment.getCurrentActivity()     // Catch: Exception -> 0x00b8
            r1 = r10
            java.lang.String r2 = "Send mail using:"
            android.content.Intent r1 = android.content.Intent.createChooser(r1, r2)     // Catch: Exception -> 0x00b8
            int r2 = com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.EMAIL_REQUEST_CODE     // Catch: Exception -> 0x00b8
            r0.startActivityForResult(r1, r2)     // Catch: Exception -> 0x00b8
        L_0x00a9:
            r0 = r11
            if (r0 == 0) goto L_0x0030
            r0 = r11
            r1 = 1
            r2 = 0
            r0.onCallback(r1, r2)
            return
        L_0x00b8:
            r8 = move-exception
            r0 = r7
            java.lang.String r1 = "Can not send email on this device"
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]
            com.ea.nimble.Log.Helper.LOGE(r0, r1, r2)
            r0 = r11
            if (r0 == 0) goto L_0x00a9
            r0 = r11
            r1 = 0
            com.ea.nimble.friends.NimbleFriendsError r2 = new com.ea.nimble.friends.NimbleFriendsError
            r3 = r2
            com.ea.nimble.friends.NimbleFriendsError$Code r4 = com.ea.nimble.friends.NimbleFriendsError.Code.NIMBLE_FRIENDS_EMAIL_NOT_AVAILABLE
            java.lang.String r5 = "Email is not available on this device"
            r3.<init>(r4, r5)
            r0.onCallback(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.sendInvitationOverEmail(java.util.ArrayList, java.lang.String, java.lang.String, com.ea.nimble.friends.INimbleOriginFriendsService$NimbleFriendInvitationCallback):void");
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0081, code lost:
        if (r9.length() <= 0) goto L_0x0084;
     */
    @Override // com.ea.nimble.friends.INimbleOriginFriendsService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void sendInvitationOverSMS(java.util.ArrayList<java.lang.String> r8, java.lang.String r9, com.ea.nimble.friends.INimbleOriginFriendsService.NimbleFriendInvitationCallback r10) {
        /*
            r7 = this;
            r0 = r8
            if (r0 == 0) goto L_0x000b
            r0 = r8
            int r0 = r0.size()
            if (r0 > 0) goto L_0x002f
        L_0x000b:
            r0 = r7
            java.lang.String r1 = "Target phone numbers is null or empty. Cannot send SMS invite."
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]
            com.ea.nimble.Log.Helper.LOGE(r0, r1, r2)
            r0 = r10
            if (r0 == 0) goto L_0x002e
            r0 = r10
            r1 = 0
            com.ea.nimble.friends.NimbleFriendsError r2 = new com.ea.nimble.friends.NimbleFriendsError
            r3 = r2
            com.ea.nimble.friends.NimbleFriendsError$Code r4 = com.ea.nimble.friends.NimbleFriendsError.Code.NIMBLE_FRIENDS_NO_TARGETS_PROVIDED
            java.lang.String r5 = "Target phone numbers is null or empty. Cannot send SMS invite."
            r3.<init>(r4, r5)
            r0.onCallback(r1, r2)
        L_0x002e:
            return
        L_0x002f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r1 = r0
            r1.<init>()
            r11 = r0
            r0 = r11
            java.lang.String r1 = "smsto:"
            java.lang.StringBuilder r0 = r0.append(r1)
            r0 = r8
            java.util.Iterator r0 = r0.iterator()
            r8 = r0
        L_0x0046:
            r0 = r8
            boolean r0 = r0.hasNext()
            if (r0 == 0) goto L_0x006a
            r0 = r11
            r1 = r8
            java.lang.Object r1 = r1.next()
            java.lang.String r1 = (java.lang.String) r1
            java.lang.StringBuilder r0 = r0.append(r1)
            r0 = r11
            java.lang.String r1 = ";"
            java.lang.StringBuilder r0 = r0.append(r1)
            goto L_0x0046
        L_0x006a:
            r0 = r11
            r1 = r11
            int r1 = r1.length()
            r2 = 1
            int r1 = r1 - r2
            java.lang.StringBuilder r0 = r0.deleteCharAt(r1)
            r0 = r9
            if (r0 == 0) goto L_0x0084
            r0 = r9
            r8 = r0
            r0 = r9
            int r0 = r0.length()
            if (r0 > 0) goto L_0x0088
        L_0x0084:
            java.lang.String r0 = "Hi, I'll appreciate if you can accept my friend invitation."
            r8 = r0
        L_0x0088:
            android.content.Intent r0 = new android.content.Intent
            r1 = r0
            java.lang.String r2 = "android.intent.action.SENDTO"
            r3 = r11
            java.lang.String r3 = r3.toString()
            android.net.Uri r3 = android.net.Uri.parse(r3)
            r1.<init>(r2, r3)
            r9 = r0
            r0 = r9
            java.lang.String r1 = "sms_body"
            r2 = r8
            android.content.Intent r0 = r0.putExtra(r1, r2)
            android.app.Activity r0 = com.ea.nimble.ApplicationEnvironment.getCurrentActivity()     // Catch: Exception -> 0x00c1
            r1 = r9
            java.lang.String r2 = "Send sms using:"
            android.content.Intent r1 = android.content.Intent.createChooser(r1, r2)     // Catch: Exception -> 0x00c1
            int r2 = com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.SMS_REQUEST_CODE     // Catch: Exception -> 0x00c1
            r0.startActivityForResult(r1, r2)     // Catch: Exception -> 0x00c1
        L_0x00b4:
            r0 = r10
            if (r0 == 0) goto L_0x002e
            r0 = r10
            r1 = 1
            r2 = 0
            r0.onCallback(r1, r2)
            return
        L_0x00c1:
            r8 = move-exception
            r0 = r7
            java.lang.String r1 = "Can not send sms on this device"
            r2 = 0
            java.lang.Object[] r2 = new java.lang.Object[r2]
            com.ea.nimble.Log.Helper.LOGE(r0, r1, r2)
            r0 = r10
            if (r0 == 0) goto L_0x00b4
            r0 = r10
            r1 = 0
            com.ea.nimble.friends.NimbleFriendsError r2 = new com.ea.nimble.friends.NimbleFriendsError
            r3 = r2
            com.ea.nimble.friends.NimbleFriendsError$Code r4 = com.ea.nimble.friends.NimbleFriendsError.Code.NIMBLE_FRIENDS_SMS_NOT_AVAILABLE
            java.lang.String r5 = "Sms is not available on this device"
            r3.<init>(r4, r5)
            r0.onCallback(r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.ea.nimble.friends.NimbleOriginFriendsServiceImpl.sendInvitationOverSMS(java.util.ArrayList, java.lang.String, com.ea.nimble.friends.INimbleOriginFriendsService$NimbleFriendInvitationCallback):void");
    }

    @Override // com.ea.nimble.Component
    public void setup() {
        Log.Helper.LOGD(this, "Component setup");
    }

    @Override // com.ea.nimble.Component
    public void suspend() {
        Log.Helper.LOGV(this, "Component suspend");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.Component
    public void teardown() {
        Log.Helper.LOGV(this, "Component teardown");
    }
}
