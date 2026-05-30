package com.example.englishapp.features.home.data.repository

import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.StreakDao
import com.example.englishapp.core.data.local.dao.StudySessionDao
import com.example.englishapp.core.data.local.dao.VocabularySetDao
import com.example.englishapp.features.home.domain.model.HomeNewWordDeck
import com.example.englishapp.features.home.domain.model.HomeRecentDeck
import com.example.englishapp.features.home.domain.model.HomeReviewDeck
import com.example.englishapp.features.home.domain.repository.IHomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val streakDao: StreakDao,
    private val srsCardDao: SrsCardDao,
    private val studySessionDao: StudySessionDao,
    private val vocabularySetDao: VocabularySetDao
) : IHomeRepository {

    override suspend fun getCurrentStreakDays(userId: String): Int {
        return streakDao.getStreak(userId)?.currentStreak ?: 0
    }

    override fun getDueWordsCount(userId: String): Flow<Int> {
        return srsCardDao.getDueCards(userId, System.currentTimeMillis()).map { cards -> cards.size }
    }

    override fun getWordsStudiedToday(userId: String, startOfDay: Long, endOfDay: Long): Flow<Int> {
        return srsCardDao.getStudiedCardsCountInRange(userId, startOfDay, endOfDay)
    }

    // === Mục "Cần ôn ngay": ghép dueCount per set với tên set thực ===
    override fun getReviewDecks(userId: String): Flow<List<HomeReviewDeck>> {
        return combine(
            srsCardDao.getDueCountPerSet(userId, System.currentTimeMillis()),
            vocabularySetDao.observeSets(userId)
        ) { dueCounts, sets ->
            val setMap = sets.associateBy { it.setId }
            dueCounts.mapNotNull { dueCount ->
                val set = setMap[dueCount.setId] ?: return@mapNotNull null
                HomeReviewDeck(
                    setId = set.setId,
                    name = set.name,
                    dueCount = dueCount.dueCount,
                    tags = if (set.tags.isBlank()) emptyList() else set.tags.split(",").map { it.trim() }
                )
            }
        }
    }

    // === Mục "Từ mới hôm nay": ghép newCount per set với tên set thực ===
    override fun getNewWordDecks(userId: String): Flow<List<HomeNewWordDeck>> {
        return combine(
            srsCardDao.getNewCountPerSet(userId),
            vocabularySetDao.observeSets(userId)
        ) { newCounts, sets ->
            val setMap = sets.associateBy { it.setId }
            newCounts.mapNotNull { newCount ->
                val set = setMap[newCount.setId] ?: return@mapNotNull null
                HomeNewWordDeck(
                    setId = set.setId,
                    name = set.name,
                    newCount = newCount.newCount,
                    tags = if (set.tags.isBlank()) emptyList() else set.tags.split(",").map { it.trim() }
                )
            }
        }
    }

    // === Mục "Gần đây": ghép session gần nhất với tên set + % mastered ===
    override fun getRecentDecks(userId: String): Flow<List<HomeRecentDeck>> {
        return combine(
            studySessionDao.getRecentSessionsPerSet(userId),
            vocabularySetDao.observeSets(userId)
        ) { sessions, sets ->
            val setMap = sets.associateBy { it.setId }
            sessions.mapNotNull { session ->
                val set = setMap[session.setId] ?: return@mapNotNull null
                val masteredPercent = if (set.wordCount > 0) {
                    (set.masteredCount * 100) / set.wordCount
                } else 0
                HomeRecentDeck(
                    setId = set.setId,
                    name = set.name,
                    lastStudiedAt = session.date,
                    masteredPercent = masteredPercent
                )
            }
        }
    }
}