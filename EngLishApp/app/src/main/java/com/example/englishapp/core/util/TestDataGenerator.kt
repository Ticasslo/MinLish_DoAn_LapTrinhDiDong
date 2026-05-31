package com.example.englishapp.core.util

import com.example.englishapp.core.data.model.*
import com.example.englishapp.core.data.remote.FirebaseService
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestDataGenerator @Inject constructor(
    private val firebaseService: FirebaseService
) {
    suspend fun generateTestData(userId: String) {
        // 0. Xóa hết dữ liệu cũ trên Firebase trước khi tạo mới để tránh bị trùng lặp/rác dữ liệu
        clearExistingData(userId)

        // 1. Chỉ cập nhật một vài thông tin cần thiết cho User hiện tại, không tạo mới object User hoàn toàn
        // Điều này giúp giữ nguyên các thông tin quan trọng khác của account đang login
        firebaseService.getUser(userId)?.let { currentUser ->
            val updatedUser = currentUser.copy(
                goal = "IELTS",
                level = "B2",
                dailyGoal = 20,
                updatedAt = System.currentTimeMillis()
            )
            firebaseService.saveUser(updatedUser)
        }

        // 2. Tạo 3 bộ từ vựng mẫu
        val sets = listOf(
            VocabularySet(
                setId = UUID.randomUUID().toString(),
                userId = userId,
                name = "IELTS Academic - Essential",
                description = "Các từ vựng cốt lõi cho kỳ thi IELTS Academic",
                tags = listOf("IELTS", "Academic"),
                wordCount = 10,
                masteredCount = 5,
                learningCount = 3,
                newCount = 2
            ),
            VocabularySet(
                setId = UUID.randomUUID().toString(),
                userId = userId,
                name = "Business Communication",
                description = "Tiếng Anh giao tiếp trong môi trường công sở",
                tags = listOf("Business", "Communication"),
                wordCount = 8,
                masteredCount = 2,
                learningCount = 4,
                newCount = 2
            ),
            VocabularySet(
                setId = UUID.randomUUID().toString(),
                userId = userId,
                name = "Daily Life Phrases",
                description = "Các cụm từ thông dụng hàng ngày",
                tags = listOf("Travel", "Communication"),
                wordCount = 12,
                masteredCount = 10,
                learningCount = 2,
                newCount = 0
            )
        )

        for (set in sets) {
            firebaseService.saveVocabularySet(set)
            
            // 3. Tạo các từ cho từng bộ
            val words = generateWordsForSet(userId, set.setId, set.name)
            for (word in words) {
                firebaseService.saveWord(word)
                
                // 4. Tạo SRS Card tương ứng cho mỗi từ
                val card = generateSrsCardForWord(userId, word.wordId, set.setId)
                firebaseService.saveSrsCard(card)
            }
        }

        // 5. Tạo Streak dữ liệu
        val streak = Streak(
            userId = userId,
            currentStreak = 7,
            longestStreak = 15,
            lastStudyDate = System.currentTimeMillis()
        )
        firebaseService.saveStreak(streak)

        // 5b. Tạo dữ liệu StudySession cho 7 ngày qua để hiển thị biểu đồ Progress
        generateStudySessions(userId, sets.map { it.setId }).forEach { session ->
            firebaseService.saveStudySession(session)
        }

        // 6. Tạo một vài thông báo
        val notification = Notification(
            notificationId = UUID.randomUUID().toString(),
            userId = userId,
            title = "Chào mừng bạn!",
            body = "Tài khoản test của bạn đã được khởi tạo đầy đủ dữ liệu học tập.",
            type = "SYSTEM",
            isRead = false,
            createdAt = System.currentTimeMillis()
        )
        firebaseService.saveNotification(notification)
    }

    private suspend fun clearExistingData(userId: String) {
        val db = firebaseService.firestore
        
        // Danh sách các collection cần dọn dẹp
        val collections = listOf("vocabulary_sets", "words", "srs_cards", "study_sessions", "notifications")
        
        for (collectionName in collections) {
            val snapshot = db.collection(collectionName)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            
            if (!snapshot.isEmpty) {
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit().await()
            }
        }
    }

    private fun generateWordsForSet(userId: String, setId: String, setName: String): List<Word> {
        val list = mutableListOf<Word>()
        
        val rawWords = when {
            setName.contains("IELTS") -> listOf(
                Triple("Accomplish", "/əˈkʌmplɪʃ/", "Hoàn thành, đạt được"),
                Triple("Accumulate", "/əˈkjuːmjəleɪt/", "Tích lũy"),
                Triple("Beneficial", "/ˌbenɪˈfɪʃl/", "Có lợi"),
                Triple("Compensate", "/ˈkɒmpenseɪt/", "Đền bù, bồi thường"),
                Triple("Diligent", "/ˈdɪlɪdʒənt/", "Cần cù, siêng năng"),
                Triple("Equivalent", "/ɪˈkwɪvələnt/", "Tương đương"),
                Triple("Feasible", "/ˈfiːzəbl/", "Khả thi"),
                Triple("Guarantee", "/ˌɡærənˈtiː/", "Bảo hành, cam kết"),
                Triple("Hierarchy", "/ˈhaɪərɑːki/", "Hệ thống cấp bậc"),
                Triple("Inevitably", "/ɪnˈevɪtəbli/", "Chắc chắn xảy ra")
            )
            setName.contains("Business") -> listOf(
                Triple("Negotiate", "/nɪˈɡəʊʃieɪt/", "Đàm phán"),
                Triple("Collaborate", "/kəˈlæbəreɪt/", "Hợp tác"),
                Triple("Revenue", "/ˈrevənjuː/", "Doanh thu"),
                Triple("Strategic", "/strəˈtiːdʒɪk/", "Chiến lược"),
                Triple("Acquisition", "/ˌækwɪˈzɪʃn/", "Sự mua lại (công ty)"),
                Triple("Bankruptcy", "/ˈbæŋkrəptsi/", "Sự phá sản"),
                Triple("Compliance", "/kəmˈplaɪəns/", "Sự tuân thủ"),
                Triple("Dividend", "/ˈdɪvɪdend/", "Cổ tức"),
                Triple("Equity", "/ˈekwəti/", "Vốn chủ sở hữu"),
                Triple("Liability", "/ˌlaɪəˈbɪləti/", "Nợ phải trả")
            )
            else -> listOf(
                Triple("Grateful", "/ˈɡreɪtfl/", "Biết ơn"),
                Triple("Sincere", "/sɪnˈsɪə(r)/", "Chân thành"),
                Triple("Optimistic", "/ˌɒptɪˈmɪstɪk/", "Lạc quan"),
                Triple("Courageous", "/kəˈreɪdʒəs/", "Dũng cảm"),
                Triple("Patience", "/ˈpeɪʃns/", "Kiên nhẫn"),
                Triple("Generous", "/ˈdʒenərəs/", "Hào phóng"),
                Triple("Humility", "/hjuːˈmɪləti/", "Sự khiêm tốn"),
                Triple("Reliable", "/rɪˈlaɪəbl/", "Đáng tin cậy"),
                Triple("Enthusiastic", "/ɪnˌθjuːziˈæstɪk/", "Nhiệt tình"),
                Triple("Thoughtful", "/ˈθɔːtfl/", "Chu đáo")
            )
        }

        for (item in rawWords) {
            list.add(Word(
                wordId = UUID.randomUUID().toString(),
                setId = setId,
                userId = userId,
                word = item.first,
                meaning = item.third,
                pronunciation = item.second,
                example = "This is an example sentence for the word '${item.first}'.",
                note = "Ghi chú mẫu cho từ ${item.first}"
            ))
        }
        return list
    }

    private fun generateStudySessions(userId: String, setIds: List<String>): List<StudySession> {
        val list = mutableListOf<StudySession>()
        val calendar = java.util.Calendar.getInstance()
        
        // Tạo dữ liệu cho 7 ngày qua
        for (i in 0..6) {
            val date = calendar.timeInMillis
            val words = (10..30).random()
            val accuracy = (70..95).random().toDouble()
            
            list.add(StudySession(
                sessionId = UUID.randomUUID().toString(),
                userId = userId,
                setId = setIds.random(),
                sessionType = if (i % 2 == 0) "review" else "new",
                date = date,
                wordsStudied = words,
                accuracy = accuracy,
                duration = (300..900).random(),
                goodCount = (words * 0.6).toInt(),
                easyCount = (words * 0.2).toInt(),
                hardCount = (words * 0.1).toInt(),
                againCount = (words * 0.1).toInt()
            ))
            
            calendar.add(java.util.Calendar.DAY_OF_YEAR, -1)
        }
        return list
    }

    private fun generateSrsCardForWord(userId: String, wordId: String, setId: String): SrsCard {
        val rand = (0..10).random()
        val status = when {
            rand < 4 -> "mastered"
            rand < 8 -> "learning"
            else -> "new"
        }
        
        return SrsCard(
            cardId = UUID.randomUUID().toString(),
            userId = userId,
            wordId = wordId,
            setId = setId,
            status = status,
            easeFactor = if (status == "mastered") 2.5 else 2.1,
            interval = if (status == "mastered") 10 else 1,
            repetitions = if (status == "mastered") 4 else 1,
            nextReview = if (status == "mastered") System.currentTimeMillis() + 86400000 * 5 else System.currentTimeMillis()
        )
    }
}
