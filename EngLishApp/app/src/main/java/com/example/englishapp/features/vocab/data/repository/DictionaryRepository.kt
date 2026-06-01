package com.example.englishapp.features.vocab.data.repository

import com.example.englishapp.features.vocab.data.model.DictionaryResponse
import com.example.englishapp.features.vocab.domain.repository.IDictionaryRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
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

/**
 * Tra cứu thông tin từ vựng trực tuyến bằng cách gọi trực tiếp Free Dictionary API.
 * Ngoài ra còn hỗ trợ dịch nghĩa tiếng Việt qua MyMemory API (miễn phí, không cần API key).
 */
@Singleton
class DictionaryRepository @Inject constructor() : IDictionaryRepository {
    private val gson = Gson()

    override suspend fun lookupWord(word: String): DictionaryResponse? = withContext(Dispatchers.IO) {
        try {
            // Thiết lập địa chỉ API tra cứu từ vựng tiếng Anh
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
     * Dịch từ tiếng Anh sang tiếng Việt bằng MyMemory API (miễn phí, không cần API key).
     * Trả về chuỗi nghĩa tiếng Việt hoặc null nếu thất bại.
     */
    suspend fun translateToVietnamese(text: String): String? = withContext(Dispatchers.IO) {
        try {
            val encoded = URLEncoder.encode(text, "UTF-8")
            val urlString = "https://api.mymemory.translated.net/get?q=$encoded&langpair=en|vi"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 6000
            connection.readTimeout = 6000

            if (connection.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()

                val json = gson.fromJson(response.toString(), JsonObject::class.java)
                val translated = json
                    ?.getAsJsonObject("responseData")
                    ?.get("translatedText")
                    ?.asString
                if (!translated.isNullOrBlank() && translated != "NO QUERY SPECIFIED") translated else null
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}