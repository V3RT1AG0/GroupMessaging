package com.novoda.v3rt1ag0.link;

import com.novoda.v3rt1ag0.user.data.model.User;

import java.net.URI;

public interface LinkFactory {

    URI inviteLinkFrom(User user);

}
