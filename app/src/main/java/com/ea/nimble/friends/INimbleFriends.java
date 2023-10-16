package com.ea.nimble.friends;

/* loaded from: stdlib.jar:com/ea/nimble/friends/INimbleFriends.class */
public interface INimbleFriends {
    public static final int IDENTITY_FRIEND_INFO_REQUEST_LIMIT = 20;
    public static final String NIMBLE_NOTIFICATION_FRIENDS_LIST_UPDATE = "nimble.notification.friends.update";

    NimbleFriendsList getFriendsList(String str, boolean z);
}
