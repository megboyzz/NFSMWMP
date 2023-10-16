package com.ea.nimble.friends;

import com.ea.nimble.Global;
import com.ea.nimble.friends.NimbleUser;
import com.google.android.gms.plus.PlusShare;
import java.util.Date;
import org.json.JSONObject;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFacebookUser.class */
class NimbleFacebookUser extends NimbleUser {
    /* JADX INFO: Access modifiers changed from: package-private */
    public NimbleFacebookUser(JSONObject jSONObject) {
        this.authenticatorId = Global.NIMBLE_AUTHENTICATOR_FACEBOOK;
        this.userId = jSONObject.optString("id");
        this.displayName = jSONObject.optString("name");
        this.imageUrl = jSONObject.optJSONObject("picture").optJSONObject("data").optString(PlusShare.KEY_CALL_TO_ACTION_URL);
        this.refreshTimestamp = new Date();
        setPlayedCurrentGame(NimbleUser.PlayedCurrentGameFlag.PLAYED);
    }
}
