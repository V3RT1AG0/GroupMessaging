package com.novoda.v3rt1ag0.chat.service;

import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.data.model.Chat;
import com.novoda.v3rt1ag0.chat.data.model.Message;
import com.novoda.v3rt1ag0.database.DatabaseResult;

import rx.Observable;

public interface ChatService {

    Observable<DatabaseResult<Chat>> getChat(Channel channel);

    void sendMessage(Channel channel, Message message);

}
