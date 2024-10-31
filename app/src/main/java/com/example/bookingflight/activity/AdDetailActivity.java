package com.example.bookingflight.activity;
import android.content.Intent; // Để sử dụng Intent
import android.os.Bundle; // Để sử dụng Bundle
import android.view.View; // Để sử dụng View
import android.widget.Button; // Để sử dụng Button
import android.widget.ImageView; // Để sử dụng ImageView
import android.widget.TextView; // Để sử dụng TextView
import androidx.appcompat.app.AppCompatActivity; // Để kế thừa AppCompatActivity
import com.bumptech.glide.Glide;
import com.example.bookingflight.R;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Ad;
import com.example.bookingflight.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdDetailActivity extends AppCompatActivity {

    private TextView txtNameAd, txtDescriptionAd, txtplace;
    private ImageView imgAd, backButton;
    private Button btnBookingNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_detail);

        // Khởi tạo các view
        txtNameAd = findViewById(R.id.txtnamead);
        txtDescriptionAd = findViewById(R.id.descriptionAd);
        imgAd = findViewById(R.id.imgad);
        btnBookingNow = findViewById(R.id.btnBookingNow);
        txtplace = findViewById(R.id.txtplace);
        backButton = findViewById(R.id.backButton);

        // Nhận mã quảng cáo từ Intent
        String maQC = getIntent().getStringExtra("maQC");

        // Gọi API để lấy thông tin quảng cáo
        fetchAdDetails(maQC);
        btnBookingNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdDetailActivity.this , YourSearchActivity.class) ;
                startActivity(intent);
                finish();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdDetailActivity.this , Home.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchAdDetails(String maQC) {
        ApiService.searchFlight.getAdById(maQC).enqueue(new Callback<ApiResponse<Ad>>() {
            @Override
            public void onResponse(Call<ApiResponse<Ad>> call, Response<ApiResponse<Ad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Ad> apiResponse = response.body(); // Giả sử bạn có phương thức getData() trong ApiResponse
                    if (apiResponse != null && apiResponse.getData() != null) {
                        Ad ad = apiResponse.getData(); // Lấy quảng cáo từ phản hồi
                        // Cập nhật giao diện người dùng với thông tin quảng cáo
                        txtNameAd.setText(ad.getName()); // Giả sử bạn có phương thức getName() trong model Ad
                        txtDescriptionAd.setText(ad.getDescription()); // Giả sử bạn có phương thức getDescription() trong model Ad
                        txtplace.setText(ad.getPlace());
                        // Sử dụng Glide để tải hình ảnh vào ImageView
                        Glide.with(AdDetailActivity.this)
                                .load(ad.getImg()) // Giả sử bạn có phương thức getImageUrl() trong model Ad
                                .into(imgAd);
                    }
                } else {
                    // Xử lý lỗi khi không nhận được dữ liệu hợp lệ
                    txtNameAd.setText("Không tìm thấy quảng cáo");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Ad>> call, Throwable t) {
                txtNameAd.setText("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
