package com.example.englishapp.features.auth.domain.usecase

import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: IAuthRepository
) {
    suspend operator fun invoke(idToken: String) = repository.signInWithGoogle(idToken)
}
