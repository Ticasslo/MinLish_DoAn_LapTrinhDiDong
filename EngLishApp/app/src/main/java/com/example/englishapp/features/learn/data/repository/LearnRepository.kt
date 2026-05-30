package com.example.englishapp.features.learn.data.repository

import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.WordDao
import com.example.englishapp.core.data.mapper.*
import com.example.englishapp.core.data.model.SrsCard
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.core.data.remote.FirebaseService
import com.example.englishapp.core.data.repository.BaseRepository
import com.example.englishapp.core.util.NetworkUtil
import com.example.englishapp.features.learn.domain.repository.ILearnRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearnRepository @Inject constructor(
    private val srsCardDao: SrsCardDao,
    private val wordDao: WordDao,
    private val studySessionDao: com.example.englishapp.core.data.local.dao.StudySessionDao,
    private val firebaseService: FirebaseService,
    networkUtil: NetworkUtil
) : BaseRepository(networkUtil), ILearnRepository {

    override fun getDueCards(userId: String, setId: String): Flow<List<SrsCard>> {
        return srsCardDao.getCardsBySetId(setId, userId).map { list ->
            list.filter { 
                it.status != "new" && it.nextReview <= System.currentTimeMillis() 
            }.map { it.toDomain() }
        }
    }

    override suspend fun getNewCards(userId: String, setId: String, limit: Int): List<SrsCard> {
        return srsCardDao.getNewCards(setId, userId, limit).map { it.toDomain() }
    }

    override suspend fun updateSrsCard(card: SrsCard) {
        syncItem(
            localOp = {
                srsCardDao.upsertCard(card.toEntity().copy(isSynced = false))
            },
            remoteOp = {
                firebaseService.saveSrsCard(card)
            },
            onSyncSuccess = {
                srsCardDao.upsertCard(card.toEntity().copy(isSynced = true))
            }
        )
    }

    override suspend fun getWordById(wordId: String): Word? {
        return wordDao.getWordById(wordId)?.toDomain()
    }

    override suspend fun saveStudySession(session: com.example.englishapp.core.data.model.StudySession) {
        syncItem(
            localOp = {
                studySessionDao.insertSession(session.toEntity().copy(isSynced = false))
            },
            remoteOp = {
                firebaseService.saveStudySession(session)
            },
            onSyncSuccess = {
                studySessionDao.insertSession(session.toEntity().copy(isSynced = true))
            }
        )
    }
}
