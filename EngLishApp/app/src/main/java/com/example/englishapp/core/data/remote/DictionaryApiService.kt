package com.example.englishapp.core.data.remote

import com.example.englishapp.features.vocab.data.model.DictionaryResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApiService {

    @GET("entries/en/{word}")
    suspend fun lookupWord(
        @Path("word") word: String
    ): List<DictionaryResponse>
}