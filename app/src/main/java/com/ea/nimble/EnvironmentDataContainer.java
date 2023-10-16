/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class EnvironmentDataContainer
implements ISynergyEnvironment,
LogSource,
Externalizable {
    private static final int SYNERGY_DIRECTOR_RESPONSE_APP_VERSION_OK = 0;
    private static final int SYNERGY_DIRECTOR_RESPONSE_APP_VERSION_UPGRADE_RECOMMENDED = 1;
    private static final int SYNERGY_DIRECTOR_RESPONSE_APP_VERSION_UPGRADE_REQUIRED = 2;
    private String m_applicationLanguageCode = "en";
    private String m_eaDeviceId;
    private Map<String, Object> m_getDirectionResponseDictionary = new HashMap<String, Object>();
    private Long m_lastDirectorResponseTimestamp;
    private Map<String, String> m_serverUrls;
    private String m_synergyAnonymousId;

    @Override
    public Error checkAndInitiateSynergyEnvironmentUpdate() {
        return null;
    }

    @Override
    public String getEADeviceId() {
        return this.m_eaDeviceId;
    }

    @Override
    public String getEAHardwareId() {
        if (this.m_getDirectionResponseDictionary == null) return null;
        if (!this.m_getDirectionResponseDictionary.isEmpty()) return (String)this.m_getDirectionResponseDictionary.get("hwId");
        return null;
    }

    Map<String, Object> getGetDirectionResponseDictionary() {
        return this.m_getDirectionResponseDictionary;
    }

    @Override
    public String getGosMdmAppKey() {
        if (this.m_getDirectionResponseDictionary == null) return null;
        if (!this.m_getDirectionResponseDictionary.isEmpty()) return (String)this.m_getDirectionResponseDictionary.get("mdmAppKey");
        return null;
    }

    /*
     * Unable to fully structure code
     */
    public Set<String> getKeysOfDifferences(ISynergyEnvironment var1_1) {
        HashSet<String> var2_3 = new HashSet<String>();
        if (var1_1 == null) {

            if (Utility.stringsAreEquivalent(this.getEADeviceId(), var1_1.getEADeviceId())){
                var2_3.add("ENVIRONMENT_KEY_EADEVICEID");
            }
            if (!Utility.stringsAreEquivalent(this.getEAHardwareId(), var1_1.getEAHardwareId())) {
                var2_3.add("ENVIRONMENT_KEY_EAHARDWAREID");
            }
            if (!Utility.stringsAreEquivalent(this.getSynergyId(), var1_1.getSynergyId())) {
                var2_3.add("ENVIRONMENT_KEY_SYNERGYID");
            }
            if (!Utility.stringsAreEquivalent(this.getSellId(), var1_1.getSellId())) {
                var2_3.add("ENVIRONMENT_KEY_SELLID");
            }
            if (!Utility.stringsAreEquivalent(this.getProductId(), var1_1.getProductId())) {
                var2_3.add("ENVIRONMENT_KEY_PRODUCTID");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("synergy.drm"), var1_1.getServerUrlWithKey("synergy.drm"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_DRM");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("synergy.director"), var1_1.getServerUrlWithKey("synergy.director"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_DIRECTOR");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("synergy.m2u"), var1_1.getServerUrlWithKey("synergy.m2u"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_MESSAGE_TO_USER");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("synergy.product"), var1_1.getServerUrlWithKey("synergy.product"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_PRODUCT");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("synergy.tracking"), var1_1.getServerUrlWithKey("synergy.tracking"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_TRACKING");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("synergy.user"), var1_1.getServerUrlWithKey("synergy.user"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_USER");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("geoip.url"), var1_1.getServerUrlWithKey("geoip.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_CENTRAL_IP_GEOLOCATION");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("synergy.s2s"), var1_1.getServerUrlWithKey("synergy.s2s"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_SYNERGY_S2S");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("friends.url"), var1_1.getServerUrlWithKey("friends.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_ORIGIN_FRIENDS");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("eadp.friends.host"), var1_1.getServerUrlWithKey("eadp.friends.host"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_EADP_FRIENDS_HOST");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("avatars.url"), var1_1.getServerUrlWithKey("avatars.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_ORIGIN_AVATAR");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("origincasualapp.url"), var1_1.getServerUrlWithKey("origincasualapp.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_ORIGIN_CASUAL_APP");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("origincasualserver.url"), var1_1.getServerUrlWithKey("origincasualserver.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_ORIGIN_CASUAL_SERVER");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("akamai.url"), var1_1.getServerUrlWithKey("akamai.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_AKAMAI");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("dmg.url"), var1_1.getServerUrlWithKey("dmg.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_DYNAMIC_MORE_GAMES");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("mayhem.url"), var1_1.getServerUrlWithKey("mayhem.url"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_MAYHEM");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("nexus.connect"), var1_1.getServerUrlWithKey("nexus.connect"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_KEY_IDENTITY_CONNECT");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("nexus.proxy"), var1_1.getServerUrlWithKey("nexus.proxy"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_KEY_IDENTITY_PROXY");
            }
            if (!Utility.stringsAreEquivalent(this.getServerUrlWithKey("nexus.portal"), var1_1.getServerUrlWithKey("nexus.portal"))) {
                var2_3.add("ENVIRONMENT_KEY_SERVER_URL_KEY_IDENTITY_PORTAL");
            }
            if (this.getLatestAppVersionCheckResult() != var1_1.getLatestAppVersionCheckResult()) {
                var2_3.add("ENVIRONMENT_KEY_APP_VERSION_CHECK_RESULT");
            }
        }

        if (var2_3.size() <= 0) return null;
        return var2_3;
    }

    @Override
    public int getLatestAppVersionCheckResult() {
        int n2 = 0;
        if (this.m_getDirectionResponseDictionary == null) return -1;
        if (this.m_getDirectionResponseDictionary.isEmpty()) {
            return -1;
        }
        Object object = this.m_getDirectionResponseDictionary.get("appUpgrade");
        if (object instanceof Integer) {
            n2 = (Integer)object;
        } else if (object instanceof String) {
            n2 = Integer.parseInt((String)object);
        }
        switch (n2) {
            default: {
                return 0;
            }
            case 0: {
                return 0;
            }
            case 1: {
                return 1;
            }
            case 2: 
        }
        return 2;
    }

    @Override
    public String getLogSourceTitle() {
        return "SynergyEnv";
    }

    Long getMostRecentDirectorResponseTimestamp() {
        return this.m_lastDirectorResponseTimestamp;
    }

    @Override
    public String getNexusClientId() {
        if (this.m_getDirectionResponseDictionary == null) return null;
        if (!this.m_getDirectionResponseDictionary.isEmpty()) return (String)this.m_getDirectionResponseDictionary.get("clientId");
        return null;
    }

    @Override
    public String getNexusClientSecret() {
        if (this.m_getDirectionResponseDictionary == null) return null;
        if (!this.m_getDirectionResponseDictionary.isEmpty()) return (String)this.m_getDirectionResponseDictionary.get("clientSecret");
        return null;
    }

    @Override
    public String getProductId() {
        if (this.m_getDirectionResponseDictionary == null) return null;
        if (!this.m_getDirectionResponseDictionary.isEmpty()) return (String)this.m_getDirectionResponseDictionary.get("productId");
        return null;
    }

    @Override
    public String getSellId() {
        if (this.m_getDirectionResponseDictionary == null) return null;
        if (!this.m_getDirectionResponseDictionary.isEmpty()) return (String)this.m_getDirectionResponseDictionary.get("sellId");
        return null;
    }

    @Override
    public String getServerUrlWithKey(String string2) {
        if (this.m_serverUrls != null) return this.m_serverUrls.get(string2);
        return null;
    }

    Map<String, String> getServerUrls() {
        return this.m_serverUrls;
    }

    String getSynergyAnonymousId() {
        return this.m_synergyAnonymousId;
    }

    @Override
    public String getSynergyDirectorServerUrl(NimbleConfiguration nimbleConfiguration) {
        return SynergyEnvironment.getComponent().getSynergyDirectorServerUrl(nimbleConfiguration);
    }

    @Override
    public String getSynergyId() {
        return this.m_synergyAnonymousId;
    }

    @Override
    public int getTrackingPostInterval() {
        if (this.m_getDirectionResponseDictionary == null) return -1;
        if (this.m_getDirectionResponseDictionary.isEmpty()) {
            return -1;
        }
        Integer n2 = (Integer)this.m_getDirectionResponseDictionary.get("telemetryFreq");
        if (n2 == null) return -1;
        return n2;
    }

    @Override
    public boolean isDataAvailable() {
        return true;
    }

    @Override
    public boolean isUpdateInProgress() {
        return false;
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.m_getDirectionResponseDictionary = (Map)objectInput.readObject();
        if (this.m_getDirectionResponseDictionary.isEmpty()) {
            this.m_getDirectionResponseDictionary = null;
        }
        this.m_serverUrls = (Map)objectInput.readObject();
        if (this.m_serverUrls.isEmpty()) {
            this.m_serverUrls = null;
        }
        this.m_eaDeviceId = (String)objectInput.readObject();
        if (this.m_eaDeviceId.length() == 0) {
            this.m_eaDeviceId = null;
        }
        this.m_synergyAnonymousId = (String)objectInput.readObject();
        if (this.m_synergyAnonymousId.length() == 0) {
            this.m_synergyAnonymousId = null;
        }
        this.m_lastDirectorResponseTimestamp = objectInput.readLong();
        if (this.m_lastDirectorResponseTimestamp == 0L) {
            this.m_lastDirectorResponseTimestamp = null;
        }
        this.m_applicationLanguageCode = (String)objectInput.readObject();
        if (this.m_applicationLanguageCode.length() != 0) return;
        this.m_applicationLanguageCode = null;
    }

    void setEADeviceId(String string2) {
        this.m_eaDeviceId = string2;
    }

    void setGetDirectionResponseDictionary(Map<String, Object> map) {
        if (map != null) {
            map.put("sellId", ((Integer)map.get("sellId")).toString());
            map.put("productId", ((Integer)map.get("productId")).toString());
            map.put("hwId", ((Integer)map.get("hwId")).toString());
            this.m_getDirectionResponseDictionary = map;
            return;
        }
        this.m_getDirectionResponseDictionary = new HashMap<String, Object>();
    }

    void setMostRecentDirectorResponseTimestamp(Long l2) {
        this.m_lastDirectorResponseTimestamp = l2;
    }

    void setServerUrls(Map<String, String> map) {
        this.m_serverUrls = map;
    }

    void setSynergyAnonymousId(String string2) {
        this.m_synergyAnonymousId = string2;
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        Object object = this.m_getDirectionResponseDictionary == null ? new HashMap<String, Object>() : this.m_getDirectionResponseDictionary;
        objectOutput.writeObject(object);
        object = this.m_serverUrls == null ? new HashMap<String, Object>() : this.m_serverUrls;
        objectOutput.writeObject(object);
        object = this.m_eaDeviceId == null ? "" : this.m_eaDeviceId;
        objectOutput.writeObject(object);
        object = this.m_synergyAnonymousId == null ? "" : this.m_synergyAnonymousId;
        objectOutput.writeObject(object);
        long l2 = this.m_lastDirectorResponseTimestamp == null ? 0L : this.m_lastDirectorResponseTimestamp;
        objectOutput.writeLong(l2);
        object = this.m_applicationLanguageCode == null ? "" : this.m_applicationLanguageCode;
        objectOutput.writeObject(object);
    }
}

