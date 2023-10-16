package com.ea.nimble.identity;

import com.ea.nimble.Base;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentity.class */
public class NimbleIdentity {
    public static final String COMPONENT_ID = "com.ea.nimble.identity";

    public static INimbleIdentity getComponent() {
        return (INimbleIdentity) Base.getComponent("com.ea.nimble.identity");
    }
}
