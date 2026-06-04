package com.example.englishapp.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import com.example.englishapp.core.data.repository.SyncRepository
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val isLoading: Boolean = true, // Mặc định là true khi bắt đầu
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
    private val getHomeDecksUseCase: GetHomeDecksUseCase,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()
    
    private var statsJob: Job? = null

    init {
        loadUserData()
        triggerSync()
    }

    private fun triggerSync() {
        viewModelScope.launch {
            try {
                syncRepository.syncAll()
            } catch (e: Exception) {
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            authRepository.observeCurrentUser().collect { user ->
                if (user != null) {
                    val currentUserId = _uiState.value.user?.userId
                    _uiState.update { it.copy(
                        user = user,
                        wordGoal = user.dailyGoal
                    ) }
                    
                    // Chỉ bắt đầu quan sát dữ liệu học tập nếu là user mới hoặc lần đầu load
                    if (user.userId != currentUserId) {
                        startObservingData(user.userId)
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, user = null) }
                }
            }
        }
    }

    private fun startObservingData(userId: String) {
        statsJob?.cancel()
        statsJob = viewModelScope.launch {
            // 1) Streak (Flow)
            launch {
                getStreakUseCase(userId).collect { streakDays ->
                    _uiState.update { it.copy(streakDays = streakDays) }
                }
            }

            // 2) Tiến độ hôm nay + số từ đến hạn
            val (startOfDay, endOfDay) = getTodayRangeMillis()
            launch {
                getDailyProgressUseCase(userId, startOfDay, endOfDay).collect { progress ->
                    _uiState.update {
                        it.copy(
                            wordsToday = progress.wordsToday,
                            dueWordsCount = progress.dueWordsCount
                        )
                    }
                }
            }

            // 3) Các bộ từ thực
            launch {
                getHomeDecksUseCase(userId).collect { decksData ->
                    _uiState.update {
                        it.copy(
                            reviewDecks = decksData.reviewDecks,
                            newWordDecks = decksData.newWordDecks,
                            recentDecks = decksData.recentDecks,
                            isLoading = false // Đã tải xong dữ liệu chính
                        )
                    }
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
