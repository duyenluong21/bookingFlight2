package com.example.bookingflight.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bookingflight.R;
import com.example.bookingflight.adapter.ChatBotAdapter;
import com.example.bookingflight.chatbot.DialogflowClient;
import com.example.bookingflight.model.ChatBot;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatBotActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatBotAdapter chatAdapter;
    private List<ChatBot> chatMessages;
    private EditText editTextMessage;
    private Button btnSend;
    private DialogflowClient dialogflowClient;
    private DatabaseReference databaseReference;

    private ImageView backButtonChat;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        recyclerView = findViewById(R.id.recyclerViewChat);
        editTextMessage = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.btnSend);
        backButtonChat = findViewById(R.id.backButtonChat);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatBotAdapter(chatMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        dialogflowClient = new DialogflowClient(this);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String maKH = sharedPreferences.getString("maKH", null);
        if (maKH == null) {
            Toast.makeText(this, "Không có ID khách hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("chat_bot").child(maKH);

        backButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatBotActivity.this, ChatStaffActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    String messageId = databaseReference.push().getKey();
                    if (messageId == null) {
                        Log.e("ChatBot", "Error: messageId is null");
                        return;
                    }

                    ChatBot userMessage = new ChatBot(message, true, messageId);

                    databaseReference.child(messageId).setValue(userMessage).addOnSuccessListener(aVoid -> {
//                        if (!chatMessages.contains(userMessage)) {
//                            chatMessages.add(userMessage);
//                        }


                        editTextMessage.setText("");

                        executorService.execute(() -> {
                            try {
                                String response = dialogflowClient.sendMessage(message);
                                if (response == null || response.isEmpty()) {
                                    Log.e("ChatBot", "Error: Dialogflow response is empty");
                                    return;
                                }

                                runOnUiThread(() -> {
                                    String responseId = databaseReference.push().getKey();
                                    if (responseId == null) {
                                        Log.e("ChatBot", "Error: responseId is null");
                                        return;
                                    }

                                    ChatBot botMessage = new ChatBot(response, false, responseId);
                                    Log.d("Chatbot Response", response.toString());


                                    // Lưu phản hồi từ bot vào Firebase
                                    databaseReference.child(responseId).setValue(botMessage).addOnSuccessListener(aVoid1 -> {
                                        if (!chatMessages.contains(botMessage)) {
                                            chatMessages.add(botMessage);

                                        }

                                    }).addOnFailureListener(e -> Log.e("ChatBot", "Error saving bot message: " + e.getMessage()));
                                });
                            } catch (Exception e) {
                                Log.e("ChatBot", "Error sending message to Dialogflow: " + e.getMessage());
                            }
                        });
                    }).addOnFailureListener(e -> Log.e("ChatBot", "Error saving user message: " + e.getMessage()));

                }
            }
        });



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatBot> newMessages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatBot chat = dataSnapshot.getValue(ChatBot.class);
                    if (chat != null) {
                        newMessages.add(chat);
                    }
                }

                // Chỉ cập nhật danh sách nếu có sự thay đổi
                if (!chatMessages.equals(newMessages)) {
                    chatMessages.clear();
                    chatMessages.addAll(newMessages);
                    chatAdapter.notifyDataSetChanged();

                    // Cuộn xuống tin nhắn mới nhất
                    if (!chatMessages.isEmpty()) {
                        recyclerView.post(() -> recyclerView.smoothScrollToPosition(chatMessages.size() - 1));
                    }
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatBotActivity.this, "Lỗi khi tải tin nhắn!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
