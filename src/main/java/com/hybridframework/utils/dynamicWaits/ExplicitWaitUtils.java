package com.hybridframework.utils.dynamicWaits;

import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Optional;

public class ExplicitWaitUtils {

    private static final Logger logger = LoggerUtils.getLogger(ExplicitWaitUtils.class);
    private static final DriverFactory driverFactory = DriverFactory.getInstance();
    private static final String TIMEOUT_KEY = "DEFAULT_GLOBAL_TIMEOUT";

    private ExplicitWaitUtils() {
        throw new AssertionError("Utility class - do not instantiate");
    }

    public static WebDriverWait getWebDriverWait() {
        try {
            Optional<Integer> timeout = getDefaultTimeout();
            return new WebDriverWait(driverFactory.getDriver(), Duration.ofSeconds(timeout.orElse(60)));
        } catch (Exception error) {
            ErrorHandler.logError(error, "getWebDriverWait", "Failed to get WebDriverWait");
            throw error;
        }
    }

    public static void waitForElementToBeVisible(WebElement element) {
        try {
            getWebDriverWait().until(ExpectedConditions.visibilityOf(element));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForElementToBeVisible", "Failed to wait for element to be visible");
            throw error;
        }
    }

    public static void waitForElementToBeClickable(WebElement element) {
        try {
            getWebDriverWait().until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForElementToBeClickable", "Failed to wait for element to be clickable");
            throw error;
        }
    }

    public static void waitForElementNotToBeVisible(WebElement element) {
        try {
            getWebDriverWait().until(ExpectedConditions.invisibilityOf(element));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForElementNotToBeVisible", "Failed to wait for element to be not visible");
            throw error;
        }
    }

    public static void waitForPresenceOfElement(By locator) {
        try {
            getWebDriverWait().until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForPresenceOfElement", "Failed to wait for element presence");
            throw error;
        }
    }

    public static void waitForTextToBePresent(WebElement element, String text) {
        try {
            getWebDriverWait().until(ExpectedConditions.textToBePresentInElement(element, text));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForTextToBePresent", "Failed to wait for text presence");
            throw error;
        }
    }

    public static void waitForAttributeToContain(WebElement element, String attribute, String value) {
        try {
            getWebDriverWait().until(ExpectedConditions.attributeContains(element, attribute, value));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForAttributeToContain", "Failed to wait for attribute change");
            throw error;
        }
    }

    public static void waitForPageTitle(String expectedTitle) {
        try {
            getWebDriverWait().until(ExpectedConditions.titleIs(expectedTitle));
        } catch (Exception error) {
            ErrorHandler.logError(error, "waitForPageTitle", "Failed to wait for page title");
            throw error;
        }
    }

    private static Optional<Integer> getDefaultTimeout() {
        try {
            return PropertiesConfigManager
                    .getConfiguration(PropertiesFileAlias.GLOBAL.getConfigurationAlias())
                    .getProperty(TIMEOUT_KEY, Integer.class);
        } catch (Exception error) {
            ErrorHandler.logError(error, "getTimeout", "Failed to retrieve timeout value");
            return Optional.empty();
        }
    }
}