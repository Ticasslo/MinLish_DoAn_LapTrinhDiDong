package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.StudySession
import com.example.englishapp.core.data.local.entity.StudySessionEntity

fun StudySessionEntity.toDomain(): StudySession {
    return StudySession(
        sessionId = this.sessionId,
        userId = this.userId,
        setId = this.setId,
        sessionType = this.sessionType,
        date = this.date,
        wordsStudied = this.wordsStudied,
        accuracy = this.accuracy,
        duration = this.duration,
        againCount = this.againCount,
        hardCount = this.hardCount,
        goodCount = this.goodCount,
        easyCount = this.easyCount,
        updatedAt = this.updatedAt
    )
}

fun StudySession.toEntity(): StudySessionEntity {
    return StudySessionEntity(
        sessionId = this.sessionId,
        userId = this.userId,
        setId = this.setId,
        sessionType = this.sessionType,
        date = this.date,
        wordsStudied = this.wordsStudied,
        accuracy = this.accuracy,
        duration = this.duration,
        againCount = this.againCount,
        hardCount = this.hardCount,
        goodCount = this.goodCount,
        easyCount = this.easyCount,
        isSynced = false,
        updatedAt = this.updatedAt
    )
}
