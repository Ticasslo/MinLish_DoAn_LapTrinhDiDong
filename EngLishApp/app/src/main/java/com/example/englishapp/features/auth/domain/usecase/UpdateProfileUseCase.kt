package com.example.englishapp.features.auth.domain.usecase

import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(name: String, goal: String, level: String, pushEnabled: Boolean): Flow<AuthResult<Unit>> {
        return repository.updateUserProfile(name, goal, level, pushEnabled)
    }
}
