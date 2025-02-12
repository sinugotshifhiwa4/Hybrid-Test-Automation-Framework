package com.hybridframework.tests.base;

import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.drivers.BrowserFactory;
import com.hybridframework.drivers.DriverFactory;
import com.hybridframework.ui.pages.base.BasePage;
import com.hybridframework.testDataStorage.TestContextStore;
import com.hybridframework.ui.pages.orangeHrmPages.LoginPage;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static com.hybridframework.tests.base.BaseUtils.*;

public class TestBase extends BasePage{

    private static final Logger logger = LoggerUtils.getLogger(TestBase.class);
    private final DriverFactory driverFactory = DriverFactory.getInstance();
    protected BrowserFactory browserFactory;
    private static final String DEMO_TEST_ID_ONE = "TEST_ONE";
    private static final String BROWSER = "CHROME_BROWSER";
    private static final String URL = "PORTAL_BASE_URL";

    // Pages
    protected LoginPage loginPage;

    @BeforeClass(alwaysRun = true)
    public void setup(){
        try{
            // Load configs
            loadConfigurations();

            // Initialize browser/driver
            browserFactory = new BrowserFactory();
            browserFactory.initializeBrowser(
                    PropertiesConfigManager.getPropertyKeyFromCache(
                            PropertiesFileAlias.GLOBAL.getConfigurationAlias(),
                            BROWSER)
            );

            // initialize TestContextStore
            initializeTestDataContext(DEMO_TEST_ID_ONE); // Test Ids to be used for storage during execution

            // initialize Json Mapper
            initializeJsonMapper();

            // Initialize Pages
            loginPage = new LoginPage(driverFactory.getDriver());

            // Navigate to Url
            driverFactory.navigateToUrl(PropertiesConfigManager.getPropertyKeyFromCache(
                    PropertiesFileAlias.UAT.getConfigurationAlias(),
                    URL
            ));
            loginPage.isCompanyLogoPresent();


            logger.info("Setup configured successfully");
        } catch (Exception error){
            ErrorHandler.logError(error, "setup", "Failed to initialize setup");
            throw error;
        }
    }

    @AfterClass(alwaysRun = true)
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
}
