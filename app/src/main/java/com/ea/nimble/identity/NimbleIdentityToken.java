package com.ea.nimble.identity;

import com.ea.nimble.Log;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityToken.class */
public class NimbleIdentityToken implements Serializable {
    private static final String NIMBLE_IDENTITY_REFRESH_TOKEN_EXPIRES_IN = "refresh_token_expires_in";
    private static final String NIMBLE_IDENTITY_TOKEN_ACCESS_TOKEN = "access_token";
    private static final String NIMBLE_IDENTITY_TOKEN_EXPIRES_IN = "expires_in";
    private static final String NIMBLE_IDENTITY_TOKEN_ID_TOKEN = "id_token";
    private static final String NIMBLE_IDENTITY_TOKEN_REFRESH_TOKEN = "refresh_token";
    private static final String NIMBLE_IDENTITY_TOKEN_TYPE = "token_type";
    private static final long serialVersionUID = 2;
    private String accessToken;
    private Date accessTokenExpiryTime;
    private String idToken;
    private String refreshToken;
    private Date refreshTokenExpiryTime;
    private String type;

    public NimbleIdentityToken() {
    }

    public NimbleIdentityToken(Map<String, Object> map) {
        this.accessToken = (String) map.get("access_token");
        this.refreshToken = (String) map.get(NIMBLE_IDENTITY_TOKEN_REFRESH_TOKEN);
        this.idToken = (String) map.get(NIMBLE_IDENTITY_TOKEN_ID_TOKEN);
        this.type = (String) map.get(NIMBLE_IDENTITY_TOKEN_TYPE);
        long currentTimeMillis = System.currentTimeMillis();
        this.accessTokenExpiryTime = new Date(getTimeFromObject(map.get("expires_in")) + currentTimeMillis);
        this.refreshTokenExpiryTime = new Date(getTimeFromObject(map.get(NIMBLE_IDENTITY_REFRESH_TOKEN_EXPIRES_IN)) + currentTimeMillis);
    }

    private long getTimeFromObject(Object obj) {
        double d;
        if (obj instanceof String) {
            d = Double.parseDouble((String) obj);
        } else if (obj instanceof Double) {
            d = ((Double) obj).doubleValue();
        } else if (obj instanceof Integer) {
            return (long) (((Integer) obj).intValue() * 1000);
        } else {
            Log.Helper.LOGES("Identity", "Couldn't get time from object of type " + obj.getClass().getName(), new Object[0]);
            d = 0.0d;
        }
        return (long) (1000.0d * d);
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public Date getAccessTokenExpiryTime() {
        return this.accessTokenExpiryTime;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }

    public Date getRefreshTokenExpiryTime() {
        return this.refreshTokenExpiryTime;
    }

    public String getType() {
        return this.type;
    }
}
