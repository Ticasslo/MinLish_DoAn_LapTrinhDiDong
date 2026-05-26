package com.example.englishapp.core.data.model

data class SrsCard(
    // PK - do Firestore auto ID tạo tự động
    // KHÔNG được null, unique
    // Ví dụ: "card_abc123"
    val cardId: String = "",

    // FK → users.userId (1 user có nhiều cards)
    // KHÔNG được null
    // Ví dụ: "uid_abc123"
    val userId: String = "",

    // FK → words.wordId (1 word có 1 card per user)
    // KHÔNG được null
    // Ví dụ: "word_abc123"
    val wordId: String = "",

    // FK → vocabulary_sets.setId (để query card theo set)
    // KHÔNG được null
    // Ví dụ: "set_abc123"
    val setId: String = "",

    // Hệ thống tự cập nhật sau mỗi lần review
    // KHÔNG được null, chỉ được 1 trong: "new", "learning", "mastered"
    // "new" = chưa học lần nào
    // "learning" = đang học, repetitions < 3
    // "mastered" = đã thuộc, repetitions >= 3
    // Ví dụ: "learning"
    val status: String = "new",

    // Hệ thống tự tính theo thuật toán SM-2
    // KHÔNG được null, từ 1.3 đến 2.5
    // Càng cao → interval càng dài → từ càng dễ nhớ
    // Ví dụ: 2.5
    val easeFactor: Double = 2.5,

    // Hệ thống tự tính theo SM-2, đơn vị ngày
    // KHÔNG được null, >= 1
    // Ví dụ: 4 (ôn lại sau 4 ngày)
    val interval: Int = 1,

    // Hệ thống tự đếm số lần đã ôn thành công (good/easy)
    // KHÔNG được null, >= 0
    // Ví dụ: 3
    val repetitions: Int = 0,

    // Hệ thống tự tính = lastReview + interval * 86400000
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val nextReview: Long = System.currentTimeMillis(),

    // Hệ thống tự ghi khi người dùng rating
    // Có thể 0 nếu chưa review lần nào
    // Ví dụ: 1716825600000
    val lastReview: Long? = null,

    // Hệ thống tự ghi khi người dùng chọn rating
    // Có thể rỗng nếu chưa review lần nào
    // Chỉ được 1 trong: "again", "hard", "good", "easy"
    // Ví dụ: "good"
    val lastRating: String? = null,

    val updatedAt: Long = System.currentTimeMillis()
)