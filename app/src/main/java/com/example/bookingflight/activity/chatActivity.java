package com.example.bookingflight.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookingflight.Crypto.AESKeyStorage;
import com.example.bookingflight.Crypto.AESUtil;
import com.example.bookingflight.Crypto.KeyStoreHelper;
import com.example.bookingflight.Crypto.RSAUtil;
import com.example.bookingflight.Crypto.SecurityUtil;
import com.example.bookingflight.R;
import com.example.bookingflight.adapter.MessAdapter;
import com.example.bookingflight.inteface.ApiService;
import com.example.bookingflight.model.Ad;
import com.example.bookingflight.model.Mess;
import com.example.bookingflight.model.PublicKeyRequest;
import com.example.bookingflight.model.PublicKeyStaff;
import com.example.bookingflight.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    // Khai báo biến publicKey
    private PublicKey publicKey;

    private String maKH, maNV;
    private String fullNameInstance;

    ImageView backButton;

    TextView tvNameStaff;
    private boolean isFirstMessage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        backButton = findViewById(R.id.backButtonMess);
        editMess = findViewById(R.id.edit_mess);
        btnSend = findViewById(R.id.btn_send);
        rcvMess = findViewById(R.id.rcv_mess);
        tvNameStaff = findViewById(R.id.tvNameStaff);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMess.setLayoutManager(linearLayoutManager);
        mListMess = new ArrayList<>();
        messAdapter = new MessAdapter();
        messAdapter.setData(mListMess);
        rcvMess.setAdapter(messAdapter);

        chatReference = FirebaseDatabase.getInstance().getReference("Chats");

        String tenNV = getIntent().getStringExtra("TEN_NV");
        if (tenNV != null) {
            tvNameStaff.setText(tenNV);
        }

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("maKH")) {
            maNV = intent.getStringExtra("MA_NV");
            maKH = intent.getStringExtra("maKH");
            getFullNameByMaKH(maKH, (context, fullname) -> {
                if (fullname != null) {
                    sendMessage(context);
                } else {
                    Log.e("SendMessage", "Failed to get fullname for maKH: " + maKH);
                }
            });
            getListMess(this, maKH, maNV);
        }
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(chatActivity.this, ChatStaffActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(v.getContext());
            }
        });

        editMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKeyboard();
            }
        });
    }

    private String getChatId(String maKH, String maNV) {
        return maKH + "_" + maNV;
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

    private void sendMessage(Context context) {
        try {
            // Bước 1: Lấy nội dung tin nhắn từ EditText
            String messageContent = editMess != null ? editMess.getText().toString() : null;
            if (TextUtils.isEmpty(messageContent)) {
                Log.e("SendMessage", "Message content is empty or EditText is null.");
                return;
            }

            // Bước 2: Xác định alias để lưu hoặc lấy khóa AES
            String recipientAlias = maKH + "_" + maNV;
            if (TextUtils.isEmpty(recipientAlias)) {
                Log.e("SendMessage", "Recipient alias is invalid.");
                return;
            }

            // Bước 3: Gọi API để lấy Public Key của người nhận
            fetchPublicKeyFromServer(maNV, new PublicKeyCallback() {
                @Override
                public void onSuccess(String publicKeyString) {
                    try {
                        // Bước 4: Lấy hoặc tạo khóa AES dùng chung
                        SecretKey aesKey = AESKeyStorage.getAESKey(context, recipientAlias);
                        if (aesKey == null) {
                            aesKey = AESUtil.generateAESKey(256);
                            AESKeyStorage.saveAESKey(context, recipientAlias, aesKey);
                        }

                        // Bước 5: Mã hóa nội dung tin nhắn bằng AES
                        String encryptedMessage = AESUtil.encrypt(messageContent, aesKey);
                        if (TextUtils.isEmpty(encryptedMessage)) {
                            Log.e("SendMessage", "Encrypted message is empty.");
                            return;
                        }

                        // Bước 6: Mã hóa khóa AES bằng Public Key của người nhận
                        PublicKey recipientPublicKey = RSAUtil.getPublicKeyFromString(publicKeyString);
                        byte[] aesKeyBytes = aesKey.getEncoded();
                        if (aesKeyBytes == null || aesKeyBytes.length == 0) {
                            Log.e("SendMessage", "AES key encoding returned null or empty.");
                            return;
                        }

                        String encryptedAESKey = RSAUtil.encryptWithPublicKey(
                                Base64.encodeToString(aesKeyBytes, Base64.NO_WRAP),
                                recipientPublicKey
                        );
                        if (TextUtils.isEmpty(encryptedAESKey)) {
                            Log.e("SendMessage", "Encrypted AES key is empty.");
                            return;
                        }

                        // Bước 7: Chuẩn bị thời gian gửi
                        String sendTime = getCurrentTime();
                        if (TextUtils.isEmpty(sendTime)) {
                            Log.e("SendMessage", "Send time is invalid.");
                            return;
                        }

                        // Bước 8: Gửi tin nhắn và khóa AES đã mã hóa
                        addEncryptedMess(maKH, maNV, fullname, encryptedAESKey, sendTime, encryptedMessage);
                        Log.d("SendMessage", "Message sent successfully!");

                        // Xóa nội dung tin nhắn trong EditText sau khi gửi
                        if (editMess != null) {
                            editMess.setText("");
                        }

                    } catch (Exception e) {
                        Log.e("SendMessage", "Error processing message: " + e.getMessage(), e);
                    }
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("SendMessage", "Failed to fetch Public Key: " + errorMessage);
                }
            });

        } catch (Exception e) {
            Log.e("SendMessage", "Error sending message: " + e.getMessage(), e);
        }
    }



    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void getFullNameByMaKH(String maKH, BiConsumer<Context, String> callback) {
        ApiService.searchFlight.getUser().enqueue(new Callback<ApiResponse<List<User>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<User>>> call, Response<ApiResponse<List<User>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body().getData();
                    for (User user : users) {
                        if (user.getMaKH().equals(maKH)) {
                            String fullname = user.getFullname(); // Lấy fullname
                            Log.d("FullName", "Fullname: " + fullname);

                            // Gọi callback với Context và fullname
                            callback.accept(getApplicationContext(), fullname);
                            return; // Thoát sớm sau khi tìm thấy
                        }
                    }

                    // Nếu không tìm thấy user
                    Log.e("FullNameError", "No user found with maKH: " + maKH);
                    callback.accept(getApplicationContext(), null);
                } else {
                    Log.e("APIError", "Error getting user data: " + response.message());
                    callback.accept(getApplicationContext(), null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<User>>> call, Throwable t) {
                Log.e("APIError", "Error: " + t.getMessage());
                callback.accept(getApplicationContext(), null); // Truyền null nếu có lỗi
            }
        });
    }


    private void getListMess(Context context, String maKH, String maNV) {
        String chatId = getChatId(maKH, maNV);

        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        chatReference.child(chatId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListMess.clear();
                int totalMessages = 0;
                int successfullyDecrypted = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mess message = snapshot.getValue(Mess.class);
                    totalMessages++;

                    if (message != null) {
                        try {
                            if (message.getEncryptedMessage() == null) {
                                Log.e("DecryptionError", "Encrypted message is null for message ID: " + snapshot.getKey());
                                continue;
                            }

                            String decryptedMessage = null;

                            if (message.isFromCustomer()) {
                                // Nếu tin nhắn từ khách hàng, dùng khóa AES từ AESKeyStorage
                                String aesKeyAlias = maKH + "_" + maNV;
                                SecretKey aesKey = AESKeyStorage.getAESKey(context, aesKeyAlias);
                                if (aesKey == null) {
                                    Log.e("DecryptionError", "AES Key not found for alias: " + aesKeyAlias);
                                    continue;
                                }
                                if (aesKey == null) {
                                    Log.e("DecryptionError", "AES Key not found in AESKeyStorage for alias: " + aesKeyAlias);
                                    continue;
                                }

                                decryptedMessage = AESUtil.decrypt(message.getEncryptedMessage(), aesKey);
                            } else {
                                // Nếu tin nhắn từ nhân viên, dùng private key để giải mã AES key
                                if (message.getEncryptedAESKey() == null) {
                                    Log.e("DecryptionError", "Encrypted AES Key is null for message ID: " + snapshot.getKey());
                                    continue;
                                }

                                PrivateKey privateKey = KeyStoreHelper.getPrivateKey(maKH);
                                if (privateKey == null) {
                                    Log.e("DecryptionError", "Private Key not found in KeyStore for maKH: " + maKH);
                                    continue;
                                }

                                // Giải mã AES Key
                                String decryptedAESKeyString = RSAUtil.decryptWithPrivateKey(message.getEncryptedAESKey(), privateKey);
                                if (TextUtils.isEmpty(decryptedAESKeyString)) {
                                    Log.e("DecryptionError", "Failed to decrypt AES Key for message ID: " + snapshot.getKey());
                                    continue;
                                }

                                SecretKey aesKey = AESUtil.convertStringToAESKey(decryptedAESKeyString);
                                if (aesKey == null) {
                                    Log.e("DecryptionError", "Failed to convert decrypted AES Key to SecretKey for message ID: " + snapshot.getKey());
                                    continue;
                                }
                                if (aesKey == null) {
                                    Log.e("DecryptionError", "Failed to decrypt AES Key for message ID: " + snapshot.getKey());
                                    continue;
                                }

                                // Giải mã nội dung tin nhắn bằng AES Key
                                decryptedMessage = AESUtil.decrypt(message.getEncryptedMessage(), aesKey);
                            }

                            if (!TextUtils.isEmpty(decryptedMessage)) {
                                message.setDecryptedMessage(decryptedMessage);
                                mListMess.add(message);
                                successfullyDecrypted++;
                            } else {
                                Log.e("DecryptionError", "Decrypted message is empty for message ID: " + snapshot.getKey());
                            }

                        } catch (Exception e) {
                            Log.e("DecryptionError", "Error decrypting message for ID: " + snapshot.getKey() + ", error: " + e.getMessage(), e);
                        }
                    }
                }

                // Log tổng số tin nhắn đã xử lý
                Log.d("DecryptionStats", "Total messages processed: " + totalMessages);
                Log.d("DecryptionStats", "Successfully decrypted messages: " + successfullyDecrypted);

                // Cập nhật giao diện
                messAdapter.setData(mListMess);
                messAdapter.notifyDataSetChanged();
                if (!mListMess.isEmpty()) {
                    rcvMess.smoothScrollToPosition(mListMess.size() - 1); // Cuộn đến cuối danh sách
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", "Failed to read messages: " + databaseError.getMessage());
            }
        });
    }


    private void addEncryptedMess(String maKH, String maNV, String fullname, String encryptedAESKey, String sendTime, String encryptedMessage) {
        String chatId = getChatId(maKH, maNV);

        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("Chats");
        String messageId = chatReference.child(chatId).push().getKey();
        if (messageId != null) {
            Mess mess = new Mess(maKH, maNV, fullname, encryptedAESKey, sendTime, encryptedMessage, true);
            chatReference.child(chatId).child(messageId).setValue(mess)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("SendMess", "Message saved successfully.");
                        } else {
                            Log.e("SendMess", "Failed to save message: " + task.getException().getMessage());
                        }
                    });
        } else {
            Log.e("SendMess", "Failed to generate message ID.");
        }
    }


    private void fetchPublicKeyFromServer(String maNV, final PublicKeyCallback callback) {
        ApiService.searchFlight.getPublicKey(maNV).enqueue(new Callback<ApiResponse<PublicKeyStaff>>() {
            @Override
            public void onResponse(Call<ApiResponse<PublicKeyStaff>> call, Response<ApiResponse<PublicKeyStaff>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PublicKeyStaff> apiResponse = response.body();

                    if (apiResponse != null && apiResponse.getData() != null) {
                        PublicKeyStaff publicKeyRequest = apiResponse.getData();
                        String publicKeyString = publicKeyRequest.getPublic_key(); // Lấy chuỗi public key

                        // Trả lại publicKeyString qua callback
                        callback.onSuccess(publicKeyString);
                    }
                } else {
                    callback.onFailure("Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PublicKeyStaff>> call, Throwable t) {
                callback.onFailure("Failed to fetch public key: " + t.getMessage());
            }
        });
    }

    // Callback interface
    public interface PublicKeyCallback {
        void onSuccess(String publicKeyString);
        void onFailure(String errorMessage);
    }

    private String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        byte[] aesKeyBytes = aesKey.getEncoded();
        if (aesKeyBytes == null || aesKeyBytes.length == 0) {
            throw new IllegalStateException("AES key encoding returned null or empty.");
        }

        if (aesKeyBytes.length > 245) { // Giới hạn kích thước cho RSA 2048-bit
            throw new IllegalArgumentException("AES key size exceeds RSA encryption limit.");
        }

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(aesKeyBytes);

        return Base64.encodeToString(encryptedKey, Base64.NO_WRAP);
    }

    private SecretKey generateOrGetAESKey(String alias) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        // Kiểm tra nếu khóa đã tồn tại trong KeyStore
        if (keyStore.containsAlias(alias)) {
            // Lấy khóa AES đã lưu
            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null);
            return secretKeyEntry.getSecretKey();
        }

        // Nếu chưa có, tạo khóa AES mới
        byte[] rawKey = new byte[16];
        new SecureRandom().nextBytes(rawKey);
        SecretKey newKey = new SecretKeySpec(rawKey, "AES");

        // Lưu vào KeyStore
        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(newKey);
        keyStore.setEntry(alias, secretKeyEntry, null);

        return newKey;
    }
}
