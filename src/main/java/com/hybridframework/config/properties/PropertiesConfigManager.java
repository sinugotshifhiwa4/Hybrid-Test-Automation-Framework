package com.hybridframework.config.properties;

import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesConfigManager {

    private static final Logger logger = LoggerUtils.getLogger(PropertiesConfigManager.class);

    /**
     * Thread-safe cache for storing loaded PropertiesConfig instances.
     */
    private static final Map<String, PropertiesConfigManager> propertyConfigurationCache = new ConcurrentHashMap<>();

    private final Properties properties;
    private final String propertiesFilePath;

    private PropertiesConfigManager(String propertiesFilePath) {
        try {
            validateFilePath(propertiesFilePath);

            // assign ...
            this.properties = new Properties();
            this.propertiesFilePath = propertiesFilePath;

            loadProperties();
        } catch (Exception error) {
            ErrorHandler.logError(
                    error,
                    "Constructor",
                    "Failed to initialize properties from file");
            throw new RuntimeException(error);
        }
    }

    public static void loadConfiguration(String configAlias, String propertiesFileName) {
        propertyConfigurationCache.computeIfAbsent(configAlias, key -> {
            try {
                logger.info("Property configuration with alias '{}' loaded successfully.", configAlias);
                return new PropertiesConfigManager(propertiesFileName);
            } catch (Exception error) {
                ErrorHandler.logError(error, "loadConfiguration",
                        "Failed to load config file");
                throw error;
            }
        });
    }

    public String getProperty(String propertyKey) {
        try {
            String systemValue = System.getProperty(propertyKey);
            if (systemValue != null) {
                logger.info("Using system property for '{}': '{}'", propertyKey, systemValue);
                return systemValue;
            }

            String value = properties.getProperty(propertyKey);
            if (value == null || value.isEmpty()) {
                logger.warn("Property '{}' not found or empty in properties file", propertyKey);
                throw new IllegalArgumentException("Property '" + propertyKey + "' not found or empty in properties file");
            }
            return value;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getProperty",
                    "Failed to retrieve property");
            throw error;
        }
    }

    public String getProperty(String propertyKey, String defaultValue) {
        try {
            String systemValue = System.getProperty(propertyKey);
            if (systemValue != null) {
                logger.info("Retrieved system property for '{}': '{}'", propertyKey, systemValue);
                return systemValue;
            }

            String value = properties.getProperty(propertyKey, defaultValue);
            if (value.equals(defaultValue)) {
                logger.warn("Property '{}' not found, using default: '{}'", propertyKey, defaultValue);
            } else {
                logger.info("Property retrieved successfully");
            }
            return value;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getProperty",
                    "Failed to retrieve property");
            throw error;
        }
    }

    public static PropertiesConfigManager getConfiguration(String configAlias) {
        try {
            PropertiesConfigManager config = propertyConfigurationCache.get(configAlias);
            if (config == null) {
                logger.error("Configuration with alias '{}' not loaded.", configAlias);
                throw new IllegalStateException(
                        "Configuration with alias '" + configAlias + "' not loaded. Call loadConfiguration() first.");
            }
            return config;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getConfiguration",
                    "Failed to retrieve configuration with alias");
            throw error;
        }
    }

    public static String getPropertyKeyFromCache(String aliasName, String propertyKey){
        try{
            return getConfiguration(aliasName).getProperty(propertyKey);
        } catch (Exception error){
            ErrorHandler.logError(error, "getCachedPropertyKey", "Failed to retrieve cached property key");
            throw error;
        }
    }

    public static <ConversionType> Optional<ConversionType> getPropertyKeyFromCache(String aliasName, String propertyKey, Class<ConversionType> type) {
        try {
            return getConfiguration(aliasName).getProperty(propertyKey, type);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getCachedPropertyKey", "Failed to retrieve cached property key");
            return Optional.empty();
        }
    }


    private void loadProperties() throws IOException {
        if (!Files.exists(Path.of(propertiesFilePath))) {
            logger.error("Properties file not found: '{}'", propertiesFilePath);
            throw new FileNotFoundException("Properties file not found: " + propertiesFilePath);
        }

        try (FileInputStream inputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(inputStream);
        } catch (IOException error) {
            ErrorHandler.logError(error, "loadProperties",
                    "Failed to load properties file");
            throw error;
        }
    }

    /**
     * Get a property with type conversion
     * @param propertyKey Property key
     * @param type Desired return type
     * @return Optional containing the converted value
     */
    public <ConversionType> Optional<ConversionType> getProperty(String propertyKey, Class<ConversionType> type) {
        try {
            // Check system properties first
            String systemValue = System.getProperty(propertyKey);
            String value = systemValue != null ? systemValue : properties.getProperty(propertyKey);

            if (value == null || value.isEmpty()) {
                logger.warn("Property '{}' not found in properties file", propertyKey);
                return Optional.empty();
            }

            // Type conversion
            ConversionType result = getConversionType(type, value);
            return Optional.of(result);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getProperty", "Failed to retrieve or convert property");
            return Optional.empty();
        }
    }

    private <ConversionType> ConversionType getConversionType(Class<ConversionType> type, String value) {
        try {
            Object convertedValue = switch (type.getSimpleName()) {
                case "String" -> value;
                case "Integer" -> Integer.parseInt(value);
                case "Boolean" -> Boolean.parseBoolean(value);
                case "Double" -> Double.parseDouble(value);
                case "Long" -> Long.parseLong(value);
                default -> throw new UnsupportedOperationException("Unsupported type conversion");
            };

            @SuppressWarnings("unchecked")
            ConversionType result = (ConversionType) convertedValue;
            return result;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getT", "Failed to convert property");
            throw error;
        }
    }

    /**
     * Check if a configuration is loaded
     * @param configAlias Configuration alias to check
     * @return true if configuration is loaded, false otherwise
     */
    public static boolean isConfigurationLoaded(String configAlias) {
        try {
            return propertyConfigurationCache.containsKey(configAlias);
        } catch (Exception error) {
            ErrorHandler.logError(error, "isConfigurationLoaded", "Failed to check if configuration is loaded");
            throw error;
        }
    }

    /**
     * Reload an existing configuration
     * @param configAlias Configuration alias to reload
     * @throws IllegalStateException if configuration not previously loaded
     */
    public static synchronized void reloadConfiguration(String configAlias) {
        try {
            PropertiesConfigManager existingConfig = propertyConfigurationCache.get(configAlias);
            if (existingConfig == null) {
                throw new IllegalStateException("Configuration '" + configAlias + "' not found. Load it first.");
            }

            // Remove and reload
            propertyConfigurationCache.remove(configAlias);
            loadConfiguration(configAlias, existingConfig.propertiesFilePath);
        } catch (Exception error) {
            ErrorHandler.logError(error, "reloadConfiguration", "Failed to reload configuration");
            throw error;
        }
    }

    /**
     * Get all loaded configuration aliases
     * @return Set of loaded configuration aliases
     */
    public static Set<String> getLoadedConfigurationAliases() {
        try {
            return Collections.unmodifiableSet(propertyConfigurationCache.keySet());
        } catch (Exception error) {
            ErrorHandler.logError(error, "getLoadedConfigurationAliases", "Failed to retrieve loaded configuration aliases");
            throw error;
        }
    }

    private void validateFilePath(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            logger.warn("Invalid file path detected: Path is null or empty.");
            throw new IllegalArgumentException("Configuration file path cannot be null or empty");
        }
    }
}
