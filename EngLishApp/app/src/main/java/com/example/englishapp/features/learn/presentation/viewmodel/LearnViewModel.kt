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
    val setId: String = "",
    val sessionType: String = "review",
    val isLoading: Boolean = false,
    val cards: List<Pair<SrsCard, Word>> = emptyList(),
    val totalOriginalCards: Int = 0, // Thêm dòng này: Mục tiêu ban đầu (ví dụ: 5 từ)
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isSessionComplete: Boolean = false,
    val sessionStats: SessionStats = SessionStats(),
    val error: String? = null
)

data class SessionStats(
    val totalStudied: Int = 0,
    val correctCount: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val againCount: Int = 0,
    val hardCount: Int = 0,
    val goodCount: Int = 0,
    val easyCount: Int = 0
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
            _uiState.update { it.copy(setId = setId, sessionType = mode, isLoading = true, sessionStats = SessionStats()) }
            val userId = getCurrentUserUseCase()?.userId ?: return@launch
            
            try {
                if (mode == "review") {
                    // SỬA: Dùng first() để lấy danh sách thẻ cần ôn một lần duy nhất khi bắt đầu.
                    // Không dùng collect() vì mỗi khi cập nhật thẻ vào DB, Flow sẽ phát ra list mới (thiếu thẻ vừa học),
                    // dẫn đến lệch currentIndex và gây crash IndexOutOfBounds.
                    val dueCards = getDueCardsUseCase(userId, setId).first()
                    val cardsWithWords = dueCards.mapNotNull { card ->
                        val word = repository.getWordById(card.wordId)
                        if (word != null) card to word else null
                    }
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            cards = cardsWithWords,
                            totalOriginalCards = cardsWithWords.size, // LƯU SỐ LƯỢNG GỐC
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
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            cards = cardsWithWords,
                            totalOriginalCards = cardsWithWords.size // LƯU SỐ LƯỢNG GỐC
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
            val r = rating.lowercase()
            val isCorrect = r != "again"

            val updatedStats = state.sessionStats.copy(
                totalStudied = state.sessionStats.totalStudied + (if (isCorrect) 1 else 0),
                correctCount = if (isCorrect) state.sessionStats.correctCount + 1 else state.sessionStats.correctCount,
                againCount = state.sessionStats.againCount + if (r == "again") 1 else 0,
                hardCount = state.sessionStats.hardCount + if (r == "hard") 1 else 0,
                goodCount = state.sessionStats.goodCount + if (r == "good") 1 else 0,
                easyCount = state.sessionStats.easyCount + if (r == "easy") 1 else 0
            )
            
            if (rating.lowercase() == "again") {
                // Nếu bấm Again (<1m), thêm từ này vào cuối danh sách để lặp lại ngay trong phiên
                updatedCards.add(currentPair)
                _uiState.update { 
                    it.copy(
                        cards = updatedCards,
                        currentIndex = it.currentIndex + 1,
                        isFlipped = false,
                        sessionStats = updatedStats
                    ) 
                }
            } else {
                // Nếu nhớ rồi, chỉ chuyển sang từ tiếp theo
                if (state.currentIndex < updatedCards.size - 1) {
                    _uiState.update { 
                        it.copy(
                            currentIndex = it.currentIndex + 1,
                            isFlipped = false,
                            sessionStats = updatedStats
                        ) 
                    }
                } else {
                    _uiState.update { it.copy(isSessionComplete = true, sessionStats = updatedStats) }
                    saveStudySession(updatedStats)
                }
            }
        }
    }

    private fun saveStudySession(stats: SessionStats) {
        val state = _uiState.value
        val userId = getCurrentUserUseCase()?.userId ?: return
        
        viewModelScope.launch {
            val totalAttempts = stats.correctCount + stats.againCount
            val session = com.example.englishapp.core.data.model.StudySession(
                sessionId = java.util.UUID.randomUUID().toString(),
                userId = userId,
                setId = state.setId,
                date = System.currentTimeMillis(),
                duration = ((System.currentTimeMillis() - stats.startTime) / 1000).toInt(),
                wordsStudied = stats.totalStudied,
                accuracy = if (totalAttempts > 0) (stats.correctCount.toDouble() / totalAttempts) * 100.0 else 0.0,
                againCount = stats.againCount,
                hardCount = stats.hardCount,
                goodCount = stats.goodCount,
                easyCount = stats.easyCount,
                sessionType = state.sessionType
            )
            repository.saveStudySession(session)
        }
    }
}
