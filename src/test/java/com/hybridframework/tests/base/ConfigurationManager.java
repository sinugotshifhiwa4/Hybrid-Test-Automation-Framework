package com.hybridframework.tests.base;

import com.hybridframework.config.environments.EnvironmentConfigManager;
import com.hybridframework.config.environments.EnvironmentFileAlias;
import com.hybridframework.config.environments.EnvironmentFilePaths;
import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.config.properties.PropertiesFilePath;
import com.hybridframework.drivers.BrowserFactory;
import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.testDataStorage.TestContextStore;
import com.hybridframework.ui.pages.orangeHrmPages.LoginPage;
import com.hybridframework.utils.jacksonUtils.JsonConverter;
import com.hybridframework.utils.logging.ErrorHandler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigurationManager {

    public static void initializeConfigurations() {
        try {
            loadPropertyConfigurations();
            loadEnvironmentConfigurations();
        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeConfigurations", "Failed to initialize configurations");
            throw error;
        }
    }

    private static void loadPropertyConfigurations() {
        PropertiesConfigManager.loadConfiguration(
                PropertiesFileAlias.GLOBAL.getConfigurationAlias(),
                PropertiesFilePath.GLOBAL.getPropertiesFilePath()
        );
        PropertiesConfigManager.loadConfiguration(
                PropertiesFileAlias.UAT.getConfigurationAlias(),
                PropertiesFilePath.UAT.getPropertiesFilePath()
        );
    }

    private static void loadEnvironmentConfigurations() {
        loadEnvironmentFileIfExists(
                EnvironmentFilePaths.BASE,
                EnvironmentFileAlias.BASE
        );
        loadEnvironmentFileIfExists(
                EnvironmentFilePaths.UAT,
                EnvironmentFileAlias.UAT
        );
    }

    private static void loadEnvironmentFileIfExists(EnvironmentFilePaths filePath, EnvironmentFileAlias alias) {
        if (Files.exists(Paths.get(filePath.getEnvironmentFileFullPath()))) {
            EnvironmentConfigManager.loadConfiguration(
                    alias.getEnvironmentAlias(),
                    filePath.getEnvironmentFilename()
            );
        }
    }

    public static void initializeTestConfig(String... testIds) {
        initializeTestContext(testIds);
        initializeJsonMapper();
    }

    private static void initializeTestContext(String... testIds) {
        TestContextStore.initializeContext(testIds);
    }

    private static void initializeJsonMapper() {
        JsonConverter.initJsonMapper();
    }
}
