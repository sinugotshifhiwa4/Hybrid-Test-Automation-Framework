package com.hybridframework.tests.unit.json;

import com.hybridframework.utils.jacksonUtils.JsonDataReader;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.util.Optional;

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

            Optional<String> username = jsonDataReader.getData(CREDENTIALS, "Username", String.class);
            Optional<String> password = jsonDataReader.getData(CREDENTIALS, "Password", String.class);

            username.ifPresent(value -> logger.info("Username: {}", value));
            password.ifPresent(value -> logger.info("Password: {}", value));

        } catch (Exception error) {
            ErrorHandler.logError(error, "testLoadJsonData", "Failed to load json data");
            throw error;
        }
    }

    @Test
    public void testLoadUserJsonData() {
        try {
            jsonDataReader = new JsonDataReader(USER_PATH);

            Optional<String> firstName = jsonDataReader.getData(USER, "FirstName", String.class);
            Optional<String> lastName = jsonDataReader.getData(USER, "LastName", String.class);
            Optional<String> contact = jsonDataReader.getData(USER, "Contact", String.class);
            Optional<Integer> postalCode = jsonDataReader.getData(USER, "PostalCode", Integer.class);
            Optional<Boolean> isRentPaid = jsonDataReader.getData(USER, "IsRentPaid", Boolean.class);

            firstName.ifPresent(value -> logger.info("First Name: {}", value));
            lastName.ifPresent(value -> logger.info("Last Name: {}", value));
            contact.ifPresent(value -> logger.info("Contact: {}", value));
            postalCode.ifPresent(value -> logger.info("PostalCode: {}", value));
            isRentPaid.ifPresent(value -> logger.info("isRentPaid: {}", value));

        } catch (Exception error) {
            ErrorHandler.logError(error, "testLoadJsonData", "Failed to load json data");
            throw error;
        }
    }

}
