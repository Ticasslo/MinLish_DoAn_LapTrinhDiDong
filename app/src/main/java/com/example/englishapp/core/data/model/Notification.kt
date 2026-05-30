package com.example.englishapp.core.data.model

data class Notification(
    // PK - do Firestore auto ID tạo tự động
    // KHÔNG được null, unique
    // Ví dụ: "notif_abc123"
    val notificationId: String = "",

    // FK → users.userId (1 user có nhiều notifications)
    // KHÔNG được null
    // Ví dụ: "uid_abc123"
    val userId: String = "",

    // Hệ thống tự tạo theo loại thông báo
    // KHÔNG được null, 1-100 ký tự
    // Ví dụ: "Đến giờ học rồi! 📚"
    val title: String = "",

    // Hệ thống tự tạo theo loại thông báo
    // KHÔNG được null
    // Ví dụ: "Bạn có 12 từ cần ôn hôm nay"
    val body: String = "",

    // Hệ thống tự xác định khi tạo thông báo
    // KHÔNG được null, chỉ được 1 trong: "daily_reminder", "review_due"
    // "daily_reminder" = nhắc học hằng ngày theo reminderTime
    // "review_due" = có từ đến hạn ôn
    // Ví dụ: "review_due"
    val type: String = "",

    // Người dùng đọc thì hệ thống cập nhật thành true
    // KHÔNG được null, default false
    // Ví dụ: false
    val isRead: Boolean = false,

    // Hệ thống tự tạo khi push notification
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)