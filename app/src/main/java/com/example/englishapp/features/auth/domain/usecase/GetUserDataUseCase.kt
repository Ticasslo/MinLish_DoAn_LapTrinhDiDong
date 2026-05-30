package com.example.englishapp.features.auth.domain.usecase

import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(uid: String): Flow<AuthResult<User>> = repository.getUserData(uid)
}
