package com.ea.nimble.friends;

import com.ea.nimble.Error;
import java.util.ArrayList;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsRefreshResult.class */
public class NimbleFriendsRefreshResult {
    protected Error m_error;
    protected boolean m_success;
    protected ArrayList<NimbleUser> m_userList = new ArrayList<>();

    public Error getError() {
        return this.m_error;
    }

    public List<NimbleUser> getUpdatedFriends() {
        return this.m_userList;
    }

    public boolean isSuccess() {
        return this.m_success;
    }
}
