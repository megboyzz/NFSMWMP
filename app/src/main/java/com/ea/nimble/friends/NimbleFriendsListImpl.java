package com.ea.nimble.friends;

import android.annotation.SuppressLint;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Network;
import com.ea.nimble.SynergyEnvironment;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentity;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

@SuppressLint({"DefaultLocale"})
/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsListImpl.class */
public abstract class NimbleFriendsListImpl implements LogSource {
    protected NimbleFriendsList m_nimbleFriendsList;
    protected NimbleFriendsList m_nimblePlayedFriendsList;
    protected Hashtable<String, NimbleUser> m_friends = new Hashtable<>();
    protected ArrayList<String> m_friendsList = new ArrayList<>();
    protected ArrayList<String> m_playedFriendsList = new ArrayList<>();
    protected int m_totalFriends = -1;
    protected int m_totalPlayedFriends = -1;
    protected String LOG_SOURCE_TITLE = "NimbleFriendsListImpl";
    protected int m_pageSize = 100;
    protected String m_authenticatorId = "";

    public NimbleFriendsListImpl() {
        Log.Helper.LOGV(this, "No default implementation for NimbleFriendsListImpl - Must construct a typed-NimbleFriendsListImpl", new Object[0]);
    }

    private Error environmentCheck() {
        Log.Helper.LOGV(this, "Environment Check -->", new Object[0]);
        if (Network.getComponent().getStatus() != Network.Status.OK) {
            Log.Helper.LOGD(this, "Environment Check - Network unavailable", new Object[0]);
            return new Error(Error.Code.NETWORK_NO_CONNECTION, "Friends component cannot do updates without network");
        } else if (SynergyEnvironment.getComponent().isDataAvailable()) {
            return null;
        } else {
            Log.Helper.LOGD(this, "Environment Check - Synergy Environment Not Ready", new Object[0]);
            return new Error(Error.Code.SYNERGY_GET_DIRECTION_TIMEOUT, "Friends component is still in initialization and not ready for operation");
        }
    }

    public void clear() {
        synchronized (this) {
            if (this.m_friends != null) {
                this.m_friends.clear();
            }
            if (this.m_friendsList != null) {
                this.m_friendsList.clear();
            }
            if (this.m_playedFriendsList != null) {
                this.m_playedFriendsList.clear();
            }
            this.m_totalFriends = -1;
            this.m_totalPlayedFriends = -1;
        }
    }

    protected abstract NimbleUser createNimbleUser(JSONObject jSONObject);

    public NimbleFriendsList getFriendsList(boolean z) {
        synchronized (this) {
            if (z) {
                Log.Helper.LOGV(this, "Returning NimbleFriendsList with playedCurrentGameOnly flag returned", new Object[0]);
                return this.m_nimblePlayedFriendsList;
            }
            Log.Helper.LOGV(this, "Returning NimbleFriendsList with all friends", new Object[0]);
            return this.m_nimbleFriendsList;
        }
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return this.LOG_SOURCE_TITLE;
    }

    public int getPageSize() {
        return this.m_pageSize;
    }

