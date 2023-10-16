package com.ea.nimble.identity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityLoginParams.class */
public abstract class NimbleIdentityLoginParams {

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityLoginParams$AnonymousLoginParams.class */
    public static class AnonymousLoginParams extends NimbleIdentityLoginParams {
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityLoginParams$FacebookAccessTokenLoginParams.class */
    public static class FacebookAccessTokenLoginParams extends NimbleIdentityLoginParams {
        private Date expiryDate;
        private String facebookAccessToken;

        FacebookAccessTokenLoginParams() {
            this.facebookAccessToken = "";
        }

        public FacebookAccessTokenLoginParams(String str, Date date) {
            this.facebookAccessToken = "";
            this.facebookAccessToken = str;
            this.expiryDate = date;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Date getExpiryDate() {
            return this.expiryDate;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getFacebookAccessToken() {
            return this.facebookAccessToken;
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityLoginParams$FacebookClientLoginParams.class */
    public static class FacebookClientLoginParams extends NimbleIdentityLoginParams {
        private List<String> facebookPermissions;

        public FacebookClientLoginParams(List<String> list) {
            if (list == null || list.size() <= 0) {
                this.facebookPermissions = new ArrayList();
                this.facebookPermissions.add("email");
                return;
            }
            this.facebookPermissions = list;
            if (!list.contains("email")) {
                this.facebookPermissions.add("email");
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public List<String> getFacebookPermissions() {
            return this.facebookPermissions;
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityLoginParams$OriginCredentialsLoginParams.class */
    public static class OriginCredentialsLoginParams extends NimbleIdentityLoginParams {
        private String password;
        private String username;

        OriginCredentialsLoginParams() {
            this.username = "";
            this.password = "";
        }

        public OriginCredentialsLoginParams(String str, String str2) {
            this.username = "";
            this.password = "";
            this.username = str;
            this.password = str2;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getPassword() {
            return this.password;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getUsername() {
            return this.username;
        }
    }

    /* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityLoginParams$OriginOAuthCodeLoginParams.class */
    public static class OriginOAuthCodeLoginParams extends NimbleIdentityLoginParams {
        private String oAuthCode;

        OriginOAuthCodeLoginParams() {
            this.oAuthCode = "";
        }

        public OriginOAuthCodeLoginParams(String str) {
            this.oAuthCode = "";
            this.oAuthCode = str;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public String getOauthCode() {
            return this.oAuthCode;
        }
    }

    protected NimbleIdentityLoginParams() {
    }
}
