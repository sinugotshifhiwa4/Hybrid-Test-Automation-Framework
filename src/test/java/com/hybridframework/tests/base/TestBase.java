package com.hybridframework.tests.base;

import com.hybridframework.config.environments.EnvironmentFileAlias;
import com.hybridframework.config.environments.EnvironmentSecretKey;
import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.crypto.utils.EnvironmentCryptoManager;
import com.hybridframework.drivers.BrowserFactory;
import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.ui.pages.base.BasePage;
import com.hybridframework.testDataStorage.TestContextStore;
import com.hybridframework.ui.pages.orangeHrmPages.LoginPage;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.List;

import static com.hybridframework.tests.base.ConfigurationManager.initializeConfigurations;
import static com.hybridframework.tests.base.ConfigurationManager.initializeTestConfig;

public class TestBase extends BasePage{

    private static final Logger logger = LoggerUtils.getLogger(TestBase.class);
    private final DriverFactory driverFactory = DriverFactory.getInstance();
    protected BrowserFactory browserFactory;
    private static final String DEMO_TEST_ID_ONE = "TEST_ONE";
    private static final String BROWSER = "CHROME_BROWSER";
    private static final String URL = "PORTAL_BASE_URL";

    // Pages
    protected LoginPage loginPage;

    @BeforeMethod(alwaysRun = true)
    public void setup() {
        try {
            // Load essential configurations first
            initializeConfigurations();
            initializeTestConfig(DEMO_TEST_ID_ONE);

            // Only initialize browser-related components if not skipped
            if (!Boolean.getBoolean("skipBrowserSetup")) {
                initializeBrowserComponents();
            } else {
                logger.info("Skipping browser setup for encryption tests.");
            }

            logger.info("Setup configured successfully");
        } catch (Exception error) {
            ErrorHandler.logError(error, "setup", "Failed to initialize setup");
            throw error;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(){
        try{
            TestContextStore.cleanupTestContext(DEMO_TEST_ID_ONE);

            logger.info("Test teardown completed successfully.");
        } catch (Exception error){
            ErrorHandler.logError(error, "tearDown", "Failed to  tear down");
            throw error;
        } finally {
            driverFactory.quitDriver();
        }
    }

    private void initializeBrowserComponents() {
        try {
            browserFactory = new BrowserFactory();

            // Retrieve browser name for this thread
            String browser = PropertiesConfigManager.getPropertyKeyFromCache(
                    PropertiesFileAlias.GLOBAL.getConfigurationAlias(),
                    BROWSER
            );

            // Initialize the browser for the current test thread
            browserFactory.initializeBrowser(browser);

            // Ensure WebDriver is set before proceeding
            if (!driverFactory.hasDriver()) {
                logger.error("Driver could not be initialized for thread name'{}', and thread id '{}'", Thread.currentThread().getName(), Thread.currentThread().threadId());
                throw new IllegalStateException("WebDriver initialization failed for thread: "
                        + Thread.currentThread().threadId());
            }

            loginPage = new LoginPage(driverFactory.getDriver());

            // Navigate to URL
            String url = PropertiesConfigManager.getPropertyKeyFromCache(
                    PropertiesFileAlias.UAT.getConfigurationAlias(),
                    URL
            );
            driverFactory.navigateToUrl(url);
            loginPage.isCompanyLogoPresent();
        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeBrowserComponents", "Failed to initialize browser components");
            throw error;
        }
    }


    public List<String> decryptCredentials() {
        try {
            return EnvironmentCryptoManager.decryptEnvironmentVariables(
                    EnvironmentFileAlias.UAT.getEnvironmentAlias(),
                    EnvironmentSecretKey.UAT.getKeyName(),
                    "PORTAL_USERNAME", "PORTAL_PASSWORD"
            );
        } catch (Exception error) {
            ErrorHandler.logError(error, "decryptCredentials", "Failed to decrypt credentials");
            throw error;
        }
    }
}
