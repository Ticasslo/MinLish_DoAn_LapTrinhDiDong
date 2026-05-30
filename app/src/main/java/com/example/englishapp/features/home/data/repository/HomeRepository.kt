package com.example.englishapp.features.home.data.repository

import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.StreakDao
import com.example.englishapp.core.data.local.dao.StudySessionDao
import com.example.englishapp.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val streakDao: StreakDao,
    private val srsCardDao: SrsCardDao,
    private val studySessionDao: StudySessionDao
) : IHomeRepository {

    override suspend fun getCurrentStreakDays(userId: String): Int {
        return streakDao.getStreak(userId)?.currentStreak ?: 0
    }

    override fun getDueWordsCount(userId: String): Flow<Int> {
        return srsCardDao.getDueCards(userId, System.currentTimeMillis()).map { cards -> cards.size }
    }

    override fun getWordsStudiedToday(userId: String, startOfDay: Long, endOfDay: Long): Flow<Int> {
        return studySessionDao.getSessionsInRange(userId, startOfDay, endOfDay)
            .map { sessions -> sessions.sumOf { it.wordsStudied } }
    }
}