package com.example.englishapp.core.data.local.dao

import androidx.room.*
import com.example.englishapp.core.data.local.entity.StudySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudySessionDao {

    // Lấy tất cả session của 1 user, mới nhất trước
    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY date DESC")
    fun getSessionsByUserId(userId: String): Flow<List<StudySessionEntity>>

    // Lấy session trong khoảng thời gian (cho biểu đồ 7 ngày / 30 ngày)
    @Query("""
        SELECT * FROM study_sessions 
        WHERE userId = :userId 
        AND date >= :from 
        AND date <= :to
        ORDER BY date ASC
    """)
    fun getSessionsInRange(userId: String, from: Long, to: Long): Flow<List<StudySessionEntity>>

    // Lấy session chưa sync để push lên Firestore
    @Query("SELECT * FROM study_sessions WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedSessions(userId: String): List<StudySessionEntity>

    // Thêm session mới sau khi hoàn thành phiên học
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    // Đánh dấu đã sync xong
    @Query("UPDATE study_sessions SET isSynced = 1 WHERE sessionId = :sessionId")
    suspend fun markAsSynced(sessionId: String)

    // Xoá session đã sync (dọn dẹp local)
    @Query("DELETE FROM study_sessions WHERE isSynced = 1 AND userId = :userId")
    suspend fun deleteSyncedSessions(userId: String)

    // Tính accuracy trung bình của 1 set
    @Query("SELECT AVG(accuracy) FROM study_sessions WHERE setId = :setId AND userId = :userId")
    suspend fun getAverageAccuracy(setId: String, userId: String): Double?

    // Lấy session cuối cùng của user (kiểm tra streak)
    @Query("SELECT * FROM study_sessions WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLastSession(userId: String): StudySessionEntity?

    // === Home screen: session gần nhất của mỗi set ===
    @Query("""
        SELECT s.* FROM study_sessions s
        INNER JOIN (
            SELECT setId, MAX(date) as maxDate 
            FROM study_sessions 
            WHERE userId = :userId 
            GROUP BY setId
        ) latest ON s.setId = latest.setId AND s.date = latest.maxDate
        WHERE s.userId = :userId
        ORDER BY s.date DESC
        LIMIT :limit
    """)
    fun getRecentSessionsPerSet(userId: String, limit: Int = 5): Flow<List<StudySessionEntity>>
}