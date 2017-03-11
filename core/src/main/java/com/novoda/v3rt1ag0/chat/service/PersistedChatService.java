package com.novoda.v3rt1ag0.chat.service;

import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.data.model.Chat;
import com.novoda.v3rt1ag0.chat.data.model.Message;
import com.novoda.v3rt1ag0.chat.database.ChatDatabase;
import com.novoda.v3rt1ag0.database.DatabaseResult;

import rx.Observable;
import rx.functions.Func1;

public class PersistedChatService implements ChatService {

    private final ChatDatabase chatDatabase;

    public PersistedChatService(ChatDatabase chatDatabase) {
        this.chatDatabase = chatDatabase;
    }

    @Override
    public Observable<DatabaseResult<Chat>> getChat(final Channel channel) {
        return chatDatabase.observeChat(channel)
                .map(asDatabaseResult())
                .onErrorReturn(DatabaseResult.<Chat>errorAsDatabaseResult());
    }

    private static Func1<Chat, DatabaseResult<Chat>> asDatabaseResult() {
        return new Func1<Chat, DatabaseResult<Chat>>() {
            @Override
            public DatabaseResult<Chat> call(Chat chat) {
                return new DatabaseResult<Chat>(chat);
            }
        };
    }

    @Override
    public void sendMessage(Channel channel, Message message) {
        chatDatabase.sendMessage(channel, message);
    }

}
