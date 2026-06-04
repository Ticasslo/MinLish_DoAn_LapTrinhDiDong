package com.example.englishapp.core.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.englishapp.MainActivity
import com.example.englishapp.R

class NotificationHelper(
    private val context: Context
) {
    companion object {
        const val DAILY_REMINDER_CHANNEL_ID = "daily_reminder_channel"
        const val REVIEW_DUE_CHANNEL_ID = "review_due_channel"
        
        const val DAILY_REMINDER_ID = 1001
        const val REVIEW_DUE_ID = 1002
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Cấu hình âm thanh mặc định của hệ thống
            val audioAttributes = android.media.AudioAttributes.Builder()
                .setContentType(android.media.AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(android.media.AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val soundUri = android.provider.Settings.System.DEFAULT_NOTIFICATION_URI

            val dailyChannel = NotificationChannel(
                DAILY_REMINDER_CHANNEL_ID,
                "Nhắc nhở hàng ngày",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(dailyChannel)

            val reviewChannel = NotificationChannel(
                REVIEW_DUE_CHANNEL_ID,
                "Ôn tập đến hạn",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(soundUri, audioAttributes)
                enableVibration(false)
            }
            notificationManager.createNotificationChannel(reviewChannel)
        }
    }

    fun showDailyReminderNotification(title: String, message: String) {
        showNotification(DAILY_REMINDER_ID, DAILY_REMINDER_CHANNEL_ID, title, message)
    }

    fun showReviewDueNotification(title: String, message: String) {
        showNotification(REVIEW_DUE_ID, REVIEW_DUE_CHANNEL_ID, title, message)
    }

    private fun showNotification(notificationId: Int, channelId: String, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, builder.build())
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }
}
