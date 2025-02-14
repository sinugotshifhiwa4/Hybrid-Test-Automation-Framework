package com.hybridframework.tests.encryption;

import com.hybridframework.config.environments.EnvironmentFilePaths;
import com.hybridframework.config.environments.EnvironmentSecretKey;
import com.hybridframework.crypto.services.SecureKeyGenerator;
import com.hybridframework.crypto.utils.EnvironmentCryptoManager;
import com.hybridframework.utils.Base64Utils;
import com.hybridframework.utils.logging.ErrorHandler;
import com.hybridframework.utils.logging.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import javax.crypto.SecretKey;
import java.io.IOException;

public class KeyGeneratorTest {
    private static final Logger logger = LoggerUtils.getLogger(KeyGeneratorTest.class);

    // Run test in terminal that generates and encrypt credentials: mvn clean test -Denv=crypto -DskipBrowserSetup=true
    @Test(groups = {"encryption"}, priority = 1)
    public void generateSecretKey() throws IOException {
        try{
            // Generate a new secret key
            SecretKey generatedSecretKey = SecureKeyGenerator.generateSecretKey();

            // Save the secret key in the base environment
            EnvironmentCryptoManager.saveSecretKeyInBaseEnvironment(
                    EnvironmentFilePaths.BASE.getEnvironmentFileFullPath(),
                    EnvironmentSecretKey.UAT.getKeyName(),
                    Base64Utils.encodeSecretKey(generatedSecretKey)
            );
            logger.info("Secret key generated and Saved successfully");
        } catch (IOException error){
            ErrorHandler.logError(error, "generateSecretKey", "Failed to generate secret key");
            throw error;
        }
    }
}
