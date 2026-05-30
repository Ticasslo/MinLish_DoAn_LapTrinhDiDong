package com.example.englishapp.features.home.domain.model

/**
 * Bộ từ vựng có card SRS đến hạn ôn tập — hiển thị trong mục "Cần ôn ngay"
 */
data class HomeReviewDeck(
    val setId: String,
    val name: String,
    val dueCount: Int,
    val tags: List<String>
)

/**
 * Bộ từ vựng có card mới chưa học — hiển thị trong mục "Từ mới hôm nay"
 */
data class HomeNewWordDeck(
    val setId: String,
    val name: String,
    val newCount: Int,
    val tags: List<String>
)

/**
 * Bộ từ vựng đã học gần đây — hiển thị trong mục "Gần đây"
 */
data class HomeRecentDeck(
    val setId: String,
    val name: String,
    val lastStudiedAt: Long,
    val masteredPercent: Int  // (masteredCount / wordCount) * 100
)
