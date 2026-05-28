package com.example.englishapp.features.vocab.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.features.auth.domain.usecase.GetCurrentUserUseCase
import com.example.englishapp.features.vocab.domain.usecase.CreateSetUseCase
import com.example.englishapp.features.vocab.domain.usecase.DeleteSetUseCase
import com.example.englishapp.features.vocab.domain.usecase.GetSetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Trạng thái dữ liệu giao diện (UI State) cho màn hình quản lý danh sách bộ từ vựng
 */
data class SetsUiState(
    val sets: List<VocabularySet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val userId: String = ""
)

/**
 * Lớp SetsViewModel chịu trách nhiệm điều phối dữ liệu giữa tầng Domain (UseCases)
 * và tầng giao diện (Compose UI) cho màn hình Danh sách bộ từ vựng (MySetsScreen).
 */
@HiltViewModel
class SetsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getSetsUseCase: GetSetsUseCase,
    private val createSetUseCase: CreateSetUseCase,
    private val deleteSetUseCase: DeleteSetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Tự động tải danh sách bộ từ vựng của người dùng khi khởi tạo ViewModel
        loadSets()
    }

    /**
     * Tải danh sách bộ từ vựng dựa trên ID người dùng hiện tại
     */
    fun loadSets() {
        val user = getCurrentUserUseCase()
        val uid = user?.userId ?: ""
        if (uid.isBlank()) {
            _uiState.update { it.copy(error = "Người dùng chưa đăng nhập") }
            return
        }

        _uiState.update { it.copy(userId = uid, isLoading = true) }

        viewModelScope.launch {
            // Lắng nghe dữ liệu thay đổi trực tiếp từ Room DB qua Flow
            getSetsUseCase(uid)
                .catch { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.localizedMessage) }
                }
                .collect { setsList ->
                    _uiState.update { it.copy(sets = setsList, isLoading = false, error = null) }
                }
        }
    }

    /**
     * Thao tác tạo nhanh một bộ từ vựng mới
     */
    fun createSet(name: String, description: String, tags: List<String>, onResult: (Boolean) -> Unit = {}) {
        val uid = _uiState.value.userId
        if (uid.isBlank()) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val newSet = VocabularySet(
                    setId = UUID.randomUUID().toString(),
                    userId = uid,
                    name = name,
                    description = description,
                    tags = tags,
                    wordCount = 0,
                    masteredCount = 0,
                    learningCount = 0,
                    newCount = 0,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                createSetUseCase(newSet)
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    /**
     * Thao tác xóa một bộ từ vựng cụ thể
     */
    fun deleteSet(setId: String) {
        val uid = _uiState.value.userId
        if (uid.isBlank()) return

        viewModelScope.launch {
            try {
                deleteSetUseCase(setId, uid)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}