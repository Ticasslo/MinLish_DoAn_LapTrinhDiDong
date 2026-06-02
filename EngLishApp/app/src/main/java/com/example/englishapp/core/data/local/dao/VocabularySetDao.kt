package com.example.englishapp.core.data.local.dao

import androidx.room.*
import com.example.englishapp.core.data.local.entity.VocabularySetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularySetDao {
    @Query("SELECT * FROM vocabulary_sets WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedSets(userId: String): List<VocabularySetEntity>

    @Query("SELECT * FROM vocabulary_sets WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeSets(userId: String): Flow<List<VocabularySetEntity>>

    @Query("SELECT * FROM vocabulary_sets WHERE setId = :setId")
    suspend fun getSetById(setId: String): VocabularySetEntity?

    @Query("""
        UPDATE vocabulary_sets 
        SET wordCount = :wordCount, masteredCount = :masteredCount, 
            learningCount = :learningCount, newCount = :newCount, 
            updatedAt = :updatedAt, isSynced = 0
        WHERE setId = :setId
    """)
    suspend fun updateCounts(setId: String, wordCount: Int, masteredCount: Int, learningCount: Int, newCount: Int, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE vocabulary_sets SET isSynced = 1 WHERE setId = :setId")
    suspend fun markAsSynced(setId: String)

    @Query("SELECT * FROM vocabulary_sets WHERE setId IN (:setIds)")
    fun observeSetsByIds(setIds: List<String>): Flow<List<VocabularySetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: VocabularySetEntity)

    @Delete
    suspend fun deleteSet(set: VocabularySetEntity)
}
