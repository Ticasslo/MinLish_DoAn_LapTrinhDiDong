package com.example.englishapp.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.profile.domain.usecase.ChangePasswordUseCase
import com.example.englishapp.features.profile.domain.usecase.GetUserDataUseCase
import com.example.englishapp.features.profile.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedOut: Boolean = false,
    val changePasswordResult: AuthResult<Unit>? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getUserDataUseCase().collect { result ->
                when (result) {
                    is AuthResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is AuthResult.Success -> {
                        _uiState.update { it.copy(user = result.data, isLoading = false) }
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }

    fun changePassword(current: String, new: String) {
        viewModelScope.launch {
            changePasswordUseCase(current, new).collect { result ->
                _uiState.update { it.copy(changePasswordResult = result) }
            }
        }
    }

    fun resetChangePasswordState() {
        _uiState.update { it.copy(changePasswordResult = null) }
    }
}
