package com.example.englishapp.features.notification.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object NotificationScheduler {
    const val DAILY_REMINDER_WORK_NAME = "daily_reminder_work"
    const val REVIEW_DUE_WORK_NAME = "review_due_work"

    fun scheduleDailyReminder(context: Context, timeString: String) {
        val parts = timeString.split(":")
        if (parts.size != 2) return

        val targetHour = parts[0].toIntOrNull() ?: return
        val targetMinute = parts[1].toIntOrNull() ?: return

        val now = Calendar.getInstance()

        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Nếu thời gian mục tiêu đã qua trong ngày hôm nay, cộng thêm 1 ngày
        if (targetTime.before(now)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        val initialDelay = targetTime.timeInMillis - now.timeInMillis

        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_REMINDER_WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }

    // ReviewDueWorker (chạy mỗi 6 tiếng)
    fun scheduleReviewDueNotification(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<ReviewDueWorker>(6, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            REVIEW_DUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Dùng KEEP để không làm gián đoạn nếu đã có lịch
            workRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
    }

    fun cancelReviewDueNotification(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(REVIEW_DUE_WORK_NAME)
    }
}
