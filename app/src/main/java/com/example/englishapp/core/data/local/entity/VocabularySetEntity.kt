package com.example.englishapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "vocabulary_sets",
    indices = [Index(value = ["userId"])]
)
data class VocabularySetEntity(
    @PrimaryKey val setId: String,
    val userId: String,
    val name: String,
    val description: String,
    val tags: String, 
    val wordCount: Int = 0,
    val masteredCount: Int = 0,
    val learningCount: Int = 0,
    val newCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
