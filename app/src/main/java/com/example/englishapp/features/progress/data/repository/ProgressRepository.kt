package com.example.englishapp.features.progress.data.repository

import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.StreakDao
import com.example.englishapp.core.data.local.dao.StudySessionDao
import com.example.englishapp.core.data.local.dao.VocabularySetDao
import com.example.englishapp.features.progress.domain.model.*
import com.example.englishapp.features.progress.domain.repository.IProgressRepository
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

class ProgressRepository @Inject constructor(
    private val streakDao: StreakDao,
    private val studySessionDao: StudySessionDao,
    private val vocabularySetDao: VocabularySetDao,
    private val srsCardDao: SrsCardDao
) : IProgressRepository {

    override fun getOverallStats(userId: String): Flow<ProgressStats> {
        return combine(
            flow { emit(streakDao.getStreak(userId)?.currentStreak ?: 0) },
            srsCardDao.getAllCards(userId).map { it.size },
            // Accuracy is tricky, let's average from study sessions
            studySessionDao.getSessionsByUserId(userId).map { sessions ->
                if (sessions.isEmpty()) 0 
                else (sessions.sumOf { it.accuracy } / sessions.size).toInt()
            }
        ) { streak, totalWords, accuracy ->
            ProgressStats(
                streak = streak,
                totalWords = totalWords,
                accuracy = accuracy,
                level = calculateLevel(totalWords),
                levelProgress = calculateLevelProgress(totalWords)
            )
        }
    }

    override fun getWeeklyActivity(userId: String): Flow<List<DailyActivity>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        // Go back 6 days to get a 7-day range including today
        val end = calendar.timeInMillis + 24 * 60 * 60 * 1000 - 1
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val start = calendar.timeInMillis

        return studySessionDao.getSessionsInRange(userId, start, end).map { sessions ->
            val activityMap = sessions.groupBy { session ->
                val cal = Calendar.getInstance()
                cal.timeInMillis = session.date
                cal.get(Calendar.DAY_OF_WEEK)
            }.mapValues { entry ->
                // Activity level based on words studied, say max is 50 words for 100%
                (entry.value.sumOf { it.wordsStudied }.toFloat() / 50f).coerceIn(0f, 1f)
            }

            val days = listOf(
                Calendar.MONDAY to "T2",
                Calendar.TUESDAY to "T3",
                Calendar.WEDNESDAY to "T4",
                Calendar.THURSDAY to "T5",
                Calendar.FRIDAY to "T6",
                Calendar.SATURDAY to "T7",
                Calendar.SUNDAY to "CN"
            )

            val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

            // Reorder days to end with today? Design shows T2 to CN.
            // Let's just follow the design's fixed order if possible, or relative to today.
            // HTML shows: T2, T3, T4 (Today), T5, T6, T7, CN
            
            days.map { (dayInt, dayName) ->
                DailyActivity(
                    dayName = dayName,
                    activityLevel = activityMap[dayInt] ?: 0f,
                    isToday = dayInt == currentDay
                )
            }
        }
    }

    override fun getWordStatus(userId: String): Flow<WordStatus> {
        return srsCardDao.getAllCards(userId).map { cards ->
            WordStatus(
                total = cards.size,
                mastered = cards.count { it.status == "mastered" },
                learning = cards.count { it.status == "learning" },
                new = cards.count { it.status == "new" }
            )
        }
    }

    override fun getRetentionRates(userId: String): Flow<List<SetRetention>> {
        return vocabularySetDao.observeSets(userId).map { sets ->
            sets.take(3).map { set -> // HTML shows top 3
                // For retention, we'll use a mock calculation or accuracy if available
                // Let's use accuracy from sessions for this set if possible, 
                // but VocabularySetEntity doesn't have accuracy.
                // We'll calculate it based on mastered / total for now as a proxy for "retention"
                val rate = if (set.wordCount > 0) (set.masteredCount * 100 / set.wordCount) else 0
                SetRetention(
                    setName = set.name,
                    retentionRate = rate,
                    iconType = getIconType(set.tags)
                )
            }
        }
    }

    private fun calculateLevel(totalWords: Int): String {
        return when {
            totalWords < 200 -> "Beginner A1"
            totalWords < 500 -> "Elementary A2"
            totalWords < 1000 -> "Intermediate B1"
            totalWords < 2000 -> "Upper-Intermediate B2"
            totalWords < 4000 -> "Advanced C1"
            else -> "Proficient C2"
        }
    }

    private fun calculateLevelProgress(totalWords: Int): Float {
        // Mock progression within B1 for example
        return (totalWords % 500).toFloat() / 500f
    }

    private fun getIconType(tags: String): String {
        return when {
            tags.contains("Business", true) -> "business"
            tags.contains("Travel", true) -> "travel"
            else -> "academic"
        }
    }
}
