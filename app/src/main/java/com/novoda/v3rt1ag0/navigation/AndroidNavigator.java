package com.novoda.v3rt1ag0.navigation;

import android.app.Activity;
import android.content.Intent;

import com.novoda.v3rt1ag0.R;
import com.novoda.v3rt1ag0.channel.ChannelsActivity;
import com.novoda.v3rt1ag0.channel.NewChannelActivity;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.chat.ChatActivity;
import com.novoda.v3rt1ag0.login.LoginActivity;
import com.novoda.v3rt1ag0.user.UsersActivity;

public class AndroidNavigator implements Navigator {

    private final Activity activity;

    public AndroidNavigator(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void toChannel(Channel channel) {
        activity.startActivity(ChatActivity.createIntentFor(activity, channel));
    }

    @Override
    public void toChannels() {
        activity.startActivity(new Intent(activity, ChannelsActivity.class));
    }

    @Override
    public void toCreateChannel() {
        activity.startActivity(new Intent(activity, NewChannelActivity.class));
    }

    @Override
    public void toMembersOf(Channel channel) {
        activity.startActivity(UsersActivity.createIntentFor(activity, channel));
    }

    @Override
    public void toLogin() {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void toParent() {
        activity.finish();
    }

    @Override
    public void toChannelWithClearedHistory(Channel channel) {
        toChannel(channel);
        activity.finish();
    }

    @Override
    public void toShareInvite(String sharingLink) {
        String sharingMessage = activity.getString(R.string.send_invite);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.check_out_bonfire, sharingLink));
        activity.startActivity(Intent.createChooser(sharingIntent, sharingMessage));
    }

}
