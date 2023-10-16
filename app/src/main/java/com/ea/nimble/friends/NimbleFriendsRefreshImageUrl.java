package com.ea.nimble.friends;

import java.util.ArrayList;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsRefreshImageUrl.class */
public class NimbleFriendsRefreshImageUrl extends NimbleFriendsRefreshScope {
    private ArrayList<String> userIds;

    public NimbleFriendsRefreshImageUrl(ArrayList<String> arrayList) {
        this.userIds = arrayList;
    }

    public ArrayList<String> getTargetedFriendIds() {
        return this.userIds;
    }
}
