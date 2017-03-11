package com.novoda.v3rt1ag0.login.displayer;

public interface LoginDisplayer {

    void attach(LoginActionListener actionListener);

    void detach(LoginActionListener actionListener);

    void showAuthenticationError(String message);

    public interface LoginActionListener {

        void onGooglePlusLoginSelected();

    }

}
