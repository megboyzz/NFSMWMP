package com.ea.nimble.friends;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsRangeRefreshResult.class */
public class NimbleFriendsRangeRefreshResult extends NimbleFriendsRefreshResult {
    protected boolean m_friendListEndInRefresh;
    protected int m_size;
    protected int m_startIndex;
    protected int m_totalFriendCount;

    public int getRefreshSize() {
        return this.m_size;
    }

    public int getRefreshStartIndex() {
        return this.m_startIndex;
    }

    public int getTotalFriendCount() {
        return this.m_totalFriendCount;
    }

    public boolean isFriendListEndInRefresh() {
        return this.m_friendListEndInRefresh;
    }
}
