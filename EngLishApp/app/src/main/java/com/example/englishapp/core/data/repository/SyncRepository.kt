package com.example.englishapp.core.data.repository

import android.util.Log
import com.example.englishapp.core.data.local.dao.*
import com.example.englishapp.core.data.mapper.*
import com.example.englishapp.core.data.remote.FirebaseService
import com.example.englishapp.core.util.NetworkUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncRepository @Inject constructor(
    private val wordDao: WordDao,
    private val srsCardDao: SrsCardDao,
    private val vocabularySetDao: VocabularySetDao,
    private val studySessionDao: StudySessionDao,
    private val streakDao: StreakDao,
    private val notificationDao: NotificationDao,
    private val userDao: UserDao,
    private val firebaseService: FirebaseService,
    networkUtil: NetworkUtil
) : BaseRepository(networkUtil) {

    private val TAG = "SyncRepository"

    suspend fun syncAll() {
        val userId = firebaseService.currentUserId ?: return
        
        Log.d(TAG, "Starting full sync for user: $userId")

        syncUserProfile(userId)
        syncVocabularySets(userId)
        syncWords(userId)
        syncSrsCards(userId)
        syncStudySessions(userId)
        syncStreak(userId)
        syncNotifications(userId)
        
        Log.d(TAG, "Sync completed")
    }

    private suspend fun syncUserProfile(userId: String) {
        val user = userDao.getCurrentUser() ?: return
        if (user.userId == userId && !user.isSynced) {
            val result = safeNetworkCall<Unit> {
                firebaseService.saveUser(user.toDomain())
            }
            if (result.isSuccess) {
                userDao.markUserAsSynced(userId)
            }
        }
    }

    private suspend fun syncVocabularySets(userId: String) {
        val unsyncedSets = vocabularySetDao.getUnsyncedSets(userId)
        unsyncedSets.forEach { entity ->
            val result = safeNetworkCall<Unit> {
                firebaseService.saveVocabularySet(entity.toDomain())
            }
            if (result.isSuccess) {
                vocabularySetDao.markAsSynced(entity.setId)
            }
        }
    }

    private suspend fun syncWords(userId: String) {
        val unsyncedWords = wordDao.getUnsyncedWords(userId)
        unsyncedWords.forEach { entity ->
            val result = safeNetworkCall<Unit> {
                firebaseService.saveWord(entity.toDomain())
            }
            if (result.isSuccess) {
                wordDao.markAsSynced(entity.wordId)
            }
        }
    }

    private suspend fun syncSrsCards(userId: String) {
        val unsyncedCards = srsCardDao.getUnsyncedCards(userId)
        unsyncedCards.forEach { entity ->
            val result = safeNetworkCall<Unit> {
                firebaseService.saveSrsCard(entity.toDomain())
            }
            if (result.isSuccess) {
                srsCardDao.markAsSynced(entity.cardId)
            }
        }
    }

    private suspend fun syncStudySessions(userId: String) {
        val unsyncedSessions = studySessionDao.getUnsyncedSessions(userId)
        unsyncedSessions.forEach { entity ->
            val result = safeNetworkCall<Unit> {
                firebaseService.saveStudySession(entity.toDomain())
            }
            if (result.isSuccess) {
                studySessionDao.markAsSynced(entity.sessionId)
            }
        }
    }

    private suspend fun syncStreak(userId: String) {
        val streak = streakDao.getStreak(userId) ?: return
        if (!streak.isSynced) {
            val result = safeNetworkCall<Unit> {
                firebaseService.saveStreak(streak.toDomain())
            }
            if (result.isSuccess) {
                streakDao.markStreakAsSynced(userId)
            }
        }
    }

    private suspend fun syncNotifications(userId: String) {
        val unsyncedNotifications = notificationDao.getUnsyncedNotifications(userId)
        unsyncedNotifications.forEach { entity ->
            val result = safeNetworkCall<Unit> {
                firebaseService.saveNotification(entity.toDomain())
            }
            if (result.isSuccess) {
                notificationDao.markAsSynced(entity.notificationId)
            }
        }
    }
}
