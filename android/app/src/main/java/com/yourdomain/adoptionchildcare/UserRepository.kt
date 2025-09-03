package com.yourdomain.adoptionchildcare

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository {
    
    private val apiService = NetworkClient.apiService
    
    suspend fun login(username: String, password: String): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val loginRequest = LoginRequest(username, password)
                val response = apiService.login(loginRequest)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data
                    if (user != null) {
                        Result.success(user)
                    } else {
                        Result.failure(Exception("User data not found"))
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Login failed"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun register(username: String, email: String, password: String, fullName: String? = null): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(username, email, password, fullName)
                val response = apiService.register(registerRequest)
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data
                    if (user != null) {
                        Result.success(user)
                    } else {
                        Result.failure(Exception("User data not found"))
                    }
                } else {
                    val errorMessage = response.body()?.message ?: "Registration failed"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun getAllUsers(): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllUsers()
                
                if (response.isSuccessful && response.body()?.success == true) {
                    val users = response.body()?.data ?: emptyList()
                    Result.success(users)
                } else {
                    val errorMessage = response.body()?.message ?: "Failed to fetch users"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
