package com.example.englishapp.features.auth.domain.usecase

import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(user: User, password: String): Flow<AuthResult<User>> {
        return repository.register(user, password)
    }
}
