package com.example.englishapp.core.data.repository

import android.util.Log
import com.example.englishapp.core.data.local.dao.*
import com.example.englishapp.core.data.mapper.*
import com.example.englishapp.core.data.remote.FirebaseService
import com.example.englishapp.core.util.NetworkUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.first
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
    private val dataStore: DataStore<Preferences>,
    networkUtil: NetworkUtil
) : BaseRepository(networkUtil) {

    private val TAG = "SyncRepository"
    
    // Hàm tạo key động theo userId để tránh lẫn dữ liệu khi đổi tài khoản
    private fun getLastSyncKey(userId: String) = longPreferencesKey("last_sync_time_$userId")

    /**
     * Thực hiện đồng bộ toàn diện 2 chiều (Push trước, Pull sau).
     * @throws Exception nếu có lỗi mạng hoặc quyền truy cập để SyncWorker thực hiện retry.
     */
    suspend fun syncAll() {
        val userId = firebaseService.currentUserId ?: return
        
        Log.d(TAG, ">>> Starting Full Sync for User: $userId")

        try {
            // Giai đoạn 1: PUSH (Local -> Remote)
            // Đẩy tất cả thay đổi chưa được đồng bộ từ máy lên Firestore
            pushLocalChanges(userId)

            // Giai đoạn 2: PULL (Remote -> Local)
            // Tải dữ liệu mới nhất từ Firestore về database local
            pullRemoteData(userId)
            
            Log.d(TAG, ">>> Full sync completed successfully!")
        } catch (e: Exception) {
            Log.e(TAG, ">>> Sync failed: ${e.message}")
            throw e // Rất quan trọng: Ném lỗi để SyncWorker biết và thực hiện Retry
        }
    }

    private suspend fun pushLocalChanges(userId: String) {
        Log.d(TAG, "Phase 1: Pushing local changes...")
        syncUserProfile(userId)
        syncVocabularySets(userId)
        syncWords(userId)
        syncSrsCards(userId)
        syncStudySessions(userId)
        syncStreak(userId)
        syncNotifications(userId)
    }

    private suspend fun pullRemoteData(userId: String) {
        Log.d(TAG, "Phase 2: Pulling remote data (Delta Sync for user $userId)...")
        
        // Lấy key riêng của User này
        val userSyncKey = getLastSyncKey(userId)
        val lastSync = dataStore.data.first()[userSyncKey] ?: 0L
        val currentSyncTime = System.currentTimeMillis()

        val result = safeNetworkCall {

            // 1. Pull User Profile (Luôn lấy mới nhất)
            firebaseService.getUser(userId)?.let { remoteUser ->
                userDao.upsertUser(remoteUser.toEntity().copy(isSynced = true))
            }

            // 2. Pull Vocabulary Sets
            val remoteSets = firebaseService.getVocabularySets(userId, lastSync)
            remoteSets.forEach { set ->
                vocabularySetDao.insertSet(set.toEntity().copy(isSynced = true))
            }

            // 3. Pull Words
            val remoteWords = firebaseService.getWords(userId, lastSync)
            if (remoteWords.isNotEmpty()) {
                wordDao.upsertWords(remoteWords.map { it.toEntity().copy(isSynced = true) })
            }

            // 4. Pull SRS Cards
            val remoteCards = firebaseService.getSrsCards(userId, lastSync)
            if (remoteCards.isNotEmpty()) {
                srsCardDao.upsertCards(remoteCards.map { it.toEntity().copy(isSynced = true) })
            }

            // 5. Pull Study Sessions
            val remoteSessions = firebaseService.getStudySessions(userId, lastSync)
            remoteSessions.forEach { sess ->
                studySessionDao.insertSession(sess.toEntity().copy(isSynced = true))
            }

            // 6. Pull Streak
            firebaseService.getStreak(userId)?.let { remoteStreak ->
                streakDao.insertStreak(remoteStreak.toEntity().copy(isSynced = true))
            }

            // 7. Pull Notifications
            val remoteNotifs = firebaseService.getNotifications(userId, lastSync)
            remoteNotifs.forEach { notif ->
                notificationDao.insertNotification(notif.toEntity().copy(isSynced = true))
            }

            // Sau khi thành công, cập nhật mốc thời gian RIÊNG cho user này
            dataStore.edit { prefs -> prefs[userSyncKey] = currentSyncTime }
            Log.d(TAG, "Delta Sync finished for $userId. Key updated to: $currentSyncTime")
        }
        
        result.getOrThrow()
    }

    // --- LOGIC ĐẨY DỮ LIỆU (PUSH) ---

    private suspend fun syncUserProfile(userId: String) {
        val userEntity = userDao.getCurrentUser() ?: return
        if (userEntity.userId == userId && !userEntity.isSynced) {
            val userToSync = userEntity.toDomain()
            
            safeNetworkCall { firebaseService.saveUser(userToSync) }
                .onSuccess { userDao.markUserAsSynced(userId) }
        }
    }

    private suspend fun syncVocabularySets(userId: String) {
        vocabularySetDao.getUnsyncedSets(userId).forEach { entity ->
            safeNetworkCall { firebaseService.saveVocabularySet(entity.toDomain()) }
                .onSuccess { vocabularySetDao.markAsSynced(entity.setId) }
        }
    }

    private suspend fun syncWords(userId: String) {
        wordDao.getUnsyncedWords(userId).forEach { entity ->
            safeNetworkCall { firebaseService.saveWord(entity.toDomain()) }
                .onSuccess { wordDao.markAsSynced(entity.wordId) }
        }
    }

    private suspend fun syncSrsCards(userId: String) {
        srsCardDao.getUnsyncedCards(userId).forEach { entity ->
            safeNetworkCall { firebaseService.saveSrsCard(entity.toDomain()) }
                .onSuccess { srsCardDao.markAsSynced(entity.cardId) }
        }
    }

    private suspend fun syncStudySessions(userId: String) {
        studySessionDao.getUnsyncedSessions(userId).forEach { entity ->
            safeNetworkCall { firebaseService.saveStudySession(entity.toDomain()) }
                .onSuccess { studySessionDao.markAsSynced(entity.sessionId) }
        }
    }

    private suspend fun syncStreak(userId: String) {
        val streak = streakDao.getStreak(userId) ?: return
        if (!streak.isSynced) {
            safeNetworkCall { firebaseService.saveStreak(streak.toDomain()) }
                .onSuccess { streakDao.markStreakAsSynced(userId) }
        }
    }

    private suspend fun syncNotifications(userId: String) {
        notificationDao.getUnsyncedNotifications(userId).forEach { entity ->
            safeNetworkCall { firebaseService.saveNotification(entity.toDomain()) }
                .onSuccess { notificationDao.markAsSynced(entity.notificationId) }
        }
    }
}
