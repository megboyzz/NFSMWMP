package com.ea.nimble.identity;

import java.io.Serializable;
import java.util.Date;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityUserInfo.class */
public class NimbleIdentityUserInfo implements Serializable, Cloneable {
    private static final long serialVersionUID = 1;
    private String avatarUri;
    private String dateOfBirth;
    private String displayName;
    private String email;
    private Date expiryTime;
    private String pid;
    private String userId;
    private String userName;

    @Override // java.lang.Object
    public NimbleIdentityUserInfo clone() {
        try {
            return (NimbleIdentityUserInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getAvatarUri() {
        return this.avatarUri;
    }

    public String getDateOfBirth() {
        return this.dateOfBirth;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEmail() {
        return this.email;
    }

    public Date getExpiryTime() {
        return this.expiryTime;
    }

    public String getPid() {
        return this.pid;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getuserName() {
        return this.userName;
    }

    public void setAvatarUri(String str) {
        this.avatarUri = str;
    }

    public void setDateOfBirth(String str) {
        this.dateOfBirth = str;
    }

    public void setDisplayName(String str) {
        this.displayName = str;
    }

    public void setEmail(String str) {
        this.email = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpiryTime(Date date) {
        this.expiryTime = date;
    }

    public void setPid(String str) {
        this.pid = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUserId(String str) {
        this.userId = str;
    }

    public void setUserName(String str) {
        this.userName = str;
    }
}
