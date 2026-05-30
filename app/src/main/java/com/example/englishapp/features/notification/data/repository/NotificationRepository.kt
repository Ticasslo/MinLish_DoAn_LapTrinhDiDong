package com.example.englishapp.features.notification.data.repository

import com.example.englishapp.features.notification.domain.model.Notification
import com.example.englishapp.features.notification.domain.model.NotificationType
import com.example.englishapp.features.notification.domain.repository.INotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Date
import javax.inject.Inject

class NotificationRepository @Inject constructor() : INotificationRepository {
    
    //TODO: Tạm thời giả lập dữ liệu vì Firestore chưa có collection notifications thực tế
    private val mockNotifications = listOf(
        Notification(
            id = "1",
            title = "Đến giờ ôn tập rồi!",
            message = "Bạn có 15 thẻ từ vựng \"Động từ bất quy tắc\" cần ôn tập để duy trì chuỗi học. Đừng bỏ lỡ nhé!",
            type = NotificationType.REVIEW_REMINDER,
            isRead = false,
            timestamp = Date(System.currentTimeMillis() - 10 * 60 * 1000) // 10 mins ago
        ),
        Notification(
            id = "2",
            title = "Cập nhật khóa học mới",
            message = "Khóa học \"Giao tiếp công sở cơ bản\" đã có sẵn trong thư viện. Khám phá ngay các bài học mới.",
            type = NotificationType.UPDATE,
            isRead = false,
            timestamp = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000) // 2 hours ago
        ),
        Notification(
            id = "3",
            title = "Chuỗi 7 ngày học liên tiếp!",
            message = "Tuyệt vời! Bạn đã duy trì thói quen học tiếng Anh suốt 1 tuần qua. Cố gắng phát huy nhé.",
            type = NotificationType.ACHIEVEMENT,
            isRead = true,
            timestamp = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000) // Yesterday
        ),
        Notification(
            id = "4",
            title = "Đã hoàn thành ôn tập",
            message = "Bạn đã học xong 20 thẻ từ vựng của chủ đề \"Du lịch\".",
            type = NotificationType.SYSTEM,
            isRead = true,
            timestamp = Date(System.currentTimeMillis() - 48 * 60 * 60 * 1000) // 2 days ago
        )
    )

    override fun getNotifications(): Flow<List<Notification>> {
        return flowOf(mockNotifications)
    }

    override suspend fun markAsRead(notificationId: String) {
        // Implement Firestore update here later
    }

    override suspend fun markAllAsRead() {
        // Implement Firestore update here later
    }

    override suspend fun deleteNotification(notificationId: String) {
        // Implement Firestore delete here later
    }
}