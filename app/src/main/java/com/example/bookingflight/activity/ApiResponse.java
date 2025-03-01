package com.example.bookingflight.activity;

public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    private String token;

    private Boolean isFingerprintRegistered;

    // Getter và Setter

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getFingerprintRegistered() {
        return isFingerprintRegistered;
    }

    public void setFingerprintRegistered(Boolean fingerprintRegistered) {
        isFingerprintRegistered = fingerprintRegistered;
    }
}