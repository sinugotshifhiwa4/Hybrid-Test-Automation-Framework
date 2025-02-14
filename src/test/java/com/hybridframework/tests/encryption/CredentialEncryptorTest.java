package com.hybridframework.tests.encryption;

import com.hybridframework.config.environments.EnvironmentFileAlias;
import com.hybridframework.config.environments.EnvironmentFilePaths;
import com.hybridframework.config.environments.EnvironmentSecretKey;
import com.hybridframework.crypto.utils.EnvironmentCryptoManager;
import com.hybridframework.tests.base.TestBase;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.crypto.CryptoException;
import org.testng.annotations.Test;

public class CredentialEncryptorTest extends TestBase {

    private static final Logger logger = LoggerUtils.getLogger(CredentialEncryptorTest.class);
    private static final String USERNAME = "PORTAL_USERNAME";
    private static final String PASSWORD = "PORTAL_PASSWORD";

    @Test(groups = {"encryption"}, priority = 2)
    public void encryptCredentials() throws CryptoException {
        try{
            // Run Encryption
            EnvironmentCryptoManager.encryptEnvironmentVariables(
                    EnvironmentFilePaths.UAT.getEnvironmentFileFullPath(),
                    EnvironmentFileAlias.UAT.getEnvironmentAlias(),
                    EnvironmentSecretKey.UAT.getKeyName(),
                    USERNAME, PASSWORD
            );
            logger.info("Successfully encrypted credentials");
        } catch (Exception error){
            ErrorHandler.logError(error, "encryptCredentials", "Failed to encrypt credentials");
            throw error;
        }
    }
}
