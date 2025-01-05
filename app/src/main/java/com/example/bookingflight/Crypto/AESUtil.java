package com.example.bookingflight.Crypto;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class AESUtil {

    private static final int GCM_IV_LENGTH = 12; // IV length for AES-GCM
    private static final int GCM_TAG_LENGTH = 128; // Authentication tag length in bits

    // Encrypt data with AES key
    public static String encrypt(String data, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        byte[] iv = cipher.getIV();
        if (iv == null || iv.length != GCM_IV_LENGTH) {
            throw new IllegalStateException("Invalid IV generated");
        }
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedData.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedData);
        return Base64.encodeToString(byteBuffer.array(), Base64.DEFAULT);
    }

    // Decrypt data with AES key
    public static String decrypt(String encryptedData, SecretKey aesKey) throws Exception {
        byte[] decodedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
        ByteBuffer byteBuffer = ByteBuffer.wrap(decodedBytes);
        byte[] iv = new byte[GCM_IV_LENGTH];
        byteBuffer.get(iv);
        byte[] encryptedMessageBytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedMessageBytes);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, spec);
        byte[] decryptedData = cipher.doFinal(encryptedMessageBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    // Generate a new AES key
    public static SecretKey generateAESKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(keySize);
        return keyGenerator.generateKey();
    }

    public static SecretKey convertStringToAESKey(String keyString) {
        try {
            byte[] decodedKey = Base64.decode(keyString, Base64.NO_WRAP);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        } catch (Exception e) {
            Log.e("AESUtil", "Error converting String to AES Key: " + e.getMessage(), e);
            return null;
        }
    }
}