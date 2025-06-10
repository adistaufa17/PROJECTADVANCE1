package com.adista.projectadvance1

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.adista.projectadvance1.core.network.ApiService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Call
import retrofit2.Callback

@HiltAndroidApp
class MyApplication : Application()

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Notifikasi"
        val body = remoteMessage.notification?.body ?: ""
        val channelId = "poke_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Colek Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", "New token: $token")
        // Kirim token ini ke server saat login
    }
}

