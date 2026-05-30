package com.example.englishapp.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _resetState = MutableStateFlow<AuthResult<Unit>?>(null)
    val resetState = _resetState.asStateFlow()

    fun resetPassword(email: String) {
        viewModelScope.launch {
            resetPasswordUseCase(email).collect { result ->
                _resetState.value = result
            }
        }
    }

    fun resetState() {
        _resetState.value = null
    }
}
