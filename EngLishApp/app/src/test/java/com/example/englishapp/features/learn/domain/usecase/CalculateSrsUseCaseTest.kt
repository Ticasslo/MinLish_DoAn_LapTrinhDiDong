package com.example.englishapp.features.learn.domain.usecase

import com.example.englishapp.core.data.model.SrsCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculateSrsUseCaseTest {

    private lateinit var calculateSrsUseCase: CalculateSrsUseCase
    private val initialCard = SrsCard(
        cardId = "card_1",
        userId = "user_1",
        wordId = "word_1",
        setId = "set_1",
        status = "new",
        easeFactor = 2.5,
        interval = 1,
        repetitions = 0
    )

    @Before
    fun setUp() {
        calculateSrsUseCase = CalculateSrsUseCase()
    }

    @Test
    fun `rating GOOD for new card should set interval to 1`() {
        val result = calculateSrsUseCase(initialCard, "good")
        assertEquals(1, result.interval)
        assertEquals(1, result.repetitions)
        assertEquals("learning", result.status)
    }

    @Test
    fun `rating EASY for new card should set interval to 4`() {
        val result = calculateSrsUseCase(initialCard, "easy")
        assertEquals(4, result.interval)
        assertEquals(1, result.repetitions)
        assertEquals("mastered", result.status)
        assertTrue(result.easeFactor > 2.5)
    }

    @Test
    fun `rating AGAIN should reset interval to 1 and repetitions to 0`() {
        val masteredCard = initialCard.copy(
            status = "mastered",
            interval = 10,
            repetitions = 5,
            easeFactor = 2.8
        )
        val result = calculateSrsUseCase(masteredCard, "again")
        assertEquals(1, result.interval)
        assertEquals(0, result.repetitions)
        assertEquals("learning", result.status)
        assertTrue(result.easeFactor < 2.8)
    }

    @Test
    fun `rating GOOD for existing card should increase interval based on ease factor`() {
        val learningCard = initialCard.copy(
            status = "learning",
            interval = 6,
            repetitions = 2,
            easeFactor = 2.5
        )
        val result = calculateSrsUseCase(learningCard, "good")
        // repetitions becomes 3, interval becomes 6 * 2.5 = 15
        assertEquals(15, result.interval)
        assertEquals(3, result.repetitions)
        assertEquals("mastered", result.status)
    }
}
