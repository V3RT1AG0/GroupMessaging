package com.novoda.v3rt1ag0.navigation;

public interface LoginNavigator extends Navigator {

    void toGooglePlusLogin();

    void attach(LoginResultListener loginResultListener);

    void detach(LoginResultListener loginResultListener);

    interface LoginResultListener {

        void onGooglePlusLoginSuccess(String tokenId);

        void onGooglePlusLoginFailed(String statusMessage);

    }

}
