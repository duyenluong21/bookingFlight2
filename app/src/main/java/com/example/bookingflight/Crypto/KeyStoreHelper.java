package com.example.bookingflight.Crypto;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Log;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class KeyStoreHelper {
    private static final String KEYSTORE_ALIAS = "AESKeyAlias";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    @TargetApi(Build.VERSION_CODES.M)
    public static void generateAndStoreRSAKeyPair(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        // Kiểm tra xem private key đã tồn tại chưa
        if (keyStore.containsAlias(alias)) {
            System.out.println("KeyPair already exists for alias: " + alias);
            return;  // Nếu đã có private key thì không cần tạo lại
        }

        // Tạo KeyPairGenerator và cấu hình RSA key pair
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore"
        );
        keyPairGenerator.initialize(
                new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setKeySize(2048)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .build()
        );

        // Tạo và lưu cặp khóa RSA vào Keystore
        keyPairGenerator.generateKeyPair();
        System.out.println("KeyPair generated and stored with alias: " + alias);
    }

    public static PrivateKey getPrivateKey(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (keyStore.containsAlias(alias)) {
            KeyStore.Entry entry = keyStore.getEntry(alias, null);

            if (entry instanceof KeyStore.PrivateKeyEntry) {
                return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
            }
        }
        throw new Exception("Private key not found for alias: " + alias);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static PublicKey getPublicKey(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null); // Load Keystore

        // Kiểm tra xem alias có tồn tại không
        if (keyStore.containsAlias(alias)) {
            KeyStore.Entry entry = keyStore.getEntry(alias, null);

            if (entry instanceof KeyStore.PrivateKeyEntry) {
                return ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
            }
        }

        throw new Exception("Public key not found for alias: " + alias);
    }
    @TargetApi(Build.VERSION_CODES.M)
    public static void savePrivateKeyToKeystore(String alias, PrivateKey privateKey) throws Exception {
        // Lấy Keystore instance
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);  // Khởi tạo keystore

        // Kiểm tra xem alias đã tồn tại chưa, nếu chưa thì lưu private key vào keystore
        if (!keyStore.containsAlias(alias)) {
            KeyStore.PrivateKeyEntry privateKeyEntry = new KeyStore.PrivateKeyEntry(privateKey, null);
            keyStore.setEntry(alias, privateKeyEntry, null);  // Lưu private key vào keystore

            // Kiểm tra lại việc lưu thành công
            if (keyStore.containsAlias(alias)) {
                Log.d("KeyStore", "Lưu private key thành công với alias: " + alias);
            } else {
                Log.e("KeyStore", "Lỗi lưu private key vào Keystore, alias không tồn tại.");
            }
        } else {
            Log.d("KeyStore", "Private key đã tồn tại với alias: " + alias);
        }
    }
    public static boolean isPrivateKeyStored(String maKH) {
        try {
            // Kiểm tra nếu private key tồn tại trong Keystore
            Key privateKey = getPrivateKey(maKH);
            return privateKey != null;
        } catch (Exception e) {
            Log.e("KeyStore", "Lỗi khi kiểm tra private key", e);
            return false;
        }
    }


    // Methods for AES keys
    @TargetApi(Build.VERSION_CODES.M)
    public static void generateAndStoreAESKey(String alias) throws Exception {
        // Tạo KeyGenerator được liên kết với Android KeyStore
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        // Cấu hình KeyGenerator để tạo khóa AES và lưu vào KeyStore
        keyGenerator.init(
                new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .setKeySize(256)
                        .build()
        );

        // Tạo và lưu khóa vào KeyStore
        keyGenerator.generateKey();

        Log.d("KeyStore", "AES Key generated and stored under alias: " + alias);
    }

    public static SecretKey getAESKey(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyStore.Entry entry = keyStore.getEntry(alias, null);
        if (entry instanceof KeyStore.SecretKeyEntry) {
            SecretKey secretKey = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
            if (!"AES".equalsIgnoreCase(secretKey.getAlgorithm())) {
                throw new IllegalArgumentException("Key algorithm is not AES");
            }
            return secretKey;
        } else {
            throw new KeyStoreException("Key not found or not a SecretKey");
        }
    }

    public static boolean isAESKeyStored(String alias) {
        try {
            return getAESKey(alias) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
        keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                        KEYSTORE_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
        );
        keyGenerator.generateKey();
    }

}
