package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Editpassword extends AppCompatActivity {
    private EditText txOldPassword, txNewPassword, txConfirmPassword;
    private Button btnSave, btnCancel;
    private ImageView backButton;
    private User currentUser;
    private String maKH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editpassword);

        txOldPassword = findViewById(R.id.txOldPassword);
        txNewPassword = findViewById(R.id.txNewPassword);
        txConfirmPassword = findViewById(R.id.txConfirmPassword);
        backButton = findViewById(R.id.backButton);
        btnSave = findViewById(R.id.btnSave);

        // Nhận currentUser từ Intent
        currentUser = getIntent().getParcelableExtra("object_user");

        // Hiển thị thông tin người dùng (nếu cần)
        if (currentUser != null) {
            Log.d("User Info", "User maKH: " + currentUser.getMaKH());
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Editpassword.this, LoginProfile.class);
                startActivity(intent);
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = txOldPassword.getText().toString().trim();
                String newPassword = txNewPassword.getText().toString().trim();
                String confirmPassword = txConfirmPassword.getText().toString().trim();

                if (validateInput(oldPassword, newPassword, confirmPassword)) {
                    // Kiểm tra mật khẩu cũ có đúng không
                    if (checkOldPassword(oldPassword)) {
                        // Cập nhật mật khẩu mới trong database thông qua API
                        updatePasswordOnServer(currentUser.getMaKH(), newPassword);
                    } else {
                        // Hiển thị thông báo mật khẩu cũ không đúng
                        Toast.makeText(Editpassword.this, "Mật khẩu cũ không đúng.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Hiển thị thông báo về lỗi nhập liệu
                    Toast.makeText(Editpassword.this, "Vui lòng nhập đầy đủ thông tin và xác nhận mật khẩu đúng.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePasswordOnServer(String maKH, String newPassword) {
        // Băm mật khẩu mới và tạo salt
        String salt = BCrypt.gensalt();
        String hashedNewPassword = BCrypt.hashpw(newPassword, salt);

        // Tạo một bản đồ để chứa thông tin cập nhật
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("password", hashedNewPassword); // Mật khẩu mới đã băm
        updateParams.put("salt", salt);
        updateParams.put("maKH", maKH);                 // Đặt mã khách hàng vào tham số cập nhật

        // Thực hiện cuộc gọi API để cập nhật mật khẩu
        Call<ApiResponse<List<User>>> callUpdate = ApiService.searchFlight.updatePassw(maKH, updateParams);
        callUpdate.enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Editpassword.this, "Cập nhật mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                    // Điều hướng về trang profile hoặc activity khác
                    Intent intent = new Intent(Editpassword.this, LoginProfile.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Editpassword.this, "Cập nhật dữ liệu thất bại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Toast.makeText(Editpassword.this, "Có lỗi khi kết nối đến server.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkOldPassword(String oldPassword) {
        if (currentUser == null) {
            Toast.makeText(Editpassword.this, "Không tìm thấy người dùng.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return BCrypt.checkpw(oldPassword, currentUser.getPassword());
    }

    private boolean validateInput(String oldPassword, String newPassword, String confirmPassword) {
        return !oldPassword.isEmpty() && !newPassword.isEmpty() && newPassword.equals(confirmPassword);
    }
}
