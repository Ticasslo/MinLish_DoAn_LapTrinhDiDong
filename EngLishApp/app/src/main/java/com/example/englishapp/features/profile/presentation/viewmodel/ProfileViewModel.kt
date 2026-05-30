package com.example.englishapp.features.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import com.example.englishapp.features.profile.domain.usecase.ChangePasswordUseCase
import com.example.englishapp.features.profile.domain.usecase.GetUserDataUseCase
import com.example.englishapp.features.profile.domain.usecase.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.englishapp.core.util.ThemeManager

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedOut: Boolean = false,
    val changePasswordResult: AuthResult<Unit>? = null,
    val settingsSaved: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val authRepository: IAuthRepository,
    private val themeManager: ThemeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    val isDarkMode: StateFlow<Boolean> = themeManager.isDarkMode

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

    /**
     * Lưu cài đặt học tập (dailyGoal, reminderTime, pushEnabled) vào Local + Firestore
     */
    fun updateSettings(dailyGoal: Int, reminderTime: String, pushEnabled: Boolean) {
        viewModelScope.launch {
            authRepository.updateUserSettings(dailyGoal, reminderTime, pushEnabled).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update { it.copy(settingsSaved = true) }
                        // Reload user data để cập nhật UI
                        loadUserData()
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is AuthResult.Loading -> { /* ignore */ }
                }
            }
        }
    }

    fun resetSettingsSavedState() {
        _uiState.update { it.copy(settingsSaved = false) }
    }

    fun setDarkMode(isDark: Boolean) {
        themeManager.setDarkMode(isDark)
    }

    fun updateAvatar(uri: String) {
        viewModelScope.launch {
            authRepository.updateUserAvatar(uri).collect { result ->
                when (result) {
                    is AuthResult.Success -> {
                        _uiState.update { it.copy(settingsSaved = true) }
                        loadUserData()
                    }
                    is AuthResult.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is AuthResult.Loading -> { /* ignore */ }
                }
            }
        }
    }
}
