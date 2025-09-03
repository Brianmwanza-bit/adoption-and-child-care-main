package com.yourdomain.adoptionchildcare

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val userId: Int? = null,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("password_hash")
    val passwordHash: String? = null,
    
    @SerializedName("role")
    val role: String? = "user",
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("last_login")
    val lastLogin: String? = null,
    
    @SerializedName("is_active")
    val isActive: Boolean? = true
)

data class LoginRequest(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("username")
    val username: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("email")
    val email: String? = null,
    
    @SerializedName("role")
    val role: String? = "user"
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String? = null,
    
    @SerializedName("data")
    val data: T? = null,
    
    @SerializedName("error")
    val error: String? = null
)
