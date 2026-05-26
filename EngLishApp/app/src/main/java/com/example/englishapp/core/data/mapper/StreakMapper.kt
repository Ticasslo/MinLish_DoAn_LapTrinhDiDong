package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.Streak
import com.example.englishapp.core.data.local.entity.StreakEntity

fun StreakEntity.toDomain(): Streak {
    return Streak(
        // Firestore document ID thường có format này để dễ quản lý
        streakId = "streak_${this.userId}", 
        userId = this.userId,
        currentStreak = this.currentStreak,
        longestStreak = this.longestStreak,
        // Chuyển 0L về null để UI hiểu là chưa có dữ liệu ngày học
        lastStudyDate = if (this.lastStudyDate == 0L) null else this.lastStudyDate,
        streakHistory = this.streakHistory,
        updatedAt = this.updatedAt
    )
}

fun Streak.toEntity(): StreakEntity {
    return StreakEntity(
        userId = this.userId,
        currentStreak = this.currentStreak,
        longestStreak = this.longestStreak,
        lastStudyDate = this.lastStudyDate ?: 0L,
        streakHistory = this.streakHistory,
        updatedAt = this.updatedAt,
        isSynced = false // Đánh dấu là thay đổi local, cần sync lên Firestore
    )
}
