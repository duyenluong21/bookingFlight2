package com.example.bookingflight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.bookingflight.R;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.User;
import com.example.bookingflight.model.UserFingerprintRequest;

import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterFingerprintActivity extends AppCompatActivity {
    ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_fingerprint);
        Intent intent = getIntent();
        User user = intent.getParcelableExtra("user");
        String maKH = user.getMaKH();

        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterFingerprintActivity.this, LoginProfile.class);
                startActivity(intent);
            }
        });

        Button registerButton = findViewById(R.id.registerButton);
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Thiết bị không hỗ trợ vân tay hoặc chưa thiết lập vân tay", Toast.LENGTH_LONG).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(RegisterFingerprintActivity.this, "Đăng ký vân tay thành công!", Toast.LENGTH_SHORT).show();
                saveFingerprintRegistration(maKH);
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(RegisterFingerprintActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(RegisterFingerprintActivity.this, "Xác thực thất bại. Thử lại.", Toast.LENGTH_SHORT).show();
            }
        };

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Đăng ký vân tay")
                .setSubtitle("Xác thực vân tay để hoàn tất đăng ký")
                .setNegativeButtonText("Hủy")
                .build();
        registerButton.setOnClickListener(view -> biometricPrompt.authenticate(promptInfo));
    }

    private void saveFingerprintRegistration(String maKH) {
        UserFingerprintRequest request = new UserFingerprintRequest(true);
        ApiService.searchFlight.registerFingerprint(maKH, request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterFingerprintActivity.this, "Đăng ký vân tay thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterFingerprintActivity.this, "Không thể lưu trạng thái vân tay.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(RegisterFingerprintActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
