package com.example.englishapp.features.vocab.domain.repository

import com.example.englishapp.features.vocab.data.model.DictionaryResponse

/**
 * Giao diện tra cứu từ điển trực tuyến
 */
interface IDictionaryRepository {
    suspend fun lookupWord(word: String): DictionaryResponse?
}