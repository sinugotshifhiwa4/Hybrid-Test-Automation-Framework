package com.hybridframework.tests.base;

import com.hybridframework.testDataStorage.TestContextStore;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import static com.hybridframework.tests.base.BaseUtils.*;

public class TestBase {

    private static final Logger logger = LoggerUtils.getLogger(TestBase.class);
    private static final String DEMO_TEST_ID_ONE = "TEST_ONE";

    @BeforeClass(alwaysRun = true)
    public void setup(){
        try{
            // Load configs
            loadConfigurations();

            // initialize TestContextStore
            initializeTestDataContext(DEMO_TEST_ID_ONE); // Test Ids to be used for storage during execution

            // initialize Json Mapper
            initializeJsonMapper();

            logger.info("Setup configured successfully");
        } catch (Exception error){
            ErrorHandler.logError(error, "setup", "Failed to initialize setup");
            throw error;
        }
    }

    @AfterClass
    public void tearDown(){
        try{
            TestContextStore.cleanupTestContext(DEMO_TEST_ID_ONE);

            logger.info("Test teardown completed successfully.");
        } catch (Exception error){
            ErrorHandler.logError(error, "tearDown", "Failed to  tear down");
            throw error;
        }
    }
}
