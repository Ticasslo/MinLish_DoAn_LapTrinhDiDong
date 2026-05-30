package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import javax.inject.Inject

/**
 * UseCase xóa bỏ hoàn toàn bộ từ vựng (Cascade Delete các từ bên trong)
 */
class DeleteSetUseCase @Inject constructor(
    private val repository: IVocabRepository
) {
    suspend operator fun invoke(setId: String, userId: String) {
        repository.deleteSet(setId, userId)
    }
}