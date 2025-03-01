package com.example.bookingflight.chatbot;

import android.content.Context;
import android.util.Log;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.*;

import java.io.InputStream;
import java.util.UUID;

public class DialogflowClient {
    private static final String TAG = "DialogflowClient";
    private SessionsClient sessionsClient;
    private SessionName session;

    public DialogflowClient(Context context) {
        try {
            InputStream stream = context.getAssets().open("bookingflight-76b84-ab9b7c4b89a8.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");
            stream.close();

            SessionsSettings sessionsSettings = SessionsSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
            sessionsClient = SessionsClient.create(sessionsSettings);

            String projectId = "bookingflight-76b84";
            String sessionId = UUID.randomUUID().toString();
            session = SessionName.of(projectId, sessionId);

            Log.d(TAG, "SessionName: " + session.toString());
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi khởi tạo Dialogflow: " + e.getMessage());
        }
    }

    public String sendMessage(String message) {
        Log.d(TAG, "User message: " + message);
        try {
            if (sessionsClient == null) {
                Log.e(TAG, "sessionsClient chưa được khởi tạo!");
                return "Lỗi kết nối với server!";
            }
            if (session == null) {
                Log.e(TAG, "session chưa được khởi tạo!");
                return "Lỗi kết nối với server!";
            }

            TextInput textInput = TextInput.newBuilder()
                    .setText(message)
                    .setLanguageCode("en")
                    .build();

            QueryInput queryInput = QueryInput.newBuilder()
                    .setText(textInput)
                    .build();

            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .build();

            DetectIntentResponse response = sessionsClient.detectIntent(request);

            if (response.hasQueryResult()) {
                return response.getQueryResult().getFulfillmentText();
            } else {
                return "Không nhận được phản hồi từ bot!";
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi gửi tin nhắn: " + e.getMessage());
            return "Lỗi khi gửi tin nhắn!";
        }
    }
}
