package com.hybridframework.tests.unit.json;

import com.hybridframework.utils.jacksonUtils.JsonDataReader;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class LoadJsonDataTests {

    private static final Logger logger = LoggerUtils.getLogger(LoadJsonDataTests.class);
    private static final String CREDENTIALS_PATH = "src/test/resources/testData/Credentials.json";
    private static final String USER_PATH = "src/test/resources/testData/User.json";
    private static final String CREDENTIALS = "Credentials";
    private static final String USER = "User";
    private JsonDataReader jsonDataReader;

    @Test
    public void testLoadLoginJsonData() {
        try {
            jsonDataReader = new JsonDataReader(CREDENTIALS_PATH);

            String username = jsonDataReader.getString(CREDENTIALS, "Username");
            String password = jsonDataReader.getString(CREDENTIALS, "Password");

            logger.info("Username: {}", username);
            logger.info("Password: {}", password);

        } catch (Exception error) {
            ErrorHandler.logError(error, "testLoadJsonData", "Failed to load json data");
            throw error;
        }
    }

    @Test
    public void testLoadUserJsonData() {
        try {
            jsonDataReader = new JsonDataReader(USER_PATH);

            String firstName = jsonDataReader.getString(USER, "FirstName");
            String lastName = jsonDataReader.getString(USER, "LastName");
            String contact = jsonDataReader.getString(USER, "Contact");
            int postalCode = jsonDataReader.getInt(USER, "PostalCode");
            Boolean isRentPaid = jsonDataReader.getBoolean(USER, "IsRentPaid");

            logger.info("First Name: {}", firstName);
            logger.info("Last Name: {}", lastName);
            logger.info("Contact: {}", contact);
            logger.info("PostalCode: {}", postalCode);
            logger.info("isRentPaid: {}", isRentPaid);

        } catch (Exception error) {
            ErrorHandler.logError(error, "testLoadJsonData", "Failed to load json data");
            throw error;
        }
    }
}
