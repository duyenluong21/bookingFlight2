package com.example.bookingflight.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.ImageSliderAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Ad;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    CardView locationCard, hanhliCard, mapCard, khuyenmaiCard;
    private SessionManager sessionManager;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ImageSliderAdapter adapter;
    private List<Ad> adList = new ArrayList<>();
    private static final String PREFS_NAME = "AdPrefs";
    private static final String KEY_AD_SHOWN = "ad_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        locationCard = findViewById(R.id.locationCard);
        hanhliCard = findViewById(R.id.hanhliCard);
        mapCard= findViewById(R.id.mapCard);
        khuyenmaiCard = findViewById(R.id.khuyenmaiCard);
        ViewPager2 viewPager2 = findViewById(R.id.imageView);
        List<Ad> adList = new ArrayList<>();

        adapter = new ImageSliderAdapter(adList, this);
        viewPager2.setAdapter(adapter);

        fetchAdData();

        handler.postDelayed(runnable = () -> {
            int nextItem = viewPager2.getCurrentItem() + 1;
            if (nextItem >= adapter.getItemCount()) {
                nextItem = 0; // Quay lại slide đầu tiên
            }
            viewPager2.setCurrentItem(nextItem, true);
            handler.postDelayed(runnable, 3000); // 3 giây đổi slide
        }, 3000);
        sessionManager = new SessionManager(getApplicationContext()); // Khởi tạo SessionManager
        hanhliCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị Dialog khi CardView được bấm
                openDialog();
            }
        });
        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , LocationActivity.class) ;
                startActivity(intent);
                finish();
            }
        });
        mapCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , MapActivity.class) ;
                startActivity(intent);
                finish();
            }
        });

        khuyenmaiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this , VoucherHomeActivity.class) ;
                startActivity(intent);
                finish();
            }
        });
        // Kiểm tra đăng nhập
        if (!sessionManager.isLoggedIn()) {
            // Chuyển đến trang đăng nhập nếu chưa đăng nhập
            Intent intent = new Intent(Home.this, Login.class);
            startActivity(intent);
            finish();
            return;
        }
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.ticket) {
                    Intent myintent = new Intent(Home.this, YourSearchActivity.class);
                    startActivity(myintent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.home) {
                    // Xử lý sự kiện khi chọn mục "home"
                    return true;
                } else if (itemId == R.id.club) {
                    SessionManager sessionManager = new SessionManager(getApplicationContext());
                    String maKH = sessionManager.getMaKH();
                    Intent myIntent1 = new Intent(Home.this, chatActivity.class);
                    myIntent1.putExtra("maKH", maKH); // Truyền maKH qua Intent
                    startActivity(myIntent1);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.profile) {
                    Intent myintent2 = new Intent(Home.this, LoginProfile.class);
                    startActivity(myintent2);
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Sử dụng layoutInflater để nạp giao diện từ tệp XML
        View dialogView = getLayoutInflater().inflate(R.layout.activity_luggage, null);

        // Thiết lập nội dung cho Dialog
        builder.setView(dialogView);

        // Tạo Dialog
        AlertDialog dialog = builder.create();

        // Thiết lập sự kiện đóng Dialog
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                // Xử lý sự kiện khi Dialog được đóng
                // (Bạn có thể thực hiện các hành động khác ở đây nếu cần)
            }
        });

        // Hiển thị Dialog
        dialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    private void fetchAdData() {
        // Gọi API để lấy dữ liệu quảng cáo
        ApiService.searchFlight.getAd().enqueue(new Callback<ApiResponse<List<Ad>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Ad>>> call, Response<ApiResponse<List<Ad>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    // Lấy danh sách quảng cáo từ phản hồi
                    List<Ad> adList = response.body().getData();

                    // Kiểm tra nếu modal đã được hiển thị
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

                    boolean isAdShown = sharedPreferences.getBoolean(KEY_AD_SHOWN, false);
                    Log.d("AdShown", "Ad shown value: " + isAdShown);
                    // Chỉ hiển thị modal nếu chưa hiển thị
                    if (!isAdShown && !adList.isEmpty()) {
                        showAdModal(adList);
                    }

                    // Cập nhật dữ liệu cho adapter (cập nhật ViewPager2)
                    runOnUiThread(() -> {
                        adapter.updateImageList(adList);  // Giả sử adapter có phương thức này để cập nhật danh sách ảnh

                    });
                } else {
                    // Xử lý lỗi nếu phản hồi không thành công
                    Log.e("API Error", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Ad>>> call, Throwable t) {
                Log.e("API Error", "Failed to fetch ad data", t);
            }
        });

    }
    private void showAdModal(List<Ad> adList) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ad_dialog, null);
        builder.setView(dialogView);

        AlertDialog adDialog = builder.create();

        // Tìm các phần tử trong layout
        ImageView imgAd = dialogView.findViewById(R.id.imgAd);
        Button btnViewDetails = dialogView.findViewById(R.id.btnViewDetails);
        ImageButton btnClose = dialogView.findViewById(R.id.btnClose);

        // Chọn một quảng cáo ngẫu nhiên từ danh sách
        int randomIndex = (int) (Math.random() * adList.size());
        Ad randomAd = adList.get(randomIndex);

        // Hiển thị ảnh quảng cáo ngẫu nhiên
        loadImage(randomAd.getImg(), imgAd);

        // Gán sự kiện cho nút Mua ngay
        btnViewDetails.setOnClickListener(v -> {
            // Thực hiện hành động khi nhấn vào Mua ngay
            Intent intent = new Intent(Home.this , YourSearchActivity.class);
            startActivity(intent);
            finish();
        });

        // Gán sự kiện cho nút đóng modal (X)
        btnClose.setOnClickListener(v -> {
            adDialog.dismiss();
            // Lưu trạng thái đã hiển thị modal
            saveAdShownState();
        });

        adDialog.setOnCancelListener(dialogInterface -> saveAdShownState());

        // Hiển thị modal
        adDialog.show();
    }

    private void loadImage(String imageUrl, ImageView imageView) {
        // Load image using Picasso (make sure to add the dependency in build.gradle)
        Picasso.get().load(imageUrl).into(imageView);
    }

    private void saveAdShownState() {
        // Lưu trạng thái đã hiển thị modal quảng cáo
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AD_SHOWN, true);
        editor.apply(); // Áp dụng thay đổi
    }


}