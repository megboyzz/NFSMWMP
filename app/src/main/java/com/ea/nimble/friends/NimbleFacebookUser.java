package com.ea.nimble.friends;

import com.ea.nimble.Global;
import java.util.Date;
import org.json.JSONObject;

class NimbleFacebookUser extends NimbleUser {

    public NimbleFacebookUser(JSONObject jSONObject) {
        this.authenticatorId = Global.NIMBLE_AUTHENTICATOR_FACEBOOK;
        this.userId = jSONObject.optString("id");
        this.displayName = jSONObject.optString("name");
        this.imageUrl = jSONObject.optJSONObject("picture").optJSONObject("data").optString("");
        this.refreshTimestamp = new Date();
        setPlayedCurrentGame(NimbleUser.PlayedCurrentGameFlag.PLAYED);
    }
}
