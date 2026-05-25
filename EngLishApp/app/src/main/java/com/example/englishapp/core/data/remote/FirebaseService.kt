package com.example.englishapp.core.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    val currentUserId get() = auth.currentUser?.uid ?: ""
    val isLoggedIn get() = auth.currentUser != null
}