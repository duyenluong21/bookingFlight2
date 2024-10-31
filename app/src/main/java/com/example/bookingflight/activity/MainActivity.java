package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bookingflight.R;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.User;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.mindrot.jbcrypt.BCrypt;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private ApiService apiService; // Biến ApiService

    EditText t1, t2, t3, t4;
    TextView tv;
    Button btn;
    DatePickerDialog datePickerDialog1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        // Khởi tạo ApiService
        apiService = ApiService.searchFlight;

        t1 = findViewById(R.id.fullname);
        t2 = findViewById(R.id.email);
        t3 = findViewById(R.id.password);
        t4 = findViewById(R.id.ngaySinh);

        tv = findViewById(R.id.signupText);
        btn = findViewById(R.id.loginButton);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        t4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Chọn ngày tháng năm sinh")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build();

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selection);

                        // Format ngày tháng năm sinh và hiển thị
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String formattedDate = sdf.format(calendar.getTime());
                        t4.setText(formattedDate);
                    }
                });

                datePicker.show(getSupportFragmentManager(), datePicker.toString());
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!t1.getText().toString().isEmpty() &&
                        !t2.getText().toString().isEmpty() &&
                        !t3.getText().toString().isEmpty() &&
                        !t4.getText().toString().isEmpty()) {
                    savedata(
                            t1.getText().toString(),
                            t2.getText().toString(),
                            t3.getText().toString(),
                            t4.getText().toString()
                    );
                } else {
                    Toast.makeText(getApplicationContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void savedata(String fullname, String email, String password, String ngaySinh) {
        // Tạo salt mới sử dụng BCrypt
        String salt = BCrypt.gensalt();
        // Băm mật khẩu và salt để lưu trữ trong cơ sở dữ liệu
        String hashedPassword = BCrypt.hashpw(password, salt);

        // Lấy ngày hiện tại
        String ngayDangKy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        // Tạo Map dữ liệu người dùng
        Map<String, String> userData = new HashMap<>();
        userData.put("fullname", fullname);
        userData.put("email", email);
        userData.put("password", hashedPassword);
        userData.put("salt", salt);
        userData.put("ngaySinh", ngaySinh);
        userData.put("ngayDangKy", ngayDangKy); // Thêm ngày đăng ký
        // Lưu ngày đăng ký vào SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ngayDangKy", ngayDangKy); // ngayDangKy là giá trị bạn nhận được
        editor.apply();

        // Gọi phương thức postUser trên apiService
        apiService.postUser(userData).enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                handleResponse(response);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                handleFailure(t);
            }
        });
    }


    private void handleResponse(Response<ApiResponse<List<User>>> response) {
        if (response.isSuccessful()) {
            // Xử lý thành công
            clearInputFields();
            Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else {
            // Xử lý lỗi
            clearInputFields();
            Toast.makeText(getApplicationContext(), "Có lỗi xảy ra", Toast.LENGTH_LONG).show();
        }
    }

    private void handleFailure(Throwable t) {
        // Xử lý lỗi kết nối
        clearInputFields();
        Toast.makeText(getApplicationContext(), "Lỗi kết nối", Toast.LENGTH_LONG).show();
        t.printStackTrace();
    }

    private void clearInputFields() {
        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
    }
}
