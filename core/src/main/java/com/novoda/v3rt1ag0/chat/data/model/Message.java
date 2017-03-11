package com.novoda.v3rt1ag0.chat.data.model;

import com.novoda.v3rt1ag0.user.data.model.User;

public class Message {

    private User author;
    private String body;
    private long timestamp;

    @SuppressWarnings("unused") //Used by Firebase
    public Message() {
    }

    public Message(User author, String body) {
        this.author = author;
        this.body = body;
        this.timestamp = System.currentTimeMillis(); //TODO move timestamp db side ?
    }

    public User getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
