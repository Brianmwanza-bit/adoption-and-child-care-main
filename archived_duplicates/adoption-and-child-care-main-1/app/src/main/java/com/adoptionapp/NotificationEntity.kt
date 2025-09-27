package com.adoptionapp

data class NotificationEntity(
    val notification_id: Int,
    val user_id: Int,
    val message: String,
    val is_read: Boolean,
    val sent_at: String
) 