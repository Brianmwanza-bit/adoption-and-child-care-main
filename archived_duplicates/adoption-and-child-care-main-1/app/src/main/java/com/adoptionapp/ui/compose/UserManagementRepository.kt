package com.adoptionapp.ui.compose

class UserManagementRepository(private val api: ApiService, private val tokenProvider: () -> String) {
    suspend fun getUsers() = api.getUsers("Bearer ${tokenProvider()}")
    suspend fun createUser(user: UsersEntity) = api.createUser("Bearer ${tokenProvider()}", user)
    suspend fun updateUser(id: Int, user: UsersEntity) = api.updateUser("Bearer ${tokenProvider()}", id, user)
    suspend fun deleteUser(id: Int) = api.deleteUser("Bearer ${tokenProvider()}", id)
} 