package com.example.bookingflight.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bookingflight.R;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Editinfo extends AppCompatActivity {
    EditText txHovaTen, txEmail, txNgaySinh, txGioiTinh, txDiaChi, txSoDienThoai, txOldPassword, txNewPassword;
    Button btnSave, btnCancel;
    ImageView backButton;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        txHovaTen = findViewById(R.id.txHovaTen);
        txEmail = findViewById(R.id.txEmail);
        txNgaySinh = findViewById(R.id.txNgaySinh);
        txGioiTinh = findViewById(R.id.txGioiTinh);
        txDiaChi = findViewById(R.id.txDiaChi);
        txSoDienThoai = findViewById(R.id.txSoDienThoai);
        backButton = findViewById(R.id.backButton);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user")) {
            User user = intent.getParcelableExtra("user");
            if (user != null) {
                displayCustomerInfo(user);
            }
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Editinfo.this, LoginProfile.class);
                startActivity(intent);
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Editinfo.this, LoginProfile.class);
                startActivity(intent);
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> updateParams = new HashMap<>();
                if (user != null) {
                    String maKH = user.getMaKH();
                    String fullname = txHovaTen.getText().toString();
                    String email = txEmail.getText().toString();
                    String ngaySinh = txNgaySinh.getText().toString();
                    String gioiTinh = txGioiTinh.getText().toString();
                    String diaChi = txDiaChi.getText().toString();
                    String soDT = txSoDienThoai.getText().toString();

                    Log.d("UpdateInfo", "Ngay Sinh Before Formatting: " + ngaySinh);

                    LocalDate ngaySinhDate = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ngaySinhDate = LocalDate.parse(ngaySinh, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                    DateTimeFormatter formatter = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    }
                    String ngaySinhFormatted = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ngaySinhFormatted = ngaySinhDate.format(formatter);
                    }

                    Log.d("UpdateInfo", "Ngay Sinh After Formatting: " + ngaySinhFormatted);

                    updateParams.put("maKH", maKH);
                    updateParams.put("fullname", fullname);
                    updateParams.put("email", email);
                    updateParams.put("ngaySinh", ngaySinhFormatted);
                    updateParams.put("gioiTinh", gioiTinh);
                    updateParams.put("diaChi", diaChi);
                    updateParams.put("soDT", soDT);
                    updateInfo(maKH, updateParams);
                }
            }
        });
    }

    private void displayCustomerInfo(User user) {
        txEmail.setEnabled(false);
        txEmail.setFocusable(false);
        txHovaTen.setEnabled(false);
        txHovaTen.setFocusable(false);
        txNgaySinh.setEnabled(false);
        txNgaySinh.setFocusable(false);
        this.user = user;
        txHovaTen.setText(this.user.getFullname());
        txEmail.setText(this.user.getEmail());
        txNgaySinh.setText(this.user.getNgaySinh());
        txGioiTinh.setText(this.user.getGioiTinh());
        txDiaChi.setText(this.user.getDiaChi());
        txSoDienThoai.setText(this.user.getSoDT());
    }

    private void updateInfo(String maKH, Map<String, String> updateParams) {
        Call<ApiResponse<List<User>>> call = ApiService.searchFlight.updateInfo(maKH, updateParams);
        call.enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                Log.d("UpdateInfo", "onResponse: " + response.toString());
                if (response.isSuccessful()) {
                    int statusCode = response.code();
                    Toast.makeText(Editinfo.this, "Cập nhật thông tin thành công.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Editinfo.this, LoginProfile.class);
                    intent.putExtra("object_user", user);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Editinfo.this, "Cập nhật dữ liệu thất bại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Toast.makeText(Editinfo.this, "Có lỗi khi kết nối đến server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
