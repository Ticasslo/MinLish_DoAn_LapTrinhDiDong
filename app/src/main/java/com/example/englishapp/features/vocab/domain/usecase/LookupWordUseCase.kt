package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.features.vocab.data.model.DictionaryResponse
import com.example.englishapp.features.vocab.domain.repository.IDictionaryRepository
import javax.inject.Inject

/**
 * UseCase tra cứu từ vựng tiếng Anh trực tuyến bằng API
 */
class LookupWordUseCase @Inject constructor(
    private val repository: IDictionaryRepository
) {
    suspend operator fun invoke(word: String): DictionaryResponse? {
        if (word.isBlank()) return null
        return repository.lookupWord(word)
    }
}