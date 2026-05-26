package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.core.data.local.entity.VocabularySetEntity

fun VocabularySetEntity.toDomain(): VocabularySet {
    return VocabularySet(
        setId = this.setId,
        userId = this.userId,
        name = this.name,
        description = this.description ?: "",
        tags = this.tags.split(","),
        wordCount = this.wordCount,
        masteredCount = this.masteredCount,
        learningCount = this.learningCount,
        newCount = this.newCount,
        updatedAt = this.updatedAt
    )
}

fun VocabularySet.toEntity(): VocabularySetEntity {
    return VocabularySetEntity(
        setId = this.setId,
        userId = this.userId,
        name = this.name,
        description = this.description,
        tags = this.tags.joinToString(","),
        wordCount = this.wordCount,
        masteredCount = this.masteredCount,
        learningCount = this.learningCount,
        newCount = this.newCount,
        updatedAt = this.updatedAt,
        isSynced = false
    )
}
