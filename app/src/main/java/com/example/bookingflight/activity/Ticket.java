package com.example.bookingflight.activity;

import static com.example.bookingflight.inteface.ApiService.searchFlight;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingflight.R;
import com.example.bookingflight.inteface.ApiCallback;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.BookTicket;
import com.example.bookingflight.model.PostTicket;
import com.example.bookingflight.model.Result;
import com.example.bookingflight.model.User;
import com.example.bookingflight.model.Voucher;
import com.example.bookingflight.model.VoucherUsage;
import com.example.bookingflight.model.detailTicket;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ticket extends AppCompatActivity {
    private ArrayList<detailTicket> detailTicketList = new ArrayList<>();
    private Spinner spinnerLoaiVe;
    private detailTicket selectedTicket;
    private EditText txSoLuong;
    private Button btnBooking;
    private int currentGiaVe;
    private PostTicket tongGiaVe;
    private TextView descriptionTextView2, txtDiscount;
    private String order_id;
    private static final int REQUEST_CODE_VOUCHER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        if (getIntent().hasExtra("order_id")) {
            order_id = getIntent().getStringExtra("order_id");
            Log.d("TicketActivity", "Giá trị của order_id từ Intent: " + order_id);
        } else {

            Toast.makeText(Ticket.this, "Không tìm thấy order_id trong Intent", Toast.LENGTH_SHORT).show();
            finish();
        }
        tongGiaVe = new PostTicket();
        TextView txHovaTen = findViewById(R.id.txHovaTen);
        TextView txNgaySinh = findViewById(R.id.txNgaySinh);
        TextView txDiaChi = findViewById(R.id.txDiaChi);
        TextView txSoDienThoai = findViewById(R.id.txSoDienThoai);
        TextView placeNameTextView = findViewById(R.id.placeNameTextView);
        TextView descriptionTextView = findViewById(R.id.descriptionTextView);
        TextView btnVoucher = findViewById(R.id.btnVoucher);
        txSoLuong = findViewById(R.id.txSoLuong);
        btnBooking = findViewById(R.id.btnBooking);
        descriptionTextView2 = findViewById(R.id.descriptionTextView2);
        spinnerLoaiVe = findViewById(R.id.spinnerLoaiVe);
        txtDiscount = findViewById(R.id.discount);
        txtDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Khi giá trị thay đổi, cập nhật lại tổng tiền
                String discountText = txtDiscount.getText().toString();
                double selectedVoucherDiscount = discountText.isEmpty() ? 0 : Double.parseDouble(discountText.replace("%", "")) / 100;
                updateTicketWithSelectedLoaiVe(selectedTicket, selectedVoucherDiscount);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btnVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Ticket.this, VoucherActivity.class);
