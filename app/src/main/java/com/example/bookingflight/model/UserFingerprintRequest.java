package com.example.bookingflight.model;

public class UserFingerprintRequest {
    private String maKH;
    private boolean isFingerprintRegistered;

    public UserFingerprintRequest(boolean isFingerprintRegistered) {
        this.isFingerprintRegistered = isFingerprintRegistered;
    }

    public String getMaKH() {
        return maKH;
    }

    public boolean isFingerprintRegistered() {
        return isFingerprintRegistered;
    }
}
