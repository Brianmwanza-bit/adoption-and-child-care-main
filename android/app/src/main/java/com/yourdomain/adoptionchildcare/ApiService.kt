package com.yourdomain.adoptionchildcare

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<ApiResponse<User>>
    
    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<ApiResponse<User>>
    
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): Response<ApiResponse<User>>
    
    @GET("users")
    suspend fun getAllUsers(): Response<ApiResponse<List<User>>>
    
    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") userId: Int, @Body user: User): Response<ApiResponse<User>>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<ApiResponse<String>>
}
