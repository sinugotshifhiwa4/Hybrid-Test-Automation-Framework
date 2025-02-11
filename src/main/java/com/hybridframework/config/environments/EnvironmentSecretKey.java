package com.hybridframework.config.environments;

public enum EnvironmentSecretKey {

    DEVELOPMENT("DEVELOPMENT_SECRET_KEY"),
    UAT("UAT_SECRET_KEY"),
    PRODUCTION("PRODUCTION_SECRET_KEY");

    private final String keyName;

    EnvironmentSecretKey(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }
}