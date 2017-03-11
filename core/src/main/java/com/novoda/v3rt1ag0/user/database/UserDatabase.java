package com.novoda.v3rt1ag0.user.database;

import com.novoda.v3rt1ag0.user.data.model.User;
import com.novoda.v3rt1ag0.user.data.model.Users;

import rx.Observable;

public interface UserDatabase {

    Observable<Users> observeUsers();

    Observable<User> readUserFrom(String userId);

    void writeCurrentUser(User user);

    Observable<User> observeUser(String userId);

}
