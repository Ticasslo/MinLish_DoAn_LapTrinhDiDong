package com.example.englishapp.features.learn.domain.usecase

import com.example.englishapp.core.data.model.SrsCard
import javax.inject.Inject
import kotlin.math.max

/**
 * CalculateSrsUseCase triển khai thuật toán SM-2 (SuperMemo-2) để tính toán
 * các chỉ số Spaced Repetition cho một thẻ từ vựng.
 */
class CalculateSrsUseCase @Inject constructor() {

    operator fun invoke(card: SrsCard, rating: String): SrsCard {
        val now = System.currentTimeMillis()
        var easeFactor = card.easeFactor
        var interval = card.interval
        var repetitions = card.repetitions
        val status: String

        when (rating.lowercase()) {
            "again" -> {
                // Quên từ: Reset lại quá trình học
                repetitions = 0
                interval = 1
                // Giảm easeFactor (từ này khó hơn chúng ta nghĩ)
                easeFactor = max(1.3, easeFactor - 0.2)
                status = "learning"
            }
            "hard" -> {
                // Khó nhớ: Tăng interval chậm, giảm nhẹ easeFactor
                repetitions += 1
                interval = if (repetitions <= 1) 1 else (interval * 1.2).toInt()
                easeFactor = max(1.3, easeFactor - 0.15)
                status = if (repetitions >= 3) "mastered" else "learning"
            }
            "good" -> {
                // Nhớ bình thường: Tăng interval theo easeFactor
                repetitions += 1
                interval = when (repetitions) {
                    1 -> 1
                    2 -> 6
                    else -> (interval * easeFactor).toInt()
                }
                // easeFactor giữ nguyên hoặc thay đổi cực ít
                status = if (repetitions >= 3) "mastered" else "learning"
            }
            "easy" -> {
                // Rất dễ: Tăng interval nhanh hơn, tăng easeFactor
                repetitions += 1
                interval = when (repetitions) {
                    1 -> 4
                    2 -> 10
                    else -> (interval * easeFactor * 1.3).toInt()
                }
                easeFactor += 0.15
                status = "mastered"
            }
            else -> return card // Không đổi nếu rating không hợp lệ
        }

        // Đảm bảo interval tối thiểu là 1 ngày
        val finalInterval = max(1, interval)
        
        // Tính ngày review tiếp theo: hôm nay + interval (mili giây)
        val nextReview = now + (finalInterval.toLong() * 24 * 60 * 60 * 1000)

        return card.copy(
            status = status,
            easeFactor = easeFactor,
            interval = finalInterval,
            repetitions = repetitions,
            nextReview = nextReview,
            lastReview = now,
            lastRating = rating,
            updatedAt = now
        )
    }
}
