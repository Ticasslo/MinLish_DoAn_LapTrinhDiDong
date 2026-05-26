package com.example.englishapp.core.data.local.dao

import androidx.room.*
import com.example.englishapp.core.data.local.entity.StreakEntity

@Dao
interface StreakDao {
    @Query("SELECT * FROM streaks WHERE userId = :userId")
    suspend fun getStreak(userId: String): StreakEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: StreakEntity)

    @Query("""
        UPDATE streaks 
        SET currentStreak = :currentStreak, longestStreak = :longestStreak, 
            lastStudyDate = :lastStudyDate, streakHistory = :streakHistory, 
            updatedAt = :updatedAt, isSynced = 0
        WHERE userId = :userId
    """)
    suspend fun updateStreak(userId: String, currentStreak: Int, longestStreak: Int, lastStudyDate: Long, streakHistory: List<Long>, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE streaks SET isSynced = 1 WHERE userId = :userId")
    suspend fun markStreakAsSynced(userId: String)
}
