package com.hybridframework.utils;

import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.tests.base.TestBase;
import com.hybridframework.utils.logging.ErrorHandler;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class TestRetryAnalyzer implements IRetryAnalyzer {

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

                // Quit and restart the WebDriver for a clean retry
                DriverFactory.getInstance().quitDriver();

                Object testInstance = result.getInstance();
                if (testInstance instanceof TestBase) {
                    ((TestBase) testInstance).setup(); // Restart the browser
                }

                return true;
            }
            return false;
        } catch (Exception error) {
            ErrorHandler.logError(error, "retry", "Failed to retry test execution");
            throw error;
        }
    }
}
