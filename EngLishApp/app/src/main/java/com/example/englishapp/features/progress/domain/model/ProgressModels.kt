package com.example.englishapp.features.progress.domain.model

data class ProgressStats(
    val streak: Int = 0,
    val totalWords: Int = 0,
    val accuracy: Int = 0,
    val level: String = "Intermediate B1",
    val levelProgress: Float = 0.64f
)

data class DailyActivity(
    val dayName: String, // T2, T3, ...
    val activityLevel: Float, // 0.0 to 1.0
    val isToday: Boolean = false
)

data class WordStatus(
    val total: Int = 0, // Tổng số từ vựng
    val mastered: Int = 0, // Từ vựng đã học
    val learning: Int = 0, // Từ vựng đang học
    val new: Int = 0 // Từ vựng mới
)

data class SetRetention(
    val setName: String,
    val retentionRate: Int, // Từ vựng đã học trên tổng số từ vựng
    val iconType: String // "business", "travel", "academic"...
)
