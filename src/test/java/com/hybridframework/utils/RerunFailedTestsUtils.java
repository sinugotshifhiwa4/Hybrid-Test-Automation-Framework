package com.hybridframework.utils;

import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.utils.logging.ErrorHandler;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RerunFailedTestsUtils implements IRetryAnalyzer {

    private static final String MAX_RETRY_COUNT = "MAX_RETRY_COUNT";
    private static final int maxRetryCount = initializeMaxRetryCount();
    private int retryCount = 0;

    private static int initializeMaxRetryCount() {
        try {
            return PropertiesConfigManager
                    .getConfiguration(PropertiesFileAlias.GLOBAL.getConfigurationAlias())
                    .getProperty(MAX_RETRY_COUNT, Integer.class)
                    .orElse(2);
        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeMaxRetryCount",
                    "Failed to retrieve retry count");
            throw error;
        }
    }

    @Override
    public boolean retry(ITestResult result) {
        try {
            if (retryCount < maxRetryCount) {
                retryCount++;
                return true;
            }
            return false;
        } catch (Exception error) {
            ErrorHandler.logError(error, "retry", "Failed to retry count");
            throw error;
        }
    }
}
