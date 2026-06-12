package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.repository.UserRepository
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import com.example.adoption_and_childcare.network.LoginRequest
import com.example.adoption_and_childcare.network.RegisterRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for user management operations.
 * 
 * This ViewModel provides methods for user CRUD operations and
 * remote authentication (login/register).
 * 
 * @property userRepository Repository for user data operations.
 */
@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun findByEmail(email: String) = userRepository.findByEmail(email)

    suspend fun findByUsername(username: String) = userRepository.findByUsername(username)

    suspend fun insert(user: UserEntity) = userRepository.insert(user)

    suspend fun loginRemote(request: LoginRequest) = userRepository.loginRemote(request)

    suspend fun registerRemote(request: RegisterRequest) = userRepository.registerRemote(request)
}
