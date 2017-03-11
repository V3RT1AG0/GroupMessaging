package com.novoda.v3rt1ag0.analytics;

public interface ErrorLogger {

    void reportError(Throwable throwable, Object... args);

}
