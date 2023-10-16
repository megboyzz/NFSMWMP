package com.ea.nimble.friends;

import com.ea.nimble.Base;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleOriginFriendsService.class */
public class NimbleOriginFriendsService {
    public static final String NIMBLE_COMPONENT_ID_FRIENDS_ORIGIN = "com.ea.nimble.friends.originfriendsservice";

    public static INimbleOriginFriendsService getComponent() {
        return (INimbleOriginFriendsService) Base.getComponent(NIMBLE_COMPONENT_ID_FRIENDS_ORIGIN);
    }
}
