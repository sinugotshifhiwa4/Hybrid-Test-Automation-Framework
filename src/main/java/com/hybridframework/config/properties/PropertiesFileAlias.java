package com.hybridframework.config.properties;

public enum PropertiesFileAlias {

    GLOBAL("GlobalConfig"),
    DEVELOPMENT("DevConfig"),
    UAT("UatConfig"),
    PRODUCTION("ProdConfig");

    private final String configAlias;

    PropertiesFileAlias(String configAlias) {
        this.configAlias = configAlias;
    }

    public String getConfigurationAlias() {
        return configAlias;
    }
}
