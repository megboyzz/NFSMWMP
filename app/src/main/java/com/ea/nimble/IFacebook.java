/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 */
package com.ea.nimble;

import com.ea.nimble.Error;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public interface IFacebook {
    public static final String NIMBLE_NOTIFICATION_FACEBOOK_STATUS_CHANGED = "nimble.notification.facebook.statuschanged";

    public String getAccessToken();

    public String getApplicationId();

    public Date getExpirationDate();

    public Map<String, Object> getGraphUser();

    public boolean hasOpenSession();

    public void login(List<String> var1, FacebookCallback var2);

    public void logout();

    public void refreshSession(String var1, Date var2);

    public void retrieveFriends(int var1, int var2, FacebookFriendsCallback var3);

    public void sendAppRequest(String var1, String var2, String var3, FacebookCallback var4);

    public static interface FacebookCallback {
        public void callback(IFacebook var1, boolean var2, Exception var3);
    }

    public static interface FacebookFriendsCallback {
        public void callback(IFacebook var1, JSONArray var2, Error var3);
    }

    public static interface FqlRequestCallBack {
        public void requestComplete(JSONArray var1, Error var2);
    }
}

