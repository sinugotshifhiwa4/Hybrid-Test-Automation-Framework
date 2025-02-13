package com.hybridframework.tests.ui.orangeHrm;

import com.hybridframework.config.environments.EnvironmentFileAlias;
import com.hybridframework.config.environments.EnvironmentSecretKey;
import com.hybridframework.crypto.utils.EnvironmentCryptoManager;
import com.hybridframework.tests.base.TestBase;
import com.hybridframework.utils.TestRetryAnalyzer;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.util.List;

public class LoginTests extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(LoginTests.class);
    private static final String INVALID_USERNAME = "User8958";
    private static final String INVALID_PASSWORD = "password123";

    @Test(retryAnalyzer = TestRetryAnalyzer.class)
    public void loginWithValidCredentials() {
        try {
            loginPage.loginToPortal(decryptCredentials().get(0), decryptCredentials().get(1));
            loginPage.isLoginErrorMessageNotVisible();
            loginPage.captureScreenshot("ValidLogin");
            logger.info("Login successful");
        } catch (Exception error) {
            ErrorHandler.logError(error, "loginWithValidCredentials", "Failed to login to portal");
            throw error;
        }
    }

    @Test(retryAnalyzer = TestRetryAnalyzer.class)
    public void loginWithInvalidCredentials() {
        try {
            loginPage.loginToPortal(INVALID_USERNAME, INVALID_PASSWORD);
            loginPage.isLoginErrorMessageVisible();
            loginPage.captureScreenshot("InvalidLogin");
            logger.info("Login Failed as expected");
        } catch (Exception error) {
            ErrorHandler.logError(error, "loginWithInvalidCredentials", "Failed to login to portal");
            throw error;
        }
    }
}
