package com.ea.nimble.friends;

import com.ea.nimble.Base;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriends.class */
public class NimbleFriends {
    public static final String NIMBLE_COMPONENT_ID_FRIENDS = "com.ea.nimble.friends";

    public static INimbleFriends getComponent() {
        return (INimbleFriends) Base.getComponent(NIMBLE_COMPONENT_ID_FRIENDS);
    }
}
