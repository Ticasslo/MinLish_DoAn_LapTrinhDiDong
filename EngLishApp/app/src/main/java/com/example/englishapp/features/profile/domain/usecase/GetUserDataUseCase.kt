package com.example.englishapp.features.profile.domain.usecase

import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(): Flow<AuthResult<User>> {
        val currentUser = repository.getCurrentUser()
        return if (currentUser != null) {
            repository.getUserData(currentUser.userId)
        } else {
            flowOf(AuthResult.Error("User not logged in"))
        }
    }
}
