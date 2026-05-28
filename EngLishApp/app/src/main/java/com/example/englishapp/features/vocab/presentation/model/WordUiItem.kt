package com.example.englishapp.features.vocab.presentation.model

import com.example.englishapp.core.data.model.Word

/**
 * Lớp WordUiItem dùng để biểu diễn từ vựng cùng với trạng thái học tập SRS hiện tại
 * (new, learning, mastered) để hiển thị chính xác trạng thái lên màn hình VocabListScreen.
 */
data class WordUiItem(
    val word: Word,
    val status: String = "new", // Trạng thái: "new" (Chưa học), "learning" (Đang học), "mastered" (Đã thuộc)
    val nextReviewText: String = "" // Văn bản hiển thị lịch ôn tập tiếp theo (ví dụ: "Ôn sau 2 ngày")
)
