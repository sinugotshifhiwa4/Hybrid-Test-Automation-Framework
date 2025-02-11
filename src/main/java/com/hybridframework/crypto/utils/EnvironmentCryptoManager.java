package com.hybridframework.crypto.utils;

import com.hybridframework.config.environments.EnvironmentConfigManager;
import com.hybridframework.config.environments.EnvironmentFileAlias;
import com.hybridframework.config.environments.EnvironmentFilePaths;
import com.hybridframework.crypto.services.CryptoService;
import com.hybridframework.utils.FileUtils;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.CryptoException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EnvironmentCryptoManager {

    private static final Logger logger = LoggerUtils.getLogger(EnvironmentCryptoManager.class);

    private EnvironmentCryptoManager() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void encryptEnvironmentVariables(
            String filePath,
            String aliasName,
            String environmentSecretKeyType,
            String... envVariables
    ) throws CryptoException {
        try {
            for (String envVariable : envVariables) {
                encryptEnvironmentVariables(filePath, aliasName, environmentSecretKeyType, envVariable);
            }
        } catch (Exception error) {
            ErrorHandler.logError(error, "encryptEnvironmentVariables", "Failed to encrypt multiple variables");
            throw error;
        }
    }

    public static void encryptEnvironmentVariables(
            String filePath,
            String aliasName,
            String environmentSecretKeyType,
            String envVariable
    ) throws CryptoException {
        try {
            String encryptedValue = encryptValue(environmentSecretKeyType, getEnvironmentVariable(aliasName, envVariable));
            updateEnvironmentVariable(filePath, envVariable, encryptedValue);
            logger.info("Variable '{}' encrypted successfully.", envVariable);

        } catch (Exception error) {
            ErrorHandler.logError(error, "encryptEnvironmentVariables", "Failed to encrypt variable: " + envVariable);
            throw error;
        }
    }

    private static String getEnvironmentVariable(String aliasName, String envVariable) {
        try{
            String envValue = EnvironmentConfigManager.getEnvironmentKeyFromCache(aliasName, envVariable);
            if (envValue == null) {
                throw new IllegalArgumentException("Environment variable '" + envVariable + "' is null");
            }
            return envValue;
        } catch (Exception error) {
            ErrorHandler.logError(error, "getEnvironmentVariable", "Failed to get environment variable: " + envVariable);
            throw error;
        }
    }

    private static String encryptValue(String environmentSecretKeyType, String envValue) throws CryptoException {
        try {
            String encryptedValue = CryptoService.encrypt(getSecretKey(EnvironmentFileAlias.BASE.getEnvironmentAlias(), environmentSecretKeyType), envValue);
            if (encryptedValue == null) {
                throw new IllegalArgumentException("Failed to encrypt value for variable: " + envValue);
            }
            return encryptedValue;
        } catch (Exception error) {
            ErrorHandler.logError(error, "encryptValue", "Failed to encrypt value for variable: " + envValue);
            throw error;
        }
    }

    public static void saveSecretKeyInBaseEnvironment(
            String baseEnvironmentFilePath,
            String secretKeyVariable,
            String encodedSecretKey
    ) throws IOException {
        try {
            ensureBaseEnvironmentFileExists();
            updateEnvironmentVariable(baseEnvironmentFilePath, secretKeyVariable, encodedSecretKey);
            logger.info("Secret key saved for variable '{}'", secretKeyVariable);
        } catch (Exception error) {
            ErrorHandler.logError(error, "saveSecretKeyInBaseEnvironment", "Failed to save secret key in base environment");
            throw error;
        }
    }

    private static void ensureBaseEnvironmentFileExists() throws IOException {
        try {
            FileUtils.createDirIfNotExists(EnvironmentFilePaths.getDirectoryPath());
            FileUtils.createFileIfNotExists(
                    EnvironmentFilePaths.getDirectoryPath(),
                    EnvironmentFilePaths.BASE.getEnvironmentFilename()
            );
        } catch (IOException error) {
            ErrorHandler.logError(error, "ensureEnvironmentFileExists", "Failed to ensure environment file exists");
            throw error;
        }
    }

    private static void updateEnvironmentVariable(String filePath, String envVariable, String value) {
        try {
            Path path = Paths.get(filePath);
            List<String> updatedLines = updateEnvironmentLines(Files.readAllLines(path), envVariable, value);

            Files.write(path, updatedLines);
            logger.info("Environment variable '{}' updated in {}", envVariable, filePath);
        } catch (IOException error) {
            ErrorHandler.logError(error, "updateEnvironmentVariable", "Failed to update environment variable: " + envVariable);
            throw new RuntimeException(error);
        }
    }

    private static List<String> updateEnvironmentLines(
            List<String> existingLines,
            String envVariable,
            String value
    ) {
        try {
            boolean[] isUpdated = {false};

            List<String> updatedLines = existingLines.stream()
                    .map(line -> {
                        if (line.startsWith(envVariable + "=")) {
                            isUpdated[0] = true;
                            return envVariable + "=" + value;
                        }
                        return line;
                    })
                    .collect(Collectors.toList());

            if (!isUpdated[0]) {
                updatedLines.add(envVariable + "=" + value);
            }

            return updatedLines;
        } catch (Exception error) {
            ErrorHandler.logError(error, "updateEnvironmentLines", "Failed to update environment lines");
            throw new RuntimeException(error);
        }
    }

    public static List<String> decryptEnvironmentVariables(
            String aliasName,
            String environmentSecretKeyType,
            String... requiredKeys
    ) {
        if (requiredKeys == null || requiredKeys.length == 0) {
            return Collections.emptyList();
        }

        try {
            return Arrays.stream(requiredKeys)
                    .map(key -> decryptSingleKey(aliasName, getSecretKey(EnvironmentFileAlias.BASE.getEnvironmentAlias(), environmentSecretKeyType), key))
                    .collect(Collectors.toList());
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public static String decryptEnvironmentVariable(
            String aliasName,
            String environmentSecretKeyType,
            String requiredKey
    ) {
        try {
            return decryptSingleKey(aliasName, getSecretKey(EnvironmentFileAlias.BASE.getEnvironmentAlias(), environmentSecretKeyType), requiredKey);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public static SecretKey getSecretKey(String aliasName, String environmentSecretKeyType) {
        try {
            return EnvironmentConfigManager.getSecretKeyFromCache(aliasName, environmentSecretKeyType);
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    private static String decryptSingleKey(String aliasName, SecretKey secretKey, String key) {
        try {
            String encryptedValue = EnvironmentConfigManager.getEnvironmentKeyFromCache(aliasName, key);
            return CryptoService.decrypt(secretKey, encryptedValue);
        } catch (CryptoException error) {
            ErrorHandler.logError(error, "decryptKeys", "Failed to decrypt key: " + key);
            throw new RuntimeException(error);
        }
    }
}
