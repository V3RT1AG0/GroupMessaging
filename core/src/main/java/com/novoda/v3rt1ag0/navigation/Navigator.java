package com.novoda.v3rt1ag0.navigation;

import com.novoda.v3rt1ag0.channel.data.model.Channel;

public interface Navigator {

    void toChannel(Channel channel);

    void toChannels();

    void toCreateChannel();

    void toLogin();

    void toMembersOf(Channel channel);

    void toParent();

    void toChannelWithClearedHistory(Channel channel);

    void toShareInvite(String sharingLink);
}
