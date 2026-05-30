package com.example.englishapp.features.progress.domain.usecase

import com.example.englishapp.features.progress.domain.model.ProgressStats
import com.example.englishapp.features.progress.domain.repository.IProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStatsUseCase @Inject constructor(
    private val repository: IProgressRepository
) {
    operator fun invoke(userId: String): Flow<ProgressStats> {
        return repository.getOverallStats(userId)
    }
}
