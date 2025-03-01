package com.example.bookingflight.activity;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
//    private static final String PREF_NAME = "UserSession";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_MAKH = "maKH";
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_JWT_TOKEN = "jwt_token";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveLoginDetails(String email) {
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public boolean isLoggedIn() {
//        return sharedPreferences.getString(KEY_EMAIL, null) != null;
        String jwtToken = sharedPreferences.getString(KEY_JWT_TOKEN, null);
        return jwtToken != null;  // Nếu token không null, người dùng đã đăng nhập
    }

    public String getToken() {
        return sharedPreferences.getString(KEY_JWT_TOKEN, null);
    }


    public void saveToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_JWT_TOKEN, token);
        editor.apply();  // Sử dụng apply() thay vì commit()
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }
    public void setMaKH(String maKH) {
        editor.putString(KEY_MAKH, maKH);
        editor.apply();
    }

    public String getMaKH() {
        return sharedPreferences.getString(KEY_MAKH, null) ;
    }
}
