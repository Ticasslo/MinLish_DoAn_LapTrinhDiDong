package com.example.englishapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "notifications",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["createdAt"])
    ]
)
data class NotificationEntity(
    @PrimaryKey
    val notificationId: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
