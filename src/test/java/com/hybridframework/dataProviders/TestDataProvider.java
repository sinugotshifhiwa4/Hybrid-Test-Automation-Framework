package com.hybridframework.dataProviders;

import com.hybridframework.utils.excelUtils.ExcelConfigManager;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class TestDataProvider {

    private static final Logger logger = LoggerUtils.getLogger(TestDataProvider.class);
    private static final String TEST_DATA_PATH = "src/test/resources/testData/";

    /**
     * Maps test methods to their corresponding test data files and sheets
     * This Excel files names are just examples
     */
    private static final Map<String, TestDataConfig> TEST_DATA_MAPPING = Map.of(
            "testLoadLoginExcelData", new TestDataConfig("CredentialsData.xlsx", "Credentials"),
            "testUserDetails", new TestDataConfig("UserData.xlsx", "User")
            // Add more mappings as needed
    );

    /**
     * Generic data provider that can be used for any test method
     * @param method The test method requesting the data
     * @return Object array containing test data
     */
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

    /**
     * Overloaded data provider that allows specifying file and sheet names directly
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return Object array containing test data
     */
    @DataProvider(name = "customDataProvider")
    public static Object[][] getTestData(String fileName, String sheetName) {
        logger.info("Fetching test data from file: {}, sheet: {}", fileName, sheetName);
        return loadTestData(fileName, sheetName);
    }

    /**
     * Loads test data from Excel file
     * @param fileName Excel file name
     * @param sheetName Sheet name
     * @return Object array containing test data
     */
    private static Object[][] loadTestData(String fileName, String sheetName) {
        try {
            List<Map<String, String>> testData = ExcelConfigManager.loadExcelDataAsList(
                    TEST_DATA_PATH + fileName,
                    sheetName
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
            Object[][] allData = TestDataProvider.getTestData(fileName, sheetName);
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
