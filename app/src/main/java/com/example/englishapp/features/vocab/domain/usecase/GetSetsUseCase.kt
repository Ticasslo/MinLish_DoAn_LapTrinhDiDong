package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase lấy danh sách các bộ từ vựng của người dùng
 */
class GetSetsUseCase @Inject constructor(
    private val repository: IVocabRepository
) {
    operator fun invoke(userId: String): Flow<List<VocabularySet>> {
        return repository.getSets(userId)
    }
}