package com.example.bookingflight.inteface;

public interface ApiCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}
