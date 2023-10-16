/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.inappmessage;

import java.io.Serializable;

public class Message
implements Serializable {
    private static final long serialVersionUID = 1L;
    String m_buttonLabel1Title;
    String m_buttonLabel2Title;
    String m_buttonLabel3Title;
    String m_message;
    int m_messageID;
    String m_title;
    String m_url;

    Message() {
    }

    Message(int n2, String string2, String string3, String string4, String string5, String string6, String string7) {
        this.m_messageID = n2;
        this.m_title = string2;
        this.m_message = string3;
        this.m_url = string4;
        this.m_buttonLabel1Title = string5;
        this.m_buttonLabel2Title = string6;
        this.m_buttonLabel3Title = string7;
    }

    public String buttonLabel1Title() {
        return this.m_buttonLabel1Title;
    }

    public String buttonLabel2Title() {
        return this.m_buttonLabel2Title;
    }

    public String buttonLabel3Title() {
        return this.m_buttonLabel3Title;
    }

    public String getMessage() {
        return this.m_message;
    }

    public int getMessageId() {
        return this.m_messageID;
    }

    public String getTitle() {
        return this.m_title;
    }

    public String getUrl() {
        return this.m_url;
    }
}

