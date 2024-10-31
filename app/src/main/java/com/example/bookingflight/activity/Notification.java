package com.example.bookingflight.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.bookingflight.R;

public class Notification {
    public static void showNotification(Context context, String title, String message) {
        // Create a Notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel_id", "Channel Name", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a custom notification layout
        RemoteViews customView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        customView.setTextViewText(R.id.notificationTitle, title);
        customView.setTextViewText(R.id.notificationMessage, message);

        // Create the notification using NotificationCompat
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel_id")
                .setSmallIcon(R.drawable.bell)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.bell))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(customView)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
