package com.example.englishapp.features.learn.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.SrsCard
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.auth.domain.usecase.GetCurrentUserUseCase
import com.example.englishapp.features.learn.domain.repository.ILearnRepository
import com.example.englishapp.features.learn.domain.usecase.CalculateSrsUseCase
import com.example.englishapp.features.learn.domain.usecase.GetDueCardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LearnUiState(
    val isLoading: Boolean = false,
    val cards: List<Pair<SrsCard, Word>> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isSessionComplete: Boolean = false,
    val sessionStats: SessionStats = SessionStats(),
    val ratingIntervals: Map<String, String> = emptyMap(),
    val error: String? = null
)

data class SessionStats(
    val totalStudied: Int = 0,
    val correctCount: Int = 0,
    val againCount: Int = 0,
    val hardCount: Int = 0,
    val goodCount: Int = 0,
    val easyCount: Int = 0,
    val startTime: Long = System.currentTimeMillis()
)

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val getDueCardsUseCase: GetDueCardsUseCase,
    private val calculateSrsUseCase: CalculateSrsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val repository: ILearnRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearnUiState(sessionStats = SessionStats()))
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    fun loadCards(setId: String, mode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, sessionStats = SessionStats()) }
            val userId = getCurrentUserUseCase()?.userId ?: return@launch
            
            try {
                if (mode == "review") {
                    // Lấy dữ liệu 1 lần duy nhất để tránh bị gián đoạn session khi DB thay đổi
                    val dueCards = getDueCardsUseCase(userId, setId).first()
                    val cardsWithWords = dueCards.mapNotNull { card ->
                        val word = repository.getWordById(card.wordId)
                        if (word != null) card to word else null
                    }
                    val initialIntervals = if (cardsWithWords.isNotEmpty()) {
                        calculateSrsUseCase.calculateIntervalStrings(cardsWithWords.first().first)
                    } else emptyMap()
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            cards = cardsWithWords,
                            ratingIntervals = initialIntervals,
                            isSessionComplete = cardsWithWords.isEmpty()
                        ) 
                    }
                } else {
                    // Chế độ học từ mới
                    val newCards = repository.getNewCards(userId, setId, 10) // Giới hạn 10 từ mới/phiên
                    val cardsWithWords = newCards.mapNotNull { card ->
                        val word = repository.getWordById(card.wordId)
                        if (word != null) card to word else null
                    }
                    val initialIntervals = if (cardsWithWords.isNotEmpty()) {
                        calculateSrsUseCase.calculateIntervalStrings(cardsWithWords.first().first)
                    } else emptyMap()
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            cards = cardsWithWords,
                            ratingIntervals = initialIntervals,
                            isSessionComplete = cardsWithWords.isEmpty()
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onFlip() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun onRatingSelected(rating: String) {
        val state = _uiState.value
        val currentPair = state.cards.getOrNull(state.currentIndex) ?: return
        
        viewModelScope.launch {
            val updatedCard = calculateSrsUseCase(currentPair.first, rating)
            repository.updateSrsCard(updatedCard)
            
            val updatedCards = state.cards.toMutableList()
            val isCorrect = rating.lowercase() != "again"
            
            val updatedStats = state.sessionStats.copy(
                totalStudied = state.sessionStats.totalStudied + 1,
                correctCount = if (isCorrect) state.sessionStats.correctCount + 1 else state.sessionStats.correctCount,
                againCount = state.sessionStats.againCount + if (rating.lowercase() == "again") 1 else 0,
                hardCount = state.sessionStats.hardCount + if (rating.lowercase() == "hard") 1 else 0,
                goodCount = state.sessionStats.goodCount + if (rating.lowercase() == "good") 1 else 0,
                easyCount = state.sessionStats.easyCount + if (rating.lowercase() == "easy") 1 else 0
            )
            
            if (rating.lowercase() == "again") {
                // Nếu bấm Again (<1m), thêm từ này vào cuối danh sách để lặp lại ngay trong phiên
                updatedCards.add(currentPair)
            }
            
            val nextIndex = state.currentIndex + 1
            if (nextIndex < updatedCards.size) {
                val nextPair = updatedCards[nextIndex]
                val nextIntervals = calculateSrsUseCase.calculateIntervalStrings(nextPair.first)
                _uiState.update { 
                    it.copy(
                        cards = updatedCards,
                        currentIndex = nextIndex,
                        isFlipped = false,
                        sessionStats = updatedStats,
                        ratingIntervals = nextIntervals
                    ) 
                }
            } else {
                _uiState.update { it.copy(isSessionComplete = true, sessionStats = updatedStats) }
                saveSession(updatedStats, updatedCards.firstOrNull()?.first?.setId ?: "")
            }
        }
    }

    private fun saveSession(stats: SessionStats, setId: String) {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()?.userId ?: return@launch
            val durationInSeconds = ((System.currentTimeMillis() - stats.startTime) / 1000).toInt()
            val accuracy = if (stats.totalStudied > 0) {
                (stats.goodCount + stats.easyCount).toDouble() / stats.totalStudied * 100.0
            } else 0.0

            val session = com.example.englishapp.core.data.model.StudySession(
                userId = userId,
                setId = setId,
                sessionType = "review", // Hiện tại để mặc định, có thể tinh chỉnh sau
                wordsStudied = stats.totalStudied,
                accuracy = accuracy,
                duration = durationInSeconds,
                againCount = stats.againCount,
                hardCount = stats.hardCount,
                goodCount = stats.goodCount,
                easyCount = stats.easyCount
            )
            repository.saveStudySession(session)
        }
    }
}
