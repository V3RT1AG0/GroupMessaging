package com.novoda.v3rt1ag0.channel.data.model;

import com.novoda.v3rt1ag0.user.data.model.User;

public class UserSearchResult {

    private final User user;

    public UserSearchResult(User user) {
        this.user = user;
    }

    public UserSearchResult() {
        this.user = null;
    }

    public boolean isSuccess() {
        return user != null;
    }

    public User getUser() {
        return user;
    }
}
