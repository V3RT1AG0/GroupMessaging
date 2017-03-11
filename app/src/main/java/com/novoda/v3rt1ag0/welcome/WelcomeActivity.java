package com.novoda.v3rt1ag0.welcome;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.novoda.v3rt1ag0.BaseActivity;
import com.novoda.v3rt1ag0.Dependencies;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.link.FirebaseDynamicLinkFactory;
import com.novoda.v3rt1ag0.navigation.AndroidNavigator;
import com.novoda.v3rt1ag0.welcome.displayer.WelcomeDisplayer;
import com.novoda.v3rt1ag0.welcome.presenter.WelcomePresenter;

public class WelcomeActivity extends BaseActivity {

    private WelcomePresenter welcomePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        String sender = getIntent().getData().getQueryParameter(FirebaseDynamicLinkFactory.SENDER);
        welcomePresenter = new WelcomePresenter(
                Dependencies.INSTANCE.getUserService(),
                (WelcomeDisplayer) findViewById(R.id.welcome_view),
                new AndroidNavigator(this),
                Dependencies.INSTANCE.getAnalytics(),
                sender
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        welcomePresenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        welcomePresenter.stopPresenting();
    }
}
