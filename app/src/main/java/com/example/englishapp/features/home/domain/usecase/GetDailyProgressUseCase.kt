package com.example.englishapp.features.home.domain.usecase

import com.example.englishapp.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class DailyProgress(
    val wordsToday: Int = 0,
    val dueWordsCount: Int = 0
)

class GetDailyProgressUseCase @Inject constructor(
    private val repository: IHomeRepository
) {
    operator fun invoke(userId: String, startOfDay: Long, endOfDay: Long): Flow<DailyProgress> {
        return combine(
            repository.getWordsStudiedToday(userId, startOfDay, endOfDay),
            repository.getDueWordsCount(userId)
        ) { wordsToday, dueWordsCount ->
            DailyProgress(wordsToday = wordsToday, dueWordsCount = dueWordsCount)
        }
    }
}