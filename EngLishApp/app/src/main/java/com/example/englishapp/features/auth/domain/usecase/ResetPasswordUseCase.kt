package com.example.englishapp.features.auth.domain.usecase

import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(email: String): Flow<AuthResult<Unit>> {
        return repository.sendPasswordResetEmail(email)
    }
}
