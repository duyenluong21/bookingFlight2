package com.example.bookingflight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookingflight.R;
import com.example.bookingflight.adapter.NotificationAdapter;
import com.example.bookingflight.model.Notification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationListView;
    private NotificationAdapter adapter;
    private List<Notification> notifications;
    private ImageView backButton;
    private TextView notificationCount; // Declare TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        backButton = findViewById(R.id.backButton);
        notificationListView = findViewById(R.id.notificationListView);
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(notifications);

        // Setup RecyclerView
        notificationListView.setLayoutManager(new LinearLayoutManager(this));
        notificationListView.setAdapter(adapter);

        // Fetch notifications from Firebase
        fetchNotificationsFromFirebase();

        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markNotificationsAsRead(); // Mark notifications as read before finishing
                Intent intent = new Intent();
                setResult(RESULT_OK, intent); // Return result to the previous activity
                finish(); // Close NotificationActivity
            }
        });
    }

    private void fetchNotificationsFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://bookingflight-76b84-default-rtdb.firebaseio.com/")
                .getReference("thongBao");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notifications.clear();
                Log.d("NotificationActivity", "Fetching notifications...");

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Notification notification = snapshot.getValue(Notification.class);
                    if (notification != null) {
                        notifications.add(notification);
                        Log.d("NotificationActivity", "Fetched: " + notification.getThongBao());
                    } else {
                        Log.d("NotificationActivity", "Notification is null");
                    }
                }

                adapter.notifyDataSetChanged();
                Log.d("NotificationActivity", "Adapter notified. Number of notifications: " + notifications.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("NotificationActivity", "Error fetching notifications: " + databaseError.getMessage());
            }
        });
    }

    private void markNotificationsAsRead() {
        // Assuming you want to set all notifications to read when the user exits this activity
        for (Notification notification : notifications) {
            notification.setRead(true); // Set each notification as read
        }
        // Optionally update the count in the previous activity if needed
    }
}
