package com.example.englishapp.features.vocab.data.model

/**
 * Định nghĩa cấu trúc dữ liệu trả về từ Free Dictionary API (https://api.dictionaryapi.dev/api/v2/entries/en/<word>)
 */
data class DictionaryResponse(
    val word: String = "",
    val phonetic: String? = null,
    val phonetics: List<PhoneticDto> = emptyList(),
    val meanings: List<MeaningDto> = emptyList()
)

data class PhoneticDto(
    val text: String? = null,
    val audio: String? = null
)

data class MeaningDto(
    val partOfSpeech: String = "",
    val definitions: List<DefinitionDto> = emptyList()
)

data class DefinitionDto(
    val definition: String = "",
    val example: String? = null,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList()
)