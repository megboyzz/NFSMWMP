package com.ea.nimble.friends;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ea.nimble.Base;
import com.ea.nimble.Component;
import com.ea.nimble.Global;
import com.ea.nimble.Log;
import com.ea.nimble.LogSource;
import com.ea.nimble.Utility;
import com.ea.nimble.identity.INimbleIdentity;
import com.ea.nimble.identity.INimbleIdentityAuthenticator;
import java.util.Hashtable;

/* loaded from: stdlib.jar:com/ea/nimble/friends/NimbleFriendsImpl.class */
public class NimbleFriendsImpl extends Component implements LogSource, INimbleFriends {
    private Hashtable<String, NimbleFriendsListImpl> m_friends = new Hashtable<>();
    private BroadcastReceiver m_authenticatorLoginChangeReceiver = new BroadcastReceiver() { // from class: com.ea.nimble.friends.NimbleFriendsImpl.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            INimbleIdentityAuthenticator authenticatorById;
            if (intent != null && intent.getExtras() != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(Global.NIMBLE_NOTIFICATION_IDENTITY_AUTHENTICATION_UPDATE)) {
                String string = intent.getExtras().getString(Global.NIMBLE_IDENTITY_DICTIONARY_KEY_AUTHENTICATOR_ID);
                if (Utility.validString(string) && (authenticatorById = ((INimbleIdentity) Base.getComponent("com.ea.nimble.identity")).getAuthenticatorById(string)) != null) {
                    if (authenticatorById.getState() == INimbleIdentityAuthenticator.NimbleIdentityAuthenticationState.NIMBLE_IDENTITY_AUTHENTICATION_SUCCESS) {
                        if (NimbleFriendsImpl.this.m_friends.get(string) != null) {
                            return;
                        }
                        if (Global.NIMBLE_AUTHENTICATOR_FACEBOOK == string) {
                            NimbleFriendsImpl.this.m_friends.put(string, new NimbleFriendsListFacebook());
                        } else if (Global.NIMBLE_AUTHENTICATOR_ORIGIN == string) {
                            NimbleFriendsImpl.this.m_friends.put(string, new NimbleFriendsListOrigin());
                        }
                    } else if (NimbleFriendsImpl.this.m_friends.get(string) != null) {
                        ((NimbleFriendsListImpl) NimbleFriendsImpl.this.m_friends.get(string)).clear();
                        NimbleFriendsImpl.this.m_friends.remove(string);
                    }
                }
            }
        }
    };

    private static void initialize() {
        Base.registerComponent(new NimbleFriendsImpl(), NimbleFriends.NIMBLE_COMPONENT_ID_FRIENDS);
    }

    @Override // com.ea.nimble.Component
    public void cleanup() {
        Log.Helper.LOGV(this, "Component cleanup", new Object[0]);
        try {
            Utility.unregisterReceiver(this.m_authenticatorLoginChangeReceiver);
        } catch (Exception e) {
        }
    }

    @Override // com.ea.nimble.Component
    public String getComponentId() {
        return NimbleFriends.NIMBLE_COMPONENT_ID_FRIENDS;
    }

    @Override // com.ea.nimble.friends.INimbleFriends
    public NimbleFriendsList getFriendsList(String str, boolean z) {
        NimbleFriendsList friendsList;
        synchronized (this) {
            friendsList = this.m_friends.get(str) != null ? this.m_friends.get(str).getFriendsList(z) : null;
        }
        return friendsList;
    }

    @Override // com.ea.nimble.LogSource
    public String getLogSourceTitle() {
        return "NimbleFriendsService";
    }

    @Override // com.ea.nimble.Component
    public void restore() {
        Log.Helper.LOGV(this, "Component restore", new Object[0]);
    }

    @Override // com.ea.nimble.Component
    public void resume() {
        Log.Helper.LOGV(this, "Component resume", new Object[0]);
    }

    @Override // com.ea.nimble.Component
    public void setup() {
        Log.Helper.LOGD(this, "Component setup", new Object[0]);
        try {
            Utility.registerReceiver(Global.NIMBLE_NOTIFICATION_IDENTITY_AUTHENTICATION_UPDATE, this.m_authenticatorLoginChangeReceiver);
        } catch (Exception e) {
        }
    }

    @Override // com.ea.nimble.Component
    public void suspend() {
        Log.Helper.LOGV(this, "Component suspend", new Object[0]);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.ea.nimble.Component
    public void teardown() {
        Log.Helper.LOGV(this, "Component teardown", new Object[0]);
    }
}
