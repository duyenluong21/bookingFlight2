package com.example.bookingflight.activity;

import static com.example.bookingflight.inteface.ApiService.searchFlight;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bookingflight.ApiServiceClient;
import com.example.bookingflight.R;
import com.example.bookingflight.adapter.NotificationAdapter;
import com.example.bookingflight.adapter.TicketAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Notification;
import com.example.bookingflight.model.User;// Make sure this is imported
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginProfile extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    CardView thongtincanhan, vedadat, changepass, thanhtoan;
    ImageView logoutButton, bellButton;
    TextView fullname;
    private List<User> mListUser;
    private List<Notification> notifications; // Initialize the notifications list
    private NotificationAdapter adapter; // Declare the adapter
    private int notificationCount = 0; // Khai báo biến đếm thông báo
    private TextView notificationCountTextView; // TextView để hiển thị số thông báo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_profile);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        logoutButton = findViewById(R.id.logoutButton);
        thongtincanhan = findViewById(R.id.thongtincanhan);
        vedadat = findViewById(R.id.vedadat);
        changepass = findViewById(R.id.changepass);
        thanhtoan = findViewById(R.id.thanhtoan);
        fullname = findViewById(R.id.fullname);
        bellButton = findViewById(R.id.bellButton);
        notificationCountTextView = findViewById(R.id.notificationCount);
        mListUser = new ArrayList<>();
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);
        getListUser();
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.ticket) {
                startActivity(new Intent(LoginProfile.this, YourSearchActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.home) {
                startActivity(new Intent(LoginProfile.this, Home.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.profile) {
                return true;
            } else if (itemId == R.id.club) {
                SessionManager sessionManager = new SessionManager(getApplicationContext());
                String maKH = sessionManager.getMaKH();
                Intent intent = new Intent(LoginProfile.this, ChatStaffActivity.class);
                intent.putExtra("maKH", maKH);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

        logoutButton.setOnClickListener(v -> performLogout());
        thongtincanhan.setOnClickListener(v -> clickThongtin());
        vedadat.setOnClickListener(v -> clickVeDaDat());
        thanhtoan.setOnClickListener(v -> clickThanhToan());
        changepass.setOnClickListener(v -> clickChangePass());
        bellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đánh dấu tất cả thông báo là đã đọc trong Firebase
                markAllNotificationsAsReadForCurrentUser();

                // Cập nhật lại số lượng thông báo và ẩn TextView đếm thông báo
                notificationCount = 0;
                updateNotificationCount();

                // Chuyển đến NotificationActivity
                Intent intent = new Intent(LoginProfile.this, NotificationActivity.class);
                startActivityForResult(intent, 1); // Sử dụng startActivityForResult để nhận kết quả
            }
        });
        fetchNotificationsFromFirebase();
        subscribeToNotifications();
    }
    private void clickChangePass() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginProfile.this, Editpassword.class);
            intent.putExtra("object_user", currentUser);
            startActivity(intent);
            finish();
        }
    }

    private void clickVeDaDat() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginProfile.this, Booked.class);
            intent.putExtra("user_maKH", currentUser.getMaKH());
            startActivity(intent);
        }
    }

    private void clickThanhToan() {
            Intent intent = new Intent(LoginProfile.this, PayActivity.class);
            startActivity(intent);
    }

    private void clickThongtin() {
        User currentUser = getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(LoginProfile.this, Profile.class);
            intent.putExtra("object_user", currentUser);
            startActivity(intent);
            finish();
        }
    }

    private User getCurrentUser() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String currentUserEmail = sessionManager.getMaKH();
        for (User user : mListUser) {
            if (currentUserEmail.equals(user.getMaKH())) {
                return user;
            }
        }
        return null;
    }

    private void getListUser() {
        Map<String, String> options = new HashMap<>();
        ApiService apiService = ApiServiceClient.getApiService(this);
        apiService.getListUser(options)
                .enqueue(new Callback<ApiResponse<List<User>>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                        ApiResponse<List<User>> apiResponse = response.body();
                        if (apiResponse != null && apiResponse.getData() != null) {
                            mListUser = apiResponse.getData();
                            User currentUser = getCurrentUser();
                            if (currentUser != null) {
                                fullname.setText(currentUser.getFullname());
                            }
                            Log.e("List User", mListUser.size() + "");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                        Toast.makeText(LoginProfile.this, "Call Api error ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void performLogout() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("jwt_token");  // Xóa jwt_token
        editor.apply();
        sessionManager.logout();

        Intent intent = new Intent(LoginProfile.this, Login.class);
        startActivity(intent);
        finish();
    }
    /// Thông báo bell
    // Phương thức để cập nhật số lượng thông báo
    private void subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("BookingFlight")
                .addOnCompleteListener(task -> {
                    String msg = "Subscribed";
                    if (!task.isSuccessful()) {
                        msg = "Subscribe failed";
                    }
                    Log.d("Hong Anh test notify", msg);
                    Toast.makeText(LoginProfile.this, msg, Toast.LENGTH_SHORT).show();
                });
    }

    // Fetch notifications from Firebase
    private void fetchNotificationsFromFirebase() {
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String currentUserId = sessionManager.getMaKH(); // Lấy ID của user hiện tại

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://bookingflight-76b84-default-rtdb.firebaseio.com/")
                .getReference("thongBao");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear(); // Xóa danh sách thông báo hiện tại
                int unreadCount = 0; // Đếm số thông báo chưa đọc

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ngayTao = snapshot.child("ngayTao").getValue(String.class);
                    String thongBao = snapshot.child("thongBao").getValue(String.class);
                    Boolean isRead = false;

                    // Kiểm tra trạng thái 'read' của người dùng hiện tại cho từng thông báo
                    if (snapshot.hasChild("users") && snapshot.child("users").hasChild(currentUserId)) {
                        isRead = snapshot.child("users").child(currentUserId).child("read").getValue(Boolean.class);
                    }

                    Notification notification = new Notification(ngayTao, thongBao);
                    notification.setRead(isRead != null ? isRead : false); // Đặt trạng thái đã đọc cho thông báo

                    // Nếu thông báo chưa đọc, tăng biến đếm
                    if (!notification.isRead()) {
                        unreadCount++;
                    }

                    notifications.add(notification);
                }

                // Cập nhật số lượng thông báo chưa đọc
                notificationCount = unreadCount;
                updateNotificationCount(); // Cập nhật TextView hiển thị số lượng thông báo
                adapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LoginProfile", "Error fetching notifications: " + databaseError.getMessage());
            }
        });
    }

    // Phương thức để cập nhật số lượng thông báo
    private void updateNotificationCount() {
        notificationCountTextView.setText(String.valueOf(notificationCount));
        // Ẩn TextView nếu không còn thông báo chưa đọc
        notificationCountTextView.setVisibility(notificationCount > 0 ? View.VISIBLE : View.GONE);
    }


    // Nhận kết quả từ NotificationActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            // Khi quay lại từ NotificationActivity, đặt notificationCount về 0
            notificationCount = 0;
            updateNotificationCount(); // Cập nhật lại số lượng thông báo
        }
    }

    private void markAllNotificationsAsReadForCurrentUser() {
        // Lấy user hiện tại từ SessionManager hoặc bất kỳ phương thức nào khác
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        String currentUserId = sessionManager.getMaKH();  // Sử dụng mã khách hàng (maKH) làm ID người dùng

        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://bookingflight-76b84-default-rtdb.firebaseio.com/")
                .getReference("thongBao");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String notificationId = snapshot.getKey();
                    if (notificationId != null && currentUserId != null) {
                        // Cập nhật trạng thái 'read' thành true cho user hiện tại
                        databaseReference.child(notificationId)
                                .child("users")
                                .child(currentUserId)
                                .child("read").setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LoginProfile", "Error marking notifications as read: " + databaseError.getMessage());
            }
        });
    }



}
