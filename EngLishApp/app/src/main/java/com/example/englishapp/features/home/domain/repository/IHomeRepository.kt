package com.example.englishapp.features.home.domain.repository

import com.example.englishapp.features.home.domain.model.HomeNewWordDeck
import com.example.englishapp.features.home.domain.model.HomeRecentDeck
import com.example.englishapp.features.home.domain.model.HomeReviewDeck
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    suspend fun getCurrentStreakDays(userId: String): Int
    fun getDueWordsCount(userId: String): Flow<Int>
    fun getWordsStudiedToday(userId: String, startOfDay: Long, endOfDay: Long): Flow<Int>
    fun getReviewDecks(userId: String): Flow<List<HomeReviewDeck>>
    fun getNewWordDecks(userId: String): Flow<List<HomeNewWordDeck>>
    fun getRecentDecks(userId: String): Flow<List<HomeRecentDeck>>
}