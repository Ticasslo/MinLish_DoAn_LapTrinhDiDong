package com.example.englishapp.features.vocab.domain.usecase

import com.example.englishapp.core.data.model.Word
import com.example.englishapp.core.util.CsvParser
import javax.inject.Inject

/**
 * UseCase nhập hàng loạt từ vựng (Import) từ nội dung văn bản CSV vào bộ từ vựng chỉ định
 */
class ImportCsvUseCase @Inject constructor(
    private val addWordUseCase: AddWordUseCase
) {
    suspend operator fun invoke(csvContent: String, setId: String, userId: String) {
        // 1. Phân tích cú pháp văn bản thô CSV
        val parsedWords = CsvParser.parseCsv(csvContent)
        
        // 2. Thêm từng từ vựng vào bộ thẻ (AddWordUseCase tự động tạo SrsCard và cập nhật bộ đếm)
        for (parsed in parsedWords) {
            val word = Word(
                wordId = "",
                setId = setId,
                userId = userId,
                word = parsed.word,
                pronunciation = parsed.pronunciation,
                meaning = parsed.meaning
            )
            addWordUseCase(word)
        }
    }
}