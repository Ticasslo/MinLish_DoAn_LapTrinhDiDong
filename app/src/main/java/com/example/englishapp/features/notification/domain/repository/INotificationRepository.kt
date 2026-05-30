package com.example.englishapp.features.notification.domain.repository

import com.example.englishapp.features.notification.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface INotificationRepository {
    fun getNotifications(): Flow<List<Notification>>
    suspend fun markAsRead(notificationId: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(notificationId: String)
}