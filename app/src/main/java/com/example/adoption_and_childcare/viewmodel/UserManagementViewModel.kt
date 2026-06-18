package com.example.adoption_and_childcare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adoption_and_childcare.data.repository.UserRepository
import com.example.adoption_and_childcare.data.db.entities.UserEntity
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
    private val userRepository: UserRepository,
    private val userDao: com.example.adoption_and_childcare.data.db.dao.UserDao
) : ViewModel() {

    val users = userDao.observeAll()

    suspend fun findByEmail(email: String) = userRepository.findByEmail(email)

    suspend fun findByUsername(username: String) = userRepository.findByUsername(username)

    suspend fun insert(user: UserEntity) = userRepository.insert(user)

    suspend fun update(user: UserEntity) = userRepository.update(user)

    suspend fun deleteById(userId: Int) = userRepository.deleteById(userId)

    /**
     * Attempts to log in the user.
     * 
     * @param username The username or email entered by the user.
     * @param password The plain-text password entered by the user.
     */
    suspend fun login(username: String, password: String) = userRepository.login(username, password)

    /**
     * Attempts to register a new user.
     * 
     * @param user The user entity to create.
     * @param password The plain-text password.
     */
    suspend fun register(user: UserEntity, password: String) = userRepository.register(user, password)

    /**
     * Fetches all users from the remote API.
     */
    suspend fun fetchUsersFromApi() = userRepository.fetchUsersFromApi()
}
