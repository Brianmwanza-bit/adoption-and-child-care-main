package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.network.AuthResponse
import com.example.adoption_and_childcare.network.LoginRequest
import com.example.adoption_and_childcare.network.RegisterRequest

interface UserRepository {
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findByUsername(username: String): UserEntity?
    suspend fun insert(user: UserEntity)
    
    // Remote Auth
    suspend fun loginRemote(request: LoginRequest): AuthResponse?
    suspend fun registerRemote(request: RegisterRequest): AuthResponse?
}

