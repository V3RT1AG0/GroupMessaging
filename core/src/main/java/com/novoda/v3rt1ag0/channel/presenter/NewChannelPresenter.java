package com.novoda.v3rt1ag0.channel.presenter;

import com.novoda.v3rt1ag0.analytics.ErrorLogger;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.channel.data.model.Channel.Access;
import com.novoda.v3rt1ag0.channel.displayer.NewChannelDisplayer;
import com.novoda.v3rt1ag0.channel.service.ChannelService;
import com.novoda.v3rt1ag0.database.DatabaseResult;
import com.novoda.v3rt1ag0.login.data.model.Authentication;
import com.novoda.v3rt1ag0.login.service.LoginService;
import com.novoda.v3rt1ag0.navigation.Navigator;
import com.novoda.v3rt1ag0.user.data.model.User;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class NewChannelPresenter {

    private final NewChannelDisplayer newChannelDisplayer;
    private final ChannelService channelService;
    private final LoginService loginService;
    private final Navigator navigator;
    private final ErrorLogger errorLogger;
    private User user;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public NewChannelPresenter(NewChannelDisplayer newChannelDisplayer,
                               ChannelService channelService,
                               LoginService loginService,
                               Navigator navigator,
                               ErrorLogger errorLogger) {
        this.newChannelDisplayer = newChannelDisplayer;
        this.channelService = channelService;
        this.loginService = loginService;
        this.navigator = navigator;
        this.errorLogger = errorLogger;
    }

    public void startPresenting() {
        newChannelDisplayer.attach(channelCreationListener);
        subscriptions.add(
                loginService.getAuthentication().subscribe(new Action1<Authentication>() {
                    @Override
                    public void call(Authentication authentication) {
                        user = authentication.getUser();
                    }
                })
        );
    }

    public void stopPresenting() {
        newChannelDisplayer.detach(channelCreationListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    private NewChannelDisplayer.ChannelCreationListener channelCreationListener = new NewChannelDisplayer.ChannelCreationListener() {

        @Override
        public void onCreateChannelClicked(String channelName, boolean isPrivate) {
            Channel newChannel = new Channel(channelName.trim(), isPrivate ? Access.PRIVATE : Access.PUBLIC);
            subscriptions.add(
                    create(newChannel).subscribe(new Action1<DatabaseResult<Channel>>() {
                        @Override
                        public void call(DatabaseResult<Channel> databaseResult) {
                            if (databaseResult.isSuccess()) {
                                navigator.toChannelWithClearedHistory(databaseResult.getData());
                            } else {
                                errorLogger.reportError(databaseResult.getFailure(), "Channel creation failed");
                                newChannelDisplayer.showChannelCreationError();
                            }
                        }
                    })
            );
        }

        @Override
        public void onCancel() {
            navigator.toParent();
        }
    };

    private Observable<DatabaseResult<Channel>> create(Channel newChannel) {
        if (newChannel.isPrivate()) {
            return channelService.createPrivateChannel(newChannel, user);
        } else {
            return channelService.createPublicChannel(newChannel);
        }
    }

}
