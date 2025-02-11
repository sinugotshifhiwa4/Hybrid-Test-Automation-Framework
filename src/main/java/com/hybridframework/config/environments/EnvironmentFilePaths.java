package com.hybridframework.config.environments;

public enum EnvironmentFilePaths {

    BASE(".env"),
    DEVELOPMENT(".env.dev"),
    UAT(".env.uat"),
    PRODUCTION(".env.prod");

    private static final String ENV_DIRECTORY = "envs";
    private final String filename;

    EnvironmentFilePaths(String filename) {
        this.filename = filename;
    }

    public String getEnvironmentFilename() {
        return filename;
    }

    public String getEnvironmentFileFullPath() {
        return ENV_DIRECTORY + "/" + filename;
    }

    public static String getDirectoryPath() {
        return ENV_DIRECTORY;
    }
}
