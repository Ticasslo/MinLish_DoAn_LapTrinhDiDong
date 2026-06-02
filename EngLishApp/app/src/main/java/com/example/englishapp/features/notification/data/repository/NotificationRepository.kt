package com.example.englishapp.features.notification.data.repository

import com.example.englishapp.core.data.local.dao.NotificationDao
import com.example.englishapp.core.data.local.entity.NotificationEntity
import com.example.englishapp.features.notification.domain.model.Notification
import com.example.englishapp.features.notification.domain.model.NotificationType
import com.example.englishapp.features.notification.domain.repository.INotificationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val auth: FirebaseAuth
) : INotificationRepository {
    
    private val userId: String get() = auth.currentUser?.uid ?: ""

    override fun getNotifications(): Flow<List<Notification>> {
        return notificationDao.getAllNotifications(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun markAsRead(notificationId: String) {
        notificationDao.markAsRead(notificationId)
    }

    override suspend fun markAllAsRead() {
        notificationDao.markAllAsRead(userId)
    }

    override suspend fun deleteNotification(notificationId: String) {
        notificationDao.deleteNotificationById(notificationId)
    }

    private fun NotificationEntity.toDomain(): Notification {
        return Notification(
            id = this.notificationId,
            title = this.title,
            message = this.body,
            type = when (this.type) {
                "review_due", "REVIEW_REMINDER" -> NotificationType.REVIEW_REMINDER
                "ACHIEVEMENT" -> NotificationType.ACHIEVEMENT
                "UPDATE" -> NotificationType.UPDATE
                else -> NotificationType.SYSTEM
            },
            isRead = this.isRead,
            timestamp = Date(this.createdAt)
        )
    }
}