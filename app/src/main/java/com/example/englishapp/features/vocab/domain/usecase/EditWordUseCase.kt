package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.vocab.data.repository.VocabRepository
import javax.inject.Inject

/**
 * UseCase chỉnh sửa thông tin từ vựng đã tồn tại
 */
class EditWordUseCase @Inject constructor(
    private val repository: VocabRepository
) {
    suspend operator fun invoke(word: Word) {
        val updatedWord = word.copy(updatedAt = System.currentTimeMillis())
        repository.insertOrUpdateWord(updatedWord)
        
        // Cập nhật lại số lượng từ (đề phòng thay đổi đếm)
        repository.recalculateSetCounts(word.setId, word.userId)
    }
}