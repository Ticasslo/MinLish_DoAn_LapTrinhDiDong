package com.example.englishapp.features.auth.domain.usecase

import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    operator fun invoke(): User? = repository.getCurrentUser()
}
