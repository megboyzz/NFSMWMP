package com.ea.nimble.friends;

import com.ea.nimble.Error;
import java.util.ArrayList;

/* loaded from: stdlib.jar:com/ea/nimble/friends/INimbleOriginFriendsService.class */
public interface INimbleOriginFriendsService {

    /* loaded from: stdlib.jar:com/ea/nimble/friends/INimbleOriginFriendsService$NimbleFriendInvitationCallback.class */
    public interface NimbleFriendInvitationCallback {
        void onCallback(boolean z, Error error);
    }

    /* loaded from: stdlib.jar:com/ea/nimble/friends/INimbleOriginFriendsService$NimbleUserSearchCallback.class */
    public interface NimbleUserSearchCallback {
        void onCallback(ArrayList<NimbleUser> arrayList, Error error);
    }

    void acceptFriendInvitation(String str, NimbleFriendInvitationCallback nimbleFriendInvitationCallback);

    void declineFriendInvitation(String str, NimbleFriendInvitationCallback nimbleFriendInvitationCallback);

    void listFriendInvitationsReceived(NimbleUserSearchCallback nimbleUserSearchCallback);

    void listFriendInvitationsSent(NimbleUserSearchCallback nimbleUserSearchCallback);

    void searchUserByDisplayName(String str, NimbleUserSearchCallback nimbleUserSearchCallback);

    void searchUserByEmail(String str, NimbleUserSearchCallback nimbleUserSearchCallback);

    void sendFriendInvitation(String str, String str2, NimbleFriendInvitationCallback nimbleFriendInvitationCallback);

    void sendInvitationOverEmail(ArrayList<String> arrayList, String str, String str2, NimbleFriendInvitationCallback nimbleFriendInvitationCallback);

    void sendInvitationOverSMS(ArrayList<String> arrayList, String str, NimbleFriendInvitationCallback nimbleFriendInvitationCallback);
}
