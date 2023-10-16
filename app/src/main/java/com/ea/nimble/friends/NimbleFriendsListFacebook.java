package com.ea.nimble.friends;

import com.ea.nimble.Base;
import com.ea.nimble.Error;
import com.ea.nimble.Global;
import com.ea.nimble.IFacebook;
import com.ea.nimble.Log;
import com.ea.nimble.friends.NimbleFriendsError;
import com.ea.nimble.friends.NimbleFriendsList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsListFacebook.class */
public class NimbleFriendsListFacebook extends NimbleFriendsListImpl {
    private static final String FACEBOOK_COMPONENT_ID = "com.ea.nimble.facebook";

    /* JADX INFO: Access modifiers changed from: package-private */
    public NimbleFriendsListFacebook() {
        this.LOG_SOURCE_TITLE = "NimbleFriendsListFacebook";
        this.m_authenticatorId = Global.NIMBLE_AUTHENTICATOR_FACEBOOK;
        Log.Helper.LOGV(this, "Constructed", new Object[0]);
        this.m_pageSize = 1000;
        this.m_nimbleFriendsList = new NimbleFriendsList(this, NimbleFriendsList.FriendsListType.ALL_FRIENDS);
        this.m_nimblePlayedFriendsList = new NimbleFriendsList(this, NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS);
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl
    protected NimbleUser createNimbleUser(JSONObject jSONObject) {
        return new NimbleFacebookUser(jSONObject);
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl, com.ea.nimble.LogSource
    public /* bridge */ /* synthetic */ String getLogSourceTitle() {
        return super.getLogSourceTitle();
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl
    protected void refreshFriendsListBasicInfo(final int i, final int i2, String str, final NimbleFriendsList.FriendsListType friendsListType, final NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, final NimbleFriendsRefreshBasicInfo nimbleFriendsRefreshBasicInfo) {
        if (friendsListType == NimbleFriendsList.FriendsListType.ALL_FRIENDS) {
            Log.Helper.LOGE(this, "Facebook no longer supports getting friends with ALL_FRIENDS flag", new Object[0]);
            invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_SCOPE_FRIENDS_LIST_TYPE_UNSUPPORTED, "Facebook no longer supports getting friends with ALL_FRIENDS flag", friendsListType);
        } else if (!isNimbleComponentAvailable("com.ea.nimble.facebook") || !((IFacebook) Base.getComponent("com.ea.nimble.facebook")).hasOpenSession()) {
            Log.Helper.LOGE(this, "Unable to refresh friends from Facebook because NimbleFacebook is either unavailable or the session is not open", new Object[0]);
            invokeCallbackWithBasicScopeError(nimbleFriendsRefreshBasicInfo, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_FACEBOOK_USER_NOT_LOGGED_IN, "Unable to refresh friends from Facebook because NimbleFacebook is either unavailable or the session is not open", friendsListType);
        } else {
            ((IFacebook) Base.getComponent("com.ea.nimble.facebook")).retrieveFriends(i, i2, new IFacebook.FacebookFriendsCallback() { // from class: com.ea.nimble.friends.NimbleFriendsListFacebook.1
                @Override // com.ea.nimble.IFacebook.FacebookFriendsCallback
                public void callback(IFacebook iFacebook, JSONArray jSONArray, Error error) {
                    if (error != null) {
                        Log.Helper.LOGE(this, String.format("Error in retrieving friends list from Facebook. Error :%s", error.toString()), new Object[0]);
                        if (nimbleFriendsRefreshCallback != null) {
                            NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult = new NimbleFriendsRangeRefreshResult();
                            nimbleFriendsRangeRefreshResult.m_success = false;
                            nimbleFriendsRangeRefreshResult.m_error = error;
                            nimbleFriendsRangeRefreshResult.m_userList = null;
                            nimbleFriendsRangeRefreshResult.m_startIndex = i;
                            nimbleFriendsRangeRefreshResult.m_size = i2;
                            nimbleFriendsRangeRefreshResult.m_friendListEndInRefresh = false;
                            if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
                                nimbleFriendsRefreshCallback.onCallback(NimbleFriendsListFacebook.this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                            } else {
                                nimbleFriendsRefreshCallback.onCallback(NimbleFriendsListFacebook.this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult);
                            }
                        }
                    } else {
                        if (jSONArray == null || jSONArray.length() <= 0) {
                            Log.Helper.LOGD(NimbleFriendsListFacebook.this, "No friends retrieved from Facebook even though the operation was successful", new Object[0]);
                            if (nimbleFriendsRefreshCallback != null) {
                                NimbleFriendsRangeRefreshResult nimbleFriendsRangeRefreshResult2 = new NimbleFriendsRangeRefreshResult();
                                nimbleFriendsRangeRefreshResult2.m_success = true;
                                nimbleFriendsRangeRefreshResult2.m_error = null;
                                nimbleFriendsRangeRefreshResult2.m_userList = null;
                                nimbleFriendsRangeRefreshResult2.m_startIndex = i;
                                nimbleFriendsRangeRefreshResult2.m_size = i2;
                                nimbleFriendsRangeRefreshResult2.m_friendListEndInRefresh = false;
                                if (friendsListType == NimbleFriendsList.FriendsListType.CURRENT_GAME_FRIENDS) {
                                    nimbleFriendsRefreshCallback.onCallback(NimbleFriendsListFacebook.this.m_nimblePlayedFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult2);
                                    return;
                                } else {
                                    nimbleFriendsRefreshCallback.onCallback(NimbleFriendsListFacebook.this.m_nimbleFriendsList, nimbleFriendsRefreshBasicInfo, nimbleFriendsRangeRefreshResult2);
                                    return;
                                }
                            }
                        }
                        Log.Helper.LOGD(this, "Successfully retrieved friends for specified scope from Facebook. Proceeding with NimbleFriendList update", new Object[0]);
                        NimbleFriendsListFacebook.this.updateFriendsListBasicInfo(jSONArray, friendsListType, nimbleFriendsRefreshCallback, nimbleFriendsRefreshBasicInfo);
                    }
                }
            });
        }
    }

    @Override // com.ea.nimble.friends.NimbleFriendsListImpl
    protected void refreshFriendsListImageUrl(List<String> list, NimbleFriendsList.FriendsListType friendsListType, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback, NimbleFriendsRefreshScope nimbleFriendsRefreshScope) {
        Log.Helper.LOGI(this, "Facebook Friends Service does not support ImageUri refresh because Facebook friends already have Image URI information", new Object[0]);
        invokeCallbackWithScopeError(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_TYPE_UNSUPPORTED, "Facebook Friends Service does not support ImageUri refresh because Facebook friends already have Image URI information", friendsListType);
    }
}
