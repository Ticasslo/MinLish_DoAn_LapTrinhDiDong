package com.example.englishapp.features.progress.domain.usecase

import com.example.englishapp.features.progress.domain.model.SetRetention
import com.example.englishapp.features.progress.domain.repository.IProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRetentionUseCase @Inject constructor(
    private val repository: IProgressRepository
) {
    operator fun invoke(userId: String): Flow<List<SetRetention>> {
        return repository.getRetentionRates(userId)
    }
}
