package com.example.englishapp.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
    val wordGoal: Int = 20
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

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
                                streakDays = 15, // Dữ liệu giả lập
                                wordsToday = 12, // Dữ liệu giả lập
                                wordGoal = result.data.dailyGoal
                            ) }
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
}
