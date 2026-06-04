package com.example.englishapp.features.progress.data.repository

import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.StreakDao
import com.example.englishapp.core.data.local.dao.StudySessionDao
import com.example.englishapp.core.data.local.dao.UserDao
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
    private val srsCardDao: SrsCardDao,
    private val userDao: UserDao
) : IProgressRepository {

    override fun getOverallStats(userId: String): Flow<ProgressStats> {
        return combine(
            flow { emit(streakDao.getStreak(userId)?.currentStreak ?: 0) },
            srsCardDao.getAllCards(userId).map { it.size },
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

        // Mốc kết thúc: Cuối ngày hôm nay
        val end = calendar.timeInMillis + 24 * 60 * 60 * 1000 - 1

        // Mốc bắt đầu: Tìm về Thứ 2 của tuần này
        val daysToSubtract = (calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
        calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract)
        val start = calendar.timeInMillis

        // Kết hợp luồng dữ liệu Sessions và luồng dữ liệu User
        return combine(
            studySessionDao.getSessionsInRange(userId, start, end),
            userDao.getUserById(userId).map { it?.dailyGoal ?: 50 } // Lấy dailyGoal, mặc định là 50 nếu null
        ) { sessions, dailyGoal ->
            val activityMap = sessions.groupBy { session ->
                //Gôm nhóm buổi học vào thứ trong
                val cal = Calendar.getInstance()
                cal.timeInMillis = session.date
                cal.get(Calendar.DAY_OF_WEEK)
            }.mapValues { entry ->
                (entry.value.sumOf { it.wordsStudied }.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f)
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

            //dayInt: 2, dayName: T2
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
            sets.take(3).map { set ->
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
