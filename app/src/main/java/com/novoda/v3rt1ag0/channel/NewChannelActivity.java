package com.novoda.v3rt1ag0.channel;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.novoda.v3rt1ag0.BaseActivity;
import com.novoda.v3rt1ag0.Dependencies;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.channel.displayer.NewChannelDisplayer;
import com.novoda.v3rt1ag0.channel.presenter.NewChannelPresenter;
import com.novoda.v3rt1ag0.navigation.AndroidNavigator;

public class NewChannelActivity extends BaseActivity {

    private NewChannelPresenter newChannelPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_channel);
        Dependencies dependencies = Dependencies.INSTANCE;
        newChannelPresenter = new NewChannelPresenter((NewChannelDisplayer) findViewById(R.id.create_channel_view),
                                                      dependencies.getChannelService(),
                                                      dependencies.getLoginService(),
                                                      new AndroidNavigator(this),
                                                      dependencies.getErrorLogger()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        newChannelPresenter.startPresenting();
    }

    @Override
    protected void onStop() {
        newChannelPresenter.stopPresenting();
        super.onStop();
    }
}
