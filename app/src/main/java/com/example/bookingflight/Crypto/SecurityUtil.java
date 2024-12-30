package com.example.bookingflight.Crypto;

import android.text.TextUtils;
import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtil {
    public static PublicKey getServerPublicKey(String publicKeyPEM) throws Exception {
        if (TextUtils.isEmpty(publicKeyPEM)) {
            throw new IllegalArgumentException("Public key string is empty.");
        }

        try {
            publicKeyPEM = publicKeyPEM.replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            // Sử dụng Android Base64 hoặc Apache Commons Codec nếu không dùng Java 8
            byte[] keyBytes = Base64.decode(publicKeyPEM, Base64.DEFAULT);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse Public Key from string.", e);
        }
    }

}
