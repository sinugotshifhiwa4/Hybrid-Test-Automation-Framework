package com.hybridframework.config.properties;

public enum PropertiesFilePath {

    GLOBAL("global-config.properties"),
    DEVELOPMENT("config-dev.properties"),
    UAT("config-uat.properties"),
    PRODUCTION("config-prod.properties");

    private static final String PROPERTIES_ROOT_PATH = "src/main/resources/configFiles/";
    private final String filename;

    PropertiesFilePath(String filename) {
        this.filename = filename;
    }

    public String getPropertiesFilePath() {
        return PROPERTIES_ROOT_PATH + filename;
    }
}