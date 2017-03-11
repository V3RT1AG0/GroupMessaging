package com.novoda.v3rt1ag0;

import com.novoda.v3rt1ag0.analytics.Analytics;
import com.novoda.v3rt1ag0.analytics.ErrorLogger;
import com.novoda.v3rt1ag0.channel.service.ChannelService;
import com.novoda.v3rt1ag0.chat.service.ChatService;
import com.novoda.v3rt1ag0.login.service.LoginService;
import com.novoda.v3rt1ag0.user.service.UserService;
import com.novoda.notils.exception.DeveloperError;

import java.lang.reflect.Field;

import org.mockito.Mockito;

public class TestDependencies {

    private TestDependencies() {
        // use init method to create an instance
    }

    public static TestDependencies init() {
        return new TestDependencies()
                .withLoginService(Mockito.mock(LoginService.class))
                .withUserService(Mockito.mock(UserService.class))
                .withChatService(Mockito.mock(ChatService.class))
                .withChannelService(Mockito.mock(ChannelService.class))
                .withAnalytics(Mockito.mock(Analytics.class))
                .withErrorLogger(Mockito.mock(ErrorLogger.class));
    }

    public TestDependencies withLoginService(LoginService loginService) {
        setDependency("loginService", loginService);
        return this;
    }

    public TestDependencies withUserService(UserService userService) {
        setDependency("userService", userService);
        return this;
    }

    public TestDependencies withChatService(ChatService chatService) {
        setDependency("chatService", chatService);
        return this;
    }

    public TestDependencies withChannelService(ChannelService channelService) {
        setDependency("channelService", channelService);
        return this;
    }

    public TestDependencies withAnalytics(Analytics analytics) {
        setDependency("analytics", analytics);
        return this;
    }

    private TestDependencies withErrorLogger(ErrorLogger errorLogger) {
        setDependency("errorLogger", errorLogger);
        return this;
    }

    private void setDependency(String fieldName, Object value) {
        try {
            Dependencies dependencies = Dependencies.INSTANCE;
            Field field = dependencies.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(dependencies, value);
        } catch (NoSuchFieldException e) {
            throw new DeveloperError("The field you're trying to access does not exist: " + fieldName, e);
        } catch (IllegalAccessException e) {
            throw new DeveloperError("Cannot access the field  " + fieldName, e);
        }
    }
}
