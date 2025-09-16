package com.adoptionapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.adoptionapp.database.UserDatabase
import com.adoptionapp.entity.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import android.content.SharedPreferences
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AuthViewModel(app: Application) : AndroidViewModel(app) {
    fun loginWithUsernameOrEmail(input: String, password: String) {
        viewModelScope.launch {
            val user = if (android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                userDao.validateLogin(input, password)
            } else {
                userDao.validateLoginByUsername(input, password)
            }
            if (user != null) {
                _currentUser.value = user
                _isLoggedIn.value = true
                prefs.edit().putBoolean("isLoggedIn", true).apply()
            } else {
                _error.value = "Invalid credentials."
            }
        }
    }
    private val db = Room.databaseBuilder(app, UserDatabase::class.java, "user_db").build()
    private val userDao = db.userDao()
    private val prefs: SharedPreferences = app.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isRegistered = MutableStateFlow<Boolean>(prefs.getBoolean("isRegistered", false))
    val isRegistered: StateFlow<Boolean> = _isRegistered

    private val _isLoggedIn = MutableStateFlow<Boolean>(prefs.getBoolean("isLoggedIn", false))
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _profilePhoto = MutableStateFlow<String?>(prefs.getString("profilePhoto", null))
    val profilePhoto: StateFlow<String?> = _profilePhoto

    fun register(username: String, email: String, password: String, role: String, profilePhoto: String?) {
        viewModelScope.launch {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                _error.value = "Email already registered."
                return@launch
            }
            val now = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            val user = User(
                username = username,
                email = email,
                password = password,
                role = role,
                created_at = now,
                updated_at = now,
                profile_photo = profilePhoto
            )
            userDao.insertUser(user)
            _isRegistered.value = true
            prefs.edit().putBoolean("isRegistered", true).apply()
            if (profilePhoto != null) prefs.edit().putString("profilePhoto", profilePhoto).apply()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = userDao.validateLogin(email, password)
            if (user != null) {
                _currentUser.value = user
                _isLoggedIn.value = true
                prefs.edit().putBoolean("isLoggedIn", true).apply()
            } else {
                _error.value = "Invalid credentials."
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
        prefs.edit().putBoolean("isLoggedIn", false).apply()
    }

    fun clearError() {
        _error.value = null
    }

    fun setProfilePhoto(uri: String) {
        _profilePhoto.value = uri
        prefs.edit().putString("profilePhoto", uri).apply()
    }
}
