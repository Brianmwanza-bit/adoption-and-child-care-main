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
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true && responseBody.data != null) {
                        Result.success(responseBody.data)
                    } else {
                        val errorMessage = responseBody?.message ?: responseBody?.error ?: "Login failed"
                        Result.failure(Exception(errorMessage))
                    }
                } else {
                    val errorMessage = "HTTP ${response.code()}: ${response.message()}"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    suspend fun register(username: String, password: String, email: String? = null): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val registerRequest = RegisterRequest(username, password, email)
                val response = apiService.register(registerRequest)
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true && responseBody.data != null) {
                        Result.success(responseBody.data)
                    } else {
                        val errorMessage = responseBody?.message ?: responseBody?.error ?: "Registration failed"
                        Result.failure(Exception(errorMessage))
                    }
                } else {
                    val errorMessage = "HTTP ${response.code()}: ${response.message()}"
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
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true && responseBody.data != null) {
                        Result.success(responseBody.data)
                    } else {
                        val errorMessage = responseBody?.message ?: responseBody?.error ?: "Failed to fetch users"
                        Result.failure(Exception(errorMessage))
                    }
                } else {
                    val errorMessage = "HTTP ${response.code()}: ${response.message()}"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
