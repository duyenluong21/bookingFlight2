package com.example.bookingflight.Crypto;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;
import android.util.Base64;

public class RSAUtil {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final int RSA_KEY_SIZE = 2048;

    /**
     * Tạo cặp khóa RSA (Public và Private Key).
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Mã hóa dữ liệu bằng khóa Public.
     */
    public static String encryptWithPublicKey(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
    }

    /**
     * Giải mã dữ liệu bằng khóa Private.
     */
    public static String decryptWithPrivateKey(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decodedBytes = Base64.decode(encryptedData, Base64.NO_WRAP);
        byte[] originalBytes = cipher.doFinal(decodedBytes);
        return new String(originalBytes, "UTF-8");
    }

    /**
     * Chuyển đổi Public Key từ chuỗi Base64 thành đối tượng PublicKey.
     */
    public static PublicKey getPublicKeyFromString(String key) throws Exception {
        byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    /**
     * Chuyển đổi Private Key từ chuỗi Base64 thành đối tượng PrivateKey.
     */
    public static PrivateKey getPrivateKeyFromString(String key) throws Exception {
        byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * Chuyển đổi Public Key thành chuỗi Base64.
     */
    public static String convertPublicKeyToString(PublicKey publicKey) {
        return Base64.encodeToString(publicKey.getEncoded(), Base64.NO_WRAP);
    }

    /**
     * Chuyển đổi Private Key thành chuỗi Base64.
     */
    public static String convertPrivateKeyToString(PrivateKey privateKey) {
        return Base64.encodeToString(privateKey.getEncoded(), Base64.NO_WRAP);
    }
}
