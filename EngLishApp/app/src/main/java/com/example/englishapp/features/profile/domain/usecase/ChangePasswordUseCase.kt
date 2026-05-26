package com.example.englishapp.features.profile.domain.usecase

import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(current: String, new: String): Flow<AuthResult<Unit>> {
        return repository.changePassword(current, new)
    }
}
