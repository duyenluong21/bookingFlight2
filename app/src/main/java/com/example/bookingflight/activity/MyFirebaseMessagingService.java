package com.example.bookingflight.activity;

import com.example.bookingflight.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Xử lý tin nhắn khi nhận được từ FCM
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            // Hiển thị notification
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Sử dụng PendingIntent.FLAG_IMMUTABLE
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Tạo notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "default_channel")
                .setSmallIcon(R.drawable.ic_airport) // Icon của thông báo
                .setContentTitle("Thông báo từ FCM") // Tiêu đề của thông báo
                .setContentText(messageBody) // Nội dung của thông báo
                .setAutoCancel(true) // Tự động hủy khi nhấn vào thông báo
                .setContentIntent(pendingIntent); // Mở Activity khi nhấn vào thông báo

        // Kiểm tra và tạo kênh thông báo nếu cần
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "default_channel",
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // Hiển thị notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }


    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Gửi token lên server nếu cần
        sendTokenToServer(token);
    }
    private void sendTokenToServer(String token) {
        // Tạo một Request hoặc sử dụng thư viện như Retrofit để gửi token lên server
        String serverUrl = "http://localhost:3000/app/api"; // Địa chỉ URL của server

        // Tạo một thread mới để gửi yêu cầu
        new Thread(() -> {
            try {
                URL url = new URL(serverUrl + "/send_notification.php"); // Thay đổi đường dẫn tới file PHP của bạn
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                // Tạo JSON chứa token
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("token", token);

                // Gửi dữ liệu
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString());
                writer.flush();
                writer.close();
                os.close();

                // Kiểm tra phản hồi từ server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d(TAG, "Token sent successfully");
                } else {
                    Log.d(TAG, "Failed to send token: " + responseCode);
                }

                connection.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error sending token to server: " + e.getMessage());
            }
        }).start();
    }
}