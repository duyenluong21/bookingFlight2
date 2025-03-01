package com.example.bookingflight.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private static final long TIMEOUT = 1 * 60 * 1000; // 30s(millisecond)
    private Handler idleHandler = new Handler();
    private Runnable idleRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        idleRunnable = () -> {
            showTimeoutDialog();
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        resetIdleTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopIdleTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        resetIdleTimer();
        return super.dispatchTouchEvent(event);
    }

    private void resetIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable);
        idleHandler.postDelayed(idleRunnable, TIMEOUT);
    }

    private void stopIdleTimer() {
        idleHandler.removeCallbacks(idleRunnable);
    }

    private void showTimeoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Phiên hết hạn")
                .setMessage("Bạn đã không hoạt động trong một thời gian dài. Vui lòng đăng nhập lại.")
                .setCancelable(false)
                .setPositiveButton("Đăng nhập lại", (dialog, which) -> {
                    logoutUser(); // Gọi hàm đăng xuất
                })
                .show();
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

