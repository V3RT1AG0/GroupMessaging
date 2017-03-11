package com.novoda.v3rt1ag0.user.presenter;

import com.novoda.v3rt1ag0.analytics.Analytics;
import com.novoda.v3rt1ag0.analytics.ErrorLogger;
import com.novoda.v3rt1ag0.channel.data.model.Channel;
import com.novoda.v3rt1ag0.channel.service.ChannelService;
import com.novoda.v3rt1ag0.database.DatabaseResult;
import com.novoda.v3rt1ag0.navigation.Navigator;
import com.novoda.v3rt1ag0.user.data.model.User;
import com.novoda.v3rt1ag0.user.data.model.Users;
import com.novoda.v3rt1ag0.user.displayer.UsersDisplayer;
import com.novoda.v3rt1ag0.user.service.UserService;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class UsersPresenter {
    private final UserService userService;
    private final ChannelService channelService;
    private final UsersDisplayer usersDisplayer;
    private final Channel channel;
    private final Navigator navigator;
    private final ErrorLogger errorLogger;
    private final Analytics analytics;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public UsersPresenter(UserService userService,
                          ChannelService channelService,
                          UsersDisplayer usersDisplayer,
                          Channel channel,
                          Navigator navigator,
                          ErrorLogger errorLogger,
                          Analytics analytics) {
        this.userService = userService;
        this.channelService = channelService;
        this.usersDisplayer = usersDisplayer;
        this.channel = channel;
        this.navigator = navigator;
        this.errorLogger = errorLogger;
        this.analytics = analytics;
    }

    public void startPresenting() {
        usersDisplayer.attach(selectionListener);

        subscriptions.add(
                userService.getAllUsers().subscribe(new Action1<Users>() {
                    @Override
                    public void call(Users users) {
                        usersDisplayer.display(users);
                    }
                })
        );
        subscriptions.add(
                channelService.getOwnersOfChannel(channel)
                        .subscribe(new Action1<DatabaseResult<Users>>() {
                            @Override
                            public void call(DatabaseResult<Users> databaseResult) {
                                if (databaseResult.isSuccess()) {
                                    usersDisplayer.displaySelectedUsers(databaseResult.getData());
                                } else {
                                    errorLogger.reportError(databaseResult.getFailure(), "Cannot fetch channel owners");
                                    usersDisplayer.showFailure();
                                }
                            }
                        })
        );
    }

    public void stopPresenting() {
        usersDisplayer.detach(selectionListener);
        subscriptions.clear();
        subscriptions = new CompositeSubscription();
    }

    private UsersDisplayer.SelectionListener selectionListener = new UsersDisplayer.SelectionListener() {
        @Override
        public void onUserSelected(final User user) {
            analytics.trackAddChannelOwner(channel.getName(), user.getId());
            channelService.addOwnerToPrivateChannel(channel, user)
                    .subscribe(updateOnActionResult());
        }

        @Override
        public void onUserDeselected(User user) {
            analytics.trackRemoveChannelOwner(channel.getName(), user.getId());
            channelService.removeOwnerFromPrivateChannel(channel, user)
                    .subscribe(updateOnActionResult());
        }

        @Override
        public void onCompleteClicked() {
            navigator.toParent();
        }
    };

    private Action1<DatabaseResult<User>> updateOnActionResult() {
        return new Action1<DatabaseResult<User>>() {
            @Override
            public void call(DatabaseResult<User> userDatabaseResult) {
                if (!userDatabaseResult.isSuccess()) {
                    errorLogger.reportError(userDatabaseResult.getFailure(), "Cannot update channel owners");
                    usersDisplayer.showFailure();
                }
            }
        };
    }

}
