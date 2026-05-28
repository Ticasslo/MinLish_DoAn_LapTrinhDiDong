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
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Tra cứu thông tin từ vựng trực tuyến bằng cách gọi trực tiếp Free Dictionary API
 * và phân tích kết quả JSON sử dụng thư viện Gson phổ biến.
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
            connection.connectTimeout = 5000 // Chờ tối đa 5 giây để kết nối
            connection.readTimeout = 5000    // Chờ tối đa 5 giây để đọc dữ liệu
            
            val responseCode = connection.responseCode
            if (responseCode == 200) {
                // Đọc luồng dữ liệu trả về
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                
                // Kết quả trả về dạng mảng JSON [ {...} ], chuyển đổi phần tử đầu tiên
                val itemType = object : TypeToken<List<DictionaryResponse>>() {}.type
                val resultList: List<DictionaryResponse> = gson.fromJson(response.toString(), itemType)
                if (resultList.isNotEmpty()) {
                    resultList[0]
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}