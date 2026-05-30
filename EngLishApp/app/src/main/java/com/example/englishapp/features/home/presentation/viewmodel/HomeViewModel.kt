package com.example.englishapp.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import com.example.englishapp.features.home.domain.model.HomeNewWordDeck
import com.example.englishapp.features.home.domain.model.HomeRecentDeck
import com.example.englishapp.features.home.domain.model.HomeReviewDeck
import com.example.englishapp.features.home.domain.usecase.GetDailyProgressUseCase
import com.example.englishapp.features.home.domain.usecase.GetHomeDecksUseCase
import com.example.englishapp.features.home.domain.usecase.GetStreakUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val streakDays: Int = 0,
    val wordsToday: Int = 0,
    val wordGoal: Int = 10,
    val dueWordsCount: Int = 0,
    val reviewDecks: List<HomeReviewDeck> = emptyList(),
    val newWordDecks: List<HomeNewWordDeck> = emptyList(),
    val recentDecks: List<HomeRecentDeck> = emptyList()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: IAuthRepository,
    private val getStreakUseCase: GetStreakUseCase,
    private val getDailyProgressUseCase: GetDailyProgressUseCase,
    private val getHomeDecksUseCase: GetHomeDecksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    private var dailyProgressJob: Job? = null
    private var homeDecksJob: Job? = null

    init {
        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                authRepository.getUserData(currentUser.userId).collect { result ->
                    when (result) {
                        is AuthResult.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is AuthResult.Success -> {
                            _uiState.update { it.copy(
                                user = result.data,
                                isLoading = false,
                                wordGoal = result.data.dailyGoal
                            ) }
                            loadHomeStats(result.data.userId)
                        }
                        is AuthResult.Error -> {
                            _uiState.update { it.copy(
                                isLoading = false,
                                error = result.message
                            ) }
                        }
                    }
                }
            }
        }
    }

    private fun loadHomeStats(userId: String) {
        // 1) Streak hiện tại
        viewModelScope.launch {
            val streakDays = getStreakUseCase(userId)
            _uiState.update { it.copy(streakDays = streakDays) }
        }

        // 2) Tiến độ hôm nay + số từ đến hạn
        val (startOfDay, endOfDay) = getTodayRangeMillis()
        dailyProgressJob?.cancel()
        dailyProgressJob = viewModelScope.launch {
            getDailyProgressUseCase(userId, startOfDay, endOfDay).collect { progress ->
                _uiState.update {
                    it.copy(
                        wordsToday = progress.wordsToday,
                        dueWordsCount = progress.dueWordsCount
                    )
                }
            }
        }

        // 3) Các bộ từ thực: review decks, new word decks, recent decks
        homeDecksJob?.cancel()
        homeDecksJob = viewModelScope.launch {
            getHomeDecksUseCase(userId).collect { decksData ->
                _uiState.update {
                    it.copy(
                        reviewDecks = decksData.reviewDecks,
                        newWordDecks = decksData.newWordDecks,
                        recentDecks = decksData.recentDecks
                    )
                }
            }
        }
    }

    private fun getTodayRangeMillis(): Pair<Long, Long> {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis

        calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        calendar.add(java.util.Calendar.MILLISECOND, -1)
        val end = calendar.timeInMillis
        return start to end
    }
}
