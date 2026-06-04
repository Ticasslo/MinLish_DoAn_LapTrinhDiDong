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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val streakDao: StreakDao,
    private val srsCardDao: SrsCardDao,
    private val studySessionDao: StudySessionDao,
    private val vocabularySetDao: VocabularySetDao
) : IHomeRepository {

    override fun getCurrentStreakDays(userId: String): Flow<Int> {
        return streakDao.observeStreak(userId).map { it?.currentStreak ?: 0 }
    }

    override fun getDueWordsCount(userId: String): Flow<Int> {
        return srsCardDao.getDueCards(userId, System.currentTimeMillis()).map { cards -> cards.size }
    }

    override fun getWordsStudiedToday(userId: String, startOfDay: Long, endOfDay: Long): Flow<Int> {
        return srsCardDao.getStudiedCardsCountInRange(userId, startOfDay, endOfDay)
    }

    // Mục "Cần ôn ngay": chỉ observe các set có từ đến hạn
    override fun getReviewDecks(userId: String): Flow<List<HomeReviewDeck>> {
        return srsCardDao.getDueCountPerSet(userId, System.currentTimeMillis()).flatMapLatest { dueCounts ->
            if (dueCounts.isEmpty()) return@flatMapLatest flowOf(emptyList())
            
            val setIds = dueCounts.map { it.setId }
            vocabularySetDao.observeSetsByIds(setIds).map { sets ->
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
    }

    // Mục "Từ mới hôm nay": chỉ observe các set có từ mới
    override fun getNewWordDecks(userId: String): Flow<List<HomeNewWordDeck>> {
        return srsCardDao.getNewCountPerSet(userId).flatMapLatest { newCounts ->
            if (newCounts.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val setIds = newCounts.map { it.setId }
            vocabularySetDao.observeSetsByIds(setIds).map { sets ->
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
    }

    // === Mục "Gần đây": Tối ưu chỉ observe các set vừa học ===
    override fun getRecentDecks(userId: String): Flow<List<HomeRecentDeck>> {
        return studySessionDao.getRecentSessionsPerSet(userId).flatMapLatest { sessions ->
            if (sessions.isEmpty()) return@flatMapLatest flowOf(emptyList())

            val setIds = sessions.map { it.setId }
            vocabularySetDao.observeSetsByIds(setIds).map { sets ->
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
}
