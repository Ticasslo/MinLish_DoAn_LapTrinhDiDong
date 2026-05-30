package com.example.englishapp.core.data.local.dao

import androidx.room.*
import com.example.englishapp.core.data.local.entity.SrsCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SrsCardDao {

    // Lấy tất cả card đến hạn ôn hôm nay
    // nextReview <= thời điểm hiện tại và status != "new"
    @Query("""
        SELECT * FROM srs_cards 
        WHERE userId = :userId 
        AND nextReview <= :now
        AND status != 'new'
        ORDER BY nextReview ASC
    """)
    fun getDueCards(userId: String, now: Long): Flow<List<SrsCardEntity>>

    // Lấy card theo wordId + userId
    @Query("SELECT * FROM srs_cards WHERE wordId = :wordId AND userId = :userId")
    suspend fun getCardByWordId(wordId: String, userId: String): SrsCardEntity?

    // Lấy tất cả card trong 1 set
    @Query("SELECT * FROM srs_cards WHERE setId = :setId AND userId = :userId")
    fun getCardsBySetId(setId: String, userId: String): Flow<List<SrsCardEntity>>

    // Lấy từ mới chưa học trong 1 set (giới hạn theo dailyGoal)
    @Query("""
        SELECT * FROM srs_cards 
        WHERE setId = :setId 
        AND userId = :userId 
        AND status = 'new'
        LIMIT :limit
    """)
    suspend fun getNewCards(setId: String, userId: String, limit: Int): List<SrsCardEntity>

    // Đếm số từ theo status trong 1 set
    @Query("""
        SELECT COUNT(*) FROM srs_cards 
        WHERE setId = :setId 
        AND userId = :userId 
        AND status = :status
    """)
    suspend fun countByStatus(setId: String, userId: String, status: String): Int

    // Thêm hoặc cập nhật card sau mỗi lần review
    @Upsert
    suspend fun upsertCard(card: SrsCardEntity)

    // Thêm nhiều card cùng lúc (khi thêm từ mới vào bộ)
    @Upsert
    suspend fun upsertCards(cards: List<SrsCardEntity>)

    // Xoá card khi xoá từ
    @Query("DELETE FROM srs_cards WHERE wordId = :wordId")
    suspend fun deleteCardByWordId(wordId: String)

    @Query("SELECT * FROM srs_cards WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedCards(userId: String): List<SrsCardEntity>

    @Query("UPDATE srs_cards SET isSynced = 1, updatedAt = :updatedAt WHERE cardId = :cardId")
    suspend fun markAsSynced(cardId: String, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM srs_cards WHERE userId = :userId")
    fun getAllCards(userId: String): Flow<List<SrsCardEntity>>

    // Đếm số từ đã học/ôn trong khoảng thời gian
    @Query("""
        SELECT COUNT(*) FROM srs_cards 
        WHERE userId = :userId 
        AND lastReview >= :startOfDay 
        AND lastReview <= :endOfDay
        AND status != 'new'
    """)
    fun getStudiedCardsCountInRange(userId: String, startOfDay: Long, endOfDay: Long): Flow<Int>

    // === Home screen: đếm card đến hạn theo từng set ===
    @Query("""
        SELECT setId, COUNT(*) as dueCount FROM srs_cards 
        WHERE userId = :userId AND nextReview <= :now AND status != 'new'
        GROUP BY setId HAVING dueCount > 0
        ORDER BY dueCount DESC
    """)
    fun getDueCountPerSet(userId: String, now: Long): Flow<List<SetDueCount>>

    // === Home screen: đếm card mới (chưa học) theo từng set ===
    @Query("""
        SELECT setId, COUNT(*) as newCount FROM srs_cards 
        WHERE userId = :userId AND status = 'new'
        GROUP BY setId HAVING newCount > 0
        ORDER BY newCount DESC
    """)
    fun getNewCountPerSet(userId: String): Flow<List<SetNewCount>>
}

data class SetDueCount(val setId: String, val dueCount: Int)
data class SetNewCount(val setId: String, val newCount: Int)