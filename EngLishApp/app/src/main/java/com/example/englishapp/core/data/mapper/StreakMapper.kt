package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.Streak
import com.example.englishapp.core.data.local.entity.StreakEntity

fun StreakEntity.toDomain(): Streak {
    return Streak(
        userId = this.userId,
        currentStreak = this.currentStreak,
        longestStreak = this.longestStreak,
        lastStudyDate = this.lastStudyDate,
        streakHistory = this.streakHistory
    )
}

fun Streak.toEntity(): StreakEntity {
    return StreakEntity(
        userId = this.userId,
        currentStreak = this.currentStreak,
        longestStreak = this.longestStreak,
        lastStudyDate = this.lastStudyDate ?: 0L,
        streakHistory = this.streakHistory
    )
}
