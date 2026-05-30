package com.example.englishapp.features.progress.domain.usecase

import com.example.englishapp.features.progress.domain.model.WordStatus
import com.example.englishapp.features.progress.domain.repository.IProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWordStatusUseCase @Inject constructor(
    private val repository: IProgressRepository
) {
    operator fun invoke(userId: String): Flow<WordStatus> {
        return repository.getWordStatus(userId)
    }
}
