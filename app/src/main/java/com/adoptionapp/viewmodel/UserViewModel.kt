package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.User
import com.adoptionapp.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val users: LiveData<List<User>> = repository.allUsers

    fun addUser(user: User) {
        viewModelScope.launch {
            repository.insert(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            repository.update(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            repository.delete(user)
        }
    }

    fun syncUsers() {
        viewModelScope.launch {
            repository.syncUsers()
        }
    }

    fun getUserById(id: Int): LiveData<User?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return users
    }

    fun getUserByEmail(email: String): LiveData<User?> {
        // This would need to be implemented as LiveData in the repository
        // For now, we'll use a simple approach
        return users
    }
} 