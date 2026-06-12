package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.dao.NotificationDao
import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import com.example.adoption_and_childcare.network.ApiService
import com.example.adoption_and_childcare.utils.AuthManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for notification data with API integration.
 * Provides offline-first architecture with background sync to MySQL backend.
 */
@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao,
    private val apiService: ApiService,
    private val authManager: AuthManager
) {
    fun observeAll(): Flow<List<NotificationEntity>> = notificationDao.observeAll()
    
    suspend fun insert(notification: NotificationEntity, token: String): Result<Long> {
        return try {
            val localId = notificationDao.insert(notification)
            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun update(notification: NotificationEntity, token: String): Result<Unit> {
        return try {
            notificationDao.update(notification)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun delete(id: Int, token: String): Result<Unit> {
        return try {
            notificationDao.deleteById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findById(id: Int): NotificationEntity? {
        return notificationDao.findById(id)
    }
    
    suspend fun count(): Int = notificationDao.getAll().size
    
    suspend fun fetchFromApi(token: String): Result<List<NotificationEntity>> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getNotifications(authHeader)
            if (response.isSuccessful && response.body() != null) {
                val notifications = response.body()!!
                for (notification in notifications) {
                    val existing = notificationDao.findById(notification.notificationId)
                    if (existing != null) {
                        notificationDao.update(notification)
                    } else {
                        notificationDao.insert(notification)
                    }
                }
                Result.success(notifications)
            } else {
                Result.failure(Exception("Failed to fetch notifications: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUnreadCount(token: String): Result<Int> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader == null) {
                return Result.failure(Exception("Not authenticated. Please log in."))
            }
            
            val response = apiService.getUnreadNotificationCount(authHeader)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.unread)
            } else {
                Result.failure(Exception("Failed to fetch unread count: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markAsRead(notificationId: Int, token: String): Result<Unit> {
        return try {
            val authHeader = authManager.getAuthHeader()
            if (authHeader != null) {
                val response = apiService.markNotificationAsRead(authHeader, notificationId)
                if (response.isSuccessful) {
                    notificationDao.markAsRead(notificationId)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
