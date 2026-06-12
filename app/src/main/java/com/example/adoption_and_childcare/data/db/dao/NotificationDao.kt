package com.example.adoption_and_childcare.data.db.dao

import androidx.room.*
import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for notification-related database operations.
 * 
 * This interface provides methods for managing user notifications,
 * including marking as read and filtering by user.
 */
@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY created_at DESC")
    fun observeAll(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    fun getNotificationsForUser(userId: Int): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = 0")
    fun getUnreadNotificationsForUser(userId: Int): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE user_id = :userId AND is_read = 0")
    suspend fun getUnreadNotificationCount(userId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Update
    suspend fun update(notification: NotificationEntity)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("SELECT * FROM notifications WHERE notification_id = :id")
    suspend fun findById(id: Int): NotificationEntity?

    @Query("SELECT * FROM notifications")
    suspend fun getAll(): List<NotificationEntity>

    @Query("DELETE FROM notifications WHERE notification_id = :id")
    suspend fun deleteById(id: Int)

    @Query("UPDATE notifications SET is_read = 1 WHERE notification_id = :notificationId")
    suspend fun markAsRead(notificationId: Int)

    @Query("UPDATE notifications SET is_read = 1 WHERE user_id = :userId")
    suspend fun markAllAsReadForUser(userId: Int)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE user_id = :userId")
    suspend fun deleteAllNotificationsForUser(userId: Int)
    
    @Query("""
        SELECT * FROM notifications 
        WHERE title LIKE :query 
           OR message LIKE :query
           OR user_id LIKE :query
           OR created_at LIKE :query
        ORDER BY created_at DESC
    """)
    suspend fun globalSearch(query: String): List<NotificationEntity>
}
