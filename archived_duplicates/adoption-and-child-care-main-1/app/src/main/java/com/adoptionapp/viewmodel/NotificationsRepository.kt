package com.adoptionapp.viewmodel

import android.content.Context
import com.adoptionapp.ApiService
import com.adoptionapp.NotificationEntity
import com.adoptionapp.RetrofitClient
import com.adoptionapp.TokenManager

class NotificationsRepository(private val context: Context) {
    private val api = RetrofitClient.apiService
    private val token: String get() = "Bearer " + (TokenManager.getToken(context) ?: "")
    suspend fun getNotifications(): List<NotificationEntity> {
        val response = api.getNotifications(token)
        return response.body() ?: emptyList()
    }
    suspend fun getUnreadCount(): Int {
        val response = api.getUnreadNotificationCount(token)
        return response.body()?.unread ?: 0
    }
    suspend fun markAsRead(notificationId: Int) {
        api.markNotificationAsRead(token, notificationId)
    }
    suspend fun markAllAsRead() {
        // Fetch all notifications and mark each as read
        getNotifications().filter { !it.is_read }.forEach { markAsRead(it.notification_id) }
    }
} 