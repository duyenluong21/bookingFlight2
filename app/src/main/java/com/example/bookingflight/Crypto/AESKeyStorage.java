package com.example.bookingflight.Crypto;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AESKeyStorage {
    private static final String PREF_NAME = "AESKeyStorage";
    private static final String AES_KEY_PREFIX = "AES_KEY_";

    /**
     * Lưu khóa AES dưới dạng Base64 vào SharedPreferences.
     *
     * @param alias Alias của khóa
     * @param key   SecretKey AES cần lưu
     */
    public static void saveAESKey(Context context, String alias, SecretKey key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String encodedKey = Base64.encodeToString(key.getEncoded(), Base64.NO_WRAP);
        editor.putString(AES_KEY_PREFIX + alias, encodedKey);
        editor.apply();
    }

    /**
     * Lấy khóa AES từ SharedPreferences.
     *
     * @param alias Alias của khóa
     * @return SecretKey nếu tồn tại, null nếu không tìm thấy
     */
    public static SecretKey getAESKey(Context context, String alias) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String encodedKey = sharedPreferences.getString(AES_KEY_PREFIX + alias, null);
        if (encodedKey == null) {
            return null;
        }

        byte[] decodedKey = Base64.decode(encodedKey, Base64.NO_WRAP);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * Xóa khóa AES từ SharedPreferences.
     *
     * @param alias Alias của khóa
     */
    public static void deleteAESKey(Context context, String alias) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(AES_KEY_PREFIX + alias);
        editor.apply();
    }
}

