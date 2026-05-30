package com.example.englishapp.core.data.remote

import com.example.englishapp.core.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor(
    val auth: FirebaseAuth,
    val firestore: FirebaseFirestore
) {
    // Các collection references dùng chung
    val usersCollection get() = firestore.collection("users")
    val setsCollection get() = firestore.collection("vocabulary_sets")
    val wordsCollection get() = firestore.collection("words")
    val srsCardsCollection get() = firestore.collection("srs_cards")
    val sessionsCollection get() = firestore.collection("study_sessions")
    val streaksCollection get() = firestore.collection("streaks")
    val notificationsCollection get() = firestore.collection("notifications")

    // User hiện tại
    val currentUserId: String? get() = auth.currentUser?.uid

    suspend fun saveVocabularySet(set: VocabularySet) {
        setsCollection.document(set.setId).set(set, SetOptions.merge()).await()
    }

    suspend fun saveWord(word: Word) {
        wordsCollection.document(word.wordId).set(word, SetOptions.merge()).await()
    }

    suspend fun saveSrsCard(card: SrsCard) {
        srsCardsCollection.document(card.cardId).set(card, SetOptions.merge()).await()
    }

    suspend fun saveStudySession(session: StudySession) {
        sessionsCollection.document(session.sessionId).set(session, SetOptions.merge()).await()
    }

    suspend fun saveStreak(streak: Streak) {
        streaksCollection.document(streak.userId).set(streak, SetOptions.merge()).await()
    }

    suspend fun saveUser(user: User) {
        usersCollection.document(user.userId).set(user, SetOptions.merge()).await()
    }

    suspend fun saveNotification(notification: Notification) {
        notificationsCollection.document(notification.notificationId).set(notification, SetOptions.merge()).await()
    }

    // --- PULL METHODS (Supporting Delta Sync) ---

    suspend fun getVocabularySets(userId: String, since: Long = 0): List<VocabularySet> {
        return setsCollection.whereEqualTo("userId", userId)
            .whereGreaterThan("updatedAt", since)
            .get().await()
            .toObjects(VocabularySet::class.java)
    }

    suspend fun getWords(userId: String, since: Long = 0): List<Word> {
        return wordsCollection.whereEqualTo("userId", userId)
            .whereGreaterThan("updatedAt", since)
            .get().await()
            .toObjects(Word::class.java)
    }

    suspend fun getSrsCards(userId: String, since: Long = 0): List<SrsCard> {
        return srsCardsCollection.whereEqualTo("userId", userId)
            .whereGreaterThan("updatedAt", since)
            .get().await()
            .toObjects(SrsCard::class.java)
    }

    suspend fun getStudySessions(userId: String, since: Long = 0): List<StudySession> {
        return sessionsCollection.whereEqualTo("userId", userId)
            .whereGreaterThan("updatedAt", since)
            .get().await()
            .toObjects(StudySession::class.java)
    }

    suspend fun getStreak(userId: String): Streak? {
        return streaksCollection.document(userId).get().await()
            .toObject(Streak::class.java)
    }

    suspend fun getNotifications(userId: String, since: Long = 0): List<Notification> {
        return notificationsCollection.whereEqualTo("userId", userId)
            .whereGreaterThan("updatedAt", since)
            .get().await()
            .toObjects(Notification::class.java)
    }

    suspend fun getUser(userId: String): User? {
        return usersCollection.document(userId).get().await()
            .toObject(User::class.java)
    }
}
