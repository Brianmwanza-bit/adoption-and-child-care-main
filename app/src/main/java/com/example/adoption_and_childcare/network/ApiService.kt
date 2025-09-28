package com.example.adoption_and_childcare.network

import com.example.adoption_and_childcare.data.db.entities.NotificationEntity
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @GET("notifications")
    suspend fun getNotifications(@Header("Authorization") token: String): Response<List<NotificationEntity>>
    
    @GET("notifications/unread-count")
    suspend fun getUnreadNotificationCount(@Header("Authorization") token: String): Response<UnreadCountResponse>
    
    @POST("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Header("Authorization") token: String, @Path("id") notificationId: Int): Response<Unit>

    @POST("auth/fcm-token")
    suspend fun updateFcmToken(
        @Header("Authorization") authToken: String,
        @Body request: FcmTokenRequest
    ): Response<Unit>
}

data class UnreadCountResponse(val unread: Int)

data class FcmTokenRequest(
    val fcmToken: String,
    val deviceId: String,
    val platform: String = "android"
)
