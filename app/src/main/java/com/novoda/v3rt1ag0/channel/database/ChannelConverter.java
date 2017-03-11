package com.novoda.v3rt1ag0.channel.database;

import com.novoda.v3rt1ag0.channel.data.model.Channel;

class ChannelConverter {

    static FirebaseChannel toFirebaseChannel(Channel channel) {
        return new FirebaseChannel(channel.getName(), channel.getAccess().name().toLowerCase());
    }

    static Channel fromFirebaseChannel(FirebaseChannel firebaseChannel) {
        return new Channel(firebaseChannel.getName(), Channel.Access.valueOf(firebaseChannel.getAccess().toUpperCase()));
    }

}
