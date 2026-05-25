package com.example.englishapp.core.data.model

data class Streak(
    // PK - 1 user chỉ có 1 streak document
    // KHÔNG được null, unique
    // Ví dụ: "streak_uid_abc123"
    val streakId: String = "",

    // FK → users.userId (1 user có đúng 1 streak)
    // KHÔNG được null, unique
    // Ví dụ: "uid_abc123"
    val userId: String = "",

    // Hệ thống tự tính mỗi ngày người dùng học
    // Reset về 0 nếu bỏ 1 ngày
    // KHÔNG được null, >= 0
    // Ví dụ: 12
    val currentStreak: Int = 0,

    // Hệ thống tự cập nhật khi currentStreak > longestStreak
    // KHÔNG được null, >= 0, >= currentStreak
    // Ví dụ: 30
    val longestStreak: Int = 0,

    // Hệ thống tự ghi mỗi khi hoàn thành phiên học
    // Có thể 0 nếu chưa học lần nào
    // Ví dụ: 1716825600000
    val lastStudyDate: Long? = null,

    // Hệ thống tự thêm timestamp mỗi ngày học
    // Có thể rỗng nếu chưa học lần nào
    // Ví dụ: [1716739200000, 1716825600000, 1716912000000]
    val streakHistory: List<Long> = emptyList()
)