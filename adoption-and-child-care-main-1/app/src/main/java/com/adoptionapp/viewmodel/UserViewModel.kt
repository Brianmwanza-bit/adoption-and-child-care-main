package com.adoptionapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adoptionapp.data.entity.User
import com.adoptionapp.repository.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    val users: LiveData<List<User>> = repository.allUsers
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addUser(user: User) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.insert(user)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.update(user)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.delete(user)
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun syncUsers() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repository.syncUsers()
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
            _loading.value = false
        }
    }

    fun getUserById(id: Int): LiveData<User?> {
        return users
    }

    fun getUserByEmail(email: String): LiveData<User?> {
        return users
    }
} 