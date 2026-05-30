package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.entity.SrsCardEntity
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.vocab.data.repository.VocabRepository
import java.util.UUID
import javax.inject.Inject

/**
 * UseCase thêm từ vựng mới vào bộ từ vựng chỉ định.
 * Tự động tạo thẻ nhớ SRS thô (status = "new") và tính toán lại thống kê cho bộ từ.
 */
class AddWordUseCase @Inject constructor(
    private val repository: VocabRepository,
    private val srsCardDao: SrsCardDao
) {
    suspend operator fun invoke(word: Word) {
        // 1. Tạo UUID mới cho từ vựng nếu từ chưa có ID
        val finalWordId = if (word.wordId.isBlank()) UUID.randomUUID().toString() else word.wordId
        val finalWord = word.copy(
            wordId = finalWordId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        
        // 2. Lưu từ vựng vào Room + đồng bộ Firestore
        repository.insertOrUpdateWord(finalWord)

        // 3. Tự động khởi tạo thẻ học SRS Card thô với trạng thái "Chưa học (new)"
        val existingCard = srsCardDao.getCardByWordId(finalWordId, word.userId)
        if (existingCard == null) {
            val card = SrsCardEntity(
                cardId = UUID.randomUUID().toString(),
                userId = word.userId,
                wordId = finalWordId,
                setId = word.setId,
                status = "new",
                easeFactor = 2.5,
                interval = 1,
                repetitions = 0,
                nextReview = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
            srsCardDao.upsertCard(card)
        }

        // 4. Tính toán lại số từ (mastered, learning, new) của bộ từ vựng
        repository.recalculateSetCounts(word.setId, word.userId)
    }
}