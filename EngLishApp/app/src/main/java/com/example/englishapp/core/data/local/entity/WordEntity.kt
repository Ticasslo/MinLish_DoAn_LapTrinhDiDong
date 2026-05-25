package com.example.englishapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Cache từ Firestore xuống local
// Sync theo setId khi mở VocabListFragment
@Entity(
    tableName = "words",
    indices = [
        androidx.room.Index(value = ["setId"]),
        androidx.room.Index(value = ["userId"])
    ]
)
data class WordEntity(

    // PK - giống wordId trên Firestore
    // KHÔNG được null, unique
    @PrimaryKey
    val wordId: String,

    // FK → vocabulary_sets.setId
    // KHÔNG được null
    val setId: String,

    // FK → users.userId
    // KHÔNG được null
    val userId: String,

    // BẮT BUỘC nhập
    // KHÔNG được null hoặc rỗng
    // Ví dụ: "accomplish"
    val word: String,

    // Tuỳ chọn
    // Có thể null
    // Ví dụ: "/əˈkʌmplɪʃ/"
    val pronunciation: String? = null,

    // BẮT BUỘC nhập
    // KHÔNG được null hoặc rỗng
    // Ví dụ: "hoàn thành, đạt được"
    val meaning: String,

    // Tuỳ chọn
    // Có thể null
    // Ví dụ: "to succeed in doing something"
    val description: String? = null,

    // Tuỳ chọn
    // Có thể null
    // Ví dụ: "She accomplished her goal."
    val example: String? = null,

    // Tuỳ chọn
    // Có thể null
    // Ví dụ: "accomplish a task / accomplish a mission"
    val collocation: String? = null,

    // Tuỳ chọn, dùng Converters để lưu List
    // Có thể rỗng
    // Ví dụ: ["achievement", "completion"]
    val relatedWords: List<String> = emptyList(),

    // Tuỳ chọn
    // Có thể null
    // Ví dụ: "Thường dùng trong văn viết trang trọng"
    val note: String? = null,

    // Hệ thống tự tạo
    // KHÔNG được null
    val createdAt: Long = System.currentTimeMillis()
)