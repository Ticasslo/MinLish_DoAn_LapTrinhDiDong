package com.example.englishapp.features.home.domain.repository

import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
    suspend fun getCurrentStreakDays(userId: String): Int
    fun getDueWordsCount(userId: String): Flow<Int>
    fun getWordsStudiedToday(userId: String, startOfDay: Long, endOfDay: Long): Flow<Int>
}