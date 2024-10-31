package com.example.bookingflight.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.MessAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Mess;
import com.example.bookingflight.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chatActivity extends AppCompatActivity {

    private EditText editMess;
    private Button btnSend;
    private RecyclerView rcvMess;
    private MessAdapter messAdapter;
    private ArrayList<Mess> mListMess;
    private User user;
    private String fullname;
    private DatabaseReference chatReference;
    private String maKH;

    ImageView backButton;
    private boolean isFirstMessage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        backButton = findViewById(R.id.backButtonMess);
        editMess = findViewById(R.id.edit_mess);
        btnSend = findViewById(R.id.btn_send);
        rcvMess = findViewById(R.id.rcv_mess);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMess.setLayoutManager(linearLayoutManager);
        mListMess = new ArrayList<>();
        messAdapter = new MessAdapter();
        messAdapter.setData(mListMess);
        rcvMess.setAdapter(messAdapter);

        chatReference = FirebaseDatabase.getInstance().getReference("Chats");

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("maKH")) {
            maKH = intent.getStringExtra("maKH");
            getFullNameByMaKH(maKH, this::sendMess);
            // Gọi phương thức để lấy fullname
            getListMess();
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatActivity.this, Home.class);
                startActivity(intent);
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMess();
            }
        });

        editMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKeyboard();
            }
        });
    }

    private void checkKeyboard() {
        final View activityRootView = findViewById(R.id.activityRoot);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(r);

                int heightDiff = activityRootView.getRootView().getHeight();
                if (heightDiff > 0.25 * activityRootView.getRootView().getHeight()) {
                    if (mListMess.size() > 0) {
                        rcvMess.scrollToPosition(mListMess.size() - 1);
                        activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });
    }

    private void getListMess() {
        chatReference.child(maKH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListMess.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mess message = snapshot.getValue(Mess.class);
                    if (message != null) {
                        mListMess.add(message);
                    }
                }
                messAdapter.setData(mListMess);
                messAdapter.notifyDataSetChanged();

                // Kiểm tra kích thước trước khi cuộn
                if (!mListMess.isEmpty()) {
                    rcvMess.smoothScrollToPosition(mListMess.size() - 1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseError", "Error: " + databaseError.getMessage());
                Toast.makeText(chatActivity.this, "Error loading messages", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendMess() {
        if (editMess != null && editMess.getText() != null) {
            String noiDung2 = editMess.getText().toString();

            if (TextUtils.isEmpty(noiDung2)) {
                Log.d("YourTag", "EditText content is empty");
                return;
            }

            String thoiGianGui = getCurrentTime();

            // Kiểm tra fullname có tồn tại
            if (fullname != null) {
                // Thêm tin nhắn vào hệ thống
                addMess(maKH, fullname, noiDung2, thoiGianGui);

                // Nếu đây là tin nhắn đầu tiên, gửi tin nhắn tự động
                if (isFirstMessage) {
                    sendAutoReplyToFirebase(maKH, "Cảm ơn bạn đã liên hệ, chúng tôi sẽ trả lời trong thời gian sớm nhất!"); // Nội dung tin nhắn tự động
                    isFirstMessage = false; // Đặt cờ để không gửi lại tin nhắn tự động sau các tin nhắn tiếp theo
                }
            } else {
                Log.e("YourTag", "Fullname is null");
                Toast.makeText(chatActivity.this, "Fullname not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("YourTag", "EditText or its text is null");
        }
    }

    private void sendAutoReplyToFirebase(String maKH, String noiDung1) {
        String thoiGianGui = getCurrentTime();
        String messageId = chatReference.child(maKH).push().getKey();

        if (messageId != null) {
            // Tạo đối tượng Mess cho tin nhắn tự động và lưu lên Firebase
            Mess autoReplyMessage = new Mess(maKH, null, noiDung1, thoiGianGui);
            chatReference.child(maKH).child(messageId).setValue(autoReplyMessage)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(chatActivity.this, "Tin nhắn tự động đã được gửi", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseError", "Error sending auto reply: " + e.getMessage());
                        Toast.makeText(chatActivity.this, "Gửi tin nhắn tự động thất bại", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void getFullNameByMaKH(String maKH, Runnable onComplete) {
        ApiService.searchFlight.getUser().enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getData();
                    for (User user : users) {
                        if (user.getMaKH().equals(maKH)) {
                            fullname = user.getFullname(); // Gán giá trị vào biến instance
                            Log.d("FullName", "Fullname: " + fullname);
                            if (onComplete != null) {
                                onComplete.run(); // Gọi callback sau khi đã có fullname
                            }
                            return; // Thoát vòng lặp sớm sau khi tìm thấy
                        }
                    }
                } else {
                    Log.e("APIError", "Error getting user data");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Log.e("APIError", "Error: " + t.getMessage());
            }
        });
    }




    private void addMess(String maKH,String fullname, String noiDung2, String thoiGianGui) {
        String messageId = chatReference.child(maKH).push().getKey();
        if (messageId != null) {
            Mess message = new Mess(maKH,fullname, noiDung2, null, thoiGianGui);
            chatReference.child(maKH).child(messageId).setValue(message)
                    .addOnSuccessListener(aVoid -> {
                        editMess.setText("");
                        Toast.makeText(chatActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseError", "Error: " + e.getMessage());
                        Toast.makeText(chatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
