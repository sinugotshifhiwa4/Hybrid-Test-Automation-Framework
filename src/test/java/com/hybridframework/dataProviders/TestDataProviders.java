package com.hybridframework.dataProviders;

import com.hybridframework.config.properties.PropertiesConfigManager;
import com.hybridframework.config.properties.PropertiesFileAlias;
import com.hybridframework.utils.logging.ErrorHandler;
import org.testng.annotations.DataProvider;

public class TestDataProviders {

    private static final String ORANGE_HRM_DATA = "ORANGE_HRM_DATA";
    private static final String SHEET_NAME = "Employee";
    private static final String ORANGE_HRM_TEST_NAME = "addNewEmployee";


    public static void setOrangeHrmData(){
        try{
            DataProviderConfig.addTestDataMapping(ORANGE_HRM_TEST_NAME, ORANGE_HRM_DATA, SHEET_NAME);
        } catch (Exception error){
            ErrorHandler.logError(error, "setOrangeHrmData", "Failed to add orange hrm data");
            throw error;
        }
    }

    @DataProvider(name = "OrangeHrm")
    public static Object[] provideSingleCustomTestData() {
        return DataProviderConfig.getTestDataByIndex(
                PropertiesConfigManager.getPropertyKeyFromCache(
                        PropertiesFileAlias.GLOBAL.getConfigurationAlias(),
                        ORANGE_HRM_DATA
                ), SHEET_NAME, 0);
    }
}