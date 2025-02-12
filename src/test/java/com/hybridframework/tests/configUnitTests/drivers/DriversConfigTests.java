package com.hybridframework.tests.configUnitTests.drivers;

import com.hybridframework.drivers.BrowserFactory;
import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DriversConfigTests {

    private static final Logger logger = LoggerUtils.getLogger(DriversConfigTests.class);
    private final BrowserFactory browserFactory = new BrowserFactory();
    private final DriverFactory driverFactory = DriverFactory.getInstance();
    private static final String URL = "https://www.google.com";

    @DataProvider(name = "browserData")
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

    @Test(dataProvider = "browserData")
    public void testBrowserInitialization(String browser, String... args) {
        try {
            logger.info("Initializing {} browser with arguments: {}", browser, args);
            browserFactory.initializeBrowser(browser, args);
            driverFactory.navigateToUrl(URL);
            logger.info("{} browser opened successfully.", browser);
        } catch (Exception error) {
            ErrorHandler.logError(error, "testBrowserInitialization", "Failed to initialize " + browser);
            throw error;
        } finally {
            driverFactory.quitDriver();
        }
    }
}
