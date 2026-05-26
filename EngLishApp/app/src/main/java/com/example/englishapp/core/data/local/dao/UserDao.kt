package com.example.englishapp.core.data.local.dao

import androidx.room.*
import com.example.englishapp.core.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Upsert
    suspend fun upsertUser(user: UserEntity)

    // Đánh dấu người dùng đã được đồng bộ từ Firestore vào local
    @Query("UPDATE users SET isSynced = 1, updatedAt = :updatedAt WHERE userId = :userId")
    suspend fun markUserAsSynced(userId: String, updatedAt: Long = System.currentTimeMillis())

    // Nếu có một user nào đó cần được lấy từ local để hiển thị (ví dụ, user đang đăng nhập)
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
