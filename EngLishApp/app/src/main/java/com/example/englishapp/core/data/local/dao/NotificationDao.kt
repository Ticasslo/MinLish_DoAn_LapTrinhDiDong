package com.example.englishapp.core.data.local.dao

import androidx.room.*
import com.example.englishapp.core.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllNotifications(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1, isSynced = 0, updatedAt = :updatedAt WHERE notificationId = :notificationId")
    suspend fun markAsRead(notificationId: String, updatedAt: Long = System.currentTimeMillis())

    @Query("UPDATE notifications SET isRead = 1, isSynced = 0, updatedAt = :updatedAt WHERE userId = :userId AND isRead = 0")
    suspend fun markAllAsRead(userId: String, updatedAt: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE notificationId = :notificationId")
    suspend fun deleteNotificationById(notificationId: String)

    @Query("DELETE FROM notifications WHERE userId = :userId")
    suspend fun clearAllNotifications(userId: String)

    @Query("SELECT * FROM notifications WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedNotifications(userId: String): List<NotificationEntity>

    @Query("UPDATE notifications SET isSynced = 1, updatedAt = :updatedAt WHERE notificationId = :notificationId")
    suspend fun markAsSynced(notificationId: String, updatedAt: Long = System.currentTimeMillis())
}
