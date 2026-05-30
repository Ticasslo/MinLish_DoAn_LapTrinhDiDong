package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.SrsCard
import com.example.englishapp.core.data.local.entity.SrsCardEntity

fun SrsCardEntity.toDomain(): SrsCard {
    return SrsCard(
        cardId = this.cardId,
        userId = this.userId,
        wordId = this.wordId,
        setId = this.setId,
        status = this.status,
        easeFactor = this.easeFactor,
        interval = this.interval,
        repetitions = this.repetitions,
        nextReview = this.nextReview,
        lastReview = this.lastReview,
        lastRating = this.lastRating,
        updatedAt = this.updatedAt
    )
}

fun SrsCard.toEntity(): SrsCardEntity {
    return SrsCardEntity(
        cardId = this.cardId,
        userId = this.userId,
        wordId = this.wordId,
        setId = this.setId,
        status = this.status,
        easeFactor = this.easeFactor,
        interval = this.interval,
        repetitions = this.repetitions,
        nextReview = this.nextReview,
        lastReview = this.lastReview,
        lastRating = this.lastRating,
        updatedAt = this.updatedAt,
        isSynced = false
    )
}
