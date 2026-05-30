package com.example.englishapp.features.vocab.data.repository

import com.example.englishapp.features.vocab.data.model.DictionaryResponse
import com.example.englishapp.features.vocab.domain.repository.IDictionaryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

// Model nội bộ để parse phản hồi từ MyMemory API
private data class MyMemoryResponse(val responseData: MyMemoryData?)
private data class MyMemoryData(val translatedText: String?)

/**
 * Tra cứu thông tin từ vựng trực tuyến bằng cách gọi trực tiếp Free Dictionary API
 * và dịch ngôn ngữ qua MyMemory API (miễn phí, không cần API key).
 */
@Singleton
class DictionaryRepository @Inject constructor() : IDictionaryRepository {
    private val gson = Gson()

    override suspend fun lookupWord(word: String): DictionaryResponse? = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.dictionaryapi.dev/api/v2/entries/en/${word.trim()}"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val itemType = object : TypeToken<List<DictionaryResponse>>() {}.type
                val resultList: List<DictionaryResponse> = gson.fromJson(response.toString(), itemType)
                if (resultList.isNotEmpty()) resultList[0] else null
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Dịch văn bản qua MyMemory API.
     * Ví dụ: translate("accomplish", "en", "vi") → "hoàn thành"
     *        translate("kiên cường", "vi", "en") → "resilient"
     */
    override suspend fun translate(text: String, fromLang: String, toLang: String): String? = withContext(Dispatchers.IO) {
        try {
            val encodedText = URLEncoder.encode(text.trim(), "UTF-8")
            val urlString = "https://api.mymemory.translated.net/get?q=$encodedText&langpair=$fromLang|$toLang"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val myMemoryResp = gson.fromJson(response.toString(), MyMemoryResponse::class.java)
                myMemoryResp?.responseData?.translatedText
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}