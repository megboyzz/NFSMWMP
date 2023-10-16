/*
 * Decompiled with CFR 0.152.
 */
package com.ea.nimble.bridge;

import com.ea.nimble.Error;
import com.ea.nimble.friends.INimbleOriginFriendsService;
import com.ea.nimble.friends.NimbleFriendsList;
import com.ea.nimble.friends.NimbleFriendsRefreshCallback;
import com.ea.nimble.friends.NimbleFriendsRefreshResult;
import com.ea.nimble.friends.NimbleFriendsRefreshScope;
import com.ea.nimble.friends.NimbleUser;

import java.util.ArrayList;

public class FriendsNativeCallback
implements INimbleOriginFriendsService.NimbleFriendInvitationCallback,
INimbleOriginFriendsService.NimbleUserSearchCallback,
NimbleFriendsRefreshCallback {
    private int m_id;

    public FriendsNativeCallback(int n2) {
        this.m_id = n2;
    }

    public void finalize() {
        BaseNativeCallback.nativeFinalize(this.m_id);
    }

    @Override
    public void onCallback(NimbleFriendsList nimbleFriendsList, NimbleFriendsRefreshScope nimbleFriendsRefreshScope, NimbleFriendsRefreshResult nimbleFriendsRefreshResult) {
        BaseNativeCallback.nativeCallback(this.m_id, nimbleFriendsList, nimbleFriendsRefreshScope, nimbleFriendsRefreshResult);
    }

    @Override
    public void onCallback(ArrayList<NimbleUser> arrayList, Error error) {
        BaseNativeCallback.nativeCallback(this.m_id, arrayList, error);
    }

    @Override
    public void onCallback(boolean bl2, Error error) {
        BaseNativeCallback.nativeCallback(this.m_id, bl2, error);
    }
}

