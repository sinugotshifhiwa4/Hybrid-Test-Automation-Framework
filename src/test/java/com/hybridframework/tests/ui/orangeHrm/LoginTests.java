package com.hybridframework.tests.ui.orangeHrm;

import com.hybridframework.tests.base.TestBase;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

public class LoginTests extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(LoginTests.class);

    @Test
    public void loginWithValidCredentials() {
        try{
            loginPage.loginToPortal("Admin", "admin123");
            loginPage.isLoginErrorMessageNotVisible();
            loginPage.captureScreenshot("ValidLogin");
            logger.info("Login successful");
        } catch (Exception error){
            ErrorHandler.logError(error, "loginWithValidCredentials", "Failed to login to portal");
            throw error;
        }
    }

    @Test
    public void loginWithInvalidCredentials(){
        try{
            loginPage.loginToPortal("Admin", "admin1234");
            loginPage.isLoginErrorMessageVisible();
            loginPage.captureScreenshot("InvalidLogin");
            logger.info("Login Failed as expected");
        } catch (Exception error){
            ErrorHandler.logError(error, "loginWithInvalidCredentials", "Failed to login to portal");
            throw error;
        }
    }
}
