package com.example.bookingflight.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.ResultAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Result;
import com.example.bookingflight.model.User;
import com.example.bookingflight.model.detailTicket;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {
    private Button btnDatVe ;
    private List<User> mListUser ;
    private List<Result> mListResult ;
    private ResultAdapter resultAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Log.d("Ticket", "Ticket Activity created");
        TextView txMaCB = findViewById(R.id.txMaCB);
        TextView txDiaDiemDen = findViewById(R.id.txDiaDiemDen);
        TextView txDiaDiemDi = findViewById(R.id.txDiaDiemDi);
        TextView txNgayDi = findViewById(R.id.txNgayDi);
        TextView txNgayDen = findViewById(R.id.txNgayDen);
        TextView txGiaVe = findViewById(R.id.txGiaVe);
        TextView txGhiChu = findViewById(R.id.txGhiChu);
        TextView txGhoBay = findViewById(R.id.txGhoBay);
        btnDatVe = findViewById(R.id.btnDatVe);
        // biến cho thông tin cá nhân
        mListUser = new ArrayList<>();
        getListUser();
        //biến cho thông tin chuyến bay
        mListResult = new ArrayList<>();
        searchPlace();

        resultAdapter = getIntent().getParcelableExtra("result_adapter");
        if (resultAdapter == null) {
            resultAdapter = new ResultAdapter(this, new ArrayList<>());
        } else {
            resultAdapter.setContext(this);
        }
        Result result = getIntent().getParcelableExtra("object_flight");

        txMaCB.setText("Mã chuyến bay:" + result.getMaCB());
        txDiaDiemDen.setText(result.getDiaDiemDen());
        txDiaDiemDi.setText(result.getDiaDiemDi());
        txNgayDi.setText(result.getNgayDi());
        txNgayDen.setText(result.getNgayDen());
        txGiaVe.setText(result.getGiaVe());
        txGhoBay.setText(result.getGioBay());
        txGhiChu.setText(result.getGhiChu());

        btnDatVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickDatVe();
            }
        });
    }
    // Hiển thị thông tin chuyến bay khách hàng vừa chọn
    private void searchPlace() {
        Map<String, String> options = new HashMap<>();
        ApiService.searchFlight.searchPlace(options)
                .enqueue(new Callback<ApiResponse<List<Result>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<Result>>> call, Response<ApiResponse<List<Result>>> response) {
                        ApiResponse<List<Result>> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.getData() != null) {
                            mListResult = apiResponse.getData();
                            Log.e("List User", mListUser.size() + "");
                        }
                    }
                    @Override
                    public void onFailure(Call<ApiResponse<List<Result>>> call, Throwable t) {
                        Toast.makeText(DetailActivity.this, "Call Api error ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Hiển thị thông tin khách hàng
    private void getListUser() {
        Map<String, String> options = new HashMap<>();
        ApiService.searchFlight.getListUser(options)
                .enqueue(new Callback<ApiResponse<List<User>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                        ApiResponse<List<User>> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.getData() != null) {
                            mListUser = apiResponse.getData();
                            Log.e("List User", mListUser.size() + "");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                        Toast.makeText(DetailActivity.this, "Call Api error ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Hành động khi ấn vào button Tiếp tục đặt vé
    private void clickDatVe() {
        User currentUser = getCurrentUser();
        Result selectedFlight = getIntent().getParcelableExtra("object_flight");

        String maCB = selectedFlight.getMaCB();
        String soLuongCon = selectedFlight.getSoLuongCon();
        String hangVe = selectedFlight.getHangVe();
        if (resultAdapter != null && selectedFlight != null && currentUser != null) {
            String orderId = generateOrderId(); // Sinh order_id
            Intent intent = new Intent(DetailActivity.this, Ticket.class);
            intent.putExtra("object_user", currentUser);
            intent.putExtra("object_flight", selectedFlight);
            intent.putExtra("order_id", orderId);
            intent.putExtra("maCB", maCB);
            intent.putExtra("soLuongCon", soLuongCon);
            intent.putExtra("hangVe", hangVe);
            startActivity(intent);
            finish();
        }
    }

    private String generateOrderId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ssss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    private User getCurrentUser() {

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String currentUserEmail = sessionManager.getEmail();

        // Tìm người dùng trong danh sách
        for (User user : mListUser) {
            if (currentUserEmail.equals(user.getEmail())) {
                return user;
            }
        }

        return null; // Trả về null nếu không tìm thấy người dùng
    }

    // Thực hiện yêu cầu API để lấy danh sách loại vé

}