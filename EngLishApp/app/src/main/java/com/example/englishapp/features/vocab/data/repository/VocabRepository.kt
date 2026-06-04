package com.example.englishapp.features.vocab.data.repository

import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.local.dao.VocabularySetDao
import com.example.englishapp.core.data.local.dao.WordDao
import com.example.englishapp.core.data.local.entity.SrsCardEntity
import com.example.englishapp.core.data.mapper.toDomain
import com.example.englishapp.core.data.mapper.toEntity
import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.core.data.remote.FirebaseService
import com.example.englishapp.core.data.repository.BaseRepository
import com.example.englishapp.core.util.NetworkUtil
import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Lớp VocabRepository chịu trách nhiệm thực hiện các nghiệp vụ CRUD cho bộ từ vựng và từ vựng chi tiết.
 * Kế thừa BaseRepository để sử dụng các hàm safeNetworkCall và syncItem giúp hỗ trợ cơ chế offline-first.
 */
@Singleton
class VocabRepository @Inject constructor(
    private val wordDao: WordDao,
    private val vocabularySetDao: VocabularySetDao,
    private val srsCardDao: SrsCardDao,
    private val firebaseService: FirebaseService,
    private val networkUtil: NetworkUtil
) : BaseRepository(networkUtil), IVocabRepository {

    // 1. CÁC PHƯƠNG THỨC QUẢN LÝ BỘ TỪ VỰNG (VOCABULARY SET)

    // Lắng nghe danh sách bộ từ vựng của một user ở local Room
    override fun getSets(userId: String): Flow<List<VocabularySet>> {
        return vocabularySetDao.observeSets(userId).map { list ->
            list.map { it.toDomain() }
        }
    }

    // Lấy thông tin chi tiết một bộ từ theo ID
    override suspend fun getSetById(setId: String): VocabularySet? {
        return vocabularySetDao.getSetById(setId)?.toDomain()
    }

    // Thêm mới hoặc cập nhật thông tin bộ từ vựng
    override suspend fun insertOrUpdateSet(set: VocabularySet) {
        syncItem(
            localOp = {
                vocabularySetDao.insertSet(set.toEntity().copy(isSynced = false))
            },
            remoteOp = {
                firebaseService.saveVocabularySet(set)
            },
            onSyncSuccess = {
                vocabularySetDao.insertSet(set.toEntity().copy(isSynced = true))
            }
        )
    }

    // Xóa bộ từ vựng (Offline-first + Cascade Delete)
    override suspend fun deleteSet(setId: String, userId: String) {
        syncItem(
            localOp = {
                // 1. Xóa bộ từ ở local
                vocabularySetDao.getSetById(setId)?.let { setEntity ->
                    vocabularySetDao.deleteSet(setEntity)
                }
                // 2. Xóa toàn bộ từ thuộc bộ từ này ở local
                wordDao.deleteWordsBySetId(setId)
            },
            remoteOp = {
                // 3. Xóa bộ từ trên Firestore
                firebaseService.setsCollection.document(setId).delete().await()

                // 4. Tìm và xóa toàn bộ từ thuộc bộ từ này trên Firestore (Manual Cascade)
                val wordsSnapshot = firebaseService.wordsCollection
                    .whereEqualTo("setId", setId)
                    .get()
                    .await()
                
                for (doc in wordsSnapshot.documents) {
                    doc.reference.delete().await()
                }

                // 5. Xóa toàn bộ SRS Card liên quan trên Firestore
                val cardsSnapshot = firebaseService.srsCardsCollection
                    .whereEqualTo("setId", setId)
                    .get()
                    .await()
                
                for (doc in cardsSnapshot.documents) {
                    doc.reference.delete().await()
                }
            },
            onSyncSuccess = {
                // Đã xóa hoàn tất trên Firestore
            }
        )
    }

    // 2. CÁC PHƯƠNG THỨC QUẢN LÝ TỪ VỰNG CHI TIẾT (WORD)
    // Lấy danh sách từ vựng trong bộ từ theo ID
    override fun getWords(setId: String): Flow<List<Word>> {
        return wordDao.getWordsBySetId(setId).map { list ->
            list.map { it.toDomain() }
        }
    }

    // Lấy từ vựng theo ID
    override suspend fun getWordById(wordId: String): Word? {
        return wordDao.getWordById(wordId)?.toDomain()
    }

    // Thêm hoặc cập nhật một từ vựng
    override suspend fun insertOrUpdateWord(word: Word) {
        syncItem(
            localOp = {
                wordDao.upsertWord(word.toEntity().copy(isSynced = false))
                // Tự động tính toán lại chỉ số sau khi thêm từ
                recalculateSetCounts(word.setId, word.userId)
            },
            remoteOp = {
                firebaseService.saveWord(word)
            },
            onSyncSuccess = {
                wordDao.upsertWord(word.toEntity().copy(isSynced = true))
            }
        )
    }

    // Xóa một từ vựng
    override suspend fun deleteWord(word: Word) {
        syncItem(
            localOp = {
                wordDao.deleteWord(word.toEntity())
                srsCardDao.deleteCardByWordId(word.wordId)
                // Tự động tính toán lại chỉ số sau khi xóa từ
                recalculateSetCounts(word.setId, word.userId)
            },
            remoteOp = {
                firebaseService.wordsCollection.document(word.wordId).delete().await()
                
                // Tìm và xóa SRS Card liên quan trên Firestore
                val cardsSnapshot = firebaseService.srsCardsCollection
                    .whereEqualTo("wordId", word.wordId)
                    .get()
                    .await()
                for (doc in cardsSnapshot.documents) {
                    doc.reference.delete().await()
                }
            },
            onSyncSuccess = {
                // Đã xóa thành công trên Firestore
            }
        )
    }

    // Tìm kiếm từ vựng theo keyword
    override fun searchWords(setId: String, query: String): Flow<List<Word>> {
        return wordDao.searchWords(setId, query).map { list ->
            list.map { it.toDomain() }
        }
    }

    // 3. CẬP NHẬT CHỈ SỐ BỘ TỪ (RECALCULATE COUNTS)
    // Tính toán lại các chỉ số đếm của bộ từ và đồng bộ lên Firestore
    override suspend fun recalculateSetCounts(setId: String, userId: String) {
        val wordList = wordDao.getWordsBySetId(setId).first()
        val totalCount = wordList.size
        
        val mastered = srsCardDao.countByStatus(setId, userId, "mastered")
        val learning = srsCardDao.countByStatus(setId, userId, "learning")
        val new = srsCardDao.countByStatus(setId, userId, "new")
        
        vocabularySetDao.updateCounts(setId, totalCount, mastered, learning, new)
        
        if (networkUtil.isOnline()) {
            vocabularySetDao.getSetById(setId)?.let { entity ->
                try {
                    firebaseService.saveVocabularySet(entity.toDomain())
                    vocabularySetDao.markAsSynced(setId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}