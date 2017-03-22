package com.novoda.v3rt1ag0.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.novoda.v3rt1ag0.BaseActivity;
import com.novoda.v3rt1ag0.Dependencies;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.chat.ChatActivity;
import com.novoda.v3rt1ag0.login.displayer.LoginDisplayer;
import com.novoda.v3rt1ag0.login.presenter.LoginPresenter;
import com.novoda.v3rt1ag0.navigation.AndroidLoginNavigator;
import com.novoda.v3rt1ag0.navigation.AndroidNavigator;

public class LoginActivity extends BaseActivity {

    private static final int RC_SIGN_IN = 42;

    private LoginPresenter presenter;
    private AndroidLoginNavigator navigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d("TAG","Login activity");
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        LoginDisplayer loginDisplayer = (LoginDisplayer) findViewById(R.id.login_view);
        LoginGoogleApiClient loginGoogleApiClient = new LoginGoogleApiClient(this);
        loginGoogleApiClient.setupGoogleApiClient();
        navigator = new AndroidLoginNavigator(this, loginGoogleApiClient, new AndroidNavigator(this));
        presenter = new LoginPresenter(Dependencies.INSTANCE.getLoginService(),
                                       loginDisplayer,
                                       navigator,
                                       Dependencies.INSTANCE.getErrorLogger(),
                                       Dependencies.INSTANCE.getAnalytics());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!navigator.onActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
        if(getIntent().hasExtra("Channelname"))
        {
            startActivity(new Intent(this, ChatActivity.class));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

}
