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

    // Lấy user chưa được đồng bộ (phục vụ SyncWorker)
    @Query("SELECT * FROM users WHERE isSynced = 0 LIMIT 1")
    suspend fun getUnsyncedUser(): UserEntity?

    // Theo dõi người dùng theo ID (Dùng cho AuthRepository)
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun observeUserById(userId: String): Flow<UserEntity?>

    // Lấy user hiện tại (thường chỉ có 1 bản ghi trong app offline-first)
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("DELETE FROM users WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}
