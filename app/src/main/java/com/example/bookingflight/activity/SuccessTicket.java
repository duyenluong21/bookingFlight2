package com.example.bookingflight.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.bookingflight.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SuccessTicket extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_ticket);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        sessionManager = new SessionManager(getApplicationContext()); // Khởi tạo SessionManager

        // Kiểm tra đăng nhập
        if (!sessionManager.isLoggedIn()) {
            // Chuyển đến trang đăng nhập nếu chưa đăng nhập
            Intent intent = new Intent(SuccessTicket.this, Login.class);
            startActivity(intent);
            finish();
        }
        bottomNavigationView.setOnItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.ticket) {
                    Intent myintent = new Intent(SuccessTicket.this, YourSearchActivity.class);
                    startActivity(myintent);
                    overridePendingTransition(0, 0);
                    return;
                } else if (itemId == R.id.home) {
                    Intent myintent = new Intent(SuccessTicket.this, Home.class);
                    startActivity(myintent);
                    overridePendingTransition(0, 0);
                    return;
                } else if (itemId == R.id.club) {
                    Intent myintent1 = new Intent(SuccessTicket.this, chatActivity.class);
                    startActivity(myintent1);
                    overridePendingTransition(0, 0);
                    return;
                } else if (itemId == R.id.profile) {
                    Intent myintent2 = new Intent(SuccessTicket.this, LoginProfile.class);
                    startActivity(myintent2);
                    overridePendingTransition(0, 0);
                    return;
                }
                return ;
            }
        });
    }
}