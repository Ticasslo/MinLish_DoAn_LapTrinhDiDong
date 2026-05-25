package com.example.englishapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Source of truth cho SRS — lưu local là chính
// Sync lên Firestore sau mỗi phiên học
@Entity(tableName = "srs_cards")
data class SrsCardEntity(

    // PK - giống cardId trên Firestore
    // KHÔNG được null, unique
    @PrimaryKey
    val cardId: String,

    // FK → users.userId
    // KHÔNG được null
    val userId: String,

    // FK → words.wordId (1 word có 1 card per user)
    // KHÔNG được null
    val wordId: String,

    // FK → vocabulary_sets.setId
    // KHÔNG được null
    val setId: String,

    // Hệ thống tự cập nhật sau mỗi lần review
    // KHÔNG được null
    // Chỉ được 1 trong: "new", "learning", "mastered"
    // Ví dụ: "learning"
    val status: String = "new",

    // Hệ thống tự tính theo SM-2
    // KHÔNG được null, từ 1.3 đến 2.5
    // Ví dụ: 2.5
    val easeFactor: Double = 2.5,

    // Hệ thống tự tính theo SM-2, đơn vị ngày
    // KHÔNG được null, >= 1
    // Ví dụ: 4
    val interval: Int = 1,

    // Hệ thống tự đếm số lần ôn thành công
    // KHÔNG được null, >= 0
    // Ví dụ: 3
    val repetitions: Int = 0,

    // Hệ thống tự tính = lastReview + interval * 86400000
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val nextReview: Long = System.currentTimeMillis(),

    // Hệ thống tự ghi khi người dùng rating
    // Null nếu chưa review lần nào
    // Ví dụ: 1716825600000
    val lastReview: Long? = null,

    // Hệ thống tự ghi khi người dùng chọn rating
    // Null nếu chưa review lần nào
    // Chỉ được 1 trong: "again", "hard", "good", "easy"
    // Ví dụ: "good"
    val lastRating: String? = null
)