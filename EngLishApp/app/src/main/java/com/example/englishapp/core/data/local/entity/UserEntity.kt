package com.example.englishapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String,
    val name: String,
    val email: String,
    val avatar: String? = null,
    val goal: String = "",
    val level: String = "",
    val estimatedLevel: String? = null,
    val dailyGoal: Int = 10,
    val reminderTime: String = "20:00",
    val pushEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false // Theo dõi trạng thái đã được sync với Firestore hay chưa
)
