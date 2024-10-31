package com.example.bookingflight.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.VoucherAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.TicketCount;
import com.example.bookingflight.model.User;
import com.example.bookingflight.model.Voucher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherActivity extends AppCompatActivity {

    private static final String TAG = "VoucherActivity";
    private List<Voucher> vouchers = new ArrayList<>();
    private VoucherAdapter voucherAdapter;
    private String maKH;
    private Voucher selectedVoucher; // Để lưu voucher được chọn

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_list);

        // Khởi tạo RecyclerView và Adapter
        RecyclerView rcv = findViewById(R.id.recyclerViewVoucher);
        voucherAdapter = new VoucherAdapter(vouchers, new VoucherAdapter.OnVoucherSelectedListener() {
            @Override
            public void onVoucherSelected(Voucher voucher) {
                selectedVoucher = voucher; // Lưu voucher được chọn
            }
        });
        rcv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcv.setAdapter(voucherAdapter);



        // Lấy maKH từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        maKH = sharedPreferences.getString("maKH", null);

        if (maKH != null) {
            // Gọi API để lấy ngày đăng ký của khách hàng
            ApiService.searchFlight.getRegistrationDate(maKH).enqueue(new Callback<ApiResponse<User>>() {
                @Override
                public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                    if (response.isSuccessful()) {
                        ApiResponse<User> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.getData() != null) {
                            String registrationDate = apiResponse.getData().getNgayDangKy();
                            boolean isNew = isNewCustomer(registrationDate);

                            // Gọi API để kiểm tra số lượng vé đã mua
                            ApiService.searchFlight.getTicketCount(maKH).enqueue(new Callback<ApiResponse<TicketCount>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<TicketCount>> call, Response<ApiResponse<TicketCount>> response) {
                                    if (response.isSuccessful()) {
                                        ApiResponse<TicketCount> ticketCountResponse = response.body();
                                        if (ticketCountResponse != null && ticketCountResponse.getData() != null) {
                                            TicketCount ticketCount = ticketCountResponse.getData();
                                            int totalTickets = ticketCount.getTotal_tickets();
                                            boolean isVIP = totalTickets > 10; // Xác định khách hàng VIP

                                            // Gọi API tương ứng để lấy dữ liệu voucher
                                            if (isVIP) {
                                                ApiService.searchFlight.getVoucherVip(maKH).enqueue(new Callback<ApiResponse<List<Voucher>>>() {
                                                    @Override
                                                    public void onResponse(Call<ApiResponse<List<Voucher>>> call, Response<ApiResponse<List<Voucher>>> response) {
                                                        if (response.isSuccessful()) {
                                                            if (response.body() != null) {
                                                                List<Voucher> voucherList = response.body().getData();
                                                                if (voucherList != null) {
                                                                    vouchers.clear();
                                                                    vouchers.addAll(voucherList);
                                                                    voucherAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        } else {
                                                            Log.e(TAG, "Failed to load VIP vouchers: " + response.message());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ApiResponse<List<Voucher>>> call, Throwable t) {
                                                        Log.e(TAG, "API Call Failure: " + t.getMessage());
                                                    }
                                                });
                                            }else if(isNew){
                                                ApiService.searchFlight.getVoucherNew(maKH).enqueue(new Callback<ApiResponse<List<Voucher>>>() {
                                                    @Override
                                                    public void onResponse(Call<ApiResponse<List<Voucher>>> call, Response<ApiResponse<List<Voucher>>> response) {
                                                        if (response.isSuccessful()) {
                                                            if (response.body() != null) {
                                                                List<Voucher> voucherList = response.body().getData();
                                                                if (voucherList != null) {
                                                                    vouchers.clear();
                                                                    vouchers.addAll(voucherList);
                                                                    voucherAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        } else {
                                                            Log.e(TAG, "Failed to load new vouchers: " + response.message());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ApiResponse<List<Voucher>>> call, Throwable t) {
                                                        Log.e(TAG, "API Call Failure: " + t.getMessage());
                                                    }
                                                });
                                            }
                                            else {
                                                ApiService.searchFlight.getVoucherCustomer(maKH).enqueue(new Callback<ApiResponse<List<Voucher>>>() {
                                                    @Override
                                                    public void onResponse(Call<ApiResponse<List<Voucher>>> call, Response<ApiResponse<List<Voucher>>> response) {
                                                        if (response.isSuccessful()) {
                                                            if (response.body() != null) {
                                                                List<Voucher> voucherList = response.body().getData();
                                                                if (voucherList != null) {
                                                                    vouchers.clear();
                                                                    vouchers.addAll(voucherList);
                                                                    voucherAdapter.notifyDataSetChanged();
                                                                }
                                                            }
                                                        } else {
                                                            Log.e(TAG, "Failed to load customer vouchers: " + response.message());
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<ApiResponse<List<Voucher>>> call, Throwable t) {
                                                        Log.e(TAG, "API Call Failure: " + t.getMessage());
                                                    }
                                                });
                                            }
                                        }
                                    } else {
                                        Log.e(TAG, "Failed to get ticket count: " + response.message());
                                    }
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<TicketCount>> call, Throwable t) {
                                    Log.e(TAG, "API Call Failure: " + t.getMessage());
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "Failed to get registration date: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                    Log.e(TAG, "API Call Failure: " + t.getMessage());
                }
            });
        } else {
            Log.e(TAG, "No maKH found in SharedPreferences");
        }
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });
        // Cài đặt sự kiện cho nút xác nhận voucher
        Button btnConfirmVoucher = findViewById(R.id.btnClickVoucher);
        btnConfirmVoucher.setOnClickListener(v -> {
            if (selectedVoucher != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("DISCOUNT", selectedVoucher.getDiscount());
                resultIntent.putExtra("maVoucher", selectedVoucher.getMaVoucher());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(VoucherActivity.this, "No voucher selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNewCustomer(String registrationDate) {
        // Định dạng ngày tháng năm cho ngày đăng ký
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date regDate = dateFormat.parse(registrationDate);
            if (regDate == null) {
                return false;
            }

            Calendar calendar = Calendar.getInstance();
            Date currentDate = calendar.getTime();

            Calendar oneMonthAgo = Calendar.getInstance();
            oneMonthAgo.add(Calendar.MONTH, -1);

            return regDate.after(oneMonthAgo.getTime()) && regDate.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
