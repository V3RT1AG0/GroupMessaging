package com.novoda.v3rt1ag0.login.service;

import com.novoda.v3rt1ag0.login.data.model.Authentication;

import rx.Observable;

public interface LoginService {

    Observable<Authentication> getAuthentication();

    void loginWithGoogle(String idToken);

}
