package com.example.bookingflight.Crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

public class EncryptionHandler {
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public EncryptionHandler() throws Exception {
        // Tạo cặp khóa RSA
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }

    // Lấy Public Key (để gửi đến server)
    public PublicKey getPublicKey() {
        return publicKey;
    }

    // Lấy Private Key (để giải mã AES key từ server)
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
