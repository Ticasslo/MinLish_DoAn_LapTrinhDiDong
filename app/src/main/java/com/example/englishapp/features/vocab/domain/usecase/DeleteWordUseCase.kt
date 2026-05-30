package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.vocab.data.repository.VocabRepository
import javax.inject.Inject

/**
 * UseCase xóa từ vựng khỏi bộ từ và cập nhật lại thống kê đếm.
 */
class DeleteWordUseCase @Inject constructor(
    private val repository: VocabRepository
) {
    suspend operator fun invoke(word: Word) {
        repository.deleteWord(word)
        
        // Tính toán lại số từ của bộ từ vựng sau khi xóa thành công
        repository.recalculateSetCounts(word.setId, word.userId)
    }
}