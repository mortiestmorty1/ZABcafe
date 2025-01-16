package com.szabist.zabapp1.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.szabist.zabapp1.MainActivity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle incoming FCM message and send notification
        remoteMessage.notification?.let {
            sendNotification(applicationContext, it.title ?: "Notification", it.body ?: "")
        }

        // Handle additional data payload (if any)
        remoteMessage.data?.let { data ->
            val type = data["type"] // Type can be "order" or "bill"
            val title = data["title"] ?: "Notification"
            val message = data["message"] ?: "You have a new update."
            sendNotification(applicationContext, title, message, type)
        }
    }

    companion object {
        // Helper function to send notifications
        fun sendNotification(context: Context, title: String, message: String, type: String? = null) {
            // Check notification permission for Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted, skip notification
                return
            }

            // Create an intent to open the app (or a specific screen if needed)
            val intent = Intent(context, MainActivity::class.java).apply {
                // Add extra data to navigate to a specific screen
                type?.let { putExtra("type", it) }
            }

            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val channelId = "zabapp1_notifications"
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = NotificationManagerCompat.from(context)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId, "ZabApp Notifications", NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }

            // Display the notification
            notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
        }
    }
}
