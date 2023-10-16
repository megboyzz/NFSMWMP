package com.ea.nimble.friends;

import com.ea.nimble.friends.NimbleFriendsError;
import java.util.ArrayList;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsList.class */
public class NimbleFriendsList {
    private NimbleFriendsListImpl m_friendsListImpl;
    private FriendsListType m_type;

    /* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsList$FriendsListType.class */
    public enum FriendsListType {
        ALL_FRIENDS,
        CURRENT_GAME_FRIENDS
    }

    public NimbleFriendsList(NimbleFriendsListImpl nimbleFriendsListImpl, FriendsListType friendsListType) {
        this.m_friendsListImpl = nimbleFriendsListImpl;
        this.m_type = friendsListType;
    }

    public NimbleUser getFriendProfile(String str) {
        if (this.m_friendsListImpl != null) {
            return this.m_friendsListImpl.m_friends.get(str);
        }
        return null;
    }

    public ArrayList<String> getFriends() {
        if (this.m_friendsListImpl != null) {
            return this.m_type == FriendsListType.CURRENT_GAME_FRIENDS ? this.m_friendsListImpl.m_playedFriendsList : this.m_friendsListImpl.m_friendsList;
        }
        return null;
    }

    public int getRefreshPageSize() {
        if (this.m_friendsListImpl != null) {
            return this.m_friendsListImpl.getPageSize();
        }
        return -1;
    }

    public int getTotalFriendCount() {
        return this.m_type == FriendsListType.CURRENT_GAME_FRIENDS ? this.m_friendsListImpl.m_totalPlayedFriends : this.m_friendsListImpl.m_totalFriends;
    }

    FriendsListType getType() {
        return this.m_type;
    }

    public void refreshFriendsList(NimbleFriendsRefreshScope nimbleFriendsRefreshScope, NimbleFriendsRefreshCallback nimbleFriendsRefreshCallback) {
        synchronized (this) {
            if (this.m_friendsListImpl != null) {
                this.m_friendsListImpl.refreshFriendsList(nimbleFriendsRefreshScope, nimbleFriendsRefreshCallback, this.m_type);
            } else if (nimbleFriendsRefreshCallback != null) {
                NimbleFriendsError nimbleFriendsError = new NimbleFriendsError(NimbleFriendsError.Code.NIMBLE_FRIENDS_REFRESH_FRIENDS_PROVIDER_NOT_AVAILABLE, "Specified Friends provider is not available. Failed to refresh Friends list.");
                NimbleFriendsRefreshResult nimbleFriendsRefreshResult = new NimbleFriendsRefreshResult();
                nimbleFriendsRefreshResult.m_error = nimbleFriendsError;
                nimbleFriendsRefreshResult.m_success = false;
                nimbleFriendsRefreshResult.m_userList = null;
                nimbleFriendsRefreshCallback.onCallback(null, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
            }
        }
    }
}
