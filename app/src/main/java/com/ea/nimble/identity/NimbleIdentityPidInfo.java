package com.ea.nimble.identity;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/* loaded from: stdlib.jar:com/ea/nimble/identity/NimbleIdentityPidInfo.class */
public class NimbleIdentityPidInfo implements Serializable {
    private static final long serialVersionUID = 2;
    private String anonymousPid;
    private String authenticationSource;
    private String country;
    private String dateCreated;
    private String dateModified;
    private String dob;
    private Date expiryTime;
    private String language;
    private String lastAuthDate;
    private String locale;
    private String pid;
    private String reasonCode;
    private String registrationSource;
    private String status;
    private String strength;
    private String tosVersion;

    NimbleIdentityPidInfo() {
    }

    public NimbleIdentityPidInfo(Map<String, Object> map, Date date) {
        Map map2;
        if (map != null && (map2 = (Map) map.get("pid")) != null) {
            this.pid = map2.get("pidId").toString();
            this.anonymousPid = (String) map2.get("anonymousPid");
            this.authenticationSource = (String) map2.get("authenticationSource");
            this.country = (String) map2.get("country");
            this.dateCreated = (String) map2.get("dateCreated");
            this.dateModified = (String) map2.get("dateModified");
            this.dob = (String) map2.get("dob");
            this.language = (String) map2.get("language");
            this.lastAuthDate = (String) map2.get("lastAuthDate");
            this.locale = (String) map2.get("locale");
            this.reasonCode = (String) map2.get("reasonCode");
            this.registrationSource = (String) map2.get("registrationSource");
            this.status = (String) map2.get("status");
            this.strength = (String) map2.get("strength");
            this.tosVersion = (String) map2.get("tosVersion");
            this.expiryTime = date;
        }
    }

    @Override // java.lang.Object
    public boolean equals(Object obj) {
        boolean z;
        if (!(obj instanceof NimbleIdentityPidInfo)) {
            z = false;
        } else {
            z = true;
            if (this != obj) {
                NimbleIdentityPidInfo nimbleIdentityPidInfo = (NimbleIdentityPidInfo) obj;
                if (!this.pid.equals(nimbleIdentityPidInfo.pid) || !this.strength.equals(nimbleIdentityPidInfo.strength) || !this.dob.equals(nimbleIdentityPidInfo.dob) || !this.country.equals(nimbleIdentityPidInfo.country) || !this.language.equals(nimbleIdentityPidInfo.language) || !this.locale.equals(nimbleIdentityPidInfo.locale) || !this.status.equals(nimbleIdentityPidInfo.status) || !this.reasonCode.equals(nimbleIdentityPidInfo.reasonCode) || !this.tosVersion.equals(nimbleIdentityPidInfo.tosVersion) || !this.dateCreated.equals(nimbleIdentityPidInfo.dateCreated) || !this.dateModified.equals(nimbleIdentityPidInfo.dateModified) || !this.lastAuthDate.equals(nimbleIdentityPidInfo.lastAuthDate) || !this.registrationSource.equals(nimbleIdentityPidInfo.registrationSource) || !this.authenticationSource.equals(nimbleIdentityPidInfo.authenticationSource)) {
                    return false;
                }
                z = true;
                if (!this.anonymousPid.equals(nimbleIdentityPidInfo.anonymousPid)) {
                    return false;
                }
            }
        }
        return z;
    }

    public String getAnonymousPid() {
        return this.anonymousPid;
    }

    public String getAuthenticationSource() {
        return this.authenticationSource;
    }

    public String getCountry() {
        return this.country;
    }

    public String getDateCreated() {
        return this.dateCreated;
    }

    public String getDateModified() {
        return this.dateModified;
    }

    public String getDob() {
        return this.dob;
    }

    public Date getExpiryTime() {
        return this.expiryTime;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getLastAuthDate() {
        return this.lastAuthDate;
    }

    public String getLocale() {
        return this.locale;
    }

    public String getPid() {
        return this.pid;
    }

    public String getReasonCode() {
        return this.reasonCode;
    }

    public String getRegistrationSource() {
        return this.registrationSource;
    }

    public String getStatus() {
        return this.status;
    }

    public String getStrength() {
        return this.strength;
    }

    public String getTosVersion() {
        return this.tosVersion;
    }

    public void setAnonymousPid(String str) {
        this.anonymousPid = str;
    }

    public void setAuthenticationSource(String str) {
        this.authenticationSource = str;
    }

    public void setCountry(String str) {
        this.country = str;
    }

    public void setDateCreated(String str) {
        this.dateCreated = str;
    }

    public void setDateModified(String str) {
        this.dateModified = str;
    }

    public void setDob(String str) {
        this.dob = str;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setExpiryTime(Date date) {
        this.expiryTime = date;
    }

    public void setLanguage(String str) {
        this.language = str;
    }

    public void setLastAuthDate(String str) {
        this.lastAuthDate = str;
    }

    public void setLocale(String str) {
        this.locale = str;
    }

    public void setPidId(String str) {
        this.pid = str;
    }

    public void setReasonCode(String str) {
        this.reasonCode = str;
    }

    public void setRegistrationSource(String str) {
        this.registrationSource = str;
    }

    public void setStatus(String str) {
        this.status = str;
    }

    public void setStrength(String str) {
        this.strength = str;
    }

    public void setTosVersion(String str) {
        this.tosVersion = str;
    }
}
