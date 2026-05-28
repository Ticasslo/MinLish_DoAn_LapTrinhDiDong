package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import javax.inject.Inject

/**
 * UseCase thêm mới hoặc cập nhật bộ từ vựng
 */
class CreateSetUseCase @Inject constructor(
    private val repository: IVocabRepository
) {
    suspend operator fun invoke(set: VocabularySet) {
        repository.insertOrUpdateSet(set)
    }
}