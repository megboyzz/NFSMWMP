package com.ea.nimble.friends;

import com.ea.nimble.Global;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleUser.class */
public class NimbleUser {
    protected boolean addedToAllFriends;
    protected String authenticatorId;
    protected String displayName;
    protected Map<String, String> extraInfo;
    protected String friendType;
    protected String imageUrl;
    protected String personaId;
    protected String pid;
    protected PlayedCurrentGameFlag playedCurrentGame;
    protected Date refreshTimestamp;
    protected String userId;

    /* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleUser$PlayedCurrentGameFlag.class */
    public enum PlayedCurrentGameFlag {
        NOT_AVAILABLE,
        PLAYED,
        NOT_PLAYED
    }

    public NimbleUser() {
        this.playedCurrentGame = PlayedCurrentGameFlag.NOT_AVAILABLE;
        this.addedToAllFriends = false;
    }

    public NimbleUser(NimbleUser nimbleUser) {
        this.playedCurrentGame = PlayedCurrentGameFlag.NOT_AVAILABLE;
        this.addedToAllFriends = false;
        this.displayName = nimbleUser.getDisplayName();
        this.authenticatorId = nimbleUser.getAuthenticatorId();
        this.userId = nimbleUser.getUserId();
        this.pid = nimbleUser.getPid();
        this.personaId = nimbleUser.getPersonaId();
        this.playedCurrentGame = nimbleUser.getPlayedCurrentGame();
        this.imageUrl = nimbleUser.getImageUrl();
        this.refreshTimestamp = nimbleUser.getRefreshTimestamp();
        this.friendType = nimbleUser.getFriendType();
        this.addedToAllFriends = nimbleUser.addedToAllFriends;
        if (nimbleUser.getExtraInfo() == null) {
            this.extraInfo = null;
        } else {
            this.extraInfo = new HashMap(nimbleUser.getExtraInfo());
        }
    }

    public String getAuthenticatorId() {
        return this.authenticatorId;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public Map<String, String> getExtraInfo() {
        return this.extraInfo;
    }

    public String getFriendType() {
        return this.friendType;
    }

    public String getImageUrl() {
        return (this.authenticatorId == null || !this.authenticatorId.equals(Global.NIMBLE_AUTHENTICATOR_FACEBOOK) || this.userId == null || this.userId.length() <= 0) ? this.imageUrl : String.format("https://graph.facebook.com/%s/picture?type=normal", this.userId);
    }

    public String getPersonaId() {
        return this.personaId;
    }

    public String getPid() {
        return this.pid;
    }

    public PlayedCurrentGameFlag getPlayedCurrentGame() {
        return this.playedCurrentGame;
    }

    public Date getRefreshTimestamp() {
        return this.refreshTimestamp;
    }

    public String getUserId() {
        return this.userId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isUserUpdated(NimbleUser nimbleUser) {
        if (nimbleUser == null) {
            return false;
        }
        boolean z = false;
        if (nimbleUser.getDisplayName() != null) {
            z = false;
            if (nimbleUser.getDisplayName() != "") {
                if (getDisplayName() == null) {
                    z = true;
                } else {
                    z = false;
                    if (!nimbleUser.getDisplayName().equals(getDisplayName())) {
                        z = true;
                    }
                }
            }
        }
        boolean z2 = z;
        if (nimbleUser.getPid() != null) {
            z2 = z;
            if (nimbleUser.getPid() != "") {
                if (getPid() == null) {
                    z2 = true;
                } else {
                    z2 = z;
                    if (!nimbleUser.getPid().equals(getPid())) {
                        z2 = true;
                    }
                }
            }
        }
        boolean z3 = z2;
        if (nimbleUser.getPersonaId() != null) {
            z3 = z2;
            if (nimbleUser.getPersonaId() != "") {
                if (getPersonaId() == null) {
                    z3 = true;
                } else {
                    z3 = z2;
                    if (!nimbleUser.getPersonaId().equals(getPersonaId())) {
                        z3 = true;
                    }
                }
            }
        }
        boolean z4 = z3;
        if (nimbleUser.getImageUrl() != null) {
            z4 = z3;
            if (nimbleUser.getImageUrl() != "") {
                if (getImageUrl() == null) {
                    z4 = true;
                } else {
                    z4 = z3;
                    if (!nimbleUser.getImageUrl().equals(getImageUrl())) {
                        z4 = true;
                    }
                }
            }
        }
        boolean z5 = z4;
        if (nimbleUser.getPlayedCurrentGame() != null) {
            if (getPlayedCurrentGame() == null) {
                z5 = true;
            } else {
                z5 = z4;
                if (!nimbleUser.getPlayedCurrentGame().equals(getPlayedCurrentGame())) {
                    z5 = true;
                }
            }
        }
        return z5;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setAuthenticatorId(String str) {
        this.authenticatorId = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDisplayName(String str) {
        this.displayName = str;
    }

    protected void setExtraInfo(Map<String, String> map) {
        this.extraInfo = map;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setFriendType(String str) {
        this.friendType = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setImageUrl(String str) {
        this.imageUrl = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPersonaId(String str) {
        this.personaId = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setPid(String str) {
        this.pid = str;
    }

    protected void setPlayedCurrentGame(PlayedCurrentGameFlag playedCurrentGameFlag) {
        this.playedCurrentGame = playedCurrentGameFlag;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setRefreshTimestamp(Date date) {
        this.refreshTimestamp = date;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setUserId(String str) {
        this.userId = str;
    }

    public String toString() {
        String str;
        String str2 = this.playedCurrentGame == PlayedCurrentGameFlag.PLAYED ? "YES" : this.playedCurrentGame == PlayedCurrentGameFlag.NOT_PLAYED ? "NO" : this.playedCurrentGame == PlayedCurrentGameFlag.NOT_AVAILABLE ? "NOT AVAILABLE" : "UNKNOWN";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            str = simpleDateFormat.format(this.refreshTimestamp);
        } catch (Exception e) {
            str = "";
        }
        new String();
        return ("displayName(" + getDisplayName() + ") friendId(" + getUserId() + ") pid(" + this.pid + ") personaId(" + getPersonaId() + ") imageUrl(" + getImageUrl() + ") authenticatorId(" + getAuthenticatorId() + ") playedCurrentGame(" + str2 + ")refreshTimestamp(" + str + ")") + "ExtraInfo(" + (getExtraInfo() == null ? "" : getExtraInfo().toString()) + ") \n";
    }
}
