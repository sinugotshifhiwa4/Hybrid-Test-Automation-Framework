package com.hybridframework.tests.unit.excel;

import com.hybridframework.dataProviders.TestDataProvider;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

public class ExcelConfigTests {

    private static final Logger logger = LoggerUtils.getLogger(ExcelConfigTests.class);

    @Test(dataProvider = "genericDataProvider", dataProviderClass = TestDataProvider.class)
    public void testLoadLoginExcelData(Map<String, String> data) {
        try {
            String username = data.get("Username");
            String password = data.get("Password");

            logger.info("Username: {}", username);
            logger.info("Password: {}", password);

        } catch (Exception error) {
            ErrorHandler.logError(error, "testLoadLoginExcelData", "Failed to load login excel data");
            throw error;
        }
    }

    @Test(dataProvider = "UserDataProvider")
    public void testLoadCustomExcelData(Map<String, String> data) {
        try {
            String firstName = data.get("FirstName");
            String lastName = data.get("LastName");

            logger.info("First Name: {}", firstName);
            logger.info("Last Name: {}", lastName);

        } catch (Exception error) {
            ErrorHandler.logError(error, "testLoadCustomExcelData", "Failed to load custom excel data");
            throw error;
        }
    }

    @Test(dataProvider = "SingleUserDataProvider")
    public void testLoadSingleUserCustomExcelData(Map<String, String> data) {
        try {
            String firstName = data.get("FirstName");
            String lastName = data.get("LastName");

            logger.info("Single User First Name: {}", firstName);
            logger.info("Single User Last Name: {}", lastName);

        } catch (Exception error) {
            ErrorHandler.logError(error, "testLoadSingleUserCustomExcelData", "Failed to load single custom excel data");
            throw error;
        }
    }

    @DataProvider(name = "UserDataProvider")
    public static Object[][] provideCustomTestData() {
        return TestDataProvider.getTestData("UserData.xlsx", "User");
    }

    @DataProvider(name = "SingleUserDataProvider")
    public static Object[] provideSingleCustomTestData() {
        return TestDataProvider.getTestDataByIndex("UserData.xlsx", "User", 1);
    }

}
