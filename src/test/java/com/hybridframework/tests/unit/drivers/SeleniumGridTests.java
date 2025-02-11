package com.hybridframework.tests.unit.drivers;

import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.drivers.SeleniumGridFactory;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class SeleniumGridTests {

    private static final Logger logger = LoggerUtils.getLogger(DriversConfigTests.class);
    private final SeleniumGridFactory browserFactory = new SeleniumGridFactory();
    private final DriverFactory driverFactory = DriverFactory.getInstance();
    private static final String URL = "https://www.google.com";

    @Test(dataProvider = "remoteBrowserData")
    public void testBrowserInitialization(String browser, String... args) {
        try {
            logger.info("Initializing {} browser with arguments: {}", browser, args);
            browserFactory.initializeRemoteBrowser(browser, args);
            logger.info("{} browser opened successfully.", browser);
        } catch (Exception error) {
            ErrorHandler.logError(error, "testBrowserInitialization", "Failed to initialize " + browser);
            throw error;
        } finally {
            driverFactory.quitDriver();
        }
    }

    @DataProvider(name = "remoteBrowserData")
    public Object[][] browserData() {
        return new Object[][]{
                {"chrome", new String[]{}},
                {"chrome", new String[]{
                        "--headless",
                        "--no-sandbox",
                        "--disable-gpu",
                        "--disable-dev-shm-usage",
                        "--remote-allow-origins=*",
                        "--window-size=1920,1080"
                }},
                {"edge", new String[]{}},
                {"edge", new String[]{
                        "--headless",
                        "--no-sandbox",
                        "--disable-gpu",
                        "--disable-dev-shm-usage",
                        "--remote-allow-origins=*",
                        "--window-size=1920,1080"
                }},
                {"firefox", new String[]{}},
                {"firefox", new String[]{
                        "--headless",
                        "--no-sandbox",
                        "--disable-gpu",
                        "--disable-dev-shm-usage",
                        "--window-size=1920,1080"
                }}
        };

    }
}
