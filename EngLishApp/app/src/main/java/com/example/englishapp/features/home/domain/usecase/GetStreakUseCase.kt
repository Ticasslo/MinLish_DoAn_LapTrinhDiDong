package com.example.englishapp.features.home.domain.usecase

import com.example.englishapp.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStreakUseCase @Inject constructor(
    private val repository: IHomeRepository
) {
    operator fun invoke(userId: String): Flow<Int> {
        return repository.getCurrentStreakDays(userId)
    }
}
