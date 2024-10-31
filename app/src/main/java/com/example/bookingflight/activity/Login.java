package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

public class Login extends AppCompatActivity {

    private EditText editemail, editpassword;
    private Button btnLogin;
    private TextView signupText;
    private List<User> mListUser;
    private SessionManager sessionManager;
    public static final String KEY_AD_SHOWN = "ad_shown";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        VideoView videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.login);
        videoView.setVideoURI(uri);
        videoView.start();

        // Lặp lại video khi hoàn thành
        videoView.setOnCompletionListener(mp -> videoView.start());

        editemail = findViewById(R.id.email);
        editpassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        sessionManager = new SessionManager(getApplicationContext());
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void attemptLogin() {
        String strEmail = editemail.getText().toString().trim();
        String strPassword = editpassword.getText().toString().trim();

        // Kiểm tra xem email và mật khẩu có được nhập không
        if (strEmail.isEmpty() || strPassword.isEmpty()) {
            Toast.makeText(Login.this, "Vui lòng nhập đầy đủ email và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API để lấy danh sách người dùng
        Map<String, String> options = new HashMap<>();
        ApiService.searchFlight.getListUser(options)
                .enqueue(new Callback<ApiResponse<List<User>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                        ApiResponse<List<User>> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.getData() != null) {
                            mListUser = apiResponse.getData();
                            checkUserCredentials(strEmail, strPassword);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                        Toast.makeText(Login.this, "Call Api error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserCredentials(String email, String enteredPassword) {
        for (User user : mListUser) {
            // Kiểm tra xem email có tồn tại trong danh sách không
            if (user.getEmail().equals(email)) {
                // Lấy salt từ dữ liệu người dùng
                String salt = user.getSalt().trim();
                // Băm mật khẩu nhập vào với salt tương ứng
                String hashedEnteredPassword = BCrypt.hashpw(enteredPassword, salt);
                // So sánh mật khẩu đã băm với mật khẩu đã lưu trong dữ liệu người dùng
                if (hashedEnteredPassword.equals(user.getPassword())) {
                    // Lưu trạng thái đăng nhập và maKH vào SessionManager
                    String maKH = user.getMaKH();
                    String hashedPassword = user.getMaKH();
                    sessionManager.saveLoginDetails(email);
                    sessionManager.setMaKH(maKH);

                    // Lưu maKH vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("maKH", maKH);
                    editor.putString("password", hashedPassword);
                    editor.putBoolean(KEY_AD_SHOWN, false);
                    editor.apply();



                    Log.d("YourTag", "MaKH after login: " + sessionManager.getMaKH());
                    Toast.makeText(Login.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, Home.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        }

        // Hiển thị thông báo khi đăng nhập không thành công
        Toast.makeText(Login.this, "Sai mật khẩu hoặc email", Toast.LENGTH_SHORT).show();
    }
}
