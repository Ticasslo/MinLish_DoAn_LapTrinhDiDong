package com.example.englishapp.features.vocab.domain.repository

import com.example.englishapp.features.vocab.data.model.DictionaryResponse

/**
 * Giao diện tra cứu từ điển trực tuyến và dịch ngôn ngữ
 */
interface IDictionaryRepository {
    /** Tra cứu phiên âm IPA, định nghĩa và ví dụ của một từ tiếng Anh */
    suspend fun lookupWord(word: String): DictionaryResponse?

    /**
     * Dịch văn bản giữa hai ngôn ngữ qua MyMemory API (miễn phí, không cần key)
     * @param text Văn bản cần dịch
     * @param fromLang Mã ngôn ngữ nguồn ("en" hoặc "vi")
     * @param toLang Mã ngôn ngữ đích ("vi" hoặc "en")
     * @return Văn bản đã dịch, hoặc null nếu thất bại
     */
    suspend fun translate(text: String, fromLang: String, toLang: String): String?
}