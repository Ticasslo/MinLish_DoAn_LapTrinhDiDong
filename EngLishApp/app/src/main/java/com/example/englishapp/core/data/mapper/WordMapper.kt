package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.Word
import com.example.englishapp.core.data.local.entity.WordEntity

fun WordEntity.toDomain(): Word {
    return Word(
        wordId = this.wordId,
        setId = this.setId,
        userId = this.userId,
        word = this.word,
        pronunciation = this.pronunciation,
        meaning = this.meaning,
        description = this.description,
        example = this.example,
        collocation = this.collocation,
        relatedWords = this.relatedWords,
        note = this.note,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Word.toEntity(): WordEntity {
    return WordEntity(
        wordId = this.wordId,
        setId = this.setId,
        userId = this.userId,
        word = this.word,
        pronunciation = this.pronunciation,
        meaning = this.meaning,
        description = this.description,
        example = this.example,
        collocation = this.collocation,
        relatedWords = this.relatedWords,
        note = this.note,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = false
    )
}
