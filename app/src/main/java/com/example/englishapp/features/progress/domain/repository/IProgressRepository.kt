package com.example.englishapp.features.progress.domain.repository

import com.example.englishapp.features.progress.domain.model.DailyActivity
import com.example.englishapp.features.progress.domain.model.ProgressStats
import com.example.englishapp.features.progress.domain.model.SetRetention
import com.example.englishapp.features.progress.domain.model.WordStatus
import kotlinx.coroutines.flow.Flow

interface IProgressRepository {
    fun getOverallStats(userId: String): Flow<ProgressStats>
    fun getWeeklyActivity(userId: String): Flow<List<DailyActivity>>
    fun getWordStatus(userId: String): Flow<WordStatus>
    fun getRetentionRates(userId: String): Flow<List<SetRetention>>
}
