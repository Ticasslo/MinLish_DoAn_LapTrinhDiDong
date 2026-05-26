package com.example.englishapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Lưu tạm local sau mỗi phiên học
// Sync lên Firestore khi có mạng → xoá local
@Entity(
    tableName = "study_sessions",
    indices = [
        androidx.room.Index(value = ["userId"]),
        androidx.room.Index(value = ["setId"]),
        androidx.room.Index(value = ["date"]),
        androidx.room.Index(value = ["isSynced"])
    ]
)
data class StudySessionEntity(

    // PK - do hệ thống tạo (UUID)
    // KHÔNG được null, unique
    @PrimaryKey
    val sessionId: String,

    // FK → users.userId
    // KHÔNG được null
    val userId: String,

    // FK → vocabulary_sets.setId
    // KHÔNG được null
    val setId: String,

    // Hệ thống tự xác định khi bắt đầu phiên
    // KHÔNG được null
    // Chỉ được 1 trong: "new", "review"
    // Ví dụ: "review"
    val sessionType: String,

    // Hệ thống tự ghi khi bắt đầu phiên
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val date: Long = System.currentTimeMillis(),

    // Hệ thống tự đếm
    // KHÔNG được null, >= 1
    // Ví dụ: 20
    val wordsStudied: Int = 0,

    // Hệ thống tự tính = (goodCount + easyCount) / wordsStudied * 100
    // KHÔNG được null, 0.0 đến 100.0
    // Ví dụ: 85.0
    val accuracy: Double = 0.0,

    // Hệ thống tự tính = thời điểm kết thúc - bắt đầu
    // KHÔNG được null, đơn vị giây
    // Ví dụ: 300
    val duration: Int = 0,

    // Hệ thống tự đếm số lần bấm "Again"
    // KHÔNG được null, >= 0
    // Ví dụ: 3
    val againCount: Int = 0,

    // Hệ thống tự đếm số lần bấm "Hard"
    // KHÔNG được null, >= 0
    // Ví dụ: 5
    val hardCount: Int = 0,

    // Hệ thống tự đếm số lần bấm "Good"
    // KHÔNG được null, >= 0
    // Ví dụ: 8
    val goodCount: Int = 0,

    // Hệ thống tự đếm số lần bấm "Easy"
    // KHÔNG được null, >= 0
    // Ví dụ: 4
    val easyCount: Int = 0,

    // Flag sync — true = đã sync lên Firestore → có thể xoá local
    // KHÔNG được null, default false
    val isSynced: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)