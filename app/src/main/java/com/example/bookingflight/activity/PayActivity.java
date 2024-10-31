package com.example.bookingflight.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.PayAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Pay;
import com.example.bookingflight.model.TicketCount;
import com.example.bookingflight.model.Voucher;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PayAdapter adapter;
    private List<Pay> payList = new ArrayList<>();
    private TextView textViewTotalAmount;
    private ImageView buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanhtoan);

        buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, LoginProfile.class);
                startActivity(intent);
                finish();
            }
        });

        // Khởi tạo các view
        recyclerView = findViewById(R.id.recyclerViewPurchaseHistory);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);

        // Thiết lập adapter cho RecyclerView
        adapter = new PayAdapter(payList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Lấy mã khách hàng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String maKH = sharedPreferences.getString("maKH", null);

        if (maKH != null) {
            // Gọi API để lấy dữ liệu
            getPayById(maKH);
        } else {
            Toast.makeText(PayActivity.this, "Mã khách hàng không tồn tại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPayById(String maKH) {
        // Gọi API để lấy dữ liệu
        ApiService.searchFlight.getPayById(maKH).enqueue(new Callback<ApiResponse<List<Pay>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Pay>>> call, Response<ApiResponse<List<Pay>>> response) {
                if (response.isSuccessful()) {
                    ApiResponse<List<Pay>> payResponse = response.body();
                    if (payResponse != null && payResponse.getData() != null) {
                        List<Pay> newPayList = payResponse.getData();

                        payList.clear(); // Xóa danh sách hiện tại
                        payList.addAll(newPayList); // Thêm dữ liệu mới vào danh sách
                        adapter.notifyDataSetChanged(); // Thông báo adapter cập nhật dữ liệu

                        // Tính tổng số tiền đã thanh toán
                        float totalAmount = 0; // Khởi tạo biến tổng là float
                        for (Pay pay : newPayList) {
                            String amountString = pay.getTongThanhToan(); // Giả sử tongThanhToan là String
                            try {
                                float amount = Float.parseFloat(amountString); // Chuyển đổi String sang float
                                totalAmount += amount; // Cộng vào tổng
                            } catch (NumberFormatException e) {
                                Log.e("PayActivity", "Invalid number format: " + amountString);
                            }
                        }
                        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                        textViewTotalAmount.setText(decimalFormat.format(totalAmount) + " VND");
                    }
                } else {
                    Log.e("PayActivity", "Failed to load payment data: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Pay>>> call, Throwable t) {
                Log.e("PayActivity", "API Call Failure: " + t.getMessage());
            }
        });
    }


}

