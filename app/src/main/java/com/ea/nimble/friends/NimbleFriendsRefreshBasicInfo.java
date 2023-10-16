package com.ea.nimble.friends;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsRefreshBasicInfo.class */
public class NimbleFriendsRefreshBasicInfo extends NimbleFriendsRefreshScope {
    private boolean m_nextPage;
    private int m_range;
    private int m_startIndex;

    public NimbleFriendsRefreshBasicInfo() {
        this.m_startIndex = -1;
        this.m_range = -1;
        this.m_nextPage = false;
        this.m_nextPage = true;
    }

    public NimbleFriendsRefreshBasicInfo(int i, int i2) {
        this.m_startIndex = -1;
        this.m_range = -1;
        this.m_nextPage = false;
        this.m_startIndex = i;
        this.m_range = i2;
        this.m_nextPage = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean getNextPage() {
        return this.m_nextPage;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRange() {
        return this.m_range;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getStartIndex() {
        return this.m_startIndex;
    }
}
