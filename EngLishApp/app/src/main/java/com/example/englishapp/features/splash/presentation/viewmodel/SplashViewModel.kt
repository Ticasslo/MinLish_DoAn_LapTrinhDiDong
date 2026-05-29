package com.example.englishapp.features.splash.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.englishapp.features.auth.domain.usecase.GetCurrentUserUseCase
import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val vocabRepository: IVocabRepository
) : ViewModel() {

    fun initAppData() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            user?.let {
                vocabRepository.seedSampleData(it.userId)
            }
        }
    }
}
