package com.novoda.v3rt1ag0.user.service;

import com.novoda.v3rt1ag0.user.data.model.User;
import com.novoda.v3rt1ag0.user.data.model.Users;
import com.novoda.v3rt1ag0.user.database.UserDatabase;

import rx.Observable;

public class PersistedUserService implements UserService {

    private final UserDatabase userDatabase;

    public PersistedUserService(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    @Override
    public Observable<Users> getAllUsers() {
        return userDatabase.observeUsers();
    }

    @Override
    public Observable<User> getUser(String userId) {
        return userDatabase.observeUser(userId);
    }

}
