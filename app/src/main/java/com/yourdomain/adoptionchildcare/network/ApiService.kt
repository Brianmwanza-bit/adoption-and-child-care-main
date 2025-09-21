package com.yourdomain.adoptionchildcare.network

import com.yourdomain.adoptionchildcare.data.db.entities.NotificationEntity
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Response<List<NotificationEntity>>
    
    @GET("notifications/unread-count")
    suspend fun getUnreadNotificationCount(@Header("Authorization") token: String): Response<UnreadCountResponse>
    
    @POST("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Header("Authorization") token: String, @Path("id") notificationId: Int): Response<Unit>
}

data class UnreadCountResponse(val unread: Int)
