package com.example.englishapp.features.home.domain.usecase

import com.example.englishapp.features.home.domain.repository.IHomeRepository
import javax.inject.Inject

class GetStreakUseCase @Inject constructor(
    private val repository: IHomeRepository
) {
    suspend operator fun invoke(userId: String): Int {
        return repository.getCurrentStreakDays(userId)
    }
}