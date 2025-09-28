package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.repository.UserRepository
import com.example.adoption_and_childcare.data.db.entities.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    suspend fun findByEmail(email: String) = userRepository.findByEmail(email)

    suspend fun findByUsername(username: String) = userRepository.findByUsername(username)

    suspend fun insert(user: UserEntity) = userRepository.insert(user)
}
