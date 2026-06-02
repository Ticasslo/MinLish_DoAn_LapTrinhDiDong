package com.example.englishapp.features.notification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.englishapp.core.data.local.dao.NotificationDao
import com.example.englishapp.core.data.local.entity.NotificationEntity
import com.example.englishapp.core.util.NotificationHelper
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class DailyReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: IAuthRepository,
    private val notificationDao: NotificationDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val user = authRepository.getCurrentUser()
            // Chỉ gửi khi user đã đăng nhập và cho phép nhận thông báo
            if (user != null && user.pushEnabled) {
                val title = "Đến giờ học tiếng Anh rồi!"
                val message = "Hãy dành chút thời gian để hoàn thành mục tiêu ${user.dailyGoal} từ vựng hôm nay nhé."

                // 1. Hiển thị thông báo đẩy (Push Notification)
                notificationHelper.showDailyReminderNotification(title, message)

                // 2. Lưu vào DB để hiển thị trong app
                val notificationEntity = NotificationEntity(
                    notificationId = UUID.randomUUID().toString(),
                    userId = user.userId,
                    title = title,
                    body = message,
                    type = "REVIEW_REMINDER",
                    isRead = false,
                    isSynced = false,
                    createdAt = System.currentTimeMillis()
                )
                notificationDao.insertNotification(notificationEntity)
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}