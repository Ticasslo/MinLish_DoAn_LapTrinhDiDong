package com.example.englishapp.core.data.local.dao

import androidx.room.*
import com.example.englishapp.core.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    // Lấy tất cả từ trong 1 bộ từ, sắp xếp theo thời gian tạo
    @Query("SELECT * FROM words WHERE setId = :setId ORDER BY createdAt ASC")
    fun getWordsBySetId(setId: String): Flow<List<WordEntity>>

    // Lấy 1 từ theo wordId
    @Query("SELECT * FROM words WHERE wordId = :wordId")
    suspend fun getWordById(wordId: String): WordEntity?

    // Thêm hoặc cập nhật 1 từ
    @Upsert
    suspend fun upsertWord(word: WordEntity)

    // Thêm hoặc cập nhật nhiều từ (import CSV)
    @Upsert
    suspend fun upsertWords(words: List<WordEntity>)

    // Xoá 1 từ
    @Delete
    suspend fun deleteWord(word: WordEntity)

    // Xoá tất cả từ trong 1 bộ từ (khi xoá bộ từ)
    @Query("DELETE FROM words WHERE setId = :setId")
    suspend fun deleteWordsBySetId(setId: String)

    // Đếm số từ trong 1 bộ từ
    @Query("SELECT COUNT(*) FROM words WHERE setId = :setId")
    suspend fun countWordsBySetId(setId: String): Int

    // Tìm kiếm từ theo keyword
    @Query("SELECT * FROM words WHERE setId = :setId AND (word LIKE '%' || :keyword || '%' OR meaning LIKE '%' || :keyword || '%')")
    fun searchWords(setId: String, keyword: String): Flow<List<WordEntity>>
}