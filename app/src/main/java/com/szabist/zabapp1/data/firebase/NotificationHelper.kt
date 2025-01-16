package com.szabist.zabapp1.data.firebase

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.szabist.zabapp1.MainActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class NotificationHelper(private val context: Context) {

    // Send in-app notification
    fun sendNotification(title: String, message: String, type: String? = null, id: String? = null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("NotificationHelper", "Permission for notifications is not granted")
                return
            }
        }

        val channelId = "zabapp1_notifications"

        val intent = Intent(context, MainActivity::class.java).apply {
            type?.let { putExtra("type", it) }
            id?.let {
                if (type == "order") {
                    putExtra("orderId", it)
                } else if (type == "bill") {
                    putExtra("billId", it)
                }
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

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

    // Send push notification to a specific user
    fun sendNotificationToUser(userId: String, title: String, message: String) {
        FirebaseDatabase.getInstance()
            .getReference("users/$userId")
            .child("fcmToken")
            .get()
            .addOnSuccessListener { snapshot ->
                val fcmToken = snapshot.getValue(String::class.java)
                if (!fcmToken.isNullOrEmpty()) {
                    // Construct the notification payload
                    val notificationPayload = mapOf(
                        "to" to fcmToken,
                        "notification" to mapOf(
                            "title" to title,
                            "body" to message
                        ),
                        "data" to mapOf(
                            "type" to "order", // Adjust this according to your use case
                            "message" to message
                        )
                    )

                    // Send the notification
                    sendFCMNotification(notificationPayload)
                }
            }
            .addOnFailureListener {
                Log.e("Notification", "Failed to fetch FCM token for user: $userId", it)
            }
    }

    // Helper method to send FCM notification
    private fun sendFCMNotification(payload: Map<String, Any>) {
        val fcmServerKey = "YOUR_SERVER_KEY" // Replace with your Firebase Cloud Messaging server key

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, Gson().toJson(payload))

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "key=$fcmServerKey")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("FCM", "Failed to send notification", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("FCM", "Notification sent successfully: ${response.body?.string()}")
            }
        })
    }
}
