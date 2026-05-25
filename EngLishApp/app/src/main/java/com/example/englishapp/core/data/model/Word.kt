package com.example.englishapp.core.data.model

data class Word(
    // PK - do Firestore auto ID tạo tự động
    // KHÔNG được null, unique
    // Ví dụ: "word_abc123"
    val wordId: String = "",

    // FK → vocabulary_sets.setId (1 set có nhiều words)
    // KHÔNG được null
    // Ví dụ: "set_abc123"
    val setId: String = "",

    // FK → users.userId (để query nhanh không cần qua set)
    // KHÔNG được null
    // Ví dụ: "uid_abc123"
    val userId: String = "",

    // Người dùng nhập, BẮT BUỘC
    // KHÔNG được null hoặc rỗng
    // Ví dụ: "accomplish"
    val word: String = "",

    // Người dùng nhập, tuỳ chọn
    // Có thể rỗng
    // Ví dụ: "/əˈkʌmplɪʃ/"
    val pronunciation: String? = null,

    // Người dùng nhập, BẮT BUỘC
    // KHÔNG được null hoặc rỗng
    // Ví dụ: "hoàn thành, đạt được"
    val meaning: String = "",

    // Người dùng nhập, tuỳ chọn, tiếng Anh
    // Có thể rỗng
    // Ví dụ: "to succeed in doing or completing something"
    val description: String? = null,

    // Người dùng nhập, tuỳ chọn
    // Có thể rỗng
    // Ví dụ: "She accomplished her goal of running a marathon."
    val example: String? = null,

    // Người dùng nhập, tuỳ chọn
    // Có thể rỗng
    // Ví dụ: "accomplish a task / accomplish a mission"
    val collocation: String? = null,

    // Người dùng nhập, tuỳ chọn
    // Có thể rỗng
    // Ví dụ: ["achievement", "completion", "attainment"]
    val relatedWords: List<String> = emptyList(),

    // Người dùng nhập, tuỳ chọn
    // Có thể rỗng
    // Ví dụ: "Thường dùng trong văn viết trang trọng"
    val note: String? = null,

    // Hệ thống tự tạo
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val createdAt: Long = System.currentTimeMillis()
)