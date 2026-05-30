package com.example.englishapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streaks")
data class StreakEntity(
    @PrimaryKey val userId: String,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStudyDate: Long = 0,
    val streakHistory: List<Long>, // Đảm bảo đã có TypeConverter trong AppDatabase
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)