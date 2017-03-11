package com.novoda.v3rt1ag0.login.database;

import com.novoda.v3rt1ag0.login.data.model.Authentication;

import rx.Observable;

public interface AuthDatabase {

    Observable<Authentication> readAuthentication();

    Observable<Authentication> loginWithGoogle(String idToken);

}
