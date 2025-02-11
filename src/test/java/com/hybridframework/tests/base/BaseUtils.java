package com.hybridframework.tests.base;

import com.hybridframework.config.environments.EnvironmentConfigManager;
import com.hybridframework.config.environments.EnvironmentFileAlias;
import com.hybridframework.config.environments.EnvironmentFilePaths;
import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.config.properties.PropertiesFilePath;
import com.hybridframework.testDataStorage.TestContextStore;
import com.hybridframework.utils.jacksonUtils.JsonConverter;
import com.hybridframework.utils.logging.ErrorHandler;

import java.nio.file.Files;
import java.nio.file.Paths;

public class BaseUtils {

    public static void loadConfigurations(){
        try{
            // Load Global Config
            PropertiesConfigManager.loadConfiguration(
                    PropertiesFileAlias.GLOBAL.getConfigurationAlias(),
                    PropertiesFilePath.GLOBAL.getPropertiesFilePath()
            );

            // Load Uat Config
            PropertiesConfigManager.loadConfiguration(
                    PropertiesFileAlias.UAT.getConfigurationAlias(),
                    PropertiesFilePath.UAT.getPropertiesFilePath()
            );

            // Load Base Environment File if it exists
            if (Files.exists(Paths.get(EnvironmentFilePaths.BASE.getEnvironmentFileFullPath()))) {
                EnvironmentConfigManager.loadConfiguration(
                        EnvironmentFileAlias.BASE.getEnvironmentAlias(),
                        EnvironmentFilePaths.BASE.getEnvironmentFilename()
                );
            }

            // Load UAT Environment File if it exists
            if (Files.exists(Paths.get(EnvironmentFilePaths.UAT.getEnvironmentFileFullPath()))) {
                EnvironmentConfigManager.loadConfiguration(
                        EnvironmentFileAlias.UAT.getEnvironmentAlias(),
                        EnvironmentFilePaths.UAT.getEnvironmentFilename()
                );
            }
        } catch (Exception error){
            ErrorHandler.logError(error, "loadAConfigurations", "Failed to load configurations");
            throw error;
        }
    }

    public static void initializeTestDataContext(String... testIds) {
        // initialize TestContextStore
        TestContextStore.initializeContext(testIds);
    }

    public static void initializeJsonMapper() {
        // Initialize ObjectMapper
        JsonConverter.initJsonMapper();
    }
}
