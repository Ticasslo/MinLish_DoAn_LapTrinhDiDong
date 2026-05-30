package com.example.englishapp.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.usecase.*
import com.example.englishapp.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val authResult: AuthResult<User>? = null,
    val updateProfileResult: AuthResult<Unit>? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUserDataUseCase: GetUserDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeUserStatus()
    }

    private fun observeUserStatus() {
        viewModelScope.launch {
            // Quan sát sự thay đổi của User trong Local DB (Offline-first)
            getCurrentUserUseCase.observe().collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(authResult = AuthResult.Success(user)) }
                } else {
                    // Nếu Local DB chưa có (mới cài lại app hoặc login lần đầu), 
                    // nhưng Firebase đã login, thì lấy dữ liệu từ Server
                    val firebaseUser = getCurrentUserUseCase()
                    if (firebaseUser != null) {
                        fetchFullUserData(firebaseUser.userId)
                    }
                }
            }
        }
    }

    private fun fetchFullUserData(uid: String) {
        viewModelScope.launch {
            getUserDataUseCase(uid).collect { result ->
                if (result is AuthResult.Success) {
                    _uiState.update { it.copy(authResult = result) }
                }
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val user = User(
                name = name,
                email = email,
                createdAt = System.currentTimeMillis()
            )
            registerUseCase(user, password).collect { result ->
                _uiState.update { it.copy(
                    authResult = result,
                    isLoading = result is AuthResult.Loading
                ) }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password).collect { result ->
                _uiState.update { it.copy(
                    authResult = result,
                    isLoading = result is AuthResult.Loading
                ) }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, authResult = AuthResult.Loading) }
            val result = signInWithGoogleUseCase(idToken)
            _uiState.update { it.copy(
                authResult = result,
                isLoading = false
            ) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { AuthUiState() }
        }
    }

    fun updateUserProfile(goal: String, level: String, pushEnabled: Boolean) {
        viewModelScope.launch {
            updateProfileUseCase(goal, level, pushEnabled).collect { result ->
                _uiState.update { it.copy(
                    updateProfileResult = result,
                    isLoading = result is AuthResult.Loading
                ) }
            }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(
            authResult = null,
            updateProfileResult = null,
            isLoading = false
        ) }
    }

    fun getStartDestination(): String {
        val user = (_uiState.value.authResult as? AuthResult.Success)?.data
        return when {
            user == null -> Screen.Onboarding.route
            user.goal.isNullOrEmpty() -> Screen.Setup.route
            else -> Screen.Home.route
        }
    }
}