//                startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_VOUCHER);
            }
        });
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPostTicket();
                sendVoucherUsage();
                // Gọi hàm updateSoLuongVe và kiểm tra thành công trước khi chuyển sang thanh toán
                updateSoLuongVe(selectedTicket, new ApiCallback() {
                    @Override
                    public void onSuccess() {
                        // Nếu cập nhật số lượng vé thành công, chuyển sang trang thanh toán
                        redirectToPaymentPage(order_id);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Nếu cập nhật thất bại, hiển thị thông báo lỗi
                        Toast.makeText(Ticket.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        User user = getIntent().getParcelableExtra("object_user");
        Result selectedFlight = getIntent().getParcelableExtra("object_flight");
        if (user != null && selectedFlight != null) {
            currentGiaVe = Integer.parseInt(selectedFlight.getGiaVe());
            txHovaTen.setText(user.getFullname());
            txNgaySinh.setText(user.getNgaysinh());
            txDiaChi.setText(user.getDiaChi());
            txSoDienThoai.setText(user.getSoDT());
            placeNameTextView.setText("Từ: " + selectedFlight.getDiaDiemDi() + " - Đến: " + selectedFlight.getDiaDiemDen());
            descriptionTextView.setText("Ngày đi: " + selectedFlight.getNgayDi() + " - Ngày đến: " + selectedFlight.getNgayDen());
        } else {
            Toast.makeText(Ticket.this, "Không thể lấy dữ liệu người dùng hoặc chuyến bay", Toast.LENGTH_SHORT).show();
            finish();
        }

        searchFlight.getTicket().enqueue(new Callback<ApiResponse<List<detailTicket>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<detailTicket>>> call, Response<ApiResponse<List<detailTicket>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<detailTicket>> apiResponse = response.body();
                    detailTicketList = new ArrayList<>(apiResponse.getData());
                    setupSpinner(detailTicketList);
                } else {
                    Toast.makeText(Ticket.this, "Failed khi gọi data từ API", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<detailTicket>>> call, Throwable t) {
                Toast.makeText(Ticket.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner(ArrayList<detailTicket> detailTicketList) {
        List<String> loaiVeList = new ArrayList<>();
        loaiVeList.add("Chọn hạng vé");

        for (detailTicket ticket : detailTicketList) {
            loaiVeList.add(ticket.getHangVe());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, loaiVeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerLoaiVe.setAdapter(adapter);
        spinnerLoaiVe.setSelection(0, false);

        spinnerLoaiVe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    String selectedLoaiVe = (String) parentView.getItemAtPosition(position);
                    selectedTicket = getDetailTicketByHangVe(selectedLoaiVe);
                    // Lấy giá trị khuyến mãi từ txtDiscount
                    String discountText = txtDiscount.getText().toString();
                    double selectedVoucherDiscount = discountText.isEmpty() ? 0 : Double.parseDouble(discountText.replace("%", "")) / 100;
                    updateTicketWithSelectedLoaiVe(selectedTicket, selectedVoucherDiscount);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private detailTicket getDetailTicketByHangVe(String hangVe) {
        for (detailTicket ticket : detailTicketList) {
            if (hangVe.equals(ticket.getHangVe())) {
                return ticket;
            }
        }
        return null;
    }

    private void updateTicketWithSelectedLoaiVe(detailTicket selectedTicket, double selectedVoucherDiscount) {
        if (selectedTicket != null) {
            if (txSoLuong.getText().toString().isEmpty()) {
                Toast.makeText(Ticket.this, "Chưa nhập số lượng", Toast.LENGTH_SHORT).show();
                return;
            }
            int soLuong = Integer.parseInt(txSoLuong.getText().toString());

            int tongThanhToan = 0;

            if ("Phổ thông".equals(selectedTicket.getHangVe())) {
                tongThanhToan = currentGiaVe + (int) (soLuong * currentGiaVe * 0.1);
            } else if ("Thương gia".equals(selectedTicket.getHangVe())) {
                tongThanhToan = currentGiaVe + (int) (soLuong * currentGiaVe * 0.3);
            } else if ("Cao cấp".equals(selectedTicket.getHangVe())) {
                tongThanhToan = currentGiaVe + (int) (soLuong * currentGiaVe * 0.5);
            }

            // Áp dụng khuyến mãi
            tongThanhToan -= tongThanhToan * selectedVoucherDiscount;

            tongGiaVe.setTongThanhToan(String.valueOf(tongThanhToan));
            descriptionTextView2.setText(tongGiaVe.getTongThanhToan());
        }
    }



    private void sendPostTicket() {
        User user = getIntent().getParcelableExtra("object_user");
        Result selectedFlight = getIntent().getParcelableExtra("object_flight");
        String nguonDat = "app";

        if (selectedFlight != null && selectedTicket != null && user != null) {
            PostTicket postTicket = new PostTicket(
                    order_id,
                    selectedTicket.getMaVe(),
                    selectedFlight.getMaCB(),
                    user.getMaKH(),
                    txSoLuong.getText().toString(),
                    descriptionTextView2.getText().toString(),
                    nguonDat
            );
            Log.d("TicketActivity", "Nguon Dat: " + nguonDat);
            searchFlight.sendPostTicket(postTicket).enqueue(new Callback<ApiResponse<List<PostTicket>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<PostTicket>>> call, Response<ApiResponse<List<PostTicket>>> response) {
                    if (response.isSuccessful()) {
                        Log.d("TicketActivity", "post ticket thành công");
                        //redirectToPaymentPage(order_id);
                    } else {
                        Toast.makeText(Ticket.this, "Đặt vé thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<PostTicket>>> call, Throwable t) {
                    Log.e("TicketActivity", "Gặp lỗi khi gửi ticket lên csdl: " + t.getMessage());
                    Toast.makeText(Ticket.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void redirectToPaymentPage(String order_id) {
        String paymentUrl = "http://192.168.1.9/TTCS/vnpay_php/vnpay_pay.php?order_id=" + order_id;
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
        startActivity(browserIntent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VOUCHER && resultCode == RESULT_OK && data != null) {
            String discount = data.getStringExtra("DISCOUNT");
            String maVoucher = data.getStringExtra("maVoucher");
            if (discount != null) {
                txtDiscount.setText(discount);
            }
            if (maVoucher != null) {
                // Lưu mã voucher vào biến toàn cục hoặc Intent để sử dụng sau này
                getIntent().putExtra("maVoucher", maVoucher);
            }
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void sendVoucherUsage() {
        User user = getIntent().getParcelableExtra("object_user");
        // Lấy mã voucher đã lưu
        String maVoucher = getIntent().getStringExtra("maVoucher");

        // Lấy ngày sử dụng (ngày hiện tại khi ấn thanh toán)
        String ngayDung = getCurrentDate();

        if (maVoucher != null) {
            // Tạo đối tượng chứa dữ liệu cần gửi
            VoucherUsage request = new VoucherUsage(user.getMaKH(), maVoucher, ngayDung);

            // Gửi yêu cầu lên server
            searchFlight.sendVoucherUsage(request).enqueue(new Callback<ApiResponse<List<VoucherUsage>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<VoucherUsage>>> call, Response<ApiResponse<List<VoucherUsage>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d("Ticket", "Sử dụng voucher thành công!");
                        //Toast.makeText(Ticket.this, "Sử dụng voucher thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        // In lỗi từ phản hồi của server
                        try {
                            String errorBody = response.errorBody().string();
                            Toast.makeText(Ticket.this, "Sử dụng voucher thất bại! Lỗi: " + errorBody, Toast.LENGTH_LONG).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<VoucherUsage>>> call, Throwable t) {
                    Toast.makeText(Ticket.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.d("Ticket", "Khách hàng không dùng voucher");
        }
    }

    private void updateSoLuongVe(detailTicket selectedTicket, final ApiCallback callback) {
        if (selectedTicket != null) {
            // Kiểm tra xem người dùng đã nhập số lượng chưa
            if (txSoLuong.getText().toString().isEmpty()) {
                Toast.makeText(Ticket.this, "Chưa nhập số lượng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy số lượng vé người dùng nhập
            int soLuongDat = Integer.parseInt(txSoLuong.getText().toString());

            // Lấy mã vé từ selectedTicket tương ứng với hạng vé đã chọn
            String maVe = selectedTicket.getMaVe();
            String maCB = getIntent().getStringExtra("maCB");

            // Lấy số lượng vé còn lại ban đầu từ chi tiết vé đã chọn
            String soLuongConString = getIntent().getStringExtra("soLuongCon");
            String hangVeString = getIntent().getStringExtra("hangVe"); // Thêm dòng này để lấy hạng vé

            if (soLuongConString == null || hangVeString == null) {
                Log.e("UpdateSoLuongVe", "Số lượng còn lại hoặc hạng vé null");
                return; // Hoặc xử lý lỗi tùy ý
            }

            // Tách các hạng vé và số lượng vé còn lại
            String[] soLuongConArray = soLuongConString.split(",\\s*");
            String[] hangVeArray = hangVeString.split(",\\s*");

            // Tạo bản đồ để ánh xạ hạng vé với số lượng còn
            Map<String, Integer> hangVeMap = new HashMap<>();
            for (int i = 0; i < hangVeArray.length; i++) {
                hangVeMap.put(hangVeArray[i], Integer.parseInt(soLuongConArray[i].trim()));
            }

            // Kiểm tra xem hạng vé đã chọn có trong bản đồ không
            String hangVeDaChon = selectedTicket.getHangVe(); // Giả định rằng `detailTicket` có phương thức này
            if (!hangVeMap.containsKey(hangVeDaChon)) {
                Toast.makeText(Ticket.this, "Hạng vé không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Lấy số lượng còn lại tương ứng với hạng vé đã chọn
            int soLuongConBanDau = hangVeMap.get(hangVeDaChon);
            int soLuongCon = soLuongConBanDau - soLuongDat;

            // Kiểm tra nếu số lượng còn lại hợp lệ
            if (soLuongCon < 0) {
                showDialog();
                Toast.makeText(Ticket.this, "Số lượng vé không đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cập nhật thông qua API
            Map<String, String> updateParams = new HashMap<>();
            updateParams.put("soLuongCon", String.valueOf(soLuongCon)); // Số lượng còn lại

            // Gọi API để cập nhật số lượng vé còn lại
            Call<ApiResponse<List<detailTicket>>> call = searchFlight.updateNumberOfTickets(maVe, maCB, updateParams);
            call.enqueue(new Callback<ApiResponse<List<detailTicket>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<detailTicket>>> call, Response<ApiResponse<List<detailTicket>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Xử lý khi cập nhật thành công
                        callback.onSuccess();
                        Log.d("API Success", "Cập nhật số lượng vé thành công");
                        Toast.makeText(Ticket.this, "Cập nhật vé thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý khi có lỗi từ server
                        callback.onFailure("Cập nhật số lượng vé thất bại");
                        Log.e("API Error", "Cập nhật vé thất bại: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<detailTicket>>> call, Throwable t) {
                    // Xử lý lỗi kết nối
                    Log.e("API Failure", "Lỗi kết nối: " + t.getMessage());
                    Toast.makeText(Ticket.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Ticket.this, "Chưa chọn hạng vé", Toast.LENGTH_SHORT).show();
        }
    }



    // Hàm để tạo và hiển thị AlertDialog
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thông báo");
        builder.setMessage("Số lượng vé bạn muốn đặt đã vượt quá số lượng còn");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // Nút đồng ý
        builder.setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Ticket.this, "Bạn đã đồng ý!", Toast.LENGTH_SHORT).show();
                // Thực hiện hành động gì đó khi người dùng chọn "Đồng ý"
            }
        });

        // Nút hủy bỏ
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(Ticket.this, "Bạn đã hủy!", Toast.LENGTH_SHORT).show();
                // Thực hiện hành động gì đó khi người dùng chọn "Hủy"
            }
        });

        // Hiển thị dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Hàm chuyển hướng về trang home
    private void redirectToHomePage() {
        Intent intent = new Intent(Ticket.this, Home.class);
        startActivity(intent);
        finish(); // Kết thúc Activity hiện tại nếu không cần quay lại
    }
}
