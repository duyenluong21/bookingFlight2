package com.example.bookingflight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.ChatAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Ad;
import com.example.bookingflight.model.Staff;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatStaffActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter staffAdapter;
    private List<Staff> staffList = new ArrayList<>();

    private String maKH;
    private ImageView backButtonMess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_chat);

        recyclerView = findViewById(R.id.rcv_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backButtonMess = findViewById(R.id.backButtonMess);
        backButtonMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatStaffActivity.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("maKH")) {
            maKH = intent.getStringExtra("maKH");
            getStaffList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getStaffList();
    }

    private void getStaffList() {
        // Gọi API để lấy danh sách nhân viên
        ApiService.searchFlight.getListStaff().enqueue(new Callback<ApiResponse<List<Staff>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Staff>>> call, Response<ApiResponse<List<Staff>>> response) {
                // Kiểm tra nếu phản hồi từ server thành công và có dữ liệu
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Staff>> apiResponse = response.body();
                    if (apiResponse.getStatus() == 200) {
                        staffList = apiResponse.getData();

                        ChatAdapter.OnUserClickListener onUserClickListener = new ChatAdapter.OnUserClickListener() {
                            @Override
                            public void onStaffClick(Staff staff, String maNV, String tenNV) {
                                // Khi click vào item, mở ChatActivity và truyền maNV
                                Intent intent = new Intent(ChatStaffActivity.this, chatActivity.class);
                                intent.putExtra("MA_NV", maNV);
                                intent.putExtra("TEN_NV", tenNV);
                                intent.putExtra("maKH", maKH);
                                startActivity(intent);
                            }
                        };
                        // Cập nhật RecyclerView với danh sách nhân viên
                        staffAdapter = new ChatAdapter(staffList, onUserClickListener);
                        recyclerView.setAdapter(staffAdapter);
                    } else {
                        // Thông báo lỗi nếu API không thành công
                        Toast.makeText(ChatStaffActivity.this, "Lỗi: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Thông báo nếu phản hồi không thành công
                    Toast.makeText(ChatStaffActivity.this, "Lỗi kết nối hoặc dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Staff>>> call, Throwable t) {
                // Thông báo lỗi nếu cuộc gọi API gặp sự cố
                Toast.makeText(ChatStaffActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
