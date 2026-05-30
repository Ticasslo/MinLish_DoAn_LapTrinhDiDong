package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.core.util.CsvParser
import com.example.englishapp.features.vocab.domain.repository.IVocabRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * UseCase kết xuất (Export) toàn bộ từ vựng thuộc về một bộ từ vựng sang chuỗi định dạng CSV
 */
class ExportCsvUseCase @Inject constructor(
    private val repository: IVocabRepository
) {
    suspend operator fun invoke(setId: String): String {
        // Lấy danh sách từ vựng hiện tại của bộ từ
        val words = repository.getWords(setId).first()
        
        // Chuyển đổi danh sách từ thành chuỗi CSV văn bản thô
        return CsvParser.toCsv(words)
    }
}