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
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val firebaseUser = getCurrentUserUseCase()
        if (firebaseUser != null) {
            viewModelScope.launch {
                getUserDataUseCase(firebaseUser.userId).collect { result ->
                    _uiState.update { it.copy(authResult = result) }
                }
            }
        } else {
            _uiState.update { it.copy(authResult = null) }
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
                _uiState.update { it.copy(authResult = result) }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password).collect { result ->
                _uiState.update { it.copy(authResult = result) }
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = signInWithGoogleUseCase(idToken)
            _uiState.update { it.copy(authResult = result, isLoading = false) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(authResult = null) }
        }
    }

    fun updateUserProfile(goal: String, level: String) {
        viewModelScope.launch {
            updateProfileUseCase(goal, level).collect { result ->
                _uiState.update { it.copy(updateProfileResult = result) }
                if (result is AuthResult.Success) {
                    val firebaseUser = getCurrentUserUseCase()
                    if (firebaseUser != null) {
                        getUserDataUseCase(firebaseUser.userId).collect { userResult ->
                            _uiState.update { it.copy(authResult = userResult) }
                        }
                    }
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(updateProfileResult = null) }
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
