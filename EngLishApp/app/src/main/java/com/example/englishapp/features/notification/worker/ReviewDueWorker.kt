package com.example.englishapp.features.notification.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.englishapp.core.data.local.dao.NotificationDao
import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.entity.NotificationEntity
import com.example.englishapp.core.util.NotificationHelper
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.UUID

@HiltWorker
class ReviewDueWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val authRepository: IAuthRepository,
    private val srsCardDao: SrsCardDao,
    private val notificationDao: NotificationDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val user = authRepository.getCurrentUser()
            
            // Chỉ gửi khi user đã đăng nhập và cho phép nhận thông báo
            if (user != null && user.pushEnabled) {
                // Kiểm tra số lượng từ đến hạn
                val now = System.currentTimeMillis()
                val dueCards = srsCardDao.getDueCards(user.userId, now).first()
                val dueCount = dueCards.size

                if (dueCount > 0) {
                    val title = "Đến giờ ôn tập!"
                    val message = "Bạn có $dueCount từ vựng cần ôn tập ngay. Đừng quên nhé!"

                    // 1. Hiển thị thông báo đẩy
                    notificationHelper.showReviewDueNotification(title, message)

                    // 2. Lưu vào DB để hiển thị trong mục thông báo trong app
                    val notificationEntity = NotificationEntity(
                        notificationId = UUID.randomUUID().toString(),
                        userId = user.userId,
                        title = title,
                        body = message,
                        type = "REVIEW_DUE",
                        isRead = false,
                        isSynced = false,
                        createdAt = System.currentTimeMillis()
                    )
                    notificationDao.insertNotification(notificationEntity)
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
