package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.bookingflight.Crypto.KeyStoreHelper;
import com.example.bookingflight.R;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.inteface.PublicKeyCheckCallback;
import com.example.bookingflight.model.PublicKeyRequest;
import com.example.bookingflight.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
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
                            checkUserCredentials(strEmail, strPassword); // Gọi trực tiếp hàm kiểm tra
                        } else {
                            Toast.makeText(Login.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                        Toast.makeText(Login.this, "Call Api error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private KeyPair generateRSAKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Độ dài khóa
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertPublicKeyToBase64(PublicKey publicKey) {
        return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
    }

    private void uploadPublicKeyToServer(String maKH, PublicKeyRequest publicKeyRequest) {

        // Gọi API để cập nhật public key
        ApiService.searchFlight.updatePublicKey(maKH, publicKeyRequest)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Đoạn này được chạy khi phản hồi HTTP 200 OK
                            Toast.makeText(Login.this, "Cập nhật khóa thành công!", Toast.LENGTH_SHORT).show();
                            Log.d("API_SUCCESS", "Cập nhật khóa thành công với maKH: " + maKH);

                            // Log thông tin về public key
                            Log.d("DEBUG_publicKey", publicKeyRequest.getPublic_key());
                        } else {
                            // Xử lý khi phản hồi không thành công
                            Toast.makeText(Login.this, "Cập nhật khóa thất bại!", Toast.LENGTH_SHORT).show();
                            // In mã lỗi và lý do từ server nếu cần
                            Log.e("API_ERROR", "Code: " + response.code() + " Message: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Xử lý lỗi khi không thể kết nối với server
                        Toast.makeText(Login.this, "Không thể kết nối tới server!", Toast.LENGTH_SHORT).show();
                        Log.e("API_FAILURE", "Error: " + t.getMessage());
                    }
                });
    }



//    private void checkUserCredentials(String email, String enteredPassword) {
//        for (User user : mListUser) {
//            if (user.getEmail().equals(email)) {
//                String salt = user.getSalt().trim();
//                String hashedEnteredPassword = BCrypt.hashpw(enteredPassword, salt);
//
//                if (hashedEnteredPassword.equals(user.getPassword())) {
//                    String maKH = user.getMaKH();
//                    sessionManager.saveLoginDetails(email);
//                    sessionManager.setMaKH(maKH);
//
//                    // Tạo cặp khóa RSA
//                    KeyPair keyPair = generateRSAKeyPair();
//                    if (keyPair != null) {
//                        // Lưu private key vào SharedPreferences (hoặc nơi an toàn hơn)
//                        PrivateKey privateKey = keyPair.getPrivate();
//                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("privateKey", Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT));
//                        editor.apply();
//
//                        // Gửi public key lên server
//                        String publicKeyBase64 = convertPublicKeyToBase64(keyPair.getPublic());
//                        PublicKeyRequest publicKeyRequest = new PublicKeyRequest(publicKeyBase64);
//                        uploadPublicKeyToServer(maKH, publicKeyRequest);
//                    }
//
//                    // Chuyển sang màn hình Home
//                    Intent intent = new Intent(Login.this, Home.class);
//                    startActivity(intent);
//                    finish();
//                    return;
//                }
//            }
//        }
//
//        Toast.makeText(Login.this, "Sai mật khẩu hoặc email", Toast.LENGTH_SHORT).show();
//    }

    private void checkUserCredentials(String email, String enteredPassword) {
        for (User user : mListUser) {
            if (user.getEmail().equals(email)) {
                String salt = user.getSalt().trim();
                String hashedEnteredPassword = BCrypt.hashpw(enteredPassword, salt);

                if (hashedEnteredPassword.equals(user.getPassword())) {
                    String maKH = user.getMaKH();
                    sessionManager.saveLoginDetails(email);
                    sessionManager.setMaKH(maKH);

                    // Kiểm tra public key bất đồng bộ
                    checkIfUserHasPublicKey(maKH, hasPublicKey -> {
                        if (!hasPublicKey) {
                            try {
                                // Tạo và lưu trữ RSA key pair vào Keystore
                                KeyStoreHelper.generateAndStoreRSAKeyPair(maKH);

                                // Lấy public key từ Keystore và gửi lên server
                                PublicKey publicKey = KeyStoreHelper.getPublicKey(maKH);
                                if (publicKey != null) {
                                    String publicKeyBase64 = convertPublicKeyToBase64(publicKey);
                                    uploadPublicKeyToServer(maKH, new PublicKeyRequest(publicKeyBase64));
                                    Log.e("KeyStore", "Đã lưu được khóa lên csdl");
                                } else {
                                    Log.e("KeyStore", "Không thể lấy public key từ Keystore");
                                    Toast.makeText(Login.this, "Lỗi xử lý khóa bảo mật", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (Exception e) {
                                Log.e("KeyStore", "Lỗi khi tạo hoặc lưu khóa RSA", e);
                                Toast.makeText(Login.this, "Lỗi khi xử lý khóa bảo mật", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        // Kiểm tra xem private key đã được lưu chưa
                        if (KeyStoreHelper.isPrivateKeyStored(maKH)) {
                            Log.d("KeyStore", "Private key đã được lưu trữ.");
                        } else {
                            Log.d("KeyStore", "Private key chưa được lưu trữ.");
                        }

                        // Chuyển sang màn hình Home
                        Intent intent = new Intent(Login.this, Home.class);
                        startActivity(intent);
                        finish();
                        return;
                    });
                }
            }
        }
    }



    private void checkIfUserHasPublicKey(String maKH, PublicKeyCheckCallback callback) {
        ApiService.searchFlight.checkIfUserHasPublicKey(maKH)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        // Kiểm tra xem response có thành công không
                        if (response.isSuccessful()) {
                            try {
                                // Parse JSON response để lấy giá trị hasPublicKey
                                JSONObject responseObject = new JSONObject(response.body().string());
                                boolean hasPublicKey = responseObject.getBoolean("hasPublicKey");

                                // Gọi callback để xử lý kết quả
                                callback.onPublicKeyChecked(hasPublicKey);
                            } catch (Exception e) {
                                // Nếu có lỗi trong quá trình parse, coi như chưa có public key
                                callback.onPublicKeyChecked(false);
                            }
                        } else {
                            // Nếu API không thành công, coi như khách hàng chưa có public key
                            callback.onPublicKeyChecked(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // Nếu có lỗi kết nối, coi như khách hàng chưa có public key
                        callback.onPublicKeyChecked(false);
                    }
                });
    }



}
