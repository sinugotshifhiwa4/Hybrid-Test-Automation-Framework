package com.hybridframework.crypto.services;

import com.hybridframework.crypto.utils.CryptoConstants;
import com.hybridframework.utils.Base64Utils;
import com.hybridframework.utils.logging.ErrorHandler;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static com.hybridframework.crypto.services.SecureKeyGenerator.generateSalt;

public class CryptoService {

    private CryptoService() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    private record EncryptionComponents(byte[] salt, byte[] iv, byte[] cipherText) {
        public byte[] combine() {
            return ByteBuffer.allocate(salt.length + iv.length + cipherText.length)
                    .put(salt)
                    .put(iv)
                    .put(cipherText)
                    .array();
        }

        public static EncryptionComponents extract(byte[] combined) {
            int saltSize = CryptoConstants.SALT_KEY_SIZE.getIntValue();
            int ivSize = CryptoConstants.IV_KEY_SIZE.getIntValue();
            int cipherTextSize = combined.length - saltSize - ivSize;

            if (combined.length < saltSize + ivSize) {
                throw new IllegalArgumentException("Combined byte array is too short.");
            }

            ByteBuffer buffer = ByteBuffer.wrap(combined);
            byte[] salt = new byte[saltSize];
            byte[] iv = new byte[ivSize];
            byte[] cipherText = new byte[cipherTextSize];

            buffer.get(salt);
            buffer.get(iv);
            buffer.get(cipherText);

            return new EncryptionComponents(salt, iv, cipherText);
        }
    }

    private static SecretKeySpec getDerivedSecretKey(String secret, byte[] salt) {
        return deriveKey(secret, salt);
    }


    public static String encrypt(SecretKey key, String data) throws CryptoException {
        validateInput(key, "Secret Key");
        validateInput(data, "Data");

        try {
            byte[] salt = generateSalt();
            byte[] iv = new byte[CryptoConstants.IV_KEY_SIZE.getIntValue()];
            new SecureRandom().nextBytes(iv);

            // Properly encode the key
            String keyString = Base64Utils.encodeArray(key.getEncoded());
            SecretKeySpec derivedKey = deriveKey(keyString, salt);

            Cipher cipher = initializeCipher(iv, derivedKey, Cipher.ENCRYPT_MODE);
            byte[] cipherText = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            EncryptionComponents components = new EncryptionComponents(salt, iv, cipherText);
            return Base64Utils.encodeArray(components.combine());
        } catch (Exception error) {
            ErrorHandler.logError(error, "encrypt", "Failed to encrypt data");
            throw new CryptoException("Encryption failed", error);
        }
    }



    public static String decrypt(SecretKey key, String encryptedData) throws CryptoException {
        validateInput(key, "Secret Key");
        validateInput(encryptedData, "Encrypted Data");

        try {
            byte[] combined = Base64Utils.decodeToArray(encryptedData);
            EncryptionComponents components = EncryptionComponents.extract(combined);

            // Properly encode the key
            String keyString = Base64Utils.encodeArray(key.getEncoded());
            SecretKeySpec derivedKey = deriveKey(keyString, components.salt());

            Cipher cipher = initializeCipher(components.iv(), derivedKey, Cipher.DECRYPT_MODE);
            byte[] decryptedBytes = cipher.doFinal(components.cipherText());

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (AEADBadTagException error) {
            ErrorHandler.logError(error, "decrypt", "Tag mismatch: Incorrect key, IV, or ciphertext corruption.");
            throw new CryptoException("Decryption failed: Tag mismatch. Ensure correct key and IV are used.", error);
        } catch (Exception error) {
            ErrorHandler.logError(error, "decrypt", "Failed to decrypt data");
            throw new CryptoException("Decryption failed", error);
        }
    }


    // Async decryption to avoid blocking Selenium
    public static CompletableFuture<String> decryptAsync(SecretKey key, String encryptedData) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return decrypt(key, encryptedData);
            } catch (CryptoException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static SecretKeySpec deriveKey(String secretKey, byte[] salt) {
        try {
            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                    .withSalt(salt)
                    .withIterations(CryptoConstants.ARGON2_ITERATIONS.getIntValue())
                    .withMemoryAsKB(CryptoConstants.ARGON2_MEMORY.getIntValue())
                    .withParallelism(CryptoConstants.ARGON2_PARALLELISM.getIntValue())
                    .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] result = new byte[CryptoConstants.AES_SECRET_KEY_SIZE.getIntValue()];
            generator.generateBytes(secretKey.getBytes(StandardCharsets.UTF_8), result);

            return new SecretKeySpec(result, CryptoConstants.AES_ALGORITHM.getStringValue());
        } catch (Exception error) {
            ErrorHandler.logError(error, "deriveKey", "Failed to derive key");
            throw new IllegalStateException("Failed to derive key", error);
        } finally {
            Arrays.fill(secretKey.toCharArray(), '\0');
        }
    }

    private static Cipher initializeCipher(byte[] iv, SecretKeySpec key, int mode) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(mode, key, new GCMParameterSpec(CryptoConstants.GCM_TAG_LENGTH.getIntValue(), iv));
            return cipher;
        } catch (Exception error) {
            ErrorHandler.logError(error, "initializeCipher", "Failed to initialize cipher");
            throw error;
        }
    }

    private static void validateInput(Object input, String inputType) {
        if (input == null) {
            throw new IllegalArgumentException(inputType + " cannot be null");
        }
    }
}