    void invokeCallbackWithBasicScopeError(NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, Error error, NimbleFriendsList.FriendsListType friendsListType) {
        if (nimbleFriendsRefreshCallback != null) {
            NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult = new NimbleFriendsRangeRefreshResult();
            nimbleFriendsRangeRefreshResult.m_success = false;
            nimbleFriendsRangeRefreshResult.m_error = error;
            nimbleFriendsRangeRefreshResult.m_userList = null;
            nimbleFriendsRangeRefreshResult.m_startIndex = nimbleFriendsRefreshBasicInfo.getStartIndex();
            nimbleFriendsRangeRefreshResult.m_size = nimbleFriendsRefreshBasicInfo.getRange();
            nimbleFriendsRangeRefreshResult.m_friendListEndInRefresh = false;
            if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalFriends;
                nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                return;
            }
            nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalPlayedFriends;
            nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
        }
    }

    void invokeCallbackWithBasicScopeError(NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsError.Code code, String str, NimbleFriendsList.FriendsListType friendsListType) {
        if (nimbleFriendsRefreshCallback != null) {
            NimbleFriendsError nimbleFriendsError = new NimbleFriendsError(code, str);
            NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult = new NimbleFriendsRangeRefreshResult();
            nimbleFriendsRangeRefreshResult.m_success = false;
            nimbleFriendsRangeRefreshResult.m_error = nimbleFriendsError;
            nimbleFriendsRangeRefreshResult.m_userList = null;
            nimbleFriendsRangeRefreshResult.m_startIndex = nimbleFriendsRefreshBasicInfo.getStartIndex();
            nimbleFriendsRangeRefreshResult.m_size = nimbleFriendsRefreshBasicInfo.getRange();
            nimbleFriendsRangeRefreshResult.m_friendListEndInRefresh = false;
            if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalFriends;
                nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                return;
            }
            nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalPlayedFriends;
            nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
        }
    }

    void invokeCallbackWithScopeError(NimbleFriendsRefreshScope nimbleFriendsRefreshScope, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, Error error, NimbleFriendsList.FriendsListType friendsListType) {
        if (nimbleFriendsRefreshCallback != null) {
            NimbleFriendsRefreshResult nimbleFriendsRefreshResult = new NimbleFriendsRefreshResult();
            nimbleFriendsRefreshResult.m_success = false;
            nimbleFriendsRefreshResult.m_error = error;
            nimbleFriendsRefreshResult.m_userList = null;
            if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
            } else {
                nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
            }
        }
    }

    void invokeCallbackWithScopeError(NimbleFriendsRefreshScope nimbleFriendsRefreshScope, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsError.Code code, String str, NimbleFriendsList.FriendsListType friendsListType) {
        if (nimbleFriendsRefreshCallback != null) {
            NimbleFriendsError nimbleFriendsError = new NimbleFriendsError(code, str);
            NimbleFriendsRefreshResult nimbleFriendsRefreshResult = new NimbleFriendsRefreshResult();
            nimbleFriendsRefreshResult.m_success = false;
            nimbleFriendsRefreshResult.m_error = nimbleFriendsError;
            nimbleFriendsRefreshResult.m_userList = null;
            if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
            } else {
                nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
            }
        }
    }

    protected boolean isNimbleComponentAvailable(String str) {
        boolean z = false;
        if (Base.getComponent(str) != null) {
            z = true;
        }
        return z;
    }

    @SuppressLint({"DefaultLocale"})
    public void refreshFriendsList(NimbleFriendsRefreshScope nimbleFriendsRefreshScope, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsList.FriendsListType friendsListType) {
        int i;
        Object obj;
        String str;
        int i2;
        String str2;
        Log.Helper.LOGV(this, "refreshFriendsList API called", new Object[0]);
        Error environmentCheck = environmentCheck();
        if (environmentCheck == null) {
            if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                Log.Helper.LOGV(this, "Refresh API called for All Friends List", new Object[0]);
                i = 0;
                obj = "ALL_FRIENDS";
                if (this.m_friendsList != null) {
                    i = 0;
                    obj = "ALL_FRIENDS";
                    if (this.m_friendsList.size() > 0) {
                        i = this.m_friendsList.size() - 1;
                        obj = "ALL_FRIENDS";
                    }
                }
            } else {
                Log.Helper.LOGV(this, "Refresh API called for Played Current Game Friends List", new Object[0]);
                i = 0;
                obj = "CURRENT_GAME_FRIENDS";
                if (this.m_playedFriendsList != null) {
                    i = 0;
                    obj = "CURRENT_GAME_FRIENDS";
                    if (this.m_playedFriendsList.size() > 0) {
                        i = this.m_playedFriendsList.size() - 1;
                        obj = "CURRENT_GAME_FRIENDS";
                    }
                }
            }
            if (nimbleFriendsRefreshScope == null) {
                Log.Helper.LOGW(this, "NimbleFriendsRefreshScope is null. Unable to process the request", new Object[0]);
                invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_INVALID, "NimbleFriendsRefreshScope is null. Unable to process the request", friendsListType);
            } else if (nimbleFriendsRefreshScope instanceof NimbleFriendsRefreshBasicInfo) {
                NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo = (NimbleFriendsRefreshBasicInfo) nimbleFriendsRefreshScope;
                int startIndex = nimbleFriendsRefreshBasicInfo.getStartIndex();
                int range = nimbleFriendsRefreshBasicInfo.getRange();
                if (nimbleFriendsRefreshBasicInfo.getNextPage()) {
                    Log.Helper.LOGV(this, "Refresh API is called to get the next page of friends", new Object[0]);
                    int i3 = this.m_pageSize;
                    if (friendsListType != NimbleFriendsList.FriendsListType.ALL_FRIENDS || this.m_friendsList == null || this.m_friendsList.size() <= 0) {
                        i2 = 0;
                        str2 = "";
                        if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
                            i2 = 0;
                            str2 = "";
                            if (this.m_playedFriendsList != null) {
                                i2 = 0;
                                str2 = "";
                                if (this.m_playedFriendsList.size() > 0) {
                                    str2 = this.m_playedFriendsList.get(i);
                                    i2 = i;
                                }
                            }
                        }
                    } else {
                        str2 = this.m_friendsList.get(i);
                        i2 = i;
                    }
                    Log.Helper.LOGD(this, String.format("Refreshing next page for Type = %s, Start Index = %d, Range = %d, Last UID: %s", obj, Integer.valueOf(i2), Integer.valueOf(i3), str2), new Object[0]);
                    refreshFriendsListBasicInfo(i2, i3, str2, friendsListType, nimbleFriendsRefreshCallback, nimbleFriendsRefreshBasicInfo);
                } else if (startIndex <= 0) {
                    Log.Helper.LOGD(this, String.format("Start Index is less than or equal to 0 - get friends from 0 to page size for Type = %s", obj), new Object[0]);
                    refreshFriendsListBasicInfo(0, this.m_pageSize, "", friendsListType, nimbleFriendsRefreshCallback, nimbleFriendsRefreshBasicInfo);
                } else if (range > this.m_pageSize || range <= 0) {
                    String format = String.format("Range (%d) either exceeds Page Size (%d) or is less than 1. Unable to process this request.", Integer.valueOf(range), Integer.valueOf(this.m_pageSize));
                    Log.Helper.LOGW(this, format, new Object[0]);
                    invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_RANGE_EXCEED_LIMIT, format, friendsListType);
                } else if (startIndex - i >= 1) {
                    String format2 = String.format("Start Index (%d) higher than currentsize (%d). Unable to process this request.", Integer.valueOf(startIndex), Integer.valueOf(i));
                    Log.Helper.LOGW(this, format2, new Object[0]);
                    invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_INVALID_START_INDEX, format2, friendsListType);
                } else if (startIndex + range > i + 1) {
                    String format3 = String.format("Range (%d) exceeds Available Size (%d). Unable to process this request.", Integer.valueOf(range), Integer.valueOf(i));
                    Log.Helper.LOGW(this, format3, new Object[0]);
                    invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_RANGE_EXCEED_LIMIT, format3, friendsListType);
                } else if (startIndex > 0) {
                    if (friendsListType != NimbleFriendsList.FriendsListType.ALL_FRIENDS || this.m_friendsList == null || this.m_friendsList.size() <= 0 || startIndex >= this.m_friendsList.size()) {
                        str = "";
                        if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
                            str = "";
                            if (this.m_playedFriendsList != null) {
                                str = "";
                                if (this.m_playedFriendsList.size() > 0) {
                                    str = "";
                                    if (startIndex < this.m_playedFriendsList.size()) {
                                        str = this.m_playedFriendsList.get(startIndex - 1);
                                    }
                                }
                            }
                        }
                    } else {
                        str = this.m_friendsList.get(startIndex - 1);
                    }
                    if (str.length() > 0) {
                        Log.Helper.LOGD(this, String.format("Refreshing friends by range for Type = %s, Start Index = %d, Range = %d, Last UID = %s", obj, Integer.valueOf(startIndex), Integer.valueOf(range), str), new Object[0]);
                        refreshFriendsListBasicInfo(startIndex, range, str, friendsListType, nimbleFriendsRefreshCallback, nimbleFriendsRefreshBasicInfo);
                        return;
                    }
                    String format4 = String.format("Unable to process refresh request because we are unable to retrieve the last UID before start index %d.", Integer.valueOf(startIndex));
                    Log.Helper.LOGW(this, format4, new Object[0]);
                    invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_INVALID, format4, friendsListType);
                } else {
                    Log.Helper.LOGD(this, String.format("Refreshing friends by range for Type = %s, Range = %d with starting index at 0 and no last UID", obj, Integer.valueOf(range)), new Object[0]);
                    refreshFriendsListBasicInfo(0, range, "", friendsListType, nimbleFriendsRefreshCallback, nimbleFriendsRefreshBasicInfo);
                }
            } else if (nimbleFriendsRefreshScope instanceof NimbleFriendsRefreshIdentityInfo) {
                ArrayList<String> targetedFriendIds = ((NimbleFriendsRefreshIdentityInfo) nimbleFriendsRefreshScope).getTargetedFriendIds();
                if (targetedFriendIds == null || targetedFriendIds.size() <= 0) {
                    Log.Helper.LOGW(this, "No user IDs provided for NimbleFriendsRefreshIdentityInfo scope. Unable to process request", new Object[0]);
                    invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_NO_USER_IDS_LIST, "No user IDs provided for NimbleFriendsRefreshIdentityInfo scope. Unable to process request", friendsListType);
                } else if (targetedFriendIds.size() > 20 || targetedFriendIds.size() > i + 1) {
                    Log.Helper.LOGW(this, "Number of user IDs provided for NimbleFriendsRefreshIdentityInfo exceeds allowed limits. Unable to process request", new Object[0]);
                    invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_RANGE_EXCEED_LIMIT, "Number of user IDs provided for NimbleFriendsRefreshIdentityInfo exceeds allowed limits. Unable to process request", friendsListType);
                } else {
                    Log.Helper.LOGD(this, "Refreshing identity info of %d friends of type %s", Integer.valueOf(targetedFriendIds.size()), obj);
                    refreshFriendsListIdentityInfo(targetedFriendIds, friendsListType, nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback);
                }
            } else if (nimbleFriendsRefreshScope instanceof NimbleFriendsRefreshImageUrl) {
                ArrayList<String> targetedFriendIds2 = ((NimbleFriendsRefreshImageUrl) nimbleFriendsRefreshScope).getTargetedFriendIds();
                if (targetedFriendIds2 == null || targetedFriendIds2.size() <= 0) {
                    Log.Helper.LOGW(this, "No user IDs provided for NimbleFriendsRefreshImageUrl scope. Unable to process request", new Object[0]);
                    invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_NO_USER_IDS_LIST, "No user IDs provided for NimbleFriendsRefreshImageUrl scope. Unable to process request", friendsListType);
                } else if (targetedFriendIds2.size() > i + 1 || targetedFriendIds2.size() > 20) {
                    Log.Helper.LOGW(this, "Number of user IDs provided for NimbleFriendsRefreshImageUrl is more than the page size. Unable to process request", new Object[0]);
                    invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_RANGE_EXCEED_LIMIT, "Number of user IDs provided for NimbleFriendsRefreshImageUrl is more than the page size. Unable to process request", friendsListType);
                } else {
                    Log.Helper.LOGD(this, "Refreshing image url of %d friends of type %s", Integer.valueOf(targetedFriendIds2.size()), obj);
                    refreshFriendsListImageUrl(targetedFriendIds2, friendsListType, nimbleFriendsRefreshCallback, nimbleFriendsRefreshScope);
                }
            }
        } else if (nimbleFriendsRefreshCallback != null) {
            NimbleFriendsRefreshResult nimbleFriendsRefreshResult = new NimbleFriendsRefreshResult();
            nimbleFriendsRefreshResult.m_error = environmentCheck;
            nimbleFriendsRefreshResult.m_success = false;
            nimbleFriendsRefreshResult.m_userList = null;
            nimbleFriendsRefreshCallback.onCallback(null, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
        }
    }

    protected abstract void refreshFriendsListBasicInfo(int i, int i2, String str, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo);

    protected void refreshFriendsListIdentityInfo(ArrayList<String> arrayList, final NimbleFriendsList.FriendsListType friendsListType, final NimbleFriendsRefreshScope nimbleFriendsRefreshScope, final NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback) {
        Log.Helper.LOGD(this, "Preparing to make the call to C&I to get the pid info", new Object[0]);
        if (!isNimbleComponentAvailable("com.ea.nimble.identity")) {
            Log.Helper.LOGE(this, "Unable to refresh friends Identity information because NimbleIdentity is not available", new Object[0]);
            invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_IDENTITY_NOT_AVAILABLE, "Unable to refresh friends Identity information because NimbleIdentity is not available", friendsListType);
            return;
        }
        try {
            INimbleIdentity iNimbleIdentity = (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
            if (iNimbleIdentity.getAuthenticatorById(this.m_authenticatorId).getState() != INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                String format = String.format("Authenticator (%s) is not logged in or is unavailable", this.m_authenticatorId);
                Log.Helper.LOGE(this, format, new Object[0]);
                invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_LOGGED_IN, format, friendsListType);
                return;
            }
            iNimbleIdentity.getAuthenticatorById(this.m_authenticatorId).requestIdentityForFriends(arrayList, new INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback() { // from class: com.ea.nimble.friends.NimbleFriendsListImpl.1
                @Override // com.ea.nimble.identity.INimbleIdentityAuthenticator.NimbleIdentityFriendsIdentityInfoCallback
                public void onCallback(INimbleIdentityAuthenticator iNimbleIdentityAuthenticator, JSONObject jSONObject, Error error) {
                    if (error != null) {
                        Log.Helper.LOGE(this, "Server error when retrieving Pid Info. Error: " + error.toString(), new Object[0]);
                        NimbleFriendsListImpl.this.invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_IDENTITY_SERVER_ERROR, error.toString(), friendsListType);
                        return;
                    }
                    Log.Helper.LOGD(this, "No errors when retrieving Pid Info response from C&I server", new Object[0]);
                    ArrayList<UserInfoFromIdentity> parseJSONObjectToArrayOfUserInfo = NimbleFriendsUtility.parseJSONObjectToArrayOfUserInfo(jSONObject);
                    if (parseJSONObjectToArrayOfUserInfo == null || parseJSONObjectToArrayOfUserInfo.size() == 0) {
                        Log.Helper.LOGW(this, "Response for Pid Info request is empty. No Pids were updated for this request. It is probably because the selected friends do not have any Pids associated with them", new Object[0]);
                        NimbleFriendsListImpl.this.invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_IDENTITY_SERVER_EMPTY_RESPONSE, "Response for Pid Info request is empty. No Pids were updated for this request. It is probably because the selected friends do not have any Pids associated with them", friendsListType);
                        return;
                    }
                    Log.Helper.LOGD(this, "Successfully retrieved non-empty updated pid info from Identity server.", new Object[0]);
                    NimbleFriendsListImpl.this.updateFriendsListIdentityInfo(parseJSONObjectToArrayOfUserInfo, friendsListType, nimbleFriendsRefreshCallback, nimbleFriendsRefreshScope);
                }
            });
        } catch (Exception e) {
            String format2 = String.format("Authenticator (%s) does not support Identity Refresh for Friends List", this.m_authenticatorId);
            Log.Helper.LOGE(this, format2, new Object[0]);
            invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_AUTHENTICATOR_NOT_SUPPORTED, format2, friendsListType);
        }
    }

    protected abstract void refreshFriendsListImageUrl(List<String> list, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsRefreshScope nimbleFriendsRefreshScope);

    protected void sendUpdateNotification() {
        HashMap hashMap = new HashMap();
        if (Base.getComponent("com.ea.nimble.identity") != null) {
            hashMap.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, this.m_authenticatorId);
        } else {
            hashMap.put(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID, this.m_authenticatorId);
        }
        Utility.sendBroadcast(INimbleFriends.NIMBLE_NOTIFICATION_FRIENDS_LIST_UPDATE, hashMap);
    }

    protected void d(ArrayList<NimbleUser> arrayList, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsRefreshScope nimbleFriendsRefreshScope) {
        boolean z;
        synchronized (this) {
            ArrayList<NimbleUser> arrayList2 = new ArrayList<>();
            if (this.m_friends == null || this.m_friends.size() <= 0) {
                Log.Helper.LOGE(this, "Current Friends list is empty. Unable to process the PID update", new Object[0]);
                invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_FRIENDS_LIST_EMPTY, "Current Friends list is empty. Unable to process the PID update", friendsListType);
            } else if (arrayList == null || arrayList.size() <= 0) {
                Log.Helper.LOGE(this, "Updated users array for Avatar info is empty", new Object[0]);
                invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_IDENTITY_SERVER_EMPTY_RESPONSE, "Updated users array for Avatar info is empty", friendsListType);
            } else {
                for (int i = 0; i < arrayList.size(); i++) {
                    NimbleUser nimbleUser = this.m_friends.get(arrayList.get(i).getUserId());
                    String imageUrl = arrayList.get(i).getImageUrl();
                    if (nimbleUser != null) {
                        String imageUrl2 = nimbleUser.getImageUrl();
                        if (!Utility.validString(imageUrl2) && Utility.validString(imageUrl)) {
                            z = true;
                        } else if (!Utility.validString(imageUrl2) || Utility.validString(imageUrl)) {
                            z = false;
                            if (Utility.validString(imageUrl2)) {
                                z = false;
                                if (Utility.validString(imageUrl)) {
                                    z = false;
                                    if (!imageUrl2.equalsIgnoreCase(imageUrl)) {
                                        z = true;
                                    }
                                }
                            }
                        } else {
                            z = true;
                        }
                        if (z) {
                            NimbleUser nimbleUser2 = new NimbleUser(nimbleUser);
                            nimbleUser2.setImageUrl(imageUrl);
                            this.m_friends.put(nimbleUser2.getUserId(), nimbleUser2);
                            arrayList2.add(nimbleUser2);
                        }
                    }
                }
                if (arrayList2.size() == 0) {
                    Log.Helper.LOGD(this, "No Avatar Info updates were made to the Friends List", new Object[0]);
                    invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_FRIENDS_LIST_NOT_UPDATED, "No Avatar Info updates were made to the Friends List", friendsListType);
                } else {
                    Log.Helper.LOGD(this, String.format("%d Friends were updated for Avatar Info", Integer.valueOf(arrayList2.size())), new Object[0]);
                    sendUpdateNotification();
                    if (nimbleFriendsRefreshCallback != null) {
                        Log.Helper.LOGD(this, "Friends list was updated. Invoking the callback", new Object[0]);
                        NimbleFriendsRefreshResult nimbleFriendsRefreshResult = new NimbleFriendsRefreshResult();
                        nimbleFriendsRefreshResult.m_success = true;
                        nimbleFriendsRefreshResult.m_error = null;
                        nimbleFriendsRefreshResult.m_userList = arrayList2;
                        if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                            nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
                        } else {
                            nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
                        }
                    }
                }
            }
        }
    }

    protected void updateFriendsListBasicInfo(ArrayList<NimbleUser> arrayList, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo) {
        synchronized (this) {
            ArrayList<NimbleUser> arrayList2 = new ArrayList<>();
            if (arrayList == null || arrayList.size() <= 0) {
                Log.Helper.LOGD(this, "Even though the retrieval of Friends list was successful, the retrieved Friend list is empty", new Object[0]);
                if (nimbleFriendsRefreshCallback != null) {
                    NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult = new NimbleFriendsRangeRefreshResult();
                    nimbleFriendsRangeRefreshResult.m_success = true;
                    nimbleFriendsRangeRefreshResult.m_error = null;
                    nimbleFriendsRangeRefreshResult.m_userList = null;
                    nimbleFriendsRangeRefreshResult.m_startIndex = nimbleFriendsRefreshBasicInfo.getStartIndex();
                    nimbleFriendsRangeRefreshResult.m_size = nimbleFriendsRefreshBasicInfo.getRange();
                    nimbleFriendsRangeRefreshResult.m_friendListEndInRefresh = false;
                    if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                        nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                    } else {
                        nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalPlayedFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                    }
                }
            } else {
                Log.Helper.LOGV(this, "Retrieved " + arrayList.size() + "friends.", new Object[0]);
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i) != null) {
                        updateInternalCache(arrayList.get(i), friendsListType, arrayList2);
                    }
                }
                if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
                    this.m_totalPlayedFriends = this.m_playedFriendsList.size();
                } else {
                    this.m_totalFriends = this.m_friendsList.size();
                }
                if (nimbleFriendsRefreshCallback != null) {
                    Log.Helper.LOGD(this, "Friends list was updated. Invoking the callback", new Object[0]);
                    NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult2 = new NimbleFriendsRangeRefreshResult();
                    nimbleFriendsRangeRefreshResult2.m_success = true;
                    nimbleFriendsRangeRefreshResult2.m_error = null;
                    nimbleFriendsRangeRefreshResult2.m_userList = arrayList2;
                    nimbleFriendsRangeRefreshResult2.m_startIndex = nimbleFriendsRefreshBasicInfo.getStartIndex();
                    nimbleFriendsRangeRefreshResult2.m_size = nimbleFriendsRefreshBasicInfo.getRange();
                    if (arrayList2.size() > 0) {
                        nimbleFriendsRangeRefreshResult2.m_friendListEndInRefresh = true;
                    } else {
                        nimbleFriendsRangeRefreshResult2.m_friendListEndInRefresh = false;
                    }
                    if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                        nimbleFriendsRangeRefreshResult2.m_totalFriendCount = this.m_totalFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult2);
                    } else {
                        nimbleFriendsRangeRefreshResult2.m_totalFriendCount = this.m_totalPlayedFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult2);
                    }
                }
                Log.Helper.LOGD(this, "Friends list was upodated, send the update notification", new Object[0]);
                sendUpdateNotification();
            }
        }
    }

    protected void updateFriendsListBasicInfo(JSONArray jSONArray, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo) {
        synchronized (this) {
            ArrayList<NimbleUser> arrayList = new ArrayList<>();
            if (jSONArray == null || jSONArray.length() <= 0) {
                Log.Helper.LOGD(this, "Even though the retrieval of Friends list was successful, the retrieved Friend list is empty", new Object[0]);
                if (nimbleFriendsRefreshCallback != null) {
                    NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult = new NimbleFriendsRangeRefreshResult();
                    nimbleFriendsRangeRefreshResult.m_success = true;
                    nimbleFriendsRangeRefreshResult.m_error = null;
                    nimbleFriendsRangeRefreshResult.m_userList = null;
                    nimbleFriendsRangeRefreshResult.m_startIndex = nimbleFriendsRefreshBasicInfo.getStartIndex();
                    nimbleFriendsRangeRefreshResult.m_size = nimbleFriendsRefreshBasicInfo.getRange();
                    nimbleFriendsRangeRefreshResult.m_friendListEndInRefresh = false;
                    if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                        nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                    } else {
                        nimbleFriendsRangeRefreshResult.m_totalFriendCount = this.m_totalPlayedFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                    }
                }
            } else {
                Log.Helper.LOGV(this, "Retrieved " + jSONArray.length() + "friends.", new Object[0]);
                for (int i = 0; i < jSONArray.length(); i++) {
                    if (jSONArray.optJSONObject(i) != null) {
                        updateInternalCache(createNimbleUser(jSONArray.optJSONObject(i)), friendsListType, arrayList);
                    }
                }
                if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
                    this.m_totalPlayedFriends = this.m_playedFriendsList.size();
                } else {
                    this.m_totalFriends = this.m_friendsList.size();
                }
                if (nimbleFriendsRefreshCallback != null) {
                    Log.Helper.LOGD(this, "Friends list was updated. Invoking the callback", new Object[0]);
                    NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult2 = new NimbleFriendsRangeRefreshResult();
                    nimbleFriendsRangeRefreshResult2.m_success = true;
                    nimbleFriendsRangeRefreshResult2.m_error = null;
                    nimbleFriendsRangeRefreshResult2.m_userList = arrayList;
                    nimbleFriendsRangeRefreshResult2.m_startIndex = nimbleFriendsRefreshBasicInfo.getStartIndex();
                    nimbleFriendsRangeRefreshResult2.m_size = nimbleFriendsRefreshBasicInfo.getRange();
                    if (arrayList.size() > 0) {
                        nimbleFriendsRangeRefreshResult2.m_friendListEndInRefresh = true;
                    } else {
                        nimbleFriendsRangeRefreshResult2.m_friendListEndInRefresh = false;
                    }
                    if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                        nimbleFriendsRangeRefreshResult2.m_totalFriendCount = this.m_totalFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult2);
                    } else {
                        nimbleFriendsRangeRefreshResult2.m_totalFriendCount = this.m_totalPlayedFriends;
                        nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult2);
                    }
                }
                Log.Helper.LOGD(this, "Friends list was upodated, send the update notification", new Object[0]);
                sendUpdateNotification();
            }
        }
    }

    protected void updateFriendsListIdentityInfo(ArrayList<UserInfoFromIdentity> arrayList, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsRefreshScope nimbleFriendsRefreshScope) {
        synchronized (this) {
            ArrayList<NimbleUser> arrayList2 = new ArrayList<>();
            if (this.m_friends == null || this.m_friends.size() <= 0) {
                Log.Helper.LOGE(this, "Current Friends list is empty. Unable to process the PID update", new Object[0]);
                invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_FRIENDS_LIST_EMPTY, "Current Friends list is empty. Unable to process the PID update", friendsListType);
            } else if (arrayList == null || arrayList.size() <= 0) {
                Log.Helper.LOGE(this, "Updated friends array is empty", new Object[0]);
                invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_IDENTITY_SERVER_EMPTY_RESPONSE, "Updated friends array is empty", friendsListType);
            } else {
                for (int i = 0; i < arrayList.size(); i++) {
                    NimbleUser nimbleUser = this.m_friends.get(arrayList.get(i).getExternalRefValue());
                    if (nimbleUser != null && nimbleUser.getPid() == null) {
                        NimbleUser nimbleUser2 = new NimbleUser(nimbleUser);
                        nimbleUser2.setPid(arrayList.get(i).getPidId());
                        nimbleUser2.setPersonaId(arrayList.get(i).getPersonaId());
                        this.m_friends.put(arrayList.get(i).getExternalRefValue(), nimbleUser2);
                        arrayList2.add(nimbleUser2);
                    }
                }
                if (arrayList2.size() == 0) {
                    Log.Helper.LOGD(this, "No Pid Info updates were made to the Friends List", new Object[0]);
                    invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_FRIENDS_LIST_NOT_UPDATED, "No Pid Info updates were made to the Friends List", friendsListType);
                } else {
                    Log.Helper.LOGD(this, String.format("%d Friends were updated for Identity Scope", Integer.valueOf(arrayList2.size())), new Object[0]);
                    sendUpdateNotification();
                    if (nimbleFriendsRefreshCallback != null) {
                        Log.Helper.LOGD(this, "Friends list was updated. Invoking the callback", new Object[0]);
                        NimbleFriendsRefreshResult nimbleFriendsRefreshResult = new NimbleFriendsRefreshResult();
                        nimbleFriendsRefreshResult.m_success = true;
                        nimbleFriendsRefreshResult.m_error = null;
                        nimbleFriendsRefreshResult.m_userList = arrayList2;
                        if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
                            nimbleFriendsRefreshCallback.onCallback(this.m_nimbleFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
                        } else {
                            nimbleFriendsRefreshCallback.onCallback(this.m_nimblePlayedFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
                        }
                    }
                }
            }
        }
    }

    protected void updateInternalCache(NimbleUser nimbleUser, NimbleFriendsList.FriendsListType friendsListType, ArrayList<NimbleUser> arrayList) {
        if (this.m_friends == null || nimbleUser == null || nimbleUser.getUserId() == null) {
            Log.Helper.LOGI(this, "updateInternalCache - Cannot update internal cache because the friend map or the new friend is null", new Object[0]);
            return;
        }
        boolean z = true;
        if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
            z = false;
        }
        if (this.m_friends.get(nimbleUser.getUserId()) == null) {
            if (z) {
                nimbleUser.addedToAllFriends = true;
                this.m_friendsList.add(nimbleUser.getUserId());
                if (nimbleUser.getPlayedCurrentGame() == NimbleUser.PlayedCurrentGameFlag.PLAYED) {
                    this.m_playedFriendsList.add(nimbleUser.getUserId());
                }
            } else {
                nimbleUser.addedToAllFriends = false;
                this.m_playedFriendsList.add(nimbleUser.getUserId());
            }
            this.m_friends.put(nimbleUser.getUserId(), nimbleUser);
            arrayList.add(nimbleUser);
            return;
        }
        NimbleUser nimbleUser2 = this.m_friends.get(nimbleUser.getUserId());
        if (z && !nimbleUser2.addedToAllFriends) {
            nimbleUser.addedToAllFriends = true;
            this.m_friends.put(nimbleUser.getUserId(), nimbleUser);
            this.m_friendsList.add(nimbleUser.getUserId());
        }
        if (nimbleUser2.isUserUpdated(nimbleUser)) {
            nimbleUser.setPersonaId(nimbleUser2.getPersonaId());
            nimbleUser.setPid(nimbleUser2.getPid());
            nimbleUser.addedToAllFriends = nimbleUser2.addedToAllFriends;
            nimbleUser.setRefreshTimestamp(new Date());
            this.m_friends.put(nimbleUser.getUserId(), nimbleUser);
            arrayList.add(nimbleUser);
        }
    }
}
