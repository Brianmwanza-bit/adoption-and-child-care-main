package com.adoptionapp.ui.compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel(private val repository: UserManagementRepository) : ViewModel() {
    private val _users = MutableStateFlow<List<UsersEntity>>(emptyList())
    val users: StateFlow<List<UsersEntity>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            _users.value = repository.getUsers()
        }
    }

    fun addUser(user: UsersEntity) {
        viewModelScope.launch {
            repository.createUser(user)
            loadUsers()
        }
    }

    fun updateUser(id: Int, user: UsersEntity) {
        viewModelScope.launch {
            repository.updateUser(id, user)
            loadUsers()
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteUser(id)
            loadUsers()
        }
    }
} 