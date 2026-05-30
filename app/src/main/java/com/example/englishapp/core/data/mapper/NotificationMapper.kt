package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.Notification
import com.example.englishapp.core.data.local.entity.NotificationEntity

fun NotificationEntity.toDomain(): Notification {
    return Notification(
        notificationId = this.notificationId,
        userId = this.userId,
        title = this.title,
        body = this.body,
        type = this.type,
        isRead = this.isRead,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Notification.toEntity(): NotificationEntity {
    return NotificationEntity(
        notificationId = this.notificationId,
        userId = this.userId,
        title = this.title,
        body = this.body,
        type = this.type,
        isRead = this.isRead,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = false // Đảm bảo tính tường minh cho logic Offline-first
    )
}
