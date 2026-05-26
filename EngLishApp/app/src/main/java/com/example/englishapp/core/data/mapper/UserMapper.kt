package com.example.englishapp.core.data.mapper

import com.example.englishapp.core.data.model.User
import com.example.englishapp.core.data.local.entity.UserEntity
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toDomain(): User {
    return User(
        userId = this.uid,
        name = this.displayName ?: "",
        email = this.email ?: "",
        avatar = this.photoUrl?.toString(),
        createdAt = this.metadata?.creationTimestamp ?: System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )
}

fun UserEntity.toDomain(): User {
    return User(
        userId = this.userId,
        name = this.name,
        email = this.email,
        avatar = this.avatar,
        goal = this.goal,
        level = this.level,
        estimatedLevel = this.estimatedLevel,
        dailyGoal = this.dailyGoal,
        reminderTime = this.reminderTime,
        pushEnabled = this.pushEnabled,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = this.userId,
        name = this.name,
        email = this.email,
        avatar = this.avatar,
        goal = this.goal,
        level = this.level,
        estimatedLevel = this.estimatedLevel,
        dailyGoal = this.dailyGoal,
        reminderTime = this.reminderTime,
        pushEnabled = this.pushEnabled,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isSynced = false
    )
}
