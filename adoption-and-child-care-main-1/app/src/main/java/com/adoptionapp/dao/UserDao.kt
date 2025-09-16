package com.adoptionapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.adoptionapp.entity.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun validateLogin(email: String, password: String): User?
    
        @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
        suspend fun validateLoginByUsername(username: String, password: String): User?
}
