package com.example.englishapp.core.data.model

data class StudySession(
    // PK - do Firestore auto ID tạo tự động
    // KHÔNG được null, unique
    // Ví dụ: "session_abc123"
    val sessionId: String = "",

    // FK → users.userId (1 user có nhiều sessions)
    // KHÔNG được null
    // Ví dụ: "uid_abc123"
    val userId: String = "",

    // FK → vocabulary_sets.setId (1 set có nhiều sessions)
    // KHÔNG được null
    // Ví dụ: "set_abc123"
    val setId: String = "",

    // Hệ thống tự xác định khi bắt đầu phiên học
    // KHÔNG được null, chỉ được 1 trong: "new", "review"
    // "new" = học từ mới (status = "new")
    // "review" = ôn từ đến hạn (status = "learning"/"mastered")
    // Ví dụ: "review"
    val sessionType: String = "",

    // Hệ thống tự ghi khi bắt đầu phiên học
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val date: Long = System.currentTimeMillis(),

    // Hệ thống tự đếm số từ trong phiên
    // KHÔNG được null, >= 1
    // Ví dụ: 20
    val wordsStudied: Int = 0,

    // Hệ thống tự tính = (goodCount + easyCount) / wordsStudied * 100
    // KHÔNG được null, 0.0 đến 100.0
    // Ví dụ: 85.0
    val accuracy: Double = 0.0,

    // Hệ thống tự tính = thời điểm kết thúc - thời điểm bắt đầu
    // KHÔNG được null, đơn vị giây, >= 0
    // Ví dụ: 300 (5 phút)
    val duration: Int = 0,

    // Hệ thống tự đếm số lần người dùng bấm "Again"
    // KHÔNG được null, >= 0
    // Ví dụ: 3
    val againCount: Int = 0,

    // Hệ thống tự đếm số lần người dùng bấm "Hard"
    // KHÔNG được null, >= 0
    // Ví dụ: 5
    val hardCount: Int = 0,

    // Hệ thống tự đếm số lần người dùng bấm "Good"
    // KHÔNG được null, >= 0
    // Ví dụ: 8
    val goodCount: Int = 0,

    // Hệ thống tự đếm số lần người dùng bấm "Easy"
    // KHÔNG được null, >= 0
    // Ví dụ: 4
    val easyCount: Int = 0,

    val updatedAt: Long = System.currentTimeMillis()
)