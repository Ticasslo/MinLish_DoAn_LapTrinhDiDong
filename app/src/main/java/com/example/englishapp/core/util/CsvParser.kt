package com.example.englishapp.core.util

import com.example.englishapp.core.data.model.Word

/**
 * Lớp ParsedWord chứa dữ liệu từ vựng thô được phân tích từ file CSV
 */
data class ParsedWord(
    val word: String,
    val meaning: String,
    val pronunciation: String? = null
)

/**
 * Bộ tiện ích phân tích cú pháp CSV đơn giản, hỗ trợ import và export từ vựng.
 * Phong cách viết mã nguồn trực quan, dễ hiểu cho người mới bắt đầu lập trình Android.
 */
object CsvParser {

    /**
     * Chuyển đổi chuỗi văn bản CSV thành danh sách các ParsedWord
     */
    fun parseCsv(csvContent: String): List<ParsedWord> {
        val resultList = mutableListOf<ParsedWord>()
        
        // Chia văn bản thành các dòng độc lập
        val lines = csvContent.split("\n")
        
        for (i in lines.indices) {
            val line = lines[i].trim()
            
            // Bỏ qua dòng trống hoặc dòng tiêu đề (Header) của file CSV
            if (line.isEmpty() || (i == 0 && line.lowercase().contains("từ vựng") && line.lowercase().contains("định nghĩa"))) {
                continue
            }
            
            // Phân tách các trường dữ liệu bằng dấu phẩy
            // Hỗ trợ cấu trúc cơ bản: Từ vựng, Định nghĩa, Phiên âm
            val tokens = parseCsvLine(line)
            if (tokens.isNotEmpty()) {
                val word = tokens[0].trim().removeSurrounding("\"")
                val meaning = if (tokens.size > 1) tokens[1].trim().removeSurrounding("\"") else ""
                val pronunciation = if (tokens.size > 2) tokens[2].trim().removeSurrounding("\"") else null
                
                // Chỉ thêm từ vựng hợp lệ (không rỗng cả từ và nghĩa)
                if (word.isNotEmpty() && meaning.isNotEmpty()) {
                    resultList.add(ParsedWord(word, meaning, pronunciation))
                }
            }
        }
        return resultList
    }

    /**
     * Xuất danh sách từ vựng thành định dạng chuỗi CSV chuẩn
     */
    fun toCsv(words: List<Word>): String {
        val sb = StringBuilder()
        
        // Thêm tiêu đề cột (Header Line)
        sb.append("Từ vựng,Định nghĩa,Phiên âm\n")
        
        for (w in words) {
            val cleanWord = escapeCsvField(w.word)
            val cleanMeaning = escapeCsvField(w.meaning)
            val cleanPron = escapeCsvField(w.pronunciation ?: "")
            
            sb.append("$cleanWord,$cleanMeaning,$cleanPron\n")
        }
        return sb.toString()
    }

    // Hàm phân tích 1 dòng CSV phức tạp hơn để hỗ trợ dấu phẩy trong ngoặc kép
    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        var inQuotes = false
        var currentToken = StringBuilder()
        
        var j = 0
        while (j < line.length) {
            val c = line[j]
            if (c == '"') {
                inQuotes = !inQuotes
            } else if (c == ',' && !inQuotes) {
                tokens.add(currentToken.toString())
                currentToken = StringBuilder()
            } else {
                currentToken.append(c)
            }
            j++
        }
        tokens.add(currentToken.toString())
        return tokens
    }

    // Bao bọc chuỗi bằng dấu nháy kép và escape nháy kép bên trong nếu cần thiết
    private fun escapeCsvField(field: String): String {
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            val escaped = field.replace("\"", "\"\"")
            return "\"$escaped\""
        }
        return field
    }
}