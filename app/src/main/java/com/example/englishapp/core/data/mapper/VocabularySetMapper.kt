package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.VocabularySet
import com.example.englishapp.core.data.local.entity.VocabularySetEntity

fun VocabularySetEntity.toDomain(): VocabularySet {
    return VocabularySet(
        setId = this.setId,
        userId = this.userId,
        name = this.name,
        description = this.description,
        // Xử lý chuỗi tags, tránh tạo ra list có 1 phần tử rỗng nếu chuỗi tags trống
        tags = if (this.tags.isEmpty()) emptyList() else this.tags.split(","),
        wordCount = this.wordCount,
        masteredCount = this.masteredCount,
        learningCount = this.learningCount,
        newCount = this.newCount,
        createdAt = this.createdAt,
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
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = false
    )
}
