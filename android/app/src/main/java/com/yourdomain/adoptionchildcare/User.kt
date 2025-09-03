package com.yourdomain.adoptionchildcare

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int? = null,
    
    @SerializedName("username")
    val username: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("full_name")
    val fullName: String? = null,
    
    @SerializedName("phone")
    val phone: String? = null,
    
    @SerializedName("role")
    val role: String? = "user",
    
    @SerializedName("created_at")
    val createdAt: String? = null,
    
    @SerializedName("updated_at")
    val updatedAt: String? = null
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
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("password")
    val password: String,
    
    @SerializedName("full_name")
    val fullName: String? = null,
    
    @SerializedName("phone")
    val phone: String? = null
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: T? = null
)
