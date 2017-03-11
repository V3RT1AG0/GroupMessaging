package com.novoda.v3rt1ag0.channel.service;

import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.channel.data.model.Channels;
import com.novoda.v3rt1ag0.database.DatabaseResult;
import com.novoda.v3rt1ag0.user.data.model.User;
import com.novoda.v3rt1ag0.user.data.model.Users;

import rx.Observable;

public interface ChannelService {

    Observable<Channels> getChannelsFor(User user);

    Observable<DatabaseResult<Channel>> createPublicChannel(Channel newChannel);

    Observable<DatabaseResult<Channel>> createPrivateChannel(Channel newChannel, User owner);

    Observable<DatabaseResult<User>> addOwnerToPrivateChannel(Channel channel, User newOwner);

    Observable<DatabaseResult<User>> removeOwnerFromPrivateChannel(Channel channel, User removedOwner);

    Observable<DatabaseResult<Users>> getOwnersOfChannel(Channel channel);
}
