package com.novoda.v3rt1ag0.welcome.displayer;

import com.novoda.v3rt1ag0.user.data.model.User;

public interface WelcomeDisplayer {

    void attach(InteractionListener interactionListener);

    void detach(InteractionListener interactionListener);

    void display(User sender);

    interface InteractionListener {
        void onGetStartedClicked();
    }
}
