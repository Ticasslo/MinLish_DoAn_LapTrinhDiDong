package com.example.englishapp.features.vocab.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.features.vocab.data.repository.DictionaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Trạng thái giao diện màn hình tra cứu từ điển trực tuyến
 */
data class DictionaryUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,

    // Kết quả tra cứu từ Free Dictionary API
    val word: String = "",
    val phonetic: String = "",           // Phiên âm IPA, VD: /rɪˈzɪl.jənt/
    val partOfSpeech: String = "",       // Loại từ, VD: adjective
    val englishDefinition: String = "",  // Định nghĩa tiếng Anh
    val example: String = "",            // Câu ví dụ

    // Kết quả dịch nghĩa tiếng Việt từ MyMemory API
    val vietnameseMeaning: String = "",  // Nghĩa tiếng Việt

    val hasResult: Boolean = false
)

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DictionaryUiState())
    val uiState = _uiState.asStateFlow()

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    /**
     * Thực hiện tra cứu từ vựng:
     * 1. Gọi Free Dictionary API lấy phiên âm, định nghĩa EN, ví dụ
     * 2. Song song gọi MyMemory API dịch sang tiếng Việt
     */
    fun lookup() {
        val word = _uiState.value.query.trim()
        if (word.isBlank()) return

        _uiState.update { it.copy(isLoading = true, error = null, hasResult = false) }

        viewModelScope.launch {
            try {
                // 1. Tra cứu định nghĩa tiếng Anh
                val dictResponse = dictionaryRepository.lookupWord(word)

                if (dictResponse == null) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Không tìm thấy từ \"$word\". Hãy kiểm tra lại chính tả.")
                    }
                    return@launch
                }

                val phonetic = dictResponse.phonetic
                    ?: dictResponse.phonetics.firstOrNull { !it.text.isNullOrEmpty() }?.text
                    ?: ""

                val firstMeaning = dictResponse.meanings.firstOrNull()
                val partOfSpeech = firstMeaning?.partOfSpeech ?: ""
                val firstDef = firstMeaning?.definitions?.firstOrNull()
                val englishDefinition = firstDef?.definition ?: ""
                val example = firstDef?.example ?: ""

                // 2. Dịch nghĩa sang tiếng Việt: chỉ dịch TỪ GỐC (không dịch định nghĩa)
                val vietnameseMeaning = dictionaryRepository.translateToVietnamese(word) ?: ""

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = null,
                        word = dictResponse.word,
                        phonetic = phonetic,
                        partOfSpeech = partOfSpeech,
                        englishDefinition = englishDefinition,
                        example = example,
                        vietnameseMeaning = vietnameseMeaning,
                        hasResult = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Lỗi kết nối. Vui lòng kiểm tra mạng.")
                }
            }
        }
    }

    fun clearResult() {
        _uiState.update { DictionaryUiState() }
    }
}
