package com.example.englishapp.features.vocab.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.local.dao.SrsCardDao
import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.auth.domain.usecase.GetCurrentUserUseCase
import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import com.example.englishapp.features.vocab.domain.usecase.*
import com.example.englishapp.features.vocab.presentation.model.WordUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Trạng thái dữ liệu giao diện (UI State) cho màn hình chi tiết danh sách từ vựng
 */
data class VocabUiState(
    val set: VocabularySet? = null,
    val words: List<WordUiItem> = emptyList(),
    val filteredWords: List<WordUiItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: String = "",
    val filterStatus: String = "Tất cả", // Bộ lọc: "Tất cả", "Chưa học", "Đang học", "Đã thuộc"
    val searchQuery: String = ""
)

/**
 * Lớp VocabViewModel điều khiển trạng thái danh sách từ vựng trong 1 bộ từ vựng chỉ định,
 * hỗ trợ tìm kiếm, lọc theo SRS status, thêm/sửa/xóa và Import/Export CSV.
 */
@HiltViewModel
class VocabViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val vocabRepository: IVocabRepository,
    private val srsCardDao: SrsCardDao,
    private val addWordUseCase: AddWordUseCase,
    private val editWordUseCase: EditWordUseCase,
    private val deleteWordUseCase: DeleteWordUseCase,
    private val lookupWordUseCase: LookupWordUseCase,
    private val importCsvUseCase: ImportCsvUseCase,
    private val exportCsvUseCase: ExportCsvUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabUiState())
    val uiState = _uiState.asStateFlow()
    
    private var setId: String = ""

    /**
     * Khởi tạo thông tin bộ từ và lắng nghe dữ liệu từ local DB
     */
    fun initialize(setId: String) {
        this.setId = setId
        val user = getCurrentUserUseCase()
        val uid = user?.userId ?: ""
        if (uid.isBlank()) {
            _uiState.update { it.copy(error = "Người dùng chưa đăng nhập") }
            return
        }
        
        _uiState.update { it.copy(userId = uid, isLoading = true) }
        
        viewModelScope.launch {
            // 1. Tải thông tin chi tiết của bộ từ vựng
            val vocabSet = vocabRepository.getSetById(setId)
            _uiState.update { it.copy(set = vocabSet) }
            
            // 2. Lắng nghe sự kết hợp thay đổi của Words và SrsCards qua Flow
            val wordsFlow = vocabRepository.getWords(setId)
            val cardsFlow = srsCardDao.getCardsBySetId(setId, uid)
            
            wordsFlow.combine(cardsFlow) { wordsList, cardsList ->
                wordsList.map { word ->
                    val card = cardsList.find { it.wordId == word.wordId }
                    val status = card?.status ?: "new"
                    
                    val nextReviewText = when (status) {
                        "mastered" -> "Ôn sau ${card?.interval ?: 14} ngày"
                        "learning" -> "Ôn sau ${card?.interval ?: 1} ngày"
                        else -> "Chưa học"
                    }
                    
                    WordUiItem(word, status, nextReviewText)
                }
            }.collect { uiItems ->
                _uiState.update { 
                    it.copy(
                        words = uiItems, 
                        isLoading = false,
                        error = null
                    ) 
                }
                // Tự động lọc và tìm kiếm dựa trên danh sách mới
                applyFilterAndSearch()
            }
        }
    }

    /**
     * Thay đổi chuỗi tìm kiếm
     */
    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSearch()
    }

    /**
     * Thay đổi bộ lọc trạng thái học tập
     */
    fun onFilterStatusChanged(status: String) {
        _uiState.update { it.copy(filterStatus = status) }
        applyFilterAndSearch()
    }

    // Thực hiện lọc và tìm kiếm dữ liệu
    private fun applyFilterAndSearch() {
        val allItems = _uiState.value.words
        val query = _uiState.value.searchQuery.lowercase().trim()
        val filter = _uiState.value.filterStatus
        
        val filtered = allItems.filter { item ->
            val matchesSearch = item.word.word.lowercase().contains(query) || 
                                item.word.meaning.lowercase().contains(query)
            
            val matchesFilter = when (filter) {
                "Chưa học" -> item.status == "new"
                "Đang học" -> item.status == "learning"
                "Đã thuộc" -> item.status == "mastered"
                else -> true
            }
            
            matchesSearch && matchesFilter
        }
        
        _uiState.update { it.copy(filteredWords = filtered) }
    }

    /**
     * Thêm từ vựng mới
     */
    fun addWord(wordText: String, meaningText: String, pronunciation: String?, defDescription: String? = null, exampleText: String? = null) {
        val uid = _uiState.value.userId
        if (uid.isBlank()) return

        viewModelScope.launch {
            try {
                val newWord = Word(
                    wordId = "",
                    setId = setId,
                    userId = uid,
                    word = wordText.trim(),
                    meaning = meaningText.trim(),
                    pronunciation = pronunciation?.trim(),
                    description = defDescription?.trim(),
                    example = exampleText?.trim(),
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                addWordUseCase(newWord)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Cập nhật thông tin từ vựng
     */
    fun editWord(word: Word) {
        viewModelScope.launch {
            try {
                editWordUseCase(word)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Xóa một từ vựng
     */
    fun deleteWord(word: Word) {
        viewModelScope.launch {
            try {
                deleteWordUseCase(word)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Nhập từ vựng từ tệp CSV
     */
    fun importCsv(csvContent: String, onComplete: () -> Unit = {}) {
        val uid = _uiState.value.userId
        if (uid.isBlank()) return
        
        viewModelScope.launch {
            try {
                importCsvUseCase(csvContent, setId, uid)
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete()
            }
        }
    }

    /**
     * Xuất toàn bộ từ vựng ra chuỗi CSV
     */
    fun exportCsv(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val csv = exportCsvUseCase(setId)
                onComplete(csv)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete("")
            }
        }
    }

    /**
     * Gọi API tra cứu từ điển trực tuyến và trả về kết quả
     */
    fun lookupWordOnline(wordText: String, onResult: (phonetic: String?, definition: String?, example: String?) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = lookupWordUseCase(wordText)
                if (resp != null) {
                    val phonetic = resp.phonetic ?: resp.phonetics.firstOrNull { !it.text.isNullOrEmpty() }?.text
                    val definition = resp.meanings.firstOrNull()?.definitions?.firstOrNull()?.definition
                    val example = resp.meanings.firstOrNull()?.definitions?.firstOrNull()?.example
                    onResult(phonetic, definition, example)
                } else {
                    onResult(null, null, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(null, null, null)
            }
        }
    }
}