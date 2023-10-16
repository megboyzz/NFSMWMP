package com.ea.nimble.friends;

import org.json.JSONObject;

/* loaded from: stdlib.jar:com/ea/nimble/friends/UserInfoFromIdentity.class */
class UserInfoFromIdentity {
    private String externalRefValue;
    private String personaId;
    private String pidId;

    public UserInfoFromIdentity(JSONObject jSONObject) {
        this.pidId = jSONObject.optString("pidId");
        this.personaId = jSONObject.optString("personaId");
        this.externalRefValue = jSONObject.optString("externalRefValue");
    }

    public String getExternalRefValue() {
        return this.externalRefValue;
    }

    public String getPersonaId() {
        return this.personaId;
    }

    public String getPidId() {
        return this.pidId;
    }
}
