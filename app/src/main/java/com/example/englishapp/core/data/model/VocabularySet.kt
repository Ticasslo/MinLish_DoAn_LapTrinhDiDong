package com.example.englishapp.core.data.model

data class VocabularySet(
    // PK - do hệ thống tự tạo (UUID hoặc Firestore auto ID)
    // KHÔNG được null, unique
    // Ví dụ: "set_abc123"
    val setId: String = "",

    // FK → users.userId (1 user có nhiều sets)
    // KHÔNG được null
    // Ví dụ: "uid_abc123"
    val userId: String = "",

    // Người dùng nhập khi tạo bộ từ
    // KHÔNG được null, 1-100 ký tự
    // Ví dụ: "IELTS Academic Vocabulary"
    val name: String = "",

    // Người dùng nhập khi tạo bộ từ, tuỳ chọn
    // Có thể rỗng
    // Ví dụ: "Từ vựng IELTS band 7.0+"
    val description: String = "",

    // Người dùng chọn khi tạo bộ từ
    // Có thể rỗng, mỗi tag chỉ được 1 trong: "IELTS","TOEIC","Business","Travel","Communication"
    // Ví dụ: ["IELTS", "Communication"]
    val tags: List<String> = emptyList(),

    // Hệ thống tự tính = masteredCount + learningCount + newCount
    // KHÔNG được null, >= 0
    // Ví dụ: 150
    val wordCount: Int = 0,

    // Hệ thống tự tính từ srs_cards.status = "mastered" của set này
    // KHÔNG được null, >= 0, <= wordCount
    // Ví dụ: 80
    val masteredCount: Int = 0,

    // Hệ thống tự tính từ srs_cards.status = "learning" của set này
    // KHÔNG được null, >= 0, <= wordCount
    // Ví dụ: 50
    val learningCount: Int = 0,

    // Hệ thống tự tính từ srs_cards.status = "new" của set này
    // KHÔNG được null, >= 0, <= wordCount
    // Ví dụ: 20
    val newCount: Int = 0,

    // Hệ thống tự tạo khi tạo bộ từ
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val createdAt: Long = System.currentTimeMillis(),

    // Hệ thống tự cập nhật mỗi khi thêm/sửa/xoá từ
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val updatedAt: Long = System.currentTimeMillis()
)