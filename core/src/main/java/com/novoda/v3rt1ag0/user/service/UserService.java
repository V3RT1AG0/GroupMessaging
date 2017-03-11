package com.novoda.v3rt1ag0.user.service;

import com.novoda.v3rt1ag0.user.data.model.User;
import com.novoda.v3rt1ag0.user.data.model.Users;

import rx.Observable;

public interface UserService {

    Observable<Users> getAllUsers();

    Observable<User> getUser(String userId);

}
