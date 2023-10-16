package com.ea.nimble.friends;

import java.util.ArrayList;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsRefreshIdentityInfo.class */
public class NimbleFriendsRefreshIdentityInfo extends NimbleFriendsRefreshScope {
    private ArrayList<String> userIds;

    public NimbleFriendsRefreshIdentityInfo(ArrayList<String> arrayList) {
        this.userIds = arrayList;
    }

    public ArrayList<String> getTargetedFriendIds() {
        return this.userIds;
    }
}
