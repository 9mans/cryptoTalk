package com.example.cryptotalk.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 32;
    private static final String IV = "1234567890123456";

    private final SecretKey secretKey;
    private final IvParameterSpec ivSpec;

    public AESUtil(String base64key) {

        byte[] decodedKey = Base64.getDecoder().decode(base64key);
        if (decodedKey.length != AES_KEY_SIZE) {
            throw new IllegalArgumentException("invalid AES key length: " + decodedKey.length);
        }

        this.secretKey = new SecretKeySpec(decodedKey, "AES");
        this.ivSpec = new IvParameterSpec(IV.getBytes());
    }

    public String encrypt(String data) throws Exception {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedData) throws Exception {

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(original);
    }
}
