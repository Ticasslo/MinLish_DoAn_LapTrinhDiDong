package com.example.englishapp.features.learn.domain.repository

import com.example.englishapp.core.data.model.SrsCard
import com.example.englishapp.core.data.model.Word
import kotlinx.coroutines.flow.Flow

interface ILearnRepository {
    fun getDueCards(userId: String, setId: String): Flow<List<SrsCard>>
    suspend fun getNewCards(userId: String, setId: String, limit: Int): List<SrsCard>
    suspend fun updateSrsCard(card: SrsCard)
    suspend fun getWordById(wordId: String): Word?
}
