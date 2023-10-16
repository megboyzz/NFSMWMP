/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.inappmessage;

import com.ea.nimble.Base;
import com.ea.nimble.Log;
import com.ea.nimble.inappmessage.IInAppMessage;
import com.ea.nimble.inappmessage.InAppMessageImpl;

public class InAppMessage {
    public static final String COMPONENT_ID = "com.ea.nimble.inappmessage";
    public static final String MESSAGE_EXCLUDE_ID = "messageExcludeID";
    static final String MESSAGE_PERSISTENCE_ID = "currentInAppMessage";
    public static final String NOTIFICATION_IN_APP_MESSAGE_REFRESH = "nimble.inappmessage.notification.message_refresh";

    public static IInAppMessage getComponent() {
        return (IInAppMessage)((Object)Base.getComponent(COMPONENT_ID));
    }

    private static void initialize() {
        Log.Helper.LOGDS("IAM", "IAM initialize");
        Base.registerComponent(new InAppMessageImpl(), COMPONENT_ID);
    }
}

