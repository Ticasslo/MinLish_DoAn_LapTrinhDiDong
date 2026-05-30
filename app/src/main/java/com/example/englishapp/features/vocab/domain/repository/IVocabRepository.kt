package com.example.englishapp.features.vocab.domain.repository

import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.core.data.model.Word
import kotlinx.coroutines.flow.Flow

/**
 * Giao diện IVocabRepository định nghĩa các thao tác CRUD đối với Bộ từ vựng (Set)
 * và Từ vựng (Word). Triển khai thực tế sẽ nằm ở lớp VocabRepository ở tầng Data.
 */
interface IVocabRepository {

    // =============================================================================
    // 1. CÁC HÀM QUẢN LÝ BỘ TỪ VỰNG (VOCABULARY SET)
    // =============================================================================

    // Lấy toàn bộ danh sách bộ từ vựng của người dùng dưới dạng luồng dữ liệu Flow
    fun getSets(userId: String): Flow<List<VocabularySet>>

    // Tìm kiếm một bộ từ vựng cụ thể theo setId
    suspend fun getSetById(setId: String): VocabularySet?

    // Thêm mới hoặc cập nhật thông tin một bộ từ vựng
    suspend fun insertOrUpdateSet(set: VocabularySet)

    // Xóa bộ từ vựng (đồng thời xóa các từ bên trong bộ từ đó)
    suspend fun deleteSet(setId: String, userId: String)

    // Tính toán lại số lượng từ (wordCount, masteredCount, learningCount, newCount) của bộ từ vựng
    suspend fun recalculateSetCounts(setId: String, userId: String)

    // =============================================================================
    // 2. CÁC HÀM QUẢN LÝ TỪ VỰNG CHI TIẾT (WORD)
    // =============================================================================

    // Lấy danh sách các từ vựng thuộc về một bộ từ cụ thể (setId) dưới dạng Flow
    fun getWords(setId: String): Flow<List<Word>>

    // Tìm kiếm một từ vựng chi tiết theo wordId
    suspend fun getWordById(wordId: String): Word?

    // Thêm mới hoặc cập nhật thông tin của một từ vựng
    suspend fun insertOrUpdateWord(word: Word)

    // Xóa một từ vựng khỏi cơ sở dữ liệu
    suspend fun deleteWord(word: Word)

    // Tìm kiếm từ vựng theo từ khóa (word hoặc meaning) trong bộ từ vựng chỉ định
    fun searchWords(setId: String, query: String): Flow<List<Word>>

    // Thêm dữ liệu mẫu (Sample data) khi lần đầu sử dụng
    suspend fun seedSampleData(userId: String)
}