package com.novoda.v3rt1ag0.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.novoda.v3rt1ag0.BaseActivity;
import com.novoda.v3rt1ag0.Dependencies;
import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.displayer.ChatDisplayer;
import com.novoda.v3rt1ag0.chat.presenter.ChatPresenter;
import com.novoda.v3rt1ag0.navigation.AndroidNavigator;

public class ChatActivity extends BaseActivity {

    private static final String NAME_EXTRA = "channel_name";
    private static final String ACCESS_EXTRA = "channel_access";
    private ChatPresenter presenter;

    public static Intent createIntentFor(Context context, Channel channel) {
        Intent intent = new Intent(context, ChatActivity.class);

        intent.putExtra(NAME_EXTRA, channel.getName());
        intent.putExtra(ACCESS_EXTRA, channel.getAccess().name());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ChatDisplayer chatDisplayer = (ChatDisplayer) findViewById(R.id.chat_view);
        Channel channel = new Channel(getIntent().getStringExtra(NAME_EXTRA),
                                      Channel.Access.valueOf(getIntent().getStringExtra(ACCESS_EXTRA)));
        presenter = new ChatPresenter(
                Dependencies.INSTANCE.getLoginService(),
                Dependencies.INSTANCE.getChatService(),
                chatDisplayer,
                channel,
                Dependencies.INSTANCE.getAnalytics(),
                new AndroidNavigator(this),
                Dependencies.INSTANCE.getErrorLogger()
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startPresenting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.stopPresenting();
    }

}
