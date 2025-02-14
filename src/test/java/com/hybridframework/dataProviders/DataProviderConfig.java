package com.hybridframework.dataProviders;

import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.utils.excelUtils.ExcelConfigManager;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataProviderConfig {

    private static final Logger logger = LoggerUtils.getLogger(DataProviderConfig.class);
    private static final String TEST_DATA_PATH = "TEST_DATA_PATH";

    /**
     * Maps test methods to their corresponding test data files and sheets
     */
    private static final Map<String, TestDataConfig> TEST_DATA_MAPPING = new HashMap<>();

    /**
     * Method to add test data mapping
     * @param testMethodName The name of the test method
     * @param testFileName The name of the Excel file
     * @param sheetName The name of the sheet in Excel file
     */
    public static void addTestDataMapping(String testMethodName, String testFileName, String sheetName) {
        TEST_DATA_MAPPING.put(testMethodName, new TestDataConfig(testFileName, sheetName));
        logger.info("Added test data mapping for method: {}, file: {}, sheet: {}",
                testMethodName, testFileName, sheetName);
    }

    /**
     * Method to remove test data mapping
     * @param testMethodName The name of the test method to remove mapping for
     */
    public static void removeTestDataMapping(String testMethodName) {
        TEST_DATA_MAPPING.remove(testMethodName);
        logger.info("Removed test data mapping for method: {}", testMethodName);
    }

    /**
     * Method to clear all test data mappings
     */
    public static void clearTestDataMappings() {
        TEST_DATA_MAPPING.clear();
        logger.info("Cleared all test data mappings");
    }

    // Rest of the class remains the same...
    @DataProvider(name = "genericDataProvider")
    public static Object[][] getTestData(Method method) {
        String methodName = method.getName();
        logger.info("Fetching test data for method: {}", methodName);

        TestDataConfig config = TEST_DATA_MAPPING.get(methodName);
        if (config == null) {
            throw new IllegalArgumentException("No test data configuration found for method: " + methodName);
        }

        return loadTestData(config.getFileName(), config.getSheetName());
    }

    @DataProvider(name = "customDataProvider")
    public static Object[][] getTestData(String fileName, String sheetName) {
        logger.info("Fetching test data from file: {}, sheet: {}", fileName, sheetName);
        return loadTestData(fileName, sheetName);
    }

    private static Object[][] loadTestData(String fileName, String sheetName) {
        try {
            List<Map<String, String>> testData = ExcelConfigManager.loadExcelDataAsList(
                    PropertiesConfigManager.getPropertyKeyFromCache(
                            PropertiesFileAlias.GLOBAL.getConfigurationAlias(),
                            TEST_DATA_PATH
                    ) + fileName, sheetName
            );

            Object[][] data = new Object[testData.size()][1];
            for (int i = 0; i < testData.size(); i++) {
                data[i][0] = testData.get(i);
            }
            return data;
        } catch (Exception error) {
            ErrorHandler.logError(error, "loadTestData", "Failed to load test data from file: " + fileName);
            throw new RuntimeException("Failed to load test data", error);
        }
    }

    public static Object[] getTestDataByIndex(String fileName, String sheetName, int index) {
        try {
            Object[][] allData = DataProviderConfig.getTestData(fileName, sheetName);
            if (index < 0 || index >= allData.length) {
                logger.error("Index out of bounds: {}", index);
                throw new IndexOutOfBoundsException("Invalid index: " + index);
            }
            return new Object[]{allData[index][0]};
        } catch (Exception error) {
            ErrorHandler.logError(error, "loadTestData", "Failed to load test data from file: " + fileName);
            throw new RuntimeException("Failed to load test data", error);
        }
    }
}