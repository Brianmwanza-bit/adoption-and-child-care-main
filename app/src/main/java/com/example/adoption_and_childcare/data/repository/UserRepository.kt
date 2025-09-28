package com.example.adoption_and_childcare.data.repository

import com.example.adoption_and_childcare.data.db.entities.UserEntity

interface UserRepository {
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findByUsername(username: String): UserEntity?
    suspend fun insert(user: UserEntity)
}

