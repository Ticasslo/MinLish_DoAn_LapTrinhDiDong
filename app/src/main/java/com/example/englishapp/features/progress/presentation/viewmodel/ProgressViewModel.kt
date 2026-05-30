package com.example.englishapp.features.progress.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import com.example.englishapp.features.progress.domain.model.DailyActivity
import com.example.englishapp.features.progress.domain.model.ProgressStats
import com.example.englishapp.features.progress.domain.model.SetRetention
import com.example.englishapp.features.progress.domain.model.WordStatus
import com.example.englishapp.features.progress.domain.usecase.GetRetentionUseCase
import com.example.englishapp.features.progress.domain.usecase.GetStatsUseCase
import com.example.englishapp.features.progress.domain.usecase.GetWeeklyActivityUseCase
import com.example.englishapp.features.progress.domain.usecase.GetWordStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val isLoading: Boolean = false,
    val stats: ProgressStats = ProgressStats(),
    val weeklyActivity: List<DailyActivity> = emptyList(),
    val wordStatus: WordStatus = WordStatus(),
    val retentionRates: List<SetRetention> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val getStatsUseCase: GetStatsUseCase,
    private val getWeeklyActivityUseCase: GetWeeklyActivityUseCase,
    private val getWordStatusUseCase: GetWordStatusUseCase,
    private val getRetentionUseCase: GetRetentionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser == null) {
            _uiState.update { it.copy(error = "User not logged in") }
            return
        }

        val userId = currentUser.userId
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            combine(
                getStatsUseCase(userId),
                getWeeklyActivityUseCase(userId),
                getWordStatusUseCase(userId),
                getRetentionUseCase(userId)
            ) { stats, activity, wordStatus, retention ->
                ProgressUiState(
                    isLoading = false,
                    stats = stats,
                    weeklyActivity = activity,
                    wordStatus = wordStatus,
                    retentionRates = retention
                )
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}
