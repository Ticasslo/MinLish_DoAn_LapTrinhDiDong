package com.example.englishapp.features.profile.domain.usecase

import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke() {
        repository.logout()
    }
}
