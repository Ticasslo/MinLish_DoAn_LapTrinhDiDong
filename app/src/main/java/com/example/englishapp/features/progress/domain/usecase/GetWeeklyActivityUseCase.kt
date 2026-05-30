package com.example.englishapp.features.progress.domain.usecase

import com.example.englishapp.features.progress.domain.model.DailyActivity
import com.example.englishapp.features.progress.domain.repository.IProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyActivityUseCase @Inject constructor(
    private val repository: IProgressRepository
) {
    operator fun invoke(userId: String): Flow<List<DailyActivity>> {
        return repository.getWeeklyActivity(userId)
    }
}
