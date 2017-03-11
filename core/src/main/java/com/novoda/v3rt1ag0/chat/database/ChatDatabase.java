package com.novoda.v3rt1ag0.chat.database;

import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.data.model.Chat;
import com.novoda.v3rt1ag0.chat.data.model.Message;

import rx.Observable;

public interface ChatDatabase {

    Observable<Chat> observeChat(Channel channel);

    void sendMessage(Channel channel, Message message);

}
