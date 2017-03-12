package com.novoda.v3rt1ag0.analytics;

public interface Analytics {

    void trackSignInStarted(String method);

    void trackSignInSuccessful(String method);

    void trackSelectChannel(String channelName, String id);

    void trackMessageLength(int messageLength, String userId, String channelName);

    void trackInvitationOpened(String senderId);

    void trackInvitationAccepted(String senderId);

    void trackManageOwners(String userId, String channelName);

    void trackAddChannelOwner(String channelName, String userId);

    void trackRemoveChannelOwner(String channelName, String userId);

    void trackSendInvitesSelected(String userId);

    void trackCreateChannel(String userId);
}
