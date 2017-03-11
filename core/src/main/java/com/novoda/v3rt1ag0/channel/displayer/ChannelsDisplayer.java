package com.novoda.v3rt1ag0.channel.displayer;

import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.channel.data.model.Channels;

public interface ChannelsDisplayer {

    void display(Channels channels);

    void attach(ChannelsInteractionListener channelsInteractionListener);

    void detach(ChannelsInteractionListener channelsInteractionListener);

    interface ChannelsInteractionListener {
        void onChannelSelected(Channel channel);

        void onAddNewChannel();

        void onInviteUsersClicked();
    }
}
