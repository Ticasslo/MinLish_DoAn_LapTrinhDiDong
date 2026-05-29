package com.example.englishapp.features.notification.domain.model

import java.util.Date

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: NotificationType = NotificationType.SYSTEM,
    val isRead: Boolean = false,
    val timestamp: Date = Date(),
    val actionData: String? = null
)

enum class NotificationType {
    REVIEW_REMINDER,
    SYSTEM,
    ACHIEVEMENT,
    UPDATE
}