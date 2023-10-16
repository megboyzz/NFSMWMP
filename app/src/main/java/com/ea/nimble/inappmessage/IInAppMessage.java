package com.ea.nimble.inappmessage;

import com.ea.nimble.inappmessage.Message;

public interface IInAppMessage {
    Message popMessageFromCache();

    void showInAppMessage();
}

