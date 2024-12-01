package com.isuru.hettiarachchi.utils;



import android.annotation.SuppressLint;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtil {

    // AES encryption algorithm
    private static final String ALGORITHM = "AES";

    // Decrypt the data using AES
    public static String decrypt(String encryptedData, String key) throws Exception {
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        @SuppressLint({"NewApi", "LocalSuppress"}) byte[] decodedData = Base64.getDecoder().decode(encryptedData); // Decode the base64 encoded string
        byte[] decryptedData = cipher.doFinal(decodedData);

        return new String(decryptedData); // Return decrypted data as string
    }

}

