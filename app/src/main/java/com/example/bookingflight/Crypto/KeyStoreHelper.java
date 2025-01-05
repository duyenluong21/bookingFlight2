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
    public static boolean isPrivateKeyStored(String maKH) {
        try {
            Key privateKey = getPrivateKey(maKH);
            return privateKey != null;
        } catch (Exception e) {
            Log.e("KeyStore", "Lỗi khi kiểm tra private key", e);
            return false;
        }
    }

}
